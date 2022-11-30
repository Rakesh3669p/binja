package com.app.fuse.utils

class Constants {
    companion object{

            /*******************************Base URL**********************************************/
        const val BASE_URL = "http://18.221.67.213:3000"


         /*******************************Socket Server Path**********************************************/
             const val SOCKET_SERVER_PATH = "http://18.221.67.213:3000/?token="

        // Request Type
        const val Application_Type = "application/json; charset=utf-8"

        //Request code
        const val REQUEST_CODE_CROP = 102
        const val REQUEST_CODE_IMAGE = 10
        const val REQUEST_CODE_LOCATION = 101
        const val REQUEST_CODE_NOTIFICATION = 1

    }
}