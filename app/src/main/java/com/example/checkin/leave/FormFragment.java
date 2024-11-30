package com.example.checkin.leave;

import android.app.Activity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import com.example.checkin.R;
import com.example.checkin.databinding.MainleaveLayoutBinding;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayout.OnTabSelectedListener;
import com.google.android.material.tabs.TabLayout.TabLayoutOnPageChangeListener;
import com.google.android.material.tabs.TabLayoutMediator;

public class FormFragment extends Fragment {

    private MainleaveLayoutBinding binding;
    Toolbar toolbar;
    NavController navController;
    TabLayout tabLayout;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.checkinmaintest_layout, container, false);

        navController = Navigation.findNavController(getActivity(), R.id.view);
        AppBarConfiguration appBarConfiguration = new AppBarConfiguration.Builder(
                R.id.formPersonalFragment, R.id.formApproveFragment, R.id.formListFragment
        ).build();


        toolbar = binding.toolbar;
        NavigationUI.setupWithNavController(toolbar, navController, appBarConfiguration);

        tabLayout = binding.tabLayout;
        setupTabLayout();

        return super.onCreateView(inflater, container, savedInstanceState);
    }

    private void setupTabLayout() {
        tabLayout.addOnTabSelectedListener(new OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                switch(tab.getPosition()) {
                    case 0:
                        navController.navigate(R.id.formPersonalFragment);
                        break;
                    case 1:
                        navController.navigate(R.id.formApproveFragment);
                        break;
                    case 2:
                        navController.navigate(R.id.formListFragment);
                        break;
                }
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {}

            @Override
            public void onTabReselected(TabLayout.Tab tab) {}
        });
    }
}
