package de.prototype74.neoinfo;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class AboutActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.about_activity);

        Toolbar toolbar = findViewById(R.id.toolbar_about);
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

        String app_version = "1.0";
        try {
            PackageInfo pi = this.getPackageManager().getPackageInfo(this.getPackageName(), 0);
            app_version = pi.versionName;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }

        TextView text_app_version = findViewById(R.id.txtAppversion_about);
        String textContent = getString(R.string.app_version);
        text_app_version.setText(String.format(textContent,  app_version));

        TextView author = findViewById(R.id.txtByName);
        textContent = getString(R.string.by_name);
        author.setText(String.format(textContent, "prototype74"));

        Button buttonOpenSource = findViewById(R.id.buttonOpenSource);
        buttonOpenSource.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent githubIntent =  new Intent(Intent.ACTION_VIEW);
                githubIntent.setData(Uri.parse("https://github.com/prototype74/NeoInfo"));
                startActivity(githubIntent);
            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
