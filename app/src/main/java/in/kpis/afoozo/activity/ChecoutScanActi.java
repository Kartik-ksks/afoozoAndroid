package in.kpis.afoozo.activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.databinding.DataBindingUtil;

import android.content.Context;
import android.os.Bundle;
import android.view.View;

import com.kpis.afoozo.R;
import com.kpis.afoozo.databinding.ActivityCheckInBinding;
import com.kpis.afoozo.databinding.ActivityChecoutScanBinding;

import in.kpis.afoozo.util.Constants;
import in.kpis.afoozo.util.Utils;

public class ChecoutScanActi extends AppCompatActivity {
    private Toolbar toolbar;

    private Context mContext;
    private ActivityChecoutScanBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = DataBindingUtil.setContentView(this, R.layout.activity_checout_scan);

        mContext = ChecoutScanActi.this;
        initialize();
    }

    private void initialize() {
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(false);

        toolbar.setNavigationIcon(R.drawable.ic_back);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        binding.toolbar.activityTitle.setText(getString(R.string.scan_and_chek_in));

    }

    public void scanProcess(View view) {
        Utils.startActivity(mContext, ScanRoomActivity.class);

    }
}