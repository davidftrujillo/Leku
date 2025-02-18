package com.adevinta.leku.geocoder

import android.location.Address
import com.google.android.gms.maps.model.LatLng
import io.reactivex.rxjava3.core.Observable

interface GeocoderInteractorDataSource {
    fun getFromLocationName(query: String): Observable<List<Address>>

    fun getFromLocationName(query: String, lowerLeft: LatLng, upperRight: LatLng): Observable<List<Address>>

    fun getFromLocation(latitude: Double, longitude: Double): Observable<List<Address>>
}
