package io.radev.catchit.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import dagger.hilt.android.AndroidEntryPoint
import io.radev.catchit.*
import kotlinx.android.synthetic.main.fragment_second.*


@AndroidEntryPoint
class ConnectionsListFragment : Fragment(), SelectDepartureListener {
    val TAG = "connectionsListFragmentTag"
    lateinit var itemAdapter: DepartureListAdapter
    lateinit var recyclerView: RecyclerView

    private val model: DashboardViewModel by activityViewModels()
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_second, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        view.findViewById<Button>(R.id.button_second).setOnClickListener {
            findNavController().navigate(R.id.action_ConnectionsListFragment_to_NearbyPlacesFragment)
        }

        recyclerView = recycler_view
        itemAdapter = DepartureListAdapter(this, requireActivity())
        recyclerView.layoutManager = LinearLayoutManager(context, RecyclerView.VERTICAL, false)
        recyclerView.adapter = itemAdapter
        swiperefresh.setOnRefreshListener {
            if (swiperefresh != null) swiperefresh.isRefreshing = true
            model.getLiveTimetable()
        }
        if (swiperefresh != null) swiperefresh.isRefreshing = true
        model.getLiveTimetable()

        model.departureDetailsModelList.observe(
            viewLifecycleOwner,
            Observer<List<DepartureDetailsUiModel>> {
                if (swiperefresh != null) swiperefresh.isRefreshing = false
                itemAdapter.setData(it)
            })

        model.stopHeaderText.observe(viewLifecycleOwner, Observer<String> {
            tv_header.text = it
        })
    }

    override fun updateFavouriteStop(atcocode: String, lineName: String, favourite: Boolean) {
        model.updateFavouriteLine(atcocode = atcocode, lineName = lineName, favourite = favourite)
    }
}



