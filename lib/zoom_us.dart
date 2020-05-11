import 'dart:async';

import 'package:flutter/cupertino.dart';
import 'package:flutter/services.dart';

class ZoomUs {
  ZoomUs._();
  static ZoomUs _instance;
  static ZoomUs get instance => _instance??(_instance= ZoomUs._());
  static const MethodChannel _channel = const MethodChannel('com.codepoka.zoom_us');
  static const _initSdkStreamHandler =const EventChannel("com.codepoka.zoom_us/zoom_us_initSdk");
  static const _joinWithMeetingStreamHandler =const EventChannel("com.codepoka.zoom_us/joinWithMeeting");
  static const _signInWithZoomStreamHandler =const EventChannel("com.codepoka.zoom_us/signInWithZoom");

  Stream<dynamic> initSdk({@required String appKey,@required String appSecret}){
    var stream = _initSdkStreamHandler.receiveBroadcastStream(
        {
          "method":"initSdk",
          "data":{
            "appKey":appKey.trim(),
            "appSecret":appSecret.trim()
          }
        }
    );
    return stream;
  }
  Stream<dynamic> joinWithMeeting({@required String displayName,@required String meetingId, String password}){
    var stream = _joinWithMeetingStreamHandler.receiveBroadcastStream(
      {
        "method":"joinWithMeeting",
        "data":{
          "displayName":displayName.trim(),
          "meetingId":meetingId.trim(),
          "password":password.trim()
        }
      }
    );
    return stream;
  }
  Stream<dynamic> signInWithZoom({@required String email,@required String password}){
    var stream = _signInWithZoomStreamHandler.receiveBroadcastStream(
        {
          "method":"signInWithZoom",
          "data":{
            "email":email.trim(),
            "password":password.trim()
          }
        }
    );
    return stream;
  }

}
