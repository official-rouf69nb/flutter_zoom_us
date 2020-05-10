package com.codepoka.zoom_us

import io.flutter.plugin.common.MethodCall
import io.flutter.plugin.common.MethodChannel
import io.flutter.plugin.common.PluginRegistry
import us.zoom.sdk.ZoomError
import us.zoom.sdk.ZoomSDK
import us.zoom.sdk.ZoomSDKInitParams
import us.zoom.sdk.ZoomSDKInitializeListener

class ZoomSdkController(private val registrar: PluginRegistry.Registrar,private val channel: MethodChannel) {


    fun initSdk(call: MethodCall) {
        val zoomSDK = ZoomSDK.getInstance()

        if(zoomSDK.isInitialized){
            channel.invokeMethod("onZoomSDKInitializeResult","SUCCESS")
            return
        }

        val params = ZoomSDKInitParams()
        params.domain = "zoom.us"
        params.appKey = call.argument("appKey")
        params.appSecret= call.argument("appSecret")
        zoomSDK.initialize(registrar.activeContext(),object:ZoomSDKInitializeListener{
            override fun onZoomSDKInitializeResult(errorCode: Int, internalErrorCode: Int) {
                when (errorCode) {
                    ZoomError.ZOOM_ERROR_SUCCESS -> {
                        channel.invokeMethod("onZoomSDKInitializeResult","SUCCESS")
                    }
                    ZoomError.ZOOM_ERROR_INVALID_ARGUMENTS -> {

                        channel.invokeMethod("onZoomSDKInitializeResult","INVALID_ARGUMENTS")
                    }
                    ZoomError.ZOOM_ERROR_ILLEGAL_APP_KEY_OR_SECRET -> {

                        channel.invokeMethod("onZoomSDKInitializeResult","ILLEGAL_APP_KEY_OR_SECRET")
                    }
                    ZoomError.ZOOM_ERROR_NETWORK_UNAVAILABLE -> {

                        channel.invokeMethod("onZoomSDKInitializeResult","NETWORK_UNAVAILABLE")
                    }
                    ZoomError.ZOOM_ERROR_DEVICE_NOT_SUPPORTED -> {

                        channel.invokeMethod("onZoomSDKInitializeResult","DEVICE_NOT_SUPPORTED")
                    }
                    else -> {
                        channel.invokeMethod("onZoomSDKInitializeResult","UNKNOWN_ERROR")
                    }
                }
            }
            override fun onZoomAuthIdentityExpired() {}
        },params)
    }

}