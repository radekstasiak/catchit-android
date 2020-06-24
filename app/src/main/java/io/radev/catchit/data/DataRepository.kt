package io.radev.catchit.data

import androidx.lifecycle.LiveData
import io.radev.catchit.db.CatchItDatabase
import io.radev.catchit.db.FavouriteLine
import io.radev.catchit.db.FavouriteStop
import io.radev.catchit.network.ApiService
import io.radev.catchit.network.DepartureResponse
import io.radev.catchit.network.PlacesResponse
import io.radev.catchit.network.PostCodeDetailsResponse
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject

/*
 * Created by radoslaw on 15/06/2020.
 * radev.io 2020.
 */

// TODO
// update error handling with
// https://proandroiddev.com/create-retrofit-calladapter-for-coroutines-to-handle-response-as-states-c102440de37a
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

    override suspend fun findFavouriteLineByAtcocode(atcocode: String): List<FavouriteStop> =
        db.favouriteStopDao().findByAtcocode(atcocode = atcocode)

    override suspend fun getPostCodeDetails(postCode: String): PostCodeDetailsResponse =
        withContext(Dispatchers.IO) {
            val result = apiService.getPostCodeDetails(query = postCode)
            withContext(Dispatchers.Main) {
                result
            }

        }

    override suspend fun getLiveTimetable(atcocode: String): DepartureResponse =
        withContext(Dispatchers.IO) {
            val result = apiService.getLiveTimetable(atcocode = atcocode)
            withContext(Dispatchers.Main) {
                result
            }
        }

    override suspend fun getNearbyPlaces(
        longitude: Double,
        latitude: Double
    ): PlacesResponse =
        withContext(Dispatchers.IO) {
            val result =
                apiService.getNearbyPlaces(
                    lon = longitude,
                    lat = latitude
                )

            withContext(Dispatchers.Main) {
                result
            }
        }

    override fun getAllFavouriteStops(): LiveData<List<FavouriteStop>> =
        db.favouriteStopDao().getAll()

    override fun getAllFavouriteLines(): LiveData<List<FavouriteLine>> =
        db.favouriteLineDao().getAll()

}


interface DataRepository {
    suspend fun addFavouriteLine(favouriteLine: FavouriteLine)
    suspend fun removeFavouriteLineByAtcocodeAndLineName(atcocode: String, lineName: String)

    suspend fun addFavouriteStop(favouriteStop: FavouriteStop)
    suspend fun removeFavouriteStopByAtcocode(atcocode: String)
    suspend fun findFavouriteLineByAtcocode(atcocode: String): List<FavouriteStop>

    suspend fun getPostCodeDetails(postCode: String): PostCodeDetailsResponse
    suspend fun getLiveTimetable(atcocode: String): DepartureResponse
    suspend fun getNearbyPlaces(longitude: Double, latitude: Double): PlacesResponse

    fun getAllFavouriteStops(): LiveData<List<FavouriteStop>>
    fun getAllFavouriteLines(): LiveData<List<FavouriteLine>>
}