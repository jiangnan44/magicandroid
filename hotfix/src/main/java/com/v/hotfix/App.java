package com.v.hotfix;

import android.app.Application;
import android.content.Context;

import java.io.File;

/**
 * Author:v
 * Time:2022/3/29
 */
public class App extends Application {

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
        File file = getFilesDir();
        if (!file.exists()) {
            file.mkdirs();
        }
        FixUtil.installPatch(this, getFilesDir() + File.separator + "patch.dex");
    }
}
