package com.maxrave.simpmusic.utils

object VersionManager {
    private var versionName: String? = "1.0.0"

    fun initialize() {
        if (versionName == null) {
            versionName = "1.0.0"
        }
    }

    fun getVersionName(): String = removeDevSuffix(versionName ?: "1.0.0")

    private fun removeDevSuffix(versionName: String): String {
        return if (versionName.endsWith("-dev")) {
            versionName.replace("-dev", "")
        } else {
            versionName
        }
    }
}
