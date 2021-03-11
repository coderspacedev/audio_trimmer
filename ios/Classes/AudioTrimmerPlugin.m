#import "AudioTrimmerPlugin.h"
#if __has_include(<audio_trimmer/audio_trimmer-Swift.h>)
#import <audio_trimmer/audio_trimmer-Swift.h>
#else
// Support project import fallback if the generated compatibility header
// is not copied when this plugin is created as a library.
// https://forums.swift.org/t/swift-static-libraries-dont-copy-generated-objective-c-header/19816
#import "audio_trimmer-Swift.h"
#endif

@implementation AudioTrimmerPlugin
+ (void)registerWithRegistrar:(NSObject<FlutterPluginRegistrar>*)registrar {
  [SwiftAudioTrimmerPlugin registerWithRegistrar:registrar];
}
@end
