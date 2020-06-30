package io.radev.catchit.domain

import io.radev.catchit.DepartureDetailsModel
import io.radev.catchit.DepartureDetailsUiModel
import io.radev.catchit.network.DepartureDetails
import io.radev.catchit.network.DepartureResponse

/*
 * Created by radoslaw on 30/06/2020.
 * radev.io 2020.
 */

interface GetDeparturesUseCase{

    fun getDepartureState()
}

class GetDeparturesInteractor: GetDeparturesUseCase {
    override fun getDepartureState() {

        //todo test cases when departures empty
        //
        TODO("Not yet implemented")
    }
}


