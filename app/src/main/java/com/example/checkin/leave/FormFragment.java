package com.example.checkin.leave;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
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

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        viewModel = new ViewModelProvider(requireActivity()).get(FormViewModel.class);
        viewModel.loadDataFromParent(parentViewModel);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = MainleaveLayoutBinding.inflate(inflater, container, false);

        navHostFragment = (NavHostFragment) getChildFragmentManager().findFragmentById(R.id.view);
        navController = navHostFragment.getNavController();

        AppBarConfiguration appBarConfiguration = new AppBarConfiguration.Builder(
                R.id.formPersonalFragment, R.id.formApproveFragment, R.id.formListFragment
        ).build();

        toolbar = binding.toolbar;
        NavigationUI.setupWithNavController(toolbar, navController, appBarConfiguration);

        tabLayout = binding.tabLayout;
        setupTabLayout();

        return binding.getRoot();
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
                        viewModel.setCurrentFragmentID(R.id.formPersonalFragment);
                        navController.navigate(R.id.formPersonalFragment);
                        break;
                    case 1:
                        viewModel.setCurrentFragmentID(R.id.formApproveFragment);
                        navController.navigate(R.id.formApproveFragment);
                        break;
                    case 2:
                        viewModel.setCurrentFragmentID(R.id.formListFragment);
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

    @Override
    public void onResume() {
        super.onResume();
        int tabs = 0;
        if (viewModel.getCurrentFragmentID() == R.id.formPersonalFragment){
            tabs = 0;
            navController.navigate(R.id.formPersonalFragment);
        } else if (viewModel.getCurrentFragmentID() == R.id.formApproveFragment){
            tabs = 1;
            navController.navigate(R.id.formApproveFragment);
        } else if (viewModel.getCurrentFragmentID() == R.id.formListFragment){
            tabs = 2;
            navController.navigate(R.id.formListFragment);
        }
        TabLayout.Tab tab = tabLayout.getTabAt(tabs);
        tabLayout.selectTab(tab);
    }
}
