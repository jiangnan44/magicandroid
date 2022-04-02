package com.v.pluginframework;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;

import androidx.annotation.NonNull;

/**
 * Author:v
 * Time:2022/3/30
 */
@SuppressLint("MissingSuperCall")
public class BaseActivity extends Activity implements ProxyActivityInterface {
    public Activity main;

    @Override
    public void attach(@NonNull Activity proxyActivity) {
        main = proxyActivity;
    }

    @Override
    public void onCreate(Bundle saveInstanceState) {

    }

    @Override
    public void onStart() {

    }

    @Override
    public void onResume() {

    }

    @Override
    public void onPause() {

    }

    @Override
    public void onStop() {

    }

    @Override
    public void onDestroy() {

    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {

    }


    @Override
    public boolean onTouchEvent(MotionEvent event) {
        return main.onTouchEvent(event);
    }

    @Override
    public void onBackPressed() {
        main.onBackPressed();
    }

    @Override
    public void setContentView(int resId) {
        main.setContentView(resId);
    }

    @NonNull
    @Override
    public View findViewById(int id) {
        return main.findViewById(id);
    }

    @Override
    public void startActivity(@NonNull Intent intent) {
        main.startActivity(intent);
    }

    @Override
    public Intent getIntent() {
        if (main!=null){
            return main.getIntent();
        }
        return super.getIntent();
    }

    @Override
    public ClassLoader getClassLoader() {
        return main.getClassLoader();
    }

    @NonNull
    @Override
    public LayoutInflater getLayoutInflater() {
        return main.getLayoutInflater();
    }

    @Override
    public ApplicationInfo getApplicationInfo() {
        return main.getApplicationInfo();
    }

    @Override
    public Window getWindow() {
        return main.getWindow();
    }
}
