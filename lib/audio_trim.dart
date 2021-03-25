import 'dart:async';
import 'dart:ui';

import 'package:flutter/services.dart';

class AudioTrimmer {
  static const MethodChannel _channel = const MethodChannel('audio_trimmer');

  VoidCallback _callback;

  AudioTrimmer() {
    _channel.setMethodCallHandler(platformCallHandler);
  }

  Future<dynamic> cut(String url, double start, double end) => _channel.invokeMethod('trim', {"path": url, "start": start, "end": end});

  void setCompletionHandler(VoidCallback callback) {
    _callback = callback;
  }

  Future platformCallHandler(MethodCall call) async {
    switch (call.method) {
      case "audio.trimmer.savePath":
        if (call.arguments != null) {
          String path = call.arguments;
          print('$path');
          if (_callback != null) {
            _callback();
          }
        }
        break;
      default:
        print('Unknown method ${call.method} ');
    }
  }
}
