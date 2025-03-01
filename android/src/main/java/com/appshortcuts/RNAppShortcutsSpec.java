package com.appshortcuts;

import com.facebook.react.bridge.Promise;
import com.facebook.react.bridge.ReactApplicationContext;
import com.facebook.react.bridge.ReadableArray;
import com.facebook.react.turbomodule.core.interfaces.TurboModule;

/**
 * TurboModule spec for RNAppShortcuts
 */
public interface RNAppShortcutsSpec extends TurboModule {
    /**
     * Check if shortcuts are supported on the device
     */
    void isSupported(Promise promise);

    /**
     * Get the shortcut used to launch the app
     */
    void getInitialShortcut(Promise promise);

    /**
     * Set dynamic shortcuts
     */
    void setShortcuts(ReadableArray shortcuts, Promise promise);

    /**
     * Clear all dynamic shortcuts
     */
    void clearShortcuts(Promise promise);

    /**
     * Add event listener for shortcut used events
     */
    void addListener(String eventName);

    /**
     * Remove event listener for shortcut used events
     */
    void removeListeners(double count);
}
