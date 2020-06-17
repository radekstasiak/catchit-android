package io.radev.catchit

import androidx.lifecycle.SavedStateHandle
import io.radev.catchit.data.DataRepository
import io.radev.catchit.network.ApiService
import org.junit.Before
import org.junit.Test
import org.mockito.Mock
import org.mockito.Mockito
import org.mockito.MockitoAnnotations

/*
 * Created by radoslaw on 17/06/2020.
 * radev.io 2020.
 */

class DashboardViewModelTest() {

    lateinit var viewModel: DashboardViewModel

    @Mock
    lateinit var apiService: ApiService

    @Mock
    lateinit var dataRepository: DataRepository

    @Mock
    lateinit var converter: DateTimeConverter

    @Mock
    lateinit var savedStateHandle: SavedStateHandle

    @Before
    fun setup() {
        MockitoAnnotations.initMocks(this)
        viewModel = DashboardViewModel(
            apiService = apiService,
            dataRepository = dataRepository,
            converter = converter,
            savedStateHandle = savedStateHandle
        )
    }

    @Test
    fun updateFavouriteStop_adds_favourite_stop_test() {
        Mockito.`when`(converter.getNowInMillis()).thenReturn(1L)
        viewModel.updateFavouriteStop(favourite = true, atcocode = "450012351")
        Mockito.verify(dataRepository).addFavouriteStop(TestUtils.any())
    }

    @Test
    fun updateFavouriteStop_removes_favourite_stop_test(){
        Mockito.`when`(converter.getNowInMillis()).thenReturn(1L)
        viewModel.updateFavouriteStop(favourite = false, atcocode = "450012351")
        Mockito.verify(dataRepository).removeFavouriteStopByAtcocode(atcocode = "450012351")
    }

}

class TestUtils {
    companion object {
        fun <T> any(): T {
            Mockito.any<T>()
            return uninitialized()
        }

        private fun <T> uninitialized(): T = null as T
    }
}