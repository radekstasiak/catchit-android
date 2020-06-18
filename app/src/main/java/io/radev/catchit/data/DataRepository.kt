package io.radev.catchit.data

import io.radev.catchit.db.CatchItDatabase
import io.radev.catchit.db.FavouriteLine
import io.radev.catchit.db.FavouriteStop
import javax.inject.Inject

/*
 * Created by radoslaw on 15/06/2020.
 * radev.io 2020.
 */

class DataRepositoryImpl @Inject constructor(val db: CatchItDatabase) : DataRepository {

    override suspend fun addFavouriteLine(favouriteLine: FavouriteLine) {
        db.favouriteLineDao().insertAll(favouriteLine)
    }

    override suspend fun removeFavouriteLineByAtcocodeAndLineName(atcocode: String, lineName: String) {
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

}


interface DataRepository {
    suspend fun addFavouriteLine(favouriteLine: FavouriteLine)
    suspend fun removeFavouriteLineByAtcocodeAndLineName(atcocode: String, lineName: String)

    suspend fun addFavouriteStop(favouriteStop: FavouriteStop)
    suspend fun removeFavouriteStopByAtcocode(atcocode: String)
    suspend fun findFavouriteLineByAtcocode(atcocode: String): List<FavouriteStop>


}