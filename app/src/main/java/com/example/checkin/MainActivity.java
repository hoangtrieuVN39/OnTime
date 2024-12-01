package com.example.checkin;

import android.os.Bundle;
import android.view.MenuItem;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.example.checkin.checkinhistory.CheckinHistoryFragment;
import com.example.checkin.checkinmain.CheckinMainFragment;
import com.example.checkin.databinding.TestBinding;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.io.IOException;

public class MainActivity extends AppCompatActivity {

    private Fragment checkinMainFragment;
    private Fragment checkinHistoryFragment;
    private Fragment currentFragment;

    private TestBinding binding;
    private BottomNavigationView bottomNavigation;
    private BaseViewModel viewModel;
    String employeeID;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = TestBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        viewModel = new ViewModelProvider(this).get(BaseViewModel.class);

        if (getIntent().hasExtra("EmployeeID")){
            employeeID = getIntent().getStringExtra("EmployeeID");
        }
        else {
            employeeID = "NV001";
        }

        try {
            viewModel.loadData(employeeID, this);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        // Initialize fragments only once
        if (checkinMainFragment == null) {
            checkinMainFragment = new CheckinMainFragment(viewModel);
        }
        if (checkinHistoryFragment == null) {
            checkinHistoryFragment = new CheckinHistoryFragment(viewModel);
        }


        // Initial fragment setup
        currentFragment = checkinMainFragment;
        getSupportFragmentManager().beginTransaction()
                .add(binding.fragmentContainerView.getId(), currentFragment)
                .add(binding.fragmentContainerView.getId(), checkinHistoryFragment)
                .hide(checkinHistoryFragment) // Add only once
                .commit();
        bottomNavigation = binding.subnavBar;
        bottomNavigation.setOnItemSelectedListener(this::onItemSelectedListener);
    }

    private boolean onItemSelectedListener(MenuItem item) {
        if (item.getItemId() == R.id.checkinMain) {
            getSupportFragmentManager().beginTransaction()
                    .hide(currentFragment)
                    .show(checkinMainFragment)
                    .commit();
            currentFragment = checkinMainFragment;
        } else if (item.getItemId() == R.id.checkinHistory) {
            getSupportFragmentManager().beginTransaction()
                    .hide(checkinMainFragment)
                    .show(currentFragment) // Assuming currentFragment is checkinHistoryFragment
                    .commit();
            currentFragment = checkinHistoryFragment; // Or the other fragment
        }
        return true;
    }
}