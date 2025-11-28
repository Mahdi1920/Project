package com.example.project.utils;

import android.content.Context;
import android.content.SharedPreferences;

public class UserPreferences {
    private static final String PREF_NAME = "DeliveryAppPrefs";
    private static final String KEY_USER_ID = "userId";
    private static final String KEY_USER_NAME = "userName";
    private static final String KEY_USER_TYPE = "userType";
    private static final String KEY_USER_PHONE = "userPhone";
    private static final String KEY_RESTAURANT_ID = "restaurantId";

    private SharedPreferences prefs;
    private SharedPreferences.Editor editor;

    public UserPreferences(Context context) {
        prefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        editor = prefs.edit();
    }

    // Save user data all-in-one
    public void saveUser(String userId, String userName, String userType, String userPhone) {
        editor.putString(KEY_USER_ID, userId);
        editor.putString(KEY_USER_NAME, userName);
        editor.putString(KEY_USER_TYPE, userType);
        editor.putString(KEY_USER_PHONE, userPhone);
        editor.apply();
    }

    // New setters requested
    public void setUserName(String name) {
        editor.putString(KEY_USER_NAME, name);
        editor.apply();
    }

    public void setUserPhone(String phone) {
        editor.putString(KEY_USER_PHONE, phone);
        editor.apply();
    }

    public void setUserType(String type) {
        editor.putString(KEY_USER_TYPE, type);
        editor.apply();
    }

    public void saveRestaurantId(String restaurantId) {
        editor.putString(KEY_RESTAURANT_ID, restaurantId);
        editor.apply();
    }

    public String getUserId() {
        return prefs.getString(KEY_USER_ID, null);
    }

    public String getUserName() {
        return prefs.getString(KEY_USER_NAME, null);
    }

    public String getUserType() {
        return prefs.getString(KEY_USER_TYPE, null);
    }

    public String getUserPhone() {
        return prefs.getString(KEY_USER_PHONE, null);
    }

    public String getRestaurantId() {
        return prefs.getString(KEY_RESTAURANT_ID, null);
    }

    public void clearUser() {
        editor.clear();
        editor.apply();
    }

    public boolean isUserLoggedIn() {
        return getUserId() != null;
    }
}
