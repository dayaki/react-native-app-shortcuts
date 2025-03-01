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
public class RNAppShortcutsModule extends ReactContextBaseJavaModule implements ActivityEventListener, LifecycleEventListener {
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

    @ReactMethod
    public void isSupported(Promise promise) {
        promise.resolve(Build.VERSION.SDK_INT >= Build.VERSION_CODES.N_MR1);
    }

    @ReactMethod
    public void getInitialShortcut(Promise promise) {
        if (initialShortcutId != null && !initialShortcutChecked) {
            WritableMap map = Arguments.createMap();
            map.putString("type", initialShortcutId);
            initialShortcutChecked = true;
            promise.resolve(map);
        } else {
            promise.resolve(null);
        }
    }

    @ReactMethod
    public void setShortcuts(ReadableArray shortcutsArray, Promise promise) {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.N_MR1) {
            promise.resolve(false);
            return;
        }

        Activity activity = getCurrentActivity();
        if (activity == null) {
            promise.reject("activity_null", "Activity is null");
            return;
        }

        Context context = getReactApplicationContext();
        List<ShortcutInfoCompat> shortcuts = new ArrayList<>();

        for (int i = 0; i < shortcutsArray.size(); i++) {
            ReadableMap shortcut = shortcutsArray.getMap(i);
            String type = shortcut.getString("type");
            String title = shortcut.getString("title");
            String iconName = shortcut.hasKey("iconName") ? shortcut.getString("iconName") : null;

            if (type == null || title == null) {
                promise.reject("invalid_shortcut", "Shortcut must contain at least type and title");
                return;
            }

            Intent intent = new Intent(context, activity.getClass());
            intent.setAction(Intent.ACTION_VIEW);
            intent.putExtra("shortcut_id", type);

            ShortcutInfoCompat.Builder shortcutInfoBuilder = new ShortcutInfoCompat.Builder(context, type)
                    .setShortLabel(title)
                    .setIntent(intent);

            if (shortcut.hasKey("subtitle")) {
                shortcutInfoBuilder.setLongLabel(shortcut.getString("subtitle"));
            }

            if (iconName != null) {
                int resourceId = context.getResources().getIdentifier(
                        iconName, "drawable", context.getPackageName());
                if (resourceId != 0) {
                    shortcutInfoBuilder.setIcon(IconCompat.createWithResource(context, resourceId));
                }
            }

            shortcuts.add(shortcutInfoBuilder.build());
        }

        boolean result = ShortcutManagerCompat.setDynamicShortcuts(context, shortcuts);
        promise.resolve(result);
    }

    @ReactMethod
    public void clearShortcuts(Promise promise) {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.N_MR1) {
            promise.resolve(false);
            return;
        }

        Context context = getReactApplicationContext();
        ShortcutManagerCompat.removeAllDynamicShortcuts(context);
        promise.resolve(true);
    }

    private void sendShortcutEvent(String shortcutId) {
        WritableMap params = Arguments.createMap();
        params.putString("type", shortcutId);
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
        if (intent != null && intent.hasExtra("shortcut_id")) {
            String shortcutId = intent.getStringExtra("shortcut_id");
            if (shortcutId != null) {
                if (getReactApplicationContext().hasActiveReactInstance()) {
                    sendShortcutEvent(shortcutId);
                } else {
                    initialShortcutId = shortcutId;
                }
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
