package in.kpis.afoozo.fragment;


import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;

import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.GridLayoutManager;

import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.SeekBar;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.kpis.afoozo.R;
import in.kpis.afoozo.activity.ScanQrActi;
import in.kpis.afoozo.activity.StealDealActi;
import in.kpis.afoozo.adapter.ConsumeAdapter;
import in.kpis.afoozo.bean.ConsumeBean;
import in.kpis.afoozo.bean.RestaurantRequestBean;
import com.kpis.afoozo.databinding.ApplyGiftCodeLayoutBinding;
import com.kpis.afoozo.databinding.FragmentConsumeBinding;
import com.kpis.afoozo.databinding.PopupConsumeBinding;
import com.kpis.afoozo.databinding.PopupGiftBinding;
import com.kpis.afoozo.databinding.PopupGiftSuccessBinding;
import in.kpis.afoozo.okhttpServcice.AppUrls;
import in.kpis.afoozo.okhttpServcice.NetworkManager;
import in.kpis.afoozo.okhttpServcice.response.ResponseClass;
import in.kpis.afoozo.util.Constants;
import in.kpis.afoozo.util.Utils;
import in.kpis.afoozo.util.recycler_view_utilities.RecyclerItemClickListener;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Calendar;

/**
 * A simple {@link Fragment} subclass.
 */
public class ConsumeFrag extends Fragment implements View.OnClickListener {

    private View view;

    private Context mContext;

    private FragmentConsumeBinding binding;

    private ConsumeAdapter mAdapter;

    private Dialog alert;

    private int selectedQty;
    private int totalQty;
    private int itemPosition;
    private int CONSUME_REQUEST = 301;

    private String restaurantId;

    private ArrayList<ConsumeBean> consumeList;
    private PopupConsumeBinding consumeBinding;
    private PopupGiftBinding giftBinding;
    private boolean isDataSet;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mContext = context;
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);

        if (isDataSet)
            refreshList();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_consume, container, false);
        view = binding.getRoot();

        initialize();
        return view;
    }

    private void initialize() {
        binding.rvConsume.setLayoutManager(new GridLayoutManager(mContext, 2));
        binding.rvConsume.setItemAnimator(new DefaultItemAnimator());
        binding.rvConsume.addOnItemTouchListener(new RecyclerItemClickListener(mContext, new RecyclerItemClickListener.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
//                Utils.consumeDialog(mContext);
                itemPosition = position;
                if (Calendar.getInstance().getTimeInMillis() >= consumeList.get(position).getConsumeAfter())
                    showConsumePopup();
                else {
                    String msg = mContext.getString(R.string.you_can_unlock_the_purchased_item_to_consume_after);
                    msg.replace("-", Utils.getDateTimeForOrders(consumeList.get(position).getConsumeAfter()));
                    Utils.retryAlertDialog(mContext, "", (mContext.getString(R.string.you_can_unlock_the_purchased_item_to_consume_after)).replace("_", Utils.getDateTimeForOrders(consumeList.get(position).getConsumeAfter())), "", mContext.getString(R.string.Ok), new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Utils.dismissRetryAlert();
                        }
                    });
                }
            }
        }));

        binding.btnGiftCode.setOnClickListener(this);

        if (((StealDealActi) getActivity()).selectedRestaurant != null) {
            restaurantId = ((StealDealActi) getActivity()).selectedRestaurant;
            callConsumeListApi();
            isDataSet = true;
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.imvPCAdd:
                if (selectedQty < totalQty){
                    ++selectedQty;
                    consumeBinding.sbQty.setProgress(selectedQty);
                    setQty();
                }
                break;
            case R.id.imvPCMinus:
                if (selectedQty > 0){
                    --selectedQty;
                    consumeBinding.sbQty.setProgress(selectedQty);
                    setQty();
                }
                break;
            case R.id.btnPCScan:
                alert.dismiss();
                Intent intent = new Intent(mContext, ScanQrActi.class);
                intent.putExtra(Constants.FROM_WHICH, getString(R.string.consume));
                intent.putExtra("consume_bean", consumeList.get(itemPosition));
                intent.putExtra(Constants.QUANTITY, selectedQty);
                startActivityForResult(intent, CONSUME_REQUEST);
                break;
            case R.id.txtPCGift:
                alert.dismiss();
                showGiftPopUp();
                break;
            case R.id.btnGiftCode:
                showApplyGiftCodePopup();
                break;
            case R.id.btnPGConfirm:
                if (giftBinding.cbPGTerms.isChecked()){
                    alert.dismiss();
                    callGenerateGiftCodeApi();
                }
                break;
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == CONSUME_REQUEST && resultCode == getActivity().RESULT_OK){
            callConsumeListApi();
        }
    }

    public void refreshList(){
        try {
            if (((StealDealActi) getActivity()).selectedRestaurant != null) {
                restaurantId = ((StealDealActi) getActivity()).selectedRestaurant;
                callConsumeListApi();
            }
        } catch (Exception e){

        }
    }

    private void showConsumePopup() {
        alert = new Dialog(mContext);
        alert.requestWindowFeature(Window.FEATURE_NO_TITLE);
        alert.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        consumeBinding = DataBindingUtil.inflate(LayoutInflater.from(mContext), R.layout.popup_consume, null, false);
        alert.setContentView(consumeBinding.getRoot());

        totalQty = consumeList.get(itemPosition).getRemainingQuantity();
        selectedQty = 1;

        consumeBinding.txtPCItemName.setText(consumeList.get(itemPosition).getStealDealItemTitle());

        setQty();


        consumeBinding.sbQty.setMax(consumeList.get(itemPosition).getRemainingQuantity());
        consumeBinding.sbQty.setProgress(selectedQty);

        consumeBinding.sbQty.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                selectedQty = (int)progress;
                setQty();
            }


            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });

        consumeBinding.imvPCAdd.setOnClickListener(this);
        consumeBinding.imvPCMinus.setOnClickListener(this);
        consumeBinding.btnPCScan.setOnClickListener(this);
        consumeBinding.txtPCGift.setOnClickListener(this);

        alert.setCancelable(true);
        alert.show();

    }

    private void showApplyGiftCodePopup() {
        alert = new Dialog(mContext);
        alert.requestWindowFeature(Window.FEATURE_NO_TITLE);
        alert.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        ApplyGiftCodeLayoutBinding applyGiftBinding = DataBindingUtil.inflate(LayoutInflater.from(mContext), R.layout.apply_gift_code_layout, null, false);
        alert.setContentView(applyGiftBinding.getRoot());

        applyGiftBinding.btnApply.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String giftCode = applyGiftBinding.etGiftCode.getText().toString().trim();
                if (!TextUtils.isEmpty(giftCode)) {
                    alert.dismiss();
                    callApplyGiftCodeApi(giftCode);
                }
            }
        });

        alert.setCancelable(true);
        alert.show();

    }

    private void showGiftPopUp() {
        alert = new Dialog(mContext);
        alert.requestWindowFeature(Window.FEATURE_NO_TITLE);
        alert.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        giftBinding = DataBindingUtil.inflate(LayoutInflater.from(mContext), R.layout.popup_gift, null, false);
        alert.setContentView(giftBinding.getRoot());

        giftBinding.txtPGItem.setText(consumeList.get(itemPosition).getStealDealItemTitle());
        giftBinding.txtPGStore.setText(consumeList.get(itemPosition).getStealDealItemTitle());
        giftBinding.txtPGQty.setText(selectedQty + " " + consumeList.get(itemPosition).getConsumableUnitPostfix());


        giftBinding.btnPGConfirm.setOnClickListener(this);

        alert.setCancelable(true);
        alert.show();

    }

    private void showSuccessPopUp(String giftCode) {
        alert = new Dialog(mContext);
        alert.requestWindowFeature(Window.FEATURE_NO_TITLE);
        alert.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        PopupGiftSuccessBinding giftSuccessBinding = DataBindingUtil.inflate(LayoutInflater.from(mContext), R.layout.popup_gift_success, null, false);
        alert.setContentView(giftSuccessBinding.getRoot());

        giftSuccessBinding.txtPopCode.setText(giftCode);

        giftSuccessBinding.btnPopShare.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                alert.dismiss();
                String message = "Hey there, \n I have gifted you" + "" + selectedQty + " " + consumeList.get(itemPosition).getConsumableUnitPostfix() + " of " + consumeList.get(itemPosition).getStealDealItemTitle() + ". \n Download Afoozo app and redeem your gift by entering the code " + giftCode +  " in STEAL DEAL > Consume section of the app.";
                Utils.shareGiftCode(mContext, message);
            }
        });

        alert.setCancelable(true);
        alert.show();
    }

    private void setQty() {
        consumeBinding.txtPCSelectedQty.setText(selectedQty + " " + consumeList.get(itemPosition).getConsumableUnitPostfix());
        consumeBinding.txtPCItemQty.setText(selectedQty + " " + consumeList.get(itemPosition).getConsumableUnitPostfix());
        consumeBinding.txtPCQtyRemaining.setText((totalQty - selectedQty) + " " + consumeList.get(itemPosition).getConsumableUnitPostfix());
    }

    private void setData() {

        mAdapter = new ConsumeAdapter(mContext, consumeList);
        binding.rvConsume.setAdapter(mAdapter);
        mAdapter.notifyDataSetChanged();

    }

    private void callConsumeListApi() {
        RestaurantRequestBean bean = new RestaurantRequestBean();
        bean.setRestaurantId(restaurantId);
        bean.setStart(0);
        bean.setLength(1000);
        bean.setSearchKey("");

        String json = new Gson().toJson(bean);

        try {
            new NetworkManager(ArrayList.class, new NetworkManager.OnCallback<ConsumeBean>() {
                @Override
                public void onSuccess(boolean success, ResponseClass responseClass, int which) {
                    if (responseClass.isSuccess()){
                        Type objType = new TypeToken<ResponseClass<ArrayList<ConsumeBean>>>() {
                        }.getType();
                        ResponseClass<ArrayList<ConsumeBean>> responseClass1 = new Gson().fromJson(responseClass.getCompletePacket(), objType);

                        consumeList = responseClass1.getResponsePacket();

                        if (consumeList != null && consumeList.size() > 0) {
                            binding.rvConsume.setVisibility(View.VISIBLE);
                            setData();
                        } else {
                            binding.rvConsume.setVisibility(View.INVISIBLE);
                        }

//
                    } else {
                        Utils.showCenterToast(mContext, responseClass.getMessage());
                    }
                }

                @Override
                public void onFailure(boolean success, String response, int which) {
                    Utils.showCenterToast(mContext, getString(R.string.internet_connection_not_found) );
                }
            }).callAPIJson(mContext, Constants.VAL_POST, AppUrls.STEAL_DEAL_RESERVATION_ITEM_ITEMLIST, json, "Loading...", true, AppUrls.REQUESTTAG_STEALDEALRESERVATIONITEMITEMLIST);

        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
    }

    private void callGenerateGiftCodeApi() {

        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put(Constants.STEAL_DEAL_ITEM_RESERVATION_ID,consumeList.get(itemPosition).getStealDealItemReservationId());
            jsonObject.put(Constants.QUANTITY, selectedQty);
        } catch (JSONException e) {
            e.printStackTrace();
        }


        try {
            new NetworkManager(String.class, new NetworkManager.OnCallback<String>() {
                @Override
                public void onSuccess(boolean success, ResponseClass responseClass, int which) {
                    if (responseClass.isSuccess()){
                       showSuccessPopUp((String) responseClass.getResponsePacket());
//
                    } else {
                        Utils.showCenterToast(mContext, responseClass.getMessage());
                    }
                }

                @Override
                public void onFailure(boolean success, String response, int which) {
                    Utils.showCenterToast(mContext, getString(R.string.internet_connection_not_found) );
                }
            }).callAPIJson(mContext, Constants.VAL_POST, AppUrls.GENERATE_GIFT_CODE, jsonObject.toString(), "Loading...", true, AppUrls.REQUESTTAG_GENERATEGIFTCODE);

        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
    }

    private void callApplyGiftCodeApi(String giftCode) {

        try {
            new NetworkManager(String.class, new NetworkManager.OnCallback<String>() {
                @Override
                public void onSuccess(boolean success, ResponseClass responseClass, int which) {
                    if (responseClass.isSuccess()){
                       callConsumeListApi();
//
                    } else {
                        Utils.showCenterToast(mContext, responseClass.getMessage());
                    }
                }

                @Override
                public void onFailure(boolean success, String response, int which) {
                    Utils.showCenterToast(mContext, getString(R.string.internet_connection_not_found) );
                }
            }).callAPIJson(mContext, Constants.VAL_GET, AppUrls.APPLY_GIFT_CODE + giftCode, "", "Loading...", true, AppUrls.REQUESTTAG_APPLYGIFTCODE);

        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
    }

}
