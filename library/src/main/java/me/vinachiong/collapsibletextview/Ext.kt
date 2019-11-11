package me.vinachiong.collapsibletextview

import android.content.Context
import androidx.annotation.ColorInt
import androidx.annotation.ColorRes
import androidx.core.content.ContextCompat

/**
 *
 *
 * @author vina.chiong@gmail.com
 * @version v1.0.0
 */
fun Context.compatColor(@ColorRes resId: Int): Int {
    return ContextCompat.getColor(this, resId)
}