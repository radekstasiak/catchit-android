package io.radev.catchit.fragment

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import io.radev.catchit.R
import io.radev.catchit.viewmodel.FavouriteDepartureAlert
import kotlinx.android.synthetic.main.fragment_favourite_list.*


class FavouriteListFragment : Fragment(), SelectDepartureListener {
    private lateinit var recyclerView: RecyclerView
    private lateinit var favourListAdapter: FavouriteListRecyclerViewAdapter

    //    private val model: DashboardViewModel by activityViewModels()
    private val model: FavouriteListViewModel by activityViewModels()
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
        favourListAdapter =
            FavouriteListRecyclerViewAdapter(context = requireContext(), listener = this)
        recyclerView.adapter = favourListAdapter
        model.favouriteStopState.observe(
            viewLifecycleOwner,
            Observer<FavouriteDepartureViewState> {
                render(it)
            })

        swipe_refresh.setOnRefreshListener {
            model.processIntents(intent = FavouriteStopListIntent.LoadFavourites)
        }

        model.processIntents(intent = FavouriteStopListIntent.LoadFavourites)
    }


    fun render(viewState: FavouriteDepartureViewState) {
        swipe_refresh.isRefreshing = viewState.isLoading
        favourListAdapter.setData(viewState.list)
    }

//    private fun updateData() {
//        swipe_refresh.isRefreshing = true
//        model.updateFavouriteDeparturesList()
//    }

    override fun updateFavouriteStop(atcocode: String, lineName: String, favourite: Boolean) {
//        model.updateFavouriteLine(atcocode = atcocode, lineName = lineName, favourite = favourite)
    }


}

class FavouriteListRecyclerViewAdapter(
    private val context: Context,
    val listener: SelectDepartureListener
) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    var data = arrayListOf<FavouriteDepartureAlert>()
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder =
        FavouriteItemViewHolder(
            LayoutInflater.from(parent.context).inflate(
                R.layout.item_connection_item,
                parent,
                false
            )
        )

    fun setData(list: List<FavouriteDepartureAlert>) {
        data = ArrayList(list)
        notifyDataSetChanged()
    }

    override fun getItemCount(): Int = data.size


    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val vh = holder as FavouriteItemViewHolder
        val item = data[position]
        vh.atcocode.text = "${item.stopName} (${item.atcocode})"
        vh.nextDeparture.text = String.format(
            context.getString(R.string.departure_wait_time),
            item.lineName,
            item.waitTime,
            item.direction
        )

        vh.favIv.setImageDrawable(
            ContextCompat.getDrawable(
                context,
                R.drawable.baseline_favorite_24
            )
        )
        vh.expectedArrival.text =
            String.format(context.getString(R.string.expected_arrival), item.nextDeparture)

        //todo update the list in reactive manner
        vh.favIv.setOnClickListener {
            if (item.lineName != null) listener.updateFavouriteStop(
                atcocode = item.atcocode,
                lineName = item.lineName,
                favourite = false
            )

        }
    }
}

class FavouriteItemViewHolder : RecyclerView.ViewHolder {
    val nextDeparture: TextView
    val expectedArrival: TextView
    val atcocode: TextView
    val favIv: ImageView

    constructor(view: View) : super(view) {
        expectedArrival = view.findViewById(R.id.expected_arrival)
        nextDeparture = view.findViewById(R.id.next_departure)
        atcocode = view.findViewById(R.id.tv_operator)
        favIv = view.findViewById(R.id.fav_iv)

    }
}
