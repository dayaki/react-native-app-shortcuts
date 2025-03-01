import React, { useEffect, useState } from 'react';
import { View, Text, Button, Alert, StyleSheet } from 'react-native';
import AppShortcuts from 'react-native-app-shortcuts';

export default function AppShortcutsExample() {
  const [isSupported, setIsSupported] = useState<boolean | null>(null);

  useEffect(() => {
    // Check if shortcuts are supported
    AppShortcuts.isSupported().then(supported => {
      setIsSupported(supported);
    });

    // Check if app was launched from a shortcut
    AppShortcuts.getInitialShortcut().then(shortcut => {
      if (shortcut) {
        handleShortcut(shortcut.type);
      }
    });

    // Add listener for shortcuts used while app is running
    const removeListener = AppShortcuts.addListener(event => {
      handleShortcut(event.type);
    });

    // Clean up listener on unmount
    return () => {
      removeListener();
    };
  }, []);

  // Handle shortcut actions
  const handleShortcut = (type: string) => {
    Alert.alert('Shortcut Used', `You used the "${type}" shortcut`);
    
    // In a real app, you would navigate to the appropriate screen
    // or perform the action associated with the shortcut
    switch (type) {
      case 'new_message':
        // Navigate to new message screen
        console.log('Navigate to new message screen');
        break;
      case 'search':
        // Navigate to search screen
        console.log('Navigate to search screen');
        break;
      default:
        console.log(`Unknown shortcut type: ${type}`);
    }
  };

  // Set up example shortcuts
  const setupShortcuts = async () => {
    try {
      const success = await AppShortcuts.setShortcuts([
        {
          type: 'new_message',
          title: 'New Message',
          subtitle: 'Create a new message',
          iconName: 'compose_icon', // This should be in your app's assets
        },
        {
          type: 'search',
          title: 'Search',
          iconName: 'search_icon', // This should be in your app's assets
        },
      ]);
      
      Alert.alert(
        'Shortcuts Setup',
        success
          ? 'Shortcuts have been set up successfully!'
          : 'Failed to set up shortcuts.'
      );
    } catch (error) {
      Alert.alert('Error', `Failed to set up shortcuts: ${error}`);
    }
  };

  // Clear all shortcuts
  const clearShortcuts = async () => {
    try {
      const success = await AppShortcuts.clearShortcuts();
      
      Alert.alert(
        'Shortcuts Cleared',
        success
          ? 'All shortcuts have been cleared.'
          : 'Failed to clear shortcuts.'
      );
    } catch (error) {
      Alert.alert('Error', `Failed to clear shortcuts: ${error}`);
    }
  };

  if (isSupported === null) {
    return (
      <View style={styles.container}>
        <Text>Checking if shortcuts are supported...</Text>
      </View>
    );
  }

  if (!isSupported) {
    return (
      <View style={styles.container}>
        <Text>Shortcuts are not supported on this device.</Text>
      </View>
    );
  }

  return (
    <View style={styles.container}>
      <Text style={styles.title}>App Shortcuts Example</Text>
      
      <Text style={styles.description}>
        This example demonstrates how to use app shortcuts in your React Native app.
        After setting up shortcuts, press and hold your app icon on the home screen
        to see the shortcuts.
      </Text>
      
      <View style={styles.buttonContainer}>
        <Button title="Set Up Shortcuts" onPress={setupShortcuts} />
      </View>
      
      <View style={styles.buttonContainer}>
        <Button title="Clear Shortcuts" onPress={clearShortcuts} />
      </View>
    </View>
  );
}

const styles = StyleSheet.create({
  container: {
    flex: 1,
    alignItems: 'center',
    justifyContent: 'center',
    padding: 20,
  },
  title: {
    fontSize: 24,
    fontWeight: 'bold',
    marginBottom: 20,
  },
  description: {
    textAlign: 'center',
    marginBottom: 30,
  },
  buttonContainer: {
    marginVertical: 10,
    width: '80%',
  },
});
