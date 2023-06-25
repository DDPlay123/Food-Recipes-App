package mai.project.foody.util

import android.util.Log
import mai.project.foody.BuildConfig

object Method {
    /**
     * Logcat
     */
    fun logE(tag: String, message: String, tr: Throwable? = null) {
        if (BuildConfig.DEBUG)
            Log.e(tag, message, tr)
    }

    fun logD(tag: String, message: String) {
        if (BuildConfig.DEBUG)
            Log.d(tag, message)
    }
}