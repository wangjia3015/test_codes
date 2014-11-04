package com.example.Demo1;

import android.app.Activity;
import android.app.ActivityManager;
import android.app.Application;
import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Debug;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ListAdapter;
import android.widget.SimpleAdapter;

import java.util.*;

public class MainActivity extends Activity implements Button.OnClickListener {

    private final static String TAG = "MainActivity";

    protected int[] btn_res_ids = {R.id.process_list_btn };

    /**
     * Called when the activity is first created.
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        initViews();
    }

    protected void initViews() {
        for (int id : btn_res_ids) {
            findViewById(id).setOnClickListener(this);
        }

        List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();

        List<AppInfo> appinfos = loadPackage();
        String[] from = new String[]{};
        int[] to = new int[]{};
        ListAdapter adapter = new SimpleAdapter(getApplicationContext(), list, 0, from, to);

    }

    @Override
    public void onClick(View view) {
    }

    public void showMsg(String msg) {
        Log.i(TAG, msg);
    }

    public class AppInfo {
        public String package_name;
        public String app_name;
        public int version_code;
        public String version_name;
        public Drawable icon_image;
    }

    protected List<AppInfo> loadPackage() {

        List<AppInfo> appInfos = new ArrayList<AppInfo>();

        PackageManager manager = getPackageManager();
        List<PackageInfo> packages = manager.getInstalledPackages(0);
        for (PackageInfo info : packages) {
            AppInfo appinfo = new AppInfo();
            appinfo.app_name = info.applicationInfo.loadLabel(manager).toString();
            appinfo.icon_image = info.applicationInfo.loadIcon(manager);
            appinfo.package_name = info.packageName;
            appinfo.version_code = info.versionCode;
            appinfo.version_name = info.versionName;
            appInfos.add(appinfo);


            String fmt = String.format("package : %s, packageName %s" , info.packageName, info.applicationInfo.loadLabel(manager).toString());
            showMsg(fmt);
        }
        return appInfos;
    }


}
