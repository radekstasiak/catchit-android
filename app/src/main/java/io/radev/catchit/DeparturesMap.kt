package io.radev.catchit

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions


class DeparturesMap : Fragment() {
    private val model: DashboardViewModel by activityViewModels()

    private val callback = OnMapReadyCallback { googleMap ->
        /**
         * Manipulates the map once available.
         * This callback is triggered when the map is ready to be used.
         * This is where we can add markers or lines, add listeners or move the camera.
         * In this case, we just add a marker near Sydney, Australia.
         * If Google Play services is not installed on the device, the user will be prompted to
         * install it inside the SupportMapFragment. This method will only be triggered once the
         * user has installed Google Play services and returned to the app.
         */

        if (!model.placeMemberModelList.hasActiveObservers()) {
            model.placeMemberModelList.observe(
                viewLifecycleOwner,
                Observer<List<PlaceMemberModel>> {
                    it.forEach { marker ->
                        val position = LatLng(marker.latitude, marker.longitude)
                        googleMap.addMarker(
                            MarkerOptions().position(position).title("${marker.atcocode} - ${marker.name}")
                        )

                    }
                    if(it.isNotEmpty()){
                        val nearestPosition = LatLng(it[0].latitude, it[0].longitude)
//                        googleMap.moveCamera(CameraUpdateFactory.newLatLng(nearestPosition))
//                        googleMap.animateCamera(CameraUpdateFactory.zoomTo(14f), 2000, null);
                        val cameraPosition = CameraPosition.Builder()
                            .target(nearestPosition)
                            .zoom(15f).build()
                        //Zoom in and animate the camera.
                        //Zoom in and animate the camera.
                        googleMap.animateCamera(
                            CameraUpdateFactory.newCameraPosition(
                                cameraPosition
                            )
                        )
                    }

                })
        }
//        val sydney = LatLng(-34.0, 151.0)
//        googleMap.addMarker(MarkerOptions().position(sydney).title("Marker in Sydney"))
//        googleMap.moveCamera(CameraUpdateFactory.newLatLng(sydney))

    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_departures_map, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val mapFragment = childFragmentManager.findFragmentById(R.id.map) as SupportMapFragment?
        mapFragment?.getMapAsync(callback)
        model.getNearbyPlaces()
    }
}