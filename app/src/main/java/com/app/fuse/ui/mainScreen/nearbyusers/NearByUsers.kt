package com.app.fuse.ui.mainScreen.nearbyusers

import android.Manifest
import android.annotation.SuppressLint
import android.content.IntentSender
import android.graphics.*
import android.graphics.drawable.BitmapDrawable
import android.media.ThumbnailUtils
import android.os.Bundle
import android.os.Looper
import android.util.Log
import android.view.View
import android.widget.AbsListView
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import coil.ImageLoader
import coil.request.ImageRequest
import coil.request.SuccessResult
import com.app.fuse.R
import com.app.fuse.databinding.FragmentNearByUsersBinding
import com.app.fuse.databinding.ProgressBarBinding
import com.app.fuse.db.BinjaDatabase
import com.app.fuse.network.BinjaRepository
import com.app.fuse.utils.*

import com.app.fuse.ui.mainScreen.nearbyusers.adapter.NearByUsersAdapter
import com.app.fuse.ui.mainScreen.nearbyusers.model.NearByUserData
import com.app.fuse.utils.common.*
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.location.*
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.google.gson.JsonObject
import com.sothree.slidinguppanel.SlidingUpPanelLayout
import kotlinx.coroutines.launch
import pub.devrel.easypermissions.AppSettingsDialog
import pub.devrel.easypermissions.EasyPermissions


@SuppressLint("MissingPermission")
class NearByUsers : Fragment(R.layout.fragment_near_by_users),
    EasyPermissions.PermissionCallbacks {
    private lateinit var binding: FragmentNearByUsersBinding
    private lateinit var progressBarBinding: ProgressBarBinding
    lateinit var session: SessionManager
    private lateinit var mapFragment: SupportMapFragment
    private lateinit var mMap: GoogleMap
    lateinit var locationRequest: LocationRequest
    lateinit var fusedLocationProviderClient: FusedLocationProviderClient

    var latLong: LatLng? = null
    lateinit var bitmap: Bitmap
    lateinit var nearByUsersAdapter: NearByUsersAdapter
    lateinit var nearByUsersViewModel: NearByUsersViewModel
    lateinit var layout: RecyclerView.LayoutManager

    var pageNo = 1
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = FragmentNearByUsersBinding.bind(view)
        progressBarBinding = ProgressBarBinding.bind(view)
        init()
        setLocation()
        setViewModel()
        setRecylce(layout)
        setClickListners()
        setSpanableView()
    }

    override fun onStart() {
        super.onStart()
        checkLocationEnabledOrNot()
    }

    private fun setSpanableView() {
        binding.spanableLayout.setScrollableView(binding.slidableLayout.topPicksRecycle)
        binding.slidableLayout.dragView.isClickable = false
        binding.spanableLayout.addPanelSlideListener(object :
            SlidingUpPanelLayout.PanelSlideListener {
            override fun onPanelSlide(panel: View?, slideOffset: Float) {
                if (slideOffset < 0.9F) {
                    binding.slidableLayout.dragView.background = ContextCompat.getDrawable(requireContext(),R.drawable.bottomsheet_bg)
                } else {
                    binding.slidableLayout.dragView.background =
                        ContextCompat.getDrawable(requireContext(),R.drawable.background_gradient)
                }
                if (slideOffset < 0.20F) {
                    nearByUsersAdapter.getLayoutType(0)
                    layout = LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
                    binding.slidableLayout.topPicksRecycle.layoutManager = layout
                }
                if (slideOffset > 0.20F) {
                    nearByUsersAdapter.getLayoutType(1 )
                    layout = GridLayoutManager(requireContext(), 2)
                    binding.slidableLayout.topPicksRecycle.layoutManager = layout

                }
            }

            override fun onPanelStateChanged(
                panel: View?,
                previousState: SlidingUpPanelLayout.PanelState?,
                newState: SlidingUpPanelLayout.PanelState?
            ) {
                if (newState == SlidingUpPanelLayout.PanelState.COLLAPSED) {
                    binding.slidableLayout.dragView.background = requireContext().getDrawable(R.drawable.bottomsheet_bg)
                } else if (newState == SlidingUpPanelLayout.PanelState.EXPANDED) {
                    binding.slidableLayout.dragView.background =
                        ContextCompat.getDrawable(requireContext(),R.drawable.background_gradient)
                }

            }

        })
    }

    private fun setViewModel() {
        val repository = BinjaRepository(BinjaDatabase(requireContext()))
        val nearByUsersViewModelFactory =
            NearByUsersViewModelFactory(activity!!.application, repository)
        nearByUsersViewModel = ViewModelProvider(this, nearByUsersViewModelFactory).get(
            NearByUsersViewModel::class.java
        )

        val jsonObject = JsonObject()

        jsonObject.addProperty("latitude", session.latitude)
        jsonObject.addProperty("longitude", session.longitude)
        nearByUsersViewModel.NearByUsers(activity!!, session.token!!, jsonObject)


        nearByUsersViewModel.nearByUsers.observe(viewLifecycleOwner, Observer { response ->
            when (response) {
                is Resources.Success -> {
                    if (response.data?.status!!) {
                        binding.progressBar.pgrBarLayout.hide(requireActivity())
                        setMarkers(response.data.data)
                        nearByUsersAdapter.differ.submitList(response?.data.data)

                    } else {

                        requireContext().showToast(response.message.toString())
                    }
                }
                is Resources.Loading -> {
                    binding.progressBar.pgrBarLayout.show(activity = requireActivity())
                }
                is Resources.Error -> {
                    binding.progressBar.pgrBarLayout.hide(requireActivity())
                    requireContext().showToast(response.message.toString())
                }
            }

        })
    }

    private fun setMarkers(userData: List<NearByUserData>) {

        for (markers in userData) {
            val lat = markers.latitude!!.toDouble()
            val long = markers.longitude!!.toDouble()
            latLong = LatLng(lat, long)
            val currentLocation = latLong

            lifecycleScope.launch {
                val thumbnail = if (markers.profile != null) {
                    ThumbnailUtils.extractThumbnail(
                        getCroppedBitmap(LoadBitmap(markers.profile)),
                        60,
                        60
                    )
                } else {
                    ThumbnailUtils.extractThumbnail(getCroppedBitmap(bitmap), 60, 60)
                }


                val border = addWhiteBorder(activity!!, thumbnail, 3F)
                val shadow = addShadowToCircularBitmap(activity!!, border, 16F)
                mMap.addMarker(
                    MarkerOptions().position(currentLocation!!).title("${markers.username}")
                        .snippet("Age: ${markers.age}")
                        .icon(BitmapDescriptorFactory.fromBitmap(shadow))
                )
            }


        }
    }

    private suspend fun LoadBitmap(profile: String?): Bitmap {
        val loading = ImageLoader(requireContext())
        val request: ImageRequest = ImageRequest.Builder(requireContext())
            .data(profile).allowHardware(false)
            .build()
        val result = (loading.execute(request) as SuccessResult).drawable
        return (result as BitmapDrawable).bitmap
    }

    private fun setClickListners() {

    }

    private fun init() {
        session = SessionManager(requireContext())
        nearByUsersAdapter = NearByUsersAdapter()
        layout = LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
        val drawable = ContextCompat.getDrawable(requireContext(),R.drawable.user)
        bitmap = (drawable as BitmapDrawable).bitmap

    }

    private fun setLocation() {
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(requireContext())
        locationRequest = LocationRequest.create()
        locationRequest.priority = LocationRequest.PRIORITY_HIGH_ACCURACY
        locationRequest.interval = 500
        locationRequest.fastestInterval = 100
        locationRequest.numUpdates = 1
    }

    private fun setRecylce(layout: RecyclerView.LayoutManager) {
        binding.slidableLayout.topPicksRecycle.apply {
            setHasFixedSize(true)
            layoutManager = layout
            adapter = nearByUsersAdapter
            nearByUsersAdapter.notifyDataSetChanged()
            addOnScrollListener(scrollListner)
        }
    }


    private fun checkLocationEnabledOrNot() {
        if (session.latitude!!.isNotEmpty() && session.longitude!!.isNotEmpty()) {
            setMap()
        } else {
            if (isPermissionsGiven(activity!!.applicationContext)) {
                if (!isLocationEnabled(activity!!.applicationContext)) {
                    setLocationEnable()
                } else {
                    GetLastLocation()
                }
            } else {
                RequestPermission()
            }
        }

    }


    private fun RequestPermission() {
        var permissionToRequest = mutableListOf<String>()
        permissionToRequest.add(Manifest.permission.ACCESS_FINE_LOCATION)
        permissionToRequest.add(Manifest.permission.ACCESS_COARSE_LOCATION)
        EasyPermissions.requestPermissions(
            this, "Allow this Permission to Serve you better",
            0, *permissionToRequest.toTypedArray()
        )
    }

    private fun GetLastLocation() {
        val locationCallback = object : LocationCallback() {
            override fun onLocationResult(p0: LocationResult?) {
                super.onLocationResult(p0)
                val locations = p0?.locations
                if (locations != null) {
                    session.latitude = locations[0].latitude.toString()
                    session.longitude = locations[0].longitude.toString()
                    setMap()
                }
            }
        }

        fusedLocationProviderClient.lastLocation.addOnCompleteListener { task ->
            if (task.isSuccessful && task.result != null) {
                val location = task.result
                session.latitude = location!!.latitude.toString()
                session.longitude = location!!.longitude.toString()
                setMap()
            } else {
                fusedLocationProviderClient.requestLocationUpdates(
                    locationRequest,
                    locationCallback!!,
                    Looper.getMainLooper()
                )
            }
        }
    }

    private fun setMap() {
        mapFragment = childFragmentManager.findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync { googleMap ->
            mMap = googleMap
            mMap.isMyLocationEnabled = true
            val lat = session.latitude!!.toDouble()
            val long = session.longitude!!.toDouble()
            latLong = LatLng(lat, long)
            val currentLocation = latLong
            val cameraPosition = CameraPosition.Builder().target(currentLocation!!).zoom(13f).build()
            mMap.moveCamera(CameraUpdateFactory.newCameraPosition(cameraPosition))
        }
    }

    private fun setLocationEnable() {
        val builder = LocationSettingsRequest.Builder().addLocationRequest(locationRequest)
        builder.setAlwaysShow(true)
        val result = LocationServices.getSettingsClient(activity!!.applicationContext)
            .checkLocationSettings(builder.build())
        result.addOnCompleteListener { task ->
            try {
                task.getResult(ApiException::class.java)
            } catch (e: ApiException) {
                when (e.statusCode) {
                    LocationSettingsStatusCodes.RESOLUTION_REQUIRED -> {
                        try {
                            e.status.startResolutionForResult(activity, 0)
                        } catch (ex: IntentSender.SendIntentException) {
                        }

                    }
                    LocationSettingsStatusCodes.SETTINGS_CHANGE_UNAVAILABLE -> {
                    }
                }

            }
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int, permissions: Array<out String>, grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (!isPermissionsGiven(activity!!.applicationContext)) {
            EasyPermissions.onRequestPermissionsResult(requestCode, permissions, grantResults, this)
        }

    }

    override fun onPermissionsGranted(requestCode: Int, perms: MutableList<String>) {}

    override fun onPermissionsDenied(requestCode: Int, perms: MutableList<String>) {
        if (EasyPermissions.somePermissionPermanentlyDenied(this, perms)) {
            AppSettingsDialog.Builder(this).build().show()
        }
    }



    fun dptoPx(dp: Int): Int = (dp * requireContext().resources.displayMetrics.density).toInt()

    var isLoading = false
    var isScrolling = false

    val scrollListner = object : RecyclerView.OnScrollListener() {
        override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
            super.onScrolled(recyclerView, dx, dy)

            Log.d("Dragging..", "$dy $dx")

            if (dy > 0) {
                val recycleLayoutManager =
                    binding.slidableLayout.topPicksRecycle.layoutManager as LinearLayoutManager
                if (!isLoading) {


                }

            }

        }

        override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
            if (newState == AbsListView.OnScrollListener.SCROLL_STATE_TOUCH_SCROLL) {
                isScrolling = true
            }

        }
    }
}

