package io.radev.catchit.fragment

import android.util.Log
import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.*
import io.radev.catchit.domain.FavouriteDepartureUpdateState
import io.radev.catchit.domain.UpdateFavouriteDeparturesAlertUseCase
import io.radev.catchit.domain.toUiModel
import io.radev.catchit.viewmodel.FavouriteDepartureAlert
import kotlinx.coroutines.launch
import javax.inject.Inject

const val TAG = "mviTest"

class FavouriteListViewModel @ViewModelInject constructor(private val dispatcher: FavouriteStopActionProcessor) :
    ViewModel() {


    private var viewState = FavouriteDepartureViewState.init()

    val favouriteStopState: LiveData<FavouriteDepartureViewState> =
        Transformations.map(dispatcher.result) { result ->
            Log.d(TAG, "new action ${result.toString()}")
            viewState = when (result) {
                FavouriteStopListResult.LoadFavouritesInProgress -> {
                    Log.d(TAG, result.toString())
                    viewState.copy(isLoading = true)
                }
                is FavouriteStopListResult.LoadFavouritesSuccess -> {
                    Log.d(TAG, result.toString())
                    viewState.copy(
                        isLoading = false,
                        list = result.list
                    )
                }
                null -> viewState.copy()
            }
            viewState
        }


    fun processIntents(intent: FavouriteStopListIntent) {
        viewModelScope.launch {
            when (intent) {
                is FavouriteStopListIntent.LoadFavourites -> {
                    dispatcher.dispatchAction(FavouriteStopListAction.LoadFavourites)
                }
            }
        }
    }

}

class FavouriteStopActionProcessor @Inject constructor(private val updateFavouriteDeparturesAlertUseCase: UpdateFavouriteDeparturesAlertUseCase) {

    val result = MutableLiveData<FavouriteStopListResult>()
    suspend fun dispatchAction(action: FavouriteStopListAction) =
        when (action) {
            FavouriteStopListAction.LoadFavourites -> {
                Log.d(TAG, "emitting in Progress")
                result.value = FavouriteStopListResult.LoadFavouritesInProgress
                Log.d(TAG, "emitting Result")
                result.value = getFavouriteStopDepartures()
            }
        }

    private suspend fun getFavouriteStopDepartures(): FavouriteStopListResult {
        val result = updateFavouriteDeparturesAlertUseCase.getFavouriteDeparturesUpdate()
        val alertList = arrayListOf<FavouriteDepartureAlert>()
        for (item in result) {
            when (item) {
                is FavouriteDepartureUpdateState.Success -> {
                    for (departureAlert in item.list) {
                        alertList.add(departureAlert.toUiModel())
                    }
                }
            }
        }
        Log.d(TAG, alertList.size.toString())
        return FavouriteStopListResult.LoadFavouritesSuccess(list = alertList)
    }


}

interface Intent {}
interface Action {}
interface Result {}

sealed class FavouriteStopListIntent : Intent {
    object LoadFavourites : FavouriteStopListIntent()
}

sealed class FavouriteStopListAction : Action {
    object LoadFavourites : FavouriteStopListAction()
}

sealed class FavouriteStopListResult : Result {
    object LoadFavouritesInProgress : FavouriteStopListResult()
    data class LoadFavouritesSuccess(val list: List<FavouriteDepartureAlert>) :
        FavouriteStopListResult()
}

data class FavouriteDepartureViewState(
    val isLoading: Boolean,
    val list: List<FavouriteDepartureAlert>
) {
    companion object {
        fun init() = FavouriteDepartureViewState(
            list = listOf(),
            isLoading = true
        )
    }
}