package com.codepoka.zoom_us

import io.flutter.plugin.common.EventChannel
import io.flutter.plugin.common.MethodCall
import io.flutter.plugin.common.MethodChannel
import io.flutter.plugin.common.MethodChannel.MethodCallHandler
import io.flutter.plugin.common.MethodChannel.Result
import io.flutter.plugin.common.PluginRegistry.Registrar


class ZoomUsPlugin(registrar: Registrar,
                   initSdkEventChannel: EventChannel,
                   joinWithEventChannel: EventChannel,
                   signInWithZoomEventChannel: EventChannel,
                   hostInstantMeetingEventChannel: EventChannel
) : MethodCallHandler{
  private val sdkController:ZoomSdkController = ZoomSdkController(registrar)

  companion object {
    @JvmStatic
    fun registerWith(registrar: Registrar) {
      val channel = MethodChannel(registrar.messenger(), "com.codepoka.zoom_us")
      val initSdkEventChannel = EventChannel(registrar.messenger(),"com.codepoka.zoom_us/zoom_us_initSdk")
      val joinWithMeetingEventChannel = EventChannel(registrar.messenger(),"com.codepoka.zoom_us/joinWithMeeting")
      val signInWithZoomEventChannel = EventChannel(registrar.messenger(),"com.codepoka.zoom_us/signInWithZoom")
      val hostInstantMeetingEventChannel = EventChannel(registrar.messenger(),"com.codepoka.zoom_us/hostInstantMeeting")
      val zoomUsPlugin = ZoomUsPlugin(registrar,
              initSdkEventChannel,
              joinWithMeetingEventChannel,
              signInWithZoomEventChannel,
              hostInstantMeetingEventChannel
      )
      channel.setMethodCallHandler(zoomUsPlugin)
    }
  }

  init {
    initSdkEventChannel.setStreamHandler(object :EventChannel.StreamHandler{
        override fun onCancel(arguments: Any?) {}
        override fun onListen(arguments: Any, events: EventChannel.EventSink?) {
            val args = (arguments as MutableMap<*, *>)
            if(args["method"] =="initSdk"){
                val data = (args["data"] as MutableMap<*, *>)
                sdkController.initSdk(data,events)
            }
        }
    })
    joinWithEventChannel.setStreamHandler(object :EventChannel.StreamHandler{
        override fun onCancel(arguments: Any?) {}
        override fun onListen(arguments: Any, events: EventChannel.EventSink?) {
            val args = (arguments as MutableMap<*, *>)
            if(args["method"] =="joinWithMeeting"){
                val data = (args["data"] as MutableMap<*, *>)
                sdkController.joinWithMeeting(data,events)
            }
        }
    })
    signInWithZoomEventChannel.setStreamHandler(object :EventChannel.StreamHandler{
        override fun onCancel(arguments: Any?) {}
        override fun onListen(arguments: Any, events: EventChannel.EventSink?) {
            val args = (arguments as MutableMap<*, *>)
            if(args["method"] =="signInWithZoom"){
                val data = (args["data"] as MutableMap<*, *>)
                sdkController.signInWithZoom(data,events)
            }
        }
    })
    hostInstantMeetingEventChannel.setStreamHandler(object :EventChannel.StreamHandler{
        override fun onCancel(arguments: Any?) {}
        override fun onListen(arguments: Any, events: EventChannel.EventSink?) {
            val args = (arguments as MutableMap<*, *>)
            if(args["method"] =="hostInstantMeeting"){
                val data = (args["data"] as MutableMap<*, *>)
                sdkController.hostInstantMeeting(data,events)
            }
        }
    })
  }

  override fun onMethodCall(call: MethodCall, result: Result) {
    when (call.method) {
        "isInitialized" -> {
          result.success(sdkController.isSdkInitialized)
        }
        "isLoggedIn" -> {
            result.success(sdkController.isLoggedIn)
        }
        "isInMeeting" -> {
            result.success(sdkController.isInMeeting)
        }
        "signOutFromZoom" -> {
            result.success(sdkController.signOutFromZoom())
        }
        else -> {
          result.success(true)
        }
    }
  }
}
