package de.prototype74.neoinfo.fragments;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.view.View;

import androidx.preference.PreferenceManager;

import java.util.HashMap;
import java.util.List;

import de.prototype74.neoinfo.R;
import de.prototype74.neoinfo.utils.UsbUtils;
import de.prototype74.neoinfo.utils.Utils;

public class MiscFragment extends MainFragment {
    private int lastOTGState;
    private boolean isOTGStateReadable = false;

    @Override
    protected void initContentsToList(View view, List<HashMap<String, String>> contents) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(view.getContext());
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

        if (advance_info) { /* Should always be the last entry in the list */
            int state = UsbUtils.getOtgRegulatorState();
            subText = getString(R.string.unknown);
            String color = null;
            if (state == 1) {
                subText = getString(R.string.active);
                color = String.valueOf(getResources().getColor(R.color.green));
            } else if (state == 0)
                subText = getString(R.string.disabled);
            addNewContent(contents, getString(R.string.usb_otg_power), subText, color);
            if (state >= 0) {
                isOTGStateReadable = true;
                lastOTGState = state;
            }
        }
    }

    private void updateOTGRegulatorState() {
        int state = UsbUtils.getOtgRegulatorState();
        if (lastOTGState == state)
            return;
        String subText = getString(R.string.disabled);
        String color = null;
        if (state == 1) {
            subText = getString(R.string.active);
            color = String.valueOf(getResources().getColor(R.color.green));
        }
        updateContent(contents, contents.size() - 1, getString(R.string.usb_otg_power), subText, color);
        adapter.notifyDataSetChanged();
        lastOTGState = state;
    }

    private final BroadcastReceiver receiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            updateOTGRegulatorState();
        }
    };

    @Override
    public void onResume() {
        super.onResume();
        IntentFilter filter = new IntentFilter();
        filter.addAction(Intent.ACTION_BATTERY_CHANGED);
        View view = getView();
        if (view != null && isOTGStateReadable)
            view.getContext().registerReceiver(receiver, filter);
    }

    @Override
    public void onPause() {
        super.onPause();
        View view = getView();
        if (view != null && isOTGStateReadable)
            view.getContext().unregisterReceiver(receiver);
    }
}
