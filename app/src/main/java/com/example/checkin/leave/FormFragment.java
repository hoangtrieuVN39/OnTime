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
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.fragment.NavHostFragment;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import com.example.checkin.BaseViewModel;
import com.example.checkin.R;
import com.example.checkin.databinding.MainleaveLayoutBinding;
import com.google.android.material.appbar.CollapsingToolbarLayout;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayout.OnTabSelectedListener;
import com.google.android.material.tabs.TabLayout.TabLayoutOnPageChangeListener;
import com.google.android.material.tabs.TabLayoutMediator;

import java.util.Objects;

public class FormFragment extends Fragment {

    private MainleaveLayoutBinding binding;
    Toolbar toolbar;
    NavController navController;
    NavHostFragment navHostFragment;
    TabLayout tabLayout;

    BaseViewModel parentViewModel;
    FormViewModel viewModel;

    public FormFragment(BaseViewModel _viewModel) {
        parentViewModel = _viewModel;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = MainleaveLayoutBinding.inflate(inflater, container, false);

        viewModel = new ViewModelProvider(requireActivity()).get(FormViewModel.class);
        viewModel.loadDataFromParent(parentViewModel);

        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        navHostFragment = (NavHostFragment) getChildFragmentManager().findFragmentById(R.id.view);
        navController = navHostFragment.getNavController();

        AppBarConfiguration appBarConfiguration = new AppBarConfiguration.Builder(
                R.id.formPersonalFragment, R.id.formApproveFragment, R.id.formListFragment
        ).build();

        toolbar = binding.toolbar;
        NavigationUI.setupWithNavController(toolbar, navController, appBarConfiguration);

        tabLayout = binding.tabLayout;
        setupTabLayout();
    }

    @Override
    public void onStart() {
        super.onStart();
        viewModel.getOnFilterBtnClicked().observe(requireActivity(), new Observer<View.OnClickListener>() {
            @Override
            public void onChanged(View.OnClickListener newValue) {
                System.out.println(newValue);
                if (viewModel.currentFragmentID != R.id.formPersonalFragment){
                    binding.buttonlistFilter.setVisibility(View.VISIBLE);
                    binding.buttonlistFilter.setOnClickListener(newValue);
                } else {
                    binding.buttonlistFilter.setVisibility(View.INVISIBLE);
                    binding.buttonlistFilter.setOnClickListener(null);
                }
            }
        });

        navController.navigate(R.id.formPersonalFragment);
    }

    private void setupTabLayout() {
        tabLayout.addOnTabSelectedListener(new OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                switch(tab.getPosition()) {
                    case 0:
                        navController.navigate(R.id.formPersonalFragment);
                        viewModel.setCurrentFragment(R.id.formPersonalFragment);
                        break;
                    case 1:
                        navController.navigate(R.id.formApproveFragment);
                        viewModel.setCurrentFragment(R.id.formApproveFragment);
                        break;
                    case 2:
                        navController.navigate(R.id.formListFragment);
                        viewModel.setCurrentFragment(R.id.formListFragment);
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
