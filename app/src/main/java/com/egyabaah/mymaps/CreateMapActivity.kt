package com.egyabaah.mymaps

import android.Manifest.permission.*
import android.app.Activity
import android.content.DialogInterface
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.os.Handler
import android.os.SystemClock
import android.util.Log
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuItem
import android.view.animation.BounceInterpolator
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.egyabaah.mymaps.databinding.ActivityCreateMapBinding
import com.egyabaah.mymaps.models.Place
import com.egyabaah.mymaps.models.UserMap
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.BitmapDescriptor
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.Marker
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.material.snackbar.Snackbar
import com.vmadalin.easypermissions.EasyPermissions
import com.vmadalin.easypermissions.annotations.AfterPermissionGranted


private const val TAG = "CreateMapActivity"
private const val REQUEST_CODE_LOCATION = 125

class CreateMapActivity : AppCompatActivity(), OnMapReadyCallback {

    private lateinit var mMap: GoogleMap
    private lateinit var binding: ActivityCreateMapBinding
    private var markers: MutableList<Marker> = mutableListOf()
    private lateinit var customMarker: BitmapDescriptor
    private lateinit var miMapTypeNormal: MenuItem
    private lateinit var miMapTypeTerrian: MenuItem
    private lateinit var miMapTypeSatellite: MenuItem
    private lateinit var miMapTypeHybrid: MenuItem
    private lateinit var fusedLocationClient: FusedLocationProviderClient


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityCreateMapBinding.inflate(layoutInflater)
        setContentView(binding.root)


        supportActionBar?.title = intent.getStringExtra(EXTRA_MAP_TITLE)

        customMarker = BitmapDescriptorFactory.fromResource(R.drawable.blue_custom_map_marker)

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        val mapFragment = supportFragmentManager
            .findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)
        binding.fabCurrentLocation.setOnClickListener{
            getUserLocation()
        }
        mapFragment.view?.let {
            Snackbar.make(it, "Long press to add a marker!", Snackbar.LENGTH_INDEFINITE)
                .setAction("OK", {})
                .setActionTextColor(ContextCompat.getColor(this, android.R.color.white)).show()
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_create_map, menu)
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
        // Check that item is the save menu option
        if (item.itemId === R.id.miSave) {
            Log.i(TAG, "Tapped on save!")
            if (markers.isEmpty()) {
                Toast.makeText(
                    this,
                    "There must be at least one marker on the map",
                    Toast.LENGTH_LONG
                ).show()
                return true
            }
            val places = markers.mapNotNull { marker ->
                if (marker.title?.isEmpty() == false && marker.snippet?.isEmpty() == false
                ) {
                    Place(
                        marker.title!!,
                        marker.snippet!!,
                        marker.position.latitude,
                        marker.position.longitude
                    )
                } else {
                    null
                }
            }
            val userMapId = intent.getIntExtra(EXTRA_MAP_ID, 0)
            val userMap = intent.getStringExtra(EXTRA_MAP_TITLE)?.let { UserMap(it, places, userMapId) }
            val data = Intent()
            data.putExtra(EXTRA_USER_MAP, userMap)
            setResult(Activity.RESULT_OK, data)
            finish()
            return true
        }
        else if (item.itemId == R.id.miMapTypeNormal){
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

        mMap.setOnInfoWindowClickListener { markerToDelete ->
            Log.i(TAG, "onWindowClickListener - delete this marker")
            markers.remove(markerToDelete)
            markerToDelete.remove()
        }

        mMap.setOnMapLongClickListener { latLng ->
            Log.i(TAG, "onMapLongClickListener")
            showAlertDialog(latLng)
        }
        // Add a marker in Sydney and move the camera
        val siliconValley = LatLng(37.4, -122.1)
//        mMap.addMarker(MarkerOptions().position(sydney).title("Marker in Sydney"))
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(siliconValley, 10f))
    }

    private fun showAlertDialog(latLng: LatLng) {
        val placeFormView = LayoutInflater.from(this).inflate(R.layout.dialog_create_place, null)
        val dialog = AlertDialog.Builder(this)
            .setTitle("Create a marker")
            .setView(placeFormView)
            .setNegativeButton("Cancel", null)
            .setPositiveButton("OK", null).show()

        dialog.getButton(DialogInterface.BUTTON_POSITIVE).setOnClickListener {
            val title = placeFormView.findViewById<EditText>(R.id.etTitle).text.toString()
            val description =
                placeFormView.findViewById<EditText>(R.id.etDescription).text.toString()
            if (title.trim().isEmpty() || description.trim().isEmpty()) {
                Toast.makeText(
                    this,
                    "Place must have non-empty title and description",
                    Toast.LENGTH_LONG
                ).show()
                return@setOnClickListener
            }


            val marker = mMap.addMarker(
                MarkerOptions().position(latLng).title(title).snippet(description)
                    .icon(customMarker)
            )
            if (marker != null) {
                markers.add(marker)
                dropPinEffect(marker)
            }
            dialog.dismiss()
        }

    }

    private fun dropPinEffect(marker: Marker) {
        val handler = Handler()
        val start = SystemClock.uptimeMillis()
        val duration: Long = 1500

        // Use the bounce interpolator
        val interpolator = BounceInterpolator()

        // Animate marker with a bounce updating its position every 15ms
        handler.post(object : Runnable {
            override fun run() {
                val elapsed = SystemClock.uptimeMillis() - start
                val t =
                    Math.max(1 - interpolator.getInterpolation(elapsed.toFloat() / duration), 0f)
                // Set the anchor
                marker.setAnchor(0.5f, 1.0f + 14 * t)
                if (t > 0.0) {
                    // Post this event again 15ms from now.
                    handler.postDelayed(this, 15)
                } else { // done elapsing, show window
                    marker.showInfoWindow()
                }
            }
        })
    }

    @AfterPermissionGranted(REQUEST_CODE_LOCATION)
    private fun getUserLocation() {
        if (EasyPermissions.hasPermissions(this, ACCESS_COARSE_LOCATION, ACCESS_FINE_LOCATION)){
            fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
            // EasyPermission is already handling this, but added here again to remove IDE errors
            if (ActivityCompat.checkSelfPermission(
                    this,
                    ACCESS_FINE_LOCATION
                ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                    this,
                    ACCESS_COARSE_LOCATION
                ) != PackageManager.PERMISSION_GRANTED
            ) {

                return
            }
            fusedLocationClient.lastLocation.addOnSuccessListener { location ->
                if (location != null) {
                    val lastLocationLatLng = LatLng(location.latitude, location.longitude)
                    Log.i(TAG, "Last location: $lastLocationLatLng")
                    mMap.animateCamera(
                        CameraUpdateFactory.newLatLngZoom(lastLocationLatLng, 10f),
                        1500,
                        null
                    )
                } else {
                    Log.i(TAG, "Last location is null")
                }
            }.addOnFailureListener { exception ->
                Log.e(TAG, "Error getting last location", exception)
            }

        }
        else{
            EasyPermissions.requestPermissions(
                this,
                "This app requires location permission to access your current location",
                REQUEST_CODE_LOCATION,
                ACCESS_COARSE_LOCATION, ACCESS_FINE_LOCATION
            )
        }

    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
//      // EasyPermissions handles the request result.
        EasyPermissions.onRequestPermissionsResult(requestCode, permissions, grantResults, this)
    }

}