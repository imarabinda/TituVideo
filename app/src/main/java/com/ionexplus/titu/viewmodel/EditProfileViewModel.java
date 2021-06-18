package com.ionexplus.titu.viewmodel;

import android.text.TextUtils;

import androidx.databinding.ObservableBoolean;
import androidx.databinding.ObservableInt;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.google.android.exoplayer2.util.Log;
import com.ionexplus.titu.adapter.ProfileCategoryAdapter;
import com.ionexplus.titu.model.user.ProfileCategory;
import com.ionexplus.titu.model.user.User;
import com.ionexplus.titu.utils.Global;

import java.io.File;
import java.util.HashMap;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Action;
import io.reactivex.functions.BiConsumer;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;

public class EditProfileViewModel extends ViewModel {
    public ProfileCategoryAdapter adapter = new ProfileCategoryAdapter();

    public User user = null;
    public String imageUri = "";
    public MutableLiveData<Boolean> isloading = new MutableLiveData<>(false);
    public ObservableBoolean isUsernameLoading = new ObservableBoolean(false);
    public ObservableBoolean isUsernameAvailable = new ObservableBoolean(true);
    public ObservableInt length = new ObservableInt();
    public MutableLiveData<Boolean> updateProfile = new MutableLiveData<>();
    public String curUserName = "";
    private String newUserName = "";
    private CompositeDisposable disposable = new CompositeDisposable();
    private String fullName = "", bio = "", fbUrl = "", instaUrl = "", youtubeUrl = "";
    public MutableLiveData<String> toast = new MutableLiveData<>();
    public String profile_cat_id = "";
    public String profile_cat_name = "";


    public void afterUserNameTextChanged(CharSequence s) {
        newUserName = s.toString();

        if (!curUserName.equals(newUserName)) {
            checkForUserName();
        } else {
            if (!newUserName.isEmpty()) {
                isUsernameAvailable.set(false);
                isUsernameLoading.set(false);
            }
            isUsernameAvailable.set(true);
            isUsernameLoading.set(false);
        }
    }


    public void fetchProfileCategories() {
        disposable.add(Global.initRetrofit().getProfileCategoryList(Global.ACCESS_TOKEN).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread())
                .unsubscribeOn(Schedulers.io()).doOnSubscribe(h->{
            isUsernameLoading.set(true);
        }).doOnTerminate(()->{
            isUsernameLoading.set(false);
        }).subscribe((profileCategory,throwable)->{
            if (profileCategory != null && profileCategory.getData() != null) {
                adapter.updateData(profileCategory.getData());
            }
            if(throwable!=null){
                Log.d("Profile category throwable ",throwable.toString());
            }
        }));
    }

    public void afterTextChanged(CharSequence charSequence, int type) {
        switch (type) {
            case 1:
                fullName = charSequence.toString();
                break;
            case 2:
                bio = charSequence.toString();
                length.set(bio.length());
                break;
            case 3:
                fbUrl = charSequence.toString();
                break;
            case 4:
                instaUrl = charSequence.toString();
                break;
            case 5:
                youtubeUrl = charSequence.toString();
                break;
            default:
                break;
        }
    }

    private void checkForUserName() {
        disposable.add(Global.initRetrofit().checkUsername(newUserName)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .unsubscribeOn(Schedulers.io())
                .doOnSubscribe(disposable1 -> isUsernameLoading.set(true))
                .doOnTerminate(() -> isUsernameLoading.set(false))
                .subscribe((checkUsername, throwable) -> {
                    if (checkUsername != null && checkUsername.getStatus() != null) {
                        isUsernameAvailable.set(checkUsername.getStatus());
                    }
                }));
    }

    public void updateUser() {
        String str = this.fullName;
        if (str == null || str.isEmpty() || this.fullName.length() < 4) {
            this.toast.setValue("Invalid FullName");
            return;
        }
        String str2 = this.newUserName;
        if (str2 == null || str2.isEmpty() || this.newUserName.length() < 4 || !this.isUsernameAvailable.get()) {
            this.toast.setValue("Invalid UserName");
            return;
        }

        HashMap<String, RequestBody> hashMap = new HashMap<>();
        hashMap.put("full_name", toRequestBody(fullName));
        hashMap.put("user_name", toRequestBody(newUserName));
        hashMap.put("bio", toRequestBody(bio));
        hashMap.put("fb_url", toRequestBody(fbUrl));
        hashMap.put("insta_url", toRequestBody(instaUrl));
        hashMap.put("youtube_url", toRequestBody(youtubeUrl));
        if (!TextUtils.isEmpty(profile_cat_id)) {
            hashMap.put("profile_category", toRequestBody(profile_cat_id));
            hashMap.put("profile_category_name", toRequestBody(profile_cat_name));
        }

        MultipartBody.Part body = null;
        if (imageUri != null && !imageUri.isEmpty()) {
            File file = new File(imageUri);
            RequestBody requestFile =
                    RequestBody.create(MediaType.parse("multipart/form-data"), file);
            body = MultipartBody.Part.createFormData("user_profile", file.getName(), requestFile);
        }
        disposable.add(Global.initRetrofit().updateUser(Global.ACCESS_TOKEN, hashMap, body)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .doOnSubscribe(new Consumer<Disposable>() {
                    @Override
                    public void accept(Disposable disposable) throws Exception {
                        isloading.setValue(true);
                    }
                }).doOnTerminate(new Action() {
                    @Override
                    public void run() throws Exception {
                        isloading.setValue(false);
                    }
                })
                .unsubscribeOn(Schedulers.io())
                .subscribe(new BiConsumer<User, Throwable>() {
                    @Override
                    public void accept(User user2, Throwable throwable) throws Exception {
                        if (user2 != null && user2.getStatus()) {
                            user = user2;
                            toast.setValue("Profile Update SuccessFully");
                            updateProfile.setValue(true);
                        }
                    }
                })
        );
    }

    public RequestBody toRequestBody(String value) {
        return RequestBody.create(MediaType.parse("text/plain"), value);
    }


    @Override
    protected void onCleared() {
        super.onCleared();
        disposable.clear();
    }

    public void updateData() {
        fullName = user.getData().getFullName();
        newUserName = user.getData().getUserName();
        bio = user.getData().getBio();
        fbUrl = user.getData().getFbUrl();
        instaUrl = user.getData().getInstaUrl();
        youtubeUrl = user.getData().getYoutubeUrl();
        profile_cat_id = user.getData().getProfileCategory();
        profile_cat_name = user.getData().getProfileCategoryName();
    }
}
