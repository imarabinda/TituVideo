package com.ionexplus.titu.model.user;

import com.facebook.AccessToken;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class User {
    @Expose
    private Data data;
    @Expose
    private String message;
    @Expose
    private Boolean status;

    public Data getData() {
        return this.data;
    }

    public void setData(Data data2) {
        this.data = data2;
    }

    public String getMessage() {
        return this.message;
    }

    public void setMessage(String str) {
        this.message = str;
    }

    public Boolean getStatus() {
        return this.status;
    }

    public void setStatus(Boolean bool) {
        this.status = bool;
    }

    public static class Data {
        @Expose
        private String bio;
        @SerializedName("created_date")
        private String createdDate;
        @SerializedName("device_token")
        private String deviceToken;
        @SerializedName("fb_url")
        private String fbUrl;
        @SerializedName("followers_count")
        private int followersCount;
        @SerializedName("following_count")
        private int followingCount;
        @SerializedName("full_name")
        private String fullName;
        @Expose
        private String identity;
        @SerializedName("insta_url")
        private String instaUrl;
        @SerializedName("is_following  ")
        private int isFollowing;
        @SerializedName("is_verify")
        private String isVerify;
        @SerializedName("login_type")
        private String loginType;
        @SerializedName("my_post_likes")
        private int myPostLikes;
        @SerializedName("my_wallet")
        private String myWallet;
        @Expose
        private String platform;
        @SerializedName("profile_category")
        private String profileCategory;
        @SerializedName("profile_category_name")
        private String profileCategoryName;
        @Expose
        private String status;
        @Expose
        private String token;
        @SerializedName("user_email")
        private String userEmail;
        @SerializedName(AccessToken.USER_ID_KEY)
        private String userId;
        @SerializedName("user_mobile_no")
        private String userMobileNo;
        @SerializedName("user_name")
        private String userName;
        @SerializedName("user_profile")
        private String userProfile;
        @SerializedName("youtube_url")
        private String youtubeUrl;

        public long getIsFollowing() {
            return (long) this.isFollowing;
        }

        public void setIsFollowing(int i) {
            this.isFollowing = i;
        }

        public int getMyPostLikes() {
            return this.myPostLikes;
        }

        public void setMyPostLikes(int i) {
            this.myPostLikes = i;
        }

        public int getFollowersCount() {
            return this.followersCount;
        }

        public void setFollowersCount(int i) {
            this.followersCount = i;
        }

        public int getFollowingCount() {
            return this.followingCount;
        }

        public void setFollowingCount(int i) {
            this.followingCount = i;
        }

        public String getBio() {
            return this.bio;
        }

        public void setBio(String str) {
            this.bio = str;
        }

        public String getCreatedDate() {
            return this.createdDate;
        }

        public void setCreatedDate(String str) {
            this.createdDate = str;
        }

        public String getDeviceToken() {
            return this.deviceToken;
        }

        public void setDeviceToken(String str) {
            this.deviceToken = str;
        }

        public String getFbUrl() {
            return this.fbUrl;
        }

        public void setFbUrl(String str) {
            this.fbUrl = str;
        }

        public String getFullName() {
            return this.fullName;
        }

        public void setFullName(String str) {
            this.fullName = str;
        }

        public String getIdentity() {
            return this.identity;
        }

        public void setIdentity(String str) {
            this.identity = str;
        }

        public String getInstaUrl() {
            return this.instaUrl;
        }

        public void setInstaUrl(String str) {
            this.instaUrl = str;
        }

        public String getIsVerify() {
            return this.isVerify;
        }

        public void setIsVerify(String str) {
            this.isVerify = str;
        }

        public boolean isVerified() {
            return this.isVerify.equals("1");
        }

        public String getLoginType() {
            return this.loginType;
        }

        public void setLoginType(String str) {
            this.loginType = str;
        }

        public String getMyWallet() {
            return this.myWallet;
        }

        public void setMyWallet(String str) {
            this.myWallet = str;
        }

        public String getPlatform() {
            return this.platform;
        }

        public void setPlatform(String str) {
            this.platform = str;
        }

        public String getStatus() {
            return this.status;
        }

        public void setStatus(String str) {
            this.status = str;
        }

        public String getToken() {
            return this.token;
        }

        public void setToken(String str) {
            this.token = str;
        }

        public String getUserEmail() {
            return this.userEmail;
        }

        public void setUserEmail(String str) {
            this.userEmail = str;
        }

        public String getUserId() {
            return this.userId;
        }

        public void setUserId(String str) {
            this.userId = str;
        }

        public String getUserMobileNo() {
            return this.userMobileNo;
        }

        public void setUserMobileNo(String str) {
            this.userMobileNo = str;
        }

        public String getUserName() {
            return this.userName;
        }

        public void setUserName(String str) {
            this.userName = str;
        }

        public String getUserProfile() {
            return this.userProfile;
        }

        public void setUserProfile(String str) {
            this.userProfile = str;
        }

        public String getYoutubeUrl() {
            return this.youtubeUrl;
        }

        public void setYoutubeUrl(String str) {
            this.youtubeUrl = str;
        }

        public String getProfileCategory() {
            return this.profileCategory;
        }

        public void setProfileCategory(String str) {
            this.profileCategory = str;
        }

        public String getProfileCategoryName() {
            return this.profileCategoryName;
        }

        public void setProfileCategoryName(String str) {
            this.profileCategoryName = str;
        }
    }
}