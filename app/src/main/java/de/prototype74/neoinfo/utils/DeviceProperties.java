package de.prototype74.neoinfo.utils;

import android.os.Build;

import java.io.BufferedReader;
import java.io.InputStreamReader;

public class DeviceProperties {
    /**
     * Get the bootloader version from android build
     *
     * @return the bootloader version else null if it's unknown
     */
    public static String getBootloader() {
        if (!Build.BOOTLOADER.equals(Build.UNKNOWN)) {
            return Build.BOOTLOADER;
        }
        return null;
    }

    /**
     * Get the baseband/modem version from android build
     *
     * @return the baseband/modem version else null if it's unknown
     */
    public static String getBaseband() {
        if (Build.getRadioVersion() != null)
            return Build.getRadioVersion();
        return null;
    }

    /**
     * Get the android version from android build
     *
     * @return the android version
     */
    public static String getAndroidVersion() {
        return Build.VERSION.RELEASE;
    }

    /**
     * Get the android sdk (api) from android build
     *
     * @return the android sdk
     */
    public static int getAndroidAPI() {
        return Build.VERSION.SDK_INT;
    }

    /**
     * Get the build number from android build
     *
     * @return the build number
     */
    public static String getBuildNumber() {
        return Build.DISPLAY;
    }

    /**
     * Get the device variant from android build. If a Samsung Galaxy S3 Neo device is detected,
     * the method will report its device variant instead.
     *
     * @return the device variant
     */
    public static String getDeviceVariant() {
        String bl = getBootloader();
        if (bl != null) {
            if (bl.startsWith("I9300I"))
                return "GT-I9300I";
            else if (bl.startsWith("I9301I"))
                return "GT-I9301I";
            else if (bl.startsWith("I9301Q"))
                return "GT-I9301Q";
        }
        return Build.MODEL;
    }

    /**
     * Read a specific device prop (getprop) value from given key
     *
     * @param key the prop key to check for
     * @return the value from key else null if key doesn't exist
     */
    private static String getSystemProp(String key) {
        String result = null;
        try {
            Process bbp = Runtime.getRuntime().exec("getprop " + key);
            BufferedReader in = new BufferedReader(new InputStreamReader(bbp.getInputStream()));
            String temp_result = in.readLine();
            if (!temp_result.isEmpty())
                result = temp_result;
            in.close();
            bbp.destroy();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }

    /**
     * Read the AP/PDA release from device props. Also usable to check if the device is running on
     * a Samsung based ROM.
     *
     * @return the AP/PDA release else null
     */
    public static String getPdaRelease() {
        return getSystemProp("ro.build.PDA");
    }

    /**
     * Read the CSC (Country Specific Code) release from device props
     *
     * @return the CSC release else null
     */
    public static String getCscVersion() {
        return getSystemProp("ril.official_cscver");
    }

    /**
     * Read the CSC sales code from device props
     *
     * @return the sales code else null
     */
    public static String getSalesCode() {
        return getSystemProp("ro.csc.sales_code");
    }
}
