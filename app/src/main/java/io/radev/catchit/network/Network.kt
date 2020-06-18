package io.radev.catchit.network

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass
import io.radev.catchit.DateTimeConverter
import io.radev.catchit.PlaceMemberModel
import io.radev.catchit.SingleBusNotificationModel
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

/*
 * Created by radek on 27/05/2020.
 * radev.io 2020.
 */

interface ApiService {


    @GET("/v3/uk/places.json")
    suspend fun getPostCodeDetails(
        @Query("app_id") appId: String = ApiConstants.API_APP_ID,
        @Query("app_key") appKey: String = ApiConstants.API_APP_KEY,
        @Query("query") query: String,
        @Query("type") type: String = "postcode"
    ): PostCodeDetailsResponse

    @GET("/v3/uk/places.json")
    suspend fun getNearbyPlaces(
        @Query("app_id") appId: String = ApiConstants.API_APP_ID,
        @Query("app_key") appKey: String = ApiConstants.API_APP_KEY,
        @Query("lat") lat: Double = 53.8288722,
        @Query("lon") lon: Double = -1.5729408,
        @Query("type") type: String = "bus_stop"
    ): PlacesResponse

    @GET("/v3/uk/bus/stop/{atcocode}/live.json")
    suspend fun getLiveTimetable(
        @Path("atcocode") atcocode: String,
        @Query("app_id") appId: String = ApiConstants.API_APP_ID,
        @Query("app_key") appKey: String = ApiConstants.API_APP_KEY,
        @Query("group") group: String = "no",
        @Query("nextbuses") nextbuses: String = "no"
    ): DepartureResponse


}
//{
//    "request_time": "2020-05-29T11:08:42+01:00",
//    "source": "O.S. (CodePoint)",
//    "acknowledgements": "Ordance Survey Open Data (CodePoint postcodes)",
//    "member": [
//    {
//        "type": "postcode",
//        "name": "LS6 4RQ",
//        "latitude": 53.82896245787713,
//        "longitude": -1.570304690443911,
//        "accuracy": 100
//    }
//    ]
//}

@JsonClass(generateAdapter = true)
data class PostCodeDetailsResponse(
    @Json(name = "request_time") val requestTime: String,
    @Json(name = "source") val source: String,
    @Json(name = "acknowledgements") val acknowledgements: String,
    @Json(name = "member") val memberList: List<PostCodeMember>
)

@JsonClass(generateAdapter = true)
data class PostCodeMember(
    @Json(name = "type") val type: String,
    @Json(name = "name") val name: String,
    @Json(name = "latitude") val latitude: Double,
    @Json(name = "longitude") val longitude: Double,
    @Json(name = "accuracy") val accuracy: Int
)

// EXAMPLE DATA
//"atcocode": "450010687",
//"smscode": "45010687",
//"request_time": "2020-05-28T15:22:36+01:00",
//"name": "Boar Lane (stop T2)",
//"stop_name": "Boar Lane",
//"bearing": "E",
//"indicator": "stop T2",
//"locality": "Leeds City Centre, Leeds",
//"location": {
//    "type": "Point",
//    "coordinates": [
//    -1.54345,
//    53.796
//    ]
//},
@JsonClass(generateAdapter = true)
data class DepartureResponse(
    @Json(name = "atcocode") val atcocode: String?,
    @Json(name = "smscode") val smsCode: String?,
    @Json(name = "request_time") val requestTime: String?,
    @Json(name = "name") val name: String?,
    @Json(name = "stop_name") val stopName: String?,
    @Json(name = "bearing") val bearing: String?,
    @Json(name = "indicator") val indicator: String?,
    @Json(name = "locality") val locality: String?,
    @Json(name = "location") val location: StopLocationDetails?,
    @Json(name = "departures") val departures: Map<String, List<DepartureDetails>>?
)

// EXAMPLE DATA
//"mode": "bus",
//"line": "7",
//"line_name": "7",
//"direction": "Primley Park",
//"operator": "FLDS",
//"date": "2020-05-28",
//"expected_departure_date": "2020-05-28",
//"aimed_departure_time": "15:20",
//"expected_departure_time": "15:20",
//"best_departure_estimate": "15:20",
//"status": {
//    "cancellation": {
//        "value": false,
//        "reason": null
//    }
//},
//"source": "FirstTicketer",
//"dir": "outbound",
//"operator_name": "First Leeds",
//"id": "https://transportapi.com/v3/uk/bus/route/FLDS/7/outbound/450010687/2020-05-28/15:20/timetable.json?app_id=68755067&app_key=1f81945ff77187126de7f9f93c5fab44"
@JsonClass(generateAdapter = true)
data class DepartureDetails(
    @Json(name = "mode") val mode: String?,
    @Json(name = "line") val line: String?,
    @Json(name = "line_name") val lineName: String?,
    @Json(name = "direction") val direction: String?,
    @Json(name = "operator") val operator: String?,
    @Json(name = "date") val date: String?,
    @Json(name = "expected_departure_date") val expectedDepartureDate: String?,
    @Json(name = "aimed_departure_time") val aimedDepartureTime: String?,
    @Json(name = "expected_departure_time") val expectedDepartureTime: String?,
    @Json(name = "best_departure_estimate") val bestDepartureEstimate: String?,
    @Json(name = "status") val status: Map<String, DepartureStatus>?,
    @Json(name = "source") val source: String?,
    @Json(name = "dir") val dir: String?,
    @Json(name = "operator_name") val operatorName: String?,
    @Json(name = "id") val id: String?
)

fun DepartureDetails.toSingleBusNotificationModel(dateTimeConverter: DateTimeConverter): SingleBusNotificationModel {
    val waitTime = dateTimeConverter.getWaitTime(
        startTime = dateTimeConverter.getNowInMillis(),
        endTime = dateTimeConverter.convertDateAndTimeToMillis(
            date = this.expectedDepartureDate ?: this.date!!,
            time = this.expectedDepartureTime ?: this.aimedDepartureTime!!
        )
    )
    return SingleBusNotificationModel(
        line = this.line ?: "",
        direction = this.direction ?: "",
        waitTime = if (waitTime > 0) "${waitTime}m" else "DUE"
    )
}


@JsonClass(generateAdapter = true)
data class DepartureStatus(
    @Json(name = "value") val value: Boolean?,
    @Json(name = "reason") val reason: String?
)

@JsonClass(generateAdapter = true)
data class StopLocationDetails(
    @Json(name = "type") val type: String?,
    @Json(name = "coordinates") val coordinates: List<Double>?
)


@JsonClass(generateAdapter = true)
data class PlacesResponse(
    @Json(name = "request_time") val requestTime: String,
    @Json(name = "source") val source: String,
    @Json(name = "acknowledgements") val acknowledgements: String,
    @Json(name = "member") val memberList: List<PlaceMember>
)

@JsonClass(generateAdapter = true)
data class PlaceMember(
    @Json(name = "type") val type: String,
    @Json(name = "name") val name: String,
    @Json(name = "latitude") val latitude: Double,
    @Json(name = "longitude") val longitude: Double,
    @Json(name = "accuracy") val accuracy: Int,
    @Json(name = "atcocode") val atcocode: String,
    @Json(name = "description") val description: String,
    @Json(name = "distance") val distance: Int
)


fun PlaceMember.toPlaceMemberModel(favourite: Boolean): PlaceMemberModel {
    return PlaceMemberModel(
        name = this.name,
        atcocode = this.atcocode,
        description = this.description,
        distance = this.distance.toString(),
        isFavourite = favourite
    )

}

object ApiConstants {
    const val API_BASE_URL = "https://transportapi.com/"
    const val API_APP_ID = "68755067"
    const val API_APP_KEY = "1f81945ff77187126de7f9f93c5fab44"
}
