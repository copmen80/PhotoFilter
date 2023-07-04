package com.photo.filter.app.interceptor

import okhttp3.Request

class RequestBuilderHeadersTransformer(
) {
    operator fun invoke(request: Request): Request.Builder {
        val requestBuilder = request.newBuilder()
        requestBuilder.header(
            "Authorization",
            "Client-ID " + "SA8nQ8_8Cjx9MC_O303PeGI_jRVxf1ogBSv9JaPDUqQ"
        )
        return requestBuilder
    }
}
