package com.vangeaux.lagrange

import java.io.File

internal object ReleaseApkCache {
    private val versionedApkFile = Regex("^Lagrange-(\\d+\\.\\d+\\.\\d+(?:-ru\\.[1-9]\\d*)?)\\.apk(\\.part)?$")

    fun cleanupInstalledOrOlder(cacheDir: File, installedVersion: String) {
        val installed = installedVersion.takeIf { versionedApkFile.matches("Lagrange-$it.apk") } ?: return
        val updateDirectory = File(cacheDir, "update-apks")
        updateDirectory.listFiles()?.forEach { file ->
            if (!file.isFile) return@forEach
            val version = versionedApkFile.matchEntire(file.name)
                ?.groupValues?.get(1)
                ?: return@forEach
            if (!isNewerReleaseVersion(installed, version)) file.delete()
        }
    }

    inline fun <T> deletePartialOnFailure(partial: File, action: () -> T): T =
        try {
            action()
        } catch (error: Exception) {
            partial.delete()
            throw error
        }

}
