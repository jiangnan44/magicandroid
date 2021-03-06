package com.v.hotfix;

import android.content.Context;
import android.os.Build;
import android.util.Log;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Array;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.sql.Ref;
import java.util.ArrayList;
import java.util.List;

/**
 * Author:v
 * Time:2022/3/29
 */
public class FixUtil {
    private static final String TAG = "FixUtil";

    public static void installPatch(Context context, String patchPath) {
        File patchFile = new File(patchPath);
        if (!patchFile.exists()) {
            Log.w(TAG, "empty patch file on:" + patchPath + ". NO need to fix");
            return;
        }

        File optDir = context.getCacheDir();
        try {
            ClassLoader classLoader = context.getClassLoader();

            //get pathList
            Class<? extends ClassLoader> pathClassLoaderCls = classLoader.getClass();
            Field pathListField = ReflectUtil.getField(pathClassLoaderCls, "pathList");
            Object pathList = pathListField.get(classLoader);

            //get dexElements
            Field dexElementsField = ReflectUtil.getField(pathList.getClass(), "dexElements");
            Object[] dexElements = (Object[]) dexElementsField.get(pathList);

            //load dex,turn to Element[]

            Object[] patchElements = null;
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                Method makeDexElementsMethod = ReflectUtil.getMethod(
                        pathList.getClass(),
                        "makeDexElements", List.class, File.class, List.class, ClassLoader.class);
                List<File> dexFileList = new ArrayList<>();
                dexFileList.add(patchFile);
                ArrayList<IOException> suppressedExceptions = new ArrayList<>();
                patchElements = (Object[]) makeDexElementsMethod.invoke(null, dexFileList, optDir, suppressedExceptions, classLoader);
            } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                // Android 6.0-7.0 ??????makePathElements(List,File,List)
                Method makePathElements = ReflectUtil.getMethod(pathList.getClass(), "makePathElements", List.class, File.class, List.class);
                List<File> dexFileList = new ArrayList<>();
                dexFileList.add(patchFile);
                ArrayList<IOException> suppressedExceptions = new ArrayList<IOException>();
                patchElements = (Object[]) makePathElements.invoke(null, dexFileList, optDir, suppressedExceptions);
            } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                // Android 5.0-6.0 ??????makeDexElements(ArrayList,File,ArrayList)  ????????????????????????ArrayList??????
                Method makeDexElementsMethod = ReflectUtil.getMethod(pathList.getClass(), "makeDexElements", ArrayList.class, File.class, ArrayList.class);
                List<File> dexFileList = new ArrayList<>();
                dexFileList.add(patchFile);
                ArrayList<IOException> suppressedExceptions = new ArrayList<IOException>();
                patchElements = (Object[]) makeDexElementsMethod.invoke(null, dexFileList, optDir, suppressedExceptions);
            } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                // Android 4.4-5.0 ??????makeDexElements(ArrayList,File,ArrayList)  ????????????????????????ArrayList??????
                // ???5.0????????????????????????4.4 ????????? Class ref in pre-verified class resolved to unexpected implementation
                Log.e(TAG, "installPatch: ???????????????" + Build.VERSION.SDK_INT + "????????????????????? Class ref in pre-verified class resolved to unexpected implementation ???????????????????????????");
                return;
            } else {
                Log.e(TAG, "installPatch: ???????????????" + Build.VERSION.SDK_INT + "??????????????????");
                return;
            }
            // merge two Element[] put patchElements before old one
            Object[] newElements = (Object[]) Array.newInstance(dexElements.getClass().getComponentType(), dexElements.length + patchElements.length);
            System.arraycopy(patchElements, 0, newElements, 0, patchElements.length);
            System.arraycopy(dexElements, 0, newElements, patchElements.length, dexElements.length);

            dexElementsField.set(pathList, newElements);
            Log.w(TAG, "Fixed the Bug!!");
        } catch (Exception e) {
            e.printStackTrace();
            Log.e(TAG, "installPatch Failed!! msg:" + e.getMessage());
        }
    }
}
