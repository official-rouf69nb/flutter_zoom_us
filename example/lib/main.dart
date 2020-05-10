import 'package:flutter/material.dart';
import 'dart:async';

import 'package:flutter/services.dart';
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
    ZoomUs.instance.initializationListener.listen((_){
      print(_);
    });
    ZoomUs.instance.init(
      appKey: "qClDCUTS1sHPHsNIQCWoKaElsdlpfMA64RpV",
      appSecret: "mMts2ZQpuaL6NvTmDnMRn02S3HDoSiCtfrJe"
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
          child: Text('Running on: $_platformVersion\n'),
        ),
        floatingActionButton: FloatingActionButton(onPressed: initPlatformState),
      ),
    );
  }
}
