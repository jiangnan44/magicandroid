package com.v.hotfix;

import android.util.Log;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

/**
 * Author:v
 * Time:2022/3/29
 */
public class ReflectUtil {
    private static final String TAG = "ReflectUtil";

    public static Field getField(Class cls, String fieldName) {
        Field declaredField = null;
        while (cls != null) {
            Log.d(TAG, "getField,current Class=" + cls.getName());
            try {
                declaredField = cls.getDeclaredField(fieldName);
                if (declaredField != null) {
                    declaredField.setAccessible(true);
                    return declaredField;
                }
            } catch (NoSuchFieldException e) {
                e.printStackTrace();
            }
            cls = cls.getSuperclass();
        }

        return null;
    }


    public static Method getMethod(Class cls, String methodName, Class<?>... paramTypes) {

        Method declaredMethod = null;
        while (cls != null) {
            try {
                declaredMethod = cls.getDeclaredMethod(methodName, paramTypes);
                if (declaredMethod != null) {
                    declaredMethod.setAccessible(true);
                    return declaredMethod;
                }
                cls = cls.getSuperclass();
            } catch (NoSuchMethodException e) {
                e.printStackTrace();
            }
        }

        return null;

    }
}
