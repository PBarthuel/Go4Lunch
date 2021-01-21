package paul.barthuel.go4lunch.ui.map_view

import android.Manifest
import android.annotation.SuppressLint
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import paul.barthuel.go4lunch.injections.ViewModelFactory

class LocalisationFragment : SupportMapFragment(), OnMapReadyCallback {
    private var mViewModel: LocalisationViewModel? = null
    private var googleMap: GoogleMap? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mViewModel = ViewModelProvider(this, ViewModelFactory.getInstance()).get(LocalisationViewModel::class.java)
        mViewModel!!.uiModelsLiveData.observe(this, Observer { lunchMarkers: List<LunchMarker> ->
            for (lunchMarker in lunchMarkers) {
                googleMap!!.addMarker(
                        MarkerOptions().position(
                                LatLng(
                                        lunchMarker.latitude,
                                        lunchMarker.longitude
                                )
                        ).icon(BitmapDescriptorFactory.defaultMarker(lunchMarker.backGroundColor.toFloat()))
                                .title(lunchMarker.name)
                )
            }
        })
    }

    override fun onActivityCreated(bundle: Bundle?) {
        super.onActivityCreated(bundle)
        getMapAsync(this)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        mViewModel!!.hasPermissions(checkPermissions())
    }

    @SuppressLint("MissingPermission")
    override fun onMapReady(map: GoogleMap) {
        googleMap = map
        mViewModel!!.onMapReady()
        val hasLocationPermissions = checkPermissions()
        if (hasLocationPermissions) {
            map.isMyLocationEnabled = true
        }
        mViewModel!!.hasPermissions(hasLocationPermissions)
    }

    private fun checkPermissions(): Boolean {
        return if (ContextCompat.checkSelfPermission(requireContext(),
                        Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            true
        } else {
            requestPermissions()
            false
        }
    }

    private fun requestPermissions() {
        ActivityCompat.requestPermissions(requireActivity(), arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                REQUEST_FINE_LOCATION)
    }

    companion object {
        private const val REQUEST_FINE_LOCATION = 0
        @JvmStatic
        fun newInstance(): LocalisationFragment {
            val args = Bundle()
            val fragment = LocalisationFragment()
            fragment.arguments = args
            return fragment
        }
    }
}