package com.techhuntdevelopers.audio_trimmer;

import android.content.Context;
import android.os.Environment;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;

import androidx.annotation.NonNull;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.text.DecimalFormat;

import io.flutter.embedding.engine.plugins.FlutterPlugin;
import io.flutter.plugin.common.MethodCall;
import io.flutter.plugin.common.MethodChannel;
import io.flutter.plugin.common.MethodChannel.MethodCallHandler;
import io.flutter.plugin.common.MethodChannel.Result;

/**
 * AudioTrimmerPlugin
 */
public class AudioTrimmerPlugin implements FlutterPlugin, MethodCallHandler {

    private MethodChannel channel;

    private static String TAG = "AudioTrimmerPlugin";
    private SoundFile cheapSoundFile = null;
    private Context context;
    final Handler handler = new Handler(Looper.getMainLooper());
    private double progressLoading = 0.0;

    @Override
    public void onAttachedToEngine(@NonNull FlutterPluginBinding flutterPluginBinding) {
        channel = new MethodChannel(flutterPluginBinding.getBinaryMessenger(), "audio_trimmer");
        channel.setMethodCallHandler(this);
    }

    @Override
    public void onMethodCall(@NonNull MethodCall call, @NonNull Result result) {
        switch (call.method) {
            case "trim":
                final String outPath = makeRingtoneFilename("AUDIO_TEMP", ".wav");

                String path = call.argument("path");
                double start = call.argument("start");
                double end = call.argument("end");

                DecimalFormat df = new DecimalFormat("#.##");
                end = Double.valueOf(df.format(end));

                try {
                    cheapSoundFile = SoundFile.create(path, null);
                } catch (IOException | SoundFile.InvalidInputException e) {
                    e.printStackTrace();
                    Log.e(TAG, "onMethodCall: " + e.getLocalizedMessage());
                }

                int sampleRate = cheapSoundFile.getSampleRate();
                int samplesPerFrame = cheapSoundFile.getSamplesPerFrame();

                final int startFrame = Util.secondsToFrames(start, sampleRate, samplesPerFrame);
                final int endFrame = Util.secondsToFrames(end, sampleRate, samplesPerFrame);

                Thread thread = new Thread() {
                    public void run() {
                        if (outPath == null) {
                            Log.e(TAG + " >> ", "Unable to find unique filename");
                            return;
                        }
                        File outFile = new File(outPath);
                        try {
                            cheapSoundFile.WriteFile(outFile, startFrame, endFrame - startFrame);
                        } catch (Exception e) {
                            // log the error and try to create a .wav file instead
                            if (outFile.exists()) {
                                outFile.delete();
                            }
                            e.printStackTrace();
                        }

                        String finalOutPath = outPath;
                        Runnable runnable = () -> {
                            channel.invokeMethod("audio.trimmer.savePath", finalOutPath);
                        };

                        new Handler(Looper.getMainLooper()).postDelayed(runnable, 500);
                    }
                };
                thread.start();
                result.success("");
                break;
            default:
                result.notImplemented();
                break;
        }
    }

    @Override
    public void onDetachedFromEngine(@NonNull FlutterPluginBinding binding) {
        channel.setMethodCallHandler(null);
    }

    private String makeRingtoneFilename(CharSequence title, String extension) {
        String subDir;
        String externalRootDir = Environment.getExternalStorageDirectory().getPath();
        if (!externalRootDir.endsWith("/")) {
            externalRootDir += "/";
        }
        subDir = "media/audio/music/";
        String parentDir = externalRootDir + subDir;

        // Create the parent directory
        File parentDirFile = new File(parentDir);
        parentDirFile.mkdirs();

        // If we can't write to that special path, try just writing
        // directly to the sdcard
        if (!parentDirFile.isDirectory()) {
            parentDir = externalRootDir;
        }

        // Turn the title into a filename
        String filename = "";
        for (int i = 0; i < title.length(); i++) {
            if (Character.isLetterOrDigit(title.charAt(i))) {
                filename += title.charAt(i);
            }
        }

        // Try to make the filename unique
        String path = null;
        for (int i = 0; i < 100; i++) {
            String testPath;
            if (i > 0)
                testPath = parentDir + filename + i + extension;
            else
                testPath = parentDir + filename + extension;

            try {
                RandomAccessFile f = new RandomAccessFile(new File(testPath), "r");
                f.close();
            } catch (Exception e) {
                // Good, the file didn't exist
                path = testPath;
                break;
            }
        }

        return path;
    }
}
