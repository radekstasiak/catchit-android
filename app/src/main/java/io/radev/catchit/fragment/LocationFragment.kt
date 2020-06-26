package io.radev.catchit.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.view.inputmethod.EditorInfo
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import dagger.hilt.android.AndroidEntryPoint
import io.radev.catchit.DashboardViewModel
import io.radev.catchit.R
import io.radev.catchit.network.ApiService
import io.radev.catchit.network.PostCodeMember
import kotlinx.android.synthetic.main.fragment_location.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class LocationFragment : Fragment() {

    @Inject
    lateinit var apiService: ApiService
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
                model.getPostCodeDetails(postCode = textView.text.toString())
                true
            }
            false
        }

        model.postCodeMember.observe(viewLifecycleOwner, Observer<PostCodeMember> {
            navigateToNearbyPlaces()
        })

    }

    private fun navigateToNearbyPlaces() {
        val action =
            LocationFragmentDirections.actionLocationFragmentToNearbyPlacesFragment()
        findNavController().navigate(action)
    }


}
