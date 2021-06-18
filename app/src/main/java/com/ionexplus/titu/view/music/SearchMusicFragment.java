package com.ionexplus.titu.view.music;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProviders;

import com.ionexplus.titu.R;
import com.ionexplus.titu.databinding.FragmentSearchMusicBinding;
import com.ionexplus.titu.viewmodel.MusicMainViewModel;

import org.jetbrains.annotations.NotNull;


public class SearchMusicFragment extends Fragment {

    public MusicMainViewModel viewModel;
    private FragmentSearchMusicBinding binding;

    @Override
    public View onCreateView(@NotNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_search_music, container, false);
        return binding.getRoot();
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        if (getParentFragment() != null) {
            viewModel = ViewModelProviders.of(getParentFragment()).get(MusicMainViewModel.class);
        }
        binding.setViewModel(viewModel);
    }

}