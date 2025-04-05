package in.kpis.afoozo.activity;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.databinding.DataBindingUtil;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.kpis.afoozo.R;

import in.kpis.afoozo.bean.ScanQrBean;

import com.kpis.afoozo.databinding.ActivityDineInBinding;

import in.kpis.afoozo.util.Constants;
import in.kpis.afoozo.util.SharedPref;
import in.kpis.afoozo.util.Utils;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

public class DineInActi extends AppCompatActivity {

    private Toolbar toolbar;

    private Context mContext;

    private ActivityDineInBinding binding;
    private int SCAN_REQUEST = 201;
    private ScanQrBean scanBean;
    private String fromWhich;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_dine_in);

        fromWhich = getIntent().getStringExtra(Constants.FROM_WHICH);
        mContext = DineInActi.this;
        initialize();
    }

    private void initialize() {
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(false);

        toolbar.setNavigationIcon(R.drawable.ic_back);
        toolbar.setNavigationOnClickListener(v -> finish());

        if(fromWhich.equals(getString(R.string.dine_in))){
        scanBean = (ScanQrBean) Utils.getJsonToClassObject(SharedPref.getScanBeanJson(mContext), ScanQrBean.class);
        if (scanBean != null) {
            DateFormat dateFormat = new SimpleDateFormat("dd");
            Date date = new Date();
            if (scanBean.getDate() == Integer.parseInt(dateFormat.format((date))))
                setScanCodeData();
            else {
                SharedPref.setScanBeanJson(mContext, "");
            }
        }
        }else if(fromWhich.equals(getString(R.string.cafe))) {
            binding.txtMsg.setText(getText(R.string.where_we_can_find_it)
                    + "\n " + getText(R.string.scan_at_cafe_help_disk)
                    + "\n " + getText(R.string.walk_to_the_cafeteria)
                    + "\n " + getText(R.string.scan_from_cafe_table_tOP)
                    + "\n " + getText(R.string.walk_to_the_counter_of_your_cafeteria_QR_code_is_available_on_Table_Top));

            scanBean = (ScanQrBean) Utils.getJsonToClassObject(SharedPref.getScanCafeBeanJson(mContext), ScanQrBean.class);
            if (scanBean != null) {
                DateFormat dateFormat = new SimpleDateFormat("dd");
                Date date = new Date();
            /*    if (scanBean.getDate() == Integer.parseInt(dateFormat.format((date))))*/
                    setScanCodeData();
               /* else {
                    SharedPref.setScanCafeBeanJson(mContext, "");
                }*/
            }

        }else {
            if(fromWhich.equals(getString(R.string.cafe))) {
                binding.txtMsg.setText(getText(R.string.where_we_can_find_it)
                        + "\n " + getText(R.string.scan_at_cafe_help_disk)
                        + "\n " + getText(R.string.walk_to_the_cafeteria)
                        + "\n " + getText(R.string.scan_from_cafe_table_tOP)
                        + "\n " + getText(R.string.walk_to_the_counter_of_your_cafeteria_QR_code_is_available_on_Table_Top));
            }

            }

        binding.toolbar.activityTitle.setText(getString(R.string.scan_and_order));
}

    public void scanProcess(View view) {
        Intent intent = new Intent(mContext, ScanQrActi.class);
        intent.putExtra(Constants.FROM_WHICH, fromWhich);
        startActivityForResult(intent, SCAN_REQUEST);
    }

    public void continueProcess(View view) {
        goToAddItemsScreen();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == SCAN_REQUEST && resultCode == RESULT_OK) {
            scanBean = (ScanQrBean) data.getSerializableExtra(Constants.SCAN_QR_BEAN);
            setScanCodeData();
            goToAddItemsScreen();
        }
    }

    private void setScanCodeData() {
        binding.btnScanAndContinue.setVisibility(View.GONE);
        binding.llContinue.setVisibility(View.VISIBLE);

        String message = getString(R.string.you_are_seated_at) + " " + scanBean.getRestaurantName() + ", \n" + scanBean.getRestaurantAddress();
        binding.txtMsg.setText(message);
        if (fromWhich.equals(getString(R.string.cafe))) {
            binding.btnScanAndContinue.setVisibility(View.GONE);
            binding.llContinue.setVisibility(View.VISIBLE);
            binding.txtTableNo.setVisibility(View.GONE);
            binding.txtMsg.setText(getText(R.string.where_we_can_find_it)
                    + "\n " + getText(R.string.scan_at_cafe_help_disk)
                    + "\n " + getText(R.string.walk_to_the_cafeteria)
                    + "\n " + getText(R.string.scan_from_cafe_table_tOP)
                    + "\n " + getText(R.string.walk_to_the_counter_of_your_cafeteria_QR_code_is_available_on_Table_Top));
        } else {
            binding.txtTableNo.setText("" + scanBean.getTableNumber());
            binding.txtTableNo.setVisibility(View.VISIBLE);
        }
    }

    private void goToAddItemsScreen() {
//        Intent intent = new Intent(mContext, AddItemsNewActi.class);
        Intent intent = new Intent(mContext, AddItemsActi.class);
        intent.putExtra(Constants.SCAN_QR_BEAN, scanBean);
        intent.putExtra(Constants.FROM_WHICH, fromWhich);
        startActivity(intent);
    }
}
