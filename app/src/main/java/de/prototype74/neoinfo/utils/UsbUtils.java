package de.prototype74.neoinfo.utils;

import java.io.BufferedReader;
import java.io.InputStreamReader;

public class UsbUtils {
    private static final String HOST_NOTIFY = "/sys/class/host_notify";
    private static final String USB_NOTIFY = "/sys/class/usb_notify";
    private static String otg_regulator_path;
    private static boolean checkOtgPathDone = false;

    /**
     * Check if the kernel includes the sys path to host_notify or usb_notify (samsung).
     * This indicates that the kernel supports USB Host.
     *
     * @return value can either be true or false
     */
    public static boolean isUsbNotifySupported() {
        return Utils.checkPathExist(USB_NOTIFY) || Utils.checkPathExist(HOST_NOTIFY);
    }

    /**
     * Find and set the correct OTG Regulator path depending to the currently
     * used charger driver.
     */
    private static void setOtgRegulatorPath() {
        String[] otg_reg_paths = {
                "/sys/class/regulator/regulator.48", // qpnp-charger
                "/sys/class/regulator/regulator.49", // qpnp-sec-charger
        };
        for (String path : otg_reg_paths) {
            try {
                Process bbp = Runtime.getRuntime().exec("cat " + path + "/name");
                BufferedReader in = new BufferedReader(new InputStreamReader(bbp.getInputStream()));
                String output = in.readLine();
                in.close();
                bbp.destroy();
                if (output.equals("8226_smbbp_otg")) {
                    otg_regulator_path = path;
                    break;
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * Read the state from 8226_smbbp_otg regulator. Access may be blocked by SELinux.
     *
     * @return -1=failed/not found, 0=disabled, 1=enabled
     */
    public static int getOtgRegulatorState() {
        int result = -1;
        if (!checkOtgPathDone) {
            setOtgRegulatorPath();
            checkOtgPathDone = true;
        }
        if (otg_regulator_path != null) {
            try {
                Process bbp = Runtime.getRuntime().exec("cat " + otg_regulator_path + "/state");
                BufferedReader in = new BufferedReader(new InputStreamReader(bbp.getInputStream()));
                String output = in.readLine();
                if (output.equalsIgnoreCase("enabled"))
                    result = 1;
                else if (output.equalsIgnoreCase("disabled"))
                    result = 0;
                in.close();
                bbp.destroy();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return result;
    }
}
