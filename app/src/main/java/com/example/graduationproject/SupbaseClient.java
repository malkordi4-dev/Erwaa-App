package com.example.graduationproject;

import android.content.Context;
import android.content.SharedPreferences;
import java.io.IOException;
import java.util.concurrent.TimeUnit;
import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class SupbaseClient {

    private static final String BASE_URL = "https://ykuhyfdwvrhvafswyisl.supabase.co/";
    
    // ملاحظة: هذا المفتاح يجب أن يكون "anon public" الطويل الموجود في إعدادات Supabase
    private static final String ANON_KEY = "sb_publishable_j0arqXUgPz7zBTN9U4vJtg_T9TcxxvO";

    private static Retrofit retrofit = null;

    public static Retrofit getClient(Context context) {
        OkHttpClient okHttpClient = new OkHttpClient.Builder()
                .connectTimeout(30, TimeUnit.SECONDS)
                .addInterceptor(new Interceptor() {
                    @Override
                    public Response intercept(Chain chain) throws IOException {
                        // قراءة التوكن الخاص بالمستخدم من التفضيلات
                        SharedPreferences prefs = context.getSharedPreferences("AppPrefs", Context.MODE_PRIVATE);
                        String userToken = prefs.getString("access_token", ANON_KEY);

                        Request original = chain.request();
                        Request request = original.newBuilder()
                                .header("apikey", ANON_KEY)
                                .header("Authorization", "Bearer " + userToken)
                                .header("Content-Type", "application/json")
                                .method(original.method(), original.body())
                                .build();
                        return chain.proceed(request);
                    }
                }).build();

        retrofit = new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .client(okHttpClient)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        return retrofit;
    }
}
