import 'package:flutter/material.dart';
import 'dart:async';
import 'package:zoom_us/zoom_us.dart';

void main() => runApp(MyApp());

class MyApp extends StatefulWidget {
  @override
  _MyAppState createState() => _MyAppState();
}

class _MyAppState extends State<MyApp> {
  String _platformVersion = 'Unknown';

  @override
  void initState() {
    super.initState();
    initPlatformState();
  }

  // Platform messages are asynchronous, so we initialize in an async method.
  Future<void> initPlatformState() async {
    ZoomUs.instance.initSdk(
      appKey: "qClDCUTS1sHPHsNIQCWoKaElsdlpfMA64RpV",
      appSecret: "mMts2ZQpuaL6NvTmDnMRn02S3HDoSiCtfrJe"
    ).listen((x){
      print(x);
      if(mounted)setState(() {
        _platformVersion = x;
      });

      if(x=="SUCCESS"){
        signInWithZoom();
      }
    });
  }

  Future<void> joinWithMeeting() async {
    ZoomUs.instance.joinWithMeeting(
      displayName: "Plugin test",
      meetingId: "3684128352",
      password: "6hfBLz"
    ).listen((x){
      print(x);
      if(mounted)setState(() {
        _platformVersion = x;
      });
    });
  }

  Future<void> signInWithZoom() async {
    ZoomUs.instance.signInWithZoom(
      email: "official.rouf69nb@gmail.com",
      password: "Rouf69nb"
    ).listen((x){
      print(x);
      if(mounted)setState(() {
        _platformVersion = x;
      });
    });
  }

  Future<void> hostMeeting() async {
    ZoomUs.instance.hostInstantMeeting(
      onMeetingStarted: (x){
        print(x);
      },
      onMeetingStateChange: (x){
        print(x);
        if(mounted)setState(() {
          _platformVersion = x.toString();
        });
      },
      onUserJoin: (x){
        print(x);
      },
      onUserLeave: (x){
        print(x);
      }
    );
  }

  @override
  Widget build(BuildContext context) {
    return MaterialApp(
      home: Scaffold(
        appBar: AppBar(
          title: const Text('Plugin example app'),
        ),
        body: Center(
          child: Text(_platformVersion),
        ),
        floatingActionButton: FloatingActionButton(onPressed: ()async{
          print(await ZoomUs.instance.isInitialized);
        }),
      ),
    );
  }
}
