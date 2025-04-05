package in.kpis.afoozo.activity;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.GridLayoutManager;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.kpis.afoozo.R;
import in.kpis.afoozo.adapter.BookNowItemAdapter;
import in.kpis.afoozo.bean.BookNowItemsBean;
import in.kpis.afoozo.bean.RestaurantRequestBean;
import in.kpis.afoozo.bean.StealDealCartBean;
import com.kpis.afoozo.databinding.ActivityBookNowBinding;
import in.kpis.afoozo.okhttpServcice.AppUrls;
import in.kpis.afoozo.okhttpServcice.NetworkManager;
import in.kpis.afoozo.okhttpServcice.response.ResponseClass;
import in.kpis.afoozo.sqlite.DatabaseHelper;
import in.kpis.afoozo.util.Constants;
import in.kpis.afoozo.util.Utils;
import in.kpis.afoozo.util.recycler_view_utilities.RecyclerItemClickListener;

import java.io.UnsupportedEncodingException;
import java.lang.reflect.Type;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class BookNowActi extends AppCompatActivity {

    private Toolbar toolbar;

    private Context mContext;

    private ActivityBookNowBinding binding;

    private String category;

    private BookNowItemAdapter mAdapter;
    private ArrayList<BookNowItemsBean> itemList;
    private String restaurantId;

    private int RESERVE_REQUEST = 101;
    private int totalItems;

    private long categoryId;

    private double totalPrice;

    private DatabaseHelper db;
    private ArrayList<StealDealCartBean> cartList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_book_now);

        if (getIntent().getExtras() != null){
            category = getIntent().getStringExtra("Category");
            restaurantId = getIntent().getStringExtra("RestaurantId");
            categoryId = getIntent().getLongExtra("CategoryId", 0);
        }

        mContext = BookNowActi.this;
        db = new DatabaseHelper(mContext);
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

        binding.toolbar.activityTitle.setText(category);

        binding.rvBookNow.setLayoutManager(new GridLayoutManager(mContext, 2));
        binding.rvBookNow.setItemAnimator(new DefaultItemAnimator());
        binding.rvBookNow.addOnItemTouchListener(new RecyclerItemClickListener(mContext, new RecyclerItemClickListener.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                Intent intent = new Intent(mContext, ReserveNowActi.class);
                intent.putExtra("Category", category);
                intent.putExtra("Item_ID", itemList.get(position).getRecordId());
                intent.putExtra("RestaurantId", restaurantId);
                startActivityForResult(intent, RESERVE_REQUEST);
            }
        }));


        callItemListApi();
    }

    private void checkCartItems() {
        cartList = db.getStealDealItems();
        totalItems = 0;
        totalPrice = 0;
        if (cartList != null && cartList.size() > 0){
            DateFormat dateFormat = new SimpleDateFormat("dd");
            Date date = new Date();
            if (cartList.get(0).getDate() != Integer.parseInt(dateFormat.format((date)))) {
                db.deleteAllStealDealItems();
            } else {
                for (StealDealCartBean sdcb: cartList)
                    totalPrice = totalPrice + (sdcb.getStealDealItemQty() * sdcb.getPrice());

                totalItems = cartList.size();
            }

        }
        setBottomPrice();
    }

    private void setBottomPrice() {
        if (totalItems > 0){
//            sheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);
            binding.llCart.llBottom.setVisibility(View.VISIBLE);

            if (totalItems == 1) {
                binding.llCart.txtTotalItems.setText(totalItems + " " + getString(R.string.total));
                binding.llCart.txtCartTotal.setText(getString(R.string.Rs) + totalPrice);
            } else {
                binding.llCart.txtTotalItems.setText(totalItems + " " + getString(R.string.items));
                binding.llCart.txtCartTotal.setText(getString(R.string.Rs) + totalPrice);
            }
        } else
            binding.llCart.llBottom.setVisibility(View.GONE);
    }

    @Override
    protected void onResume() {
        super.onResume();
        checkCartItems();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == RESERVE_REQUEST && resultCode == RESULT_OK)
            finish();
    }

    public void saveOrderProcess(View view){
        Utils.startActivity(mContext, StealDealCartActi.class);
    }

    private void setData() {
        mAdapter = new BookNowItemAdapter(mContext, itemList);
        binding.rvBookNow.setAdapter(mAdapter);
        mAdapter.notifyDataSetChanged();
    }

    private void callItemListApi() {
        RestaurantRequestBean bean = new RestaurantRequestBean();
        bean.setRestaurantId(restaurantId);
        bean.setCategoryId(categoryId);
        bean.setStart(0);
        bean.setLength(1000);
        bean.setSearchKey("");

        String json = new Gson().toJson(bean);

        try {
            new NetworkManager(ArrayList.class, new NetworkManager.OnCallback<BookNowItemsBean>() {
                @Override
                public void onSuccess(boolean success, ResponseClass responseClass, int which) {
                    if (responseClass.isSuccess()){
                        Type objType = new TypeToken<ResponseClass<ArrayList<BookNowItemsBean>>>() {
                        }.getType();
                        ResponseClass<ArrayList<BookNowItemsBean>> responseClass1 = new Gson().fromJson(responseClass.getCompletePacket(), objType);

                        itemList = responseClass1.getResponsePacket();

                        if (itemList != null && itemList.size() > 0)
                            setData();

                    } else {
                        Utils.showCenterToast(mContext, responseClass.getMessage());
                    }
                }

                @Override
                public void onFailure(boolean success, String response, int which) {
                    Utils.showCenterToast(mContext, getString(R.string.internet_connection_not_found) );
                }
            }).callAPIJson(mContext, Constants.VAL_POST, AppUrls.GET_STEAL_DEAL_ITEM_LIST, json, "Loading...", true, AppUrls.REQUESTTAG_GETSTEALDEALITEMLIST);

        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
    }
}
