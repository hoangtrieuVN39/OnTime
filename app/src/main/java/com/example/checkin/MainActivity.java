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
import com.example.checkin.leave.FormFragment;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.io.IOException;

public class MainActivity extends AppCompatActivity {

    private Fragment checkinMainFragment;
    private Fragment checkinHistoryFragment;
    private Fragment FormFragment;

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
        if (FormFragment == null) {
            FormFragment = new FormFragment(viewModel);
        }

        // Initial fragment setup
        getSupportFragmentManager().beginTransaction()
                .add(binding.fragmentContainerView.getId(), checkinMainFragment)
                .add(binding.fragmentContainerView.getId(), checkinHistoryFragment)
                .add(binding.fragmentContainerView.getId(), FormFragment)
                .show(checkinMainFragment)
                .hide(checkinHistoryFragment)
                .hide(FormFragment) // Add only once
                .commit();
        bottomNavigation = binding.subnavBar;
        bottomNavigation.setOnItemSelectedListener(this::onItemSelectedListener);
        bottomNavigation.setSelectedItemId(R.id.checkinMain);
    }

    private boolean onItemSelectedListener(MenuItem item) {
        if (item.getItemId() == R.id.checkinMain) {
            getSupportFragmentManager().beginTransaction()
                    .hide(FormFragment)
                    .hide(checkinHistoryFragment)
                    .show(checkinMainFragment)
                    .commit();
        } else if (item.getItemId() == R.id.checkinHistory) {
            getSupportFragmentManager().beginTransaction()
                    .hide(FormFragment)
                    .hide(checkinMainFragment)
                    .show(checkinHistoryFragment) // Assuming currentFragment is checkinHistoryFragment
                    .commit();
        }
        else if (item.getItemId() == R.id.form) {
            getSupportFragmentManager().beginTransaction()
                    .hide(checkinMainFragment)
                    .hide(checkinHistoryFragment)
                    .show(FormFragment)
                    .commit();
        }
        return true;
    }
}