package io.radev.catchit.db

import androidx.room.*

/*
 * Created by radoslaw on 15/06/2020.
 * radev.io 2020.
 */

@Dao
interface FavouriteStopDao {
    @Query("SELECT * FROM ${DatabaseConstants.FAVOURITE_STOP}")
    suspend fun getAll(): List<FavouriteStop>

    @Query(
        """
        SELECT * FROM  ${DatabaseConstants.FAVOURITE_STOP} 
        WHERE ${DatabaseConstants.ATCOCODE}=:atcocode
        LIMIT 1
    """
    )
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
    fun getAll(): List<FavouriteLine>

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


}