package in.kpis.afoozo.activity;

import static android.Manifest.permission.CALL_PHONE;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.MailTo;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import android.view.View;
import android.webkit.WebResourceRequest;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.databinding.DataBindingUtil;

import com.kpis.afoozo.R;
import com.kpis.afoozo.databinding.ActivityTermsBinding;

import in.kpis.afoozo.okhttpServcice.AppUrls;
import in.kpis.afoozo.util.Constants;
import in.kpis.afoozo.util.Utils;


public class TermsActi extends AppCompatActivity {

    private Toolbar toolbar;

    private Context mContext;

    private ActivityTermsBinding binding;

    private String forWhich;
    private String phoneNumber = "";
    private int REQUESTPERMISSIONCODE = 203;
    private String paymentUrl;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_terms);

        if (!TextUtils.isEmpty(getIntent().getStringExtra("ForWhich"))){
            forWhich = getIntent().getStringExtra("ForWhich" );
            paymentUrl = getIntent().getStringExtra(Constants.PAYMENT_URL);
        }

        mContext = TermsActi.this;
        initilaize();


    }

    private void initilaize() {
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

        binding.toolbar.activityTitle.setText(getString(R.string.pay_now));

        binding.webView.getSettings().setJavaScriptEnabled(true);

//        binding.webView.setWebViewClient(new WebViewClient() {
//            public void onPageFinished(WebView view, String url) {
//                binding.pbTerms.setVisibility(View.GONE);
//            }
//        });

        // Set the web view client
        binding.webView.setWebViewClient(new WebViewClient(){

            // For api level bellow 24
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url){
//                Toast.makeText(mContext, "Old Method",Toast.LENGTH_SHORT).show();

                if(url.startsWith("http")){
//                    Toast.makeText(mContext,"Page link", Toast.LENGTH_SHORT).show();
                    // Return false means, web view will handle the link
                    return false;
                }else if(url.startsWith("tel:")){
                    // Handle the tel: link
                    checkCallFunctionality(url);

                    // Return true means, leave the current web view and handle the url itself
                    return true;
                }else if (url.startsWith("mailto:")) {
                        MailTo mt = MailTo.parse(url);
                        Intent i = newEmailIntent(mContext, mt.getTo(), "", "", "");
                        startActivity(i);
                        view.reload();
                        return true;

                }

                return false;
            }


            private Intent newEmailIntent(Context context, String address, String subject, String body, String cc) {
                Intent intent = new Intent(Intent.ACTION_SEND);
                intent.putExtra(Intent.EXTRA_EMAIL, new String[] { address });
                intent.putExtra(Intent.EXTRA_TEXT, body);
                intent.putExtra(Intent.EXTRA_SUBJECT, subject);
                intent.putExtra(Intent.EXTRA_CC, cc);
                intent.setType("message/rfc822");
                return intent;
            }

            // From api level 24
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, WebResourceRequest request){
//                Toast.makeText(mContext, "New Method",Toast.LENGTH_SHORT).show();
                // Get the tel: url
                String url = request.getUrl().toString();
                if(url.startsWith("http")){
                    if (url.contains("https://afoozocafe.com/v1/api/sodexo/") && (url.contains("sodexoConfirmation") || url.contains("sodexoCancelTransaction"))){
                        goToPreviousPage();

                    }
/*                    if (url.contains("https://bifrost.mum1-pp.zetaapps.in/z-sodexo/") || url.contains("CANCELLED_BY_USER_AGENT")){
                        //http://www.appmovil.eqtaxi911.com/v1/api/payPhone/getTransactionIds?id=9956990&clientTransactionId=TXN-0000000047
//                        String[] urlData = url.split("=");
//                        String id = urlData[1].substring(0,urlData[1].indexOf("&"));
//                        String clientTransactionId = urlData[2];
//                        if (!TextUtils.isEmpty(id) && !TextUtils.isEmpty(clientTransactionId)) {
////                            goToPlanPage(id, clientTransactionId);
//
//                        }
                        goToPreviousPage();
                    }*/
//                    Toast.makeText(mContext,"Page link",Toast.LENGTH_SHORT).show();
                    // Return false means, web view will handle the link
                    return false;
                }
                /*if(url.startsWith("http")){
                    Toast.makeText(mContext,"Page link",Toast.LENGTH_SHORT).show();
                    // Return false means, web view will handle the link
                    return false;
                }*/else if(url.startsWith("tel:")){
                    // Handle the tel: link
                    checkCallFunctionality(url);

                    // Return true means, leave the current web view and handle the url itself
                    return true;
                }else if (url.startsWith("mailto:")) {
                    MailTo mt = MailTo.parse(url);
                    Intent i = newEmailIntent(mContext, mt.getTo(), "", "", "");
                    startActivity(i);
                    view.reload();
                    return true;

                }

                return false;
            }

            public void onPageFinished(WebView view, String url) {
                binding.pbTerms.setVisibility(View.GONE);
            }
        });


        if (!TextUtils.isEmpty(forWhich)) {
            if (forWhich.equalsIgnoreCase("Terms")) {
                binding.toolbar.activityTitle.setText(R.string.terms_conditions);
                binding.webView.loadUrl(AppUrls.BASE_URL_STATIC + "TermsAndCondition");
            } else if (forWhich.equalsIgnoreCase("Help")) {
                binding.toolbar.activityTitle.setText(R.string.help_and_support);
                binding.webView.loadUrl(AppUrls.BASE_URL_STATIC + "HelpAndSupport");
            }else  if (forWhich.equalsIgnoreCase("payment") && !TextUtils.isEmpty(paymentUrl)){
                binding.toolbar.activityTitle.setText(R.string.complete_payment);
                binding.webView.loadUrl(paymentUrl);
            }
            else {
                binding.toolbar.activityTitle.setText(R.string.about_app);
                binding.webView.loadUrl(AppUrls.BASE_URL_STATIC + "AboutApp");
            }
        }
    }

    private void goToPreviousPage() {
        Intent intent = new Intent();
        setResult(RESULT_OK,intent);
        finish();
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
