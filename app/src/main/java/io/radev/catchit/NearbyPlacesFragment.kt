package io.radev.catchit

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import kotlinx.android.synthetic.main.fragment_first.*
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import org.jetbrains.anko.doAsync
import org.jetbrains.anko.uiThread
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory


class NearbyPlacesFragment : Fragment(), SelectPlaceListener {
    val TAG = "nearbyPlacesFragment"
    lateinit var itemAdapter: NearbyPlacesItemAdapter
    lateinit var recyclerView: RecyclerView

    lateinit var retrofitService: ApiService
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_first, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupNetworkLayer()
        recyclerView = recycler_view
        itemAdapter = NearbyPlacesItemAdapter(this)
        recyclerView.layoutManager = LinearLayoutManager(context, RecyclerView.VERTICAL, false)
        recyclerView.adapter = itemAdapter
        swiperefresh.setOnRefreshListener {
            getNearbyPlaces()
        }
        getNearbyPlaces()
    }

    private fun getNearbyPlaces() {
        val request = retrofitService.getNearbyPlaces()
        doAsync {
            swiperefresh.isRefreshing = true
            val response = request.execute()
            uiThread {
                swiperefresh.isRefreshing = false
                if (response.body() != null) itemAdapter.setData(response.body()!!.memberList)
            }
        }
    }

    fun setupNetworkLayer() {
        val httpLoggingInterceptor = HttpLoggingInterceptor()
        httpLoggingInterceptor.level = HttpLoggingInterceptor.Level.BODY

        val moshi = Moshi.Builder()
            .add(KotlinJsonAdapterFactory())
            .build()

        val okHttpClient = OkHttpClient.Builder()
            .addNetworkInterceptor(httpLoggingInterceptor)
            .build();

        retrofitService = Retrofit.Builder()
            .baseUrl(ApiConstants.API_BASE_URL)
            .client(okHttpClient)
            .addConverterFactory(MoshiConverterFactory.create())
            .build()
            .create(ApiService::class.java)
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
