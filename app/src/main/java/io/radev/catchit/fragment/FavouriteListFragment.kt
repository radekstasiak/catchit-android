package io.radev.catchit.fragment

import android.os.Bundle
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import io.radev.catchit.DashboardViewModel
import io.radev.catchit.FavouriteListRecyclerViewAdapter
import io.radev.catchit.R
import io.radev.catchit.db.FavouriteStop
import kotlinx.android.synthetic.main.fragment_first.recycler_view

/**
 * A fragment representing a list of Items.
 */
class FavouriteListFragment : Fragment() {
    private lateinit var recyclerView: RecyclerView
    private lateinit var favourListAdapter: FavouriteListRecyclerViewAdapter

    private val model: DashboardViewModel by activityViewModels()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_favourite_list, container, false)


    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        recyclerView = recycler_view
        recyclerView.layoutManager = LinearLayoutManager(context, RecyclerView.VERTICAL, false)
        favourListAdapter = FavouriteListRecyclerViewAdapter()
        recyclerView.adapter = favourListAdapter
        model.favouriteStopList.observe(viewLifecycleOwner, Observer<List<FavouriteStop>> {
            favourListAdapter.setData(it)
        })
    }

}