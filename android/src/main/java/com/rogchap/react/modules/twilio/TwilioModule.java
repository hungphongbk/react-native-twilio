package com.rogchap.react.modules.twilio;

import javax.annotation.Nullable;

import android.Manifest;
import android.util.Log;
import android.app.PendingIntent;
import android.content.Intent;

import com.facebook.react.bridge.NativeModule;
import com.facebook.react.bridge.ReactContextBaseJavaModule;
import com.facebook.react.bridge.ReactApplicationContext;
import com.facebook.react.bridge.ReactContext;
import com.facebook.react.bridge.ReadableMap;
import com.facebook.react.bridge.ReadableType;
import com.facebook.react.bridge.ReadableMapKeySetIterator;
import com.facebook.react.bridge.ReactMethod;
import com.facebook.react.bridge.Arguments;
import com.facebook.react.bridge.WritableMap;
import com.facebook.react.modules.core.RCTNativeAppEventEmitter;


import com.twilio.client.Connection;
import com.twilio.client.ConnectionListener;
import com.twilio.client.Device;
import com.twilio.client.DeviceListener;
import com.twilio.client.PresenceEvent;
import com.twilio.client.Twilio;

import java.net.URL;
import java.net.URLConnection;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.BufferedReader;
import java.util.HashMap;
import java.util.Map;


public class TwilioModule extends ReactContextBaseJavaModule implements DeviceListener, ConnectionListener {

  private static final String TAG = TwilioModule.class.getName();

  private ReactContext _reactContext;
  private Device _phone;
  private Connection _connection;
  private Connection _pendingConnection;

  public TwilioModule(ReactApplicationContext reactContext) {
    super(reactContext);
    _reactContext = reactContext;
  }

  private void sendEvent(String eventName, @Nullable WritableMap params) {
    Log.d(TAG, "event sent " + eventName);
    _reactContext
      .getJSModule(RCTNativeAppEventEmitter.class)
      .emit(eventName, params);
  }

  @Override
  public String getName() {
    return "Twilio";
  }

  @ReactMethod
  public void initWithTokenUrl(String tokenUrl) {
    StringBuilder sb = new StringBuilder();
    try {
      URLConnection conn = new URL(tokenUrl).openConnection();
      InputStream in = conn.getInputStream();
      BufferedReader reader = new BufferedReader(new InputStreamReader(in, "UTF-8"));
      String line = reader.readLine();
      while (line != null) {
        sb.append(line);
      }
    } catch (Exception e) {
      Log.e(TAG, e.getMessage());
    }
    initWithToken(sb.toString());
  }

  @ReactMethod
  public void initWithToken(final String capabilityToken) {
    if (!Twilio.isInitialized()) {
      Twilio.initialize(_reactContext.getApplicationContext(), new Twilio.InitListener() {
        @Override
        public void onInitialized() {
          try {
            createDevice(capabilityToken);
          } catch (Exception e) {
            Log.e(TAG, e.getMessage());
          }
        }

        @Override
        public void onError(Exception e) {
          Log.e(TAG, e.getMessage());
        }
      });
    } else {
      sendEvent("deviceReady", null);
    }
  }

  /*
   * Create a Device or update the capabilities of the current Device
   */
  private void createDevice(String capabilityToken) {
    try {
      if (_phone == null) {
        _phone = Twilio.createDevice(capabilityToken, this);

        /*
         * Providing a PendingIntent to the newly create Device, allowing you to receive incoming calls
         *
         *  What you do when you receive the intent depends on the component you set in the Intent.
         *
         *  If you're using an Activity, you'll want to override Activity.onNewIntent()
         *  If you're using a Service, you'll want to override Service.onStartCommand().
         *  If you're using a BroadcastReceiver, override BroadcastReceiver.onReceive().
         */

        Intent intent = new Intent(_reactContext.getApplicationContext(), TwilioModule.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(_reactContext.getApplicationContext(), 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        _phone.setIncomingIntent(pendingIntent);
        sendEvent("deviceReady", null);
      } else {
        _phone.updateCapabilityToken(capabilityToken);
        sendEvent("deviceUpdated", null);
      }

      // TextView clientNameTextView = (TextView) capabilityPropertiesView.findViewById(R.id.client_name_registered_text);
      // clientNameTextView.setText("Client Name: " + TwilioModule.this.clientProfile.getName());

      // TextView outgoingCapabilityTextView = (TextView) capabilityPropertiesView.findViewById(R.id.outgoing_capability_registered_text);
      // outgoingCapabilityTextView.setText("Outgoing Capability: " +Boolean.toString(TwilioModule.this.clientProfile.isAllowOutgoing()));

      // TextView incomingCapabilityTextView = (TextView) capabilityPropertiesView.findViewById(R.id.incoming_capability_registered_text);
      // incomingCapabilityTextView.setText("Incoming Capability: " +Boolean.toString(TwilioModule.this.clientProfile.isAllowIncoming()));

    } catch (Exception e) {
      Log.e(TAG, "An error has occurred updating or creating a Device: \n" + e.toString());
    }
  }

  @ReactMethod
  public void connect(ReadableMap readableMap) {
    if (_phone != null) {
      _connection = _phone.connect(covnertToNativeMap(readableMap), this);
    } else {
      Log.e(TAG, "Device is null");
    }
  }

  private Map<String, String> covnertToNativeMap(ReadableMap readableMap) {
    Map<String, String> hashMap = new HashMap<String, String>();
    ReadableMapKeySetIterator iterator = readableMap.keySetIterator();
    while (iterator.hasNextKey()) {
      String key = iterator.nextKey();
      ReadableType readableType = readableMap.getType(key);
      switch (readableType) {
        case String:
          hashMap.put(key, readableMap.getString(key));
          break;
        default:
          throw new IllegalArgumentException("Could not convert object with key: " + key + ".");
      }
    }
    return hashMap;
  }

  @ReactMethod
  public void disconnect() {
    if (_connection != null) {
      _connection.disconnect();
      _connection = null;
    }
  }

  @ReactMethod
  public void accept() {
    _pendingConnection.accept();
    _pendingConnection.setConnectionListener(this);
    _connection = _pendingConnection;
    _pendingConnection = null;
  }

  @ReactMethod
  public void reject() {
    _pendingConnection.reject();
  }

  @ReactMethod
  public void ignore() {
    _pendingConnection.ignore();
  }

  @ReactMethod
  public void setMuted(Boolean isMuted) {
    if (_connection != null && _connection.getState() == Connection.State.CONNECTED) {
      _connection.setMuted(isMuted);
    }
  }

  /* Device Listener */
  @Override
  public void onStartListening(Device device) {
    Log.d(TAG, "Device has started listening for incoming connections");
    sendEvent("deviceDidStartListening", null);
  }

  /* Device Listener */
  @Override
  public void onStopListening(Device device) {
    Log.d(TAG, "Device has stopped listening for incoming connections");
    sendEvent("deviceDidStopListening", null);
  }

  /* Device Listener */
  @Override
  public void onStopListening(Device device, int errorCode, String error) {
    Log.e(TAG, String.format("Device has encountered an error and has stopped" +
            " listening for incoming connections: %s", error));
    sendEvent("deviceDidStopListening", null);
  }

  /* Device Listener */
  @Override
  public boolean receivePresenceEvents(Device device) {
    return false;
  }

  /* Device Listener */
  @Override
  public void onPresenceChanged(Device device, PresenceEvent presenceEvent) {

  }

  /* ConnectionListener */

  @Override
  public void onConnecting(Connection connection) {
    Map<String, String> connParams = connection.getParameters();
    WritableMap params = Arguments.createMap();
    for (Map.Entry<String, String> entry : connParams.entrySet()) {
      params.putString(entry.getKey(), entry.getValue());
    }
    sendEvent("connectionDidStartConnecting", params);
  }

  @Override
  public void onConnected(Connection connection) {
    Map<String, String> connParams = connection.getParameters();
    WritableMap params = Arguments.createMap();
    for (Map.Entry<String, String> entry : connParams.entrySet()) {
      params.putString(entry.getKey(), entry.getValue());
    }
    sendEvent("connectionDidConnect", params);
  }

  @Override
  public void onDisconnected(Connection connection) {
    if (connection == _connection) {
      _connection = null;
    }
    if (connection == _pendingConnection) {
      _pendingConnection = null;
    }
    Map<String, String> connParams = connection.getParameters();
    WritableMap params = Arguments.createMap();
    for (Map.Entry<String, String> entry : connParams.entrySet()) {
      params.putString(entry.getKey(), entry.getValue());
    }
    sendEvent("connectionDidDisconnect", params);
  }

  @Override
  public void onDisconnected(Connection connection, int errorCode, String errorMessage) {
    WritableMap errors = Arguments.createMap();
    errors.putString("err", errorMessage);
    sendEvent("connectionDidFail", errors);
  }
}
