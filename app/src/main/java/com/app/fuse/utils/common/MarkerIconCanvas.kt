package com.app.fuse.utils.common

import android.app.Activity
import android.graphics.*
import androidx.core.content.ContextCompat
import com.app.fuse.R


fun getCroppedBitmap(bitmap: Bitmap): Bitmap? {

    val output = Bitmap.createBitmap(
        bitmap.width,
        bitmap.height, Bitmap.Config.ARGB_8888
    )
    val canvas = Canvas(output)

    val paint = Paint().apply {
        isAntiAlias = true
        style = Paint.Style.FILL_AND_STROKE
        strokeWidth = 2F
        color = Color.WHITE


    }
    val rect = Rect(0, 0, bitmap.width, bitmap.height)
    canvas.drawARGB(0, 0, 0, 0)


    canvas.drawCircle(
        (bitmap.width / 2).toFloat(), (bitmap.height / 2).toFloat(),
        (bitmap.width / 2).toFloat(), paint
    )
    paint.xfermode = PorterDuffXfermode(PorterDuff.Mode.SRC_IN)
    canvas.drawBitmap(bitmap, rect, rect, paint)
    return output
}

fun addWhiteBorder(activity: Activity, bmp: Bitmap, borderSize: Float): Bitmap {
    val bmpWithBorder = Bitmap.createBitmap(
        bmp.width + borderSize.toInt() * 2,
        bmp.height + borderSize.toInt() * 2,
        bmp.config
    )
    val canvas = Canvas(bmpWithBorder)
    canvas.drawBitmap(bmp, borderSize, borderSize, null)
    return bmpWithBorder
}

fun addShadowToCircularBitmap(activity: Activity, srcBitmap: Bitmap, shadowWidth: Float): Bitmap? {
    // Calculate the circular bitmap width with shadow
    val dstBitmapWidth = (srcBitmap.width + shadowWidth * 2).toInt()
    val dstBitmap = Bitmap.createBitmap(dstBitmapWidth, dstBitmapWidth, Bitmap.Config.ARGB_8888)

    // Initialize a new Canvas instance
    val canvas = Canvas(dstBitmap)
    canvas.drawBitmap(srcBitmap, shadowWidth, shadowWidth, null)

    // Paint to draw circular bitmap shadow
    val paint = Paint()
    paint.color = ContextCompat.getColor(activity!!.applicationContext, R.color.red_lite)
    paint.style = Paint.Style.FILL_AND_STROKE
    paint.strokeWidth = 1F
    paint.isAntiAlias = true
    paint.maskFilter = BlurMaskFilter(shadowWidth, BlurMaskFilter.Blur.OUTER)

    // Draw the shadow around circular bitmap
    canvas.drawCircle(
        (dstBitmapWidth / 2).toFloat(),  // cx
        (dstBitmapWidth / 2).toFloat(),  // cy
        (dstBitmapWidth / 2 - shadowWidth),  // Radius
        paint // Paint
    )

    // Return the circular bitmap with shadow
    return dstBitmap
}
