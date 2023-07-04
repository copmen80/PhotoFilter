package com.photo.filter.app.interceptor

import okhttp3.Interceptor
import okhttp3.Response

class HeaderInterceptor(
    private val requestBuilderHeadersTransformer: RequestBuilderHeadersTransformer
) : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        val request = chain.request()
        val requestBuilder =
            requestBuilderHeadersTransformer.invoke(request)
        return chain.proceed(requestBuilder.method(request.method, request.body).build())
    }
}