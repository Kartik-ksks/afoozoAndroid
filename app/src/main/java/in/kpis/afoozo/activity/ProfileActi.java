package in.kpis.afoozo.activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.databinding.DataBindingUtil;

import android.app.AlertDialog;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;

import com.kpis.afoozo.R;
import in.kpis.afoozo.bean.UserBean;
import com.kpis.afoozo.databinding.ActivityProfileBinding;
import in.kpis.afoozo.util.SharedPref;
import in.kpis.afoozo.util.Utils;

public class ProfileActi extends AppCompatActivity {

    private Toolbar toolbar;

    private Context mContext;

    private ActivityProfileBinding binding;
    private AlertDialog dialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_profile);

        mContext = ProfileActi.this;
        initialize();
    }

    @Override
    protected void onResume() {
        super.onResume();
        setData();
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

        binding.toolbar.activityTitle.setText(getString(R.string.profile));

    }

    private void setData() {
        UserBean userBean = (UserBean) Utils.getJsonToClassObject(SharedPref.getUserModelJSON(mContext), UserBean.class);
        if (userBean != null) {

            binding.txtName.setText("" + userBean.getFullName());
            binding.txtMobile.setText("+91 " + userBean.getMobile());

            if (!TextUtils.isEmpty(userBean.getUserImage())) {
                Utils.setImage(mContext, binding.imvUser, binding.pbProfile, userBean.getUserImage());
            } else if(userBean.getGender().equalsIgnoreCase("MALE")){
                Drawable res = getResources().getDrawable(R.drawable.profile_thumb);
                binding.imvUser.setImageDrawable(res);
            }else if(userBean.getGender().equalsIgnoreCase("FEMALE")) {
                Drawable res = getResources().getDrawable(R.drawable.girl);
                binding.imvUser.setImageDrawable(res);
            }
        }
    }

    public void goToUpdateScreen(View view){
        Utils.startActivity(mContext, UpdateProfileActi.class);
    }

    public void goToSettingScreen(View view){
        Utils.startActivity(mContext, NotificationSettingActi.class);
    }

    public void goToNotificationScreen(View view){
        Utils.startActivity(mContext, NotificationActi.class);
    }

    public void logoutProcess(View view){
        dialog = Utils.retryAlertDialog(this, getResources().getString(R.string.app_name), getResources().getString(R.string.Are_you_sure_to_logout), getResources().getString(R.string.Cancel), getResources().getString(R.string.Yes), new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Utils.logout(mContext);
                dialog.dismiss();
            }
        });
    }
}
