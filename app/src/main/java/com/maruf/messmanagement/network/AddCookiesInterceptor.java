package com.maruf.messmanagement.network;

import android.content.Context;
import android.preference.PreferenceManager;
import android.util.Log;

import androidx.annotation.NonNull;

import com.google.gson.Gson;

import java.io.IOException;
import java.util.HashSet;

import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;

public class AddCookiesInterceptor implements Interceptor {

    public static final String PREF_COOKIES = "PREF_COOKIES";
    private final Context context;

    public AddCookiesInterceptor(Context context) {
        this.context = context;
    }

    @NonNull
    @Override
    public Response intercept(Interceptor.Chain chain) throws IOException {
        Log.d("lllll", "AddCookiesInterceptor");

        Request.Builder builder = chain.request().newBuilder();

        HashSet<String> preferences = (HashSet<String>) PreferenceManager.getDefaultSharedPreferences(context).getStringSet(PREF_COOKIES, new HashSet<String>());

        Request original = chain.request();

        Log.d("llll",original.url().toString());

        for (String cookie : preferences) {
            builder.addHeader("Cookie", cookie);
            Log.d("lllll", cookie);

        }

        return chain.proceed(builder.build());
    }
}