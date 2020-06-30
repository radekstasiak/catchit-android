package io.radev.catchit


import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import dagger.hilt.android.AndroidEntryPoint
import io.radev.catchit.alarm.UpdateTimetableAlarmManager
import io.radev.catchit.fragment.ConnectionListAdapter
import io.radev.catchit.fragment.SelectDepartureListener
import kotlinx.android.synthetic.main.fragment_item_list_dialog_list_dialog.*
import kotlinx.android.synthetic.main.fragment_item_list_dialog_list_dialog.recycler_view
import javax.inject.Inject

@AndroidEntryPoint
class DepartureListDialogFragment : BottomSheetDialogFragment(), SelectDepartureListener {
    lateinit var itemAdapter: ConnectionListAdapter
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
        itemAdapter = ConnectionListAdapter(this, requireActivity())
        recyclerView.layoutManager = LinearLayoutManager(context, RecyclerView.VERTICAL, false)
        recyclerView.adapter = itemAdapter
        model.getLiveTimetable()
        model.departureDetailsModelList.observe(
            viewLifecycleOwner,
            Observer<List<DepartureDetailsModel>> {
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