package r2.adam.bashful.pac

import android.content.Context
import android.content.res.Resources
import androidx.annotation.DrawableRes
import androidx.vectordrawable.graphics.drawable.VectorDrawableCompat

/**
 * Created by Alexander Kolpakov on 11/4/2018
 */
fun Context.getVectorDrawable(@DrawableRes resId: Int): VectorDrawableCompat? {
    return try {
        VectorDrawableCompat.create(resources, resId, null)
    } catch (e: Resources.NotFoundException) {
        null
    }
}

fun lerp(a: Float, b: Float, t: Float): Float {
    return a + (b - a) * t
}

fun Int.mid(): Int = this / 2

fun Float.mid(): Float = this / 2f

typealias UnitCallback = () -> Unit