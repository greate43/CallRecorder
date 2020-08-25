package net.synapticweb.callrecorder.recorder;

import android.accessibilityservice.AccessibilityService;
import android.app.Service;
import android.content.Intent;
import android.os.Build;
import android.util.Log;
import android.view.accessibility.AccessibilityEvent;

import androidx.annotation.RequiresApi;

import net.synapticweb.callrecorder.CrApp;

public class StartAccessibilityService extends AccessibilityService {
    private static final String TAG = "CallRecorder";

    public void onInterrupt() {
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    public void onAccessibilityEvent(AccessibilityEvent accessibilityEvent) {
        Log.d(TAG, "onAccesibilityEvent called");
        if (accessibilityEvent.getEventType() == 64 && CrApp.getIncomingCall() != null && !CrApp.isServiceStarted()) {
            CrApp instance = CrApp.getInstance();
            Intent intent = new Intent(instance, RecorderService.class);
            CrApp.setServiceName(intent.getComponent());
            intent.putExtra(CallReceiver.ARG_NUM_PHONE, CrApp.getPhoneNumber());
            intent.putExtra(CallReceiver.ARG_INCOMING, CrApp.getIncomingCall());
            instance.startForegroundService(intent);
            CrApp.setServiceStarted(true);
        }
    }
}
