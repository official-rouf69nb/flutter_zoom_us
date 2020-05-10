#import "ZoomUsPlugin.h"
#if __has_include(<zoom_us/zoom_us-Swift.h>)
#import <zoom_us/zoom_us-Swift.h>
#else
// Support project import fallback if the generated compatibility header
// is not copied when this plugin is created as a library.
// https://forums.swift.org/t/swift-static-libraries-dont-copy-generated-objective-c-header/19816
#import "zoom_us-Swift.h"
#endif

@implementation ZoomUsPlugin
+ (void)registerWithRegistrar:(NSObject<FlutterPluginRegistrar>*)registrar {
  [SwiftZoomUsPlugin registerWithRegistrar:registrar];
}
@end
