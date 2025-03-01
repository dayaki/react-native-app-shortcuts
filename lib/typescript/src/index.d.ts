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
declare const AppShortcuts: {
    /**
     * Check if shortcuts are supported on the device
     *
     * @returns Promise<boolean> - true if shortcuts are supported
     */
    isSupported(): Promise<boolean>;
    /**
     * Get the shortcut that was used to launch the app (if any)
     *
     * @returns Promise<ShortcutEvent | null> - The shortcut event or null
     */
    getInitialShortcut(): Promise<ShortcutEvent | null>;
    /**
     * Set dynamic shortcuts for the app
     *
     * @param shortcuts - Array of shortcut objects
     * @returns Promise<boolean> - true if successful
     */
    setShortcuts(shortcuts: Shortcut[]): Promise<boolean>;
    /**
     * Clear all dynamic shortcuts
     *
     * @returns Promise<boolean> - true if successful
     */
    clearShortcuts(): Promise<boolean>;
    /**
     * Add a listener for when a shortcut is used while the app is running
     *
     * @param callback - Function to call when a shortcut is used
     * @returns Function - Call to remove the listener
     */
    addListener(callback: (event: ShortcutEvent) => void): () => void;
};
export default AppShortcuts;
//# sourceMappingURL=index.d.ts.map