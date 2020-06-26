package io.radev.catchit.db

import androidx.room.Database
import androidx.room.RoomDatabase

/*
 * Created by radoslaw on 15/06/2020.
 * radev.io 2020.
 */

@Database(entities = [FavouriteLine::class, FavouriteStop::class], version = 1)
abstract class CatchItDatabase : RoomDatabase() {
    abstract fun favouriteStopDao(): FavouriteStopDao
    abstract fun favouriteLineDao(): FavouriteLineDao
}

object DatabaseConstants {
    const val DATABASE_NAME="catchit_database"

    //tables
    const val FAVOURITE_LINE="favourite_line"
    const val FAVOURITE_STOP = "favourite_stop"

    //columns
    const val ID = "id"
    const val LINE_NAME = "line_name"
    const val ATCOCODE = "atcocode"
    const val CREATED_AT = "created_at"
    const val MODIFIED_AT = "modified_at"


}