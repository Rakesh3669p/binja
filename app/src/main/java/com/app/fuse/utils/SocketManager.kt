package com.app.fuse.utils

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.net.ConnectivityManager
import android.os.Handler
import android.os.Looper
import android.os.PowerManager
import android.util.Log
import io.socket.client.IO
import io.socket.client.Socket

interface OnSocketConnectionListener {
    fun onSocketEventFailed()
    fun onSocketConnectionStateChange(socketState: Int)
    fun onInternetConnectionStateChange(socketState: Int)
}
class SocketManager {
    private var socket: Socket? = null
    private var onSocketConnectionListenerList: MutableList<OnSocketConnectionListener>? = null

    fun connectSocket(token: String, host: String){
        try {
            if (socket == null) {
                val opts: IO.Options =  IO.Options()
                opts.forceNew = true
                opts.reconnection = true
                opts.reconnectionAttempts = 5
                opts.secure = true
                opts.query = "token=$token"
                Log.d("SocketToken","$token")
                socket = IO.socket(host, opts)
                socket!!.on(Socket.EVENT_CONNECT) {
                    fireSocketStatus(STATE_CONNECTED)
                    Log.d(TAG, "socket connected")

                }.on(Socket.EVENT_CONNECT_ERROR) {
                    Log.e(TAG, "Socket connect error")
                    fireSocketStatus(STATE_DISCONNECTED)
                    socket!!.disconnect()
                }.on(Socket.EVENT_DISCONNECT) {
                    Log.e(TAG, "Socket disconnect event")
                    fireSocketStatus(STATE_DISCONNECTED)
                }.on(Socket.EVENT_CONNECT_ERROR) { args ->
                    try {
                        val error = args[0] as String
                        Log.e("$TAG error EVENT_ERROR ", error)
                        if (error.contains("Unauthorized") && !socket!!.connected()) {
                            if (onSocketConnectionListenerList != null) {
                                for (listener in onSocketConnectionListenerList!!) {
                                    Handler(Looper.getMainLooper())
                                        .post(Runnable { listener.onSocketEventFailed() })
                                }
                            }
                        }
                    } catch (e: Exception) {
                        if (e.message != null) e.message else ""?.let { Log.e(TAG, it) }
                    }
                }.on("Error") { Log.d(TAG, " Error") }
                socket!!.connect()
            } else if (!socket!!.connected()) {
                socket!!.connect()
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private var lastState = -1

    /**
     * Fire socket status intent.
     *
     * @param socketState the socket state
     */
    @Synchronized
    fun fireSocketStatus(socketState: Int) {
        if (onSocketConnectionListenerList != null && lastState != socketState) {
            lastState = socketState
            Handler(Looper.getMainLooper()).post {
                for (listener in onSocketConnectionListenerList!!) {
                        listener.onSocketConnectionStateChange(socketState)
                }
            }
            Handler(Looper.getMainLooper()).postDelayed({ lastState = -1 }, 1000)
        }
    }

    /**
     * Fire internet status intent.
     *
     * @param socketState the socket state
     */
    @Synchronized
    fun fireInternetStatusIntent(socketState: Int) {
        Handler(Looper.getMainLooper()).post {
            if (onSocketConnectionListenerList != null) {
                for (listener in onSocketConnectionListenerList!!) {
                    listener.onInternetConnectionStateChange(socketState)
                }
            }
        }
    }

    /**
     * Gets socket.
     *
     * @return the socket
     */
    fun getSocket(): Socket? {
        return socket
    }

    /**
     * Sets socket.
     *
     * @param socket the socket
     */
    fun setSocket(socket: Socket?) {
        this.socket = socket
    }

    /**
     * Destroy.
     */
    fun destroy() {
        if (socket != null) {
            socket!!.off()
            socket!!.disconnect()
            socket!!.close()
            socket = null
        }
    }


    class NetReceiver : BroadcastReceiver() {
        /**
         * The Tag.
         */
        val TAG = NetReceiver::class.java.simpleName
        override fun onReceive(context: Context, intent: Intent?) {
            val cm = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
            val activeNetwork = cm.activeNetworkInfo
            val isConnected = activeNetwork != null &&
                    activeNetwork.isConnectedOrConnecting
            instance!!.fireInternetStatusIntent(
                if (isConnected) STATE_CONNECTED else STATE_DISCONNECTED
            )
            if (isConnected) {
                if (instance!!.getSocket() != null
                    && !instance!!.getSocket()!!.connected()
                ) {
                    instance!!.fireSocketStatus(STATE_CONNECTING)
                }
                val powerManager = context.getSystemService(Context.POWER_SERVICE) as PowerManager
                val isScreenOn: Boolean = powerManager.isInteractive
                if (isScreenOn && instance!!.getSocket() != null) {
                    Log.d(TAG, "NetReceiver: Connecting Socket")
                    if (!instance!!.getSocket()!!.connected()) {
                        instance!!.getSocket()!!.connect()
                    }
                }
            } else {
                instance!!.fireSocketStatus(STATE_DISCONNECTED)
                if (instance!!.getSocket() != null) {
                    Log.d(TAG, "NetReceiver: disconnecting socket")
                    instance!!.getSocket()!!.disconnect()
                }
            }
        }
    }

    companion object {
        /**
         * The constant STATE_CONNECTING.
         */
        const val STATE_CONNECTING = 1

        /**
         * The constant STATE_CONNECTED.
         */
        const val STATE_CONNECTED = 2

        /**
         * The constant STATE_DISCONNECTED.
         */
        const val STATE_DISCONNECTED = 3

        /**
         * The constant CONNECTING.
         */
        const val CONNECTING = "Connecting"

        /**
         * The constant CONNECTED.
         */
        const val CONNECTED = "Connected"

        /**
         * The constant DISCONNECTED.
         */
        const val DISCONNECTED = "Disconnected"

        /**
         * Gets instance.
         *
         * @return the instance
         */
        @get:Synchronized
        var instance: SocketManager? = null
            get() {
                if (field == null) {
                    field = SocketManager()
                }
                return field
            }
            private set

        /**
         * The constant TAG.
         */
        val TAG = SocketManager::class.java.simpleName
    }
}