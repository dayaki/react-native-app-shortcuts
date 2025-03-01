#import "RNAppShortcuts.h"
#import <React/RCTUtils.h>

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
    [instance sendEventWithName:RNAppShortcutsEvent body:@{@"type": shortcutItem.type}];
  }
}

RCT_EXPORT_METHOD(getInitialShortcut:(RCTPromiseResolveBlock)resolve reject:(RCTPromiseRejectBlock)reject)
{
  if (initialShortcutItem) {
    NSDictionary *shortcutData = [self shortcutItemToDictionary:initialShortcutItem];
    resolve(shortcutData);
    initialShortcutItem = nil;
  } else {
    resolve(nil);
  }
}

RCT_EXPORT_METHOD(isSupported:(RCTPromiseResolveBlock)resolve reject:(RCTPromiseRejectBlock)reject)
{
  // UIApplicationShortcutItem is available since iOS 9.0
  resolve(@YES);
}

RCT_EXPORT_METHOD(setShortcuts:(NSArray *)shortcuts resolve:(RCTPromiseResolveBlock)resolve reject:(RCTPromiseRejectBlock)reject)
{
  NSMutableArray *shortcutItems = [NSMutableArray new];
  
  for (NSDictionary *shortcut in shortcuts) {
    NSString *type = shortcut[@"type"];
    NSString *title = shortcut[@"title"];
    NSString *subtitle = shortcut[@"subtitle"];
    NSString *iconName = shortcut[@"iconName"];
    NSDictionary *userInfo = shortcut[@"userInfo"];
    
    if (!type || !title) {
      reject(@"invalid_shortcut", @"Shortcut must contain at least type and title", nil);
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

RCT_EXPORT_METHOD(clearShortcuts:(RCTPromiseResolveBlock)resolve reject:(RCTPromiseRejectBlock)reject)
{
  dispatch_async(dispatch_get_main_queue(), ^{
    [UIApplication sharedApplication].shortcutItems = @[];
    resolve(@YES);
  });
}

- (NSDictionary *)shortcutItemToDictionary:(UIApplicationShortcutItem *)item
{
  if (!item) return nil;
  
  NSMutableDictionary *dict = [NSMutableDictionary new];
  dict[@"type"] = item.type;
  dict[@"title"] = item.localizedTitle;
  
  if (item.localizedSubtitle) {
    dict[@"subtitle"] = item.localizedSubtitle;
  }
  
  if (item.userInfo) {
    dict[@"userInfo"] = item.userInfo;
  }
  
  return dict;
}

@end
