package de.prototype74.neoinfo.fragments;

import android.content.SharedPreferences;
import android.view.View;

import androidx.preference.PreferenceManager;

import java.util.HashMap;
import java.util.List;

import de.prototype74.neoinfo.R;
import de.prototype74.neoinfo.utils.CameraUtils;
import de.prototype74.neoinfo.utils.Utils;

public class CameraFragment extends MainFragment {

    @Override
    protected void initContentsToList(View view, List<HashMap<String, String>> contents) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getContext());
        boolean advance_info = prefs.getBoolean("display_advance_info", false);
        String cameraSensorInfo = "";
        String unknown = getString(R.string.unknown);
        String text = Utils.getBackCameraTypeByFirmware();

        cameraSensorInfo += String.format(
                "\n%s: %s\n", getString(R.string.fw_rear_camera),
                text != null ? text : unknown);

        text = Utils.getCameraTypeByKernel(false);
        cameraSensorInfo += String.format(
                "%s: %s\n", getString(R.string.kernel_rear_camera),
                text != null ? text : unknown);

        text = Utils.getBackCameraTypeByVendor();
        cameraSensorInfo += String.format(
                "%s: %s\n",getString(R.string.vendor_rear_camera),
                text != null ? text : unknown);

        if (advance_info) {
            text = Utils.getCameraTypeByKernel(true);
            cameraSensorInfo += String.format(
                    "%s: %s\n", getString(R.string.kernel_front_camera),
                    text != null ? text : unknown);
        }

        int result = Utils.getBackCameraTypeResult(view.getContext());
        text = unknown;
        if (result == 0)
            text = getString(R.string.rear_samsung_sensor);
        else if (result == 1)
            text = getString(R.string.rear_sony_sensor);
        else if (result == 2)
            text = getString(R.string.rear_mixed_sensors);
        addNewContent(contents, text, cameraSensorInfo, null);

        result = CameraUtils.getBackCameraStatus(view.getContext());
        text = getString(R.string.cam_status_fail);
        // Okay=GREEN/PLAIN, Warning=ORANGE, Critical=RED
        String color = String.valueOf(getResources().getColor(R.color.red));
        if (result == 0) {
            text = getString(R.string.cam_status_okay);
            color = String.valueOf(getResources().getColor(R.color.green));
        } else if (result == 1) {
            text = getString(R.string.cam_status_warn);
            color = String.valueOf(getResources().getColor(R.color.orange));
        }
        addNewContent(contents, getString(R.string.rear_camera_status), text, color);
    }
}
