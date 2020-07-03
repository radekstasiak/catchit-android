package io.radev.catchit

import android.Manifest
import android.annotation.SuppressLint
import android.content.DialogInterface
import android.content.pm.PackageManager
import android.location.Location
import android.os.Bundle
import android.util.Log
import android.view.*
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.widget.SearchView
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.*
import com.google.android.material.dialog.MaterialAlertDialogBuilder


class DeparturesMap : Fragment(), GoogleMap.OnMarkerDragListener {
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private val model: DashboardViewModel by activityViewModels()
    val markerList = arrayListOf<Marker>()
    val requestPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted: Boolean ->
        if (isGranted) {
            // Permission is granted. Continue the action or workflow in your
            // app.
            Log.d("LOCATION_PERM", "just granted")
            getLastLocation()
        } else {
            Log.d("LOCATION_PERM", "declined")
            // Explain to the user that the feature is unavailable because the
            // features requires a permission that the user has denied. At the
            // same time, respect the user's decision. Don't link to system
            // settings in an effort to convince the user to change their
            // decision.
        }
    }

    @SuppressLint("MissingPermission")
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
        googleMap.uiSettings.isMapToolbarEnabled = false
        googleMap.uiSettings.isMyLocationButtonEnabled = true
        googleMap.setOnMyLocationButtonClickListener {
            getLastLocation()
            true
        }
        if (!model.placeMemberModelList.hasActiveObservers()) {
            model.placeMemberModelList.observe(
                viewLifecycleOwner,
                Observer<DepartureMapModel> {
                    googleMap.isMyLocationEnabled = isLocationPermissionGranted()
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
        setHasOptionsMenu(true)
        val mapFragment = childFragmentManager.findFragmentById(R.id.map) as SupportMapFragment?
        mapFragment?.getMapAsync(callback)
//        model.getNearbyPlaces()
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(requireActivity())
        checkForLocationPermission()
    }

    private fun isLocationPermissionGranted(): Boolean = ContextCompat.checkSelfPermission(
        requireContext(),
        Manifest.permission.ACCESS_FINE_LOCATION
    ) == PackageManager.PERMISSION_GRANTED

    private fun checkForLocationPermission() {
        when {
            isLocationPermissionGranted() -> {
                // You can use the API that requires the permission.
                getLastLocation()
            }
            shouldShowRequestPermissionRationale(Manifest.permission.ACCESS_FINE_LOCATION) -> {
                // In an educational UI, explain to the user why your app requires this
                // permission for a specific feature to behave as expected. In this UI,
                // include a "cancel" or "no thanks" button that allows the user to
                // continue using your app without granting the permission.
                displayLocationDialog()
            }
            else -> {
                // You can directly ask for the permission.
                // The registered ActivityResultCallback gets the result of this request.
                requestPermissionLauncher.launch(
                    Manifest.permission.ACCESS_FINE_LOCATION
                )
            }
        }
    }


    @SuppressLint("MissingPermission")
    private fun getLastLocation() {
        fusedLocationClient.lastLocation
            .addOnSuccessListener { location: Location? ->
                if (location != null) model.getNearbyPlaces(
                    longitude = location.longitude,
                    latitude = location.latitude
                )
            }
    }

    private fun displayLocationDialog() {
        MaterialAlertDialogBuilder(context)
            .setTitle(getString(R.string.location_permission_required))
            .setMessage(getString(R.string.location_permission_dialog))
            .setPositiveButton(getString(R.string.allow),
                DialogInterface.OnClickListener { _, _ ->
                    requestPermissionLauncher.launch(
                        Manifest.permission.ACCESS_FINE_LOCATION
                    )
                })
            .setNegativeButton(getString(R.string.cancel),
                DialogInterface.OnClickListener { dialog, _ ->
                    dialog.cancel()
                })
            .show()
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

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.options_menu, menu)
        val searchView = (menu.findItem(R.id.search).actionView as SearchView)
        searchView.queryHint = getString(R.string.search_hint)
        searchView.setOnQueryTextListener(object :
            SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                if (query != null) {model.getPostCodeDetails(query)
                    searchView.setQuery("", false);
                    searchView.clearFocus();
                    searchView.setIconified(true);
                }
                return true
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                return true
            }
        }
        )
        // Associate searchable configuration with the SearchView
//        val searchManager = requireActivity().getSystemService(Context.SEARCH_SERVICE) as SearchManager
//        (menu.findItem(R.id.search).actionView as SearchView).apply {
//            setSearchableInfo(searchManager.getSearchableInfo(componentName))
//        }

        super.onCreateOptionsMenu(menu, inflater)

    }


}