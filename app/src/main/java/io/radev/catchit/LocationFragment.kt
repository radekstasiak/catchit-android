package io.radev.catchit

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.view.inputmethod.EditorInfo
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import kotlinx.android.synthetic.main.fragment_location.*
import org.jetbrains.anko.doAsync
import org.jetbrains.anko.uiThread


class LocationFragment : Fragment() {
    private val model: DashboardViewModel by activityViewModels()
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

    private fun getPostCodeDetails(postCode: String) {
        val request = CatchItApp.apiService.getPostCodeDetails(query = postCode)
        doAsync {
            val response = request.execute()
            uiThread {
                model.selectPostCodeMember(response.body()!!.memberList[0])
                navigateToNearbyPlaces()
            }

        }
    }


    private fun navigateToNearbyPlaces() {
        val action = LocationFragmentDirections.actionLocationFragmentToNearbyPlacesFragment()
        findNavController().navigate(action)
    }


}
