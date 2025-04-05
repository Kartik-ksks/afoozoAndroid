package in.kpis.afoozo.activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

import android.content.Context;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;

import com.anupkumarpanwar.scratchview.ScratchView;
import com.bumptech.glide.Glide;
import com.kpis.afoozo.R;
import in.kpis.afoozo.bean.CouponListBean;
import in.kpis.afoozo.bean.RewardCouponBean;
import com.kpis.afoozo.databinding.ActivityRewardFullBinding;
import in.kpis.afoozo.okhttpServcice.AppUrls;
import in.kpis.afoozo.okhttpServcice.NetworkManager;
import in.kpis.afoozo.okhttpServcice.response.ResponseClass;
import in.kpis.afoozo.util.Constants;
import in.kpis.afoozo.util.Utils;

import java.io.UnsupportedEncodingException;
import java.text.DecimalFormat;

/**
 * An example full-screen activity that shows and hides the system UI (i.e.
 * status bar and navigation/system bar) with user interaction.
 */
public class RewardFullActi extends AppCompatActivity {

    private Context mContext;

    private ActivityRewardFullBinding binding;
    private RewardCouponBean bean;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = DataBindingUtil.setContentView(this, R.layout.activity_reward_full);

        getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);

        mContext = RewardFullActi.this;

        bean = (RewardCouponBean) getIntent().getSerializableExtra("RewardsBean");
//        int icon = bean.getImageIcon();

//        binding.imvScratch.setImageResource(icon);
        initialize();
    }

    private void initialize(){
        if (bean.getRewardsBean() != null) {
            binding.txtAmount.setText(mContext.getString(R.string.Rs) + bean.getRewardsBean().getCouponDiscountAmount());

            if (!TextUtils.isEmpty(bean.getRewardsBean().getCouponCode())) {
                setScratchCard();
            } else {
                binding.scratchView.setVisibility(View.VISIBLE);
                binding.setIsScratched(false);

                binding.scratchView.setRevealListener(new ScratchView.IRevealListener() {
                    @Override
                    public void onRevealed(ScratchView scratchView) {
//                        Toast.makeText(mContext, "Reveled", Toast.LENGTH_LONG).show();
                        binding.scratchView.setVisibility(View.GONE);
                        callScratchTheCardApi();
                    }

                    @Override
                    public void onRevealPercentChangedListener(ScratchView scratchView, float percent) {
                        if (percent >= 0.5) {
                            scratchView.clear();
//                            Log.d("Reveal Percentage", "onRevealPercentChangedListener: " + String.valueOf(percent));
                        }
                    }
                });
            }
        } else {
            binding.txtAmount.setText(mContext.getString(R.string.Rs) + bean.getCouponListBean().getMaxDiscountAmount());

            setScratchCard();
            binding.scratchView.setVisibility(View.GONE);
        }
    }

    private void setScratchCard() {
        binding.setIsScratched(true);
        binding.txtHeading.setText(getString(R.string.terms_and_conditions));
        if (bean.getRewardsBean() != null) {
            if (!TextUtils.isEmpty(bean.getRewardsBean().getTitle())) {
                binding.txtTitle.setVisibility(View.VISIBLE);
                binding.txtTitle.setText(bean.getRewardsBean().getTitle());
            } else
                binding.txtTitle.setVisibility(View.GONE);

            binding.txtAmount.setText((mContext.getString(R.string.you_get_rs_off_on_your_next_order)).replace("_", new DecimalFormat("0").format(bean.getRewardsBean().getCouponDiscountAmount())));
            binding.txtCouponCode.setText(bean.getRewardsBean().getCouponCode());
//            binding.txtValidity.setText(getString(R.string.valid_till) + " " + Utils.getFormetedDateTime(bean.getRewardsBean().get));
            if (!TextUtils.isEmpty(bean.getRewardsBean().getDescription()))
                binding.txtDesc.setText(bean.getRewardsBean().getDescription());
        } else {
            binding.txtValidity.setVisibility(View.VISIBLE);
            binding.txtTitle.setVisibility(View.VISIBLE);
            binding.txtTitle.setText(bean.getCouponListBean().getTitle());
            if (bean.getCouponListBean().getDiscountType().equalsIgnoreCase("FixedPercentage"))
                binding.txtAmount.setText((mContext.getString(R.string.you_get_off_on_your_next_order)).replace("_", (new DecimalFormat("0").format(bean.getCouponListBean().getDiscountValue()) + "%")));
            else
                binding.txtAmount.setText((mContext.getString(R.string.you_get_rs_off_on_your_next_order)).replace("_", new DecimalFormat("0").format(bean.getCouponListBean().getMaxDiscountAmount())));
            binding.txtCouponCode.setText(bean.getCouponListBean().getCouponCode());
            binding.txtValidity.setText(getString(R.string.valid_till) + " " + bean.getCouponListBean().getEndDate());
            if (!TextUtils.isEmpty(bean.getCouponListBean().getDescription()))
                binding.txtDesc.setText(bean.getCouponListBean().getDescription());
        }
    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent();
        setResult(RESULT_OK, intent);
        intent.putExtra("RewardsBean", bean);
        finishAfterTransition();
    }

    public void copyCode(View view){
        if (bean.getRewardsBean() != null)
            Utils.copyCode(mContext, bean.getRewardsBean().getCouponCode());
        else
            Utils.copyCode(mContext, bean.getCouponListBean().getCouponCode());
    }

    public void goToPreviousScreen(View view){
        onBackPressed();
    }

    private void showRewardsProcess() {
        Glide.with(this).asGif().load(R.raw.rewards).into(binding.imvWin1);
        Glide.with(this).asGif().load(R.raw.rewards).into(binding.imvWin2);

        binding.llWin.setVisibility(View.VISIBLE);

        int resID=getResources().getIdentifier("offers_tone", "raw", getPackageName());
        MediaPlayer mediaPlayer = MediaPlayer.create(this,resID);
        mediaPlayer.start();

        new Thread(new Runnable() {
            @Override
            public void run() {
                try{
                    Thread.sleep(4000);
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            binding.llWin.setVisibility(View.GONE);
                            setScratchCard();
                        }
                    });
                } catch (InterruptedException e) {
                    e.printStackTrace();

                }

            }
        }).start();
    }

    private void callScratchTheCardApi() {

        try {
            new NetworkManager(CouponListBean.class, new NetworkManager.OnCallback<CouponListBean>() {
                @Override
                public void onSuccess(boolean success, ResponseClass responseClass, int which) {
                    if (responseClass.isSuccess()){
                        CouponListBean couponListBean = (CouponListBean) responseClass.getResponsePacket();
                        bean.setCouponListBean(couponListBean);
                        bean.setRewardsBean(null);
                        showRewardsProcess();
                    } else {
                        Utils.showCenterToast(mContext, responseClass.getMessage());
                    }
                }

                @Override
                public void onFailure(boolean success, String response, int which) {
                    Utils.showCenterToast(mContext, getString(R.string.internet_connection_not_found) );
                }
            }).callAPIJson(mContext, Constants.VAL_GET, AppUrls.SCRATCH_THE_CARD + bean.getRewardsBean().getRecordId(), "", "Loading...", true, AppUrls.REQUESTTAG_SCRATCHTHECARD);

        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
    }

}
