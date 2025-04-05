package in.kpis.afoozo.activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;

import com.kpis.afoozo.R;
import com.kpis.afoozo.databinding.ActivityNotificationPopUpBinding;
import in.kpis.afoozo.util.Constants;
import in.kpis.afoozo.util.SharedPref;
import in.kpis.afoozo.util.Utils;

public class NotificationPopUpActi extends AppCompatActivity {

    private Context mContext;

    private ActivityNotificationPopUpBinding binding;
    private String message;
    private String imageUrl;
    private String type;
    private Intent intent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_notification_pop_up);

        getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);

        if (getIntent().getExtras() != null){
            message = getIntent().getStringExtra(Constants.MESSAGE);
            imageUrl = getIntent().getStringExtra(Constants.IMAGE_URL);
            type = getIntent().getStringExtra(Constants.ORDER_TYPE);
        }

        mContext = NotificationPopUpActi.this;
        initialize();
    }

    private void initialize(){
        if (!TextUtils.isEmpty(imageUrl)){
            binding.imvNotification.setVisibility(View.VISIBLE);
            Utils.setImage(mContext, binding.imvNotification, imageUrl);
        } else
            binding.imvNotification.setVisibility(View.VISIBLE);

        if (!TextUtils.isEmpty(message))
            binding.txtPopUpMsg.setText(message);

        if (type.equals("Information")){
            binding.btnView.setText(getString(R.string.Ok));
        }
    }

    public void viewProcess(View view){

        if (type.equals("Information")) {
            finish();
        } else {
            if (type.equalsIgnoreCase(Constants.TAKE_AWAY)) {
                intent = new Intent(mContext, RestaurantActi.class);
                intent.putExtra(Constants.FROM_WHICH, mContext.getString(R.string.take_away));
            } else if (type.equalsIgnoreCase(Constants.HOME_DELIVERY)) {
                intent = new Intent(mContext, AddressActi.class);
                intent.putExtra(Constants.FROM_WHICH, mContext.getString(R.string.delivery));
                intent.putExtra(Constants.IS_FROM_NOTIFICATION, true);
                intent.putExtra(Constants.IS_FROM_CART, true);
            } else if (type.equalsIgnoreCase(Constants.DINE_IN)) {
                intent = new Intent(mContext, DineInActi.class);
                intent.putExtra(Constants.FROM_WHICH, mContext.getString(R.string.dine_in));
            } else if (type.equals("Event") || type.equals("EventDetail")) {
                intent = new Intent(mContext, EventsActi.class);
                intent.putExtra(Constants.FROM_WHICH, mContext.getString(R.string.events));
            }
            SharedPref.setNotificationJSON(mContext, "");
            startActivity(intent);
            finish();
        }
    }

    public void cancelProcess(View view){
        SharedPref.setNotificationJSON(mContext, "");
        finish();
    }
}
