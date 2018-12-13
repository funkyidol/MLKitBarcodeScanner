package com.funkyidol.mlkitbarcodescanner.api

import com.squareup.moshi.Moshi
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import timber.log.Timber
import java.util.concurrent.TimeUnit

class RetrofitModule {
    private val BASE_URL: String = "https://www.buycott.com/"

    private lateinit var retrofit: Retrofit

    fun initRetrofit(): Retrofit {
        retrofit = Retrofit.Builder().baseUrl(BASE_URL)
            .client(getOkHttpClient(getHttpLoggingInterceptor()))
            .addConverterFactory(MoshiConverterFactory.create(getMoshiInstance()))
            .build()
        return retrofit
    }

    fun getMoshiInstance(): Moshi {
        return Moshi.Builder()
//            .add(NullPrimitiveAdapter())
            .build()
    }

    fun getOkHttpClient(httpLoggingInterceptor: HttpLoggingInterceptor): OkHttpClient {
        httpLoggingInterceptor.setLevel(HttpLoggingInterceptor.Level.BODY)
//        networkConnectionInterceptor = new NetworkConnectionInterceptor (context);
//        hvAuthenticator = new HvAuthenticator (sharedPrefUtil, moshi, httpLoggingInterceptor, hvDb);

        return OkHttpClient.Builder()
            .connectTimeout(5, TimeUnit.MINUTES)
            .readTimeout(5, TimeUnit.MINUTES)
            .addInterceptor(httpLoggingInterceptor)
            .build()
    }

    fun getHttpLoggingInterceptor(): HttpLoggingInterceptor {
        val httpLoggingInterceptor: HttpLoggingInterceptor = HttpLoggingInterceptor(HttpLoggingInterceptor.Logger {
            Timber.d(it)
        })
        httpLoggingInterceptor.level = HttpLoggingInterceptor.Level.BODY
        return httpLoggingInterceptor
    }

    /*class NullPrimitiveAdapter {
        @FromJson
        public intFromJson(@Nullable Integer value) {
            if (value == null) {
                return 0;
            }
            return value;
        }

        @FromJson
        public boolean booleanFromJson(@Nullable Boolean value) {
            if (value == null) {
                return false;
            }
            return value;
        }

        @FromJson
        public double doubleFromJson(@Nullable Double value) {
            if (value == null) {
                return 0;
            }
            return value;
        }
    }*/

}