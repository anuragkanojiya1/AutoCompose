package com.example.autocompose.util

import android.content.Context
import androidx.core.os.ConfigurationCompat

/** Language + region tag used for resource loading (matches `values-xx-rYY` resolution). */
object LocaleConfiguration {

    fun currentLocaleTag(context: Context): String {
        val locales = ConfigurationCompat.getLocales(context.resources.configuration)
        return if (locales.size() == 0) {
            "und"
        } else {
            locales[0]?.toLanguageTag() ?: "und"
        }
    }
}
