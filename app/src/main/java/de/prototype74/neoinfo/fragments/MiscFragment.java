package de.prototype74.neoinfo.fragments;

import android.content.SharedPreferences;
import android.view.View;

import androidx.preference.PreferenceManager;

import java.util.HashMap;
import java.util.List;

import de.prototype74.neoinfo.R;
import de.prototype74.neoinfo.utils.UsbUtils;
import de.prototype74.neoinfo.utils.Utils;

public class MiscFragment extends MainFragment {
    @Override
    protected void initContentsToList(View view, List<HashMap<String, String>> contents) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getContext());
        boolean advance_info = prefs.getBoolean("display_advance_info", false);

        String subText =
                (Utils.isStockCharger() ?
                        getString(R.string.stock_charger) :
                        Utils.isNewCharger() ?
                                getString(R.string.new_charger) :
                                getString(R.string.unknown));
        addNewContent(contents, getString(R.string.charger_driver), subText, null);

        subText =
                (UsbUtils.isUsbNotifySupported() ?
                        getString(R.string.supported) :
                        getString(R.string.not_supported));
        addNewContent(contents, getString(R.string.usb_host_notify), subText, null);

        if (advance_info) {
            int state = UsbUtils.getOtgRegulatorState();
            String color = null;
            subText = getString(R.string.unknown);
            if (state == 1) {
                subText = getString(R.string.active);
                color = String.valueOf(getResources().getColor(R.color.green));
            } else if (state == 0)
                subText = getString(R.string.disabled);
            addNewContent(contents, getString(R.string.usb_otg_power), subText, color);
        }
    }
}
