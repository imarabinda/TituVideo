package com.ionexplus.titu.viewmodel;

import androidx.lifecycle.MutableLiveData;
import com.ionexplus.titu.api.CmsService;
import com.ionexplus.titu.api.ContentsApi;
import com.ionexplus.titu.api.ContentsResponse;
import com.ionexplus.titu.utils.AppConfig;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/* access modifiers changed from: package-private */
public class ContentsRepository {
    private ContentsApi contentsApi;

    private ContentsRepository() {
        this.contentsApi = CmsService.createContentsService(AppConfig.API_URL);
    }

    static ContentsRepository getInstance() {
        return LazyHolder.INSTANCE;
    }

    public MutableLiveData<ContentsResponse> getContents() {
        final MutableLiveData<ContentsResponse> mutableLiveData = new MutableLiveData<>();
        this.contentsApi.getContents(AppConfig.API_KEY).enqueue(new Callback<ContentsResponse>() {

            @Override
            public void onResponse(Call<ContentsResponse> call, Response<ContentsResponse> response) {
                if (response.isSuccessful()) {
                    mutableLiveData.setValue(response.body());
                }
            }

            @Override
            public void onFailure(Call<ContentsResponse> call, Throwable th) {
                mutableLiveData.setValue(null);
            }
        });
        return mutableLiveData;
    }

    private static class LazyHolder {
        private static final ContentsRepository INSTANCE = new ContentsRepository();

        private LazyHolder() {
        }
    }
}