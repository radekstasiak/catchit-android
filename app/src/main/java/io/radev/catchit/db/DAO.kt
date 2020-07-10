package io.radev.catchit.db

import androidx.lifecycle.LiveData

import androidx.room.*

/*
 * Created by radoslaw on 15/06/2020.
 * radev.io 2020.
 */
//TODO read https://medium.com/swlh/kotlin-coroutines-review-53e951c4a0fa
//TODO watch https://www.youtube.com/watch?v=lh2Vqt4DpHU or https://skillsmatter.com/skillscasts/12727-coroutines-by-example

@Dao
interface FavouriteStopDao {
    @Query("SELECT * FROM ${DatabaseConstants.FAVOURITE_STOP}")
    fun getAll(): LiveData<List<FavouriteStop>>

    //https://stackoverflow.com/questions/44167111/android-room-simple-select-query-cannot-access-database-on-the-main-thread
    //Asynchronous queries (queries that return LiveData or RxJava Flowable) are exempt from this rule since they asynchronously run the query on a background thread when needed
    @Query(
        """
        SELECT * FROM  ${DatabaseConstants.FAVOURITE_STOP} 
        WHERE ${DatabaseConstants.ATCOCODE}=:atcocode
        LIMIT 1
    """
    )
    //room makes sure that suspend function executes off the main thread
    //https://medium.com/androiddevelopers/room-coroutines-422b786dc4c5
    suspend fun findByAtcocode(atcocode: String): List<FavouriteStop>


    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertAll(vararg favouriteStop: FavouriteStop)

    @Delete
    suspend fun delete(favouriteStop: FavouriteStop)

    @Query(
        """
        DELETE FROM ${DatabaseConstants.FAVOURITE_STOP}
        WHERE ${DatabaseConstants.ATCOCODE} = :atcocode
    """
    )

    suspend fun deleteByAtcocode(atcocode: String)

}

@Dao
interface FavouriteLineDao {
    @Query("SELECT * FROM ${DatabaseConstants.FAVOURITE_LINE}")
    fun getAll(): LiveData<List<FavouriteLine>>


    @Query("SELECT * FROM ${DatabaseConstants.FAVOURITE_LINE}")
    suspend fun getAllSync(): List<FavouriteLine>

    @Query(
        """
        SELECT * FROM  ${DatabaseConstants.FAVOURITE_LINE} 
        WHERE ${DatabaseConstants.ATCOCODE}=:atcocode
        AND ${DatabaseConstants.LINE_NAME}=:lineName
        LIMIT 1
    """
    )
    suspend fun findByAtcocodeAndLine(atcocode: String, lineName: String): FavouriteLine

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertAll(vararg favouriteLines: FavouriteLine)

    @Delete
    suspend fun delete(favouriteLine: FavouriteLine)

    @Query(
        """
        DELETE FROM ${DatabaseConstants.FAVOURITE_LINE}
        WHERE ${DatabaseConstants.ATCOCODE}=:atcocode
        AND ${DatabaseConstants.LINE_NAME} = :lineName
    """
    )
    suspend fun deleteByAtcocodeAndLineName(atcocode: String, lineName: String)

    @Query(
        """
        SELECT * FROM  ${DatabaseConstants.FAVOURITE_LINE} 
        WHERE ${DatabaseConstants.ATCOCODE}=:atcocode
    """
    )
    suspend fun getByAtcocode(atcocode: String): List<FavouriteLine>


}