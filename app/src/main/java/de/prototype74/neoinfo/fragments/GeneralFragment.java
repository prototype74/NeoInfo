package de.prototype74.neoinfo.fragments;

import android.content.SharedPreferences;
import android.view.View;

import androidx.preference.PreferenceManager;

import java.util.HashMap;
import java.util.List;

import de.prototype74.neoinfo.R;
import de.prototype74.neoinfo.utils.DeviceProperties;
import de.prototype74.neoinfo.utils.Utils;

public class GeneralFragment extends MainFragment {

    @Override
    protected void initContentsToList(View view, List<HashMap<String, String>> contents) {
        String orange_color = String.valueOf(getResources().getColor(R.color.orange));
        String red_color = String.valueOf(getResources().getColor(R.color.red));
        int status; // 0=Okay, 1=Warning, 2=Critical
        String color = null; // Okay=PLAIN, Warning=ORANGE, Critical=RED
        String unknown = getString(R.string.unknown);
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getContext());
        boolean advance_info = prefs.getBoolean("display_advance_info", false);

        String subText = DeviceProperties.getBootloader();
        if (subText != null) {
            status = Utils.isBootloaderOld();
            if (status == 1)
                color = orange_color;
            else if (status == 2)
                color = red_color;
        } else {
            subText = unknown;
        }
        addNewContent(contents, getString(R.string.bootloader), subText, color);

        if (advance_info) {
            subText = DeviceProperties.getPdaRelease();
            if (subText == null)
                subText = unknown;
            addNewContent(contents, getString(R.string.pda), subText, null);

            subText = DeviceProperties.getCscVersion();
            if (subText == null)
                subText = unknown;
            if (DeviceProperties.getSalesCode() != null)
                subText = String.format("%s (%s)", subText, DeviceProperties.getSalesCode());
            addNewContent(contents, getString(R.string.csc), subText, null);
        }

        subText = DeviceProperties.getBaseband();
        if (subText != null) {
            status = Utils.isBasebandOld();
            if (status == 1)
                color = orange_color;
            else if (status == 2)
                color = red_color;
        } else {
            subText = unknown;
        }
        addNewContent(contents, getString(R.string.baseband), subText, color);

        addNewContent(contents, getString(R.string.device_variant), DeviceProperties.getDeviceVariant(), null);
        addNewContent(contents, getString(R.string.android_ver), DeviceProperties.getAndroidVersion(), null);
        addNewContent(contents, getString(R.string.android_api), String.valueOf(DeviceProperties.getAndroidAPI()), null);
        addNewContent(contents, getString(R.string.build_number), DeviceProperties.getBuildNumber(), null);
    }
}
