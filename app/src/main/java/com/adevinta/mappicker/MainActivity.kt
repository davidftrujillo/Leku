package com.adevinta.mappicker

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.location.Address
import android.location.Location
import android.os.Bundle
import android.os.StrictMode
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.adevinta.leku.ADDRESS
import com.adevinta.leku.LATITUDE
import com.adevinta.leku.LEKU_POI
import com.adevinta.leku.LOCATION_ADDRESS
import com.adevinta.leku.LONGITUDE
import com.adevinta.leku.LekuPoi
import com.adevinta.leku.LocationPicker
import com.adevinta.leku.LocationPickerActivity
import com.adevinta.leku.TIME_ZONE_DISPLAY_NAME
import com.adevinta.leku.TIME_ZONE_ID
import com.adevinta.leku.TRANSITION_BUNDLE
import com.adevinta.leku.ZIPCODE
import com.adevinta.leku.tracker.LocationPickerTracker
import com.adevinta.leku.tracker.TrackEvents
import java.util.UUID
import kotlin.collections.ArrayList
import kotlin.collections.List

private const val MAP_BUTTON_REQUEST_CODE = 1
private const val MAP_POIS_BUTTON_REQUEST_CODE = 2
private const val DEMO_LATITUDE = 41.4036299
private const val DEMO_LONGITUDE = 2.1743558
private const val POI1_LATITUDE = 41.4036339
private const val POI1_LONGITUDE = 2.1721618
private const val POI2_LATITUDE = 41.4023265
private const val POI2_LONGITUDE = 2.1741417

class MainActivity : AppCompatActivity() {

    private val lekuPois: List<LekuPoi>
        get() {
            val pois = ArrayList<LekuPoi>()

            val locationPoi1 = Location("leku")
            locationPoi1.latitude = POI1_LATITUDE
            locationPoi1.longitude = POI1_LONGITUDE
            val poi1 = LekuPoi(UUID.randomUUID().toString(), "Los bellota", locationPoi1)
            pois.add(poi1)

            val locationPoi2 = Location("leku")
            locationPoi2.latitude = POI2_LATITUDE
            locationPoi2.longitude = POI2_LONGITUDE
            val poi2 = LekuPoi(UUID.randomUUID().toString(), "Starbucks", locationPoi2)
            poi2.address = "Plaça de la Sagrada Família, 19, 08013 Barcelona"
            pois.add(poi2)

            return pois
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        StrictMode.setThreadPolicy(
                StrictMode.ThreadPolicy.Builder().detectAll().penaltyLog().build())
        StrictMode.setVmPolicy(StrictMode.VmPolicy.Builder().detectAll().penaltyLog().build())
        setContentView(R.layout.activity_main)
        val mapButton = findViewById<View>(R.id.map_button)
        mapButton.setOnClickListener {
            val locationPickerIntent = LocationPickerActivity.Builder()
                    .withLocation(DEMO_LATITUDE, DEMO_LONGITUDE)
                    // .withGeolocApiKey("<PUT API KEY HERE>")
                    // .withGooglePlacesApiKey("<PUT API KEY HERE>")
                    .withSearchZone("es_ES")
                    // .withSearchZone(SearchZoneRect(LatLng(26.525467, -18.910366), LatLng(43.906271, 5.394197)))
                    .withDefaultLocaleSearchZone()
                    // .shouldReturnOkOnBackPressed()
                    // .withStreetHidden()
                    // .withCityHidden()
                    // .withZipCodeHidden()
                    // .withSatelliteViewHidden()
                    .withGoogleTimeZoneEnabled()
                    // .withVoiceSearchHidden()
                    .withUnnamedRoadHidden()
                    .build(applicationContext)

            // this is optional if you want to return RESULT_OK if you don't set the
            // latitude/longitude and click back button
            locationPickerIntent.putExtra("test", "this is a test")

            startActivityForResult(locationPickerIntent, MAP_BUTTON_REQUEST_CODE)
        }

        val mapLegacyButton = findViewById<View>(R.id.map_button_legacy)
        mapLegacyButton.setOnClickListener {
            val locationPickerIntent = LocationPickerActivity.Builder()
                .withLocation(DEMO_LATITUDE, DEMO_LONGITUDE)
                .withUnnamedRoadHidden()
                .withLegacyLayout()
                .build(applicationContext)
            startActivityForResult(locationPickerIntent, MAP_BUTTON_REQUEST_CODE)
        }

        val mapPoisButton = findViewById<View>(R.id.map_button_with_pois)
        mapPoisButton.setOnClickListener {
            val locationPickerIntent = LocationPickerActivity.Builder()
                    .withLocation(DEMO_LATITUDE, DEMO_LONGITUDE)
                    .withPois(lekuPois)
                    .build(applicationContext)

            startActivityForResult(locationPickerIntent, MAP_POIS_BUTTON_REQUEST_CODE)
        }

        val mapStyleButton = findViewById<View>(R.id.map_button_with_style)
        mapStyleButton.setOnClickListener {
            val locationPickerIntent = LocationPickerActivity.Builder()
                    .withLocation(DEMO_LATITUDE, DEMO_LONGITUDE)
                    .withMapStyle(R.raw.map_style_retro)
                    .build(applicationContext)
            startActivityForResult(locationPickerIntent, MAP_POIS_BUTTON_REQUEST_CODE)
        }

        initializeLocationPickerTracker()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK && data != null) {
            Log.d("RESULT****", "OK")
            if (requestCode == 1) {
                val latitude = data.getDoubleExtra(LATITUDE, 0.0)
                Log.d("LATITUDE****", latitude.toString())
                val longitude = data.getDoubleExtra(LONGITUDE, 0.0)
                Log.d("LONGITUDE****", longitude.toString())
                val address = data.getStringExtra(LOCATION_ADDRESS)
                Log.d("ADDRESS****", address.toString())
                val postalcode = data.getStringExtra(ZIPCODE)
                Log.d("POSTALCODE****", postalcode.toString())
                val bundle = data.getBundleExtra(TRANSITION_BUNDLE)
                Log.d("BUNDLE TEXT****", bundle?.getString("test").toString())
                val fullAddress = data.getParcelableExtra<Address>(ADDRESS)
                if (fullAddress != null) {
                    Log.d("FULL ADDRESS****", fullAddress.toString())
                }
                val timeZoneId = data.getStringExtra(TIME_ZONE_ID)
                if (timeZoneId != null) {
                    Log.d("TIME ZONE ID****", timeZoneId)
                }
                val timeZoneDisplayName = data.getStringExtra(TIME_ZONE_DISPLAY_NAME)
                if (timeZoneDisplayName != null) {
                    Log.d("TIME ZONE NAME****", timeZoneDisplayName)
                }
            } else if (requestCode == 2) {
                val latitude = data.getDoubleExtra(LATITUDE, 0.0)
                Log.d("LATITUDE****", latitude.toString())
                val longitude = data.getDoubleExtra(LONGITUDE, 0.0)
                Log.d("LONGITUDE****", longitude.toString())
                val address = data.getStringExtra(LOCATION_ADDRESS)
                Log.d("ADDRESS****", address.toString())
                val lekuPoi = data.getParcelableExtra<LekuPoi>(LEKU_POI)
                Log.d("LekuPoi****", lekuPoi.toString())
            }
        }
        if (resultCode == Activity.RESULT_CANCELED) {
            Log.d("RESULT****", "CANCELLED")
        }
    }

    private fun initializeLocationPickerTracker() {
        LocationPicker.setTracker(MyPickerTracker(this))
    }

    private class MyPickerTracker(private val context: Context) : LocationPickerTracker {
        override fun onEventTracked(event: TrackEvents) {
            Toast.makeText(context, "Event: " + event.eventName, Toast.LENGTH_SHORT).show()
        }
    }
}
