/**
 * Sample React Native App
 * https://github.com/facebook/react-native
 *
 * @format
 */

import React from 'react';
import {
  NativeModules,
  Pressable,
  SafeAreaView,
  ScrollView,
  StyleSheet,
  Text
} from 'react-native';
import MetricAfricaModule from './MetricAfricaModule';

// NativeModules.RTNMetricAfrica.


function App(): React.JSX.Element {
  const CLIENT_KEY = "I0GB6GIe0PEVNRy9D6Ct";
  const CLIENT_SECRET = "FnZ7VtLwbSH75Zt7UuNCNinJdTmYHvdQb3dZHFNInDMN0";

  async function initVerification(token: string) {
    MetricAfricaModule.initializeVerification(token)
  }

  async function initMetricSdk(clientId: string, clientSecret: string) {
    MetricAfricaModule.initializeMetricAfricaSdk(clientId, clientSecret)
  }

  return (
    <SafeAreaView style={styles.container}>

      <ScrollView
        contentInsetAdjustmentBehavior="automatic"
        contentContainerStyle={styles.container}>
        <Pressable onPress={() => {
          MetricAfricaModule.askForCameraPermission().then(() => {
            initMetricSdk(CLIENT_KEY, CLIENT_SECRET).then(() => {
              initVerification("13JN7OWUJ")
            })
          })

        }}>
          <Text>Pressable</Text>
        </Pressable>
      </ScrollView>
    </SafeAreaView>
  );
}

const styles = StyleSheet.create({
  container: {
    flex: 1,
    justifyContent: 'center',
    alignItems: 'center'
  },
  sectionContainer: {
    marginTop: 32,
    paddingHorizontal: 24,
  },
  sectionTitle: {
    fontSize: 24,
    fontWeight: '600',
  },
  sectionDescription: {
    marginTop: 8,
    fontSize: 18,
    fontWeight: '400',
  },
  highlight: {
    fontWeight: '700',
  },
});

export default App;
