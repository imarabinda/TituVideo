package com.ionexplus.titu.model.user;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import java.util.List;

public class ProfileCategory {
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
        @SerializedName("profile_category_id")
        private String profileCategoryId;
        @SerializedName("profile_category_image")
        private String profileCategoryImage;
        @SerializedName("profile_category_name")
        private String profileCategoryName;

        public String getProfileCategoryId() {
            return profileCategoryId;
        }

        public void setProfileCategoryId(String str) {
            profileCategoryId = str;
        }

        public String getProfileCategoryImage() {
            return profileCategoryImage;
        }

        public void setProfileCategoryImage(String str) {
            profileCategoryImage = str;
        }

        public String getProfileCategoryName() {
            return profileCategoryName;
        }

        public void setProfileCategoryName(String str) {
            profileCategoryName = str;
        }
    }
}
