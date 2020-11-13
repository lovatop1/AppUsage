package com.example.appusage;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import android.app.AppOpsManager;
import android.app.usage.UsageStats;
import android.app.usage.UsageStatsManager;
import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.provider.Settings;
import android.content.Intent;

import static android.app.AppOpsManager.MODE_ALLOWED;
import static android.app.AppOpsManager.OPSTR_GET_USAGE_STATS;
import static android.os.Process.myUid;

public class MainActivity extends AppCompatActivity {

    //private ProgressBar progressBar;
    private TextView permissionMessage;
    String grant_permission_message = "ACCESO CONCEDIDO";
    Context context;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        permissionMessage = (TextView) findViewById(R.id.center);

        permissionMessage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MainActivity.this.openSettings();
            }
        });

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            if (checkForPermission(null)) {
                MainActivity app1 = new MainActivity();
                app1.getInstalledApps();
                app1.getStats();
            } else {

            }
        }
    }


    //private static final String TAG = appUsage.class.getSimpleName();
    public PackageManager pm;
    public UsageStatsManager mUsageStatsManager;


    protected List<UsageStatsWrapper> getStats(){

        //tiempo inicial de referencia
        Calendar beginCal = Calendar.getInstance();
        //beginCal.set(Calendar.DATE, 0);
        beginCal.set(Calendar.MONTH, -1);
        //beginCal.set(Calendar.YEAR, 2020);

        Calendar endCal = Calendar.getInstance();
        //endCal.set(Calendar.DATE, 1);
        //endCal.set(Calendar.MONTH, 0);
        //endCal.set(Calendar.YEAR, 2020);

        List<String> installedApps = getInstalledApps(); //Lista de aplicaciones
        //
        List<UsageStats> stats = mUsageStatsManager.queryUsageStats(UsageStatsManager.INTERVAL_DAILY,
                beginCal.getTimeInMillis(), endCal.getTimeInMillis());
//        List<UsageStats> stats = new ArrayList<>();
//        stats.addAll(usageStats.values());

        List<UsageStatsWrapper> finalList = buildUsageStatsWrapper(installedApps, stats);

        //Log.i(TAG, "Tiempo de uso diario por mes:");
        System.out.print("Tiempo de uso diario por mes:");
        return finalList;
    }

    protected List<UsageStatsWrapper> buildUsageStatsWrapper(List<String> packageNames, List<UsageStats> usageStatses) {
        List<UsageStatsWrapper> list = new ArrayList<>();
        for (String name : packageNames) {
            boolean added = false;
            for (UsageStats stat : usageStatses) {
                if (name.equals(stat.getPackageName())) {
                    added = true;
                    list.add(fromUsageStat(stat));
                }
            }
            if (!added) {
                list.add(fromUsageStat(name));
            }
        }
        //Collections.sort(list);
        return list;
    }

    protected UsageStatsWrapper fromUsageStat(String packageName) throws IllegalArgumentException {
        try {
            ApplicationInfo ai = pm.getApplicationInfo(packageName, 0);
            return new UsageStatsWrapper(null, pm.getApplicationLabel(ai).toString());

        } catch (PackageManager.NameNotFoundException e) {
            throw new IllegalArgumentException(e);
        }
    }

    private UsageStatsWrapper fromUsageStat(UsageStats usageStats) throws IllegalArgumentException {
        try {
            ApplicationInfo ai = pm.getApplicationInfo(usageStats.getPackageName(), 0);
            return new UsageStatsWrapper(usageStats, pm.getApplicationLabel(ai).toString());

        } catch (PackageManager.NameNotFoundException e) {
            throw new IllegalArgumentException(e);
        }
    }

    protected List<String> getInstalledApps(){ //funcion que retorna la lista de aplicaciones con el nombre del paquete completo
        List<ApplicationInfo> apps = pm.getInstalledApplications(PackageManager.GET_META_DATA);
        List<String> installedApps = new ArrayList<>();
        for (ApplicationInfo info : apps){
            installedApps.add(info.packageName);
        }
        return installedApps;
    }

    @RequiresApi(api = Build.VERSION_CODES.Q)
    private boolean checkForPermission(Context context) {
        AppOpsManager appOps = (AppOpsManager) context.getSystemService(Context.APP_OPS_SERVICE);
        int mode = appOps.unsafeCheckOp (OPSTR_GET_USAGE_STATS, myUid(), context.getPackageName());
        return mode == MODE_ALLOWED;
    }

    private void openSettings() {
        startActivity(new Intent(Settings.ACTION_USAGE_ACCESS_SETTINGS));
    }
}