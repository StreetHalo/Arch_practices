package com.example.arch_practices.utils

import android.util.Log
import retrofit2.HttpException

suspend fun <T: Any> handleRequest(requestFunc: suspend () -> T): Result<T> {
    return try {
        Result.success(requestFunc.invoke())
    } catch (he: HttpException) {
        Log.d("request_error","HttpException ${he.message()}")
        Result.failure(he)
    } catch (e: Exception) {
        Log.d("request_error","Exception ${e.localizedMessage}")
        Result.failure(e)
    }
}