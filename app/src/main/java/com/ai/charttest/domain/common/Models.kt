package com.ai.charttest.domain.common

import android.os.Parcelable
import com.squareup.moshi.JsonClass
import kotlinx.android.parcel.Parcelize

@JsonClass(generateAdapter = true)
data class CompositePointsResponse(
    val result: Int,
    val response: PointsResponse
)

@JsonClass(generateAdapter = true)
data class CompositeErrorResponse(
    val response: ErrorResponse
)

@JsonClass(generateAdapter = true)
data class ErrorResponse(
    val result: Int,
    val message: String
)

@JsonClass(generateAdapter = true)
data class PointsResponse(val points: List<Point>?)

@Parcelize
@JsonClass(generateAdapter = true)
data class Point(val x: Float, val y: Float) : Parcelable

@JsonClass(generateAdapter = true)
data class GetPointsParam(val version: String, val count: Int)