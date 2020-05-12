package com.codepoka.zoom_us
import io.flutter.plugin.common.EventChannel
import io.flutter.plugin.common.PluginRegistry
import us.zoom.sdk.*
import kotlin.Exception

class ZoomSdkController(private val registrar: PluginRegistry.Registrar) {
    private var isMeetingRunning: Boolean=false
    private var hostId: Long?=null

    val isSdkInitialized: Boolean get() = ZoomSDK.getInstance().isInitialized
    val isLoggedIn: Boolean get() = ZoomSDK.getInstance().isLoggedIn
    val isInMeeting: Boolean get() = isMeetingRunning

    fun initSdk(data: MutableMap<*, *>, sink: EventChannel.EventSink?) {
        val zoomSDK = ZoomSDK.getInstance()

        if(zoomSDK.isInitialized){
            sink?.success("SUCCESS")
            return
        }

        val params = ZoomSDKInitParams()
        params.domain = "zoom.us"
        params.appKey = (data["appKey"] as String?)!!
        params.appSecret= (data["appSecret"] as String?)!!
        zoomSDK.initialize(registrar.activeContext(),object:ZoomSDKInitializeListener{
            override fun onZoomSDKInitializeResult(errorCode: Int, internalErrorCode: Int) {
                when (errorCode) {
                    ZoomError.ZOOM_ERROR_SUCCESS -> {
                        sink?.success("SUCCESS")
                        ZoomSDK.getInstance().meetingSettingsHelper.setAutoConnectVoIPWhenJoinMeeting(true)
                    }
                    ZoomError.ZOOM_ERROR_INVALID_ARGUMENTS -> {
                        sink?.success("INVALID_ARGUMENTS")
                    }
                    ZoomError.ZOOM_ERROR_ILLEGAL_APP_KEY_OR_SECRET -> {
                        sink?.success("ILLEGAL_APP_KEY_OR_SECRET")
                    }
                    ZoomError.ZOOM_ERROR_NETWORK_UNAVAILABLE -> {
                        sink?.success("NETWORK_UNAVAILABLE")
                    }
                    ZoomError.ZOOM_ERROR_DEVICE_NOT_SUPPORTED -> {
                        sink?.success("DEVICE_NOT_SUPPORTED")
                    }
                    else -> {
                        sink?.success("UNKNOWN_ERROR")
                    }
                }
            }
            override fun onZoomAuthIdentityExpired() {}
        },params)
    }
    fun joinWithMeeting(data: MutableMap<*, *>, sink: EventChannel.EventSink?) {

        val zoomSDK = ZoomSDK.getInstance()
        if(!zoomSDK.isInitialized){
            sink?.success("SDK_NOT_INITIALIZED")
            return
        }




        // Step 1: resolve meeting service.
        val meetingService = zoomSDK.meetingService


        // Step 2: Configure meeting options.
        val opts = JoinMeetingOptions()
        opts.no_driving_mode = true
        opts.no_invite = true
        opts.no_meeting_end_message = false
        opts.no_titlebar = false
        opts.no_bottom_toolbar = false
        opts.no_dial_in_via_phone = true
        opts.no_dial_out_to_phone = true
        opts.no_disconnect_audio = false
        opts.no_share = true
        opts.no_audio = false
        opts.no_video = false
        opts.meeting_views_options = MeetingViewsOptions.NO_BUTTON_SHARE
        opts.no_meeting_error_message = true

        // Step 3: resolve meeting parameters
        val displayName:String = (data["displayName"] as String?)!!
        val meetingId:String = (data["meetingId"] as String?)!!
        val password:String = (data["password"] as String?)!!

        // Step 4: Setup join meeting parameters
        val params = JoinMeetingParams()
        params.displayName =displayName
        params.meetingNo = meetingId
        params.password = password


        // Step 5: Call meeting service to join meeting
        meetingService.addListener { meetingStatus, _, _ ->
            when(meetingStatus){
                MeetingStatus.MEETING_STATUS_IDLE -> sink?.success("IDLE")
                MeetingStatus.MEETING_STATUS_CONNECTING ->  sink?.success("CONNECTING")
                MeetingStatus.MEETING_STATUS_WAITINGFORHOST ->  sink?.success("WAITING_FOR_HOST")
                MeetingStatus.MEETING_STATUS_INMEETING -> sink?.success("IN_MEETING")
                MeetingStatus.MEETING_STATUS_DISCONNECTING -> sink?.success("DISCONNECTING")
                MeetingStatus.MEETING_STATUS_RECONNECTING -> sink?.success("RECONNECTING")
                MeetingStatus.MEETING_STATUS_FAILED -> sink?.success("FAILED")
                MeetingStatus.MEETING_STATUS_IN_WAITING_ROOM -> sink?.success("IN_WAITING_ROOM")
                MeetingStatus.MEETING_STATUS_WEBINAR_PROMOTE ->  {}
                MeetingStatus.MEETING_STATUS_WEBINAR_DEPROMOTE -> {}
                MeetingStatus.MEETING_STATUS_UNKNOWN ->  sink?.success("UNKNOWN")
                null -> sink?.success("UNKNOWN")
            }

            if(!isMeetingRunning && meetingStatus == MeetingStatus.MEETING_STATUS_INMEETING){
                isMeetingRunning=true
            }
            else if(isMeetingRunning && meetingStatus == MeetingStatus.MEETING_STATUS_IDLE){
                isMeetingRunning=false
            }
        }


        zoomSDK.inMeetingService.addListener(object : InMeetingServiceListener {
            override fun onMeetingUserJoin(p0: MutableList<Long>) {
                val service = ZoomSDK.getInstance().inMeetingService
                service.inMeetingUserList.forEach{x->
                    if(service.isHostUser(x)){
                        hostId = x
                    }
                }
            }
            override fun onMeetingUserLeave(p0: MutableList<Long>) {
                if(p0[0]== hostId){
                   // ZoomSDK.getInstance().inMeetingService.leaveCurrentMeeting(true)
                    sink?.success("HOST_DISCONNECTED")
                }
            }
            override fun onMeetingLeaveComplete(p0: Long) {
                sink?.success("LEAVE_COMPLETE")
            }

            override fun onMeetingActiveVideo(p0: Long) {}
            override fun onFreeMeetingReminder(p0: Boolean, p1: Boolean, p2: Boolean) {}
            override fun onJoinWebinarNeedUserNameAndEmail(p0: InMeetingEventHandler?) {}
            override fun onActiveVideoUserChanged(p0: Long) {}
            override fun onActiveSpeakerVideoUserChanged(p0: Long) {}
            override fun onChatMessageReceived(p0: InMeetingChatMessage?) {}
            override fun onUserNetworkQualityChanged(p0: Long) {}
            override fun onMeetingFail(p0: Int, p1: Int) {}
            override fun onUserAudioTypeChanged(p0: Long) {}
            override fun onMyAudioSourceTypeChanged(p0: Int) {}
            override fun onSilentModeChanged(p0: Boolean) {}
            override fun onMeetingCoHostChanged(p0: Long) {}
            override fun onLowOrRaiseHandStatusChanged(p0: Long, p1: Boolean) {}
            override fun onSinkAttendeeChatPriviledgeChanged(p0: Int) {}
            override fun onMeetingUserUpdated(p0: Long) {}
            override fun onMeetingSecureKeyNotification(p0: ByteArray?) {}
            override fun onMeetingNeedColseOtherMeeting(p0: InMeetingEventHandler?) {}
            override fun onMicrophoneStatusError(p0: InMeetingAudioController.MobileRTCMicrophoneError?) {}
            override fun onHostAskStartVideo(p0: Long) {}
            override fun onSinkAllowAttendeeChatNotification(p0: Int) {}
            override fun onWebinarNeedRegister() {}
            override fun onSpotlightVideoChanged(p0: Boolean) {}
            override fun onMeetingHostChanged(p0: Long) {}
            override fun onHostAskUnMute(p0: Long) {}
            override fun onUserAudioStatusChanged(p0: Long) {}
            override fun onUserVideoStatusChanged(p0: Long) {}
            override fun onMeetingNeedPasswordOrDisplayName(p0: Boolean, p1: Boolean, p2: InMeetingEventHandler?) {}
        })
        meetingService.joinMeetingWithParams(registrar.activeContext(), params, opts)
    }
    fun signInWithZoom(data: MutableMap<*, *>, sink: EventChannel.EventSink?) {
        val zoomSDK = ZoomSDK.getInstance()

        if(!zoomSDK.isInitialized){
            sink?.success("SDK_NOT_INITIALIZED")
            return
        }
        if(zoomSDK.isLoggedIn){
            sink?.success("SUCCESS")
            return
        }

        zoomSDK.addAuthenticationListener(object :ZoomSDKAuthenticationListener{
            override fun onZoomSDKLoginResult(result: Long) {
                if(result.toInt() == ZoomAuthenticationError.ZOOM_AUTH_ERROR_SUCCESS) {
                  sink?.success("SUCCESS")
                }else if (result.toInt() == ZoomAuthenticationError.ZOOM_AUTH_ERROR_USER_NOT_EXIST) {
                    sink?.success("USER_NOT_EXIST")
                }
                else if (result.toInt() == ZoomAuthenticationError.ZOOM_AUTH_ERROR_WRONG_PASSWORD) {
                    sink?.success("WRONG_PASSWORD")
                }
                else{
                    sink?.success("UNKNOWN")
                }
            }
            override fun onZoomSDKLogoutResult(p0: Long) {
            }
            override fun onZoomIdentityExpired() {}
            override fun onZoomAuthIdentityExpired() {}
        })


        val email = (data["email"] as String?)!!
        val password= (data["password"] as String?)!!
        zoomSDK.loginWithZoom(email, password)
    }
    fun signOutFromZoom():String {
        val zoomSDK = ZoomSDK.getInstance()

        if(!zoomSDK.isInitialized){
            return "SDK_NOT_INITIALIZED"
        }
        if(!zoomSDK.isLoggedIn){
            return "SUCCESS"
        }
        return if(zoomSDK.logoutZoom()){
            "SUCCESS"
        }else{
            "FAILED"
        }
    }
    fun hostInstantMeeting(data: MutableMap<*, *>, sink: EventChannel.EventSink?) {
        val zoomSDK = ZoomSDK.getInstance()
        // Check if the zoom SDK is initialized
        if (!zoomSDK.isInitialized) {
            sink?.success("SDK_NOT_INITIALIZED")
            return
        }

        // is logged in
        if (!zoomSDK.isLoggedIn) {
            sink?.success("NOT_SIGNED_IN")
            return
        }

        // Get meeting service from zoom SDK instance.
        val meetingService = zoomSDK.meetingService
        // Configure meeting options.
        val opts = InstantMeetingOptions()

        // Some available options
        opts.no_driving_mode = true
        opts.no_invite = true
        opts.no_meeting_end_message = false
        opts.no_titlebar = false
        opts.no_bottom_toolbar = false
        opts.no_dial_in_via_phone = true
        opts.no_dial_out_to_phone = true
        opts.no_disconnect_audio = false
        opts.no_share = true
        opts.custom_meeting_id= data["meetingId"] as String?

        meetingService.addListener { meetingStatus, _, _ ->
            when(meetingStatus){
                MeetingStatus.MEETING_STATUS_IDLE -> sink?.success("IDLE")
                MeetingStatus.MEETING_STATUS_CONNECTING ->  sink?.success("CONNECTING")
                MeetingStatus.MEETING_STATUS_WAITINGFORHOST ->  sink?.success("WAITING_FOR_HOST")
                MeetingStatus.MEETING_STATUS_INMEETING -> sink?.success("IN_MEETING")
                MeetingStatus.MEETING_STATUS_DISCONNECTING -> sink?.success("DISCONNECTING")
                MeetingStatus.MEETING_STATUS_RECONNECTING -> sink?.success("RECONNECTING")
                MeetingStatus.MEETING_STATUS_FAILED -> sink?.success("FAILED")
                MeetingStatus.MEETING_STATUS_IN_WAITING_ROOM -> sink?.success("IN_WAITING_ROOM")
                MeetingStatus.MEETING_STATUS_WEBINAR_PROMOTE ->  {}
                MeetingStatus.MEETING_STATUS_WEBINAR_DEPROMOTE -> {}
                MeetingStatus.MEETING_STATUS_UNKNOWN ->  sink?.success("UNKNOWN")
                null -> sink?.success("UNKNOWN")
            }

            if(!isMeetingRunning && meetingStatus == MeetingStatus.MEETING_STATUS_INMEETING){
                isMeetingRunning=true
                val s = ZoomSDK.getInstance().inMeetingService
                sink?.success(hashMapOf(
                        "method" to "MEETING_STARTED",
                        "data" to hashMapOf(
                                "meetingId" to s.currentMeetingNumber,
                                "meetingPassword" to s.meetingPassword,
                                "hostId" to s.myUserID,
                                "hostName" to  s.myUserInfo.userName
                        )
                ))
            }
            else if(isMeetingRunning && meetingStatus == MeetingStatus.MEETING_STATUS_IDLE){
                isMeetingRunning=false
                sink?.success("MEETING_ENDED")
            }
        }
        zoomSDK.inMeetingService.addListener(object : InMeetingServiceListener {
            override fun onMeetingUserJoin(p0: MutableList<Long>) {
                val service = zoomSDK.inMeetingService
                p0.forEach{u->
                    val user = service.getUserInfoById(u)
                    sink?.success(hashMapOf(
                            "method" to "USER_JOIN",
                            "data" to hashMapOf(
                                    "userId" to u,
                                    "displayName" to user.userName
                            )
                    ))
                }
            }
            override fun onMeetingUserLeave(p0: MutableList<Long>) {
                p0.forEach{u->
                    sink?.success(hashMapOf(
                            "method" to "USER_LEAVE",
                            "data" to hashMapOf(
                                    "userId" to u
                            )
                    ))
                }
            }
            override fun onMeetingLeaveComplete(p0: Long) {}
            override fun onMeetingActiveVideo(p0: Long) {}
            override fun onFreeMeetingReminder(p0: Boolean, p1: Boolean, p2: Boolean) {}
            override fun onJoinWebinarNeedUserNameAndEmail(p0: InMeetingEventHandler?) {}
            override fun onActiveVideoUserChanged(p0: Long) {}
            override fun onActiveSpeakerVideoUserChanged(p0: Long) {}
            override fun onChatMessageReceived(p0: InMeetingChatMessage?) {}
            override fun onUserNetworkQualityChanged(p0: Long) {}
            override fun onMeetingFail(p0: Int, p1: Int) {}
            override fun onUserAudioTypeChanged(p0: Long) {}
            override fun onMyAudioSourceTypeChanged(p0: Int) {}
            override fun onSilentModeChanged(p0: Boolean) {}
            override fun onMeetingCoHostChanged(p0: Long) {}
            override fun onLowOrRaiseHandStatusChanged(p0: Long, p1: Boolean) {}
            override fun onSinkAttendeeChatPriviledgeChanged(p0: Int) {}
            override fun onMeetingUserUpdated(p0: Long) {}
            override fun onMeetingSecureKeyNotification(p0: ByteArray?) {}
            override fun onMeetingNeedColseOtherMeeting(p0: InMeetingEventHandler?) {}
            override fun onMicrophoneStatusError(p0: InMeetingAudioController.MobileRTCMicrophoneError?) {}
            override fun onHostAskStartVideo(p0: Long) {}
            override fun onSinkAllowAttendeeChatNotification(p0: Int) {}
            override fun onWebinarNeedRegister() {}
            override fun onSpotlightVideoChanged(p0: Boolean) {}
            override fun onMeetingHostChanged(p0: Long) {}
            override fun onHostAskUnMute(p0: Long) {}
            override fun onUserAudioStatusChanged(p0: Long) {}
            override fun onUserVideoStatusChanged(p0: Long) {}
            override fun onMeetingNeedPasswordOrDisplayName(p0: Boolean, p1: Boolean, p2: InMeetingEventHandler?) {}
        })
        meetingService.startInstantMeeting(registrar.activeContext(), opts)
    }

    fun leaveMeeting(endIfPossible: Boolean) {
        try {
            ZoomSDK.getInstance().inMeetingService.leaveCurrentMeeting(endIfPossible)
        }catch (e:Exception){}
    }

}