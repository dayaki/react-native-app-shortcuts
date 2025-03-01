#import <React/RCTBridgeModule.h>
#import <React/RCTEventEmitter.h>

#ifdef RCT_NEW_ARCH_ENABLED
#import <React/RCTTurboModule.h>
#endif

@interface RNAppShortcuts : RCTEventEmitter <RCTBridgeModule>
#ifdef RCT_NEW_ARCH_ENABLED
<RCTTurboModule>
#endif

+ (void)handleShortcutItem:(UIApplicationShortcutItem *)shortcutItem;

@end
