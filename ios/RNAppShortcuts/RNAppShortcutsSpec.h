#ifdef RCT_NEW_ARCH_ENABLED
#import <React/RCTTurboModule.h>
#import <React/RCTBridgeModule.h>

#import <ReactCommon/RCTTurboModule.h>

/**
 * Spec protocol for RNAppShortcuts
 */
@protocol RNAppShortcutsSpec <RCTTurboModule>

/**
 * Check if shortcuts are supported on the device
 */
- (void)isSupported:(RCTPromiseResolveBlock)resolve
             reject:(RCTPromiseRejectBlock)reject;

/**
 * Get the shortcut used to launch the app
 */
- (void)getInitialShortcut:(RCTPromiseResolveBlock)resolve
                    reject:(RCTPromiseRejectBlock)reject;

/**
 * Set dynamic shortcuts
 */
- (void)setShortcuts:(NSArray *)shortcuts
             resolve:(RCTPromiseResolveBlock)resolve
              reject:(RCTPromiseRejectBlock)reject;

/**
 * Clear all dynamic shortcuts
 */
- (void)clearShortcuts:(RCTPromiseResolveBlock)resolve
                reject:(RCTPromiseRejectBlock)reject;

/**
 * Add event listener for shortcut used events
 */
- (void)addListener:(NSString *)eventName;

/**
 * Remove event listener for shortcut used events
 */
- (void)removeListeners:(double)count;

@end

namespace facebook {
  namespace react {
    /**
     * TurboModule implementation for RNAppShortcuts
     */
    class JSI_EXPORT RNAppShortcutsSpecJSI : public ObjCTurboModule {
    public:
      RNAppShortcutsSpecJSI(const ObjCTurboModule::InitParams &params);
    };
  }
}

#endif // RCT_NEW_ARCH_ENABLED
