package com.rogchap.react.modules.twilio;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.facebook.react.bridge.Callback;
import com.facebook.react.bridge.ReactApplicationContext;
import com.facebook.react.bridge.ReactContextBaseJavaModule;
import com.facebook.react.bridge.ReactMethod;
import com.facebook.react.bridge.ReadableMap;
import com.twilio.Twilio;
import com.twilio.rest.api.v2010.account.Message;

import org.apache.http.auth.AUTH;

public class TwilioModuleV2 extends ReactContextBaseJavaModule {
    private String _SERVICE_SID;
//    private IntentReceiver _receiver;
//    public class IntentReceiver extends BroadcastReceiver{
//        @Override
//        public void onReceive(Context context, Intent intent) {
//
//        }
//    }

    public TwilioModuleV2(ReactApplicationContext reactContext) {
        super(reactContext);
//        this._receiver=new IntentReceiver()
    }

    @Override
    public String getName() {
        return "Twilio";
    }

    @ReactMethod
    public void init(final String ACCOUNT_SID, final String AUTH_TOKEN, final String SERVICE_SID) {
        Twilio.init(ACCOUNT_SID, AUTH_TOKEN);
        this._SERVICE_SID = SERVICE_SID;
    }

    @ReactMethod
    public void sendMessage(final ReadableMap payload, Callback errorCallback, Callback successCallback) {
        if (!this._checkMessagePayload(payload, errorCallback)) return;
        String phoneNumber = payload.getString("phoneNumber");
        String body = payload.getString("body");
        Message message = Message.creator(new com.twilio.type.PhoneNumber(phoneNumber), this._SERVICE_SID, body).create();

        successCallback.invoke(message.getSid());
    }

    private boolean _checkMessagePayload(final ReadableMap payload, Callback errorCallback) {
        if (!payload.hasKey("phoneNumber")) {
            errorCallback.invoke("Please provide phoneNumber field in message payload!");
            return false;
        }
        if (!payload.hasKey("body")) {
            errorCallback.invoke("Please provide body field in message payload!");
            return false;
        }
        return true;
    }
}
