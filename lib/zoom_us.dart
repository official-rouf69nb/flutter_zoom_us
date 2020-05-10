import 'dart:async';

import 'package:flutter/cupertino.dart';
import 'package:flutter/services.dart';

class ZoomUs {
  ZoomUs._(){
    _channel.setMethodCallHandler(_handler);
  }
  static ZoomUs _instance;
  static ZoomUs get instance => _instance??(_instance= ZoomUs._());
  static const MethodChannel _channel = const MethodChannel('zoom_us');

  StreamController<String> _initializationStreamController = StreamController.broadcast();

  //Getter
  Stream<String> get initializationListener => _initializationStreamController.stream;

  init({@required String appKey,@required String appSecret}){
    _channel.invokeMethod("initSdk",{"appKey":appKey,"appSecret":appSecret});
  }
  dispose(){
    _initializationStreamController.close();
    _instance = null;
  }


  Future<dynamic> _handler(MethodCall call) async {
    if(call.method == "onZoomSDKInitializeResult"){
      _initializationStreamController.sink.add(call.arguments);
    }
  }
}
