package com.mousom.jiscesoochana.data.api

import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Url

interface GetNoticeDataApi {
    @GET
    suspend fun getNoticeData(@Url url: String): Response<String>
}