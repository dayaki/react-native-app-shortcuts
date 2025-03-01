#import "RNAppShortcuts.h"
#import <React/RCTUtils.h>

#ifdef RCT_NEW_ARCH_ENABLED
#import "RNAppShortcutsSpec.h"
#endif

static NSString *const RNAppShortcutsEvent = @"RNAppShortcuts:ShortcutUsed";
static UIApplicationShortcutItem *initialShortcutItem;

@implementation RNAppShortcuts

RCT_EXPORT_MODULE();

+ (BOOL)requiresMainQueueSetup
{
  return NO;
}

- (NSArray<NSString *> *)supportedEvents
{
  return @[RNAppShortcutsEvent];
}

+ (void)handleShortcutItem:(UIApplicationShortcutItem *)shortcutItem
{
  initialShortcutItem = shortcutItem;
  
  // If we're already running and the bridge is available, send the event immediately
  RNAppShortcuts *instance = [RNAppShortcuts allocWithZone:nil];
  if (instance.bridge) {
    [instance sendEventWithName:RNAppShortcutsEvent body:@{@"shortcutId": shortcutItem.type}];
  }
}

#ifdef RCT_NEW_ARCH_ENABLED
- (void)getInitialShortcut:(RCTPromiseResolveBlock)resolve reject:(RCTPromiseRejectBlock)reject
#else
RCT_EXPORT_METHOD(getInitialShortcut:(RCTPromiseResolveBlock)resolve reject:(RCTPromiseRejectBlock)reject)
#endif
{
  if (initialShortcutItem) {
    NSDictionary *shortcutData = [self shortcutItemToDictionary:initialShortcutItem];
    resolve(shortcutData);
    initialShortcutItem = nil;
  } else {
    resolve(nil);
  }
}

#ifdef RCT_NEW_ARCH_ENABLED
- (void)isSupported:(RCTPromiseResolveBlock)resolve reject:(RCTPromiseRejectBlock)reject
#else
RCT_EXPORT_METHOD(isSupported:(RCTPromiseResolveBlock)resolve reject:(RCTPromiseRejectBlock)reject)
#endif
{
  // UIApplicationShortcutItem is available since iOS 9.0
  resolve(@YES);
}

#ifdef RCT_NEW_ARCH_ENABLED
- (void)setShortcuts:(NSArray *)shortcuts resolve:(RCTPromiseResolveBlock)resolve reject:(RCTPromiseRejectBlock)reject
#else
RCT_EXPORT_METHOD(setShortcuts:(NSArray *)shortcuts resolve:(RCTPromiseResolveBlock)resolve reject:(RCTPromiseRejectBlock)reject)
#endif
{
  NSMutableArray *shortcutItems = [NSMutableArray new];
  
  for (NSDictionary *shortcut in shortcuts) {
    NSString *type = shortcut[@"id"];
    NSString *title = shortcut[@"title"];
    NSString *subtitle = shortcut[@"subtitle"];
    NSString *iconName = shortcut[@"iconName"];
    NSDictionary *userInfo = shortcut[@"data"];
    
    if (!type || !title) {
      reject(@"invalid_shortcut", @"Shortcut must contain at least id and title", nil);
      return;
    }
    
    UIApplicationShortcutIcon *icon = nil;
    if (iconName) {
      icon = [UIApplicationShortcutIcon iconWithTemplateImageName:iconName];
    }
    
    UIApplicationShortcutItem *shortcutItem = [[UIApplicationShortcutItem alloc]
                                             initWithType:type
                                             localizedTitle:title
                                             localizedSubtitle:subtitle
                                             icon:icon
                                             userInfo:userInfo];
    
    [shortcutItems addObject:shortcutItem];
  }
  
  dispatch_async(dispatch_get_main_queue(), ^{
    [UIApplication sharedApplication].shortcutItems = shortcutItems;
    resolve(@YES);
  });
}

#ifdef RCT_NEW_ARCH_ENABLED
- (void)clearShortcuts:(RCTPromiseResolveBlock)resolve reject:(RCTPromiseRejectBlock)reject
#else
RCT_EXPORT_METHOD(clearShortcuts:(RCTPromiseResolveBlock)resolve reject:(RCTPromiseRejectBlock)reject)
#endif
{
  dispatch_async(dispatch_get_main_queue(), ^{
    [UIApplication sharedApplication].shortcutItems = @[];
    resolve(@YES);
  });
}

#ifdef RCT_NEW_ARCH_ENABLED
- (void)addListener:(NSString *)eventName
{
  // Required for RN built in Event Emitter Calls
}

- (void)removeListeners:(double)count
{
  // Required for RN built in Event Emitter Calls
}
#endif

- (NSDictionary *)shortcutItemToDictionary:(UIApplicationShortcutItem *)item
{
  if (!item) return nil;
  
  NSMutableDictionary *dict = [NSMutableDictionary new];
  dict[@"shortcutId"] = item.type;
  
  return dict;
}

// Don't compile this code when we build for the old architecture.
#ifdef RCT_NEW_ARCH_ENABLED
- (std::shared_ptr<facebook::react::TurboModule>)getTurboModule:
    (const facebook::react::ObjCTurboModule::InitParams &)params
{
    return std::make_shared<facebook::react::RNAppShortcutsSpecJSI>(params);
}
#endif

@end
