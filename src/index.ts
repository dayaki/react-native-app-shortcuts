import { NativeModules, NativeEventEmitter, Platform } from 'react-native';

const LINKING_ERROR =
  `The package '@dayaki/react-native-app-shortcuts' doesn't seem to be linked. Make sure: \n\n` +
  Platform.select({ ios: "- You have run 'pod install'\n", default: '' }) +
  '- You rebuilt the app after installing the package\n' +
  '- You are not using Expo Go\n';

const RNAppShortcuts = NativeModules.RNAppShortcuts
  ? NativeModules.RNAppShortcuts
  : new Proxy(
      {},
      {
        get() {
          throw new Error(LINKING_ERROR);
        },
      }
    );

const eventEmitter = new NativeEventEmitter(RNAppShortcuts);

export interface Shortcut {
  /**
   * Unique identifier for the shortcut
   */
  type: string;
  
  /**
   * Title to display for the shortcut
   */
  title: string;
  
  /**
   * Optional subtitle (long label on Android)
   */
  subtitle?: string;
  
  /**
   * Optional icon name (must be in app's assets)
   */
  iconName?: string;
  
  /**
   * Optional additional data
   */
  userInfo?: Record<string, any>;
}

export interface ShortcutEvent {
  /**
   * The type/id of the shortcut that was used
   */
  type: string;
}

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
  isSupported(): Promise<boolean> {
    return RNAppShortcuts.isSupported();
  },

  /**
   * Get the shortcut that was used to launch the app (if any)
   * 
   * @returns Promise<ShortcutEvent | null> - The shortcut event or null
   */
  getInitialShortcut(): Promise<ShortcutEvent | null> {
    return RNAppShortcuts.getInitialShortcut();
  },

  /**
   * Set dynamic shortcuts for the app
   * 
   * @param shortcuts - Array of shortcut objects
   * @returns Promise<boolean> - true if successful
   */
  setShortcuts(shortcuts: Shortcut[]): Promise<boolean> {
    return RNAppShortcuts.setShortcuts(shortcuts);
  },

  /**
   * Clear all dynamic shortcuts
   * 
   * @returns Promise<boolean> - true if successful
   */
  clearShortcuts(): Promise<boolean> {
    return RNAppShortcuts.clearShortcuts();
  },

  /**
   * Add a listener for when a shortcut is used while the app is running
   * 
   * @param callback - Function to call when a shortcut is used
   * @returns Function - Call to remove the listener
   */
  addListener(callback: (event: ShortcutEvent) => void) {
    const subscription = eventEmitter.addListener(
      'RNAppShortcuts:ShortcutUsed',
      callback
    );
    
    return () => subscription.remove();
  },
};

export default AppShortcuts;
