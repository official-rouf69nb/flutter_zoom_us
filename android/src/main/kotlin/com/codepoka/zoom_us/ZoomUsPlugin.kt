package com.codepoka.zoom_us

import io.flutter.plugin.common.MethodCall
import io.flutter.plugin.common.MethodChannel
import io.flutter.plugin.common.MethodChannel.MethodCallHandler
import io.flutter.plugin.common.MethodChannel.Result
import io.flutter.plugin.common.PluginRegistry.Registrar


class ZoomUsPlugin(registrar: Registrar, channel: MethodChannel) : MethodCallHandler {
  private val sdkController:ZoomSdkController = ZoomSdkController(registrar,channel)

  companion object {
    @JvmStatic
    fun registerWith(registrar: Registrar) {
      val channel = MethodChannel(registrar.messenger(), "zoom_us")
      channel.setMethodCallHandler(ZoomUsPlugin(registrar,channel))
    }
  }

  override fun onMethodCall(call: MethodCall, result: Result) {
    when (call.method) {
        "initSdk" -> {
          result.success(true)
          sdkController.initSdk(call)
        }
        "getPlatformVersion" -> {
          result.success("Android ${android.os.Build.VERSION.RELEASE}")
        }
        else -> {
          result.notImplemented()
        }
    }
  }
}
