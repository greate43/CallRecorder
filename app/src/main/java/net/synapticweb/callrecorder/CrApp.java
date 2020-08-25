/*
 * Copyright (C) 2019 Eugen Rădulescu <synapticwebb@gmail.com> - All rights reserved.
 *
 * You may use, distribute and modify this code only under the conditions
 * stated in the SW Call Recorder license. You should have received a copy of the
 * SW Call Recorder license along with this file. If not, please write to <synapticwebb@gmail.com>.
 */

package net.synapticweb.callrecorder;

import android.accessibilityservice.AccessibilityService;
import android.accessibilityservice.AccessibilityServiceInfo;
import android.app.Application;
import android.content.ComponentName;
import android.content.Context;
import android.content.pm.ServiceInfo;
import android.view.accessibility.AccessibilityManager;

import net.synapticweb.callrecorder.di.AppComponent;
import net.synapticweb.callrecorder.di.DaggerAppComponent;

import org.acra.ACRA;
import org.acra.annotation.AcraCore;
import org.acra.annotation.AcraHttpSender;
import org.acra.data.StringFormat;
import org.acra.sender.HttpSender;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.List;

//Oferă context cînd nu este nicio activitate disponibilă. Are nevoie ca să funcționeze de
// android:name=".CrApp" în AndroidManifest.xml
//Servește și ca bibliotecă a aplicației.
@AcraCore(reportFormat = StringFormat.KEY_VALUE_LIST)
@AcraHttpSender(uri = "http://crashes.infopsihologia.ro",
        httpMethod = HttpSender.Method.POST )
public class CrApp extends Application {
    public AppComponent appComponent;
    private static Boolean incomingCall = null;
    private static String phoneNumber = null;
    private static ComponentName serviceName = null;
    private static boolean serviceStarted = false;
    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
        if(!BuildConfig.DEBUG)
            ACRA.init(this);
        appComponent = DaggerAppComponent.factory().create(base);
    }

    private static CrApp instance;

    public CrApp() {
        instance = this;
    }

    @Override
    public void onCreate() {
        super.onCreate();
//        Shell.Config.setFlags(Shell.FLAG_REDIRECT_STDERR);
//        Shell.Config.verboseLogging(BuildConfig.DEBUG);
    }

    public static CrApp getInstance() {
        return instance;
    }
    public static boolean isServiceStarted() {
        return serviceStarted;
    }

    public static void setServiceStarted(boolean z) {
        serviceStarted = z;
    }

    public static Boolean getIncomingCall() {
        return incomingCall;
    }

    public static void setIncomingCall(Boolean bool) {
        incomingCall = bool;
    }

    public static String getPhoneNumber() {
        return phoneNumber;
    }

    public static void setPhoneNumber(String str) {
        phoneNumber = str;
    }

    public static ComponentName getServiceName() {
        return serviceName;
    }

    public static void setServiceName(ComponentName componentName) {
        serviceName = componentName;
    }
    public static String rawHtmlToString(int i) {
        StringBuilder sb = new StringBuilder();
        try {
            InputStream openRawResource = getInstance().getResources().openRawResource(i);
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(openRawResource, StandardCharsets.UTF_8));
            while (true) {
                String readLine = bufferedReader.readLine();
                if (readLine == null) {
                    break;
                }
                sb.append(readLine);
            }
            bufferedReader.close();
            openRawResource.close();
        } catch (Exception e) {
            StringBuilder sb2 = new StringBuilder();
            sb2.append("Error converting raw html to string: ");
            sb2.append(e.getMessage());
            CrLog.log(CrLog.ERROR, sb2.toString());
        }
        return sb.toString();
    }
    public static boolean isAccessibilityServiceEnabled(Class<? extends AccessibilityService> cls) {
        AccessibilityManager accessibilityManager = (AccessibilityManager) instance.getSystemService("accessibility");
        List<AccessibilityServiceInfo> enabledAccessibilityServiceList = accessibilityManager != null ? accessibilityManager.getEnabledAccessibilityServiceList(-1) : null;
        if (enabledAccessibilityServiceList != null) {
            for (AccessibilityServiceInfo resolveInfo : enabledAccessibilityServiceList) {
                ServiceInfo serviceInfo = resolveInfo.getResolveInfo().serviceInfo;
                if (serviceInfo.packageName.equals(instance.getPackageName()) && serviceInfo.name.equals(cls.getName())) {
                    return true;
                }
            }
        }
        return false;
    }
}
