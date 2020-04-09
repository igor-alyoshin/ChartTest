package com.ai.charttest.domain.common

import com.squareup.moshi.JsonAdapter
import com.squareup.moshi.Moshi
import com.squareup.moshi.Types
import java.lang.reflect.Type

inline fun <reified T> String?.fromJson(moshi: Moshi): T? =
        this?.let { MoshiUtil.fromJson(this, T::class.java, moshi = moshi) }

inline fun <reified T> T?.toJson(moshi: Moshi): String =
        MoshiUtil.toJson(this, T::class.java, moshi = moshi)

inline fun <reified T> Moshi.fromJson(json: String?): T? =
        json?.let { MoshiUtil.fromJson(json, T::class.java, moshi = this) }

inline fun <reified T> Moshi.toJson(t: T?): String =
        MoshiUtil.toJson(t, T::class.java, moshi = this)

fun <T> T?.toJson(typeOf: Type, moshi: Moshi): String =
        MoshiUtil.toJson(this, typeOf, moshi = moshi)

object MoshiUtil {

    fun <T> toJson(any: T?, classOfT: Class<T>, moshi: Moshi): String {
        return moshi.adapter(classOfT).toJson(any)
    }

    fun <T> toJson(any: T?, typeOf: Type, moshi: Moshi): String {
        return moshi.adapter<T>(typeOf).toJson(any)
    }

    fun <T> fromJson(json: String, classOfT: Class<T>, moshi: Moshi): T? {
        return moshi.adapter(classOfT).fromJson(json)
    }

    fun <T> fromJson(json: String, typeOfT: Type, moshi: Moshi): T? {
        return moshi.adapter<T>(typeOfT).fromJson(json)
    }

    fun <T> listToJson(list: List<T>?, classOfT: Class<T>, moshi: Moshi): String {
        val type = Types.newParameterizedType(List::class.java, classOfT)
        val adapter: JsonAdapter<List<T>> = moshi.adapter(type)
        return adapter.toJson(list)
    }

    fun <T> jsonToList(json: String, classOfT: Class<T>, moshi: Moshi): List<T>? {
        val type = Types.newParameterizedType(List::class.java, classOfT)
        val adapter = moshi.adapter<List<T>>(type)
        return adapter.fromJson(json)
    }
}