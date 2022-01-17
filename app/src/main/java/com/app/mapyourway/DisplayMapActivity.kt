package com.app.mapyourway

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.app.mapyourway.models.Place
import com.app.mapyourway.models.UserMap
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.LatLngBounds
import com.google.android.gms.maps.model.MarkerOptions

class DisplayMapActivity : AppCompatActivity(), OnMapReadyCallback {

    private lateinit var mMap: GoogleMap
    private lateinit var userMap: UserMap
    private var places: MutableList<Place> = emptyList<Place>().toMutableList()
    private lateinit var rvPlaces: RecyclerView
    private lateinit var adapter: PlacesAdapter

    companion object {
        private const val TAG = "DisplayMapActivity"
        const val EXTRA_USER_MAP = "EXTRA_USER_MAP"
        const val USER_MAP_PLACES = "USER_MAP_PLACES"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_display_map)
        userMap = intent.getSerializableExtra(EXTRA_USER_MAP) as UserMap
        places.addAll(userMap.places)
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        val mapFragment = supportFragmentManager
            .findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)
        adapter = PlacesAdapter(this, places, object : PlacesAdapter.OnClickListener {
            //When user taps on a view in RV, navigate to link
            override fun onItemClick(position: Int) {
                val uri = userMap.places[position].link
                if (!uri.isEmpty()) {
                    if (!uri.startsWith("http://") && !uri.startsWith("https://")) {
                        val webpage = Uri.parse("http://" + uri)
                        startActivity(Intent(Intent.ACTION_VIEW, webpage))
                    }
                }
            }
        })
        rvPlaces = findViewById(R.id.rvPlaces)
        rvPlaces.layoutManager = LinearLayoutManager(this)
        rvPlaces.adapter = adapter
    }

    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap

        Log.i(TAG, "usermap to render: ${userMap.title}")
        supportActionBar?.title = userMap.title

        val boundsBuilder = LatLngBounds.Builder()
        for (place in places) {
            val latLng = LatLng(place.latitude, place.longitude)
            boundsBuilder.include(latLng)
            mMap.addMarker(
                MarkerOptions().position(latLng).title(place.title).snippet(place.description)
            )
        }
        if (places.size > 1) {
            mMap.moveCamera(
                CameraUpdateFactory.newLatLngBounds(
                    boundsBuilder.build(),
                    300,
                    250,
                    0
                )
            )
        } else {
            mMap.moveCamera(
                CameraUpdateFactory.newLatLngZoom(
                    LatLng(
                        places[0].latitude,
                        places[0].longitude
                    ), 5f
                )
            )
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_edit_map, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == R.id.miEdit) {
            launchIntentToEditMapActivity(userMap)
            return true
        }
        return super.onOptionsItemSelected(item)
    }

    private fun launchIntentToEditMapActivity(userMap: UserMap) {
        val intent = Intent(this@DisplayMapActivity, CreateMapActivity::class.java)
        intent.putExtra(CreateMapActivity.USER_MAP_PLACES, userMap)
        resultLauncher.launch(intent)
    }

    var resultLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                // There are no request codes
                val data: Intent? = result.data
                userMap = data?.getSerializableExtra(CreateMapActivity.USER_MAP_PLACES) as UserMap
                places.clear()
                places.addAll(userMap.places)
                //places = userMap.places
                adapter.notifyDataSetChanged()
                mMap.clear()
                val boundsBuilder = LatLngBounds.Builder()
                for (place in places) {
                    val latLng = LatLng(place.latitude, place.longitude)
                    boundsBuilder.include(latLng)
                    mMap.addMarker(
                        MarkerOptions().position(latLng).title(place.title)
                            .snippet(place.description)
                    )
                }
            }
        }
}