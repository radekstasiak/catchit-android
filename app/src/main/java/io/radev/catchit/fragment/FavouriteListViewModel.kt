package io.radev.catchit.fragment

import android.util.Log
import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.*
import io.radev.catchit.data.DataRepository
import io.radev.catchit.domain.FavouriteDepartureUpdateState
import io.radev.catchit.domain.UpdateFavouriteDeparturesAlertUseCase
import io.radev.catchit.domain.toUiModel
import io.radev.catchit.viewmodel.FavouriteDepartureAlert
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.consumeAsFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

const val TAG = "mviTest"

class FavouriteListViewModel @ViewModelInject constructor(
    private val dispatcher: FavouriteStopActionProcessor,
    private val useCase: UpdateFavouriteDeparturesAlertUseCase
) :
    ViewModel(), Model<FavouriteDepartureViewState, FavouriteStopListIntent> {

    override val intents: Channel<FavouriteStopListIntent>
        get() = Channel(Channel.UNLIMITED)

    private val _state = MutableLiveData<FavouriteDepartureViewState>().apply {
        value = FavouriteDepartureViewState.init()
    }
    override val state: LiveData<FavouriteDepartureViewState>
        get() = _state


    init {
        handlerIntent()
    }


    private fun handlerIntent() {
        viewModelScope.launch {
            intents.consumeAsFlow().collect { userIntent ->
                when (userIntent) {
                    FavouriteStopListIntent.LoadFavourites -> loadFavourites()
                    FavouriteStopListIntent.RefreshFavourites -> loadFavourites()
                }
            }
        }
    }

    private fun loadFavourites() {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                updateState { it.copy(isLoading = true) }
                updateState {
                    it.copy(
                        isLoading = false,
                        list = getFavouriteList()
                    )
                }
            } catch (e: Exception) {
                updateState { it.copy(isLoading = false, errorMessage = e.message ?: "") }
            }
        }
    }

    private suspend fun getFavouriteList(): List<FavouriteDepartureAlert> {
        val result = useCase.getFavouriteDeparturesUpdate()
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
        return alertList
    }

    private suspend fun updateState(handler: suspend (intent: FavouriteDepartureViewState) -> FavouriteDepartureViewState) {
        _state.postValue(handler(state.value!!))
    }


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
                is FavouriteStopListResult.RemoveFavouriteLine -> viewState.copy(list = result.list)
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
                is FavouriteStopListIntent.RemoveFavouriteLine -> {
                    dispatcher.dispatchAction(
                        FavouriteStopListAction.RemoveFavouriteLine(
                            atcocode = intent.atcocode,
                            lineName = intent.lineName,
                            list = viewState.list
                        )
                    )
                }
            }
        }
    }


}

class FavouriteStopActionProcessor @Inject constructor(
    private val updateFavouriteDeparturesAlertUseCase: UpdateFavouriteDeparturesAlertUseCase,
    private val dataRepository: DataRepository
) {

    val result = MutableLiveData<FavouriteStopListResult>()

    @Suppress("IMPLICIT_CAST_TO_ANY")
    suspend fun dispatchAction(action: FavouriteStopListAction) =
        when (action) {
            FavouriteStopListAction.LoadFavourites -> {
                Log.d(TAG, "emitting in Progress")
                result.value = FavouriteStopListResult.LoadFavouritesInProgress
                Log.d(TAG, "emitting Result")
                result.value = getFavouriteStopDepartures()
            }
            is FavouriteStopListAction.RemoveFavouriteLine -> result.value = removeFavouriteLine(
                atcocode = action.atcocode,
                lineName = action.lineName,
                alertList = action.list
            )
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

    private suspend fun removeFavouriteLine(
        alertList: List<FavouriteDepartureAlert>,
        atcocode: String,
        lineName: String
    ): FavouriteStopListResult {
        dataRepository.removeFavouriteLineByAtcocodeAndLineName(
            atcocode = atcocode,
            lineName = lineName
        )
        val result =
            (alertList as ArrayList).filterNot { it.atcocode == atcocode && it.lineName == lineName }
        return FavouriteStopListResult.RemoveFavouriteLine(list = result)
    }


}

interface Intent
interface Action
interface Result
interface ViewState
interface IView<S : ViewState> {
    fun render(state: S)
}

interface Model<S : ViewState, I : Intent> {
    val intents: Channel<I>
    val state: LiveData<S>
}

sealed class FavouriteStopListIntent : Intent {
    object LoadFavourites : FavouriteStopListIntent()
    object RefreshFavourites : FavouriteStopListIntent()
    data class RemoveFavouriteLine(
        val atcocode: String,
        val lineName: String
    ) : FavouriteStopListIntent()
}

sealed class FavouriteStopListAction : Action {
    object LoadFavourites : FavouriteStopListAction()
    data class RemoveFavouriteLine(
        val list: List<FavouriteDepartureAlert>,
        val atcocode: String,
        val lineName: String
    ) : FavouriteStopListAction()
}

sealed class FavouriteStopListResult : Result {
    object LoadFavouritesInProgress : FavouriteStopListResult()
    data class LoadFavouritesSuccess(val list: List<FavouriteDepartureAlert>) :
        FavouriteStopListResult()

    data class RemoveFavouriteLine(val list: List<FavouriteDepartureAlert>) :
        FavouriteStopListResult()
}

data class FavouriteDepartureViewState(
    val isLoading: Boolean,
    val errorMessage: String,
    val list: List<FavouriteDepartureAlert>
) : ViewState {
    companion object {
        fun init() = FavouriteDepartureViewState(
            list = listOf(),
            errorMessage = "",
            isLoading = true
        )
    }
}