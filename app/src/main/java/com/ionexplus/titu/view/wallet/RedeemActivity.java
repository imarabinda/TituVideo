package com.ionexplus.titu.view.wallet;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.ViewModelProviders;

import com.ionexplus.titu.R;
import com.ionexplus.titu.databinding.ActivityRedeemBinding;
import com.ionexplus.titu.utils.CustomDialogBuilder;
import com.ionexplus.titu.utils.Global;
import com.ionexplus.titu.view.base.BaseActivity;
import com.ionexplus.titu.view.home.MainActivity;
import com.ionexplus.titu.view.web.WebViewActivity;
import com.ionexplus.titu.viewmodel.RedeemViewModel;
import com.ionexplus.titu.viewmodelfactory.ViewModelFactory;

public class RedeemActivity extends BaseActivity {

    ActivityRedeemBinding binding;
    RedeemViewModel viewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_redeem);
        viewModel = ViewModelProviders.of(this, new ViewModelFactory(new RedeemViewModel()).createFor()).get(RedeemViewModel.class);

        initView();
        initListeners();
        initObserve();
        binding.setViewmodel(viewModel);
    }

    private void initView() {
        if (getIntent().getStringExtra("coins") != null) {
            viewModel.coindCount = getIntent().getStringExtra("coins");
            viewModel.coinRate = getIntent().getStringExtra("coinrate");
            binding.tvCount.setText(Global.prettyCount(Integer.parseInt(viewModel.coindCount)));
        }
    }


    private void initListeners() {
        binding.tvTerm.setOnClickListener(v -> startActivity(new Intent(this, WebViewActivity.class)));
        String[] paymentTypes = getResources().getStringArray(R.array.payment);
        binding.spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                ((TextView) parent.getChildAt(0)).setTextColor(getResources().getColor(R.color.color_text_light));
                viewModel.requestType = paymentTypes[position];
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                Log.i("", "");
            }
        });
        binding.btnRedeem.setOnClickListener(v -> {
            if (viewModel.accountId != null && !TextUtils.isEmpty(viewModel.accountId)) {

                viewModel.callApiToRedeem();
            } else {
                Toast.makeText(this, "Please enter your account number", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void initObserve() {
        viewModel.redeem.observe(this, redeem -> {
            if (redeem != null && redeem.getStatus()) {
                Toast.makeText(this, "Request Submitted Successfully", Toast.LENGTH_SHORT).show();
                startActivity(new Intent(this, MainActivity.class));
                finishAffinity();
            }
        });
        CustomDialogBuilder customDialogBuilder = new CustomDialogBuilder(this);
        viewModel.isLoading.observe(this,isloading->{
            if(isloading){
                customDialogBuilder.showLoadingDialog();
            }else{
                customDialogBuilder.hideLoadingDialog();
            }
        });
    }


}