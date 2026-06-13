package com.example.graduationproject;

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
    
    // ملاحظة: تأكد من أن هذا هو الـ Anon Key الصحيح من إعدادات Supabase
    private static final String ANON_KEY = "sb_publishable_j0arqXUgPz7zBTN9U4vJtg_T9TcxxvO";

    private static Retrofit retrofit = null;

    public static Retrofit getClient() {
        if (retrofit == null) {
            OkHttpClient okHttpClient = new OkHttpClient.Builder()
                    .connectTimeout(30, TimeUnit.SECONDS)
                    .readTimeout(30, TimeUnit.SECONDS)
                    .writeTimeout(30, TimeUnit.SECONDS)
                    .addInterceptor(new Interceptor() {
                        @Override
                        public Response intercept(Chain chain) throws IOException {
                            Request original = chain.request();
                            Request request = original.newBuilder()
                                    .header("apikey", ANON_KEY)
                                    .header("Authorization", "Bearer " + ANON_KEY)
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
        }
        return retrofit;
    }
}
