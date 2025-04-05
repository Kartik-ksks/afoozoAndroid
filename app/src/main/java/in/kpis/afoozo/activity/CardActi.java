package in.kpis.afoozo.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.databinding.DataBindingUtil;

import com.kpis.afoozo.R;
import in.kpis.afoozo.bean.CardBean;
import com.kpis.afoozo.databinding.ActivityCardBinding;
import in.kpis.afoozo.util.Constants;
import in.kpis.afoozo.util.Utils;
import com.razorpay.Razorpay;


public class CardActi extends AppCompatActivity {

    private Toolbar toolbar;

    private Context mContext;

    private ActivityCardBinding binding;

    private int count;

    private String name;
    private String cardNo;
    private String cvv;
    private String year;
    private String month;
    private String selectedNickName = "";
    private String nickName;
    private boolean isActive;
    private String expiryDate;
    private Razorpay razorpay;
    private String cardNetwork;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_card);

        mContext = CardActi.this;
        initialize();
    }

    private void initialize() {
        razorpay = new Razorpay(this);
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(false);

        binding.btnAddCard.setEnabled(false);
        binding.btnAddCard.setBackgroundResource(R.drawable.grey_rounded_btn_bg);

        toolbar.setNavigationIcon(R.drawable.ic_back);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        binding.etCardNo.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (count <= binding.etCardNo.getText().toString().length()
                        &&(binding.etCardNo.getText().toString().length()==4
                        ||binding.etCardNo.getText().toString().length()==9
                        ||binding.etCardNo.getText().toString().length()==14)){
                    binding.etCardNo.setText(binding.etCardNo.getText().toString()+" ");
                    int pos = binding.etCardNo.getText().length();
                    binding.etCardNo.setSelection(pos);
                }else if (count >= binding.etCardNo.getText().toString().length()
                        &&(binding.etCardNo.getText().toString().length()==4
                        ||binding.etCardNo.getText().toString().length()==9
                        ||binding.etCardNo.getText().toString().length()==14)){
                    binding.etCardNo.setText(binding.etCardNo.getText().toString().substring(0,binding.etCardNo.getText().toString().length()-1));
                    int pos = binding.etCardNo.getText().length();
                    binding.etCardNo.setSelection(pos);
                }
                count = binding.etCardNo.getText().toString().length();
                if(binding.etCardNo.getText().toString().length() == 19){
                    cardNetwork = razorpay.getCardNetwork(binding.etCardNo.getText().toString().trim());
                    String cardNumber = binding.etCardNo.getText().toString().replace(" ", "");
                    boolean isCardValid = razorpay.isValidCardNumber(cardNumber);
                    if(cardNetwork.equals("visa")){
                        binding.etCardNo.setCompoundDrawablesWithIntrinsicBounds(0,0,R.drawable.ic_visa,0);
                    }else if(cardNetwork.equals("mastercard")){
                        binding.etCardNo.setCompoundDrawablesWithIntrinsicBounds(0,0,R.drawable.ic_master_card,0);
                    }else if(cardNetwork.equals("diners")){
                        binding.etCardNo.setCompoundDrawablesWithIntrinsicBounds(0,0,R.drawable.ic_diners_card,0);
                    }else if(cardNetwork.equals("amex")){
                        binding.etCardNo.setCompoundDrawablesWithIntrinsicBounds(0,0,R.drawable.ic_amex,0);
                    }else if(cardNetwork.equals("jcb")){
                        binding.etCardNo.setCompoundDrawablesWithIntrinsicBounds(0,0,R.drawable.ic_jcb,0);
                    }else{
                        binding.etCardNo.setCompoundDrawablesWithIntrinsicBounds(0,0,R.drawable.ic_card,0);

                    }
                    if(isCardValid) {
                        binding.btnAddCard.setEnabled(true);
                        binding.btnAddCard.setBackgroundResource(R.drawable.yellow_rounded_btn_bg);

                    }else{
                        binding.btnAddCard.setBackgroundResource(R.drawable.grey_rounded_btn_bg);
                    }
                }else{
                    binding.btnAddCard.setEnabled(false);
                    binding.btnAddCard.setBackgroundResource(R.drawable.grey_rounded_btn_bg);
                    binding.etCardNo.setCompoundDrawablesWithIntrinsicBounds(0,0,R.drawable.ic_card,0);
                }
            }
        });

        binding.etExpiryDate.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (start == 1 && before == 0) {
                    binding.etExpiryDate.setText(s + "/");
                    binding.etExpiryDate.setSelection(3);
                } else if (start == 2 && before == 1) {
                    binding.etExpiryDate.setText("" + s.charAt(0));
                    binding.etExpiryDate.setSelection(1);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
                count = binding.etExpiryDate.getText().length();
                String date = binding.etExpiryDate.getText().toString();
                if (count == 1) {
                    if (Integer.parseInt(date) > 1) {
                        binding.etExpiryDate.setText("0" + date + "/");
                        binding.etExpiryDate.setSelection(3);
                    }
                } else if (count >= 2){
                    if (Integer.parseInt(date.substring(0,2)) > 12)
                        binding.tilExpiry.setError("Invalid Data");
                    else
                        binding.tilExpiry.setError(null);
                }
            }
        });

        binding.toolbar.activityTitle.setText(getString(R.string.add_a_card));
        atFirstTimePersonalSelected();
    }
    private void atFirstTimePersonalSelected(){
        selectedNickName = getString(R.string.personal);
        binding.txtPersonal.setBackground(getResources().getDrawable(R.drawable.color_primary_rounded_bg));
        binding.txtBusiness.setBackground(getResources().getDrawable(R.drawable.color_primary_rounded_border));
        binding.txtOther.setBackground(getResources().getDrawable(R.drawable.color_primary_rounded_border));

        binding.txtPersonal.setTextColor(getResources().getColor(R.color.white));
        binding.txtBusiness.setTextColor(getResources().getColor(R.color.colorPrimary));
        binding.txtOther.setTextColor(getResources().getColor(R.color.colorPrimary));

        binding.tilNickName.setVisibility(View.GONE);
    }

    public void setPersonal(View view){
        selectedNickName = getString(R.string.personal);
        binding.txtPersonal.setBackground(getResources().getDrawable(R.drawable.color_primary_rounded_bg));
        binding.txtBusiness.setBackground(getResources().getDrawable(R.drawable.color_primary_rounded_border));
        binding.txtOther.setBackground(getResources().getDrawable(R.drawable.color_primary_rounded_border));

        binding.txtPersonal.setTextColor(getResources().getColor(R.color.white));
        binding.txtBusiness.setTextColor(getResources().getColor(R.color.colorPrimary));
        binding.txtOther.setTextColor(getResources().getColor(R.color.colorPrimary));

        binding.tilNickName.setVisibility(View.GONE);
    }

    public void setBusiness(View view){
        selectedNickName = getString(R.string.business);
        binding.txtPersonal.setBackground(getResources().getDrawable(R.drawable.color_primary_rounded_border));
        binding.txtBusiness.setBackground(getResources().getDrawable(R.drawable.color_primary_rounded_bg));
        binding.txtOther.setBackground(getResources().getDrawable(R.drawable.color_primary_rounded_border));

        binding.txtPersonal.setTextColor(getResources().getColor(R.color.colorPrimary));
        binding.txtBusiness.setTextColor(getResources().getColor(R.color.white));
        binding.txtOther.setTextColor(getResources().getColor(R.color.colorPrimary));

        binding.tilNickName.setVisibility(View.GONE);
    }

    public void setOther(View view){
        selectedNickName = getString(R.string.other);
        binding.txtPersonal.setBackground(getResources().getDrawable(R.drawable.color_primary_rounded_border));
        binding.txtBusiness.setBackground(getResources().getDrawable(R.drawable.color_primary_rounded_border));
        binding.txtOther.setBackground(getResources().getDrawable(R.drawable.color_primary_rounded_bg));

        binding.txtPersonal.setTextColor(getResources().getColor(R.color.colorPrimary));
        binding.txtBusiness.setTextColor(getResources().getColor(R.color.colorPrimary));
        binding.txtOther.setTextColor(getResources().getColor(R.color.white));

        binding.tilNickName.setVisibility(View.VISIBLE);
    }

    public void cardSaveProcess(View view){
        name = binding.etName.getText().toString();
        cardNo = binding.etCardNo.getText().toString();
        expiryDate = binding.etExpiryDate.getText().toString();
        cvv = binding.etCVV.getText().toString();
        if (selectedNickName.equals(getString(R.string.personal)))
            nickName = getString(R.string.personal);
        else if (selectedNickName.equals(getString(R.string.business)))
            nickName = getString(R.string.business);
        else
            nickName = binding.etNickName.getText().toString();



        if (Utils.isValidName(mContext, name) && Utils.isValidCardNo(mContext, cardNo) && Utils.isValidExpiryDate(mContext, expiryDate) && Utils.isValidCVV(mContext, cvv)&& Utils.isValidNickName(mContext, nickName)){
            month = (binding.etExpiryDate.getText().toString()).substring(0, 2);
            year = (binding.etExpiryDate.getText().toString()).substring(3, 5);
            CardBean bean = new CardBean(name, cardNo, month, year, cvv, nickName, cardNetwork);
            Intent intent = new Intent();
            intent.putExtra(Constants.CARD_DATA, bean);
            setResult(RESULT_OK, intent);
            finish();
        }

    }
}
