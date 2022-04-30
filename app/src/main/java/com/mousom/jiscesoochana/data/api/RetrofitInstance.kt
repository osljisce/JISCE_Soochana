package com.mousom.jiscesoochana.data.api

import com.mousom.jiscesoochana.utils.Constants.Companion.BASE_URL
import retrofit2.Retrofit
import retrofit2.converter.scalars.ScalarsConverterFactory

object RetrofitInstance {

    private val retrofit by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(ScalarsConverterFactory.create())
            .build()
    }

    val getNoticeApi: GetNoticeDataApi by lazy {
        retrofit.create(GetNoticeDataApi::class.java)
    }
}