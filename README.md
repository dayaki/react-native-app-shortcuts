# @dayaki/react-native-app-shortcuts

React Native package for iOS UIApplicationShortcutItem (Quick Actions) and Android App Shortcuts.

This package allows you to add home screen shortcuts to your React Native app, enabling users to quickly access specific actions within your app directly from the home screen.

## Features

- Create dynamic shortcuts for iOS and Android
- Handle shortcut selection events
- Check if shortcuts are supported on the device
- TypeScript support
- Compatible with the new React Native architecture

## Installation

```sh
npm install @dayaki/react-native-app-shortcuts
```

or

```sh
yarn add @dayaki/react-native-app-shortcuts
```

### iOS Setup

#### Pod Installation

```sh
cd ios && pod install
```

#### AppDelegate Configuration

You need to modify your AppDelegate to handle shortcut selection events.

##### For Objective-C (AppDelegate.m)

Add the import at the top of your file:

```objective-c
#import <@dayaki/react-native-app-shortcuts/RNAppShortcuts.h>
```

Add the following method to your AppDelegate implementation:

```objective-c
- (void)application:(UIApplication *)application performActionForShortcutItem:(UIApplicationShortcutItem *)shortcutItem completionHandler:(void (^)(BOOL))completionHandler {
  [RNAppShortcuts handleShortcutItem:shortcutItem];
  completionHandler(YES);
}
```

##### For Swift (AppDelegate.swift)

First, create or modify your bridging header to include:

```objective-c
// Bridging-Header.h
#import <@dayaki/react-native-app-shortcuts/RNAppShortcuts.h>
```

Then add the following method to your AppDelegate:

```swift
override func application(_ application: UIApplication, performActionFor shortcutItem: UIApplicationShortcutItem, completionHandler: @escaping (Bool) -> Void) {
  RNAppShortcuts.handle(shortcutItem)
  completionHandler(true)
}
```

### Android Setup

No additional setup is required for Android.

## Usage

```javascript
import AppShortcuts from '@dayaki/react-native-app-shortcuts';

// Check if shortcuts are supported
const checkSupport = async () => {
  const isSupported = await AppShortcuts.isSupported();
  console.log('Shortcuts supported:', isSupported);
};

// Set dynamic shortcuts
const setShortcuts = async () => {
  const shortcuts = [
    {
      type: 'compose', // unique identifier
      title: 'New Message',
      subtitle: 'Create a new message', // optional
      iconName: 'compose_icon', // optional, must be in app assets
    },
    {
      type: 'search',
      title: 'Search',
      iconName: 'search_icon',
    },
  ];
  
  const success = await AppShortcuts.setShortcuts(shortcuts);
  console.log('Shortcuts set:', success);
};

// Clear all shortcuts
const clearShortcuts = async () => {
  const success = await AppShortcuts.clearShortcuts();
  console.log('Shortcuts cleared:', success);
};

// Check if app was launched from a shortcut
const checkInitialShortcut = async () => {
  const shortcut = await AppShortcuts.getInitialShortcut();
  if (shortcut) {
    console.log('App launched from shortcut:', shortcut.type);
    handleShortcut(shortcut.type);
  }
};

// Listen for shortcut selection when app is running
const shortcutListener = AppShortcuts.addListener((event) => {
  console.log('Shortcut selected:', event.type);
  handleShortcut(event.type);
});

// Handle shortcut action
const handleShortcut = (type) => {
  switch (type) {
    case 'compose':
      // Navigate to compose screen
      break;
    case 'search':
      // Navigate to search screen
      break;
    // Add more cases as needed
  }
};

// Don't forget to remove the listener when component unmounts
useEffect(() => {
  checkInitialShortcut();
  
  return () => {
    shortcutListener(); // Remove listener
  };
}, []);
```

## Icons

### iOS

For iOS, you need to add your icon images to your app's asset catalog. The `iconName` parameter should match the name of the image in your asset catalog.

### Android

For Android, you need to add your icon images to the drawable resources folder. The `iconName` parameter should match the name of the drawable resource (without the file extension).

## API Reference

### Methods

#### `isSupported(): Promise<boolean>`

Check if shortcuts are supported on the device.

#### `getInitialShortcut(): Promise<ShortcutEvent | null>`

Get the shortcut that was used to launch the app (if any).

#### `setShortcuts(shortcuts: Shortcut[]): Promise<boolean>`

Set dynamic shortcuts for the app.

#### `clearShortcuts(): Promise<boolean>`

Clear all dynamic shortcuts.

#### `addListener(callback: (event: ShortcutEvent) => void): () => void`

Add a listener for when a shortcut is used while the app is running. Returns a function to remove the listener.

### Types

#### `Shortcut`

```typescript
interface Shortcut {
  type: string;      // Unique identifier
  title: string;     // Display title
  subtitle?: string; // Optional subtitle
  iconName?: string; // Optional icon name
  userInfo?: Record<string, any>; // Optional additional data
}
```

#### `ShortcutEvent`

```typescript
interface ShortcutEvent {
  type: string;      // The type/id of the shortcut that was used
}
```

## License

MIT
