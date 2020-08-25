/*
* Copyright (C) 2019 Eugen Rădulescu <synapticwebb@gmail.com> - All rights reserved.
*
* You may use, distribute and modify this code only under the conditions
* stated in the SW Call Recorder license. You should have received a copy of the
* SW Call Recorder license along with this file. If not, please write to <synapticwebb@gmail.com>.
*/

package net.synapticweb.callrecorder.recorder;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.telephony.TelephonyManager;
import android.util.Log;

import net.synapticweb.callrecorder.CrApp;
import net.synapticweb.callrecorder.settings.SettingsFragment;


public class CallReceiver extends BroadcastReceiver {
    public static final String ARG_INCOMING = "arg_incoming";
    public static final String ARG_NUM_PHONE = "arg_num_phone";
    private static final String TAG = "CallRecorder";
    private static boolean stateProcessed = false;

    public void onReceive(Context context, Intent intent) {
        String action = intent.getAction();
        boolean z = PreferenceManager.getDefaultSharedPreferences(CrApp.getInstance()).getBoolean(SettingsFragment.ENABLED, true);
        if (action != null && action.equals("android.intent.action.PHONE_STATE")) {
            Bundle extras = intent.getExtras();
            if (extras != null) {
                String string = extras.getString("state");
                StringBuilder sb = new StringBuilder();
                sb.append(intent.getAction());
                sb.append(" ");
                sb.append(string);
                String sb2 = sb.toString();
                String str = TAG;
                Log.d(str, sb2);
                String str2 = ARG_INCOMING;
                if (string != null && string.equals(TelephonyManager.EXTRA_STATE_RINGING)) {
                    String string2 = extras.getString("incoming_number");
                    StringBuilder sb3 = new StringBuilder();
                    sb3.append("Incoming number: ");
                    sb3.append(string2);
                    Log.d(str, sb3.toString());
                    if (!stateProcessed && z) {
                        if (Build.VERSION.SDK_INT < 29) {
                            Intent intent2 = new Intent(context, RecorderService.class);
                            CrApp.setServiceName(intent2.getComponent());
                            intent2.putExtra(ARG_NUM_PHONE, string2);
                            intent2.putExtra(str2, true);
                            if (Build.VERSION.SDK_INT >= 26) {
                                context.startForegroundService(intent2);
                            } else {
                                context.startService(intent2);
                            }
                            CrApp.setServiceStarted(true);
                        } else {
                            CrApp.setIncomingCall(Boolean.valueOf(true));
                            CrApp.setPhoneNumber(string2);
                        }
                        stateProcessed = true;
                    }
                } else if (string == null || !string.equals(TelephonyManager.EXTRA_STATE_OFFHOOK)) {
                    if (string != null && string.equals(TelephonyManager.EXTRA_STATE_IDLE)) {
                        if (CrApp.isServiceStarted()) {
                            Intent intent3 = new Intent(context, RecorderService.class);
                            intent3.setComponent(CrApp.getServiceName());
                            context.stopService(intent3);
                            CrApp.setServiceStarted(false);
                        }
                        if (stateProcessed) {
                            CrApp.setIncomingCall(null);
                            CrApp.setPhoneNumber(null);
                            CrApp.setServiceName(null);
                            stateProcessed = false;
                        }
                    }
                } else if (!stateProcessed && z) {
                    if (Build.VERSION.SDK_INT < 29) {
                        Intent intent4 = new Intent(context, RecorderService.class);
                        CrApp.setServiceName(intent4.getComponent());
                        intent4.putExtra(str2, false);
                        if (Build.VERSION.SDK_INT >= 26) {
                            context.startForegroundService(intent4);
                        } else {
                            context.startService(intent4);
                        }
                        CrApp.setServiceStarted(true);
                    } else {
                        CrApp.setIncomingCall(Boolean.valueOf(false));
                    }
                    stateProcessed = true;
                }
            }
        }
    }
}