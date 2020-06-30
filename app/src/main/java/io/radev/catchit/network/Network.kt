package io.radev.catchit.network

import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.LatLngBounds
import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass
import io.radev.catchit.*
import io.radev.catchit.domain.DepartureDetailsDomainModel
import io.radev.catchit.domain.DepartureDomainModel
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

/*
 * Created by radek on 27/05/2020.
 * radev.io 2020.
 */

interface ApiService {

    //TODO app id and api key add to the header interceptor
    @GET("/v3/uk/places.json")
    suspend fun getPostCodeDetails(
        @Query("app_id") appId: String = ApiConstants.API_APP_ID,
        @Query("app_key") appKey: String = ApiConstants.API_APP_KEY,
        @Query("query") query: String,
        @Query("type") type: String = "postcode"
    ): NetworkResponse<PostCodeDetailsResponse, ErrorResponse>

    @GET("/v3/uk/places.json")
    suspend fun getNearbyPlaces(
        @Query("app_id") appId: String = ApiConstants.API_APP_ID,
        @Query("app_key") appKey: String = ApiConstants.API_APP_KEY,
        @Query("lat") lat: Double = 53.8288722,
        @Query("lon") lon: Double = -1.5729408,
        @Query("type") type: String = "bus_stop"
    ): NetworkResponse<PlacesResponse, ErrorResponse>

    @GET("/v3/uk/bus/stop/{atcocode}/live.json")
    suspend fun getLiveTimetable(
        @Path("atcocode") atcocode: String,
        @Query("app_id") appId: String = ApiConstants.API_APP_ID,
        @Query("app_key") appKey: String = ApiConstants.API_APP_KEY,
        @Query("group") group: String = "no",
        @Query("nextbuses") nextbuses: String = "no"
    ): NetworkResponse<DepartureResponse, ErrorResponse>


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

//@JsonClass(generateAdapter = true)
//object ErrorResponse

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

fun DepartureResponse.toDomainModel(dateTimeConverter: DateTimeConverterImpl): DepartureDomainModel =
    DepartureDomainModel(
        atcocode = this.atcocode ?: "",
        smsCode = this.smsCode ?: "",
        requestTime = this.requestTime ?: "",
        name = this.name ?: "",
        stopName = this.stopName ?: "",
        bearing = this.bearing ?: "",
        indicator = this.indicator ?: "",
        locality = this.locality ?: "",
        location = if (this.location != null && this.location.coordinates != null && this.location.coordinates.size > 1) Pair(
            location.coordinates[0],
            location.coordinates[1]
        ) else Pair(
            0.0,
            0.0
        ),
        departures = if (departures != null && departures.containsKey("all")) (this.departures["all"]
            ?: listOf()).toDepartureDetailsDomainModelList(dateTimeConverter = dateTimeConverter) else listOf()
    )

fun List<DepartureDetails>.toDepartureDetailsDomainModelList(dateTimeConverter: DateTimeConverterImpl): List<DepartureDetailsDomainModel> =
    this.map {
        DepartureDetailsDomainModel(
            mode = it.mode ?: "",
            line = it.line ?: "",
            lineName = it.lineName ?: "",
            direction = it.direction ?: "",
            operator = it.operator ?: "",
            bestDepartureEstimate = if (it.date != null && it.bestDepartureEstimate != null) dateTimeConverter.convertDateAndTimeToMillis(
                date = it.date,
                time = it.bestDepartureEstimate
            ) else 0L,
            source = it.source ?: "",
            dir = it.dir ?: "",
            operatorName = it.operatorName ?: "",
            id = it.id ?: ""
        )
    }

fun DepartureDetails.toDepartureDetailsModel(atcocode: String, favourite: Boolean) =
    DepartureDetailsModel(
        departureTime = this.expectedDepartureTime ?: this.aimedDepartureTime!!,
        departureDate = this.expectedDepartureDate ?: this.date!!,
        lineName = this.lineName ?: "",
        direction = this.direction ?: "",
        operator = this.operator ?: "",
        mode = this.mode ?: "",
        atcocode = atcocode,
        isFavourite = favourite,
        waitTime = ""
    )

fun DepartureDetails.toSingleBusNotificationModel(dateTimeConverter: DateTimeConverterImpl): SingleBusNotificationModel {
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

@JsonClass(generateAdapter = true)
data class ErrorResponse(
    @Json(name = "error") val error: Int?,
    @Json(name = "message") val message: String?
)


fun PlaceMember.toPlaceMemberModel(favourite: Boolean): PlaceMemberModel {
    return PlaceMemberModel(
        name = this.name,
        atcocode = this.atcocode,
        description = this.description,
        distance = this.distance.toString(),
        isFavourite = favourite,
        longitude = this.longitude,
        latitude = this.latitude
    )
}

fun List<PlaceMemberModel>.toDeparturesMap(userLatLng: LatLng): DepartureMapModel {
    val latList = this.map { it.latitude }
    val lngList = this.map { it.longitude }

    val latLngMax =
        LatLng(latList.min() ?: userLatLng.latitude, lngList.min() ?: userLatLng.longitude)
    val latLngMin =
        LatLng(latList.max() ?: userLatLng.latitude, lngList.max() ?: userLatLng.longitude)

    return DepartureMapModel(
        departuresList = this,
        userLatLng = userLatLng, //select the one with highest accuracy
        latLngBounds = LatLngBounds(latLngMax, latLngMin)
    )

}

object ApiConstants {
    const val API_BASE_URL = "https://transportapi.com/"
    const val API_APP_ID = "68755067"
    const val API_APP_KEY = "1f81945ff77187126de7f9f93c5fab44"
}

