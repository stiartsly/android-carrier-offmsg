package org.elastos.carrier.chatdemo;

import android.content.Context;
import android.content.SharedPreferences;

class PreferenceUtil {
    private static final String PREFERENCE = "Preference_Test";

    private static SharedPreferences getDefaultPreference(Context context) {
        return context.getSharedPreferences(PREFERENCE, Context.MODE_PRIVATE);
    }

    static synchronized String getString(Context context, String key) {
        return getDefaultPreference(context).getString(key, null);
    }

    static synchronized void saveString(Context context, String key, String value) {
        SharedPreferences share = getDefaultPreference(context);
        SharedPreferences.Editor editor = share.edit();
        editor.putString(key, value);
        editor.apply();
    }
}
