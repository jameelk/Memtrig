package com.memtrig.globals;

import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;

public class App extends Application {

    private static SharedPreferences preferences;
    private static Context context;
    public static String EMAIL = "email";
    public static String LOGIN = "signup_login";

    @Override
    public void onCreate() {
        super.onCreate();
        preferences = getSharedPreferences("prefs", MODE_PRIVATE);
    }

    public static void saveString(String key,String value) {
        preferences.edit().putString(key, value).apply();
    }

    public static String getString(String key) {
        return preferences.getString(key, "");
    }

    public static void saveLogin(boolean login) {
        preferences.edit().putBoolean(LOGIN, login).apply();
    }

    public static boolean isLogin() {
        return preferences.getBoolean(LOGIN, false);
    }


    public static void todayAlarmState(String date,boolean set) {
        preferences.edit().putBoolean(date, set).apply();
    }

    public static boolean isTodayAlarmSet(String date) {
        return preferences.getBoolean(date, false);
    }


    public static void taskAlarmSet(String key, boolean set) {
        preferences.edit().putBoolean(key, set).commit();
    }

    public static boolean isTaskAlarmSet(String key) {
        return preferences.getBoolean(key, false);
    }

    public static void clearSettings() {
        preferences.edit().clear().apply();
    }

}
