import 'dart:ui';

import 'package:audio_trimmer/audio_trimmer.dart';
import 'package:audio_trimmer_example/trimstate.dart';
import 'package:flutter/material.dart';

void main() {
  runApp(MyApp());
}

class MyApp extends StatefulWidget {
  @override
  _MyAppState createState() => _MyAppState();
}

class _MyAppState extends State<MyApp> {
  AudioTrimmer audioTrimmer;

  TrimState trimState;

  double start_timer_in_double = 0.0;

  double end_timer_in_double = 10.0;

  @override
  void initState() {
    super.initState();
    initCutter();
  }

  void initCutter() {
    audioTrimmer = AudioTrimmer();

    audioTrimmer.setCompletionHandler(() {
      setState(() {
        trimState = TrimState.complete;
      });
    });
  }

  @override
  Widget build(BuildContext context) {
    return MaterialApp(
      debugShowCheckedModeBanner: false,
      home: Scaffold(
        appBar: AppBar(
          title: const Text('Audio Trimmer'),
        ),
        body: Center(
          child: Column(crossAxisAlignment: CrossAxisAlignment.center, mainAxisAlignment: MainAxisAlignment.center, children: <Widget>[
            Text('Trim audio with song path, start time and end time'),
            SizedBox(
              height: 24.0,
            ),
            MaterialButton(
              onPressed: () {
                audioTrimmer.cut('path', start_timer_in_double, end_timer_in_double);
              },
              color: Colors.blueAccent,
              child: Text(
                'Trim',
                style: TextStyle(color: Colors.white),
              ),
            )
          ]),
        ),
      ),
    );
  }
}
