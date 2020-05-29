package io.radev.catchit

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.view.inputmethod.EditorInfo
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import kotlinx.android.synthetic.main.fragment_location.*
import org.jetbrains.anko.doAsync
import org.jetbrains.anko.uiThread


class LocationFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_location, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        requireActivity().window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);
        et_post_code.setOnEditorActionListener { textView, actionId, keyEvent ->
            if (actionId == EditorInfo.IME_ACTION_SEARCH) {
//                navigateToNearbyPlaces(postCode = textView.text.toString())
                getPostCodeDetails(postCode = textView.text.toString())
                true
            }
            false
        }

    }

    fun getPostCodeDetails(postCode: String) {
        val request = CatchItApp.apiService.getPostCodeDetails(query = postCode)
        doAsync {
            val response = request.execute()
            uiThread {
                navigateToNearbyPlaces(
                    postCode = response.body()!!.memberList[0].name,
                    longitude = response.body()!!.memberList[0].longitude,
                    latitude = response.body()!!.memberList[0].latitude
                )
            }

        }
    }


    private fun navigateToNearbyPlaces(postCode: String, longitude: Double, latitude: Double) {
        val action = LocationFragmentDirections.actionLocationFragmentToNearbyPlacesFragment(
            longitude = longitude.toString(),
            latitude = latitude.toString(),
            postCode = postCode
        )
        findNavController().navigate(action)
    }


}
