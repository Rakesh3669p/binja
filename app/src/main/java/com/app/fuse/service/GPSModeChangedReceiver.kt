package com.app.fuse.service

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.app.fuse.utils.common.showToast

class GPSModeChangedReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context?, intent: Intent?) {
        context!!.showToast(intent!!.type.toString())
        val isGPSEnabled = intent.getBooleanExtra(resultData, false) ?: return
        if(isGPSEnabled){
//                context!!.showToast("GPS Enabled")
        }else{
//            context!!.showToast("GPS Disabled")
        }
    }
}