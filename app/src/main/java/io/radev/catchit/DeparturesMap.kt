package io.radev.catchit

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.*


class DeparturesMap : Fragment(), GoogleMap.OnMarkerDragListener {
    private val model: DashboardViewModel by activityViewModels()
    val markerList = arrayListOf<Marker>()
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
                Observer<DepartureMapModel> {
                    val departuresList = it.departuresList
                    markerList.forEach { existingMarker -> existingMarker.remove() }
                    markerList.clear()
                    departuresList.forEach { departure ->
                        val position = LatLng(departure.latitude, departure.longitude)
                        val marker = googleMap.addMarker(
                            MarkerOptions().position(position).title(departure.atcocode)
                                .snippet("Click to see departing buses")
                        )
                        marker.tag = "departure"

                        markerList.add(marker)

                    }

                    val userLocationMarker = googleMap.addMarker(
                        MarkerOptions().position(it.userLatLng).title("You")
                            .icon(
                                BitmapDescriptorFactory
                                    .defaultMarker(BitmapDescriptorFactory.HUE_AZURE)
                            ).draggable(true)
                    )

                    markerList.add(userLocationMarker)

                    userLocationMarker.tag = "user"
//                    if(departuresList.isNotEmpty()){

//                        googleMap.moveCamera(CameraUpdateFactory.newLatLng(nearestPosition))
//                        googleMap.animateCamera(CameraUpdateFactory.zoomTo(14f), 2000, null);
                    val cameraPosition = CameraPosition.Builder()
                        .target(it.userLatLng)
                        .zoom(15f).build()
                    //Zoom in and animate the camera.
                    //Zoom in and animate the camera.
                    googleMap.animateCamera(
                        CameraUpdateFactory.newCameraPosition(
                            cameraPosition
                        )
                    )

                    googleMap.setOnInfoWindowClickListener { marker ->
                        if (marker != null && (marker.tag as String) == "departure") onPlaceSelected(
                            marker.title
                        )
                    }

                    googleMap.setOnMarkerDragListener(this)

//                    googleMap.setLatLngBoundsForCameraTarget(it.latLngBounds)
//                    val uiSettings = googleMap.uiSettings
//                    uiSettings.isScrollGesturesEnabled = false

//                    }

                })
        }
//        val sydney = LatLng(-34.0, 151.0)
//        googleMap.addMarker(MarkerOptions().position(sydney).title("Marker in Sydney"))
//        googleMap.moveCamera(CameraUpdateFactory.newLatLng(sydney))
//https://developers.google.com/maps/documentation/android-sdk/marker
        //https://developers.google.com/maps/documentation/android-sdk/infowindows
    }

    fun onPlaceSelected(atcocode: String) {
        val action = DeparturesMapDirections.actionDeparturesMapToDepartureListDialogFragment()
        model.selectAtcocode(atcocode)
        findNavController().navigate(action)
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

    override fun onMarkerDragEnd(marker: Marker?) {
        if (marker != null && marker.tag == "user") model.getNearbyPlaces(
            longitude = marker.position.longitude,
            latitude = marker.position.latitude
        )
    }

    override fun onMarkerDragStart(p0: Marker?) {

    }

    override fun onMarkerDrag(p0: Marker?) {

    }


}