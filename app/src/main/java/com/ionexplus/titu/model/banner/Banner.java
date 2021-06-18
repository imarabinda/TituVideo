package com.ionexplus.titu.model.banner;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import java.util.List;

public class Banner {
    @Expose
    private List<Data> data;
    @Expose
    private String message;
    @Expose
    private Boolean status;

    public List<Data> getData() {
        return data;
    }

    public void setData(List<Data> list) {
        data = list;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String str) {
        message = str;
    }

    public Boolean getStatus() {
        return status;
    }

    public void setStatus(Boolean bool) {
        status = bool;
    }

    public static class Data {
        @SerializedName("banner_id")
        private String bannerId;
        @SerializedName("banner_image")
        private String bannaerImage;
        @SerializedName("banner_action_value")
        private String bannerActionValue;
        @SerializedName("banner_action")
        private String bannerAction;

        public String getBannerId() {
            return bannerId;
        }

        public void setBannerId(String str) {
            bannerId = str;
        }

        public String getBannerImage() {
            return bannaerImage;
        }

        public void setBannaerImage(String str) {
            bannaerImage = str;
        }

        public String getBannerActionValue() {
            return bannerActionValue;
        }

        public void setBannerActionValue(String str) {
            bannerActionValue = str;
        }

        public String getBannerAction() {
            return bannerAction;
        }

        public void setBannerAction(String str) {
            bannerAction = str;
        }

    }
}
