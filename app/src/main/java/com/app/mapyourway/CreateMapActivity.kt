package com.app.mapyourway

import android.Manifest
import android.app.Activity
import android.content.DialogInterface
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Location
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuItem
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModelProvider
import com.app.mapyourway.models.Place
import com.app.mapyourway.models.UserMap
import com.app.mapyourway.viewmodels.UserMapViewModel
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.LatLngBounds
import com.google.android.gms.maps.model.Marker
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.material.snackbar.Snackbar

class CreateMapActivity : AppCompatActivity(), OnMapReadyCallback {

    companion object {
        private const val TAG = "CreateMapActivity"
        const val EXTRA_MAP_TITLE = "EXTRA_MAP_TITLE"
        const val EXTRA_USER_MAP = "EXTRA_USER_MAP"
        const val USER_MAP_PLACES = "USER_MAP_PLACES"
        private val REQUEST_LOCATION_PERMISSION = 1
        private val DEFAULT_ZOOM = 15f
    }

    private lateinit var mMap: GoogleMap
    private lateinit var fusedLocationProviderClient: FusedLocationProviderClient
    private lateinit var defaultLocation: LatLng
    private lateinit var currentLocation: Location
    private var markers: MutableList<Marker> = mutableListOf()
    private lateinit var userMap: UserMap
    private var isEdit: Boolean = false
    private lateinit var viewModel: UserMapViewModel
    //private var places = mutableListOf<Place>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_create_map)

        supportActionBar?.title = intent.getStringExtra(MainActivity.EXTRA_MAP_TITLE)
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        val mapFragment = supportFragmentManager
            .findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)

        mapFragment.view?.let {
            Snackbar.make(
                it,
                "Long press to add a marker.\nTap on item description to delete.",
                Snackbar.LENGTH_INDEFINITE
            )
                .setAction("OK", {})
                .setActionTextColor(ContextCompat.getColor(this, android.R.color.white))
                .show()
        }
        // Construct a FusedLocationProviderClient.
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this)
        viewModel = ViewModelProvider(this).get(UserMapViewModel::class.java)
    }

    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap

        mMap.setOnInfoWindowClickListener { markerToDelete ->
            Log.i(TAG, "onInfoWindowClickListener-delete this marker")
            markers.remove(markerToDelete)

            var indexToDelete = 0
            for (i in 0 until userMap.places.size) {
                if (userMap.places[i].latitude == markerToDelete.position.latitude && userMap.places[i].longitude == markerToDelete.position.longitude) {
                    indexToDelete = i
                }
            }
            if (indexToDelete != 0) {
                userMap.places.removeAt(indexToDelete)
            }
            markerToDelete.remove()
        }
        mMap.setOnMapLongClickListener { latLng ->
            Log.i(TAG, "onMapLongClickListener")
            showAlertDialog(latLng)
        }

        enableMyLocation()

        //edit new markers
        if (intent.getSerializableExtra(DisplayMapActivity.USER_MAP_PLACES) != null) {
            isEdit = true
            userMap = intent.getSerializableExtra(DisplayMapActivity.USER_MAP_PLACES) as UserMap
            supportActionBar?.title = userMap.title

            val boundsBuilder = LatLngBounds.Builder()
            for (place in userMap.places) {
                val latLng = LatLng(place.latitude, place.longitude)
                boundsBuilder.include(latLng)
                mMap.addMarker(
                    MarkerOptions().position(latLng).title(place.title)
                        .snippet(place.description)
                )
            }
            if (userMap.places.size > 1) {
                mMap.moveCamera(
                    CameraUpdateFactory.newLatLngBounds(
                        boundsBuilder.build(),
                        300,
                        250,
                        0
                    )
                )
            } else if (userMap.places.size == 1) {
                mMap.moveCamera(
                    CameraUpdateFactory.newLatLngZoom(
                        LatLng(
                            userMap.places[0].latitude,
                            userMap.places[0].longitude
                        ), 5f
                    )
                )
            }
        }
    }

    private fun showAlertDialog(latLng: LatLng) {
        val placeFormView = LayoutInflater.from(this).inflate(R.layout.dialog_create_place, null)
        val dialog = AlertDialog.Builder(this)
            .setTitle("Create a pin")
            .setView(placeFormView)
            .setNegativeButton("Cancel", null)
            .setPositiveButton("OK", null)
            .show()
        dialog.getButton(DialogInterface.BUTTON_POSITIVE).setOnClickListener {
            val title = placeFormView.findViewById<EditText>(R.id.txtTitle).text.toString()
            val description = placeFormView.findViewById<EditText>(R.id.txtDesc).text.toString()
            val link = placeFormView.findViewById<EditText>(R.id.txtLink).text.toString()
            if (title.trim().isEmpty() || description.trim().isEmpty()) {
                Toast.makeText(
                    this,
                    "Place must have non-empty title and description",
                    Toast.LENGTH_LONG
                ).show()
                return@setOnClickListener
            }
            val marker = mMap.addMarker(
                MarkerOptions().position(latLng)
                    .title(title)
                    .snippet(description)
            )
            markers.add(marker)
            addNewPlace(marker, link)
            dialog.dismiss()
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_create_map, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == R.id.miSave) {
//            if (places.isEmpty()) {
//                Toast.makeText(
//                    this,
//                    "There must be at least one place on the map",
//                    Toast.LENGTH_LONG
//                ).show()
//                return true
//            }

            //back to main activity list
            val name = intent.getStringExtra(EXTRA_MAP_TITLE)
            if (name != null) {
                val newUserMap = UserMap()
                newUserMap.title = name
                newUserMap.places.addAll(userMap.places)

                viewModel.insert(newUserMap)
                val data = Intent()
                data.putExtra(EXTRA_USER_MAP, newUserMap)
                setResult(Activity.RESULT_OK, data)
                finish()
            }
            //back to display activity from edit
            if (isEdit) {
                //update places for usermap in db
                //userMap.places.addAll(places)
                viewModel.update(userMap)
                val data = Intent()
                data.putExtra(USER_MAP_PLACES, userMap)
                setResult(Activity.RESULT_OK, data)
                finish()
            }
            return true
        }
        return super.onOptionsItemSelected(item)
    }

    private fun addNewPlace(
        marker: Marker, link: String
    ) {
        val place = Place()
        if (isEdit) {
            place.mapId = userMap.userMapId
        }
        place.title = marker.title
        place.link = link
        place.description = marker.snippet
        place.latitude = marker.position.latitude
        place.longitude = marker.position.longitude
        userMap.places.add(place)
    }

    private fun isPermissionGranted(): Boolean {
        return ContextCompat.checkSelfPermission(
            this,
            Manifest.permission.ACCESS_FINE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED
    }

    private fun enableMyLocation() {
        if (isPermissionGranted()) {
            if (ActivityCompat.checkSelfPermission(
                    this,
                    Manifest.permission.ACCESS_FINE_LOCATION
                ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                    this,
                    Manifest.permission.ACCESS_COARSE_LOCATION
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                ActivityCompat.requestPermissions(
                    this,
                    arrayOf<String>(Manifest.permission.ACCESS_FINE_LOCATION),
                    REQUEST_LOCATION_PERMISSION
                )
                return
            }
            mMap.isMyLocationEnabled = true
            getDeviceLocation()
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == REQUEST_LOCATION_PERMISSION) {
            if (grantResults.contains(PackageManager.PERMISSION_GRANTED)) {
                enableMyLocation()
            }
        }
    }

    private fun getDeviceLocation() {
        /*
         * Get the best and most recent location of the device, which may be null in rare
         * cases when a location is not available.
         */
        try {
            if (mMap.isMyLocationEnabled) {
                val locationResult = fusedLocationProviderClient.lastLocation
                locationResult.addOnCompleteListener(this) { task ->
                    if (task.isSuccessful) {
                        // Set the map's camera position to the current location of the device.
                        currentLocation = task.result
                        if (currentLocation != null) {
                            mMap?.moveCamera(
                                CameraUpdateFactory.newLatLngZoom(
                                    LatLng(
                                        currentLocation!!.latitude,
                                        currentLocation!!.longitude
                                    ), DEFAULT_ZOOM
                                )
                            )
                        }
                    } else {
                        Log.d(TAG, "Current location is null. Using defaults.")
                        Log.e(TAG, "Exception: %s", task.exception)
                        mMap?.moveCamera(
                            CameraUpdateFactory
                                .newLatLngZoom(defaultLocation, DEFAULT_ZOOM)
                        )
                        mMap?.uiSettings?.isMyLocationButtonEnabled = false
                    }
                }
            }
        } catch (e: SecurityException) {
            Log.e("Exception: %s", e.message, e)
        }
    }
}