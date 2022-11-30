package com.app.fuse.utils

import android.content.Context
import android.content.SharedPreferences

class SessionManager(var mcxt: Context) {

    companion object {
        val PREF_NAME = " BINJA"
        val PREF_GENERAL = "PREF_GENERAL"
        val KEY_ISLOGIN = "isloggedin"
        val KEY_ACCESS_TOKEN = "accesstoken"
        val KEY_FCM = "firebase_fcm"
        val KEY_IS_VERIFIED = "isverified"
        val KEY_ISLOCATION = "islocation"
        val KEY_LATITUDE = "latitude"
        val KEY_LONGITUDE = "longitude"
        val KEY_USERDATA = "userdata"
        val KEY_CHANNELFILTERDATA = "channelfilterdata"
        val KEY_GENREFILTERDATA = "genrefilterdata"
        val KEY_MESSAGE_COUNT = "messagecount"
        val KEY_USER_ID = "user_id"
        val KEY_CHAT_SCREEN = "chatscreen"
        val KEY_GAME_REQUEST_SCREEN = "chatscreen"
    }

    var loginEditor: SharedPreferences.Editor
    var login_pref: SharedPreferences


    var locationEditor: SharedPreferences.Editor
    var locationPref: SharedPreferences

    var generalEditor: SharedPreferences.Editor
    var generalPref: SharedPreferences

    private var PRIVATE_MODE = 0

    init {

        login_pref = mcxt.getSharedPreferences(PREF_GENERAL, PRIVATE_MODE)
        loginEditor = login_pref.edit()

        locationPref = mcxt.getSharedPreferences(PREF_GENERAL, PRIVATE_MODE)
        locationEditor = locationPref.edit()

        generalPref = mcxt.getSharedPreferences(PREF_GENERAL, PRIVATE_MODE)
        generalEditor = generalPref.edit()

    }


    val isLoggedIn: Boolean
        get() = login_pref.getBoolean(KEY_ISLOGIN, false)

    fun setLogin(isLoggesIN: Boolean) {
        loginEditor.putBoolean(KEY_ISLOGIN, isLoggesIN)
        loginEditor.commit()

    }


    var token: String?
        get() = login_pref.getString(KEY_ACCESS_TOKEN, "")
        set(token) {
            loginEditor.putString(KEY_ACCESS_TOKEN, token)
            loginEditor.commit()
        }


    var messageCount: Int?
        get() = generalPref.getInt(KEY_MESSAGE_COUNT, 0)
        set(count) {
            generalEditor.putInt(KEY_MESSAGE_COUNT, count!!)
            generalEditor.commit()
        }


    var userID: Int?
        get() = generalPref.getInt(KEY_USER_ID, 0)
        set(userId) {
            generalEditor.putInt(KEY_USER_ID, userId!!)
            generalEditor.commit()
        }


    var fcmToken: String?
        get() = login_pref.getString(KEY_FCM, "")
        set(fcmToken) {
            loginEditor.putString(KEY_FCM, fcmToken!!)
            loginEditor.commit()
        }

    var userLoginData: String?
        get() = login_pref.getString(KEY_USERDATA, "")
        set(loginData) {
            loginEditor.putString(KEY_USERDATA, loginData!!)
            loginEditor.commit()
        }

    var isVerified: Boolean?
        get() = login_pref.getBoolean(KEY_IS_VERIFIED, false)
        set(isVerified) {
            loginEditor.putBoolean(KEY_IS_VERIFIED, isVerified!!)
            loginEditor.commit()
        }

  var isFromChatScreen: Boolean?
        get() = generalPref.getBoolean(KEY_CHAT_SCREEN, false)
        set(isFrom) {
            generalEditor.putBoolean(KEY_CHAT_SCREEN, isFrom!!)
            generalEditor.commit()
        }


    var isFromGameRequestScreen: Boolean?
        get() = generalPref.getBoolean(KEY_GAME_REQUEST_SCREEN, false)
        set(isFrom) {
            generalEditor.putBoolean(KEY_GAME_REQUEST_SCREEN, isFrom!!)
            generalEditor.commit()
        }


    var isLocationEnabled: Boolean?
        get() = locationPref.getBoolean(KEY_ISLOCATION, false)
        set(isLocationEnabled) {
            locationEditor.putBoolean(KEY_ISLOCATION, isLocationEnabled!!)
            locationEditor.commit()
        }


    var latitude: String?
        get() = locationPref.getString(KEY_LATITUDE, "")
        set(latitude) {
            locationEditor.putString(KEY_LATITUDE, latitude!!)
            locationEditor.commit()
        }

    var longitude: String?
        get() = locationPref.getString(KEY_LONGITUDE, "")
        set(latitude) {
            locationEditor.putString(KEY_LONGITUDE, latitude)
            locationEditor.commit()
        }

    var channelfilters: Set<String>?
        get() = generalPref.getStringSet(KEY_CHANNELFILTERDATA, emptySet())
        set(channelSet) {
            generalEditor.putStringSet(KEY_CHANNELFILTERDATA, channelSet)
            generalEditor.commit()
        }

    var genrefilters: Set<String>?
        get() = generalPref.getStringSet(KEY_GENREFILTERDATA, emptySet())
        set(genreSet) {
            generalEditor.putStringSet(KEY_GENREFILTERDATA, genreSet)
            generalEditor.commit()
        }
}