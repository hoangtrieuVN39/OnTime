package com.example.checkin.testing;

import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.example.checkin.R;
import com.example.checkin.databinding.TestBinding;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.io.IOException;

public class MainActivity extends AppCompatActivity {
    private TestBinding binding;
    private BottomNavigationView bottomNavigation;
    private Fragment checkinMainFragment;
    private Fragment checkinHistoryFragment;
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

        checkinMainFragment = new CheckinMainFragment(viewModel);
        checkinHistoryFragment = new CheckinHistoryFragment(viewModel);

        getSupportFragmentManager().beginTransaction().replace(binding.fragmentContainerView.getId(), checkinHistoryFragment).commit();

        bottomNavigation = binding.subnavBar;
        bottomNavigation.setSelectedItemId(R.id.checkinMain);
        bottomNavigation.setOnItemSelectedListener( item -> {
            if (item.getItemId() == R.id.checkinMain){
                getSupportFragmentManager().beginTransaction().replace(binding.fragmentContainerView.getId(), checkinMainFragment).commit();
            }
            else if (item.getItemId() == R.id.checkinHistory){
                getSupportFragmentManager().beginTransaction().replace(binding.fragmentContainerView.getId(), checkinHistoryFragment).commit();
            }
            else if (item.getItemId() == R.id.leave){
//                getSupportFragmentManager().beginTransaction().
            }
            return true;
        });
    }
}