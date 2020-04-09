package com.ai.charttest.domain

import com.ai.charttest.data.RestApi
import com.ai.charttest.domain.common.*
import com.squareup.moshi.JsonDataException
import com.squareup.moshi.Moshi


class GetPointsUseCase(private val restApi: RestApi, private val moshi: Moshi) :
    BaseUseCase<CompositePointsResponse, GetPointsParam>() {

    override suspend fun run(param: GetPointsParam): Either<Failure, CompositePointsResponse> {
        val responseString = restApi.getPoints(param.version, param.count).await()
        return try {
            val response =
                moshi.fromJson<CompositePointsResponse>(responseString)
                    ?: throw JsonDataException("Can't parse json")
            Either.Right(response)
        } catch (e: JsonDataException) {
            val response =
                moshi.fromJson<CompositeErrorResponse>(responseString)?.response
                    ?: throw JsonDataException("Can't parse json")
            Either.Left(Failure.ApiFailure(response.result, response.message))
        }
    }
}