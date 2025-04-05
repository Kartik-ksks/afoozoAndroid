package in.kpis.afoozo.activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.databinding.DataBindingUtil;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.telephony.TelephonyManager;
import android.view.View;
import android.webkit.WebResourceRequest;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Toast;

import com.kpis.afoozo.R;
import com.kpis.afoozo.databinding.ActivityCheckInBinding;
import com.kpis.afoozo.databinding.ActivityTermsBinding;

import in.kpis.afoozo.okhttpServcice.AppUrls;
import in.kpis.afoozo.util.Utils;

import static android.Manifest.permission.CALL_PHONE;

public class CheckInActi extends AppCompatActivity {
    private Toolbar  toolbar;

    private Context mContext;

    private ActivityCheckInBinding binding;
    private String forWhich;
    private String phoneNumber = "";
    private int REQUESTPERMISSIONCODE = 203;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_check_in);

        mContext = CheckInActi.this;
        initilaize();
    }
        private void initilaize() {
            toolbar = (androidx.appcompat.widget.Toolbar) findViewById(R.id.toolbar);
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

            binding.toolbar.activityTitle.setText(getString(R.string.check_in));

            binding.webView.getSettings().setJavaScriptEnabled(true);

            binding.webView.setWebViewClient(new WebViewClient(){

                // For api level bellow 24
                @Override
                public boolean shouldOverrideUrlLoading(WebView view, String url){
//                Toast.makeText(mContext, "Old Method",Toast.LENGTH_SHORT).show();

                    if(url.startsWith("http")){
                        Toast.makeText(mContext,"Page link", Toast.LENGTH_SHORT).show();
                        // Return false means, web view will handle the link
                        return false;
                    }else if(url.startsWith("tel:")){
                        // Handle the tel: link
                        checkCallFunctionality(url);

                        // Return true means, leave the current web view and handle the url itself
                        return true;
                    }

                    return false;
                }


                // From api level 24
                @Override
                public boolean shouldOverrideUrlLoading(WebView view, WebResourceRequest request){
                    Toast.makeText(mContext, "New Method",Toast.LENGTH_SHORT).show();

                    // Get the tel: url
                    String url = request.getUrl().toString();

                    if(url.startsWith("http")){
                        Toast.makeText(mContext,"Page link",Toast.LENGTH_SHORT).show();
                        // Return false means, web view will handle the link
                        return false;
                    }else if(url.startsWith("tel:")){
                        // Handle the tel: link
                        checkCallFunctionality(url);

                        // Return true means, leave the current web view and handle the url itself
                        return true;
                    }

                    return false;
                }

                public void onPageFinished(WebView view, String url) {
                    binding.pbTerms.setVisibility(View.GONE);
                }
            });
            binding.webView.loadUrl("https://gc.synxis.com/rez.aspx?template=RBE&shell=RBE3&hotel=&Rooms=1&Chain=18173&_ga=2.1723953.1267231439.1591862970-2108142909.1590645902");

        }

    public void checkCallFunctionality(String url) {
        phoneNumber = url;
        TelephonyManager tm = (TelephonyManager) mContext.getSystemService(Context.TELEPHONY_SERVICE);
        if (tm.getPhoneType() == TelephonyManager.PHONE_TYPE_NONE) {
            //No calling functionality
            Utils.showCenterToast(mContext, "No calling functionality");
        } else {
            //calling functionality
            checkPermission();
        }
    }

    public boolean checkPermission() {
        if (Build.VERSION.SDK_INT >= 23) {
            int result = ContextCompat.checkSelfPermission(mContext, CALL_PHONE);
            if (result != PackageManager.PERMISSION_GRANTED)
                requestPermission();
            else
                callAction();
            return result == PackageManager.PERMISSION_GRANTED;
        } else {
            callAction();
            return true;
        }
    }

    private void requestPermission() {
        ActivityCompat.requestPermissions((Activity) mContext, new String[]{CALL_PHONE}, REQUESTPERMISSIONCODE);
    }

    @SuppressLint("MissingPermission")
    private void callAction() {
        Intent callIntent = new Intent(Intent.ACTION_DIAL);

        callIntent.setData(Uri.parse("tel:" + phoneNumber));
        startActivity(callIntent);
    }
}
