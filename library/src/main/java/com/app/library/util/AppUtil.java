package com.app.library.util;

import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.WifiManager;
import android.support.v4.content.LocalBroadcastManager;
import android.telephony.TelephonyManager;

import com.app.library.lg.Lg;

import java.net.NetworkInterface;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;



public class AppUtil {
    public final static String PKName = "com.pingan.pinganwifi";
    private final static String DEFAULT_KEY = "com.default.unknowen.pavpn";

    /**
     * 得到手机上安装的包名和对应uid
     */

    public static Map<String, String> getUidsAndPackageNames(Context context) {
        Map<String, String> uidList = new HashMap<String, String>();
        PackageManager pm = context.getPackageManager();
        List<PackageInfo> packinfos = pm.getInstalledPackages(PackageManager.GET_UNINSTALLED_PACKAGES
                | PackageManager.GET_PERMISSIONS);
        for (PackageInfo info : packinfos) {
            String[] premissions = info.requestedPermissions;
            if (premissions != null && premissions.length > 0) {
                for (String premission : premissions) {
                    if ("android.permission.INTERNET".equals(premission)) {
                        // System.out.println(info.packageName+"访问网络");
                        int uid = info.applicationInfo.uid;
                        uidList.put(String.valueOf(uid), info.packageName);
                    }
                }
            }
        }
        Lg.i("uid and packageName = " + uidList);
        return uidList;
    }

    /**
     * 返回所有的有互联网访问权限的应用程序的流量信息。
     *
     * @return
     */
    public static String getAppUidAndPKName(Context context, List<String> byPassList) {
        PackageManager pm = context.getPackageManager();
        String pkInfo = "";
        String resByPassList = "";
        ApplicationInfo info;
        // 获取到配置权限信息的应用程序
        for (String byPass : byPassList) {
            try {
                info = pm.getApplicationInfo(byPass, PackageManager.GET_ACTIVITIES);
                pkInfo = info.uid + "|" + info.packageName + ";";
                Lg.d("pkInfo = " + pkInfo);
            } catch (Exception e) {
                Lg.e("pPackageName err", e);

            }
            resByPassList += pkInfo;
            pkInfo = "";
        }
        Lg.d("resByPassList = " + resByPassList);
        return resByPassList;
    }

    /**
     * 返回手机服务商名字
     */
    public static String getProvidersName(Context context) {
        String ProvidersName = null;
        // 返回唯一的用户ID;就是这张卡的编号神马的
        String IMSI = getIMSI(context);
        if (IMSI == null) {
            return null;
        }

        // IMSI号前面3位460是国家，紧接着后面2位00 02 07是中国移动，01是中国联通，03是中国电信。
        if (IMSI.startsWith("46000") || IMSI.startsWith("46002") || IMSI.startsWith("46007")) {
            ProvidersName = "YD";
        } else if (IMSI.startsWith("46001")) {
            ProvidersName = "LT";
        } else if (IMSI.startsWith("46003")) {
            ProvidersName = "DX";
        } else {
            ProvidersName = "OTHER:" + IMSI;
        }
        return ProvidersName;
    }

    /**
     * 取得当前sim手机卡的imsi
     */
    public static String getIMSI(Context context) {
        if (null == context) {
            return null;
        }
        String imsi = "";
        try {
            TelephonyManager tm = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
            imsi = tm.getSubscriberId();
        } catch (Exception e) {
        }
        return imsi;
    }


    /**
     * wap网络类型判断
     *
     * @param context
     * @return
     */
    public static boolean isWap(Context context) {
        try {
            ConnectivityManager connectivityManager = (ConnectivityManager) context
                    .getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo t = connectivityManager.getActiveNetworkInfo();
            if (t != null) {
                String extra = t.getExtraInfo();
                return extra != null && extra.contains("wap");
            } else {
                return false;
            }

        } catch (Exception var3) {
            return true;
        }
    }

    /**
     * 发送本地广播
     *
     * @param context
     * @param action
     * @param value
     */
    public static void sendLocalBC(Context context, String action, String value) {
        if (null == context || null == action) {
            return;
        }
        LocalBroadcastManager lbm = LocalBroadcastManager.getInstance(context);
        Intent intent = new Intent(action);
        intent.putExtra("status", value);
        lbm.sendBroadcast(intent);
    }

    public static String getAPPName(Context context, String pkName) {
        PackageManager pm = context.getPackageManager();
        String name = null;
        try {
            name = pm.getApplicationLabel(
                    pm.getApplicationInfo(pkName,
                            PackageManager.GET_META_DATA)).toString();
        } catch (PackageManager.NameNotFoundException e) {
            Lg.e("getAPPName err", e);
        }
        if (name == null) {
            name = "当前应用";
        }
        return name;
    }

    public static boolean isWifiEnabled(Context context) {
        WifiManager connManager = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
        return connManager.isWifiEnabled();
    }

    public static String getPkName(Map<String, String> map, String key) {
        if (map != null && map.containsKey(key)) {
            return map.get(key);
        }
        return DEFAULT_KEY;
    }

    public static boolean isVpnUsed() {
        try {
            Enumeration<NetworkInterface> niList = NetworkInterface.getNetworkInterfaces();
            if (niList != null) {
                for (NetworkInterface intf : Collections.list(niList)) {
                    if (!intf.isUp() || intf.getInterfaceAddresses().size() == 0) {
                        continue;
                    }
                    Lg.d("isVpnUsed() NetworkInterface Name: " + intf.getName());
                    if (intf.getName() != null && intf.getName().contains("tun")) {
                        return true; // The VPN is up
                    }
                    if ("tun0".equals(intf.getName()) || "ppp0".equals(intf.getName())) {
                        return true; // The VPN is up
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    /**
     * 获取当前网络运营商；
     *
     * @param context
     * @return
     */
    public static String getNetWorkProviderName(Context context) {
        String providerName = "";
        //                tv_sim1.setText("sim1 = "+getIMSI(MainActivity.this));
        String net = getNetWork(context);
        List<String> infos = getNetWorkList(context);
        if (net == null || net.equals("WIFI")) {
            if (infos.size() > 1) {
                infos.remove("WIFI");
                net = infos.get(0);
                if (net.equals("3gwap") || net.equals("uniwap")
                        || net.equals("3gnet") || net.equals("uninet")) {
                    providerName = "LT";
                } else if (net.equals("cmnet") || net.equals("cmwap")) {
                    providerName = "YD";
                } else if (net.equals("ctnet") || net.equals("ctwap")) {
                    providerName = "DX";
                }
            }
        } else {
            if (net.equals("3gwap") || net.equals("uniwap")
                    || net.equals("3gnet") || net.equals("uninet")) {
                providerName = "LT";
            } else if (net.equals("cmnet") || net.equals("cmwap")) {
                providerName = "YD";
            } else if (net.equals("ctnet") || net.equals("ctwap")) {
                providerName = "DX";
            }
        }
        return providerName;
    }

    /**
     * 获取当前网络名称；
     *
     * @param context
     * @return
     */
    public static List<String> getNetWorkList(Context context) {


        List<String> list = new ArrayList<String>();
        ConnectivityManager manager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        try {
            NetworkInfo[] networkInfos = manager.getAllNetworkInfo();

            if (networkInfos != null) {
                for (int i = 0; i < networkInfos.length; i++) {
                    NetworkInfo info = networkInfos[i];
                    String infoName = null;
                    if (info.getTypeName().equals("WIFI")) {
                        infoName = info.getTypeName();
                    } else {
                        infoName = info.getExtraInfo();
                    }
                    if (infoName != null && list.contains(infoName) == false) {
                        list.add(infoName);
                        // System.out.println(name);
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return list;
    }

    public static String getNetWork(Context context) {
        String NOWNET = null;
        ConnectivityManager cm = (ConnectivityManager) context
                .getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo info = cm.getActiveNetworkInfo();
        if (info != null && info.isAvailable()) {
            if (info.getTypeName().equals("WIFI")) {
                NOWNET = info.getTypeName();
            } else {
                NOWNET = info.getExtraInfo();// cmwap/cmnet/wifi/uniwap/uninet
            }
        }
        return NOWNET;
    }
}