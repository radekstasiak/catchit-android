package io.radev.catchit

import io.radev.catchit.db.FavouriteLine
import io.radev.catchit.db.toUniqueAtcocodeList
import io.radev.catchit.viewmodel.PlaceMemberModel
import org.junit.Assert
import org.junit.Test

/*
 * Created by radoslaw on 29/06/2020.
 * radev.io 2020.
 */

class ExtensionsTest {


    @Test
    fun list_of_PlaceMemberModel_toDeparturesMap_correct_latLngBounds_test() {

    }

    private fun getPlaceMemberModelList(): List<PlaceMemberModel> = listOf(
        getPlaceMemberModel(),
        getPlaceMemberModel().copy(
            latitude = 53.8297,
            longitude = -1.56837
        ),
        getPlaceMemberModel().copy(
            latitude = 53.82851,
            longitude = -1.56703
        ),
        getPlaceMemberModel().copy(
            latitude = 53.82851,
            longitude = -1.56703
        ),
        getPlaceMemberModel().copy(
            latitude = 53.82869,
            longitude = -1.56696
        ),
        getPlaceMemberModel().copy(
            latitude = 53.8287,
            longitude = -1.56589
        ),
        getPlaceMemberModel().copy(
            latitude = 53.8288,
            longitude = -1.56554
        ),
        getPlaceMemberModel().copy(
            latitude = 53.82696,
            longitude = -1.56641
        ),
        getPlaceMemberModel().copy(
            latitude = 53.82614,
            longitude = -1.56515
        ),
        getPlaceMemberModel().copy(
            latitude = 53.83266,
            longitude = -1.56694
        ),
        getPlaceMemberModel().copy(
            latitude = 53.83277,
            longitude = -1.56694
        )
    )

    private fun getPlaceMemberModel(): PlaceMemberModel =
        PlaceMemberModel(
            name = "Green Rd Green View - SE-bound",
            atcocode = "450010441",
            description = "Meanwood, Leeds",
            distance = "142.0",
            isFavourite = true,
            latitude = 53.82968,
            longitude = -1.56852
        )

    @Test
    fun `test favouriteLineList returns list of unique atcocodes`() {
        val entityList = listOf(
            FavouriteLine(createdAt = 1L, modifiedAt = 1L, lineName = "51", atcocode = "450010441"),
            FavouriteLine(createdAt = 1L, modifiedAt = 1L, lineName = "2", atcocode = "450010361"),
            FavouriteLine(createdAt = 1L, modifiedAt = 1L, lineName = "16", atcocode = "450010441"),
            FavouriteLine(createdAt = 1L, modifiedAt = 1L, lineName = "43", atcocode = "450010467")
        )

        val result = entityList.toUniqueAtcocodeList()
        Assert.assertEquals(3, result.size)
    }

}