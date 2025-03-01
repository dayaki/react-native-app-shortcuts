import type { TurboModule } from 'react-native';
import { TurboModuleRegistry } from 'react-native';

export interface Spec extends TurboModule {
  /**
   * Check if shortcuts are supported on the device
   */
  isSupported(): Promise<boolean>;

  /**
   * Get the shortcut used to launch the app
   */
  getInitialShortcut(): Promise<{ shortcutId: string } | null>;

  /**
   * Set dynamic shortcuts
   */
  setShortcuts(shortcuts: Array<{
    id: string;
    title: string;
    subtitle?: string;
    iconName?: string;
    data?: Object;
  }>): Promise<boolean>;

  /**
   * Clear all dynamic shortcuts
   */
  clearShortcuts(): Promise<boolean>;

  /**
   * Add event listener for shortcut used events
   */
  addListener(eventName: string): void;

  /**
   * Remove event listener for shortcut used events
   */
  removeListeners(count: number): void;
}

export default TurboModuleRegistry.getEnforcing<Spec>('RNAppShortcuts');
