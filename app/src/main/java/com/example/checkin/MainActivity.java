package com.example.checkin;

import android.os.Bundle;
import android.view.MenuItem;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.example.checkin.checkinhistory.CheckinHistoryFragment;
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
                .replace(binding.fragmentContainerView.getId(), currentFragment)
                .commit();

        bottomNavigation = binding.subnavBar;
        bottomNavigation.setOnItemSelectedListener(this::onItemSelectedListener);
        bottomNavigation.setSelectedItemId(R.id.checkinHistory);

    }

    private boolean onItemSelectedListener(MenuItem item){
        Fragment fragmentToShow = null;

        if (item.getItemId() == R.id.checkinMain) {
            fragmentToShow = checkinMainFragment;
        } else if (item.getItemId() == R.id.checkinHistory) {
            fragmentToShow = checkinHistoryFragment;
        }

        // Only replace if it's a different fragment
        if (fragmentToShow != null && fragmentToShow != currentFragment) {
            getSupportFragmentManager().beginTransaction()
                    .setReorderingAllowed(true)  // Optimize transaction
                    .replace(binding.fragmentContainerView.getId(), fragmentToShow)
                    .commit();
            currentFragment = fragmentToShow;
        }

        return true;
    }
}