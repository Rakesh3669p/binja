/*
package com.app.fuse.db

import androidx.room.TypeConverter
import com.app.fuse.ui.MainScreen.home.FriendsList.model.FriendDetails
import com.app.fuse.ui.MainScreen.search.model.RequestData
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import java.util.*

class Converters {

    var gson = Gson()

    @TypeConverter
    fun foodItemToString(foodItems:FriendDetails): String {
        return gson.toJson(foodItems)
    }

    @TypeConverter
    fun stringToFoodItem(data: String): FriendDetails {
        val listType = object : TypeToken<FriendDetails>() {
        }.type
        return gson.fromJson(data, listType)
    }


    @TypeConverter
    fun StringToLanguages(value: String): RequestData? {
        val langs: MutableList<Array<String>> = listOf(
            value.split("\\s*,\\s*".toRegex()).toTypedArray()
        ) as MutableList<Array<String>>
        return if(value==null){
            null
        }else{
            RequestData("","")
        }

    }

    @TypeConverter
    fun languagesTString(cl: RequestData): String? {
        return ""
    }
}
*/
