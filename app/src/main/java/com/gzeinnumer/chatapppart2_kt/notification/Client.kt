package com.gzeinnumer.chatapppart2_kt.notification

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

//todo 79
object Client {
    private var retrofit: Retrofit? = null
    fun getCLient(url: String?): Retrofit? {
        if (retrofit == null) {
            retrofit = Retrofit.Builder()
                .baseUrl(url)
                .addConverterFactory(GsonConverterFactory.create())
                .build()
        }
        return retrofit
    }
}