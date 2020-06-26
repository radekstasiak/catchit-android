package io.radev.catchit.fragment

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import dagger.hilt.android.AndroidEntryPoint
import io.radev.catchit.DashboardViewModel
import io.radev.catchit.DepartureDetailsModel
import io.radev.catchit.R
import kotlinx.android.synthetic.main.fragment_second.*


@AndroidEntryPoint
class ConnectionsListFragment : Fragment(), SelectDepartureListener {
    val TAG = "connectionsListFragmentTag"
    lateinit var itemAdapter: ConnectionListAdapter
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
        itemAdapter = ConnectionListAdapter(this, requireActivity())
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
            Observer<List<DepartureDetailsModel>> {
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

class ConnectionListAdapter(
    val listener: SelectDepartureListener,
    private val context: Context
) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    var data = arrayListOf<DepartureDetailsModel>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder =
        ConnectionListViewHolder(
            LayoutInflater.from(parent.context).inflate(
                R.layout.item_connection_item,
                parent,
                false
            )
        )

    fun setData(list: List<DepartureDetailsModel>) {
        data = ArrayList(list)
        notifyDataSetChanged()
    }

    override fun getItemCount(): Int = data.size

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val vh = holder as ConnectionListViewHolder
        val item = data[position]
        vh.departureDate.text = "${item.departureTime} (${item.departureDate})"
        vh.line.text = item.lineName
        vh.direction.text = item.direction
        vh.operator.text = item.operator
        vh.mode.text = item.mode
        vh.favIv.setImageDrawable(
            if (item.isFavourite) ContextCompat.getDrawable(
                context,
                R.drawable.baseline_favorite_24
            ) else ContextCompat.getDrawable(context, R.drawable.baseline_favorite_border_24)
        )
        vh.favIv.setOnClickListener {
            if (item.lineName != null) listener.updateFavouriteStop(
                atcocode = item.atcocode,
                lineName = item.lineName,
                favourite = !item.isFavourite
            )
        }
    }

}

class ConnectionListViewHolder : RecyclerView.ViewHolder {
    val departureDate: TextView
    val line: TextView
    val direction: TextView
    val operator: TextView
    val mode: TextView
    val favIv: ImageView

    constructor(view: View) : super(view) {
        departureDate = view.findViewById(R.id.tv_departure_date)
        line = view.findViewById(R.id.tv_line)
        direction = view.findViewById(R.id.tv_direction)
        operator = view.findViewById(R.id.tv_operator)
        mode = view.findViewById(R.id.tv_mode)
        favIv = view.findViewById(R.id.fav_iv)
    }
}

interface SelectDepartureListener {
    fun updateFavouriteStop(atcocode: String, lineName: String, favourite: Boolean)
}

