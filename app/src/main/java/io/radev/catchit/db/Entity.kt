package io.radev.catchit.db

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

/*
 * Created by radoslaw on 15/06/2020.
 * radev.io 2020.
 */

@Entity(tableName = DatabaseConstants.FAVOURITE_LINE)
data class FavouriteLine(
    @PrimaryKey @ColumnInfo(name = DatabaseConstants.ID) val id: Long,
    @ColumnInfo(name = DatabaseConstants.CREATED_AT) val createdAt: Long,
    @ColumnInfo(name = DatabaseConstants.MODIFIED_AT) val modifiedAt: Long,
    @ColumnInfo(name = DatabaseConstants.LINE_NAME) val lineName: String,
    @ColumnInfo(name = DatabaseConstants.ATCOCODE) val atcocode: String
)

@Entity(
    tableName = DatabaseConstants.FAVOURITE_STOP,
    indices = [
        Index(value = [DatabaseConstants.ATCOCODE], unique = true)
    ]
)
data class FavouriteStop(
    @PrimaryKey @ColumnInfo(name = DatabaseConstants.ID) val id: Long,
    @ColumnInfo(name = DatabaseConstants.CREATED_AT) val createdAt: Long,
    @ColumnInfo(name = DatabaseConstants.MODIFIED_AT) val modifiedAt: Long,
    @ColumnInfo(name = DatabaseConstants.ATCOCODE) val atcocode: String
)

