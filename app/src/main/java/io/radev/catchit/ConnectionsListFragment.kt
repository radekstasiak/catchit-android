package io.radev.catchit

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import kotlinx.android.synthetic.main.fragment_second.*
import kotlinx.android.synthetic.main.fragment_second.recycler_view
import kotlinx.android.synthetic.main.fragment_second.tv_header
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import org.jetbrains.anko.doAsync
import org.jetbrains.anko.uiThread
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory


class ConnectionsListFragment : Fragment() {
    val args: ConnectionsListFragmentArgs by navArgs()

    val TAG = "connectionsListFragmentTag"
    lateinit var itemAdapter: ConnectionListAdapter
    lateinit var recyclerView: RecyclerView
    lateinit var retrofitService: ApiService

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

        setupNetworkLayer()
        recyclerView = recycler_view
        itemAdapter = ConnectionListAdapter()
        recyclerView.layoutManager = LinearLayoutManager(context, RecyclerView.VERTICAL, false)
        recyclerView.adapter = itemAdapter
        swiperefresh.setOnRefreshListener {
            getLiveTimetable()
        }
        getLiveTimetable()

    }

    fun getLiveTimetable() {
        val request = retrofitService.getLiveTimetable(atcocode = args.ATCOCODE)
        doAsync {
            swiperefresh.isRefreshing = true
            val response = request.execute()
            uiThread {
                swiperefresh.isRefreshing = false
                tv_header.text = "${response.body()!!.name} - ${response.body()!!.atcocode}"
                if (response.body() != null) itemAdapter.setData(response.body()!!.departures.get("all")!!)
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

}


class ConnectionListAdapter :
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    var data = arrayListOf<DepartureDetails>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder =
        ConnectionListViewHolder(
            LayoutInflater.from(parent.context).inflate(
                R.layout.item_connection_item,
                parent,
                false
            )
        )

    fun setData(list: List<DepartureDetails>) {
        data = ArrayList(list)
        notifyDataSetChanged()
    }

    override fun getItemCount(): Int = data.size

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val vh = holder as ConnectionListViewHolder
        val item = data[position]
        vh.departureDate.text = "${item.expectedDepartureTime} (${item.expectedDepartureDate})"
        vh.line.text = item.line
        vh.direction.text = item.direction
        vh.operator.text = item.operator
        vh.mode.text = item.mode

    }

}

class ConnectionListViewHolder : RecyclerView.ViewHolder {
    val departureDate: TextView
    val line: TextView
    val direction: TextView
    val operator: TextView
    val mode: TextView

    constructor(view: View) : super(view) {
        departureDate = view.findViewById(R.id.tv_departure_date)
        line = view.findViewById(R.id.tv_line)
        direction = view.findViewById(R.id.tv_direction)
        operator = view.findViewById(R.id.tv_operator)
        mode = view.findViewById(R.id.tv_mode)

    }
}
