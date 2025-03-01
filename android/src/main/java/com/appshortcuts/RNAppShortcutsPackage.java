package com.appshortcuts;

import androidx.annotation.NonNull;

import com.facebook.react.ReactPackage;
import com.facebook.react.bridge.NativeModule;
import com.facebook.react.bridge.ReactApplicationContext;
import com.facebook.react.uimanager.ViewManager;
import com.facebook.react.module.model.ReactModuleInfo;
import com.facebook.react.module.model.ReactModuleInfoProvider;
import com.facebook.react.TurboReactPackage;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class RNAppShortcutsPackage extends TurboReactPackage {
    @NonNull
    @Override
    public NativeModule getModule(String name, @NonNull ReactApplicationContext reactContext) {
        if (name.equals(RNAppShortcutsModule.NAME)) {
            return new RNAppShortcutsModule(reactContext);
        } else {
            return null;
        }
    }

    @Override
    public ReactModuleInfoProvider getReactModuleInfoProvider() {
        return () -> {
            final Map<String, ReactModuleInfo> moduleInfos = new HashMap<>();
            boolean isTurboModule = BuildConfig.IS_NEW_ARCHITECTURE_ENABLED;
            moduleInfos.put(
                    RNAppShortcutsModule.NAME,
                    new ReactModuleInfo(
                            RNAppShortcutsModule.NAME,
                            RNAppShortcutsModule.NAME,
                            false, // canOverrideExistingModule
                            false, // needsEagerInit
                            true, // hasConstants
                            false, // isCxxModule
                            isTurboModule // isTurboModule
                    )
            );
            return moduleInfos;
        };
    }

    // For backward compatibility with RN < 0.71
    @NonNull
    @Override
    public List<NativeModule> createNativeModules(@NonNull ReactApplicationContext reactContext) {
        List<NativeModule> modules = new ArrayList<>();
        modules.add(new RNAppShortcutsModule(reactContext));
        return modules;
    }

    @NonNull
    @Override
    public List<ViewManager> createViewManagers(@NonNull ReactApplicationContext reactContext) {
        return Collections.emptyList();
    }
}
