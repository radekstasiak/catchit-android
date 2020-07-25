package io.radev.catchit

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.Observer
import androidx.lifecycle.SavedStateHandle
import io.mockk.MockKAnnotations
import io.mockk.every
import io.mockk.impl.annotations.RelaxedMockK
import io.mockk.mockkStatic
import io.radev.catchit.data.DataRepository
import io.radev.catchit.domain.*
import io.radev.catchit.network.DepartureResponse
import io.radev.catchit.network.NetworkResponse
import io.radev.catchit.viewmodel.DashboardViewModel
import io.radev.catchit.viewmodel.DepartureDetailsUiModel
import io.radev.catchit.viewmodel.DepartureMapModel
import io.radev.catchit.viewmodel.LatitudeLongitude
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.test.TestCoroutineDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.rules.TestRule
import org.mockito.Mock
import org.mockito.Mockito
import org.mockito.MockitoAnnotations

/*
 * Created by radoslaw on 17/06/2020.
 * radev.io 2020.
 */

class DashboardViewModelTest : TestHelper() {
    private val mainThreadSurrogate = TestCoroutineDispatcher()

    lateinit var viewModel: DashboardViewModel

    private lateinit var lifeCycleTestOwner: LifeCycleTestOwner

    @get:Rule
    var rule: TestRule = InstantTaskExecutorRule()

    @Mock
    lateinit var dataRepository: DataRepository

    @Mock
    lateinit var converter: DateTimeConverterImpl

    @Mock
    lateinit var savedStateHandle: SavedStateHandle

    @Mock
    lateinit var placeMemberModelObserver: Observer<DepartureMapModel>

    @Mock
    lateinit var departureDetailsObserver: Observer<List<DepartureDetailsUiModel>>

    @Mock
    lateinit var postCodeMemberObserver: Observer<LatitudeLongitude>

    @Mock
    lateinit var getDeparturesUseCase: GetDeparturesUseCase

    @Mock
    lateinit var getNearbyStopsForSelectedPostcodeUseCase: GetNearbyStopsForSelectedPostcodeUseCase

    @RelaxedMockK
    lateinit var updateFavouriteDeparturesAlertUseCase: UpdateFavouriteDeparturesAlertUseCase

    @RelaxedMockK
    lateinit var departureDomainModelMock: DepartureDomainModel

    @RelaxedMockK
    lateinit var departureDetailsDomainList: List<DepartureDetailsDomainModel>

    @RelaxedMockK
    lateinit var departureDetailsUiList: List<DepartureDetailsUiModel>

    @Before
    fun setup() {
        MockKAnnotations.init(this)
        //TODO migrate to mockk from mockito
        MockitoAnnotations.initMocks(this)
        Dispatchers.setMain(mainThreadSurrogate)

        val favStopLiveData = TestHelper.favouriteStopLiveData
        val favLineLiveData = TestHelper.favouriteLineLiveData

        Mockito.`when`(dataRepository.getAllFavouriteStops()).thenReturn(favStopLiveData)
        Mockito.`when`(dataRepository.getAllFavouriteLines()).thenReturn(favLineLiveData)

        lifeCycleTestOwner = LifeCycleTestOwner()
        lifeCycleTestOwner.onCreate()

        viewModel = DashboardViewModel(
            dataRepository = dataRepository,
            converter = converter,
            savedStateHandle = savedStateHandle,
            getDeparturesUseCase = getDeparturesUseCase,
            getNearbyStopsForSelectedPostcodeUseCase = getNearbyStopsForSelectedPostcodeUseCase,
            updateFavouriteDeparturesAlertUseCase = updateFavouriteDeparturesAlertUseCase
        )
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain() // reset main dispatcher to the original Main dispatcher
        lifeCycleTestOwner.onDestroy()
        mainThreadSurrogate.cleanupTestCoroutines()
        Dispatchers.resetMain()
    }

    @Test
    fun getLiveTimetableTest_onSuccess() = runBlocking {
        //todo clean mess in this test (mockito and mockk)
        mockkStatic("io.radev.catchit.domain.ModelKt")
        every { departureDomainModelMock.departures } returns departureDetailsDomainList
        every {
            departureDetailsDomainList.toDepartureDetailsUiModel(
                atcocode = "450012351",
                dateTimeConverter = converter
            )
        } returns departureDetailsUiList

        viewModel.departureDetailsModelList.observe(lifeCycleTestOwner, departureDetailsObserver)
        lifeCycleTestOwner.onResume()
        viewModel.atcocode.value = "450012351"

        val result = NetworkResponse.Success<DepartureResponse>(body = getDepartureResponse())

        val departureState = DeparturesState.Success(data = departureDomainModelMock)
        Mockito.`when`(getDeparturesUseCase.getDepartureState(atcocode = "450012351"))
            .thenReturn(departureState)
        Mockito.`when`(dataRepository.getLiveTimetable(atcocode = "450012351"))
            .thenReturn(result)
        viewModel.getLiveTimetable()

        Mockito.verify(departureDetailsObserver).onChanged(Mockito.any())

    }

    @Test
    fun getPostCodeDetailsTest_onSuccess() = runBlocking {
        Mockito.`when`(getNearbyStopsForSelectedPostcodeUseCase.getNearbyStops("LS71HT"))
            .thenReturn(
                PlaceMembersState.Success(
                    longitude = 1.0,
                    latitude = 42.0,
                    data = placesResponse.memberList
                )
            )
        val response = NetworkResponse.Success(TestHelper.postCodeDetailsResponse)
        viewModel._userLatLang.observe(lifeCycleTestOwner, postCodeMemberObserver)
        viewModel.placeMemberModelList.observe(lifeCycleTestOwner, placeMemberModelObserver)
        lifeCycleTestOwner.onResume()
//        Mockito.`when`(dataRepository.getPostCodeDetails(postCode = "LS71HT")).thenReturn(response)
        viewModel.getNearbyStopsWithPostcode(postCode = "LS71HT")
        Mockito.verify(postCodeMemberObserver).onChanged(any())
        Mockito.verify(placeMemberModelObserver).onChanged(any())
    }

    @Test
    fun getNearbyPlacesTest_onSuccess() = runBlocking {
        viewModel.placeMemberModelList.observe(lifeCycleTestOwner, placeMemberModelObserver)
        lifeCycleTestOwner.onResume()
        viewModel._userLatLang.value =
            LatitudeLongitude(
                latitude = 1.0,
                longitude = 53.0
            )
        val response = NetworkResponse.Success(TestHelper.placesResponse)
        Mockito.`when`(
            dataRepository.getNearbyPlaces(
                longitude = viewModel._userLatLang.value!!.longitude,
                latitude = viewModel._userLatLang.value!!.latitude
            )
        ).thenReturn(response)
        viewModel.getNearbyPlaces()
        Mockito.verify(placeMemberModelObserver).onChanged(Mockito.any())
    }

    @Test
    fun updateFavouriteStop_adds_favourite_stop_test() = runBlocking {
        Mockito.`when`(converter.getNowInMillis()).thenReturn(1L)
        viewModel.updateFavouriteStop(favourite = true, atcocode = "450012351")
        Mockito.verify(dataRepository).addFavouriteStop(TestHelper.any())
    }


    @Test
    fun updateFavouriteStop_removes_favourite_stop_test() {
        Mockito.`when`(converter.getNowInMillis()).thenReturn(1L)
        viewModel.updateFavouriteStop(favourite = false, atcocode = "450012351")
        runBlocking {
            launch {
                Mockito.verify(dataRepository).removeFavouriteStopByAtcocode(atcocode = "450012351")
            }
        }
    }

    @Test
    fun updateFavouriteStop_adds_favourite_line_test() = runBlocking {
        Mockito.`when`(converter.getNowInMillis()).thenReturn(1L)
        viewModel.updateFavouriteLine(favourite = true, atcocode = "450012351", lineName = "51")
        Mockito.verify(dataRepository).addFavouriteLine(TestHelper.any())
    }

    @Test
    fun updateFavouriteLine_removes_favourite_stop_test() {
        Mockito.`when`(converter.getNowInMillis()).thenReturn(1L)
        viewModel.updateFavouriteLine(favourite = false, atcocode = "450012351", lineName = "51")
        runBlocking {
            launch {
                Mockito.verify(dataRepository).removeFavouriteLineByAtcocodeAndLineName(
                    atcocode = "450012351",
                    lineName = "51"
                )
            }
        }
    }

}


