package de.prototype74.neoinfo.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.os.Build;

import androidx.appcompat.app.AppCompatDelegate;
import androidx.preference.PreferenceManager;

import java.io.BufferedReader;
import java.io.File;
import java.io.FilenameFilter;
import java.io.InputStreamReader;

public class Utils {
    private static final String SAMSUNG_CAMERA = "SLSI_S5K4H5YB";
    private static final String SONY_CAMERA = "SONY_IMX175";
    private static final String SAMSUNG_AND_SONY_CAMERA = "SLSI_S5K4H5YB && SONY_IMX175";
    private static final String KERNEL_FRONT_CAMTYPE = "/sys/class/camera/front/front_camtype";
    private static final String KERNEL_REAR_CAMTYPE = "/sys/class/camera/rear/rear_camtype";
    private static final String STOCK_CHARGER = "/sys/module/qpnp_sec_charger";
    private static final String NEW_CHARGER = "/sys/module/qpnp_charger";
    private static final String SEC_BATTERY = "/sys/bus/platform/drivers/sec-battery";
    private static String lib_path;
    private static String vendor_path;

    /**
     * Set the vendor paths depending on current API Level
     */
    public static void setVendorPaths() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            lib_path = "/vendor/lib/";
            vendor_path = lib_path;
        } else { // (Build.VERSION.SDK_INT < Build.VERSION_CODES.P)
            lib_path = "/system/lib/";
            vendor_path = "/system/vendor/lib/";
        }
    }

    /**
     * Check if the current bootloader version is outdated
     *
     * @return 0=up-tp-date, 1=warn, 2=outdated
     */
    public static int isBootloaderOld() {
        String bl = DeviceProperties.getBootloader();
        if (bl != null) {
            String releaseYear = "";
            try {
                releaseYear = bl.substring(bl.length()-3);
            } catch (StringIndexOutOfBoundsException e) {
                e.printStackTrace();
            }
            if (releaseYear.startsWith("O")) // 2015
                return 1;
            else if (releaseYear.startsWith("N")) // 2014
                return 2;
        }
        return 0;
    }

    /**
     * Check if the current baseband/modem version is outdated
     *
     * @return 0=up-tp-date, 1=warn, 2=outdated
     */
    public static int isBasebandOld() {
        String bb = DeviceProperties.getBaseband();
        if (bb != null) {
            String releaseYear = "";
            try {
                releaseYear = bb.substring(bb.length()-3);
            } catch (StringIndexOutOfBoundsException e) {
                e.printStackTrace();
            }
            if (releaseYear.startsWith("O")) // 2015
                return 1;
            else if (releaseYear.startsWith("N")) // 2014
                return 2;
        }
        return 0;
    }

    /**
     * Check if given path exists
     *
     * @return true=path exists, false=path doesn't exist
     */
    protected static boolean checkPathExist(String path) {
        File file = new File(path);
        return file.exists();
    }

    /**
     * Figure out which camera sensor was shipped to the target firmware release by comparing
     * its bootloader version.
     * <p>
     * If the bootloader version starts with:<p>
     * I9301IXC, I9301IXD it's SAMSUNG SENSOR
     * <p>or<p>
     * I9301IXE, I9301IXX, I9301IXW, I9300I, I9301Q it's SONY SENSOR
     *
     * @return String value can either be SAMSUNG_CAMERA, SONY_CAMERA or null
     */
    public static String getBackCameraTypeByFirmware() {
        String bl = DeviceProperties.getBootloader();
        if (bl != null) {
            if (bl.startsWith("I9301IXC")|| bl.startsWith("I9301IXD")) {
                return SAMSUNG_CAMERA;
            } else if (bl.startsWith("I9301IXE") || bl.startsWith("I9301IXX")
                    || bl.startsWith("I9301IXW") || bl.startsWith("I9300I")
                    || bl.startsWith("I9301Q")) {
                return SONY_CAMERA;
            }
        }
        return null;
    }

    /**
     * Read the camera type reported from the target camera back/front kernel sys path.
     *
     * @param front if the method should check for the front type instead
     * @return the camera type reported from kernel or null
     */
    public static String getCameraTypeByKernel(boolean front) {
        String result = null;
        String type = KERNEL_REAR_CAMTYPE;
        if (front)
            type = KERNEL_FRONT_CAMTYPE;
        if (checkPathExist(type)) {
            try {
                Process bbp = Runtime.getRuntime().exec("cat " + type);
                BufferedReader in = new BufferedReader(new InputStreamReader(bbp.getInputStream()));
                result = in.readLine();
                in.close();
                bbp.destroy();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return result;
    }

    /**
     * Check if the target (camera) vendor blobs do exist.
     *
     * @param prefix the first given characters of the blob names (e.g. E08QL_)
     * @return true if one or more blobs were found else false
     */
    private static boolean vendorBlobsExist(String prefix) {
        File lib_dir = new File(lib_path);
        File[] lib_files = lib_dir.listFiles(new FilenameFilter() {
            @Override
            public boolean accept(File dir, String name) {
                return name.startsWith(prefix) && name.endsWith(".so");
            }
        });
        File vendor_dir = new File(vendor_path);
        File[] vendor_files = vendor_dir.listFiles(new FilenameFilter() {
            @Override
            public boolean accept(File dir, String name) {
                return name.startsWith(prefix) && name.endsWith(".so");
            }
        });
        if (lib_files != null && vendor_files != null) {
            return lib_files.length > 0 && vendor_files.length > 0;
        }
        return false;
    }

    /**
     * Check if the Samsung Galaxy S3 Neo specific camera blobs do exist
     *
     * @return String value can either be SAMSUNG_AND_SONY_CAMERA, SAMSUNG_CAMERA, SONY_CAMERA or
     * null
     */
    public static String getBackCameraTypeByVendor() {
        boolean doSamsungBlobExist = vendorBlobsExist("E08QL_");
        boolean doSonyBlobExist = vendorBlobsExist("B08QT_");
        if (doSamsungBlobExist && doSonyBlobExist)
            return SAMSUNG_AND_SONY_CAMERA;
        else if (doSamsungBlobExist)
            return SAMSUNG_CAMERA;
        else if (doSonyBlobExist)
            return SONY_CAMERA;
        return null;
    }

    /**
     * This method compares the result from all 3 reported camera types methods
     * (getBackCameraTypeByFirmware(), getCameraTypeByKernel(), getBackCameraTypeByVendor())
     *
     * @return -1=unable to check, 0=okay, 1=kernel type doesn't match, 2=fw and vendor types don't
     * match
     */
    public static int getBackCameraTypeResult() {
        String fw_type = getBackCameraTypeByFirmware();
        String k_type = getCameraTypeByKernel(false);
        String v_type = getBackCameraTypeByVendor();

        if (fw_type == null || v_type == null || v_type.equals(SAMSUNG_AND_SONY_CAMERA))
            return -1;

        if (k_type == null) {
            if (fw_type.equals(v_type))
                return 0;
        } else {
            if (fw_type.equals(v_type) && v_type.equals(k_type)) {
                return 0;
            } else if (fw_type.equals(v_type)) {
                return 1;
            }
        }
        return 2;
    }

    /**
     * Check if the kernel uses the stock qpnp-charger driver
     *
     * @return value can either be true or false
     */
    public static boolean isStockCharger() {
        return checkPathExist(STOCK_CHARGER);
    }

    /**
     * Check if the kernel uses the new qpnp-charger and sec-battery drivers
     *
     * @return value can either be true or false
     */
    public static boolean isNewCharger() {
        return checkPathExist(NEW_CHARGER) && checkPathExist(SEC_BATTERY);
    }

    /**
     * Set the App theme depending to the current theme_settings preference
     *
     * @param context Context from an app activity
     */
    public static void setAppTheme(Context context) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        String listPref = prefs.getString("theme_settings", "unknown");
        switch (listPref) {
            case "default":
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM);
                break;
            case "light":
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
                break;
            case "dark":
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
                break;
        }
    }

    /**
     * Check if the current App theme is night mode
     *
     * @param context Context from an app activity
     */
    public static boolean isNightMode(Context context) {
        int night_mode = (context.getResources().getConfiguration().uiMode & Configuration.UI_MODE_NIGHT_MASK);
        return night_mode == Configuration.UI_MODE_NIGHT_YES;
    }
}
