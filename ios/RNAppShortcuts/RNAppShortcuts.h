#import <React/RCTBridgeModule.h>
#import <React/RCTEventEmitter.h>

@interface RNAppShortcuts : RCTEventEmitter <RCTBridgeModule>

+ (void)handleShortcutItem:(UIApplicationShortcutItem *)shortcutItem;

@end
