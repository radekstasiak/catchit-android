package io.radev.catchit

import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.LifecycleRegistry
import androidx.lifecycle.liveData
import com.squareup.moshi.Moshi
import io.radev.catchit.db.FavouriteLine
import io.radev.catchit.db.FavouriteStop
import io.radev.catchit.network.*
import org.mockito.Mockito

/*
 * Created by radoslaw on 26/06/2020.
 * radev.io 2020.
 */

open class TestHelper {
    private val moshi = Moshi.Builder().build();

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

    }

    private val departureResponse = """
    {
        "atcocode": "450013965",
        "smscode": "45013965",
        "request_time": "2020-06-30T12:49:05+01:00",
        "name": "Compton Road Compton Centre",
        "stop_name": "Compton Road Compton Centre",
        "bearing": "SW",
        "indicator": "opp",
        "locality": "Harehills, Leeds",
        "location": {
        "type": "Point",
        "coordinates": [-1.50967, 53.80841]
    },
        "departures": {
        "all": [{
        "mode": "bus",
        "line": "42",
        "line_name": "42",
        "direction": "Farnley, Leeds",
        "operator": "FLDS",
        "operator_name": "First Leeds",
        "date": "2020-06-30",
        "aimed_departure_time": "12:50",
        "expected_departure_date": null,
        "expected_departure_time": null,
        "best_departure_estimate": "12:50",
        "dir": "inbound",
        "id": "https://transportapi.com/v3/uk/bus/route/FLDS/42/inbound/450013965/2020-06-30/12:50/timetable.json?app_id=68755067\u0026app_key=1f81945ff77187126de7f9f93c5fab44",
        "source": "tnds timetable (nextbuses disabled)"
    }, {
        "mode": "bus",
        "line": "4",
        "line_name": "4",
        "direction": "Pudsey",
        "operator": "FLDS",
        "operator_name": "First Leeds",
        "date": "2020-06-30",
        "aimed_departure_time": "12:54",
        "expected_departure_date": null,
        "expected_departure_time": null,
        "best_departure_estimate": "12:54",
        "dir": "outbound",
        "id": "https://transportapi.com/v3/uk/bus/route/FLDS/4/outbound/450013965/2020-06-30/12:54/timetable.json?app_id=68755067\u0026app_key=1f81945ff77187126de7f9f93c5fab44",
        "source": "tnds timetable (nextbuses disabled)"
    }, {
        "mode": "bus",
        "line": "16",
        "line_name": "16",
        "direction": "Pudsey",
        "operator": "FLDS",
        "operator_name": "First Leeds",
        "date": "2020-06-30",
        "aimed_departure_time": "12:57",
        "expected_departure_date": null,
        "expected_departure_time": null,
        "best_departure_estimate": "12:57",
        "dir": "outbound",
        "id": "https://transportapi.com/v3/uk/bus/route/FLDS/16/outbound/450013965/2020-06-30/12:57/timetable.json?app_id=68755067\u0026app_key=1f81945ff77187126de7f9f93c5fab44",
        "source": "tnds timetable (nextbuses disabled)"
    }, {
        "mode": "bus",
        "line": "61",
        "line_name": "61",
        "direction": "St James's Hospital, Leeds",
        "operator": "CTPL",
        "operator_name": "CT Plus (Yorkshire)",
        "date": "2020-06-30",
        "aimed_departure_time": "12:57",
        "expected_departure_date": null,
        "expected_departure_time": null,
        "best_departure_estimate": "12:57",
        "dir": "inbound",
        "id": "https://transportapi.com/v3/uk/bus/route/CTPL/61/inbound/450013965/2020-06-30/12:57/timetable.json?app_id=68755067\u0026app_key=1f81945ff77187126de7f9f93c5fab44",
        "source": "tnds timetable (nextbuses disabled)"
    }, {
        "mode": "bus",
        "line": "42",
        "line_name": "42",
        "direction": "Farnley, Leeds",
        "operator": "FLDS",
        "operator_name": "First Leeds",
        "date": "2020-06-30",
        "aimed_departure_time": "13:02",
        "expected_departure_date": null,
        "expected_departure_time": null,
        "best_departure_estimate": "13:02",
        "dir": "inbound",
        "id": "https://transportapi.com/v3/uk/bus/route/FLDS/42/inbound/450013965/2020-06-30/13:02/timetable.json?app_id=68755067\u0026app_key=1f81945ff77187126de7f9f93c5fab44",
        "source": "tnds timetable (nextbuses disabled)"
    }, {
        "mode": "bus",
        "line": "4",
        "line_name": "4",
        "direction": "Pudsey",
        "operator": "FLDS",
        "operator_name": "First Leeds",
        "date": "2020-06-30",
        "aimed_departure_time": "13:06",
        "expected_departure_date": null,
        "expected_departure_time": null,
        "best_departure_estimate": "13:06",
        "dir": "outbound",
        "id": "https://transportapi.com/v3/uk/bus/route/FLDS/4/outbound/450013965/2020-06-30/13:06/timetable.json?app_id=68755067\u0026app_key=1f81945ff77187126de7f9f93c5fab44",
        "source": "tnds timetable (nextbuses disabled)"
    }, {
        "mode": "bus",
        "line": "16",
        "line_name": "16",
        "direction": "Pudsey",
        "operator": "FLDS",
        "operator_name": "First Leeds",
        "date": "2020-06-30",
        "aimed_departure_time": "13:09",
        "expected_departure_date": null,
        "expected_departure_time": null,
        "best_departure_estimate": "13:09",
        "dir": "outbound",
        "id": "https://transportapi.com/v3/uk/bus/route/FLDS/16/outbound/450013965/2020-06-30/13:09/timetable.json?app_id=68755067\u0026app_key=1f81945ff77187126de7f9f93c5fab44",
        "source": "tnds timetable (nextbuses disabled)"
    }, {
        "mode": "bus",
        "line": "42",
        "line_name": "42",
        "direction": "Farnley, Leeds",
        "operator": "FLDS",
        "operator_name": "First Leeds",
        "date": "2020-06-30",
        "aimed_departure_time": "13:14",
        "expected_departure_date": null,
        "eted_departure_time ": null,
        "best_departure_estimate ": "13: 14 ",
        "dir": "inbound ",
        "id ": "https: //transportapi.com/v3/uk/bus/route/FLDS/42/inbound/450013965/2020-06-30/13:14/timetable.json?app_id=68755067\u0026app_key=1f81945ff77187126de7f9f93c5fab44",
        "source": "tnds timetable (nextbuses disabled)"
    }, {
        "mode": "bus",
        "line": "4",
        "line_name": "4",
        "direction": "Pudsey",
        "operator": "FLDS",
        "operator_name": "First Leeds",
        "date": "2020-06-30",
        "aimed_departure_time": "13:18",
        "expected_departure_date": null,
        "expected_departure_time": null,
        "best_departure_estimate": "13:18",
        "dir": "outbound",
        "id": "https://transportapi.com/v3/uk/bus/route/FLDS/4/outbound/450013965/2020-06-30/13:18/timetable.json?app_id=68755067\u0026app_key=1f81945ff77187126de7f9f93c5fab44",
        "source": "tnds timetable (nextbuses disabled)"
    }, {
        "mode": "bus",
        "line": "16",
        "line_name": "16",
        "direction": "Pudsey",
        "operator": "FLDS",
        "operator_name": "First Leeds",
        "date": "2020-06-30",
        "aimed_departure_time": "13:21",
        "expected_departure_date": null,
        "expected_departure_time": null,
        "best_departure_estimate": "13:21",
        "dir": "outbound",
        "id": "https://transportapi.com/v3/uk/bus/route/FLDS/16/outbound/450013965/2020-06-30/13:21/timetable.json?app_id=68755067\u0026app_key=1f81945ff77187126de7f9f93c5fab44",
        "source": "tnds timetable (nextbuses disabled)"
    }]
    }
    }
    """

    fun getDepartureResponse(): DepartureResponse =
        moshi.adapter<DepartureResponse>(DepartureResponse::class.java)
            .fromJson(departureResponse)!!
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