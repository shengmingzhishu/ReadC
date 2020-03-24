package com.a3read.readc.utils;

import android.content.Context;
import android.content.SharedPreferences;

import java.util.HashSet;
import java.util.Set;

public class SharedUtils {
    public static void putWord(Set<String> stringSet, Context context){
        SharedPreferences sharedPreferences = context.getSharedPreferences("TXT_SET",context.MODE_PRIVATE);
        SharedPreferences.Editor editor =  sharedPreferences.edit();
        editor.putStringSet("learnSet",stringSet);
        editor.commit();
    }

    public static Set<String> getWord( Context context){
        SharedPreferences sharedPreferences = context.getSharedPreferences("TXT_SET",context.MODE_PRIVATE);
        Set<String> stringSet = sharedPreferences.getStringSet("learnSet",new HashSet<String>());
        return stringSet;
    }
}
