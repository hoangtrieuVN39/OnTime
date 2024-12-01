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
import androidx.fragment.app.FragmentContainerView;
import androidx.lifecycle.ViewModel;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.fragment.NavHostFragment;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import com.example.checkin.R;
import com.example.checkin.databinding.MainleaveLayoutBinding;
import com.google.android.material.appbar.CollapsingToolbarLayout;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayout.OnTabSelectedListener;
import com.google.android.material.tabs.TabLayout.TabLayoutOnPageChangeListener;
import com.google.android.material.tabs.TabLayoutMediator;

public class FormFragment extends Fragment {

    private MainleaveLayoutBinding binding;
    Toolbar toolbar;
    NavController navController;
    NavHostFragment navHostFragment;
    TabLayout tabLayout;

    ViewModel viewModel;

    public FormFragment(ViewModel _viewModel) {
        viewModel = _viewModel;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = MainleaveLayoutBinding.inflate(inflater, container, false);
//        View view = binding.getRoot();
//
//        navController = Navigation.findNavController(view.findViewById(R.id.view));
//        AppBarConfiguration appBarConfiguration = new AppBarConfiguration.Builder(
//                R.id.formPersonalFragment, R.id.formApproveFragment, R.id.formListFragment
//        ).build();
//
//        toolbar = binding.toolbar;
//        NavigationUI.setupWithNavController(toolbar, navController, appBarConfiguration);
//
//        tabLayout = binding.tabLayout;
//        setupTabLayout();

        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);


        CollapsingToolbarLayout layout = binding.collapsingToolbar;
        navHostFragment = (NavHostFragment) getChildFragmentManager().findFragmentById(R.id.view);
        navController = navHostFragment.getNavController(); // Find NavController here
        AppBarConfiguration appBarConfiguration = new AppBarConfiguration.Builder(
                R.id.formPersonalFragment, R.id.formApproveFragment, R.id.formListFragment
        ).build();

        toolbar = binding.toolbar;
        NavigationUI.setupWithNavController(layout, toolbar, navController, appBarConfiguration);

        tabLayout = binding.tabLayout;
        setupTabLayout();
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
