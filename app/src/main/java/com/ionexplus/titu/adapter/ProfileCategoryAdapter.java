package com.ionexplus.titu.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.exoplayer2.util.Log;
import com.ionexplus.titu.R;
import com.ionexplus.titu.databinding.ItemProfileCategoryListBinding;
import com.ionexplus.titu.model.comment.Comment;
import com.ionexplus.titu.model.user.ProfileCategory;

import java.util.ArrayList;
import java.util.List;

public class ProfileCategoryAdapter extends RecyclerView.Adapter<ProfileCategoryAdapter.ProfileCategoryViewHolder> {

    private ArrayList<ProfileCategory.Data> mList = new ArrayList<>();
    private OnRecyclerViewItemClick onRecyclerViewItemClick;

    public interface OnRecyclerViewItemClick {
        void onCategoryClick(ProfileCategory.Data data);
    }

    public OnRecyclerViewItemClick getOnRecyclerViewItemClick() {
        return this.onRecyclerViewItemClick;
    }

    public void setOnRecyclerViewItemClick(OnRecyclerViewItemClick onRecyclerViewItemClick2) {
        this.onRecyclerViewItemClick = onRecyclerViewItemClick2;
    }

    @NonNull
    @Override
    public ProfileCategoryViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int i) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_profile_category_list, parent, false);
        return new ProfileCategoryAdapter.ProfileCategoryViewHolder(view);
    }

    public void onBindViewHolder(@NonNull ProfileCategoryViewHolder holder, int position) {
        holder.setModel(mList.get(position));
    }

    @Override
    public int getItemCount() {
        return mList.size();
    }

    public void updateData(List<ProfileCategory.Data> list) {
        mList = (ArrayList<ProfileCategory.Data>) list;
        notifyDataSetChanged();
    }

    public void loadMore(List<ProfileCategory.Data> list) {
        for (int i = 0; i < list.size(); i++) {
            mList.add(list.get(i));
            notifyItemInserted(mList.size() - 1);
        }
    }

    class ProfileCategoryViewHolder extends RecyclerView.ViewHolder {
        ItemProfileCategoryListBinding binding;

        ProfileCategoryViewHolder(View view) {
            super(view);
            binding = DataBindingUtil.bind(view);
        }

        public void setModel(ProfileCategory.Data data) {
            binding.setModel(data);
            binding.getRoot().setOnClickListener(view->{
                onRecyclerViewItemClick.onCategoryClick(data);
            });
        }

    }



}

