package io.radev.catchit

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query

/*
 * Created by radek on 27/05/2020.
 * Pocketworks 2020.
 */

interface ApiService {
    @GET("/v3/uk/places.json")
    fun getNearbyPlaces(
        @Query("app_id") appId: String=ApiConstants.API_APP_ID,
        @Query("app_key") appKey: String=ApiConstants.API_APP_KEY,
        @Query("lat") lat: Double=53.8288722,
        @Query("lon") lon: Double=-1.5729408,
        @Query("type") type: String = "bus_stop"
    ): Call<PlacesResponse>
}

@JsonClass(generateAdapter = true)
data class PlacesResponse(@Json(name="request_time") val requestTime:String,
@Json(name="source") val source:String,
@Json(name="acknowledgements") val acknowledgements:String,
@Json(name="member")val memberList:List<PlaceMember>)

@JsonClass(generateAdapter = true)
data class PlaceMember(@Json(name="type") val type:String,
                       @Json(name="name") val name:String,
                       @Json(name="latitude") val latitude:Double,
                       @Json(name="longitude") val longitude:Double,
                       @Json(name="accuracy") val accuracy:Int,
                       @Json(name="atcocode") val atcocode:String,
                       @Json(name="description") val description:String,
                       @Json(name="distance") val distance:Int)


//class HeadersInterceptor :
//    Interceptor {
//    override fun intercept(chain: Interceptor.Chain): Response {
//        val builder = chain.request().newBuilder()
//
//            builder.addHeader(
//                "Authorization",
//                "Bearer: " + authorizationSettingsRepository.getApiAuthorizationToken()
//            )
//
//        builder.addHeader(
//            "accept",
//            "application/vnd.carbsandcals.v1+json"
//        )
//        return chain.proceed(builder.build())
//    }
//}