package de.prototype74.neoinfo.fragments;

import android.content.SharedPreferences;
import android.os.FileObserver;
import android.view.View;

import androidx.annotation.Nullable;
import androidx.preference.PreferenceManager;

import java.util.HashMap;
import java.util.List;

import de.prototype74.neoinfo.R;
import de.prototype74.neoinfo.utils.UsbUtils;
import de.prototype74.neoinfo.utils.Utils;

public class MiscFragment extends MainFragment {
    @Override
    protected void initContentsToList(View view, List<HashMap<String, String>> contents) {
        String subText = getString(R.string.unknown);
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getContext());
        boolean advance_info = prefs.getBoolean("display_advance_info", false);

        if (Utils.isStockCharger())
            subText = getString(R.string.stock_charger);
        else if (Utils.isNewCharger())
            subText = getString(R.string.new_charger);
        addNewContent(contents, getString(R.string.charger_driver), subText, null);

        if (UsbUtils.isUsbNotifySupported())
            subText = getString(R.string.supported);
        else
            subText = getString(R.string.not_supported);
        addNewContent(contents, getString(R.string.usb_host_notify), subText, null);

        if (advance_info) {
            int state = UsbUtils.getOtgRegulatorState();
            String color = null;
            if (state == 1) {
                subText = getString(R.string.active);
                color = String.valueOf(getResources().getColor(R.color.green));
            }
            else if (state == 0)
                subText = getString(R.string.disabled);
            else
                subText = getString(R.string.unknown);
            addNewContent(contents, getString(R.string.usb_otg_power), subText, color);
        }
    }
}
