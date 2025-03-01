import { NativeModules, NativeEventEmitter, Platform } from 'react-native';
const LINKING_ERROR = `The package '@dayaki/react-native-app-shortcuts' doesn't seem to be linked. Make sure: \n\n` + Platform.select({
  ios: "- You have run 'pod install'\n",
  default: ''
}) + '- You rebuilt the app after installing the package\n' + '- You are not using Expo Go\n';
const RNAppShortcuts = NativeModules.RNAppShortcuts ? NativeModules.RNAppShortcuts : new Proxy({}, {
  get() {
    throw new Error(LINKING_ERROR);
  }
});
const eventEmitter = new NativeEventEmitter(RNAppShortcuts);
/**
 * React Native App Shortcuts
 * 
 * A module for handling iOS Quick Actions (UIApplicationShortcutItem) 
 * and Android App Shortcuts
 */
const AppShortcuts = {
  /**
   * Check if shortcuts are supported on the device
   * 
   * @returns Promise<boolean> - true if shortcuts are supported
   */
  isSupported() {
    return RNAppShortcuts.isSupported();
  },
  /**
   * Get the shortcut that was used to launch the app (if any)
   * 
   * @returns Promise<ShortcutEvent | null> - The shortcut event or null
   */
  getInitialShortcut() {
    return RNAppShortcuts.getInitialShortcut();
  },
  /**
   * Set dynamic shortcuts for the app
   * 
   * @param shortcuts - Array of shortcut objects
   * @returns Promise<boolean> - true if successful
   */
  setShortcuts(shortcuts) {
    return RNAppShortcuts.setShortcuts(shortcuts);
  },
  /**
   * Clear all dynamic shortcuts
   * 
   * @returns Promise<boolean> - true if successful
   */
  clearShortcuts() {
    return RNAppShortcuts.clearShortcuts();
  },
  /**
   * Add a listener for when a shortcut is used while the app is running
   * 
   * @param callback - Function to call when a shortcut is used
   * @returns Function - Call to remove the listener
   */
  addListener(callback) {
    const subscription = eventEmitter.addListener('RNAppShortcuts:ShortcutUsed', callback);
    return () => subscription.remove();
  }
};
export default AppShortcuts;
//# sourceMappingURL=index.js.map