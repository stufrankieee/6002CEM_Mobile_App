package com.coventry.hkqipao

import NetworkStatusListener
import NetworkStatusTracker
import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.coordinatorlayout.widget.CoordinatorLayout
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import com.coventry.hkqipao.databinding.ActivityMainBinding
//import com.coventry.hkqipao.network.MyState
//import com.coventry.hkqipao.network.NetworkStatusTracker
//import com.coventry.hkqipao.network.NetworkStatusViewModel
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.snackbar.Snackbar


class MainActivity : AppCompatActivity(), NetworkStatusListener {

    private lateinit var binding: ActivityMainBinding
    private lateinit var navView: BottomNavigationView
    private var informationSnackbar: Snackbar? = null

//    private val viewModel: NetworkStatusViewModel by lazy {
//        ViewModelProvider(
//            this,
//            object : ViewModelProvider.Factory {
//                override fun <T : ViewModel> create(modelClass: Class<T>): T {
//                    val networkStatusTracker = NetworkStatusTracker(this@MainActivity)
//                    return NetworkStatusViewModel(networkStatusTracker) as T
//                }
//            },
//        ).get(NetworkStatusViewModel::class.java)
//    }

    private lateinit var networkStatusTracker: NetworkStatusTracker

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        navView = binding.navView

        startNetworkStatusTracking()

        val navController = findNavController(R.id.nav_host_fragment_activity_main)
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        val appBarConfiguration = AppBarConfiguration(
            setOf(
                R.id.navigation_explore, R.id.navigation_reservation, R.id.navigation_profile
            )
        )

        setupActionBarWithNavController(navController, appBarConfiguration)
        navView.setupWithNavController(navController)
    }

    override fun onNetworkStatusChanged(isOnline: Boolean) {
        if (isOnline) {
            hideNetworkUnavailableSnackbar()
        } else {
            showNetworkUnavailableSnackbar()
        }
    }

    override fun onResume() {
        super.onResume()
        startNetworkStatusTracking()
    }

    override fun onDestroy() {
        super.onDestroy()
        networkStatusTracker.stopListening()
    }

    private fun showNetworkUnavailableSnackbar() {
        // Show the Snackbar with the CoordinatorLayout as the parent view
        informationSnackbar = Snackbar.make(
            binding.container,
            "Network is not available",
            Snackbar.LENGTH_INDEFINITE
        )
        informationSnackbar?.setAnchorView(navView)?.show()
    }

    private fun hideNetworkUnavailableSnackbar() {
        informationSnackbar?.duration = Snackbar.LENGTH_SHORT
        informationSnackbar?.dismiss()
    }

    private fun startNetworkStatusTracking() {
        networkStatusTracker = NetworkStatusTracker(this)
        networkStatusTracker.startListening(this)

        // Check initial network status
        val connectivityManager =
            getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val networkCapabilities = connectivityManager.activeNetwork?.let {
            connectivityManager.getNetworkCapabilities(it)
        }
        val isOnline = networkCapabilities != null &&
                networkCapabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)

        // Call onNetworkStatusChanged with the initial network status
        onNetworkStatusChanged(isOnline)
    }
}