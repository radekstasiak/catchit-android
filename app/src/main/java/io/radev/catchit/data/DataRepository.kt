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

    override fun addFavouriteLine(favouriteLine: FavouriteLine) {
        db.favouriteLineDao().insertAll(favouriteLine)
    }

    override fun removeFavouriteLineByAtcocodeAndLineName(atcocode: String, lineName: String) {
        db.favouriteLineDao().deleteByAtcocodeAndLineName(atcocode = atcocode, lineName = lineName)
    }

    override fun addFavouriteStop(favouriteStop: FavouriteStop) {
        db.favouriteStopDao().insertAll(favouriteStop)
    }

    override fun removeFavouriteStopByAtcocode(atcocode: String) {
        db.favouriteStopDao().deleteByAtcocode(atcocode = atcocode)
    }

}


interface DataRepository {
    fun addFavouriteLine(favouriteLine: FavouriteLine)
    fun removeFavouriteLineByAtcocodeAndLineName(atcocode: String, lineName: String)

    fun addFavouriteStop(favouriteStop: FavouriteStop)
    fun removeFavouriteStopByAtcocode(atcocode: String)

}