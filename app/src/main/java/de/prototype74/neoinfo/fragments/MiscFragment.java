package de.prototype74.neoinfo.fragments;

import android.view.View;
import java.util.HashMap;
import java.util.List;

import de.prototype74.neoinfo.R;
import de.prototype74.neoinfo.utils.Utils;

public class MiscFragment extends MainFragment {
    @Override
    protected void initContentsToList(View view, List<HashMap<String, String>> contents) {
        String subText = getString(R.string.unknown);

        if (Utils.isStockCharger())
            subText = getString(R.string.stock_charger);
        else if (Utils.isNewCharger())
            subText = getString(R.string.new_charger);
        addNewContent(contents, getString(R.string.charger_driver), subText, null);
    }
}
