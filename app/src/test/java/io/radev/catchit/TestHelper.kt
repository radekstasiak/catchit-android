package io.radev.catchit

import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.LifecycleRegistry
import androidx.lifecycle.liveData
import io.radev.catchit.db.FavouriteLine
import io.radev.catchit.db.FavouriteStop
import io.radev.catchit.network.*
import org.mockito.Mockito

/*
 * Created by radoslaw on 26/06/2020.
 * radev.io 2020.
 */

class TestHelper {
    companion object {
        fun <T> any(): T {
            Mockito.any<T>()
            return uninitialized()
        }

        private fun <T> uninitialized(): T = null as T

        val favouriteStopLiveData = liveData<List<FavouriteStop>> { }
        val favouriteLineLiveData = liveData<List<FavouriteLine>> { }
        val postCodeMember = PostCodeMember(
            type = "postcode",
            name = "LS6 4RQ",
            latitude = 53.82896245787713,
            longitude = -1.570304690443911,
            accuracy = 100
        )

        val postCodeDetailsResponse = PostCodeDetailsResponse(
            requestTime = "2020-06-24T13:50:38+01:00",
            source = "O.S. (CodePoint)",
            acknowledgements = "Ordance Survey Open Data (CodePoint postcodes)",
            memberList = listOf(postCodeMember)
        )


        val placesResponse = PlacesResponse(
            requestTime = "2020-06-26T12:04:35+01:00",
            source = "O.S. (CodePoint), Network Rail, NaPTAN, ",
            acknowledgements = "Ordance Survey Open Data (CodePoint postcodes), Contains information of Network Rail Infrastructure Limited. License http://www.networkrail.co.uk/data-feeds/terms-and-conditions/, Contains DfT NaPTAN bus stops data, , Contains DfT NaPTAN tube station data",
            memberList = listOf(
                PlaceMember(
                    type = "postcode",
                    name = "LS6 4RG",
                    latitude = 53.829011740396034,
                    longitude = -1.5715196360257315,
                    accuracy = 100,
                    distance = 95,
                    description = "Meanwood, Leeds",
                    atcocode = "450011104"
                )
            )
        )

        val departureResponse = DepartureResponse(
            atcocode = "450012351",
            smsCode = "45012351",
            requestTime = "2020-06-26T11:09:25+01:00",
            name = "Green Rd Green View",
            stopName = "Green Rd Green View",
            bearing = "NW",
            indicator = "",
            locality = "Meanwood, Leeds",
            location = StopLocationDetails(
                type = "Point",
                coordinates = listOf(
                    -1.56837,
                    53.8297
                )
            ),
            departures = mapOf(
                Pair(
                    "all", listOf(
                        DepartureDetails(
                            mode = "bus",
                            line = "51",
                            lineName = "51",
                            direction = "Moortown, Leeds",
                            operator = "FLDS",
                            operatorName = "First Leeds",
                            date = "2020-06-26",
                            aimedDepartureTime = "11:17",
                            expectedDepartureTime = null,
                            expectedDepartureDate = null,
                            bestDepartureEstimate = "11:17",
                            dir = "inbound",
                            id = "https://transportapi.com/v3/uk/bus/route/FLDS/51/inbound/450012351/2020-06-26/11:17/timetable.json?app_id=68755067&app_key=1f81945ff77187126de7f9f93c5fab44",
                            source = "tnds timetable (nextbuses disabled)",
                            status = null
                        )
                    )
                )
            )
        )
    }
}

class LifeCycleTestOwner : LifecycleOwner {

    private val registry = LifecycleRegistry(this)

    override fun getLifecycle(): Lifecycle {
        return registry
    }

    fun onCreate() {
        registry.handleLifecycleEvent(Lifecycle.Event.ON_CREATE)
    }

    fun onResume() {
        registry.handleLifecycleEvent(Lifecycle.Event.ON_RESUME)
    }

    fun onDestroy() {
        registry.handleLifecycleEvent(Lifecycle.Event.ON_DESTROY)
    }
}