package io.radev.catchit.domain

import io.radev.catchit.TestHelper
import org.junit.Assert
import org.junit.Before
import org.junit.Test


/*
 * Created by radoslaw on 30/06/2020.
 * radev.io 2020.
 */

class GetDeparturesInteractorTest : TestHelper() {

    lateinit var interactor: GetDeparturesInteractor

    @Before
    fun before() {
        interactor = GetDeparturesInteractor()
    }

    @Test
    fun getDeparturesState_success_test() {

        interactor.getDepartureState()
    }
}

class UniqueDepartureListMapperTest : TestHelper() {
    lateinit var mapper: UniqueDepartureListMapperImpl

    @Before
    fun setup() {
        mapper = UniqueDepartureListMapperImpl()
    }

    @Test
    fun mapDepartureResponse_to_uniqueDepartureList_returns_unique_items_test() {
        val departureDetailsList = getDepartureResponse().departures!!["all"]
        val result = mapper.mapDepartureResponseToDepartureList(departureDetailsList!!)

        Assert.assertTrue(result.size == 4)

    }

}