package io.radev.catchit.domain

import io.mockk.MockKAnnotations
import io.mockk.coEvery
import io.mockk.every
import io.mockk.impl.annotations.RelaxedMockK
import io.mockk.mockk
import io.radev.catchit.data.DataRepository
import io.radev.catchit.network.ErrorResponse
import io.radev.catchit.network.NetworkResponse
import io.radev.catchit.network.PlacesResponse
import io.radev.catchit.network.PostCodeDetailsResponse
import kotlinx.coroutines.runBlocking
import org.junit.Assert

import org.junit.Before
import org.junit.Test

/*
 * Created by radoslaw on 03/07/2020.
 * radev.io 2020.
 */

class GetNearbyStopsForSelectedPostcodeInteractorTest {

    lateinit var interactor: GetNearbyStopsForSelectedPostcodeInteractor

    @RelaxedMockK
    lateinit var dataRepository: DataRepository

    @Before
    fun setup() {
        MockKAnnotations.init(this)
        interactor = GetNearbyStopsForSelectedPostcodeInteractor(dataRepository)
    }

    @Test
    fun `when interactor called it returns unknown error when getNearbyPlaces fails`() =
        runBlocking {
            coEvery { dataRepository.getPostCodeDetails("LS7 1HT") } returns
                    mockk<NetworkResponse.Success<PostCodeDetailsResponse>> {
                        every { body } returns
                                mockk {
                                    every { memberList } returns listOf(
                                        mockk {
                                            every { latitude } returns -2.0
                                            every { longitude } returns 53.0
                                        }
                                    )
                                }
                    }

            coEvery { dataRepository.getNearbyPlaces(longitude = 53.0, latitude = -2.0) } returns
                    mockk<NetworkResponse.UnknownError> {
                        every { error } returns Throwable("unknown")
                    }

            val result = interactor.getNearbyStops("LS7 1HT")
            Assert.assertTrue(result is PlaceMembersState.UnknownError)
        }

    @Test
    fun `when interactor called it returns network error when getNearbyPlaces fails`() =
        runBlocking {
            coEvery { dataRepository.getPostCodeDetails("LS7 1HT") } returns
                    mockk<NetworkResponse.Success<PostCodeDetailsResponse>> {
                        every { body } returns
                                mockk {
                                    every { memberList } returns listOf(
                                        mockk {
                                            every { latitude } returns -2.0
                                            every { longitude } returns 53.0
                                        }
                                    )
                                }
                    }

            coEvery { dataRepository.getNearbyPlaces(longitude = 53.0, latitude = -2.0) } returns
                    mockk<NetworkResponse.NetworkError> {
                    }

            val result = interactor.getNearbyStops("LS7 1HT")
            Assert.assertTrue(result is PlaceMembersState.NetworkError)
        }

    @Test
    fun `when interactor called it returns api error when getNearbyPlaces fails`() = runBlocking {
        coEvery { dataRepository.getPostCodeDetails("LS7 1HT") } returns
                mockk<NetworkResponse.Success<PostCodeDetailsResponse>> {
                    every { body } returns
                            mockk {
                                every { memberList } returns listOf(
                                    mockk {
                                        every { latitude } returns -2.0
                                        every { longitude } returns 53.0
                                    }
                                )
                            }
                }

        coEvery { dataRepository.getNearbyPlaces(longitude = 53.0, latitude = -2.0) } returns
                mockk<NetworkResponse.ApiError<ErrorResponse>> {
                    every { code } returns 500

                }

        val result = interactor.getNearbyStops("LS7 1HT")
        Assert.assertTrue(result is PlaceMembersState.ApiError)
        Assert.assertEquals(500, (result as PlaceMembersState.ApiError).code)
    }

    @Test
    fun `when interactor called it returns successfully with place members list`() = runBlocking {
        coEvery { dataRepository.getPostCodeDetails("LS7 1HT") } returns
                mockk<NetworkResponse.Success<PostCodeDetailsResponse>> {
                    every { body } returns
                            mockk {
                                every { memberList } returns listOf(
                                    mockk {
                                        every { latitude } returns -2.0
                                        every { longitude } returns 53.0
                                    }
                                )
                            }
                }

        coEvery { dataRepository.getNearbyPlaces(longitude = 53.0, latitude = -2.0) } returns
                mockk<NetworkResponse.Success<PlacesResponse>> {
                    every { body } returns
                            mockk {
                                every { memberList } returns listOf(
                                    mockk {

                                    },
                                    mockk {

                                    },
                                    mockk {

                                    },
                                    mockk {

                                    })
                            }
                }


        val result = interactor.getNearbyStops("LS7 1HT")

        Assert.assertTrue(result is PlaceMembersState.Success)
        Assert.assertEquals(-2.0, (result as PlaceMembersState.Success).latitude, 0.0)
        Assert.assertEquals(53.0, (result as PlaceMembersState.Success).longitude, 0.0)
        Assert.assertEquals(4, (result as PlaceMembersState.Success).data.size)
    }


    @Test
    fun `when interactor called it returns successfully with post code not found`() = runBlocking {
        coEvery { dataRepository.getPostCodeDetails("LS7 1HT") } returns
                mockk<NetworkResponse.Success<PostCodeDetailsResponse>> {
                    every { body } returns
                            mockk {
                                every { memberList } returns listOf(
                                )
                            }
                }


        val result = interactor.getNearbyStops("LS7 1HT")

        Assert.assertTrue(result is PlaceMembersState.PostCodeNotFound)
    }

    @Test
    fun `when interactor called postCodeDetails returns with API error`() = runBlocking {
        coEvery { dataRepository.getPostCodeDetails("LS7 1HT") } returns
                mockk<NetworkResponse.ApiError<ErrorResponse>> {
                    every { code } returns 500
                }

        val result = interactor.getNearbyStops("LS7 1HT")
        Assert.assertTrue(result is PlaceMembersState.ApiError)
        Assert.assertEquals(500, (result as PlaceMembersState.ApiError).code)
    }

    @Test
    fun `when interactor called postCodeDetails returns with Network error`() = runBlocking {
        coEvery { dataRepository.getPostCodeDetails("LS7 1HT") } returns
                mockk<NetworkResponse.NetworkError> {
                }

        val result = interactor.getNearbyStops("LS7 1HT")
        Assert.assertTrue(result is PlaceMembersState.NetworkError)
    }

    @Test
    fun `when interactor called postCodeDetails returns with Unknown error`() = runBlocking {
        coEvery { dataRepository.getPostCodeDetails("LS7 1HT") } returns
                mockk<NetworkResponse.UnknownError> {
                    every { error } returns Throwable("unknown")
                }

        val result = interactor.getNearbyStops("LS7 1HT")
        Assert.assertTrue(result is PlaceMembersState.UnknownError)
    }


}