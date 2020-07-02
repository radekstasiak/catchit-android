package io.radev.catchit


import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import dagger.hilt.android.AndroidEntryPoint
import io.radev.catchit.alarm.UpdateTimetableAlarmManager
import kotlinx.android.synthetic.main.fragment_item_list_dialog_list_dialog.*
import javax.inject.Inject

@AndroidEntryPoint
class DepartureListDialogFragment : BottomSheetDialogFragment(), SelectDepartureListener {
    lateinit var itemAdapter: DepartureListAdapter
    lateinit var recyclerView: RecyclerView

    private val model: DashboardViewModel by activityViewModels()

    @Inject
    lateinit var updateTimetableAlarmManager: UpdateTimetableAlarmManager

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_item_list_dialog_list_dialog, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        recyclerView = recycler_view
        itemAdapter = DepartureListAdapter(this, requireActivity())
        recyclerView.layoutManager = LinearLayoutManager(context, RecyclerView.VERTICAL, false)
        recyclerView.adapter = itemAdapter
        model.getLiveTimetable()
        model.departureDetailsModelList.observe(
            viewLifecycleOwner,
            Observer<List<DepartureDetailsUiModel>> {
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

class DepartureListAdapter(
    val listener: SelectDepartureListener,
    private val context: Context
) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    var data = arrayListOf<DepartureDetailsUiModel>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder =
        DepartureListViewHolder(
            LayoutInflater.from(parent.context).inflate(
                R.layout.item_connection_item,
                parent,
                false
            )
        )

    fun setData(list: List<DepartureDetailsUiModel>) {
        data = ArrayList(list)
        notifyDataSetChanged()
    }

    override fun getItemCount(): Int = data.size

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val vh = holder as DepartureListViewHolder
        val item = data[position]
        vh.nextDeparture.text = String.format(
            context.getString(R.string.departure_wait_time),
            item.lineName,
            item.waitTime,
            item.direction
        )
        vh.expectedArrival.text =
            String.format(context.getString(R.string.expected_arrival), item.nextDeparture)
        vh.operator.text =
            String.format(context.getString(R.string.operator_name), item.operatorName)
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

class DepartureListViewHolder : RecyclerView.ViewHolder {
    val nextDeparture: TextView
    val expectedArrival: TextView
    val operator: TextView
    val favIv: ImageView

    constructor(view: View) : super(view) {
        nextDeparture = view.findViewById(R.id.next_departure)
        expectedArrival = view.findViewById(R.id.expected_arrival)
        operator = view.findViewById(R.id.tv_operator)
        favIv = view.findViewById(R.id.fav_iv)
    }
}

interface SelectDepartureListener {
    fun updateFavouriteStop(atcocode: String, lineName: String, favourite: Boolean)
}