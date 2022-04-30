package com.mousom.jiscesoochana.repository

import com.mousom.jiscesoochana.data.api.RetrofitInstance
import retrofit2.Response
import retrofit2.http.Url

class BaseRepository {

    suspend fun getNoticeData(@Url url: String): Response<String> {
        return RetrofitInstance.getNoticeApi.getNoticeData(url)
    }
}