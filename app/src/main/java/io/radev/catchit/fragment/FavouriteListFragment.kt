package io.radev.catchit.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import io.radev.catchit.viewmodel.DashboardViewModel
import io.radev.catchit.R
import io.radev.catchit.db.FavouriteStop
import kotlinx.android.synthetic.main.fragment_favourite_list.*


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

class FavouriteListRecyclerViewAdapter : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    var data = arrayListOf<FavouriteStop>()
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder =
        FavouriteItemViewHolder(
            LayoutInflater.from(parent.context).inflate(
                R.layout.favourite_item,
                parent,
                false
            )
        )

    fun setData(list: List<FavouriteStop>) {
        data = ArrayList(list)
        notifyDataSetChanged()
    }

    override fun getItemCount(): Int = data.size


    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val vh = holder as FavouriteItemViewHolder
        val item = data[position]
        vh.atcocode.text = item.atcocode
        vh.timestamp.text = item.createdAt.toString()
    }
}

class FavouriteItemViewHolder : RecyclerView.ViewHolder {

    val atcocode: TextView
    val timestamp: TextView

    constructor(view: View) : super(view) {

        atcocode = view.findViewById(R.id.tv_atcocode)
        timestamp = view.findViewById(R.id.tv_timestamp)
    }
}
