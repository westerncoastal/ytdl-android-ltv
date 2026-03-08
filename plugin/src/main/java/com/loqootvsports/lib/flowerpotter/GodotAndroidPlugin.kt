package com.loqootvsports.lib.flowerpotter

import android.util.Log
import com.yausername.youtubedl_android.YoutubeDL
import com.yausername.youtubedl_android.YoutubeDLRequest
import com.yausername.ffmpeg.FFmpeg
import org.godotengine.godot.Godot
import org.godotengine.godot.plugin.GodotPlugin
import org.godotengine.godot.plugin.UsedByGodot
import org.godotengine.godot.plugin.SignalInfo

class GodotAndroidPlugin(godot: Godot): GodotPlugin(godot) {

    override fun getPluginName() = BuildConfig.GODOT_PLUGIN_NAME

    // 1. Define signals so Godot can receive progress updates
    override fun getPluginSignals(): MutableSet<SignalInfo> {
        return mutableSetOf(
            SignalInfo("download_progress", Float::class.javaObjectType, Long::class.javaObjectType),
            SignalInfo("download_completed", String::class.java),
            SignalInfo("download_error", String::class.java)
        )
    }

    // 2. Initialize the library (Call this from Godot's _ready)
    @UsedByGodot
    fun initDownloader(): Boolean {
        return try {
            YoutubeDL.getInstance().init(godot.context)
            FFmpeg.getInstance().init(godot.context)
            true
        } catch (e: Exception) {
            Log.e(pluginName, "Init failed: ${e.message}")
            false
        }
    }

    // 3. The actual download function
    @UsedByGodot
    fun startDownload(url: String, savePath: String) {
        val request = YoutubeDLRequest(url)
        request.addOption("-o", "$savePath/%(title)s.%(ext)s")
        
        // Execute in a background thread so the game doesn't freeze
        Thread {
            try {
                YoutubeDL.getInstance().execute(request) { progress, eta ->
                    emitSignal("download_progress", progress, eta)
                }
                emitSignal("download_completed", url)
            } catch (e: Exception) {
                emitSignal("download_error", e.message ?: "Unknown Error")
            }
        }.start()
    }
}
