package com.app.fuse.utils.common

import android.app.Activity
import android.content.ActivityNotFoundException
import android.content.ContentResolver
import android.content.Context
import android.content.Intent
import android.graphics.*
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.net.Uri
import android.os.Build
import android.os.StrictMode
import android.provider.MediaStore
import android.util.Base64
import android.util.Patterns
import android.view.View
import android.view.WindowManager
import android.view.inputmethod.InputMethodManager
import android.webkit.MimeTypeMap
import android.widget.Toast
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.FileProvider
import androidx.fragment.app.Fragment
import com.app.fuse.R
import com.app.fuse.utils.Constants
import com.bumptech.glide.request.RequestOptions
import kotlinx.android.synthetic.main.activity_main.*
import java.io.ByteArrayOutputStream
import java.io.File

fun Context.showToast(message:String) {
    Toast.makeText(this,message,Toast.LENGTH_SHORT).show()
}

fun String.isEmail(): Boolean = !isBlank() && Patterns.EMAIL_ADDRESS.matcher(this).matches()


fun hideKeyboard(activity: Activity) {
    val imm = activity.getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager
    var view = activity.currentFocus
    if (view == null) {
        view = View(activity)
    }
    imm.hideSoftInputFromWindow(view.windowToken, 0)
}

fun getPlaceHolder():RequestOptions{
    val requestOptions = RequestOptions()
    requestOptions.placeholder(R.drawable.user)
    requestOptions.error(R.drawable.user)
    return requestOptions
}

fun getPlaceHolderSearch():RequestOptions{
    val requestOptions = RequestOptions()
    requestOptions.placeholder(R.drawable.place_holder)
    requestOptions.error(R.drawable.place_holder)
    return requestOptions
}

fun getPlaceHolderMovie():RequestOptions{
    val requestOptions = RequestOptions()
    requestOptions.placeholder(R.drawable.movie_place_holder)
    requestOptions.error(R.drawable.movie_place_holder)
    return requestOptions
}


fun View.makeVisible() {
    this.visibility = View.VISIBLE
}

fun View.makeGone() {
    this.visibility = View.GONE
}

fun showSoftKeyboard(activity: Activity) {
    val imm = activity.getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager
    var view = activity.currentFocus
    if (view == null) {
        view = View(activity)
    }
    imm.showSoftInput(view, 0)
}

fun ConstraintLayout.show(activity: Activity) {
    visibility = View.VISIBLE
    activity?.window?.setFlags(
        WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE,
        WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE
    )
}

fun ConstraintLayout.hide(activity: Activity) {
    visibility = View.GONE
    activity?.window?.clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE)
}

fun View.show(activity: Activity) {
    visibility = View.VISIBLE
}

fun View.hide(activity: Activity) {
    visibility = View.GONE
}




fun hasInternetConnection(activity: Activity): Boolean {
    val connectivityManager =
        activity.application.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
        val activeNetwork = connectivityManager.activeNetwork ?: return false
        val capabilities =
            connectivityManager.getNetworkCapabilities(activeNetwork) ?: return false
        return when {
            capabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) -> true
            capabilities.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) -> true
            capabilities.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET) -> true
            else -> false
        }
    } else {
        connectivityManager.activeNetworkInfo?.run {
            return when (type) {
                ConnectivityManager.TYPE_WIFI -> true
                ConnectivityManager.TYPE_MOBILE -> true
                ConnectivityManager.TYPE_ETHERNET -> true
                else -> false
            }
        }
    }
    return false
}


fun performCrop(picUri: File, f: Fragment) {
    val packageName: String = f.requireContext().applicationContext.packageName
    val authority = "$packageName.fileprovider"
    //  val uri =   FileProvider.getUriForFile(Objects.requireNonNull(getApplicationContext()), BuildConfig.APPLICATION_ID + ".fileprovider", picUri)
    val uri = FileProvider.getUriForFile(f.requireContext(), authority, picUri)
    if (Build.VERSION.SDK_INT >= 24) {
        try {
            val m = StrictMode::class.java.getMethod("disableDeathOnFileUriExposure")
            m.invoke(null)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
    try {
        val cropIntent = Intent("com.android.camera.action.CROP")
        cropIntent.setDataAndType(uri, "image/*")
        cropIntent.putExtra("crop", true)
        cropIntent.putExtra("aspectX", 1)
        cropIntent.putExtra("aspectY", 1)
        cropIntent.putExtra("outputX", 512)
        cropIntent.putExtra("outputY", 512)
        cropIntent.putExtra("return-data", true)
//        cropIntent.putExtra(MediaStore.EXTRA_OUTPUT, uri)
        cropIntent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(picUri))
        cropIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
        cropIntent.addFlags(Intent.FLAG_GRANT_WRITE_URI_PERMISSION)
        f.startActivityForResult(
            Intent.createChooser(
                cropIntent,
                "Complete action using"
            ), Constants.REQUEST_CODE_CROP
        )
    } // respond to users whose devices do not support the crop action
    catch (anfe: ActivityNotFoundException) {
// display an error message
        val errorMessage = "Whoops - your device doesn't support the crop action!"
        val toast = Toast.makeText(f.requireContext(), errorMessage, Toast.LENGTH_SHORT)
        toast.show()
    }
}

fun encode(activity: Activity, filepath: String): String {
    val imgtype = getMimeType(activity, Uri.parse(filepath))
    val bitmap = BitmapFactory.decodeFile(File(filepath).absolutePath)
    val stream = ByteArrayOutputStream()
    bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream)
    val image: ByteArray = stream.toByteArray()
    val imgtobase64: String = Base64.encodeToString(image, 0)
    return imgtype + imgtobase64
}

fun getMimeType(activity: Activity, uri: Uri): String? {
    val extension: String? = if (uri.scheme.equals(ContentResolver.SCHEME_CONTENT)) {
        val mime = MimeTypeMap.getSingleton()
        mime.getExtensionFromMimeType(activity.contentResolver.getType(uri))
    } else {
        MimeTypeMap.getFileExtensionFromUrl(Uri.fromFile(File(uri.path)).toString())
    }
    return "data:image/$extension;base64,";
}

fun dptoPx(context: Context,dp: Int): Int {
    val px: Float = dp * context.resources.displayMetrics.density
    return px.toInt()
}