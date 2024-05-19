package com.egyabaah.mymaps

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem

import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.egyabaah.mymaps.databinding.ActivityDisplayMapBinding
import com.egyabaah.mymaps.models.UserMap
import com.google.android.gms.maps.model.BitmapDescriptor
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.LatLngBounds

private const val TAG = "DisplayMapActivity"
class DisplayMapActivity : AppCompatActivity(), OnMapReadyCallback {

    private lateinit var mMap: GoogleMap
    private lateinit var binding: ActivityDisplayMapBinding
    private  lateinit var userMap: UserMap
    private lateinit var customMarker: BitmapDescriptor
    private lateinit var miMapTypeNormal: MenuItem
    private lateinit var miMapTypeTerrian: MenuItem
    private lateinit var miMapTypeSatellite: MenuItem
    private lateinit var miMapTypeHybrid: MenuItem


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityDisplayMapBinding.inflate(layoutInflater)
        setContentView(binding.root)

        userMap = intent.getSerializableExtra(EXTRA_USER_MAP) as UserMap
        supportActionBar?.title = userMap.title
        customMarker = BitmapDescriptorFactory.fromResource(R.drawable.blue_custom_map_marker)
        

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        val mapFragment = supportFragmentManager
            .findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_map_type, menu)
        if (menu != null) {
            miMapTypeNormal = menu.findItem(R.id.miMapTypeNormal)
            miMapTypeTerrian = menu.findItem(R.id.miMapTypeTerrain)
            miMapTypeSatellite = menu.findItem(R.id.miMapTypeSatellite)
            miMapTypeHybrid = menu.findItem(R.id.miMapTypeHybrid)
        }
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == R.id.miMapTypeNormal){
            mMap.mapType = GoogleMap.MAP_TYPE_NORMAL
            miMapTypeNormal.isEnabled = false
            miMapTypeTerrian.isEnabled = true
            miMapTypeSatellite.isEnabled = true
            miMapTypeHybrid.isEnabled = true

            return true
        }
        else if (item.itemId == R.id.miMapTypeTerrain){
            mMap.mapType = GoogleMap.MAP_TYPE_TERRAIN
            miMapTypeTerrian.isEnabled = false
            miMapTypeNormal.isEnabled = true
            miMapTypeSatellite.isEnabled = true
            miMapTypeHybrid.isEnabled = true
            return true
        }
        else if (item.itemId == R.id.miMapTypeSatellite){
            mMap.mapType = GoogleMap.MAP_TYPE_SATELLITE
            miMapTypeSatellite.isEnabled = false
            miMapTypeNormal.isEnabled = true
            miMapTypeTerrian.isEnabled = true
            miMapTypeHybrid.isEnabled = true
            return true
        }
        else if (item.itemId == R.id.miMapTypeHybrid){
            mMap.mapType = GoogleMap.MAP_TYPE_HYBRID
            miMapTypeHybrid.isEnabled = false
            miMapTypeSatellite.isEnabled = true
            miMapTypeNormal.isEnabled = true
            miMapTypeTerrian.isEnabled = true
            return true
        }
        return super.onOptionsItemSelected(item)
    }

    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap

        Log.i(TAG, "user map to render: ${userMap.title}")

        // Add userMap places marker
        val boundsBuilder = LatLngBounds.Builder()
        for (place in userMap.places){
            val latLng = LatLng(place.latitude, place.longitude)
            boundsBuilder.include(latLng)
            mMap.addMarker(MarkerOptions().position(latLng).title(place.title).snippet(place.description).icon(customMarker))
        }
        mMap.moveCamera(CameraUpdateFactory.newLatLngBounds(boundsBuilder.build(), 1000, 1000, 0))
    }
}