package com.benhan.bluegreen.network

import com.google.gson.Gson
import com.google.gson.GsonBuilder
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory


class ApiClient {

    var interceptor = HttpLoggingInterceptor()

    companion object{

        const val BASE_URL = "http://15.165.70.243/"

        private lateinit var retrofit: Retrofit




    }

    init {
        interceptor.level = HttpLoggingInterceptor.Level.BODY
    }



    var gson: Gson = GsonBuilder()
        .setLenient()
        .create()


    val client = OkHttpClient.Builder().addInterceptor(interceptor).build()


    fun getApiClient(): Retrofit {

        retrofit = Retrofit.Builder().baseUrl(
            BASE_URL
        )
            .addConverterFactory(GsonConverterFactory.create(gson))
            .client(client)
            .build()
        return retrofit
    }




    }