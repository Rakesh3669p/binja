package com.app.fuse

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.content.IntentFilter
import android.content.IntentSender
import android.location.LocationManager
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.*
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.NavController
import androidx.navigation.findNavController
import androidx.navigation.ui.setupWithNavController
import com.app.fuse.databinding.ActivityMainBinding
import com.app.fuse.db.BinjaDatabase
import com.app.fuse.network.BinjaRepository
import com.app.fuse.service.GPSModeChangedReceiver
import com.app.fuse.ui.chatmodule.ChatHistoryViewModel
import com.app.fuse.ui.chatmodule.ChatHistoryViewModelFactory
import com.app.fuse.utils.*
import com.app.fuse.utils.Constants.Companion.REQUEST_CODE_LOCATION
import com.google.android.gms.ads.*
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.location.*
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.messaging.FirebaseMessaging
import io.socket.client.Socket
import pub.devrel.easypermissions.AppSettingsDialog
import pub.devrel.easypermissions.EasyPermissions


@Suppress("DEPRECATION")
class MainActivity : AppCompatActivity(), EasyPermissions.PermissionCallbacks {
    private lateinit var binding: ActivityMainBinding

    lateinit var session: SessionManager
    lateinit var navController: NavController
    lateinit var chatViewModel: ChatHistoryViewModel

    private var mSocket: Socket? = null

    lateinit var locationRequest: LocationRequest
    var permissionToRequest = mutableListOf<String>()
    lateinit var fusedLocationProviderClient: FusedLocationProviderClient
    lateinit var gpsReceiver: GPSModeChangedReceiver

    lateinit var app: BinjaApplication

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        MobileAds.initialize(this) {}
        init()
        SocketConnection()
        setUpBannerAd()
        GetFCM()
    }

    private fun SocketConnection() {
        SocketManager.instance!!.connectSocket(session.token!!, Constants.BASE_URL)
        mSocket = SocketManager.instance!!.getSocket()
        mSocket!!.connect()
    }


    private fun setUpBannerAd() {
        /*val adView = AdView(this@MainActivity)
        adView.adSize = AdSize.BANNER
        adView.adUnitId = getString(com.app.fuse.R.string.banner_ad_key)
        val adRequest = AdRequest.Builder().build()
        binding.bannerAd.loadAd(adRequest)*/

    }


    private fun init() {
        session = SessionManager(applicationContext)
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(applicationContext)
        binding.bottomNavigationView.visibility = View.GONE
        locationRequest = LocationRequest.create()
        locationRequest.priority = LocationRequest.PRIORITY_HIGH_ACCURACY
        locationRequest.interval = 500
        locationRequest.fastestInterval = 100
        locationRequest.numUpdates = 1
        gpsReceiver = GPSModeChangedReceiver()
        checkLocationEnabled()
    }


    private fun setGPSBroadCast() {
        IntentFilter(LocationManager.PROVIDERS_CHANGED_ACTION).also {
            registerReceiver(gpsReceiver, it)
        }
    }

    fun GetFCM() {
        FirebaseMessaging.getInstance().token.addOnCompleteListener(OnCompleteListener { task ->
            if (!task.isSuccessful) {
                Log.d(
                    "TAG", "Fetching FCM registration token failed", task.exception
                )
                return@OnCompleteListener
            }

            val token = task.result
            println("fcmToken $token")
            session.fcmToken = token

        })

    }

    private fun checkLocationEnabled() {
        if (isPermissionsGiven(applicationContext)) {
            if (!isLocationEnabled(applicationContext)) {
                enableLocation()
            } else {
                GetLocation()
                Handler().postDelayed({
                    setBottomNavigation()
                }, 2000)

            }
        } else {
            requestPermission()
        }
    }


    @SuppressLint("MissingPermission")
    private fun GetLocation() {
        val locationCallback = object : LocationCallback() {
            override fun onLocationResult(p0: LocationResult) {
                p0.let { super.onLocationResult(it) }
                val locations = p0.locations
                session.latitude = locations[0].latitude.toString()
                session.longitude = locations[0].longitude.toString()
            }
        }

        fusedLocationProviderClient.lastLocation.addOnCompleteListener { task ->
            if (task.isSuccessful && task.result != null) {
                val location = task.result
                session.latitude = location!!.latitude.toString()
                session.longitude = location.longitude.toString()
            } else {
                fusedLocationProviderClient.requestLocationUpdates(
                    locationRequest,
                    locationCallback,
                    Looper.getMainLooper()
                )
            }
        }
    }


    private fun requestPermission() {
        permissionToRequest.add(Manifest.permission.ACCESS_FINE_LOCATION)
        permissionToRequest.add(Manifest.permission.ACCESS_COARSE_LOCATION)
        permissionToRequest.add(Manifest.permission.CAMERA)
        permissionToRequest.add(Manifest.permission.READ_EXTERNAL_STORAGE)
        permissionToRequest.add(Manifest.permission.WRITE_EXTERNAL_STORAGE)
        EasyPermissions.requestPermissions(
            this,
            "Allow this Permission to serve you better", REQUEST_CODE_LOCATION, *permissionToRequest.toTypedArray()
        )
    }

    private fun enableLocation() {
        val builder = LocationSettingsRequest.Builder().addLocationRequest(locationRequest)
        builder.setAlwaysShow(true)
        val result = LocationServices.getSettingsClient(applicationContext)
            .checkLocationSettings(builder.build())

        result.addOnCompleteListener { task ->
            try {
                task.getResult(ApiException::class.java)
            } catch (e: ApiException) {
                when (e.statusCode) {
                    LocationSettingsStatusCodes.RESOLUTION_REQUIRED -> {
                        try {
                            e.status.startResolutionForResult(this, REQUEST_CODE_LOCATION)

                        } catch (ex: IntentSender.SendIntentException) {
                            ex.printStackTrace()
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
        if (!isPermissionsGiven(applicationContext)) {
            EasyPermissions.onRequestPermissionsResult(
                requestCode,
                permissions,
                grantResults,
                this
            )
        } else {
            checkLocationEnabled()
        }

    }

    override fun onPermissionsGranted(requestCode: Int, perms: MutableList<String>) {

    }

    override fun onPermissionsDenied(requestCode: Int, perms: MutableList<String>) {
        if (EasyPermissions.somePermissionPermanentlyDenied(this, perms)) {
            AppSettingsDialog.Builder(this).build().show()
        } else {
            checkLocationEnabled()
        }
    }

    private fun setBottomNavigation() {
        binding.bottomNavigationView.itemIconTintList = null
        navController = findNavController(com.app.fuse.R.id.navHostFragment)


        binding.bottomNavigationView.setupWithNavController(navController)
        if (session.isLoggedIn) {
            val graph = navController.navInflater.inflate(com.app.fuse.R.navigation.navigation_graph)
            graph.setStartDestination(R.id.homeMainFragment)
            navController.graph = graph

        } else {
            val graph = navController.navInflater.inflate(com.app.fuse.R.navigation.navigation_graph)
            graph.setStartDestination(R.id.welcome)
            navController.graph = graph
        }


        navController.addOnDestinationChangedListener { _, destination, _ ->
            setStatusBarColor(ContextCompat.getColor(this, com.app.fuse.R.color.purple_lite))
            if (session.isFromGameRequestScreen!!) {
                session.isFromGameRequestScreen=false
                navController.navigate(com.app.fuse.R.id.messagesFragment)
            } else
                if (destination.id == R.id.homeMainFragment ||
                    destination.id == com.app.fuse.R.id.searchFragment ||
                    destination.id == com.app.fuse.R.id.nearByFriendsFragment ||
                    destination.id == com.app.fuse.R.id.messagesFragment ||
                    destination.id == com.app.fuse.R.id.profileFragment

                ) {
                    binding.bottomNavigationView.visibility = View.VISIBLE
                    binding.bannerAd.visibility = View.VISIBLE

                } else {
                    binding.bottomNavigationView.visibility = View.GONE
                    binding.bannerAd.visibility = View.GONE

                }


        }
    }

    private fun setStatusBarColor(color: Int) {
        window.apply {
            addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
            statusBarColor = color
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        return super.onSupportNavigateUp()
    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        when (resultCode) {

            Activity.RESULT_OK -> {
                if (REQUEST_CODE_LOCATION == requestCode) {
                    session.isLocationEnabled = true
                    checkLocationEnabled()
                }
            }

            Activity.RESULT_CANCELED -> {
                if (REQUEST_CODE_LOCATION == requestCode) {
                    session.isLocationEnabled = false
                    checkLocationEnabled()
                }
            }
        }
    }

    override fun onResume() {
        super.onResume()
        moveToConversationFragment()
        setCount()
    }

    private fun moveToConversationFragment() {
        if (session.isFromChatScreen!! || session.isFromGameRequestScreen!!) {
            session.isFromChatScreen = false
            session.isFromGameRequestScreen = false
            navController.navigate(com.app.fuse.R.id.messagesFragment)
        }
    }

    private fun setCount() {
        val repository = BinjaRepository(BinjaDatabase(this))
        val chatViewModelFactory = ChatHistoryViewModelFactory(this.application, repository)
        chatViewModel =
            ViewModelProvider(this, chatViewModelFactory).get(ChatHistoryViewModel::class.java)

        chatViewModel.ChatCount(this)
        chatViewModel.chatCount.observe(this, androidx.lifecycle.Observer { response ->
            when (response) {
                is Resources.Success -> {
                    session.messageCount = response.data!!.data.unReadTotal
                    if (session.messageCount == 0) {
                        binding.bottomNavigationView.getOrCreateBadge(com.app.fuse.R.id.messagesFragment).apply {
                            isVisible = false
                        }
                    } else {
                        binding.bottomNavigationView.getOrCreateBadge(com.app.fuse.R.id.messagesFragment).apply {
                            number = session.messageCount!!
                            isVisible = true
                        }
                    }
                }
                is Resources.Loading -> { }
                is Resources.Error -> { }
            }
        })

    }


    override fun onAttachFragment(fragment: Fragment) {
        super.onAttachFragment(fragment)
        setCount()
    }

}

