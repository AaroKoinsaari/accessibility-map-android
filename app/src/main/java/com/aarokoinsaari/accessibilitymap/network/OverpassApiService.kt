package com.aarokoinsaari.accessibilitymap.network

import com.aarokoinsaari.accessibilitymap.model.OverpassApiResponse
import retrofit2.http.GET
import retrofit2.http.Query

interface OverpassApiService {
    @GET("interpreter")
    suspend fun getMarkers(@Query("data") data: String): OverpassApiResponse
}
