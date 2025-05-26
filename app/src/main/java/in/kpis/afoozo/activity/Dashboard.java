package in.kpis.afoozo.activity;
import in.kpis.afoozo.bean.TopSellingBean;

import android.Manifest;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.databinding.DataBindingUtil;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import com.blikoon.qrcodescanner.QrCodeActivity;
import com.kpis.afoozo.R;
import com.kpis.afoozo.databinding.ActivityDashboardBinding;

import java.io.UnsupportedEncodingException;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import in.kpis.afoozo.bean.WalletBean;
import in.kpis.afoozo.fragment.HomeFrag;
import in.kpis.afoozo.fragment.NavMenuFrag;
import in.kpis.afoozo.okhttpServcice.AppUrls;
import in.kpis.afoozo.okhttpServcice.NetworkManager;
import in.kpis.afoozo.okhttpServcice.response.ResponseClass;
import in.kpis.afoozo.service.DataObject;
import in.kpis.afoozo.util.Constants;
import in.kpis.afoozo.util.SharedPref;
import in.kpis.afoozo.util.Utils;

public class Dashboard extends AppCompatActivity implements NavMenuFrag.FragmentDrawerListener {
    private Toolbar toolbar;
    private Context mContext;

    private TopSellingBean topSellingBean;
    private ActivityDashboardBinding binding;
    boolean doubleBackToExitPressedOnce = false;
    private NavMenuFrag navMenuFrag;
    private Fragment fragment = new HomeFrag();
    private Fragment fragmentHome = new HomeFrag();
    private android.app.AlertDialog dialog;
    private Intent mServiceIntent;
    public boolean isFromSplash;
    public boolean isFromNotification;
    public boolean isHomeLoad;
    private boolean isPermission;
    private int REQUEST_CODE_QR_SCAN = 400;
    private int requestPermissionForPhoto = 401;
    private ActionBarDrawerToggle mDrawerToggle;
    private double totalBalance;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_dashboard);
        if (getIntent().getExtras() != null) {
            isFromSplash = getIntent().getBooleanExtra(Constants.IS_FROM_SPLASH, false);
            isFromNotification = getIntent().getBooleanExtra(Constants.IS_FROM_NOTIFICATION, false);
        }
        mContext = Dashboard.this;
        initialize();

//        BottomNavigationView bottomNav = findViewById(R.id.bottom_navigation);
//        bottomNav.setOnNavigationItemSelectedListener(item -> {
//            switch (item.getItemId()) {
//                case R.id.nav_dine_in:
//                    // Dine In: open DineInActi with proper intent extra
//                    Intent dineInIntent = new Intent(this, DineInActi.class);
//                    dineInIntent.putExtra(Constants.FROM_WHICH, getString(R.string.dine_in));
//                    startActivity(dineInIntent);
//                    return true;
//                case R.id.nav_delivery:
//                    // Delivery: open AddressActi with proper intent extras
//                    Intent intent = new Intent(mContext, AddressActi.class);
//                    intent.putExtra(Constants.IS_FROM_CART, true);
//                    intent.putExtra(Constants.FROM_WHICH, getString(R.string.delivery));
////                    intent.putExtra(Constants.TITLE, topSellingBean.getHomeDeliveryTopSellingItem().get(position).getTitle());
////                    intent.putExtra(Constants.IS_OPEN, topSellingBean.getHomeDeliveryTopSellingItem().get(position).isRestaurantOpen());
////                    intent.putExtra(Constants.RESTAURANT_ID, topSellingBean.getHomeDeliveryTopSellingItem().get(position).getRestaurantUuid());
////                    intent.putExtra(Constants.TAKEAWAY_MIN_ORDER_VALUE, topSellingBean.getHomeDeliveryTopSellingItem().get(position).getTakeAwayMinimumOrderAmount());
////                    intent.putExtra(Constants.DELIVERY_MIN_ORDER_VALUE, topSellingBean.getHomeDeliveryTopSellingItem().get(position).getDeliveryMinimumOrderAmount());
//                    startActivityForResult(intent, 103);
//                    goToDeliveryRestaurantScreen(getString(R.string.delivery))
//                    return true;
////                case R.id.nav_take_away:
////                    // Take Away: open RestaurantActi with proper intent extra
////                    Intent takeAwayIntent = new Intent(this, RestaurantActi.class);
////                    takeAwayIntent.putExtra(Constants.FROM_WHICH, getString(R.string.take_away));
////                    startActivity(takeAwayIntent);
////                    return true;
//                case R.id.nav_check_in:
//                    // Check-In: open ChecoutScanActi
//                    Intent checkInIntent = new Intent(this, ChecoutScanActi.class);
//                    startActivity(checkInIntent);
//                    return true;
//            }
//            return false;
//        });
    }
    @Override
    protected void onResume() {
        super.onResume();
        if (isHomeLoad)
            checkNotificationData();
        callGetBalanceApi();
//        LocalBroadcastManager.getInstance(this).registerReceiver(mMessageReceiver, new IntentFilter(Constants.LOCAL_INTENT_ORDER_CANCELED));
    }
    @Override
    protected void onPause() {
        super.onPause();
//        LocalBroadcastManager.getInstance(this).unregisterReceiver(mMessageReceiver);
    }
    private void initialize() {
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        navMenuFrag = (NavMenuFrag) getSupportFragmentManager().findFragmentById(R.id.fragment_navigation_drawer);
        navMenuFrag.setUp(R.id.fragment_navigation_drawer, (DrawerLayout) findViewById(R.id.drawerLayout), toolbar);
        navMenuFrag.setDrawerListener(this);
        mDrawerToggle = new ActionBarDrawerToggle(this, binding.drawerLayout, toolbar, R.string.drawer_open, R.string.drawer_close);
        mDrawerToggle.getDrawerArrowDrawable().setColor(getResources().getColor(R.color.colorAccent));
        binding.toolbar.activityTitle.setVisibility(View.GONE);
        binding.toolbar.txtAfoozo.setVisibility(View.VISIBLE);
        binding.toolbar.toolbarQR.setVisibility(View.GONE);
        binding.toolbar.txtBalance.setVisibility(View.VISIBLE);
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        binding.toolbar.toolbarQR.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (checkAndRequestPermissions())
                    showScanScreen();
            }
        });
        displayView(getResources().getString(R.string.dashboard));
        if (fragment == null)
            fragment = new HomeFrag();
        loadHomeFragment();
    }
    private void checkNotificationData() {
        Utils.dismissProgressDialog();
        DataObject notificationBean = (DataObject) Utils.getJsonToClassObject(SharedPref.getNotificationJSON(mContext), DataObject.class);
        if (notificationBean != null) {
            Intent intent = new Intent(mContext, NotificationPopUpActi.class);
            intent.putExtra(Constants.ORDER_TYPE, notificationBean.getType());
            intent.putExtra(Constants.MESSAGE, notificationBean.getMessage());
            intent.putExtra(Constants.IMAGE_URL, notificationBean.getImage());
            startActivity(intent);
        }
    }
    private void showScanScreen() {
        Intent i = new Intent(Dashboard.this, QrCodeActivity.class);
        startActivityForResult(i, REQUEST_CODE_QR_SCAN);
    }
    private void loadHomeFragment() {
        if (fragment != null) {
//            FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
//
//            fragmentTransaction.setCustomAnimations(android.R.anim.fade_in,
//                    android.R.anim.fade_out);
////            fragmentTransaction.replace(R.id.fmContainer, fragment, CURRENT_TAG);
//            fragmentTransaction.replace(R.id.fmContainer, fragment).addToBackStack(null).commit();
////            fragmentTransaction.commitAllowingStateLoss();

            Utils.replaceFrg(this, fragment, false, Constants.DEFAULT_ID);
        } else {
            Toast.makeText(this, "FRAGMNT", Toast.LENGTH_SHORT).show();
        }
    }
    /**
     * This method is used to set text from fragment
     *
     * @param title = title which we wants to show
     */
    public void setTitle(String title) {
        binding.toolbar.activityTitle.setText(title);
    }
    @Override
    public void onDrawerItemSelected(View view, String menuNmae) {
        displayView(menuNmae);
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CODE_QR_SCAN && resultCode == RESULT_OK) {
            if (data == null)
                showScanScreen();
            //Getting the passed result
            String scanResult = data.getStringExtra("com.blikoon.qrcodescanner.got_qr_scan_relult");
//            Log.d(LOGTAG,"Have scan result in your app activity :"+ result);
            String scannedCode = scanResult.substring((scanResult.indexOf('?') + 1), scanResult.length());
            if (scannedCode != null)
                callScanQrCodeApi(scannedCode);

        }
    }
    private void displayView(String menuNmae) {
//        fragment = null;
        if (menuNmae.equalsIgnoreCase(mContext.getResources().getString(R.string.home))) {
            fragment = new HomeFrag();
            loadHomeFragment();
        } else if (menuNmae.equalsIgnoreCase(mContext.getResources().getString(R.string.profile))) {
            Utils.startActivity(mContext, ProfileActi.class);
        } else if (menuNmae.equalsIgnoreCase(mContext.getResources().getString(R.string.reservation))) {
            Utils.startActivity(mContext, ReservationActi.class);
        } else if (menuNmae.equalsIgnoreCase(mContext.getResources().getString(R.string.events))) {
            Utils.startActivity(mContext, EventsActi.class);
        } else if (menuNmae.equalsIgnoreCase(mContext.getResources().getString(R.string.check_in))) {
            Utils.startActivity(mContext, CheckInOrdersActi.class);
        }else if (menuNmae.equalsIgnoreCase(mContext.getResources().getString(R.string.notification))) {
            Utils.startActivity(mContext, NotificationActi.class);
        } else if (menuNmae.equalsIgnoreCase(mContext.getResources().getString(R.string.prepaid_card))) {
            Utils.startActivity(mContext, ATMBarActi.class);
        } else if (menuNmae.equalsIgnoreCase(mContext.getResources().getString(R.string.wallet_money))) {
            Intent intent = new Intent(mContext, WalletActi.class);
            intent.putExtra(Constants.IS_FROM_HOME, true);
            startActivity(intent);
        }
        if (menuNmae.equalsIgnoreCase(mContext.getResources().getString(R.string.order_history))) {
            Utils.startActivity(mContext, NewOrderHistoryActi.class);
        }if (menuNmae.equalsIgnoreCase(mContext.getResources().getString(R.string.bill_to_company_orders))) {
            Utils.startActivity(mContext, OrderBillToCompanyHistoryActi.class);
        }
        if (menuNmae.equalsIgnoreCase(mContext.getResources().getString(R.string.help_and_support))) {
            goToWebActivity("Help");
        }
        if (menuNmae.equalsIgnoreCase(mContext.getResources().getString(R.string.live_orders))) {
            Utils.startActivity(mContext, LiveOrderActi.class);
        } else if (menuNmae.equalsIgnoreCase(mContext.getResources().getString(R.string.offers_rewards))) {
            Utils.startActivity(mContext, RewardsNewActi.class);
        } else if (menuNmae.equalsIgnoreCase(mContext.getResources().getString(R.string.locations))) {
            goToRestaurantScreen();
        } else if (menuNmae.equalsIgnoreCase(mContext.getResources().getString(R.string.feedback))) {
            Utils.startActivity(mContext, FeedbackActi.class);
        }
        if (menuNmae.equalsIgnoreCase(mContext.getResources().getString(R.string.about_app))) {
            goToWebActivity("About");
        } else if (menuNmae.equalsIgnoreCase(mContext.getResources().getString(R.string.terms_conditions))) {
            goToWebActivity("Terms");
        }
//        else if (menuNmae.equalsIgnoreCase(mContext.getResources().getString(R.string.help_and_support))) {
//            goToWebActivity("Help");
//        }
        else if (menuNmae.equalsIgnoreCase(mContext.getResources().getString(R.string.live_chat))) {
//            ZohoSalesIQ.Chat.show();
        } else if (menuNmae.equalsIgnoreCase(mContext.getResources().getString(R.string.logout))) {

            dialog = Utils.retryAlertDialog(this, getResources().getString(R.string.app_name), getResources().getString(R.string.Are_you_sure_to_logout), getResources().getString(R.string.Cancel), getResources().getString(R.string.Yes), new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Utils.logout(Dashboard.this);
                    dialog.dismiss();
                }
            });


        }
        if (fragment != null) {
            FragmentManager fragmentManager = getSupportFragmentManager();
            fragmentManager.beginTransaction().replace(R.id.fmContainer, fragment).addToBackStack(null).commit();
        }
    }
    private void goToWebActivity(String about) {
        Intent intent = new Intent(mContext, TermsActi.class);
        intent.putExtra("ForWhich", about);
        startActivity(intent);
    }
    private void goToRestaurantScreen() {
        Intent intent = new Intent(mContext, RestaurantActi.class);
        intent.putExtra(Constants.FROM_WHICH, getString(R.string.locations));
        startActivity(intent);
    }
    @Override
    public void onBackPressed() {
        super.onBackPressed();
        backCountToExit();
    }
    private void backCountToExit() {

        FragmentManager fm = this.getSupportFragmentManager();

        if (fm.getBackStackEntryCount() > 1) {

            fm.popBackStack();


        } else {

            if (doubleBackToExitPressedOnce) {
                finish();
            }

            this.doubleBackToExitPressedOnce = true;
            Toast.makeText(this, getString(R.string.Please_BACK_again_to_exit), Toast.LENGTH_SHORT).show();

            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    doubleBackToExitPressedOnce = false;
                }
            }, 2000);


        }
    }
    private boolean checkAndRequestPermissions() {
        int cameraPermission = ContextCompat.checkSelfPermission(mContext, Manifest.permission.CAMERA);
//        int flashPermission = ContextCompat.checkSelfPermission(mContext, Manifest.permission.FLASHLIGHT);

        List<String> listPermissionsNeeded = new ArrayList<>();

        if (cameraPermission != PackageManager.PERMISSION_GRANTED) {
            listPermissionsNeeded.add(Manifest.permission.CAMERA);
        }
//        if (cameraPermission != PackageManager.PERMISSION_GRANTED) {
//            listPermissionsNeeded.add(Manifest.permission.CAMERA);
//        }

        if (!listPermissionsNeeded.isEmpty()) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                requestPermissions(listPermissionsNeeded.toArray(new String[listPermissionsNeeded.size()]), requestPermissionForPhoto);
            }
            return false;
        }
        return true;
    }
    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == requestPermissionForPhoto) {

            Map<String, Integer> perms = new HashMap<>();
            // Initialize the map with both permissions

            if (grantResults.length > 0) {
                for (int i = 0; i < permissions.length; i++) {
                    perms.put(permissions[i], grantResults[i]);
                }

                // Check for both permissions
                if (perms.get(Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED) {
                    isPermission = true;
                    showScanScreen();

                } else if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.CAMERA)) {

                    showDialogOK("Camera and Storage Permission required for this app",
                            new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    switch (which) {
                                        case DialogInterface.BUTTON_POSITIVE:
                                            checkAndRequestPermissions();
                                            break;
                                        case DialogInterface.BUTTON_NEGATIVE:
                                            // proceed with logic by disabling the related features or quit the app.
                                            finish();
                                            break;
                                    }
                                }
                            });
                } else {
                    Toast.makeText(mContext, "Go to settings and enable permissions", Toast.LENGTH_LONG).show();
                }
            }
        }
    }
    private void showDialogOK(String message, DialogInterface.OnClickListener okListener) {
        new AlertDialog.Builder(mContext)
                .setMessage(message)
                .setPositiveButton("OK", okListener)
                .setNegativeButton("Cancel", okListener)
                .create()
                .show();
    }
    private void callScanQrCodeApi(String code) {
        try {
            new NetworkManager(String.class, new NetworkManager.OnCallback<String>() {
                @Override
                public void onSuccess(boolean success, ResponseClass responseClass, int which) {
                    if (responseClass.isSuccess()) {
//                        Utils.showCenterToast(mContext, responseClass.getMessage());
//                        Utils.retryAlertDialog(mContext, getString(R.string.app_name), responseClass.getMessage(), getString(R.string.Ok), "", null);
                        Utils.showOfferPopUp(Dashboard.this, false, responseClass.getMessage(), null, new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                Utils.hideOfferPopUp();
                                Utils.startActivity(mContext, RewardsNewActi.class);
                            }
                        });
                    } else {
                        Utils.showCenterToast(mContext, responseClass.getMessage());
                    }
                }

                @Override
                public void onFailure(boolean success, String response, int which) {
                    Utils.showCenterToast(mContext, getString(R.string.internet_connection_not_found));
                }
            }).callAPIJson(mContext, Constants.VAL_GET, AppUrls.SCAN_QR_REWARD + code, "", "Loading...", true, AppUrls.REQUESTTAG_SCANQRREWARD);

        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
    }
    /* this api use for getWallet balance and show on TitleBar */
    public void callGetBalanceApi() {

        try {
            new NetworkManager(WalletBean.class, new NetworkManager.OnCallback<WalletBean>() {
                @Override
                public void onSuccess(boolean success, ResponseClass responseClass, int which) {

                    if (responseClass.isSuccess()) {
                        WalletBean    walletBean = (WalletBean) responseClass.getResponsePacket();
                        binding.toolbar.txtBalance.setText(getString(R.string.Rs) + new DecimalFormat("0.00").format(walletBean.getWalletBalance()));
                    }
                }

                @Override
                public void onFailure(boolean success, String response, int which) {
                    Utils.showCenterToast(mContext, "Check your Internet Connection");
                }
            }).callAPIJson(mContext, Constants.VAL_GET, AppUrls.GET_COIN_AND_WALLET_BALANCE, "", "Loading...", true, AppUrls.REQUESTTAG_GETCOINANDWALLETBALANCE);

        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
    }

}
