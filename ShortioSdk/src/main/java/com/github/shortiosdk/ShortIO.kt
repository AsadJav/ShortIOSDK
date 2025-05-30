package com.github.shortio

import okhttp3.*
import okhttp3.MediaType.Companion.toMediaType
import com.google.gson.Gson
import okhttp3.RequestBody.Companion.toRequestBody


object ShortioSdk {
    fun shortenUrl(
        apiKey: String,
        parameters: ShortIOParametersModel
    ): ShortIOResponseModel? {
        val gson = Gson()
        val client = OkHttpClient()
        val mediaType = "application/json".toMediaType()
        val jsonBody = gson.toJson(parameters)
        val body = jsonBody.toRequestBody(mediaType)

        val request = Request.Builder()
            .url("https://api.short.io/links/public")
            .post(body)
            .addHeader("accept", "application/json")
            .addHeader("content-type", "application/json")
            .addHeader("authorization", apiKey)
            .build()

        val response = client.newCall(request).execute()
        if (response.isSuccessful) {
            response.body?.string()?.let { json ->
                return gson.fromJson(json, ShortIOResponseModel::class.java)
            }
        }

        return null
    }
}
