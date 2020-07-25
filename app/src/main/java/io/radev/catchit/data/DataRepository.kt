package io.radev.catchit.data

import androidx.lifecycle.LiveData
import io.radev.catchit.db.CatchItDatabase
import io.radev.catchit.db.FavouriteLine
import io.radev.catchit.db.FavouriteStop
import io.radev.catchit.network.*
import javax.inject.Inject

/*
 * Created by radoslaw on 15/06/2020.
 * radev.io 2020.
 */

class DataRepositoryImpl @Inject constructor(
    val db: CatchItDatabase,
    val apiService: ApiService
) :
    DataRepository {

    override suspend fun addFavouriteLine(favouriteLine: FavouriteLine) {
        db.favouriteLineDao().insertAll(favouriteLine)
    }

    override suspend fun removeFavouriteLineByAtcocodeAndLineName(
        atcocode: String,
        lineName: String
    ) {
        db.favouriteLineDao().deleteByAtcocodeAndLineName(atcocode = atcocode, lineName = lineName)
    }

    override suspend fun addFavouriteStop(favouriteStop: FavouriteStop) {
        db.favouriteStopDao().insertAll(favouriteStop)
    }

    override suspend fun removeFavouriteStopByAtcocode(atcocode: String) {
        db.favouriteStopDao().deleteByAtcocode(atcocode = atcocode)
    }

    override suspend fun findFavouriteStopByAtcocode(atcocode: String): List<FavouriteStop> =
        db.favouriteStopDao().findByAtcocode(atcocode = atcocode)

    override fun getAllFavouriteStops(): LiveData<List<FavouriteStop>> =
        db.favouriteStopDao().getAll()

    override fun getAllFavouriteLines(): LiveData<List<FavouriteLine>> =
        db.favouriteLineDao().getAll()

    override suspend fun getAllFavouriteLinesSync(): List<FavouriteLine> =
        db.favouriteLineDao().getAllSync()


    override suspend fun getFavouriteLinesByAtcocode(atcocode: String): List<FavouriteLine> =
        db.favouriteLineDao().getByAtcocode(atcocode = atcocode)

    override suspend fun getFavouriteLineByAtcocodeAndLineName(
        atcocode: String,
        lineName: String
    ): List<FavouriteLine> =
        db.favouriteLineDao().findByAtcocodeAndLine(atcocode = atcocode, lineName = lineName)


    //Retrofit takes care of the main-safety - no need to switch between threads
    //https://proandroiddev.com/do-i-need-to-call-suspend-functions-of-retrofit-and-room-on-a-background-thread-26650dac762d
    override suspend fun getPostCodeDetails(postCode: String): NetworkResponse<PostCodeDetailsResponse, ErrorResponse> =
        apiService.getPostCodeDetails(query = postCode)


    override suspend fun getLiveTimetable(atcocode: String): NetworkResponse<DepartureResponse, ErrorResponse> =
        apiService.getLiveTimetable(atcocode = atcocode)

    override suspend fun getNearbyPlaces(
        longitude: Double,
        latitude: Double
    ): NetworkResponse<PlacesResponse, ErrorResponse> = apiService.getNearbyPlaces(
        lon = longitude,
        lat = latitude
    )


}


interface DataRepository {
    suspend fun addFavouriteLine(favouriteLine: FavouriteLine)
    suspend fun findFavouriteStopByAtcocode(atcocode: String): List<FavouriteStop>
    suspend fun removeFavouriteLineByAtcocodeAndLineName(atcocode: String, lineName: String)

    suspend fun addFavouriteStop(favouriteStop: FavouriteStop)
    suspend fun removeFavouriteStopByAtcocode(atcocode: String)

    suspend fun getPostCodeDetails(postCode: String): NetworkResponse<PostCodeDetailsResponse, ErrorResponse>
    suspend fun getLiveTimetable(atcocode: String): NetworkResponse<DepartureResponse, ErrorResponse>
    suspend fun getNearbyPlaces(
        longitude: Double,
        latitude: Double
    ): NetworkResponse<PlacesResponse, ErrorResponse>

    fun getAllFavouriteStops(): LiveData<List<FavouriteStop>>
    fun getAllFavouriteLines(): LiveData<List<FavouriteLine>>

    //TODO add tests
    suspend fun getAllFavouriteLinesSync(): List<FavouriteLine>
    suspend fun getFavouriteLinesByAtcocode(atcocode: String): List<FavouriteLine>
    suspend fun getFavouriteLineByAtcocodeAndLineName(atcocode: String, lineName: String): List<FavouriteLine>
}