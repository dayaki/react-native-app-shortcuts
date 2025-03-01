package com.appshortcuts;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ShortcutInfo;
import android.content.pm.ShortcutManager;
import android.graphics.drawable.Icon;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.pm.ShortcutInfoCompat;
import androidx.core.content.pm.ShortcutManagerCompat;
import androidx.core.graphics.drawable.IconCompat;

import com.facebook.react.bridge.ActivityEventListener;
import com.facebook.react.bridge.Arguments;
import com.facebook.react.bridge.LifecycleEventListener;
import com.facebook.react.bridge.Promise;
import com.facebook.react.bridge.ReactApplicationContext;
import com.facebook.react.bridge.ReactContextBaseJavaModule;
import com.facebook.react.bridge.ReactMethod;
import com.facebook.react.bridge.ReadableArray;
import com.facebook.react.bridge.ReadableMap;
import com.facebook.react.bridge.WritableMap;
import com.facebook.react.module.annotations.ReactModule;
import com.facebook.react.modules.core.DeviceEventManagerModule;

import java.util.ArrayList;
import java.util.List;

@ReactModule(name = RNAppShortcutsModule.NAME)
public class RNAppShortcutsModule extends ReactContextBaseJavaModule implements ActivityEventListener, LifecycleEventListener, RNAppShortcutsSpec {
    public static final String NAME = "RNAppShortcuts";
    private static final String SHORTCUT_USED_EVENT = "RNAppShortcuts:ShortcutUsed";
    private static String initialShortcutId = null;
    private boolean initialShortcutChecked = false;

    public RNAppShortcutsModule(ReactApplicationContext reactContext) {
        super(reactContext);
        reactContext.addActivityEventListener(this);
        reactContext.addLifecycleEventListener(this);
    }

    @Override
    @NonNull
    public String getName() {
        return NAME;
    }

    @Override
    public void isSupported(Promise promise) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N_MR1) {
            promise.resolve(true);
        } else {
            promise.resolve(false);
        }
    }

    @Override
    public void getInitialShortcut(Promise promise) {
        if (initialShortcutId != null) {
            WritableMap map = Arguments.createMap();
            map.putString("shortcutId", initialShortcutId);
            promise.resolve(map);
        } else {
            promise.resolve(null);
        }
    }

    @Override
    public void setShortcuts(ReadableArray shortcutsArray, Promise promise) {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.N_MR1) {
            promise.resolve(false);
            return;
        }

        try {
            Context context = getReactApplicationContext();
            ShortcutManager shortcutManager = context.getSystemService(ShortcutManager.class);
            List<ShortcutInfo> shortcuts = new ArrayList<>();

            for (int i = 0; i < shortcutsArray.size(); i++) {
                ReadableMap shortcut = shortcutsArray.getMap(i);
                String id = shortcut.getString("id");
                String title = shortcut.getString("title");
                String subtitle = shortcut.hasKey("subtitle") ? shortcut.getString("subtitle") : null;
                String iconName = shortcut.hasKey("iconName") ? shortcut.getString("iconName") : null;

                ShortcutInfo.Builder shortcutInfoBuilder = new ShortcutInfo.Builder(context, id)
                        .setShortLabel(title);

                if (subtitle != null) {
                    shortcutInfoBuilder.setLongLabel(subtitle);
                }

                if (iconName != null) {
                    int resourceId = context.getResources().getIdentifier(
                            iconName,
                            "drawable",
                            context.getPackageName()
                    );
                    if (resourceId != 0) {
                        shortcutInfoBuilder.setIcon(Icon.createWithResource(context, resourceId));
                    }
                }

                Intent intent = new Intent(context, context.getClass());
                intent.setAction(Intent.ACTION_VIEW);
                intent.putExtra("shortcutId", id);

                shortcutInfoBuilder.setIntent(intent);
                shortcuts.add(shortcutInfoBuilder.build());
            }

            shortcutManager.setDynamicShortcuts(shortcuts);
            promise.resolve(true);
        } catch (Exception e) {
            promise.reject("SET_SHORTCUTS_ERROR", e.getMessage());
        }
    }

    @Override
    public void clearShortcuts(Promise promise) {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.N_MR1) {
            promise.resolve(false);
            return;
        }

        try {
            Context context = getReactApplicationContext();
            ShortcutManager shortcutManager = context.getSystemService(ShortcutManager.class);
            shortcutManager.removeAllDynamicShortcuts();
            promise.resolve(true);
        } catch (Exception e) {
            promise.reject("CLEAR_SHORTCUTS_ERROR", e.getMessage());
        }
    }

    @Override
    public void addListener(String eventName) {
        // Keep: Required for RN built in Event Emitter Calls
    }

    @Override
    public void removeListeners(double count) {
        // Keep: Required for RN built in Event Emitter Calls
    }

    private void sendShortcutEvent(String shortcutId) {
        WritableMap params = Arguments.createMap();
        params.putString("shortcutId", shortcutId);
        getReactApplicationContext()
                .getJSModule(DeviceEventManagerModule.RCTDeviceEventEmitter.class)
                .emit(SHORTCUT_USED_EVENT, params);
    }

    @Override
    public void onActivityResult(Activity activity, int requestCode, int resultCode, Intent data) {
        // Not used
    }

    @Override
    public void onNewIntent(Intent intent) {
        handleIntent(intent);
    }

    private void handleIntent(Intent intent) {
        if (intent != null && intent.hasExtra("shortcutId")) {
            String shortcutId = intent.getStringExtra("shortcutId");
            if (getReactApplicationContext().hasActiveReactInstance()) {
                sendShortcutEvent(shortcutId);
            } else {
                initialShortcutId = shortcutId;
            }
        }
    }

    @Override
    public void onHostResume() {
        Activity activity = getCurrentActivity();
        if (activity != null) {
            handleIntent(activity.getIntent());
        }
    }

    @Override
    public void onHostPause() {
        // Not used
    }

    @Override
    public void onHostDestroy() {
        // Not used
    }
}
