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
        String subText;
        String green_color = String.valueOf(getResources().getColor(R.color.green));
        String orange_color = String.valueOf(getResources().getColor(R.color.orange));
        String red_color = String.valueOf(getResources().getColor(R.color.red));
        int status; // 0=Okay, 1=Warning, 2=Critical
        String color; // Okay=GREEN/PLAIN, Warning=ORANGE, Critical=RED
        String unknown = getString(R.string.unknown);
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getContext());
        boolean advance_info = prefs.getBoolean("display_advance_info", false);

        subText = Utils.getBackCameraTypeByFirmware();
        if (subText == null)
            subText = unknown;
        addNewContent(contents, getString(R.string.fw_back_type), subText, null);

        subText = Utils.getCameraTypeByKernel(false);
        if (subText == null)
            subText = unknown;
        addNewContent(contents, getString(R.string.kernel_back_type), subText, null);

        subText = Utils.getBackCameraTypeByVendor();
        if (subText == null)
            subText = unknown;
        addNewContent(contents, getString(R.string.vendor_back_type), subText, null);

        status = Utils.getBackCameraTypeResult();
        color = orange_color;
        if (status == 0) {
            subText = getString(R.string.cam_result_okay);
            color = green_color;
        } else if (status == 1) {
            subText = getString(R.string.cam_result_fail_kernel_match);
        } else if (status == 2) {
            subText = getString(R.string.cam_result_fail_fw_match);
        } else {
            subText = getString(R.string.cam_result_fail);
        }
        addNewContent(contents, getString(R.string.rear_camera_note), subText, color);

        status = CameraUtils.getBackCameraStatus(view.getContext());
        if (status == 0) {
            subText = getString(R.string.cam_status_okay);
            color = green_color;
        } else if (status == 1) {
            subText = getString(R.string.cam_status_warn);
            color = orange_color;
        } else {
            subText = getString(R.string.cam_status_fail);
            color = red_color;
        }
        addNewContent(contents, getString(R.string.rear_camera_status), subText, color);

        if (advance_info) {
            subText = Utils.getCameraTypeByKernel(true);
            if (subText == null)
                subText = unknown;
            addNewContent(contents, getString(R.string.kernel_front_type), subText, null);
        }
    }
}
