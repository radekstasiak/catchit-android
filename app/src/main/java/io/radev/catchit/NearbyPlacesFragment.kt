package io.radev.catchit

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.fragment_first.*
import org.jetbrains.anko.doAsync
import org.jetbrains.anko.uiThread


class NearbyPlacesFragment : Fragment(), SelectPlaceListener {
    val TAG = "nearbyPlacesFragment"
    private lateinit var itemAdapter: NearbyPlacesItemAdapter
    private lateinit var recyclerView: RecyclerView
    private val model: DashboardViewModel by activityViewModels()

    //    private var longitude: Double = 0.0
//    private var latitude: Double = 0.0
//    private lateinit var postCode: String
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
//
//        longitude = args.longitude.toDouble()
//        latitude = args.latitude.toDouble()
//        postCode = args.postCode
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_first, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        tv_header.text = (String.format(requireActivity().resources.getString(R.string.nearby_places_header), model.postCodeMember.value!!.name))
        recyclerView = recycler_view
        itemAdapter = NearbyPlacesItemAdapter(this)
        recyclerView.layoutManager = LinearLayoutManager(context, RecyclerView.VERTICAL, false)
        recyclerView.adapter = itemAdapter
        swiperefresh.setOnRefreshListener {
            getNearbyPlaces(longitude = model.postCodeMember.value!!.longitude, latitude = model.postCodeMember.value!!.latitude)
        }
        getNearbyPlaces(longitude = model.postCodeMember.value!!.longitude, latitude = model.postCodeMember.value!!.latitude)
    }

    private fun getNearbyPlaces(longitude: Double, latitude: Double) {
        val request = CatchItApp.apiService.getNearbyPlaces(lon = longitude, lat = latitude)
        if (swiperefresh != null) swiperefresh.isRefreshing = true
        doAsync {
            val response = request.execute()
            uiThread {
                if (swiperefresh != null) swiperefresh.isRefreshing = false
                if (response.body() != null) itemAdapter.setData(response.body()!!.memberList)
            }
        }
    }


    override fun onPlaceSelected(atcocode: String) {
        val action =
            NearbyPlacesFragmentDirections.actionFirstFragmentToSecondFragment(ATCOCODE = atcocode)
        findNavController().navigate(action)
    }


}

class NearbyPlacesItemAdapter(val listener: SelectPlaceListener) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    var data = arrayListOf<PlaceMember>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder =
        PlacesNearbyItemViewHolder(
            LayoutInflater.from(parent.context).inflate(
                R.layout.item_nearby_places,
                parent,
                false
            )
        )

    fun setData(list: List<PlaceMember>) {
        data = ArrayList(list)
        notifyDataSetChanged()
    }

    override fun getItemCount(): Int = data.size

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val vh = holder as PlacesNearbyItemViewHolder
        val item = data[position]
        vh.name.text = item.name
        vh.description.text = item.description
        vh.atcocode.text = item.atcocode
        vh.distance.text = item.distance.toString()
        vh.itemView.setOnClickListener { listener.onPlaceSelected(item.atcocode) }
    }

}

class PlacesNearbyItemViewHolder : RecyclerView.ViewHolder {
    val name: TextView
    val description: TextView
    val atcocode: TextView
    val distance: TextView

    constructor(view: View) : super(view) {
        name = view.findViewById(R.id.tv_name)
        description = view.findViewById(R.id.tv_description)
        atcocode = view.findViewById(R.id.tv_atcocode)
        distance = view.findViewById(R.id.tv_distance)
    }
}

interface SelectPlaceListener {
    fun onPlaceSelected(atcocode: String)
}
