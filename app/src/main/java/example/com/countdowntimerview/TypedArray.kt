package example.com.countdowntimerview

import android.content.res.TypedArray
import androidx.annotation.StyleableRes

fun TypedArray.getResourceIdOrNull(@StyleableRes index: Int): Int? = if (hasValue(index)) {
    getResourceId(index, 0)
} else {
    null
}