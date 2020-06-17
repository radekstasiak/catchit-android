package io.radev.catchit.db

import androidx.room.*

/*
 * Created by radoslaw on 15/06/2020.
 * radev.io 2020.
 */

@Dao
interface FavouriteStopDao {
    @Query("SELECT * FROM ${DatabaseConstants.FAVOURITE_STOP}")
    fun getAll(): List<FavouriteStop>

    @Query(
        """
        SELECT * FROM  ${DatabaseConstants.FAVOURITE_STOP} 
        WHERE ${DatabaseConstants.ATCOCODE}=:atcocode
        LIMIT 1
    """
    )
    fun findByAtcocode(atcocode: String): FavouriteStop

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    fun insertAll(vararg favouriteStop: FavouriteStop)

    @Delete
    fun delete(favouriteStop: FavouriteStop)

    @Query(
        """
        DELETE FROM ${DatabaseConstants.FAVOURITE_STOP}
        WHERE ${DatabaseConstants.ATCOCODE} = :atcocode
    """
    )

    fun deleteByAtcocode(atcocode: String)
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
    fun findByAtcocodeAndLine(atcocode: String, lineName: String): FavouriteLine

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    fun insertAll(vararg favouriteLines: FavouriteLine)

    @Delete
    fun delete(favouriteLine: FavouriteLine)

    @Query(
        """
        DELETE FROM ${DatabaseConstants.FAVOURITE_LINE}
        WHERE ${DatabaseConstants.ATCOCODE}=:atcocode
        AND ${DatabaseConstants.LINE_NAME} = :lineName
    """
    )
    fun deleteByAtcocodeAndLineName(atcocode: String, lineName: String)


}