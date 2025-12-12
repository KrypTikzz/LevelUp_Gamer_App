package com.example.levelup_gamerapp.core

import okhttp3.Interceptor
import okhttp3.Response

/**
 * interceptor que agrega el token jwt a cada request
 * si el usuario está autenticado.
 */
class AuthInterceptor : Interceptor {

    override fun intercept(chain: Interceptor.Chain): Response {
        val requestOriginal = chain.request()
        val token = UserSession.token

        val requestConToken = if (token != null) {
            requestOriginal.newBuilder()
                .addHeader("Authorization", "Bearer $token")
                .build()
        } else {
            requestOriginal
        }

        return chain.proceed(requestConToken)
    }
}
