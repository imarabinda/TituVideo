package com.ionexplus.titu.adapter;

import android.content.Intent;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.RecyclerView;

import com.ionexplus.titu.R;
import com.ionexplus.titu.databinding.ItemBannerListBinding;
import com.ionexplus.titu.databinding.ItemSearchHashListBinding;
import com.ionexplus.titu.model.Explore;
import com.ionexplus.titu.model.banner.Banner;
import com.ionexplus.titu.utils.Global;
import com.ionexplus.titu.view.search.FetchUserActivity;
import com.ionexplus.titu.view.search.HashTagActivity;

import java.util.ArrayList;
import java.util.List;

public class BannerAdapter extends RecyclerView.Adapter<BannerAdapter.BannerViewHolder> {
    private ArrayList<Banner.Data> mList = new ArrayList<>();

    @NonNull
    @Override
    public BannerViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_banner_list, parent, false);
        return new BannerViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull BannerViewHolder holder, int position) {
        holder.setModel(mList.get(position));

    }

    @Override
    public int getItemCount() {
        return mList.size();
    }

    public void updateData(List<Banner.Data> list) {
        mList = (ArrayList<Banner.Data>) list;
        notifyDataSetChanged();
    }

    public void loadMore(List<Banner.Data> data) {
        for (int i = 0; i < data.size(); i++) {
            mList.add(data.get(i));
            notifyItemInserted(mList.size() - 1);
        }

    }

    public List<Banner.Data> getData() {
        return mList;
    }

    static class BannerViewHolder extends RecyclerView.ViewHolder {
        ItemBannerListBinding binding;
        BannerAdapter adapter = new BannerAdapter();


        BannerViewHolder(@NonNull View itemView) {
            super(itemView);
            binding = DataBindingUtil.bind(itemView);
            if (binding != null) {
                binding.executePendingBindings();
            }
        }

        public void setModel(Banner.Data banner) {
            binding.setModel(banner);
            binding.banner.setOnClickListener(v -> {
                if(banner.getBannerAction().equals("hashtag")){
                    Intent intent = new Intent(binding.getRoot().getContext(), HashTagActivity.class);
                    intent.putExtra("hashtag", banner.getBannerActionValue());
                    binding.getRoot().getContext().startActivity(intent);
                }

                if(banner.getBannerAction().equals("url")){
                    binding.getRoot().getContext().startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(banner.getBannerActionValue())));
                }

                if(banner.getBannerAction().equals("profile")){
                    Intent intent = new Intent(binding.getRoot().getContext(), FetchUserActivity.class);
                    intent.putExtra("userid", banner.getBannerActionValue());
                    binding.getRoot().getContext().startActivity(intent);
                }

            });
        }
    }
}