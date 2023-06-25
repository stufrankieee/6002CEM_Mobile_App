import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.net.ConnectivityManager
import android.net.Network
import android.net.NetworkCapabilities
import android.net.NetworkRequest
import android.os.Build
import android.provider.Settings

interface NetworkStatusListener {
    fun onNetworkStatusChanged(isOnline: Boolean)
}

class NetworkStatusTracker(private val context: Context) {
    private var isTracking = false
    private var networkStatusListener: NetworkStatusListener? = null
    private var airplaneModeReceiver: BroadcastReceiver? = null

    fun startListening(networkStatusListener: NetworkStatusListener) {
        if (isTracking) {
            return
        }

        this.networkStatusListener = networkStatusListener
        registerAirplaneModeReceiver()
        registerNetworkCallback()

        isTracking = true
    }

    fun stopListening() {
        if (!isTracking) {
            return
        }

        networkStatusListener = null
        unregisterAirplaneModeReceiver()
        unregisterNetworkCallback()

        isTracking = false
    }

    private fun registerAirplaneModeReceiver() {
        val intentFilter = IntentFilter(Intent.ACTION_AIRPLANE_MODE_CHANGED)
        val receiver = AirplaneModeReceiver(networkStatusListener!!)
        context.registerReceiver(receiver, intentFilter)
        airplaneModeReceiver = receiver
    }

    private fun unregisterAirplaneModeReceiver() {
        airplaneModeReceiver?.let { context.unregisterReceiver(it) }
        airplaneModeReceiver = null
    }

    private fun registerNetworkCallback() {
        val connectivityManager =
            context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager

        val networkRequest = NetworkRequest.Builder()
            .addTransportType(NetworkCapabilities.TRANSPORT_CELLULAR)
            .addTransportType(NetworkCapabilities.TRANSPORT_WIFI)
            .build()

        connectivityManager.registerNetworkCallback(networkRequest, networkCallback)
    }

    private fun unregisterNetworkCallback() {
        val connectivityManager =
            context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        connectivityManager.unregisterNetworkCallback(networkCallback)
    }

    private val networkCallback = object : ConnectivityManager.NetworkCallback() {
        override fun onAvailable(network: Network) {
            networkStatusListener?.onNetworkStatusChanged(true)
        }

        override fun onLost(network: Network) {
            networkStatusListener?.onNetworkStatusChanged(false)
        }
    }

    private class AirplaneModeReceiver(private val listener: NetworkStatusListener) :
        BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            if (intent.action == Intent.ACTION_AIRPLANE_MODE_CHANGED) {
                val isAirplaneModeOn = isAirplaneModeOn(context)
                listener.onNetworkStatusChanged(!isAirplaneModeOn)
            }
        }

        private fun isAirplaneModeOn(context: Context): Boolean {
            return if (Build.VERSION.SDK_INT < Build.VERSION_CODES.JELLY_BEAN_MR1) {
                Settings.System.getInt(
                    context.contentResolver,
                    Settings.System.AIRPLANE_MODE_ON,
                    0
                ) != 0
            } else {
                Settings.Global.getInt(
                    context.contentResolver,
                    Settings.Global.AIRPLANE_MODE_ON,
                    0
                ) != 0
            }
        }
    }
}