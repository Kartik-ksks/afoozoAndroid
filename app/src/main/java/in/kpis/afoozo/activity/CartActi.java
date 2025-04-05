package in.kpis.afoozo.activity;

import static android.view.View.GONE;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.CompoundButton;
import android.widget.DatePicker;
import android.widget.SeekBar;
import android.widget.TimePicker;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.bumptech.glide.Glide;
import com.cashfree.pg.api.CFPaymentGatewayService;
import com.cashfree.pg.core.api.CFSession;
import com.cashfree.pg.core.api.callback.CFCheckoutResponseCallback;
import com.cashfree.pg.core.api.exception.CFException;
import com.cashfree.pg.core.api.utils.CFErrorResponse;
import com.cashfree.pg.core.api.webcheckout.CFWebCheckoutPayment;
import com.cashfree.pg.core.api.webcheckout.CFWebCheckoutTheme;
import com.clevertap.android.sdk.displayunits.DisplayUnitListener;
import com.clevertap.android.sdk.displayunits.model.CleverTapDisplayUnit;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.kpis.afoozo.R;
import com.kpis.afoozo.databinding.ActivityCartBinding;
import com.kpis.afoozo.databinding.InstructionPopupLayoutBinding;
import com.kpis.afoozo.databinding.PopupDiscounOfferBinding;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;

import in.kpis.afoozo.AppInitialization;
import in.kpis.afoozo.adapter.CartAdapter;
import in.kpis.afoozo.adapter.YouMayLikeAdaper;
import in.kpis.afoozo.bean.BankBean;
import in.kpis.afoozo.bean.BeanPaytmTxnResponse;
import in.kpis.afoozo.bean.CardBean;
import in.kpis.afoozo.bean.CashFreeBean;
import in.kpis.afoozo.bean.CheckSumBean;
import in.kpis.afoozo.bean.CreateOrderBean;
import in.kpis.afoozo.bean.DeliveryOfferBean;
import in.kpis.afoozo.bean.LastPaymentModeBean;
import in.kpis.afoozo.bean.MenuListBean;
import in.kpis.afoozo.bean.OrderDetailsBean;
import in.kpis.afoozo.bean.OrderDetailsItemBean;
import in.kpis.afoozo.bean.PayOrderBean;
import in.kpis.afoozo.bean.PlaceOrderBean;
import in.kpis.afoozo.bean.SavedCardBean;
import in.kpis.afoozo.bean.SodexoBean;
import in.kpis.afoozo.bean.UserBean;
import in.kpis.afoozo.bean.WalletBean;
import in.kpis.afoozo.custome.ProgressBarAnimation;
import in.kpis.afoozo.custome.RangeTimePickerDialog;
import in.kpis.afoozo.interfaces.AddRemoveClick;
import in.kpis.afoozo.okhttpServcice.AppUrls;
import in.kpis.afoozo.okhttpServcice.NetworkManager;
import in.kpis.afoozo.okhttpServcice.response.ResponseClass;
import in.kpis.afoozo.sqlite.DatabaseHelper;
import in.kpis.afoozo.util.Constants;
import in.kpis.afoozo.util.SharedPref;
import in.kpis.afoozo.util.Utils;

public class CartActi extends AppCompatActivity implements AddRemoveClick, View.OnClickListener, DisplayUnitListener, CFCheckoutResponseCallback {


    private DatabaseHelper db;

    private Toolbar toolbar;

    private Context mContext;

    private ActivityCartBinding binding;

    private CartAdapter mAdapter;
    private YouMayLikeAdaper likeAdaper;

    private ArrayList<OrderDetailsItemBean> itemList = new ArrayList<>();
    private ArrayList<MenuListBean> likeList = new ArrayList<>();

    private BankBean bankBean;
    private CardBean cardBean;
    private SavedCardBean savedCardBean;
    private LastPaymentModeBean lastPaymentModeBean;

    private OrderDetailsBean orderDetails;
//    private RoomDetailBean roomDetails;

    private boolean isCouponApplied;
    private boolean isSavedCard;

    private int REQUEST_COUPON = 101;
    private int ADDRESS_REQUEST_CODE = 102;
    private int PAYMENT_TYPE_REQUEST_CODE = 103;
    private int PAYMENT_REQUEST_CODE = 104;
    private int DELIVERY_REQUEST_CODE = 105;
    private int SODEXO_PAYMENT_REQUEST_CODE = 106;

    private int takeawayMinOrderValue;
    private int deliveryMinOrderValue;

    private double couponDiscount;
    private double totalPrice;
    private double latitude;

    private String orderId;
    private String couponCode;
    private String paymentMode;
    private String paymentType;
    private String transactionId;
    private String nickName;
    private String fromWhich;
    private String paymentRequestId;
    private boolean deliveryLaterFlag;

    private Dialog alert;
    private long deliveryTime;

    private double walletBalance;
    private double coinBalance;
    private double payableAmountByWallet;
    private double payableAmountByCoin;
    private double remainingAmount;
    private AddressBean addressBean;
    private String placeOrderJson;
    private CheckSumBean checkSumBean;
    private BeanPaytmTxnResponse beanPaytmTxnResponse;
    private boolean isOfferSet;
    private DeliveryOfferBean deliveryOfferBean;
    private MediaPlayer mediaPlayer;
    private String restaurantId;
    private Dialog instructionAlert;
    //    private PayUmoneySdkInitializer.PaymentParam.Builder builder;
    private int mYear;
    private int mMonth;
    private int mDay;
    private int selectedHour;
    private int selectedMinute;
    private Calendar finalToSend = Calendar.getInstance();
    private Calendar dateTimeCalender = Calendar.getInstance();

    private RangeTimePickerDialog timePickerDialog;

    private ArrayList<String> paymentTypeList;

    private PlaceOrderBean cleverTapBean;
    private String restaurantName;
    private CashFreeBean cashFreeResponse = new CashFreeBean();
    private SodexoBean sodexoBean;

    CreateOrderBean createOrderBean = new CreateOrderBean();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_cart);

        AppInitialization.getInstance().clevertapDefaultInstance.setDisplayUnitListener(this);

        if (getIntent().getExtras() != null) {
            orderId = getIntent().getStringExtra(Constants.ORDER_ID);
            restaurantId = getIntent().getStringExtra(Constants.RESTAURANT_ID);
            fromWhich = getIntent().getStringExtra(Constants.FROM_WHICH);
            restaurantName = getIntent().getStringExtra(Constants.RESTAURANT_NAME);

            takeawayMinOrderValue = getIntent().getIntExtra(Constants.TAKEAWAY_MIN_ORDER_VALUE, 0);
            deliveryMinOrderValue = getIntent().getIntExtra(Constants.DELIVERY_MIN_ORDER_VALUE, 0);
        }

        mContext = CartActi.this;
        db = new DatabaseHelper(mContext);
        try {
            // Set Callback for payment result
            CFPaymentGatewayService.getInstance().setCheckoutCallback(this);
        } catch (CFException exception) {
            Log.d("exceptionDetails", "##" + exception);
            exception.printStackTrace();
        }

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

        binding.toolbar.activityTitle.setText(getString(R.string.checkout));

        binding.rvCart.setLayoutManager(new LinearLayoutManager(mContext));
        binding.rvCart.setItemAnimator(new DefaultItemAnimator());
//        binding.rvCart.addItemDecoration(new DividerItemDecorationColorPrimary(mContext));

        binding.rvYouMayLike.setLayoutManager(new LinearLayoutManager(mContext, LinearLayoutManager.HORIZONTAL, false));
        binding.rvYouMayLike.setItemAnimator(new DefaultItemAnimator());

        binding.setIsPayment(false);
        binding.setIsAddress(false);

        binding.cbWallet.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                walletCheckUncheckProcess();
            }
        });
        binding.cbCoin.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                coinCheckUncheckProcess();
            }
        });
        /* this for new type Apply coin*/
//        binding.cbCoinLoyalty.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
//            @Override
//            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
//                if(isChecked)
//                    callApplyLoyaltyCoinApi();
//                else
//                    callRemoveCoinApi();
//            }
//        });

        if (fromWhich.equals(getString(R.string.dine_in)) || fromWhich.equals(getString(R.string.take_away)) || fromWhich.equals(getString(R.string.cafe))) {
            binding.setIsAddress(true);
            binding.llAddressMain.setVisibility(View.GONE);
            binding.btnDeliveryLatter.setVisibility(View.GONE);
            binding.llRoomBooking.setVisibility(View.GONE);
            binding.llFood.setVisibility(View.VISIBLE);
        } else if (fromWhich.equals(Constants.ROOM_BOOKING)) {
            binding.setIsAddress(true);
            binding.llRoomBooking.setVisibility(View.VISIBLE);
            binding.llFood.setVisibility(GONE);
        } else {
            binding.llRoomBooking.setVisibility(View.GONE);
            binding.llFood.setVisibility(View.VISIBLE);

//            binding.btnDeliveryLatter.setVisibility(View.VISIBLE);
            binding.llbtnOrder.setPadding(5, 5, 5, 5);
            binding.setIsAddress(false);
            binding.llAddressMain.setVisibility(View.VISIBLE);
        }
        if (fromWhich.equals(getString(R.string.cafe))) {
            binding.llCoin.setVisibility(GONE);
        }


        binding.sbOrder.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if (progress == 100) {
                    binding.llbtnOrder.setVisibility(View.VISIBLE);
                    binding.llProgress.setVisibility(View.GONE);
//                    if (fromWhich.equals(Constants.ROOM_BOOKING)) {
//                        callRoomPlaceApi();
//                    } else
                    paymentProcess();
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });

        Utils.progressDialog(mContext, "");
        callGetBalanceApi(false);


    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btnDeliveryLatter:
                if (fromWhich.equalsIgnoreCase(getString(R.string.delivery))) {
                    if (binding.getIsAddress()) {
                        deliveryLaterFlag = true;
                        selectDate();
                    } else
                        Toast.makeText(mContext, getString(R.string.please_delivery_add_address) + "", Toast.LENGTH_SHORT).show();
                }
                break;
            case R.id.btnDeliveryNow:
                deliveryLaterFlag = false;
                if (fromWhich.equalsIgnoreCase(getString(R.string.delivery))) {
                    if (binding.getIsAddress()) {
                        startProgress();
                    } else
                        Toast.makeText(mContext, getString(R.string.please_delivery_add_address) + "", Toast.LENGTH_SHORT).show();
                } else {
                    startProgress();
                }
                break;
        }
    }


    @Override
    public void addClick(boolean isSubCat, int catPosition, int subCatPosition, int itemPosition) {
        Utils.progressDialog(mContext, "");
        callUpdateItemApi(itemPosition, "ADD");
    }

    @Override
    public void removeClick(boolean isSubCat, int catPosition, int subCatPosition, int itemPosition) {
        Utils.progressDialog(mContext, "");
        callUpdateItemApi(itemPosition, "LESS");
    }

    @Override
    public void editClick(boolean isSubcategory, int catPosition, int subCatPosition, int itemPosition) {
        showInstructionPopup(itemPosition);
    }

    @Override
    public void stickyClick(int position) {

    }

    @Override
    public void scrollCallback() {

    }

    public void showInfoPopUp(View view) {
        if (orderDetails != null && orderDetails.getTaxJson() != null && orderDetails.getTaxJson().size() > 0)
            Utils.showTaxInfoPopUp(mContext, orderDetails.getTaxJson());
    }

    private void showInstructionPopup(int itemPosition) {
        instructionAlert = new Dialog(mContext);
        instructionAlert.requestWindowFeature(Window.FEATURE_NO_TITLE);
        instructionAlert.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        Window dialogWindow = instructionAlert.getWindow();
        WindowManager.LayoutParams lp = dialogWindow.getAttributes();
        dialogWindow.setGravity(Gravity.CENTER);

        lp.width = WindowManager.LayoutParams.MATCH_PARENT;

        dialogWindow.setAttributes(lp);

        InstructionPopupLayoutBinding instructionBinding = DataBindingUtil.inflate(LayoutInflater.from(mContext), R.layout.instruction_popup_layout, null, false);
        instructionAlert.setContentView(instructionBinding.getRoot());

//        alert.setContentView(R.layout.buy_popup_layout);

        if (itemList.get(itemPosition).getCustomization() != null && itemList.get(itemPosition).getCustomization().size() > 0)
            instructionBinding.etPopInst.setText(db.checkAndGetInstruction(itemList.get(itemPosition).getItemId(), itemList.get(itemPosition).getCustomization()));
        else
            instructionBinding.etPopInst.setText(db.getInstruction(itemList.get(itemPosition).getItemId()));

        instructionBinding.btnPopAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                callUpdateInstructionApi(itemPosition, instructionBinding.etPopInst.getText().toString());
            }
        });


        instructionAlert.getWindow().getAttributes().windowAnimations = R.style.Animation_Dialog;
        instructionAlert.setCancelable(true);
        instructionAlert.show();
    }

    public void couponProcess(View view) {
        if (!isCouponApplied) {
            Intent intent = new Intent(mContext, CouponActi.class);
            intent.putExtra(Constants.ORDER_ID, orderId);
            intent.putExtra(Constants.RESTAURANT_ID, restaurantId);
            intent.putExtra(Constants.FROM_WHICH, fromWhich);
            startActivityForResult(intent, REQUEST_COUPON);
        } else {
            Utils.progressDialog(mContext, "");
            if (fromWhich.equalsIgnoreCase(Constants.ROOM_BOOKING)) {
                callRemoveCouponApi(AppUrls.REMOVECOUPON_FOR_BOOKING);
            } else
                callRemoveCouponApi(AppUrls.REMOVE_COUPON);
        }

    }

    /**
     * Show Applied Coupon code
     */

    private void setCouponCode() {
        isCouponApplied = true;
        binding.txtApplyCoupon.setText(orderDetails.getCouponCode());
        binding.imvCoupon.setImageResource(R.drawable.ic_delete);
    }

    /**
     * This method is use to remove coupon code
     */
    private void restoreCouponCode() {
        isCouponApplied = false;
        binding.imvCoupon.setImageResource(R.drawable.ic_next);
        binding.txtApplyCoupon.setText(getString(R.string.apply_promo_code));
    }

    public void goToAddressScreen(View view) {
        Intent intent = new Intent(mContext, AddressActi.class);
        intent.putExtra(Constants.IS_FROM_CART, true);
        startActivityForResult(intent, ADDRESS_REQUEST_CODE);
    }

    public void showOffer(View view) {
        String msg = getString(R.string.get_free_on_minimum_orders_value).replace("_", (deliveryOfferBean.getFreeItemQty() + " " + deliveryOfferBean.getFreeItemTitle()));
        msg = msg.replace("*", new DecimalFormat("0").format(deliveryOfferBean.getMinimumOrderValue()));
//        showDiscountOfferPopup(false, msg);

        Utils.showOfferPopUp(CartActi.this, false, msg, null, new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Utils.hideOfferPopUp();
            }
        });
    }

    private void setData() {

        if (orderDetails.getItemList() != null && orderDetails.getItemList().size() > 0) {

            binding.nsvData.setVisibility(View.VISIBLE);
            binding.txtNoData.setVisibility(View.GONE);
            itemList = orderDetails.getItemList();
            mAdapter = new CartAdapter(mContext, itemList, this);
            binding.rvCart.setAdapter(mAdapter);
            mAdapter.notifyDataSetChanged();

            if (!TextUtils.isEmpty(orderDetails.getCouponCode()))
                setCouponCode();
            else
                restoreCouponCode();
            couponDiscount = orderDetails.getCouponDiscountAmount();
            setTotalPrice();
            setWalletAmount();
            if (!TextUtils.isEmpty(orderDetails.getDeliveryCompleteAddress())) {
                binding.txtDeliveryAdd.setText(orderDetails.getDeliveryCompleteAddress());
                binding.llAddress.setVisibility(View.VISIBLE);
                binding.txtAddAddress.setVisibility(View.GONE);
                binding.setIsAddress(true);
                if (binding.getIsPayment()) {
                    showHidePayButton(true);
//                    binding.setIsPlace(true);
                }
            }

            if (!isOfferSet) {
                isOfferSet = true;
                deliveryOfferBean = (DeliveryOfferBean) Utils.getJsonToClassObject(SharedPref.getDeliveryOfferJson(mContext), DeliveryOfferBean.class);
                if (deliveryOfferBean != null && !TextUtils.isEmpty(deliveryOfferBean.getOrderType()) && deliveryOfferBean.getOrderType().equalsIgnoreCase(fromWhich) && ((Calendar.getInstance().getTimeInMillis() - deliveryOfferBean.getLastRequestTime()) / 60000) < 60) {
                    binding.setIsOfferAvailable(true);
                    String msg = getString(R.string.get_free_on_minimum_orders_value).replace("_", (deliveryOfferBean.getFreeItemQty() + " " + deliveryOfferBean.getFreeItemTitle()));
                    msg = msg.replace("*", new DecimalFormat("0").format(deliveryOfferBean.getMinimumOrderValue()));
                    binding.txtOfferMessage.setText(msg);
                } else
                    binding.setIsOfferAvailable(false);
            }

        } else {
            binding.nsvData.setVisibility(View.GONE);
            binding.txtNoData.setVisibility(View.VISIBLE);
            binding.llbtnOrder.setVisibility(View.GONE);
            binding.btnDeliveryNow.setVisibility(View.GONE);
        }

    }

    private void setRoomData() {
        binding.llFood.setVisibility(GONE);
        binding.llRoomBooking.setVisibility(View.VISIBLE);
        binding.txtRestaurantName.setText(orderDetails.getRestaurantName());
        binding.txtRoomType.setText(orderDetails.getRoomType());
        binding.txtRoomNumber.setText(orderDetails.getRoomNumber());
        binding.txtCheckInDate.setText(Utils.getDateTimeForOrders(orderDetails.getCheckInDateTimeStamp()));
        binding.txtCheckOutDate.setText(Utils.getDateTimeForOrders(orderDetails.getCheckOutDateTimeStamp()));
        binding.txtRoomCharge.setText(mContext.getString(R.string.Rs) + orderDetails.getBookingSubTotal());
        binding.txtRoomTax.setText(mContext.getString(R.string.Rs) + orderDetails.getTaxAmount());
        binding.txtRoomTotal.setText(mContext.getString(R.string.Rs) + orderDetails.getOrderTotal());
        remainingAmount = orderDetails.getOrderTotal();
        if (orderDetails.getCouponDiscountAmount() > 0) {
            binding.llCouponRoom.setVisibility(View.VISIBLE);
            binding.txtCouponDisRoom.setText(getString(R.string.Rs) + " " + new DecimalFormat("0.00").format(orderDetails.getCouponDiscountAmount()));
        } else
            binding.llCouponRoom.setVisibility(View.GONE);
//        binding.txtPayWalletAmount.setText(getString(R.string.Rs) + 0);
        setWalletAmount();
    }

    private void setWalletAmount() {
        binding.txtAvailableWAmount.setText(getString(R.string.available_wallet_amount_) + " " + getString(R.string.Rs) + new DecimalFormat("0.00").format(walletBalance));
        binding.txtAvailableCoinAmount.setText(getString(R.string.available_coin_amount_) + " " + getString(R.string.Rs) + new DecimalFormat("0.00").format(coinBalance));
//        binding.txtAvailableCoinLoyaltyAmount.setText(getString(R.string.available_coin_amount_) + " " + getString(R.string.Rs) + new DecimalFormat("0.00").format(coinBalance));
        if (walletBalance > 0) {
            walletCheckUncheckProcess();
//        }else if(coinBalance > 0) {
//            coinCheckUncheckProcess();
        } else {
            remainingAmount = orderDetails.getOrderTotal();
            binding.txtPayWalletAmount.setText(getString(R.string.Rs) + 0);
            binding.llPaymentType.setVisibility(View.VISIBLE);
            binding.txtPayWalletAmount.setVisibility(View.GONE);

            if (binding.getIsAddress() && binding.getIsPayment())
                showHidePayButton(true);
            else
                showHidePayButton(false);
//                showHidePayButton(true);
        }
    }

    private void walletCheckUncheckProcess() {
        if (binding.cbWallet.isChecked()) {
            if (binding.cbCoin.isChecked()) {
                if (coinBalance > orderDetails.getOrderTotal()) {
                    payableAmountByCoin = orderDetails.getOrderTotal();
                    payableAmountByWallet = 0;
                    remainingAmount = 0;
                    binding.txtPayCoinAmount.setVisibility(View.VISIBLE);
                    binding.txtPayCoinAmount.setText(getString(R.string.Rs) + new DecimalFormat("0.00").format(payableAmountByCoin));
                    binding.txtPayWalletAmount.setVisibility(View.VISIBLE);
                    binding.txtPayWalletAmount.setText(getString(R.string.Rs) + new DecimalFormat("0.00").format(payableAmountByWallet));

                    binding.llPaymentType.setVisibility(View.GONE);
                    binding.setIsPayment(true);
                    showHidePayButton(true);
                } else {
                    payableAmountByCoin = coinBalance;
                    remainingAmount = orderDetails.getOrderTotal() - coinBalance;
                    if (walletBalance >= remainingAmount) {
                        payableAmountByWallet = remainingAmount;
                        remainingAmount = 0;
                        binding.txtPayWalletAmount.setVisibility(View.VISIBLE);

                        binding.txtPayWalletAmount.setText(getString(R.string.Rs) + new DecimalFormat("0.00").format(payableAmountByWallet));

                        binding.llPaymentType.setVisibility(View.GONE);
                        binding.setIsPayment(true);
//                if (binding.getIsAddress())
                        showHidePayButton(true);
                    } else {
                        payableAmountByWallet = walletBalance;
                        remainingAmount = remainingAmount - walletBalance;
//                     remainingAmount = orderDetails.getOrderTotal() - walletBalance;
                        binding.txtPayWalletAmount.setVisibility(View.VISIBLE);
                        binding.txtPayWalletAmount.setText(getString(R.string.Rs) + new DecimalFormat("0.00").format(payableAmountByWallet));
                        binding.llPaymentType.setVisibility(View.VISIBLE);
                        if (paymentType != null)
                            binding.setIsPayment(true);
                        else
                            binding.setIsPayment(false);
                        if (binding.getIsPayment() && binding.getIsAddress())
                            showHidePayButton(true);
                        else
                            showHidePayButton(true);
                    }
                }
            } else {
                binding.txtPayWalletAmount.setVisibility(View.VISIBLE);
                if (walletBalance > orderDetails.getOrderTotal()) {
                    payableAmountByWallet = orderDetails.getOrderTotal();
                    remainingAmount = 0;
                    binding.txtPayWalletAmount.setText(getString(R.string.Rs) + new DecimalFormat("0.00").format(payableAmountByWallet));

                    binding.llPaymentType.setVisibility(View.GONE);
                    binding.setIsPayment(true);
//                if (binding.getIsAddress())
                    showHidePayButton(true);
//                else
//                    showHidePayButton(false);
//                    showHidePayButton(false);
                } else {
                    payableAmountByWallet = walletBalance;
                    remainingAmount = orderDetails.getOrderTotal() - walletBalance;
                    binding.txtPayWalletAmount.setText(getString(R.string.Rs) + new DecimalFormat("0.00").format(payableAmountByWallet));
                    binding.llPaymentType.setVisibility(View.VISIBLE);
                    if (paymentType != null)
                        binding.setIsPayment(true);
                    else
                        binding.setIsPayment(false);
                    if (binding.getIsPayment() && binding.getIsAddress())
                        showHidePayButton(true);
                    else
                        showHidePayButton(true);
                }
            }
        } else {
            payableAmountByWallet = 0;
            binding.txtPayWalletAmount.setVisibility(View.GONE);
            if (binding.cbCoin.isChecked()) {
                if (coinBalance >= orderDetails.getOrderTotal()) {
                    binding.llPaymentType.setVisibility(View.GONE);
                    binding.setIsPayment(true);
                    showHidePayButton(true);
                } else {
                    payableAmountByWallet = 0;
                    remainingAmount = orderDetails.getOrderTotal() - coinBalance;
                    binding.llPaymentType.setVisibility(View.VISIBLE);
                    binding.txtPayWalletAmount.setVisibility(View.GONE);

                    if (paymentType != null)
                        binding.setIsPayment(true);
                    else
                        binding.setIsPayment(false);

//            if (binding.getIsPayment() && binding.getIsAddress())
                    if (binding.getIsPayment())
                        showHidePayButton(true);
                    else
                        showHidePayButton(false);
                }
            } else {
                payableAmountByWallet = 0;
                remainingAmount = orderDetails.getOrderTotal();
                binding.llPaymentType.setVisibility(View.VISIBLE);
                binding.txtPayWalletAmount.setVisibility(View.GONE);

                if (paymentType != null)
                    binding.setIsPayment(true);
                else
                    binding.setIsPayment(false);

//            if (binding.getIsPayment() && binding.getIsAddress())
                if (binding.getIsPayment())
                    showHidePayButton(true);
                else
                    showHidePayButton(false);
            }
        }
    }

    private void coinCheckUncheckProcess() {
        if (binding.cbCoin.isChecked()) {
            binding.txtPayCoinAmount.setVisibility(View.VISIBLE);
            if (coinBalance > orderDetails.getOrderTotal()) {
                if (binding.cbWallet.isChecked()) {
                    payableAmountByWallet = 0;
                    binding.txtPayWalletAmount.setVisibility(View.VISIBLE);
                    binding.txtPayWalletAmount.setText(getString(R.string.Rs) + new DecimalFormat("0.00").format(payableAmountByWallet));
                    payableAmountByCoin = orderDetails.getOrderTotal();
                    remainingAmount = 0;
                    binding.txtPayCoinAmount.setText(getString(R.string.Rs) + new DecimalFormat("0.00").format(payableAmountByCoin));
                    binding.llPaymentType.setVisibility(View.GONE);
                    binding.setIsPayment(true);
                    showHidePayButton(true);
                } else {
                    payableAmountByCoin = orderDetails.getOrderTotal();
                    remainingAmount = 0;
                    binding.txtPayCoinAmount.setText(getString(R.string.Rs) + new DecimalFormat("0.00").format(payableAmountByCoin));
                    binding.llPaymentType.setVisibility(View.GONE);
                    binding.setIsPayment(true);
                    showHidePayButton(true);
                }
            } else {
                if (binding.cbWallet.isChecked()) {
                    payableAmountByCoin = coinBalance;
                    remainingAmount = orderDetails.getOrderTotal() - coinBalance;
                    if (walletBalance >= remainingAmount) {
                        payableAmountByWallet = remainingAmount;
                        remainingAmount = 0;
                        binding.txtPayCoinAmount.setVisibility(View.VISIBLE);
                        binding.txtPayCoinAmount.setText(getString(R.string.Rs) + new DecimalFormat("0.00").format(payableAmountByCoin));
                        binding.txtPayWalletAmount.setText(getString(R.string.Rs) + new DecimalFormat("0.00").format(payableAmountByWallet));
                        binding.llPaymentType.setVisibility(View.GONE);
                        binding.setIsPayment(true);
//                if (binding.getIsAddress())
                        showHidePayButton(true);
                    } else {
                        payableAmountByWallet = walletBalance;
                        remainingAmount = remainingAmount - walletBalance;
//                     remainingAmount = orderDetails.getOrderTotal() - walletBalance;
                        binding.txtPayCoinAmount.setVisibility(View.VISIBLE);
                        binding.txtPayCoinAmount.setText(getString(R.string.Rs) + new DecimalFormat("0.00").format(payableAmountByCoin));
                        binding.llPaymentType.setVisibility(View.VISIBLE);
                        if (paymentType != null)
                            binding.setIsPayment(true);
                        else
                            binding.setIsPayment(false);
                        if (binding.getIsPayment() && binding.getIsAddress())
                            showHidePayButton(true);
                        else
                            showHidePayButton(true);
                    }
                } else {
                    payableAmountByCoin = coinBalance;
                    remainingAmount = orderDetails.getOrderTotal() - coinBalance;
                    binding.txtPayCoinAmount.setText(getString(R.string.Rs) + new DecimalFormat("0.00").format(payableAmountByCoin));
                    binding.llPaymentType.setVisibility(View.VISIBLE);

                    if (paymentType != null)
                        binding.setIsPayment(true);
                    else
                        binding.setIsPayment(false);

                    if (binding.getIsPayment() && binding.getIsAddress())
                        showHidePayButton(true);
                    else
//                    showHidePayButton(false);
                        showHidePayButton(true);
                }
            }
        } else {
            payableAmountByCoin = 0;
            binding.txtPayCoinAmount.setVisibility(View.GONE);
            if (binding.cbWallet.isChecked()) {
                if (walletBalance >= orderDetails.getOrderTotal()) {
                    binding.llPaymentType.setVisibility(View.GONE);
                    payableAmountByWallet = orderDetails.getOrderTotal();
                    remainingAmount = 0;
                    binding.txtPayWalletAmount.setText(getString(R.string.Rs) + new DecimalFormat("0.00").format(payableAmountByWallet));
                    binding.llPaymentType.setVisibility(View.VISIBLE);
                    binding.setIsPayment(true);
//                if (binding.getIsAddress())
                    showHidePayButton(true);
                } else {
                    payableAmountByWallet = walletBalance;
                    remainingAmount = remainingAmount - walletBalance;
//                     remainingAmount = orderDetails.getOrderTotal() - walletBalance;
                    binding.txtPayWalletAmount.setVisibility(View.VISIBLE);
                    binding.txtPayWalletAmount.setText(getString(R.string.Rs) + new DecimalFormat("0.00").format(payableAmountByWallet));
                    binding.llPaymentType.setVisibility(View.VISIBLE);
                    if (paymentType != null)
                        binding.setIsPayment(true);
                    else
                        binding.setIsPayment(false);
                    if (binding.getIsPayment() && binding.getIsAddress())
                        showHidePayButton(true);
                    else
                        showHidePayButton(true);
                }
            } else {
                payableAmountByCoin = 0;
                remainingAmount = orderDetails.getOrderTotal();
                binding.llPaymentType.setVisibility(View.VISIBLE);
                binding.txtPayCoinAmount.setVisibility(View.GONE);

                if (paymentType != null)
                    binding.setIsPayment(true);
                else
                    binding.setIsPayment(false);

//            if (binding.getIsPayment() && binding.getIsAddress())
                if (binding.getIsPayment())
                    showHidePayButton(true);
                else
                    showHidePayButton(false);
            }
        }
    }


    private void showHidePayButton(boolean isShow) {
        if (fromWhich.equals(Constants.ROOM_BOOKING)) {
            binding.btnDeliveryNow.setText(getString(R.string.pay_now));
        } else {
            if (remainingAmount > 0) {

                if (fromWhich.equals(getString(R.string.dine_in)) || fromWhich.equals(getString(R.string.take_away)) || fromWhich.equals(getString(R.string.cafe)))
                    binding.btnDeliveryNow.setText(getString(R.string.pay_securely) + " " + getString(R.string.Rs) + remainingAmount);
                else
                    binding.btnDeliveryNow.setText(getString(R.string.delivery_now));
            } else {
                if (fromWhich.equals(getString(R.string.dine_in)) || fromWhich.equals(getString(R.string.take_away)) || fromWhich.equals(getString(R.string.cafe)))
                    binding.btnDeliveryNow.setText(getString(R.string.pay_now));
                else
                    binding.btnDeliveryNow.setText(getString(R.string.delivery_now));
            }
            if (isShow) {
                binding.btnDeliveryNow.setVisibility(View.VISIBLE);
                if (fromWhich.equals(getString(R.string.delivery))) {
                    binding.btnDeliveryLatter.setVisibility(View.VISIBLE);
                }
            } else {
                binding.btnDeliveryNow.setVisibility(View.GONE);
                binding.btnDeliveryLatter.setVisibility(GONE);
            }
        }

    }

    /**
     * this method is for go to payment type screen
     *
     * @param view
     */
    public void goToPaymentTypeScreen(View view) {
        Intent intent = new Intent(mContext, PaymentTypeActi.class);
        intent.putExtra(Constants.FROM_WHICH, fromWhich);
        intent.putExtra(Constants.TOTAL_AMOUNT, remainingAmount);
        startActivityForResult(intent, PAYMENT_TYPE_REQUEST_CODE);
    }

    /**
     * Go To Pyament Screen for payment
     */
    public void goToPaymentScreen() {
        Intent intent = new Intent(mContext, PaymentActi.class);
        intent.putExtra(Constants.PAYMENT_MODE, paymentMode);
        intent.putExtra(Constants.PAYMENT_TYPE, paymentType);
        intent.putExtra(Constants.PAYMENT_REQUEST_ID, paymentRequestId);
        if (paymentType.equals(getString(R.string.netbanking)))
            intent.putExtra(Constants.BANK_BEAN, bankBean);
        else if (paymentType.equals(getString(R.string.credit_debit_atm_card))) {
            intent.putExtra(Constants.CARD_DATA, cardBean);
            intent.putExtra(Constants.IS_SAVED_CARD, false);
        }

        intent.putExtra(Constants.TOTAL_AMOUNT, remainingAmount);
        startActivityForResult(intent, PAYMENT_REQUEST_CODE);
    }

    /**
     * This method is for set all calculated prices, discounts etc...
     */
    private void setTotalPrice() {
        binding.txtRestBill.setText(getString(R.string.Rs) + " " + new DecimalFormat("0.00").format(orderDetails.getOrderSubTotal()));
        if ((orderDetails.getTaxAmount() - orderDetails.getTaxDiscountAmount()) > 0) {
            binding.llTax.setVisibility(View.VISIBLE);
            binding.txtTax.setText(getString(R.string.Rs) + " " + new DecimalFormat("0.00").format(orderDetails.getTaxAmount() - orderDetails.getTaxDiscountAmount()));
        } else
            binding.txtTax.setVisibility(View.GONE);

        if (orderDetails.getPackingCharges() > 0) {
            binding.llPackingCharges.setVisibility(View.VISIBLE);
            binding.txtPacking.setText(getString(R.string.Rs) + " " + new DecimalFormat("0.00").format(orderDetails.getPackingCharges()));
        } else
            binding.llPackingCharges.setVisibility(View.GONE);

        if (orderDetails.getDeliveryFee() > 0) {
            binding.llDeliveryCharges.setVisibility(View.VISIBLE);
            binding.txtDeliveryCharge.setText(getString(R.string.Rs) + " " + new DecimalFormat("0.00").format(orderDetails.getDeliveryFee()));
        } else
            binding.llDeliveryCharges.setVisibility(View.GONE);

        if (orderDetails.getLoyaltyAmount() > 0) {
            binding.llLoyaltyAmount.setVisibility(View.VISIBLE);
            binding.txtPayCoinLoyaltyAmount.setText(getString(R.string.Rs) + new DecimalFormat("0.00").format(orderDetails.getLoyaltyAmount()));
            binding.txtLoyaltyAmount.setText(getString(R.string.Rs) + " " + new DecimalFormat("0.00").format(orderDetails.getLoyaltyAmount()));
        } else {
            binding.txtPayCoinLoyaltyAmount.setText("");

            binding.llLoyaltyAmount.setVisibility(View.GONE);
        }
        if (orderDetails.getCouponDiscountAmount() > 0) {
            binding.llCoupon.setVisibility(View.VISIBLE);
            binding.txtCouponDis.setText(getString(R.string.Rs) + " " + new DecimalFormat("0.00").format(orderDetails.getCouponDiscountAmount()));
        } else
            binding.llCoupon.setVisibility(View.GONE);

        binding.txtTotal.setText(getString(R.string.Rs) + " " + new DecimalFormat("0.00").format(orderDetails.getOrderTotal()));
    }

    public void paymentProcess() {

        if (fromWhich.equalsIgnoreCase(getString(R.string.delivery))) {

            if (binding.getIsAddress()) {

                if (orderDetails.getOrderTotal() >= deliveryMinOrderValue)
                    checkIsOfferApiCall();
                else {
                    Utils.showCenterToast(mContext, getString(R.string.order_must_not_be_less_then) + " " + deliveryMinOrderValue);
                }
            } else {
                Toast.makeText(mContext, getString(R.string.please_delivery_add_address) + "", Toast.LENGTH_SHORT).show();
            }

        } else if (fromWhich.equalsIgnoreCase(getString(R.string.take_away))) {
            if (orderDetails.getOrderTotal() >= takeawayMinOrderValue)
                checkIsOfferApiCall();
            else
                Utils.showCenterToast(mContext, getString(R.string.order_must_not_be_less_then) + " " + takeawayMinOrderValue);
        } else
            checkIsOfferApiCall();

//        Utils.startActivity(mContext, TrackActi.class);
    }

    private void checkIsOfferApiCall() {
        if (deliveryOfferBean != null && !TextUtils.isEmpty(deliveryOfferBean.getOrderType()) && deliveryOfferBean.getOrderType().equalsIgnoreCase(fromWhich) && ((Calendar.getInstance().getTimeInMillis() - deliveryOfferBean.getLastRequestTime()) / 60000) < 60) {
            placeOrderProcess();
        } else {
            SharedPref.setDeliveryOfferJson(mContext, "");
//            callGetDeliveryOfferApi();
            placeOrderProcess();
        }
    }

    private void placeOrderProcess() {
        if (binding.getIsAddress()) {
            PlaceOrderBean bean = new PlaceOrderBean();
            bean.setSpecialInstruction(binding.etInstruction.getText().toString());
            if (!TextUtils.isEmpty(paymentType) && paymentType.equals(getString(R.string.cash_on_delivery))) {
                bean.setPaymentType("Wallet");
                bean.setPaidByWallet((long) payableAmountByWallet);
                bean.setPaidByCoin((long) orderDetails.getLoyaltyAmount());
                bean.setPaidOnDelivery((long) remainingAmount);
            } else {
                bean.setPaymentType("Wallet");
                bean.setPaidByWallet((long) orderDetails.getOrderTotal());
                bean.setPaidByCoin((long) orderDetails.getLoyaltyAmount());
                bean.setPaidOnDelivery(0);
            }
//            if (!TextUtils.isEmpty(paymentType) && paymentType.equals(Constants.BILL_TO_COMPANY)) {
            if (!TextUtils.isEmpty(paymentType) && paymentType.equalsIgnoreCase(Constants.BILL_TO_COMPANY)) {
                bean.setPaymentType("BillToCompany");
                bean.setPaidByWallet(0);
                bean.setPaidByCoin(0);
                bean.setPaidOnDelivery(0);
                bean.setBillToCompany(true);
            } else {
                bean.setBillToCompany(false);
            }

            if (deliveryOfferBean != null && !TextUtils.isEmpty(deliveryOfferBean.getOfferId())) {
                bean.setDeliveryOfferApplicable(true);
                bean.setDeliveryOfferId(deliveryOfferBean.getOfferId());
            }
            if (fromWhich.equals(Constants.ROOM_BOOKING)) {
                bean.setBookingReferenceId(orderId);
            }


            if (deliveryLaterFlag == true)
                bean.setDeliveryLaterTimeStamp(finalToSend.getTimeInMillis());
            cleverTapBean = bean;
            placeOrderJson = new Gson().toJson(bean);
            if (remainingAmount == 0 || paymentType.equalsIgnoreCase(Constants.BILL_TO_COMPANY)) {
                if (fromWhich.equals(Constants.ROOM_BOOKING)) {
                    callRoomPlaceApi();
                } else {
                    callPlaceOrderApi();
                }
            } else {
                setLastPaymentMode();
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == ADDRESS_REQUEST_CODE && resultCode == RESULT_OK) {
            if (data != null) {
                addressBean = (AddressBean) data.getSerializableExtra(Constants.ADDRESS_BEAN);

                binding.txtDeliveryAdd.setText(addressBean.getAddressLine1());
                Log.e("address", addressBean.getAddressLine1());
                binding.llAddress.setVisibility(View.VISIBLE);
                binding.txtAddAddress.setVisibility(View.GONE);

                Utils.progressDialog(mContext, "");
                callUpdateAddressApi();
            }

//        } else if (requestCode == PayUmoneyFlowManager.REQUEST_CODE_PAYMENT && resultCode == RESULT_OK && data != null) {
//            TransactionResponse transactionResponse = data.getParcelableExtra(PayUmoneyFlowManager.INTENT_EXTRA_TRANSACTION_RESPONSE);
//
//            ResultModel resultModel = data.getParcelableExtra(PayUmoneyFlowManager.ARG_RESULT);
//
//            if (transactionResponse != null && transactionResponse.getPayuResponse() != null) {
//
//                // Response from Payumoney
//                String payuResponse = transactionResponse.getPayuResponse();
//
//                // Response from SURl and FURL
//                String merchantResponse = transactionResponse.getTransactionDetails();
//
//                if (transactionResponse.getTransactionStatus().equals(TransactionResponse.TransactionStatus.SUCCESSFUL)) {
//                    //Success Transaction
////                    goToTrackingActi(orderId);
//                      Utils.progressDialog(mContext,"");
//                    CountDownTimer countDownTimer = new CountDownTimer(60000, 1000) {
//                        @Override
//                        public void onTick(long millisUntilFinished) {
//
//                        }
//
//                        @Override
//                        public void onFinish() {
//                            Utils.dismissProgressDialog();
//                            callUpdatePaymentResuestApi("PayUMoney");
//                        }
//
//                    }.start();
//
//
//                } else {
//                    Utils.dismissProgressDialog();
//                    customCleverPaymentFailedEvent("PayUMoney", payuResponse);
//
//                }
//            }
        } /*else if (requestCode == CFPaymentService.REQ_CODE) {
            boolean isPayment = false;
            if (data != null) {
                Bundle bundle = data.getExtras();
                if (bundle != null)
                    for (String key : bundle.keySet()) {
                        if (bundle.getString(key) != null && bundle.getString("orderId") != null && bundle.getString("orderAmount") != null
                                && bundle.getString("referenceId") != null && bundle.getString("txStatus") != null) {
                            cashFreeResponse.setOrderId(bundle.getString("orderId"));
                            cashFreeResponse.setOrderAmount(Double.parseDouble(bundle.getString("orderAmount")));
                            cashFreeResponse.setReferenceId(bundle.getString("referenceId"));
                            cashFreeResponse.setTxStatus(bundle.getString("txStatus"));
                            cashFreeResponse.setTxMsg(bundle.getString("txMsg"));
                            Log.d("key", key + " : " + bundle.getString(key) + "  == " + cashFreeResponse);
                            isPayment = true;
                        }
                    }
                if (isPayment) {
                    callUpdatePaymentRequestCashFreeApi();
                }
            }
        } */ else if (requestCode == DELIVERY_REQUEST_CODE && resultCode == RESULT_OK) {
            if (data != null) {
                deliveryTime = data.getLongExtra(Constants.DELIVERY_TIME, 0);
                Toast.makeText(mContext, deliveryTime + "", Toast.LENGTH_SHORT).show();
//                placeOrderProcess(deliveryTime);
//                orderType = "Schedule";
//                startProgress();
            }
        } else if (requestCode == REQUEST_COUPON && resultCode == RESULT_OK) {
            isCouponApplied = true;
//            if(binding.cbCoinLoyalty.isChecked())
//                callApplyLoyaltyCoinApi();
//            else
            if (fromWhich.equals(Constants.ROOM_BOOKING)) {
                callRoomDetailsApi(true);
            } else {
                callGetOrderDetailsApi(true);
            }
        } else if (requestCode == PAYMENT_TYPE_REQUEST_CODE && resultCode == RESULT_OK && data != null) {
            paymentMode = data.getStringExtra(Constants.PAYMENT_MODE);
            paymentType = data.getStringExtra(Constants.PAYMENT_TYPE);
            isSavedCard = data.getBooleanExtra(Constants.IS_SAVED_CARD, false);
            bankBean = null;
            savedCardBean = null;
            cardBean = null;
            if (paymentType.equals(getString(R.string.netbanking)))
                bankBean = (BankBean) data.getSerializableExtra(Constants.BANK_BEAN);
            else if (paymentType.equals(getString(R.string.credit_debit_atm_card))) {
                if (isSavedCard) {
                    savedCardBean = (SavedCardBean) data.getSerializableExtra(Constants.SAVED_CARD_DATA);
                } else {
                    cardBean = (CardBean) data.getSerializableExtra(Constants.CARD_DATA);
                }
            }

            setPaymentType();
        } else if (requestCode == PAYMENT_REQUEST_CODE && resultCode == RESULT_OK && data != null) {
            transactionId = data.getStringExtra(Constants.TRANSACTION_ID);
            nickName = data.getStringExtra(Constants.NICK_NAME);
            if (orderId.equals(""))
                orderId = data.getStringExtra(Constants.ORDER_ID);

            callUpdatePaymentResuestApi("Razorpay");
        } else if (requestCode == SODEXO_PAYMENT_REQUEST_CODE && resultCode == RESULT_OK) {
//            callGetTransactionDetail();
//            callUpdatePaymentResuestApi("Razorpay");
            callUpdatePaymentRequestForSodexoApi("Sodexo");
        }
    }

    private void setPaymentType() {
        if (paymentType.equalsIgnoreCase(Constants.BILL_TO_COMPANY)) {
            binding.txtPaymentType.setText(getString(R.string.bill_to_company));
        } else
            binding.txtPaymentType.setText(paymentType);

        LastPaymentModeBean lastPaymentModeBean = new LastPaymentModeBean();
        lastPaymentModeBean.setLastPaymentType(paymentType);
        lastPaymentModeBean.setLastPaymentMode(paymentMode);
        lastPaymentModeBean.setSavedCard(savedCardBean);
        lastPaymentModeBean.setBankBean(bankBean);
        lastPaymentModeBean.setCardBean(cardBean);
        binding.setIsPayment(true);
//        if (binding.getIsAddress())
        binding.setIsPlace(true);
        setPaymentData(lastPaymentModeBean);

//        showHidePayButton(true);
    }

    private void setPaymentData(LastPaymentModeBean bean) {
        paymentType = bean.getLastPaymentType();
        paymentMode = bean.getLastPaymentMode();

//        Razorpay razorpay = new Razorpay(this);


        if (paymentMode.equals(getString(R.string.cash))) {
            if (paymentType.equals(Constants.BILL_TO_COMPANY)) {
                binding.txtPaymentType.setText(getString(R.string.bill_to_company));
                binding.imvPaymentType.setImageResource(R.drawable.ic_cashondelivery);
            } else {
                binding.imvPaymentType.setImageResource(R.drawable.ic_cashondelivery);
                binding.txtPaymentType.setText(paymentType);
            }
        } else if (paymentMode.equals(getString(R.string.bill_to_company))) {
            binding.imvPaymentType.setImageResource(R.drawable.ic_cashondelivery);
//            binding.txtPaymentType.setText(paymentType);
            binding.txtPaymentType.setText(getString(R.string.bill_to_company));
        } else if (paymentMode.equals(getString(R.string.wallet))) {
            binding.txtPaymentType.setText(paymentType);
            binding.txtPaymentMode.setText(getString(R.string.payment_mode));
            if (paymentType.equals(getString(R.string.paytm))) {
//                Utils.setImage(mContext, binding.imvPaymentType, razorpay.getWalletSqLogoUrl("paytm"));
            } else if (paymentType.equals(getString(R.string.phone_pe))) {
//                Utils.setImage(mContext, binding.imvPaymentType, razorpay.getWalletSqLogoUrl("phonepe"));
            } else if (paymentType.equals(getString(R.string.ola_money))) {
//                Utils.setImage(mContext, binding.imvPaymentType, razorpay.getWalletSqLogoUrl("olamoney"));
            } else if (paymentType.equals(getString(R.string.pay_z_app))) {
//                Utils.setImage(mContext, binding.imvPaymentType, razorpay.getWalletSqLogoUrl("payzapp"));
            } else if (paymentType.equals(getString(R.string.freecharge))) {
//                Utils.setImage(mContext, binding.imvPaymentType, razorpay.getWalletSqLogoUrl("freecharge"));
            }
        } else if (paymentMode.equals(getString(R.string.online))) {
            if (paymentType.equals(getString(R.string.netbanking))) {
                binding.imvPaymentType.setImageResource(R.drawable.ic_netbanking);
                if (bean.getBankBean() != null) {
                    bankBean = bean.getBankBean();
                    binding.txtPaymentType.setText(bean.getBankBean().getBankName());
                    binding.txtPaymentMode.setText(getString(R.string.netbanking));
                } else {
                    binding.txtPaymentMode.setText(getString(R.string.payment_mode));
                    binding.txtPaymentType.setText(getString(R.string.netbanking));
                }
            } else if (paymentType.equals(getString(R.string.credit_debit_atm_card))) {
                if (bean.getSavedCard() != null) {
                    savedCardBean = bean.getSavedCard();
                    isSavedCard = true;
                    binding.txtPaymentType.setText("xxxx xxxx xxxx " + bean.getSavedCard().getLast4());
                    binding.txtPaymentMode.setText(bean.getSavedCard().getNickName());

                    if (bean.getSavedCard().getNetwork().equalsIgnoreCase("visa")) {
                        binding.imvPaymentType.setImageResource(R.drawable.ic_visa);
                    } else if (bean.getSavedCard().getNetwork().equalsIgnoreCase("mastercard")) {
                        binding.imvPaymentType.setImageResource(R.drawable.ic_master_card);
                    } else if (bean.getSavedCard().getNetwork().equalsIgnoreCase("diners")) {
                        binding.imvPaymentType.setImageResource(R.drawable.ic_diners_card);
                    } else if (bean.getSavedCard().getNetwork().equalsIgnoreCase("amex")) {
                        binding.imvPaymentType.setImageResource(R.drawable.ic_amex);
                    } else if (bean.getSavedCard().getNetwork().equalsIgnoreCase("jcb")) {
                        binding.imvPaymentType.setImageResource(R.drawable.ic_jcb);
                    } else {
                        binding.imvPaymentType.setImageResource(R.drawable.ic_card);
                    }
                } else if (bean.getCardBean() != null) {
                    cardBean = bean.getCardBean();
                    isSavedCard = false;
                    String cardNo = bean.getCardBean().getCardNo().replace(bean.getCardBean().getCardNo().substring(0, 15), "xxxx xxxx xxxx ");
                    binding.txtPaymentType.setText(cardNo);
                    binding.txtPaymentMode.setText(bean.getCardBean().getNickName());

                    if (bean.getCardBean().getNetwork().equalsIgnoreCase("visa")) {
                        binding.imvPaymentType.setImageResource(R.drawable.ic_visa);
                    } else if (bean.getCardBean().getNetwork().equalsIgnoreCase("mastercard")) {
                        binding.imvPaymentType.setImageResource(R.drawable.ic_master_card);
                    } else if (bean.getCardBean().getNetwork().equalsIgnoreCase("diners")) {
                        binding.imvPaymentType.setImageResource(R.drawable.ic_diners_card);
                    } else if (bean.getCardBean().getNetwork().equalsIgnoreCase("amex")) {
                        binding.imvPaymentType.setImageResource(R.drawable.ic_amex);
                    } else if (bean.getCardBean().getNetwork().equalsIgnoreCase("jcb")) {
                        binding.imvPaymentType.setImageResource(R.drawable.ic_jcb);
                    } else {
                        binding.imvPaymentType.setImageResource(R.drawable.ic_card);

                    }
                } else {
                    binding.imvPaymentType.setImageResource((R.drawable.ic_card));
                    binding.txtPaymentType.setText(getString(R.string.credit_debit_atm_card));
                    binding.txtPaymentMode.setText(getString(R.string.payment_mode));
                }
            } else {
                binding.txtPaymentType.setText(paymentType);
                binding.txtPaymentMode.setText(getString(R.string.payment_mode));
                if (paymentType.equals(getString(R.string.paytm))) {
//                    Utils.setImage(mContext, binding.imvPaymentType, razorpay.getWalletSqLogoUrl("paytm"));
                } else if (paymentType.equals(getString(R.string.phone_pe))) {
//                    Utils.setImage(mContext, binding.imvPaymentType, razorpay.getWalletSqLogoUrl("phonepe"));
                } else if (paymentType.equals(getString(R.string.pay_u_money))) {
//                    Utils.setImage(mContext, binding.imvPaymentType, razorpay.getWalletSqLogoUrl("PayUmoney"));
                } else if (paymentType.equals(getString(R.string.ola_money))) {
//                    Utils.setImage(mContext, binding.imvPaymentType, razorpay.getWalletSqLogoUrl("olamoney"));
                } else if (paymentType.equals(getString(R.string.pay_z_app))) {
//                    Utils.setImage(mContext, binding.imvPaymentType, razorpay.getWalletSqLogoUrl("payzapp"));
                } else if (paymentType.equals(getString(R.string.freecharge))) {
//                    Utils.setImage(mContext, binding.imvPaymentType, razorpay.getWalletSqLogoUrl("freecharge"));
                }
            }
        }

        if (orderDetails != null)
            setWalletAmount();
        else {
            binding.llPaymentType.setVisibility(View.VISIBLE);
            binding.setIsPayment(true);
        }

    }

    /**
     * show popup when we comes from take_away
     */
    private void showSuccessPopUp(String message) {
        Utils.showSuccessPopUp(mContext, message, new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Utils.dismissSuccessAlert();
                if (fromWhich.equalsIgnoreCase(getString(R.string.take_away))) {
                    Intent intent = new Intent(mContext, Dashboard.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(intent);
                    finish();
                } else if (fromWhich.equals(getString(R.string.dine_in)) || fromWhich.equals(getString(R.string.cafe))) {
                    Intent intent = new Intent(mContext, LiveOrderActi.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(intent);
//                    finish();
//                    Intent intent = new Intent();
//                    setResult(RESULT_OK, intent);
//                    finish();
                }
            }
        });

    }

    private void showDiscountOfferPopup(boolean showAddMore, String message) {
        alert = new Dialog(mContext);
        alert.requestWindowFeature(Window.FEATURE_NO_TITLE);
        alert.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        PopupDiscounOfferBinding discountBinding = DataBindingUtil.inflate(LayoutInflater.from(mContext), R.layout.popup_discoun_offer, null, false);
        alert.setContentView(discountBinding.getRoot());


        Glide.with(this).asGif().load(R.raw.offers).into(discountBinding.imvOffer);

        int resID = getResources().getIdentifier("offers_tone", "raw", getPackageName());
        mediaPlayer = MediaPlayer.create(this, resID);
        mediaPlayer.start();

        discountBinding.txtDiscountMsg.setText(message);


        if (showAddMore) {
            discountBinding.btnSkip.setVisibility(View.VISIBLE);

            discountBinding.btnSkip.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    alert.cancel();
                    placeOrderProcess();
                }
            });

            discountBinding.btnAddMore.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    alert.cancel();
                    finish();
                }
            });
        } else {
            discountBinding.btnSkip.setVisibility(View.GONE);
            discountBinding.btnAddMore.setText(getString(R.string.Ok));

            discountBinding.btnAddMore.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    alert.cancel();
                }
            });
        }


        alert.setCancelable(false);
        alert.show();

    }

    /**
     * This method check and remove data from local database
     *
     * @param position = item position
     * @param type     = Which operation we are performing
     */
    private void updateProcess(int position, String type) {
        int qty = itemList.get(position).getQuantity();
        if (type.equalsIgnoreCase("LESS")) {
            if (itemList.get(position).getCustomization() != null) {
                db.checkUpdateQty(itemList.get(position).getItemId(), --qty, itemList.get(position).getCustomization());
            } else {
                if (qty == 1)
                    db.deleteItem(itemList.get(position).getItemId());
                else
                    db.updateItemQty(--qty, itemList.get(position).getItemId());
            }
        } else {
            if (itemList.get(position).getCustomization() != null) {
                db.checkUpdateQty(itemList.get(position).getItemId(), ++qty, itemList.get(position).getCustomization());
            } else {
                db.updateItemQty(++qty, itemList.get(position).getItemId());
            }
        }
        callGetOrderDetailsApi(false);
    }

    public void goToCvvScreen(String placeorderjson) {
        Intent intent = new Intent(mContext, CvvActi.class);

        intent.putExtra(Constants.PAYMENT_MODE, paymentMode);
        intent.putExtra(Constants.PAYMENT_TYPE, paymentType);

        intent.putExtra(Constants.SAVED_CARD_DATA, savedCardBean);
//        intent.putExtra(Constants.TOTAL_AMOUNT, totalPrice);
        intent.putExtra(Constants.TOTAL_AMOUNT, remainingAmount);
        intent.putExtra(Constants.PLACE_ORDER_JSON, placeorderjson);
        intent.putExtra(Constants.IS_SAVED_CARD, true);
        intent.putExtra(Constants.PAYMENT_REQUEST_ID, paymentRequestId);
        startActivityForResult(intent, PAYMENT_REQUEST_CODE);
    }

    /**
     * This method is used  to go to Tracking sceen and clear all the screens
     *
     * @param orderId = generated orderId
     */
    private void goToTrackingActi(String orderId) {
        Intent intent = new Intent(mContext, TrackActi.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        intent.putExtra(Constants.ORDER_ID, orderId);
        intent.putExtra(Constants.IS_FROM_CART, true);
        startActivity(intent);
        finish();
    }

    /**
     * Paytm Payment process
     */

//    private void paytmPaymentProcess() {
//        if (checkSumBean != null) {
//            Map<String, String> paramMap = new HashMap<String, String>();
//            paramMap.put("MID", checkSumBean.getMid());
//            // Key in your staging and production MID available in your dashboard
//            paramMap.put("ORDER_ID", paymentRequestId);
//            paramMap.put("CUST_ID", String.valueOf(checkSumBean.getCustId()).replace("-", ""));
//            paramMap.put("MOBILE_NO", checkSumBean.getPhone());
//            paramMap.put("EMAIL", checkSumBean.getEmail());
//            paramMap.put("CHANNEL_ID", checkSumBean.getChannelId());
//            paramMap.put("TXN_AMOUNT", checkSumBean.getTxnAmount());
//            paramMap.put("WEBSITE", checkSumBean.getWebsite());
//            paramMap.put("MERC_UNQ_REF", checkSumBean.getMercunqref());
//            // This is the staging value. Production value is available in your dashboard
//            paramMap.put("INDUSTRY_TYPE_ID", checkSumBean.getIndustryTypeId());
//            // This is the staging value. Production value is available in your dashboard
//            paramMap.put("CALLBACK_URL", checkSumBean.getCallbackUrl());
//            paramMap.put("CHECKSUMHASH", checkSumBean.getChecksumhash());
//
//            PaytmOrder Order = new PaytmOrder((HashMap<String, String>) paramMap);
//
////            PaytmPGService Service = PaytmPGService.getStagingService();
//            PaytmPGService Service = PaytmPGService.getProductionService();
//
//            Service.initialize(Order, null);
//
//            Service.startPaymentTransaction(this, true, true, new PaytmPaymentTransactionCallback() {
//                /*Call Backs*/
//                public void someUIErrorOccurred(String inErrorMessage) {
//                    Toast.makeText(getApplicationContext(), "UI Error " + inErrorMessage, Toast.LENGTH_LONG).show();
//                }
//
//                public void onTransactionResponse(Bundle inResponse) {
//                    if (inResponse.getString("STATUS").equals("TXN_SUCCESS")) {
//                        getDataFromPaytm(inResponse);
//                        Toast.makeText(getApplicationContext(), "Payment Successful ", Toast.LENGTH_LONG).show();
//                    } else {
//                        Toast.makeText(getApplicationContext(), "Payment Failed ", Toast.LENGTH_LONG).show();
//                    }
//                }
//
//                public void networkNotAvailable() {
//                    Toast.makeText(getApplicationContext(), "Network connection error: Check your internet connectivity", Toast.LENGTH_LONG).show();
//                }
//
//                public void clientAuthenticationFailed(String inErrorMessage) {
//                    Toast.makeText(getApplicationContext(), "Authentication failed: Server error" + inErrorMessage, Toast.LENGTH_LONG).show();
//                }
//
//                public void onErrorLoadingWebPage(int iniErrorCode, String inErrorMessage, String inFailingUrl) {
//                    Toast.makeText(getApplicationContext(), "Unable to load webpage " + inErrorMessage, Toast.LENGTH_LONG).show();
//                }
//
//                public void onBackPressedCancelTransaction() {
//                    Toast.makeText(getApplicationContext(), "Transaction cancelled", Toast.LENGTH_LONG).show();
////                    callCancelTransactionApi();
//                }
//
//                public void onTransactionCancel(String inErrorMessage, Bundle inResponse) {
//                    Toast.makeText(getApplicationContext(), "Transaction Cancelled", Toast.LENGTH_LONG).show();
//                }
//            });
//        }
//    }
    private void getDataFromPaytm(Bundle inResponse) {
        beanPaytmTxnResponse = new BeanPaytmTxnResponse();

        beanPaytmTxnResponse.setSTATUS("STATUS");
        beanPaytmTxnResponse.setCHECKSUMHASH(inResponse.getString("CHECKSUMHASH"));
        beanPaytmTxnResponse.setBANKNAME(inResponse.getString("BANKNAME"));
        beanPaytmTxnResponse.setORDERID(inResponse.getString("ORDERID"));
        beanPaytmTxnResponse.setTXNAMOUNT(inResponse.getString("TXNAMOUNT"));
        beanPaytmTxnResponse.setTXNDATE(inResponse.getString("TXNDATE"));
        beanPaytmTxnResponse.setMID(inResponse.getString("MID"));
        beanPaytmTxnResponse.setTXNID(inResponse.getString("TXNID"));
        beanPaytmTxnResponse.setRESPCODE(inResponse.getString("RESPCODE"));
        beanPaytmTxnResponse.setPAYMENTMODE(inResponse.getString("PAYMENTMODE"));
        beanPaytmTxnResponse.setBANKTXNID(inResponse.getString("BANKTXNID"));
        beanPaytmTxnResponse.setCURRENCY(inResponse.getString("CURRENCY"));
        beanPaytmTxnResponse.setGATEWAYNAME(inResponse.getString("GATEWAYNAME"));
        beanPaytmTxnResponse.setRESPMSG(inResponse.getString("RESPMSG"));

        PayOrderBean bean = new PayOrderBean();
        bean.setOrderId(paymentRequestId);
//        bean.setUserId(Long.parseLong(SharedPref.getUserId(mContext)));
        bean.setTxnResponse(beanPaytmTxnResponse);

//        callPayOrderByPaytm(bean);
        callUpdatePaymentResuestApi("Paytm");

    }

    /*
    this code uer for delivery later for selecet data and time
    */

    public void selectDate() {
        // Get Current Date
        Date today = new Date();
        final Calendar c = Calendar.getInstance();
        mYear = c.get(Calendar.YEAR);
        mMonth = c.get(Calendar.MONTH);
        mDay = c.get(Calendar.DAY_OF_MONTH);

        c.setTime(today);
//        c.add(Calendar.YEAR, - 13);
//        long maxDate = c.getTime().getTime();
//        c.add(Calendar.YEAR, - 45);
        long minDate = c.getTime().getTime();


        DatePickerDialog datePickerDialog = new DatePickerDialog(this, R.style.DatePickerDialogTheme,
                new DatePickerDialog.OnDateSetListener() {

                    @Override
                    public void onDateSet(DatePicker view, int year,
                                          int monthOfYear, int dayOfMonth) {

                        String date = String.format("%02d-%02d-%01d", dayOfMonth, monthOfYear + 1, year);

                        Calendar cal = Calendar.getInstance();
                        cal.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                        cal.set(Calendar.MONTH, monthOfYear);
                        cal.set(Calendar.YEAR, year);

                        finalToSend.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                        finalToSend.set(Calendar.MONTH, monthOfYear);
                        finalToSend.set(Calendar.YEAR, year);

                        selectTime();

//                        binding.tvDate.setText(date);
                        if (mDay == cal.get(Calendar.DAY_OF_MONTH)) ;
//                            binding.tvTime.setText("");

                    }
                }, mYear, mMonth, mDay);
//        datePickerDialog.getDatePicker().setMaxDate(System.currentTimeMillis() - 568025136000L);
        datePickerDialog.getDatePicker().setMinDate(minDate);
        datePickerDialog.show();
    }

    public void selectTime() {
        final Calendar c = Calendar.getInstance();
        int mHour = c.get(Calendar.HOUR_OF_DAY);
        int mMinute = c.get(Calendar.MINUTE);
        timePickerDialog = new RangeTimePickerDialog(mContext, 2, new TimePickerDialog.OnTimeSetListener() {
            @Override
            public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                selectedHour = hourOfDay;
                selectedMinute = minute;
                dateTimeCalender.set(Calendar.HOUR_OF_DAY, hourOfDay);
                dateTimeCalender.set(Calendar.MINUTE, minute);

                finalToSend.set(Calendar.HOUR_OF_DAY, hourOfDay);
                finalToSend.set(Calendar.MINUTE, minute);
                Log.d("show time", String.valueOf(finalToSend.getTimeInMillis()));

                startProgress();

                if (hourOfDay == 0) {
//                    binding.tvTime.setText(12 + ":" + minute + " AM");
                } else if (hourOfDay == 12) {
//                    binding.tvTime.setText(12 + ":" + minute + " PM");
                } else if (hourOfDay > 12) {
//                    binding.tvTime.setText(new DecimalFormat("00").format(hourOfDay -12) + ":" + minute + " PM");
                } else {
//                    binding.tvTime.setText(new DecimalFormat("00").format(hourOfDay) + ":" + minute + " AM");
                }

            }
        }, mHour, mMinute, false);


        if (finalToSend.get(Calendar.DAY_OF_MONTH) == c.get(Calendar.DAY_OF_MONTH))
            timePickerDialog.setMin(mHour, mMinute);
        timePickerDialog.show();
    }

    private void startProgress() {
        binding.llProgress.setVisibility(View.VISIBLE);
        binding.llbtnOrder.setVisibility(View.GONE);
        ProgressBarAnimation anim = new ProgressBarAnimation(binding.sbOrder, 0, 100);
        anim.setDuration(8000);
        binding.sbOrder.startAnimation(anim);
        binding.llProgress.setVisibility(View.VISIBLE);
        binding.llbtnOrder.setVisibility(View.GONE);

    }

    public void closeProgress(View view) {
        binding.llProgress.setVisibility(View.GONE);
        binding.llbtnOrder.setVisibility(View.VISIBLE);
        deliveryLaterFlag = false;
//        binding.nsvData.fullScroll(1);
    }

    public void callGetBalanceApi(boolean isProgress) {

        try {
            new NetworkManager(WalletBean.class, new NetworkManager.OnCallback<WalletBean>() {
                @Override
                public void onSuccess(boolean success, ResponseClass responseClass, int which) {
                    if (responseClass.isSuccess()) {
                        callPaymentTypeApi();
                        WalletBean walletBean = (WalletBean) responseClass.getResponsePacket();
                        walletBalance = (double) walletBean.getWalletBalance();
                        coinBalance = (double) walletBean.getCoinBalance();
//                            if(coinBalance > 0){
//                                callApplyLoyaltyCoinApi();
//                            }else {
                        callGetPaymentModeApi();
//                            }
                    } else
                        callGetPaymentModeApi();
                }

                @Override
                public void onFailure(boolean success, String response, int which) {
                    Utils.dismissProgressDialog();
                    Utils.showCenterToast(mContext, "Check your Internet Connection");
                }
            }).callAPIJson(mContext, Constants.VAL_GET, AppUrls.GET_COIN_AND_WALLET_BALANCE, "", "Loading...", isProgress, AppUrls.REQUESTTAG_GETCOINANDWALLETBALANCE);

        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
    }

    private void callGetOrderDetailsApi(final boolean showLoading) {

        try {
            new NetworkManager(OrderDetailsBean.class, new NetworkManager.OnCallback<OrderDetailsBean>() {
                @Override
                public void onSuccess(boolean success, ResponseClass responseClass, int which) {
                    if (!showLoading)
                        Utils.dismissProgressDialog();
                    if (responseClass.isSuccess()) {
                        orderDetails = (OrderDetailsBean) responseClass.getResponsePacket();
                        setData();

                    } else {
                        Utils.showCenterToast(mContext, responseClass.getMessage());
                    }
                }

                @Override
                public void onFailure(boolean success, String response, int which) {
                    if (!showLoading)
                        Utils.dismissProgressDialog();
                    Utils.showCenterToast(mContext, getString(R.string.internet_connection_not_found));
                }
            }).callAPIJson(mContext, Constants.VAL_GET, AppUrls.ORDER_DETAIL + orderId, "", "Loading...", showLoading, AppUrls.REQUESTTAG_ORDERDETAIL);

        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
    }

    private void callUpdateAddressApi() {

        PlaceOrderBean bean = new PlaceOrderBean();
        bean.setAddressId(addressBean.getRecordId());
        bean.setOrderReferenceId(orderId);

        String json = new Gson().toJson(bean);

        try {
            new NetworkManager(String.class, new NetworkManager.OnCallback<String>() {
                @Override
                public void onSuccess(boolean success, ResponseClass responseClass, int which) {
                    if (responseClass.isSuccess()) {
                        binding.setIsAddress(true);
                        if (binding.getIsPayment())
                            binding.setIsPlace(true);
//                        if(binding.cbCoinLoyalty.isChecked()){
//                            callApplyLoyaltyCoinApi();
//                        }else
                        callGetOrderDetailsApi(false);
                    } else {
                        Utils.dismissProgressDialog();
                        Utils.showCenterToast(mContext, responseClass.getMessage());
                    }
                }

                @Override
                public void onFailure(boolean success, String response, int which) {
                    Utils.dismissProgressDialog();
                    Utils.showCenterToast(mContext, getString(R.string.internet_connection_not_found));
                }
            }).callAPIJson(mContext, Constants.VAL_POST, AppUrls.UPDATE_ADDRESS, json, "Loading...", false, AppUrls.REQUESTTAG_UPDATEADDRESS);

        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
    }

    private void callUpdateItemApi(final int position, final String type) {
        try {
            new NetworkManager(String.class, new NetworkManager.OnCallback<String>() {
                @Override
                public void onSuccess(boolean success, ResponseClass responseClass, int which) {
                    if (responseClass.isSuccess()) {
                        updateProcess(position, type);
                    } else {
                        Utils.dismissProgressDialog();
                        Utils.showCenterToast(mContext, responseClass.getMessage());
                    }
                }

                @Override
                public void onFailure(boolean success, String response, int which) {
                    Utils.dismissProgressDialog();
                    Utils.showCenterToast(mContext, getString(R.string.internet_connection_not_found));
                }
            }).callAPIJson(mContext, Constants.VAL_GET, AppUrls.UPDATE_ORDER_ITEM_QUANTITY + orderId + "/" + itemList.get(position).getOrderItemId() + "/" + type, "", "Loading...", false, AppUrls.REQUESTTAG_UPDATEORDERITEMQUANTITY);

        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
    }

    private void callUpdateInstructionApi(final int itemPosition, String insturction) {

        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put(Constants.FLD_SPECIAL_INSTRUCTION, insturction);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        try {
            new NetworkManager(String.class, new NetworkManager.OnCallback<String>() {
                @Override
                public void onSuccess(boolean success, ResponseClass responseClass, int which) {
                    if (responseClass.isSuccess()) {
                        if (itemList.get(itemPosition).getCustomization() != null && itemList.get(itemPosition).getCustomization().size() > 0)
                            db.checkUpdateInstruction(itemList.get(itemPosition).getItemId(), insturction, itemList.get(itemPosition).getCustomization());
                        else
                            db.updateInstruction(insturction, itemList.get(itemPosition).getItemId());

                        itemList.get(itemPosition).setSpecialInstruction(insturction);
                        mAdapter.notifyDataSetChanged();

                        instructionAlert.dismiss();
                    } else {
                        Utils.showCenterToast(mContext, responseClass.getMessage());
                    }
                }

                @Override
                public void onFailure(boolean success, String response, int which) {
                    Utils.dismissProgressDialog();
                    Utils.showCenterToast(mContext, getString(R.string.internet_connection_not_found));
                }
            }).callAPIJson(mContext, Constants.VAL_POST, AppUrls.UPDATE_INSTRUCTION_ON_ORDER_ITEM + orderId + "/" + itemList.get(itemPosition).getOrderItemId(), jsonObject.toString(), "Loading...", true, AppUrls.REQUESTTAG_UPDATEINSTRUCTIONONORDERITEM);

        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
    }

    private void callRemoveCouponApi(String url) {
        try {
            new NetworkManager(String.class, new NetworkManager.OnCallback<String>() {
                @Override
                public void onSuccess(boolean success, ResponseClass responseClass, int which) {
                    if (responseClass.isSuccess()) {
                        isCouponApplied = false;
                        SharedPref.setCouponCode(mContext, "");
                        restoreCouponCode();
                        if (fromWhich.equals(Constants.ROOM_BOOKING)) {
                            callRoomDetailsApi(true);
                        } else
                            callGetOrderDetailsApi(false);
                    } else {
                        Utils.dismissProgressDialog();
                        Utils.showCenterToast(mContext, responseClass.getMessage());
                    }
                }

                @Override
                public void onFailure(boolean success, String response, int which) {
                    Utils.dismissProgressDialog();
                    Utils.showCenterToast(mContext, getString(R.string.internet_connection_not_found));
                }
            }).callAPIJson(mContext, Constants.VAL_GET, url + orderId, "", "Loading...", false, AppUrls.REQUESTTAG_REMOVECOUPON);

        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
    }

    private void callGetDeliveryOfferApi() {
        JSONObject jsonObject = new JSONObject();
        String orderType = "";
        if (fromWhich.endsWith(getString(R.string.take_away)))
            orderType = Constants.TAKE_AWAY;
        else if (fromWhich.endsWith(getString(R.string.delivery)))
            orderType = Constants.HOME_DELIVERY;
        else if (fromWhich.endsWith(getString(R.string.dine_in)))
            orderType = Constants.DINE_IN;
        try {
            jsonObject.put(Constants.ORDER_TYPE, orderType);
            jsonObject.put(Constants.ORDER_TOTAL, orderDetails.getOrderTotal());
        } catch (JSONException e) {
            e.printStackTrace();
        }

        try {
            new NetworkManager(DeliveryOfferBean.class, new NetworkManager.OnCallback<DeliveryOfferBean>() {
                @Override
                public void onSuccess(boolean success, ResponseClass responseClass, int which) {
                    if (responseClass.isSuccess()) {

                        DeliveryOfferBean deliveryOfferBean = (DeliveryOfferBean) responseClass.getResponsePacket();
                        deliveryOfferBean.setOrderType(fromWhich);
                        deliveryOfferBean.setLastRequestTime(Calendar.getInstance().getTimeInMillis());

                        SharedPref.setDeliveryOfferJson(mContext, new Gson().toJson(deliveryOfferBean));

//                        showDiscountOfferPopup(true, deliveryOfferBean.getOfferMessage());
                        Utils.showOfferPopUp(CartActi.this, true, deliveryOfferBean.getOfferMessage(), new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                Utils.hideOfferPopUp();
                                placeOrderProcess();
                            }
                        }, new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                Utils.hideOfferPopUp();
                                finish();
                            }
                        });
                    } else {
                        placeOrderProcess();
                    }
                }

                @Override
                public void onFailure(boolean success, String response, int which) {
                    Utils.dismissProgressDialog();
                    Utils.showCenterToast(mContext, getString(R.string.internet_connection_not_found));
                }
            }).callAPIJson(mContext, Constants.VAL_POST, AppUrls.GET_DELIVERY_OFFER, jsonObject.toString(), "Loading...", true, AppUrls.REQUESTTAG_GETDELIVERYOFFER);

        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
    }

    private void callPlaceOrderApi() {

        try {
            new NetworkManager(String.class, new NetworkManager.OnCallback<String>() {
                @Override
                public void onSuccess(boolean success, ResponseClass responseClass, int which) {
                    if (responseClass.isSuccess()) {
                        new Thread(new Runnable() {
                            @Override
                            public void run() {
                                customCleverChargedEvent(cleverTapBean.isDeliveryOfferApplicable());
                            }
                        }).start();

                        db.deleteAllItems();
                        SharedPref.setDeliveryOfferJson(mContext, "");
                        SharedPref.setCouponCode(mContext, "");
                        if (fromWhich.equalsIgnoreCase(getString(R.string.take_away)) || fromWhich.equals(getString(R.string.dine_in))
                                || fromWhich.equals(getString(R.string.cafe))) {
                            showSuccessPopUp(responseClass.getMessage());
                        } else {
                            Utils.showCenterToast(mContext, responseClass.getMessage());
                            Intent intent = new Intent(mContext, TrackActi.class);
                            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                            intent.putExtra(Constants.ORDER_ID, orderId);
                            intent.putExtra(Constants.IS_FROM_CART, true);
                            startActivity(intent);
                            finish();
                        }
                    } else {
                        Utils.showCenterToast(mContext, responseClass.getMessage());
                    }
                }

                @Override
                public void onFailure(boolean success, String response, int which) {
                    Utils.showCenterToast(mContext, getString(R.string.internet_connection_not_found));
                }
            }).callAPIJson(mContext, Constants.VAL_POST, AppUrls.PLACE_ORDER + "/" + orderId, placeOrderJson, "Loading...", true, AppUrls.REQUESTTAG_PLACEORDER);

        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
    }

    private void setLastPaymentMode() {
        String lastPaymentMode = "";
        try {

            LastPaymentModeBean lastPaymentModeBean = new LastPaymentModeBean();
            lastPaymentModeBean.setLastPaymentMode(paymentMode);
            lastPaymentModeBean.setLastPaymentType(paymentType);

            if (paymentMode.equals(getString(R.string.online))) {
                if (paymentType.equals(getString(R.string.netbanking))) {
                    lastPaymentModeBean.setBankBean(bankBean);
                }
                if (paymentType.equals(getString(R.string.credit_debit_atm_card))) {
                    if (isSavedCard)
                        lastPaymentModeBean.setSavedCard(savedCardBean);
                }
            }

            Gson gson = new GsonBuilder().create();
            lastPaymentMode = gson.toJson(lastPaymentModeBean);

            lastPaymentModeBean.setLastPaymentMode(lastPaymentMode);

            String json = gson.toJson(lastPaymentModeBean);

            callSetPaymentModeApi(json);
            Log.e("LASTPAYMENTMODE---", json);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void callSetPaymentModeApi(String json) {
        try {
            new NetworkManager(String.class, new NetworkManager.OnCallback<String>() {
                @Override
                public void onSuccess(boolean success, ResponseClass responseClass, int which) {
                    if (responseClass.isSuccess()) {
                        if (paymentType.equals(getString(R.string.cash_on_delivery)) || paymentType.equals(Constants.BILL_TO_COMPANY)) {
//                            Utils.showCenterToast(mContext, responseClass.getMessage());
//                            goToTrackingActi(orderId);
                            if (fromWhich.equals(Constants.ROOM_BOOKING)) {
                                callRoomPlaceApi();
                            } else {
                                callPlaceOrderApi();
                            }
                        } else {
//                            if(isSavedCard)
//                                goToCvvScreen(placeOrderJson);
//                            else
//                                goToPaymentScreen();
                            /* this for  generate for payumoney generate payment Request*/
//                            callGeneratePaymentRequestApi();

                            if (paymentType.equalsIgnoreCase("sodexo")) {
                                callGenerateTransactionLinkApi();
                            } else {
                                callGetTokenApi();
                            }

                        }
                    } else {
                        Utils.showCenterToast(mContext, responseClass.getMessage());
                    }
                }

                @Override
                public void onFailure(boolean success, String response, int which) {
                    Utils.showCenterToast(mContext, getString(R.string.internet_connection_not_found));
                }
            }).callAPIJson(mContext, Constants.VAL_POST, AppUrls.SET_LAST_PAYMENT_MODE, json, "Loading...", true, AppUrls.REQUESTTAG_SETLASTPAYMENTMODE);

        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
    }

    private void callGetPaymentModeApi() {
        try {
            new NetworkManager(String.class, new NetworkManager.OnCallback<String>() {
                @Override
                public void onSuccess(boolean success, ResponseClass responseClass, int which) {
                    if (responseClass.isSuccess()) {
                        if (!TextUtils.isEmpty((String) responseClass.getResponsePacket())) {
                            GsonBuilder builder = new GsonBuilder();
                            Gson gson = builder.create();
                            lastPaymentModeBean = gson.fromJson(responseClass.getResponsePacket().toString(), LastPaymentModeBean.class);
                            if (!TextUtils.isEmpty(lastPaymentModeBean.getLastPaymentMode()) && !TextUtils.isEmpty(lastPaymentModeBean.getLastPaymentType())) {

                                if (fromWhich.equals(getString(R.string.dine_in)) && lastPaymentModeBean.getLastPaymentMode().equals(getString(R.string.cash))) {
                                    binding.setIsPlace(false);
                                } else {
                                    binding.setIsPlace(true);
                                    setPaymentData(lastPaymentModeBean);
                                }
                            }
                            if (fromWhich.equals(Constants.ROOM_BOOKING)) {
                                callRoomDetailsApi(true);
                            } else
                                callGetOrderDetailsApi(true);
                        } else {
                            binding.setIsPlace(false);
                            if (fromWhich.equals(Constants.ROOM_BOOKING)) {
                                callRoomDetailsApi(true);
                            } else
                                callGetOrderDetailsApi(false);
                        }
                    } else {
                        Utils.showCenterToast(mContext, responseClass.getMessage());
                        if (fromWhich.equals(Constants.ROOM_BOOKING)) {
                            callRoomDetailsApi(true);
                        } else
                            callGetOrderDetailsApi(false);
                    }
                }

                @Override
                public void onFailure(boolean success, String response, int which) {
                    Utils.showCenterToast(mContext, getString(R.string.internet_connection_not_found));
                }
            }).callAPIJson(mContext, Constants.VAL_GET, AppUrls.GETLASTPAYMENTMODE, null, "Loading...", false, AppUrls.REQUESTTAG_GETLASTPAYMENTMODE);

        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
    }

    private void callGeneratePaymentRequestApi() {
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put(Constants.PAYMENT_AMOUNT, remainingAmount);
            jsonObject.put(Constants.ORDER_ID, orderId);
            jsonObject.put(Constants.GENERATEPAYMENTREQUESTFORORDER, true);

        } catch (JSONException e) {
            e.printStackTrace();
        }

        try {
            new NetworkManager(String.class, new NetworkManager.OnCallback<String>() {
                @Override
                public void onSuccess(boolean success, ResponseClass responseClass, int which) {
                    if (responseClass.isSuccess()) {
                        paymentRequestId = (String) responseClass.getResponsePacket();
//                        if (paymentType.equalsIgnoreCase("Paytm")) {
//                            callGenerateChecksumApi();
//                        } else if (isSavedCard)
//                            goToCvvScreen(placeOrderJson);
//                        else
//                            goToPaymentScreen();
//                        generatePaymentId(paymentRequestId);
                        callGetTokenApi();
//                        callUpdatePaymentResuestApi();
                    } else {
                        Utils.showCenterToast(mContext, responseClass.getMessage());
                    }
                }

                @Override
                public void onFailure(boolean success, String response, int which) {
                    Utils.showCenterToast(mContext, getString(R.string.internet_connection_not_found));
                }
            }).callAPIJson(mContext, Constants.VAL_POST, AppUrls.GENERATE_PAYMENT_REQUEST, jsonObject.toString(), "Loading...", true, AppUrls.REQUESTTAG_GENERATEPAYMENTREQUEST);

        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }

    }

    private void callUpdatePaymentResuestApi(String paymentGateway) {
        String successTransactionId = "";
//        if (paymentGateway.equalsIgnoreCase("Razorpay"))
//            successTransactionId = transactionId;
//        if (paymentGateway.equalsIgnoreCase("PayUMoney"))
//            successTransactionId = transactionId;
//        else
        if (paymentMode.equalsIgnoreCase("sodexo")) {
            successTransactionId = paymentRequestId;
        } else {
            successTransactionId = sodexoBean.getTransactionId();
        }
//        successTransactionId = paymentRequestId;
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put(Constants.PAYMENT_GATEWAY, paymentGateway);
            jsonObject.put(Constants.NICKNAME, nickName);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        try {
            new NetworkManager(String.class, new NetworkManager.OnCallback<String>() {
                @Override
                public void onSuccess(boolean success, ResponseClass responseClass, int which) {
                    if (responseClass.isSuccess()) {
                        if (fromWhich.equals(Constants.ROOM_BOOKING)) {
                            callRoomPlaceApi();
                        } else
                            callPlaceOrderApi();
                    } else {
                        Utils.showCenterToast(mContext, responseClass.getMessage());
                    }
                }

                @Override
                public void onFailure(boolean success, String response, int which) {
                    Utils.showCenterToast(mContext, getString(R.string.internet_connection_not_found));
                }
            }).callAPIJson(mContext, Constants.VAL_POST, AppUrls.UPDATE_PAYMENT_REQUEST + successTransactionId, jsonObject.toString(), "Loading...", true, AppUrls.REQUESTTAG_UPDATEPAYMENTREQUEST);

        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
    }


    private void callUpdatePaymentRequestForSodexoApi(String paymentGateway) {
        String successTransactionId = "";
//        if (paymentMode.equalsIgnoreCase("sodexo")){
//            successTransactionId = paymentRequestId;
//        }else{
//            successTransactionId = sodexoBean.getTransactionId();
//        }
        successTransactionId = sodexoBean.getTransactionId();
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put(Constants.PAYMENT_GATEWAY, paymentGateway);
            jsonObject.put(Constants.ORDER_ID, orderDetails.getOrderRefId());
            jsonObject.put(Constants.TRANSACTION_ID, sodexoBean.getTransactionId());
            jsonObject.put(Constants.NICKNAME, nickName);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        try {
            new NetworkManager(String.class, new NetworkManager.OnCallback<String>() {
                @Override
                public void onSuccess(boolean success, ResponseClass responseClass, int which) {
                    if (responseClass.isSuccess()) {
                        if (fromWhich.equals(Constants.ROOM_BOOKING)) {
                            callRoomPlaceApi();
                        } else
                            callPlaceOrderApi();
                    } else {
                        if (responseClass.getErrorCode() == 1) {
                            onBackPressed();
                        }
//                        Utils.showCenterToast(mContext, responseClass.getMessage());
                    }
                }

                @Override
                public void onFailure(boolean success, String response, int which) {
                    Utils.showCenterToast(mContext, getString(R.string.internet_connection_not_found));
                }
            }).callAPIJson(mContext, Constants.VAL_POST, AppUrls.UPDATE_PAYMENT_REQUEST_FOR_SODEXO, jsonObject.toString(), "Loading...", true, AppUrls.REQUESTTAG_UPDATE_PAYMENT_REQUEST_FOR_SODEXO);

        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
    }


    private void callCheckPaymentApi() {

        try {
            new NetworkManager(String.class, new NetworkManager.OnCallback<String>() {
                @Override
                public void onSuccess(boolean success, ResponseClass responseClass, int which) {
                    if (responseClass.isSuccess()) {
                        Utils.showCenterToast(mContext, responseClass.getMessage());


                        goToTrackingActi(orderId);

                    } else {
                        Utils.showCenterToast(mContext, responseClass.getMessage());
                    }
                }

                @Override
                public void onFailure(boolean success, String response, int which) {
                    Utils.showCenterToast(mContext, getString(R.string.internet_connection_not_found));
                }
            }).callAPIJson(mContext, Constants.VAL_GET, AppUrls.CHECK_PAYMENT_STATUS + orderId + "/" + transactionId + "/" + nickName + "/" + isSavedCard, "", "Loading...", true, AppUrls.REQUESTTAG_CHECKPAYMENTSTATUS);

        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
    }

    private void callGenerateChecksumApi() {
        UserBean userBean = (UserBean) Utils.getJsonToClassObject(SharedPref.getUserModelJSON(mContext), UserBean.class);

        CheckSumBean bean = new CheckSumBean();
        bean.setMid(Constants.PAYTM_MID);
        bean.setCallbackUrl("https://securegw.paytm.in/order/process?ORDER_ID=" + paymentRequestId);
//        bean.setCallbackUrl("https://securegw-stage.paytm.in/order/process?ORDER_ID=" + paymentRequestId);
        bean.setOrderId(paymentRequestId);
        bean.setMercunqref("Afoozo" + " " + paymentRequestId);
        bean.setChannelId("WAP");
//        bean.setCustId(Long.parseLong(SharedPref.getUserId(mContext)));
        bean.setPhone(userBean.getMobile());
        bean.setEmail(userBean.getEmail());
        bean.setTxnAmount(new DecimalFormat("0.00").format(remainingAmount));
//        bean.setTxnAmount("1");
        bean.setWebsite("DEFAULT");
//        bean.setWebsite("WEBSTAGING");
//        bean.setIndustryTypeId("Retail");
        bean.setIndustryTypeId("Retail109");
//        bean.setType(type);

        Gson gson = new Gson();
        String json = gson.toJson(bean);

        try {
            new NetworkManager(CheckSumBean.class, new NetworkManager.OnCallback<CheckSumBean>() {
                @Override
                public void onSuccess(boolean success, ResponseClass responseClass, int which) {
                    if (responseClass.isSuccess()) {
                        checkSumBean = ((CheckSumBean) responseClass.getResponsePacket());
//                        paytmPaymentProcess();
                    } else {
                        Utils.showCenterToast(mContext, responseClass.getMessage());
                    }
                }

                @Override
                public void onFailure(boolean success, String response, int which) {
                    Utils.showCenterToast(mContext, getString(R.string.internet_connection_not_found));
                }
            }).callAPIJson(mContext, Constants.VAL_POST, AppUrls.PG_REDIRECT + paymentRequestId, json, "Loading...", true, AppUrls.REQUESTTAG_PGREDIRECT);

        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
    }

    private void callPayOrderByPaytm(PayOrderBean bean) {

        Gson gson = new Gson();
        String json = gson.toJson(bean);

        try {
            new NetworkManager(String.class, new NetworkManager.OnCallback<String>() {
                @Override
                public void onSuccess(boolean success, ResponseClass responseClass, int which) {
                    if (responseClass.isSuccess()) {
//                        goToTrackingActi(orderId);
                        callPlaceOrderApi();
                    } else {
                        Utils.showCenterToast(mContext, responseClass.getMessage());
                    }
                }

                @Override
                public void onFailure(boolean success, String response, int which) {
                    Utils.showCenterToast(mContext, getString(R.string.internet_connection_not_found));
                }
            }).callAPIJson(mContext, Constants.VAL_POST, AppUrls.VERIFY_TRANSACTION + paymentRequestId, json, "Loading...", true, AppUrls.REQUESTTAG_VERIFYTRANSACTION);

        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
    }

    /**
     * This function prepares the data for payment and launches payumoney plug n play sdk
     */

//    private void generatePaymentId(String paymentRequestId) {
//        UserBean userBean = (UserBean) Utils.getJsonToClassObject(SharedPref.getUserModelJSON(mContext), UserBean.class);
//
////        transactionId = System.currentTimeMillis() +"";
//        transactionId = paymentRequestId;
//
//        builder = new PayUmoneySdkInitializer.PaymentParam.Builder();
//
//
//        builder.setAmount(new DecimalFormat("0.00").format(remainingAmount))     // Payment amount
//                .setTxnId(paymentRequestId)                                              // Transaction ID
//                .setPhone(userBean.getMobile())                                           // User Phone number
//                .setProductName("Afoozo")                   // Product Name or description
//                .setFirstName(userBean.getFullName().replace(" ", ""))                              // User First name
//                .setEmail(userBean.getEmail())                                            // User Email ID
//                .setsUrl("https://www.payumoney.com/mobileapp/payumoney/success.php")                    // Success URL (surl)
//                .setfUrl("https://www.payumoney.com/mobileapp/payumoney/failure.php")             //Failure URL (furl)
//                .setUdf1("a")
//                .setUdf2("b")
//                .setUdf3("c")
//                .setUdf4("d")
//                .setUdf5("e")
//                .setUdf6("")
//                .setUdf7("")
//                .setUdf8("")
//                .setUdf9("")
//                .setUdf10("")
//                .setIsDebug(false)                              // Integration environment - true (Debug)/ false(Production)
//                .setKey(Constants.MERCHANT_KEY)                        // Merchant key
//                .setMerchantId(Constants.MERCHANT_ID);// Merchant ID
//
//        MerchantKeyBean bean = new MerchantKeyBean();
////        bean.setKey(Constants.MERCHANT_KEY);
//        bean.setAmount((float) remainingAmount);
//        bean.setEmail(userBean.getEmail());
//        bean.setFirstname(userBean.getFullName().replace(" ", ""));
//        bean.setProductinfo("Afoozo");
//        bean.setTxnid(paymentRequestId);
//        bean.setUdf1("a");
//        bean.setUdf2("b");
//        bean.setUdf3("c");
//        bean.setUdf4("d");
//        bean.setUdf5("e");
////        Log.d("trangsationId",transactionId);
//        callGetMerchantHashApi(new Gson().toJson(bean));
//
//    }
    private void callGetMerchantHashApi(String json) {

        try {
            new NetworkManager(String.class, new NetworkManager.OnCallback<String>() {
                @Override
                public void onSuccess(boolean success, ResponseClass responseClass, int which) {
                    if (responseClass.isSuccess()) {
//                        Utils.showCenterToast(mContext, responseClass.getMessage());
                        generatePaymentParams((String) responseClass.getResponsePacket());

                    } else {
                        Utils.showCenterToast(mContext, responseClass.getMessage());
                    }
                }

                @Override
                public void onFailure(boolean success, String response, int which) {
                    Utils.showCenterToast(mContext, getString(R.string.internet_connection_not_found));
                }
            }).callAPIJson(mContext, Constants.VAL_POST, AppUrls.GENERATE_HASH, json, "Loading...", true, AppUrls.REQUESTTAG_GENERATEHASH);

        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
    }

    private void generatePaymentParams(String hash) {
        //declare paymentParam object
//        PayUmoneySdkInitializer.PaymentParam paymentParam = null;
//        try {
//            paymentParam = builder.build();
//            paymentParam.setMerchantHash(hash);
//
//            PayUmoneyFlowManager.startPayUMoneyFlow(paymentParam, CartActi.this, R.style.AppTheme_Grey, true);
//
//        } catch (Exception e) {
//            e.printStackTrace();
//        }

//set the hash
    }

//    private void callCancelTransactionApi() {
//        String json = new Gson().toJson(checkSumBean);
//        NetworkManager networkManager ;
//        try {
//            new NetworkManager(String.class, new NetworkManager.OnCallback<String>() {
//                @Override
//                public void onSuccess(boolean success, ResponseClass responseClass, int which) {
//                    if (responseClass.isSuccess()){
//                        Utils.showCenterToast(mContext, responseClass.getMessage());
//                        onResume();
//                    } else {
//                        Utils.showCenterToast(mContext, responseClass.getMessage());
//                    }
//                }
//
//                @Override
//                public void onFailure(boolean success, String response, int which) {
//                    Utils.showCenterToast(mContext, getString(R.string.internet_connection_not_found) );
//                }
//            }).callAPIJson(mContext, Constants.VAL_POST, AppUrls.CANCEL_TRANSACTION, json, "Loading...", true, AppUrls.REQUESTTAG_CANCELTRANSACTION);
//
//        } catch (UnsupportedEncodingException e) {
//            e.printStackTrace();
//        }
//    }

    /* this api use for th payment type list */

    private void callPaymentTypeApi() {

        try {
            new NetworkManager(ArrayList.class, new NetworkManager.OnCallback<String>() {
                @Override
                public void onSuccess(boolean success, ResponseClass responseClass, int which) {
                    if (responseClass.isSuccess()) {
//                        Type objType = new TypeToken<ResponseClass<ArrayList<SavedCardBean>>>() {
//                        }.getType();
//                        ResponseClass<ArrayList<SavedCardBean>> responseClass1 = new Gson().fromJson(responseClass.getCompletePacket(), objType);
                        paymentTypeList = (ArrayList<String>) responseClass.getResponsePacket();

                        if (paymentTypeList != null && paymentTypeList.size() > 0) {
                            setWalletShow();
                        }

                    } else {
                        Utils.showCenterToast(mContext, responseClass.getMessage());
                    }
                }

                @Override
                public void onFailure(boolean success, String response, int which) {
                    Utils.showCenterToast(mContext, "Check your Internet Connection");
                }
            }).callAPIJson(mContext, Constants.VAL_GET, AppUrls.GET_PAYMENTGATEWAYLIST_V1 + fromWhich, "", "Loading...", true, AppUrls.REQUESTTAG__GETPAYMENTGATEWAYLIST_V1);

        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
    }

    /*  This method use for check wallet show or not in application */
    private void setWalletShow() {
        for (int i = 0; i < paymentTypeList.size(); i++) {
            if (paymentTypeList.get(i).equalsIgnoreCase("Wallet")) {
                binding.llWallet.setVisibility(View.VISIBLE);
                break;
            } else
                binding.llWallet.setVisibility(GONE);

        }
    }

    private void customCleverChargedEvent(boolean isDeliveryOfferApplicable) {
        HashMap<String, Object> chargedAction = new HashMap<String, Object>();
        chargedAction.put("Payment Mode", paymentMode);
        chargedAction.put("Location Name", restaurantName);
        chargedAction.put("Order Type", fromWhich);
        chargedAction.put("Delivery Offer Applied", isDeliveryOfferApplicable ? "Applied" : "N/A");
        chargedAction.put("Coupon Code Applied", orderDetails.getCouponCode() != null ? orderDetails.getCouponCode() : "N/A");
        chargedAction.put("Amount", cleverTapBean.getPaidByWallet() + cleverTapBean.getPaidOnDelivery());

        ArrayList<HashMap<String, Object>> items = new ArrayList<HashMap<String, Object>>();
        for (OrderDetailsItemBean itemBean : orderDetails.getItemList()) {

            HashMap<String, Object> item1 = new HashMap<String, Object>();
            item1.put("Item Name", itemBean.getTitle());
            item1.put("Item Id", itemBean.getItemId());
            item1.put("Quantity", itemBean.getQuantity());
            items.add(item1);
        }

        try {
            AppInitialization.getInstance().clevertapDefaultInstance.pushChargedEvent(chargedAction, items);
        } catch (Exception e) {
// You have to specify the first parameter to push()
// as CleverTapAPI.CHARGED_EVENT
        }
    }

    private void customCleverPaymentFailedEvent(String paymentMode, String reason) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                HashMap<String, Object> paymentFailedAction = new HashMap<String, Object>();
                paymentFailedAction.put("Payment Mode", paymentMode);
                paymentFailedAction.put("Reason", reason);
                AppInitialization.getInstance().clevertapDefaultInstance.pushEvent(fromWhich + " Payment Failed", paymentFailedAction);
            }
        }).start();

    }

    @Override
    public void onDisplayUnitsLoaded(ArrayList<CleverTapDisplayUnit> units) {
        Log.e("TestData", units.toString());
    }

    /* order details for room booking*/

    private void callRoomDetailsApi(final boolean showLoading) {

        try {
            new NetworkManager(OrderDetailsBean.class, new NetworkManager.OnCallback<OrderDetailsBean>() {
                @Override
                public void onSuccess(boolean success, ResponseClass responseClass, int which) {
                    if (!showLoading)
                        Utils.dismissProgressDialog();
                    if (responseClass.isSuccess()) {
                        orderDetails = (OrderDetailsBean) responseClass.getResponsePacket();
                        setRoomData();

                    } else {
                        Utils.showCenterToast(mContext, responseClass.getMessage());
                    }
                }

                @Override
                public void onFailure(boolean success, String response, int which) {
                    if (!showLoading)
                        Utils.dismissProgressDialog();
                    Utils.showCenterToast(mContext, getString(R.string.internet_connection_not_found));
                }
            }).callAPIJson(mContext, Constants.VAL_GET, AppUrls.ROOM_BOOKING_DETAIL + orderId, "", "Loading...", showLoading, AppUrls.REQUESTTAG_ROOMBOOKINGDETAIL);

        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
    }

    /* room booking order Place*/

    private void callRoomPlaceApi() {
        try {
            new NetworkManager(String.class, new NetworkManager.OnCallback<String>() {
                @Override
                public void onSuccess(boolean success, ResponseClass responseClass, int which) {
                    if (responseClass.isSuccess()) {
//                        showSuccessPopUp(responseClass.getMessage());
                        Utils.showCenterToast(mContext, responseClass.getMessage());
                        Utils.startActivityWithFinish(mContext, Dashboard.class);
                    } else {
                        Utils.showCenterToast(mContext, responseClass.getMessage());
                    }
                }

                @Override
                public void onFailure(boolean success, String response, int which) {
                    Utils.showCenterToast(mContext, getString(R.string.internet_connection_not_found));
                }
            }).callAPIJson(mContext, Constants.VAL_POST, AppUrls.PLACE_ROOM_BOOKING, placeOrderJson, "Loading...", true, AppUrls.REQUESTTAG_PLACEROOMBOOKING);

        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
    }

    private void callGenerateTransactionLinkApi() {
        JSONObject jsonObject = new JSONObject();
        try {
            if (fromWhich.equals(Constants.ROOM_BOOKING)) {
                jsonObject.put("orderId", orderDetails.getBookingReferenceId());
            } else {
                jsonObject.put("orderId", orderDetails.getOrderRefId());
            }
            jsonObject.put("amount", remainingAmount);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        try {
            new NetworkManager(SodexoBean.class, new NetworkManager.OnCallback<SodexoBean>() {
                @Override
                public void onSuccess(boolean success, ResponseClass responseClass, int which) {
                    if (responseClass.isSuccess()) {
                        sodexoBean = (SodexoBean) responseClass.getResponsePacket();
                        goToWebViewActivity(sodexoBean.getPaymentUrl());
                    } else {
                        Utils.showCenterToast(mContext, responseClass.getMessage());
                    }
                }

                @Override
                public void onFailure(boolean success, String response, int which) {
                    Utils.showCenterToast(mContext, getString(R.string.internet_connection_not_found));
                }
            }).callAPIJson(mContext, Constants.VAL_POST, AppUrls.GENERATE_TRANSACTION_LINK, jsonObject.toString(), "Loading...", true, AppUrls.REQUESTTAG_GENERATE_TRANSACTION_LINK);

        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
    }

    private void goToWebViewActivity(String paymentUrl) {
        Intent intent = new Intent(mContext, TermsActi.class);
        intent.putExtra(Constants.PAYMENT_URL, paymentUrl);
        intent.putExtra("ForWhich", "payment");
        startActivityForResult(intent, SODEXO_PAYMENT_REQUEST_CODE);
    }

    private void callGetTokenApi() {
        OrderDetailsBean bean = new OrderDetailsBean();
        bean.setOrderId(orderId);
        bean.setTotalAmount(Math.round(remainingAmount));
        String json = new Gson().toJson(bean);
        try {
            new NetworkManager(CreateOrderBean.class, new NetworkManager.OnCallback<CreateOrderBean>() {
                @Override
                public void onSuccess(boolean success, ResponseClass responseClass, int which) {
                    if (responseClass.isSuccess()) {
                        createOrderBean = ((CreateOrderBean) responseClass.getResponsePacket());
                        doWebCheckoutPayment(createOrderBean.getPayment_session_id(), createOrderBean.getOrder_id());
                    } else {
                        Utils.showCenterToast(mContext, responseClass.getMessage());
                    }
                }

                @Override
                public void onFailure(boolean success, String response, int which) {
                    Utils.showCenterToast(mContext, getString(R.string.internet_connection_not_found));
                }
            }).callAPIJson(mContext, Constants.VAL_POST, AppUrls.createOrder, json, "Loading...", true, AppUrls.REQUESTTAG_CREATE_ORDER);

        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
    }

    public void doWebCheckoutPayment(String sessionId, String generatedOrderId) {
        try {
            CFSession cfSession = new CFSession.CFSessionBuilder()
//                    .setEnvironment(CFSession.Environment.PRODUCTION)
                    .setEnvironment(CFSession.Environment.SANDBOX)
                    .setPaymentSessionID(sessionId)
                    .setOrderId(generatedOrderId)
                    .build();
            // Replace with your application's theme colors
            CFWebCheckoutTheme cfTheme = new CFWebCheckoutTheme.CFWebCheckoutThemeBuilder()
                    .setNavigationBarBackgroundColor("#fc2678")
                    .setNavigationBarTextColor("#ffffff")
                    .build();
            CFWebCheckoutPayment cfWebCheckoutPayment = new CFWebCheckoutPayment.CFWebCheckoutPaymentBuilder()
                    .setSession(cfSession)
                    .setCFWebCheckoutUITheme(cfTheme)
                    .build();
            CFPaymentGatewayService.getInstance().doPayment(CartActi.this, cfWebCheckoutPayment);
        } catch (CFException exception) {
            exception.printStackTrace();
        }
    }

    private void callUpdatePaymentRequestCashFreeApi(String generatedOrderID, String paymentStatus) {
        cashFreeResponse.setPaymentGateway("CashFree");
        cashFreeResponse.setOrderId(generatedOrderID);
        cashFreeResponse.setOrderAmount(remainingAmount);
        cashFreeResponse.setReferenceId(orderId);
        cashFreeResponse.setTxStatus(paymentStatus);
        String json = new Gson().toJson(cashFreeResponse);

        try {
            new NetworkManager(String.class, new NetworkManager.OnCallback<String>() {
                @Override
                public void onSuccess(boolean success, ResponseClass responseClass, int which) {
                    if (responseClass.isSuccess()) {
                        if (fromWhich.equals(Constants.ROOM_BOOKING)) {
                            callRoomPlaceApi();
                        } else
                            callPlaceOrderApi();
                    } else {
                        Utils.showCenterToast(mContext, responseClass.getMessage());
                    }
                }

                @Override
                public void onFailure(boolean success, String response, int which) {
                    Utils.showCenterToast(mContext, getString(R.string.internet_connection_not_found));
                }
            }).callAPIJson(mContext, Constants.VAL_POST, AppUrls.UPDATE_PAYMENT_REQUEST_FOR_CASH_FREE_V2, json, "Loading...", true, AppUrls.REQUESTTAG_UPDATEPAYMENTREQUESTFORCASHFREE_V2);

        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onPaymentVerify(String orderID) {
        callVerifyPaymentCashFree(orderID, "SUCCESS");
//        callUpdatePaymentRequestCashFreeApi(orderID,"SUCCESS");
        Log.d("orderID", "####" + orderID);
    }

    @Override
    public void onPaymentFailure(CFErrorResponse cfErrorResponse, String orderID) {
        Log.d("cfErrorResponse_Order_id", "####" + cfErrorResponse + orderID);
        callVerifyPaymentCashFree(orderID, "FAILED");
    }

    private void callVerifyPaymentCashFree(String generatedOrderId, String orderStatus) {
        try {
            new NetworkManager(String.class, new NetworkManager.OnCallback<String>() {
                @Override
                public void onSuccess(boolean success, ResponseClass responseClass, int which) {
                    if (responseClass.getResponsePacket().toString().equalsIgnoreCase("SUCCESS")) {
                        callUpdatePaymentRequestCashFreeApi(generatedOrderId, "SUCCESS");

                    } else {
                        Utils.showCenterToast(mContext, responseClass.getResponsePacket().toString());
                    }
                }

                @Override
                public void onFailure(boolean success, String response, int which) {
                    Utils.showCenterToast(mContext, getString(R.string.internet_connection_not_found));
                }
            }).callAPIJson(mContext, Constants.VAL_GET, AppUrls.verifiedPaymentCashFree + generatedOrderId, "", "Loading...", true, AppUrls.REQUESTTAG_verifiedPaymentCashFree);

        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
    }

}
