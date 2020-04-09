package com.ai.charttest.data

import kotlinx.coroutines.Deferred
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.POST
import retrofit2.http.Query

interface RestApi {
    @FormUrlEncoded
    @POST("mobws/json/pointsList")
    fun getPoints(
        @Query("version") version: String,
        @Field("count") count: Int
    ): Deferred<String>
}