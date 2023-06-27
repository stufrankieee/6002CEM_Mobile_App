package com.coventry.hkqipao

//import com.coventry.hkqipao.network.MyState
//import com.coventry.hkqipao.network.NetworkStatusTracker
//import com.coventry.hkqipao.network.NetworkStatusViewModel
import NetworkStatusListener
import NetworkStatusTracker
import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import com.coventry.hkqipao.databinding.ActivityMainBinding
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.snackbar.Snackbar

class MainActivity : AppCompatActivity(), NetworkStatusListener {

    companion object {
        private const val TAG = "MainActivity"
    }

    private lateinit var binding: ActivityMainBinding
    private lateinit var navView: BottomNavigationView
    private var networkUnavailableSnackbar: Snackbar? = null

    private lateinit var networkStatusTracker: NetworkStatusTracker

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        navView = binding.navView

        supportActionBar?.hide()

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
        Log.d(TAG, "onResume called")
        startNetworkStatusTracking()
    }

    override fun onDestroy() {
        super.onDestroy()
        networkStatusTracker.stopListening()
    }

    private fun showNetworkUnavailableSnackbar() {
        // Show the Snackbar with the CoordinatorLayout as the parent view
        networkUnavailableSnackbar = Snackbar.make(
            binding.container,
            "Network is not available",
            Snackbar.LENGTH_INDEFINITE
        )
        networkUnavailableSnackbar?.setAnchorView(navView)?.show()
    }

    private fun hideNetworkUnavailableSnackbar() {
        networkUnavailableSnackbar?.duration = Snackbar.LENGTH_SHORT
        networkUnavailableSnackbar?.dismiss()
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

    fun getBottomNavigationView(): BottomNavigationView {
        return navView
    }
}