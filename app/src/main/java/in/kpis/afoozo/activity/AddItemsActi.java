package in.kpis.afoozo.activity;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.CompoundButton;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.appcompat.widget.Toolbar;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.kpis.afoozo.R;
import com.kpis.afoozo.databinding.ActivityAddItemsBinding;
import com.kpis.afoozo.databinding.InstructionPopupLayoutBinding;

import java.io.UnsupportedEncodingException;
import java.lang.reflect.Type;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;

import in.kpis.afoozo.AppInitialization;
import in.kpis.afoozo.adapter.CustomizationAdapter;
import in.kpis.afoozo.adapter.MenuAdapter;
import in.kpis.afoozo.adapter.MenuTabAdapter;
import in.kpis.afoozo.bean.CustomizationBean;
import in.kpis.afoozo.bean.CustomizationOptionsBean;
import in.kpis.afoozo.bean.LocalCartBean;
import in.kpis.afoozo.bean.MenuBean;
import in.kpis.afoozo.bean.MenuListBean;
import in.kpis.afoozo.bean.MenuTabBean;
import in.kpis.afoozo.bean.RestaurantRequestBean;
import in.kpis.afoozo.bean.SaveItemsBean;
import in.kpis.afoozo.bean.SaveOrderBean;
import in.kpis.afoozo.bean.ScanQrBean;
import in.kpis.afoozo.interfaces.AddRemoveClick;
import in.kpis.afoozo.interfaces.CustomizationClick;
import in.kpis.afoozo.okhttpServcice.AppUrls;
import in.kpis.afoozo.okhttpServcice.NetworkManager;
import in.kpis.afoozo.okhttpServcice.response.ResponseClass;
import in.kpis.afoozo.sqlite.DatabaseHelper;
import in.kpis.afoozo.util.Constants;
import in.kpis.afoozo.util.Utils;
import in.kpis.afoozo.util.recycler_view_utilities.RecyclerItemClickListener;

public class AddItemsActi extends AppCompatActivity implements AddRemoveClick, View.OnClickListener, CustomizationClick, Filterable {

    private DatabaseHelper db;
    private Toolbar toolbar;
    private Context mContext;
    private BottomSheetBehavior sheetBehavior;
    private ActivityAddItemsBinding binding;
    private MenuBean menuBean;
    private ArrayList<MenuBean> menuList = new ArrayList<>();
    public ArrayList<MenuBean> vegOnlyList;
    private ArrayList<MenuBean> filterList;
    private ArrayList<MenuTabBean> categoryList = new ArrayList<>();
    private MenuTabAdapter tabAdapter;
    private MenuAdapter mAdapter;
    private boolean isSameRestaurant;
    private boolean isOpen;
    private int scrollPositon;
    private int oldPosition;
    private int catPosition;
    private int itemPosition;
    private int totalItems;
    private double itemPrice;
    private double totalAmount;
    private String restaurantId;
    private String fromWhich;
    private Dialog alert;
    private ArrayList<CustomizationBean> custList = new ArrayList<>();
    private CustomizationAdapter customizationAdapter;
    private ScanQrBean scanQrBean;
    private AddressBean addressBean;
    private boolean isVegOnly;
    private boolean isNonVegOnly;
    public String restaurantName = "";
    private int minmumAmount;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_add_items);
        if (getIntent().getExtras() != null) {
            fromWhich = getIntent().getStringExtra(Constants.FROM_WHICH);
            if (fromWhich.equals(getString(R.string.dine_in)) || fromWhich.equals(getString(R.string.cafe))){
                scanQrBean = (ScanQrBean) getIntent().getSerializableExtra(Constants.SCAN_QR_BEAN);
                restaurantId = scanQrBean.getRestaurantUuid();
                isOpen = true;
                restaurantName = scanQrBean.getRestaurantName();
            }else if (fromWhich.equals(getString(R.string.delivery))) {
                restaurantId = getIntent().getStringExtra(Constants.RESTAURANT_ID);
                addressBean = (AddressBean) getIntent().getSerializableExtra(Constants.ADDRESS_BEAN);
                isOpen = getIntent().getBooleanExtra(Constants.IS_OPEN, false);
                restaurantName = getIntent().getStringExtra(Constants.RESTAURANT_NAME);
                minmumAmount = getIntent().getIntExtra(Constants.DELIVERY_MIN_ORDER_VALUE, 0);

            } else {
                restaurantId = getIntent().getStringExtra(Constants.RESTAURANT_ID);
                isOpen = getIntent().getBooleanExtra(Constants.IS_OPEN, false);
                restaurantName = getIntent().getStringExtra(Constants.RESTAURANT_NAME);
            }
        }

        mContext = AddItemsActi.this;
        db = new DatabaseHelper(mContext);
        initialize();
    }

    @Override
    protected void onResume() {
        super.onResume();
        totalAmount = 0;
        totalItems = 0;
        callGetRestaurantApi();
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
//        if(isOpen){
            binding.llAllLayout.setVisibility(View.VISIBLE);
            binding.txtNotOpen.setVisibility(View.GONE);

//        }else {
//            binding.llAllLayout.setVisibility(View.GONE);
//            binding.txtNotOpen.setVisibility(View.VISIBLE);
//            binding.llBottom.setVisibility(View.GONE);
//        }

        binding.toolbar.activityTitle.setText(getString(R.string.menu));

        sheetBehavior = BottomSheetBehavior.from(binding.bottomSheet.llBottomSheet);

        binding.bottomSheet.rvBottomSheet.setLayoutManager(new LinearLayoutManager(mContext));
        binding.bottomSheet.rvBottomSheet.setItemAnimator(new DefaultItemAnimator());

        binding.bottomSheet.btnChoose.setOnClickListener(this);
        binding.bottomSheet.btnRepeat.setOnClickListener(this);

        binding.rvCategory.setLayoutManager(new LinearLayoutManager(mContext, LinearLayoutManager.HORIZONTAL, false));
        binding.rvCategory.setItemAnimator(new DefaultItemAnimator());

        final LinearLayoutManager menuManager = new LinearLayoutManager(mContext);
        binding.rvMenu.setLayoutManager(menuManager);
        binding.rvMenu.setItemAnimator(new DefaultItemAnimator());
        binding.rvMenu.addOnScrollListener(onScrollListener);

        binding.rvCategory.addOnItemTouchListener(new RecyclerItemClickListener(mContext, new RecyclerItemClickListener.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                if (mAdapter != null)
//                    binding.rvMenu.getLayoutManager().scrollToPosition(position);
                    menuManager.scrollToPositionWithOffset(position, 0);
            }
        }));

        binding.searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
//                if (mAdapter != null && list != null && list.size() > 0 )
//                    mAdapter.getFilter().filter(query);
                if (mAdapter != null && menuList != null && menuList.size() > 0)
                    customCleverSearchedEvent(query);
                getFilter().filter(query);
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
//                if (mAdapter != null && list != null && list.size() > 0 )
//                    mAdapter.getFilter().filter(newText);
                if (mAdapter != null && menuList != null && menuList.size() > 0)
                    customCleverSearchedEvent(newText);

                getFilter().filter(newText);
                return false;
            }
        });

        binding.shimmerRv.startShimmer();

        binding.switchVeg.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                Utils.progressDialog(mContext, "");
                if(isChecked== true) {
                    binding.switchVeg.setChecked(true);
                    binding.switchNonVeg.setChecked(false);
                }
                isVegOnly = isChecked;
                isNonVegOnly = false;
                filterVegOnly(isChecked, false);
            }
        });

        binding.switchNonVeg.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                Utils.progressDialog(mContext, "");
                if(isChecked== true) {
                    binding.switchNonVeg.setChecked(true);
                    binding.switchVeg.setChecked(false);
                }
                isVegOnly = false;
                isNonVegOnly =true;
                filterVegOnly(false, isChecked);
            }
        });
    }

    private void filterVegOnly(boolean isChecked, boolean nogVeg) {

        if (!isChecked && !nogVeg)
            filterList = menuList;
        else if(!isChecked && nogVeg) {
            ArrayList<MenuBean> filteredList = new ArrayList<>();
            for (MenuBean row : menuList) {

                // name match condition. this might differ depending on your requirement
                // here we are looking for name or phone number match
                MenuBean mainBean = new MenuBean();
                boolean isItemFound = false;
                if (row.getMenuList() != null && row.getMenuList().size() > 0) {
                    ArrayList<MenuListBean> itemList = new ArrayList<>();
                    for (MenuListBean ib : row.getMenuList()) {
                        if (ib.getVegNonVeg().equalsIgnoreCase("NonVeg")){
                            itemList.add(ib);
                        }
                    }
                    if (itemList.size() > 0) {
                        mainBean.setCategoryName(row.getCategoryName());
                        mainBean.setCategoryUuid(row.getCategoryUuid());
                        mainBean.setCategoryViewType(row.getCategoryViewType());
                        mainBean.setMenuList(itemList);
                        isItemFound = true;
                    }
                }

                if (row.getSubCategoryList() != null && row.getSubCategoryList().size() > 0){
                    ArrayList<MenuBean> subCatList = new ArrayList<>();
                    for (MenuBean subRow: row.getSubCategoryList()){
                        MenuBean subMenuBean = new MenuBean();
                        if (subRow.getMenuList() != null && subRow.getMenuList().size() > 0) {
                            ArrayList<MenuListBean> subItemList = new ArrayList<>();
                            for (MenuListBean ib : subRow.getMenuList()) {
                                if (ib.getVegNonVeg().equalsIgnoreCase("NonVeg")) {
                                    subItemList.add(ib);
                                }
                            }
                            if (subItemList.size() > 0) {
                                subMenuBean.setCategoryName(subRow.getCategoryName());
                                subMenuBean.setCategoryUuid(subRow.getCategoryUuid());
                                subMenuBean.setCategoryViewType(subRow.getCategoryViewType());
                                subMenuBean.setMenuList(subItemList);
                                isItemFound = true;
                                subCatList.add(subMenuBean);
                            }
                        }
                    }
                    if (subCatList.size() > 0) {
                        if (mainBean.getMenuList() == null) {
                            mainBean.setCategoryName(row.getCategoryName());
                            mainBean.setCategoryUuid(row.getCategoryUuid());
                            mainBean.setCategoryViewType(row.getCategoryViewType());
                        }
                        mainBean.setSubCategoryList(subCatList);
                    }
                }
                if (isItemFound)
                    filteredList.add(mainBean);

            }
            filterList = filteredList;
            vegOnlyList = filteredList;
        }
        else {
            ArrayList<MenuBean> filteredList = new ArrayList<>();
            for (MenuBean row : menuList) {

                // name match condition. this might differ depending on your requirement
                // here we are looking for name or phone number match
                MenuBean mainBean = new MenuBean();
                boolean isItemFound = false;
                if (row.getMenuList() != null && row.getMenuList().size() > 0) {
                    ArrayList<MenuListBean> itemList = new ArrayList<>();
                    for (MenuListBean ib : row.getMenuList()) {
                        if (!ib.getVegNonVeg().equalsIgnoreCase("NonVeg")){
                            itemList.add(ib);
                        }
                    }
                    if (itemList.size() > 0) {
                        mainBean.setCategoryName(row.getCategoryName());
                        mainBean.setCategoryUuid(row.getCategoryUuid());
                        mainBean.setCategoryViewType(row.getCategoryViewType());
                        mainBean.setMenuList(itemList);
                        isItemFound = true;
                    }
                }

                if (row.getSubCategoryList() != null && row.getSubCategoryList().size() > 0){
                    ArrayList<MenuBean> subCatList = new ArrayList<>();
                    for (MenuBean subRow: row.getSubCategoryList()){
                        MenuBean subMenuBean = new MenuBean();
                        if (subRow.getMenuList() != null && subRow.getMenuList().size() > 0) {
                            ArrayList<MenuListBean> subItemList = new ArrayList<>();
                            for (MenuListBean ib : subRow.getMenuList()) {
                                if (!ib.getVegNonVeg().equalsIgnoreCase("NonVeg")) {
                                    subItemList.add(ib);
                                }
                            }
                            if (subItemList.size() > 0) {
                                subMenuBean.setCategoryName(subRow.getCategoryName());
                                subMenuBean.setCategoryUuid(subRow.getCategoryUuid());
                                subMenuBean.setCategoryViewType(subRow.getCategoryViewType());
                                subMenuBean.setMenuList(subItemList);
                                isItemFound = true;
                                subCatList.add(subMenuBean);
                            }
                        }
                    }
                    if (subCatList.size() > 0) {
                        if (mainBean.getMenuList() == null) {
                            mainBean.setCategoryName(row.getCategoryName());
                            mainBean.setCategoryUuid(row.getCategoryUuid());
                            mainBean.setCategoryViewType(row.getCategoryViewType());
                        }
                        mainBean.setSubCategoryList(subCatList);
                    }
                }
                if (isItemFound)
                    filteredList.add(mainBean);

            }
            filterList = filteredList;
            vegOnlyList = filteredList;
        }
        Utils.dismissProgressDialog();
        setData();

    }



    public void hideBottomSheet(View view){
        toggleBottomSheet();
    }

    private void toggleBottomSheet() {
        if (sheetBehavior.getState() != BottomSheetBehavior.STATE_EXPANDED) {
            sheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);
        } else {
            sheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
        }
    }

    public void addCustomizationClick(View view){
        if (isSameRestaurant)
            addCustomizableItem();
        else {
            Utils.retryAlertDialog(mContext, getString(R.string.app_name), getString(R.string.you_already_added_some_items), getString(R.string.cancel), getString(R.string.Ok), new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    db.deleteAllItems();
                    isSameRestaurant = true;
                    addCustomizableItem();
                    Utils.dismissRetryAlert();
                }
            });
        }
    }

    @Override
    public void onClick(View v) {

    }


    @Override
    public void addClick(boolean isSubCat, int catPosition, int subCatPosition, int itemPosition) {
        if(isOpen) {
            this.catPosition = catPosition;
            this.itemPosition = itemPosition;

            if (filterList.get(catPosition).getMenuList().get(itemPosition).getCustomization() != null) {
                showCustomizations();
            } else {
                if (isSameRestaurant)
                    addItemProcess();
                else {
                    Utils.retryAlertDialog(mContext, getString(R.string.app_name), getString(R.string.you_already_added_some_items), getString(R.string.cancel), getString(R.string.Ok), new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            db.deleteAllItems();
                            isSameRestaurant = true;
                            addItemProcess();
                            Utils.dismissRetryAlert();
                        }
                    });
                }
            }
        }else
            Utils.retryAlertDialog(mContext, getString(R.string.app_name), getString(R.string.restaurant_closed_msg), getString(R.string.Ok), "", null);


    }

    @Override
    public void removeClick(boolean isSubCat, int catPosition, int subCatPosition, int itemPosition) {
        this.catPosition = catPosition;
        this.itemPosition = itemPosition;

        if (filterList.get(catPosition).getMenuList().get(itemPosition).getCustomization() != null){
            Utils.showCenterToast(mContext, getString(R.string.go_to_cart_to_remove_cust_item));
        } else {
            MenuListBean bean = filterList.get(catPosition).getMenuList().get(itemPosition);
            int qty = bean.getQuantity();
            if (qty > 1) {
                boolean isUpdate = db.updateItemQty(--qty, bean.getItemId());
            } else {
                db.deleteItem(bean.getItemId());
                --qty;
                --totalItems;
            }

            totalAmount = totalAmount - bean.getPrice();
            filterList.get(catPosition).getMenuList().get(itemPosition).setQuantity(qty);

            mAdapter.notifyDataSetChanged();
            setBottomPrice();
        }
    }

    @Override
    public void editClick(boolean isSubcategory, int catPosition, int subCatPosition, int itemPosition) {
        if(isOpen){
        this.catPosition = catPosition;
        this.itemPosition = itemPosition;
        showInstructionPopup();
    }else
            Utils.retryAlertDialog(mContext, getString(R.string.app_name), getString(R.string.restaurant_closed_msg), getString(R.string.Ok), "", null);

}

    @Override
    public void stickyClick(int position) {

    }

    @Override
    public void scrollCallback() {

    }

    private void showInstructionPopup () {
        if(isOpen){
        alert = new Dialog(mContext);
        alert.requestWindowFeature(Window.FEATURE_NO_TITLE);
        alert.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        Window dialogWindow = alert.getWindow();
        WindowManager.LayoutParams lp = dialogWindow.getAttributes();
        dialogWindow.setGravity(Gravity.CENTER);

        lp.width = WindowManager.LayoutParams.MATCH_PARENT;

        dialogWindow.setAttributes(lp);

        InstructionPopupLayoutBinding successBinding = DataBindingUtil.inflate(LayoutInflater.from(mContext), R.layout.instruction_popup_layout, null, false);
        alert.setContentView(successBinding.getRoot());

//        alert.setContentView(R.layout.buy_popup_layout);

        successBinding.etPopInst.setText(db.getInstruction(filterList.get(catPosition).getMenuList().get(itemPosition).getItemId()));

        successBinding.btnPopAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                db.updateInstruction(successBinding.etPopInst.getText().toString(), filterList.get(catPosition).getMenuList().get(itemPosition).getItemId());
                alert.dismiss();
            }
        });


        alert.getWindow().getAttributes().windowAnimations = R.style.Animation_Dialog;
        alert.setCancelable(true);
        alert.show();
        }else
            Utils.retryAlertDialog(mContext, getString(R.string.app_name), getString(R.string.restaurant_closed_msg), getString(R.string.Ok), "", null);

    }


    @Override
    public void onRadioButtonClick(int catPosition, int itemPosition) {
        ArrayList<CustomizationOptionsBean> itemList = custList.get(catPosition).getCustomizationOptions();

        for (int i = 0; i < itemList.size(); i++) {

            if (i == itemPosition) {
                if (!itemList.get(i).isAdded()) {
                    itemList.get(i).setAdded(true);
                    itemPrice = itemPrice + itemList.get(i).getPrice();
                } else {
                    itemList.get(i).setAdded(false);
                    itemPrice = itemPrice - itemList.get(i).getPrice();
                }
            } else {
                if (itemList.get(i).isAdded()) {
                    itemList.get(i).setAdded(false);
                    itemPrice = itemPrice - itemList.get(i).getPrice();
                }
            }
        }

        custList.get(catPosition).setCustomizationOptions(itemList);
        customizationAdapter.notifyItemChanged(catPosition);

        setCustomizationPrice();
    }

    @Override
    public void onCheckBoxClick(int catPosition, int itemPosition) {
        ArrayList<CustomizationOptionsBean> itemList = custList.get(catPosition).getCustomizationOptions();
        int count = 0;
        for(CustomizationOptionsBean bean : itemList){
            if(bean.isAdded()){
                count++;
            }
        }
        if(custList.get(catPosition).getCustomizationType().equalsIgnoreCase("MultiSelection")) {
            if (custList.get(catPosition).getMinSelection() > 0) {
                if (custList.get(catPosition).getMinSelection() <= count) {
                    itemList.get(itemPosition).setAdded(false);
                    itemPrice = itemPrice - itemList.get(itemPosition).getPrice();
                    Toast.makeText(mContext, "Please add " + custList.get(catPosition).getMinSelection() + " items", Toast.LENGTH_SHORT).show();
                    return;
                }
            }
        }

        if (itemList.get(itemPosition).isAdded()) {
            itemList.get(itemPosition).setAdded(false);
            itemPrice = itemPrice - itemList.get(itemPosition).getPrice();
        } else {
            itemList.get(itemPosition).setAdded(true);
            itemPrice = itemPrice + itemList.get(itemPosition).getPrice();
        }


        custList.get(catPosition).setCustomizationOptions(itemList);
        customizationAdapter.notifyItemChanged(catPosition);

        setCustomizationPrice();
    }

    /**
     * This method is used for add non customizable items in local cart
     */
    private void addItemProcess() {
        MenuListBean bean = filterList.get(catPosition).getMenuList().get(itemPosition);
        int qty = bean.getQuantity();
        if (qty == 0) {
            db.insertCartItem(restaurantId, filterList.get(catPosition).getCategoryUuid(), bean.getItemId(), ++qty, bean.getPrice(), "", null);
            ++totalItems;
        } else {
            boolean isUpdate = db.updateItemQty(++qty, bean.getItemId());
        }

        totalAmount = totalAmount + bean.getPrice();
        filterList.get(catPosition).getMenuList().get(itemPosition).setQuantity(qty);

        mAdapter.notifyDataSetChanged();
        setBottomPrice();
        customCleverItemAddEvent(bean, filterList.get(catPosition).getCategoryName());

    }

    /**
     * Method for set total price and total items and show in bottom
     */
    private void setBottomPrice() {
        if (totalItems > 0){
//            sheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);
            binding.llBottom.setVisibility(View.VISIBLE);

            if (totalItems == 1) {
                binding.txtTotalItems.setText(totalItems + " " + getString(R.string.total));
                binding.txtCartTotal.setText(getString(R.string.Rs) + totalAmount);
            } else {
                binding.txtTotalItems.setText(totalItems + " " + getString(R.string.items));
                binding.txtCartTotal.setText(getString(R.string.Rs) + totalAmount);
            }
        } else
            binding.llBottom.setVisibility(View.GONE);
    }

    public void showCustomizations() {

//        if (list.get(position).getLastCustomization() != null && list.get(position).getLastCustomization().size() > 0)
//            showRepeatLayout();
//        else
        showChooseNewLayout();

        toggleBottomSheet();

    }

    private void showChooseNewLayout() {
        binding.bottomSheet.llChooseCustom.setVisibility(View.VISIBLE);
        binding.bottomSheet.llRepeat.setVisibility(View.GONE);

        ArrayList<CustomizationBean> cl = null;

        cl = filterList.get(catPosition).getMenuList().get(itemPosition).getCustomization();
        custList = filterList.get(catPosition).getMenuList().get(itemPosition).getCustomization();
        binding.bottomSheet.txtTitleBS.setText(filterList.get(catPosition).getMenuList().get(itemPosition).getTitle());
        itemPrice = filterList.get(catPosition).getMenuList().get(itemPosition).getPrice();
        if (!TextUtils.isEmpty(filterList.get(catPosition).getMenuList().get(itemPosition).getVegNonVeg()) && filterList.get(catPosition).getMenuList().get(itemPosition).getVegNonVeg().equalsIgnoreCase("Veg")) {
            binding.bottomSheet.imvVegNonVegBS.setImageResource(R.drawable.ic_veg);
            binding.bottomSheet.imvVegNonVegBS.setVisibility(View.VISIBLE);
        } else if (!TextUtils.isEmpty(filterList.get(catPosition).getMenuList().get(itemPosition).getVegNonVeg()) && filterList.get(catPosition).getMenuList().get(itemPosition).getVegNonVeg().equalsIgnoreCase("NonVeg")) {
            binding.bottomSheet.imvVegNonVegBS.setImageResource(R.drawable.ic_non_veg_icon);
            binding.bottomSheet.imvVegNonVegBS.setVisibility(View.VISIBLE);
        } else{
            binding.bottomSheet.imvVegNonVegBS.setVisibility(View.GONE);
        }



        for (CustomizationBean custBean: cl){
            ArrayList<CustomizationOptionsBean> opList = custBean.getCustomizationOptions();
            if (opList != null && opList.size() > 0){
                for (CustomizationOptionsBean opBean: opList){
                    if (opBean.isAdded())
                        itemPrice = itemPrice + opBean.getPrice();
                }
            }
        }

        setCustomizationPrice();

        if (custList != null && custList.size() > 0) {
            customizationAdapter = new CustomizationAdapter(mContext, custList, this);
            binding.bottomSheet.rvBottomSheet.setAdapter(customizationAdapter);
            mAdapter.notifyDataSetChanged();
        }
    }

    private void setCustomizationPrice() {
        binding.bottomSheet.txtBottomPrice.setText(getString(R.string.item_total) + " " + getString(R.string.Rs) + new DecimalFormat("#.00").format(itemPrice));
    }

    private void showRepeatLayout() {
        binding.bottomSheet.llChooseCustom.setVisibility(View.GONE);
        binding.bottomSheet.llRepeat.setVisibility(View.VISIBLE);

        itemPrice = filterList.get(catPosition).getMenuList().get(itemPosition).getPrice();

        String lastCustom = "";

//        for (CustomItemsBean opBean: list.get(position).getLastCustomization()){
//            itemPrice = itemPrice + opBean.getPrice();
//            if (TextUtils.isEmpty(lastCustom))
//                lastCustom = opBean.getName();
//            else
//                lastCustom = ", " + opBean.getName();
//        }

        binding.bottomSheet.txtPrevCust.setText(lastCustom);
    }

    private void repeatProcess() {
        toggleBottomSheet();

//        AddCustomeBean bean = new AddCustomeBean();
//        bean.setProductId(list.get(position).getRecordId());
//        bean.setCartType("Restaurant");
//        bean.setItemCount("Add");
//
//        bean.setCustomMenuList(list.get(position).getLastCustomization());
//        String json = new Gson().toJson(bean);
//        callAddCustomizeItemApi(json);
    }

//    public void addCustomizableItem(View view){
//        toggleBottomSheet();
//        selectedCustList = new ArrayList<>();
//
//        AddCustomeBean bean = new AddCustomeBean();
////        bean.setRestaurantId(restaurantId);
//        if (isSubCategory) {
//            bean.setProductId(categoryList.get(catPosition).getSubCategory().get(subCatPosition).getItemList().get(position).getItemId());
//
//            for (CustomizationBean cb : categoryList.get(catPosition).getSubCategory().get(subCatPosition).getItemList().get(position).getCustomizations()) {
//                for (CustomItemsBean cmb : cb.getCustomMenuList()) {
//                    if (cmb.isAdded())
//                        selectedCustList.add(cmb);
//                }
//            }
//
//        } else {
//            bean.setProductId(categoryList.get(catPosition).getItems().get(position).getItemId());
//
//            for (CustomizationBean cb : list.get(position).getCustomizations()) {
//                for (CustomItemsBean cmb : cb.getCustomMenuList()) {
//                    if (cmb.isAdded())
//                        selectedCustList.add(cmb);
//                }
//            }
//        }
//        bean.setCartType("Restaurant");
//        bean.setItemCount("Add");
//        bean.setCustomMenuList(selectedCustList);
//        String json = new Gson().toJson(bean);
//
//        callAddCustomizeItemApi(json);
//    }

    /**
     * This method is used for add customizable items in local database
     */
    private void addCustomizableItem(){
        if(isOpen) {
            toggleBottomSheet();
//        bean.setRestaurantId(restaurantId);

//        int count = 0;
//        for(CustomizationOptionsBean bean : itemList){
//            if(bean.isAdded()){
//                count++;
//            }
//        }
////        if(custList.get(catPosition).getCustomizationType().equalsIgnoreCase("MultiSelection")) {
//            if (custList.get(catPosition).getMinSelection() > 0) {
//                if (custList.get(catPosition).getMinSelection() <= count) {
//                    itemList.get(itemPosition).setAdded(false);
//                    itemPrice = itemPrice - itemList.get(itemPosition).getPrice();
//                    Toast.makeText(mContext, "Please add " + custList.get(catPosition).getMinSelection() + " items", Toast.LENGTH_SHORT).show();
//                    return;
//                }
//            }
//        }

            ArrayList<CustomizationBean> list = new ArrayList<>();
            for (CustomizationBean cb : custList) {
                ArrayList<CustomizationOptionsBean> optionsList = new ArrayList<>();
                for (CustomizationOptionsBean cmb : cb.getCustomizationOptions()) {
                    if (cmb.isAdded())
                        optionsList.add(cmb);
                }
                if (cb.getCustomizationType().equalsIgnoreCase("MultiSelection")) {
                    if (cb.getMinSelection() > 0) {
                        if (cb.getMinSelection() != optionsList.size()) {
                            Toast.makeText(mContext, "Please add " + cb.getMinSelection() + " items", Toast.LENGTH_SHORT).show();
                            toggleBottomSheet();
                            return;

                        }
                    }
                }

                if (optionsList.size() > 0) {
                    CustomizationBean bean = new CustomizationBean();
                    bean.setRecordId(cb.getRecordId());
                    bean.setCustomizationOptions(optionsList);
                    list.add(bean);
                }
            }

            boolean isNew = false;
            int qty = filterList.get(catPosition).getMenuList().get(itemPosition).getQuantity();
            if (qty == 0) {
                isNew = true;
                db.insertCartItem(restaurantId, filterList.get(catPosition).getCategoryUuid(), filterList.get(catPosition).getMenuList().get(itemPosition).getItemId(), 1, itemPrice, "", list);
            } else
                isNew = db.checkUpdateItem(restaurantId, filterList.get(catPosition).getCategoryUuid(), filterList.get(catPosition).getMenuList().get(itemPosition).getItemId(), itemPrice, "", list);

            filterList.get(catPosition).getMenuList().get(itemPosition).setQuantity(++qty);

            if (isNew)
                totalItems = totalItems + 1;
            totalAmount = totalAmount + itemPrice;

            mAdapter.notifyDataSetChanged();
            setBottomPrice();
        }else
            Utils.retryAlertDialog(mContext, getString(R.string.app_name), getString(R.string.restaurant_closed_msg), getString(R.string.Ok), "", null);

    }


    private RecyclerView.OnScrollListener onScrollListener = new RecyclerView.OnScrollListener() {
        @Override
        public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
            super.onScrollStateChanged(recyclerView, newState);
        }
        @Override
        public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
            super.onScrolled(recyclerView, dx, dy);
            if (((LinearLayoutManager)recyclerView.getLayoutManager()).findFirstVisibleItemPosition() >= 0 ) {
                scrollPositon = ((LinearLayoutManager) recyclerView.getLayoutManager()).findFirstVisibleItemPosition();

                checkScrollPosition();
            }
        }
    };

    /**
     * This method is get data from local cart and call SaveApi
     * @param view
     */
    public void saveOrderProcess(View view){
        if (isOpen) {
            Utils.progressDialog(mContext, "");
            ArrayList<LocalCartBean> localList = new ArrayList<>();
            localList = db.getCartItems();
            SaveOrderBean orderBean = new SaveOrderBean();
            ArrayList<SaveItemsBean> itemsList = new ArrayList<>();
            if (localList != null && localList.size() > 0) {
                for (LocalCartBean lcb : localList) {
                    itemsList.add(new SaveItemsBean(lcb.getItemId(), lcb.getQty(), lcb.getSpecialInstruction(), lcb.getCustomization()));
                }
            }
            if (itemsList != null && itemsList.size() > 0) {
                orderBean.setRestaurantId(restaurantId);
                orderBean.setSpecialInstruction("");
                if (fromWhich.equalsIgnoreCase(getString(R.string.take_away)))
                    orderBean.setOrderType(Constants.TAKE_AWAY);
                else if (fromWhich.equalsIgnoreCase(getString(R.string.delivery))) {
                    orderBean.setOrderType(Constants.HOME_DELIVERY);
                    if(addressBean!= null){
                        orderBean.setAddressId(addressBean.getRecordId());
                    }
                }else if (fromWhich.equalsIgnoreCase(getString(R.string.dine_in))) {
                    orderBean.setOrderType(Constants.DINE_IN);
                    orderBean.setTableNumber(scanQrBean.getTableNumber());
                } else if (fromWhich.equalsIgnoreCase(getString(R.string.cafe))) {
                    orderBean.setOrderType(Constants.CAFE);
                }
                orderBean.setItemList(itemsList);

                String json = new Gson().toJson(orderBean);

                callSaveOrderApi(json);
            }
        } else {
            Utils.retryAlertDialog(mContext, getString(R.string.app_name), getString(R.string.restaurant_closed_msg), getString(R.string.Ok), "", null);
        }
    }

    /**
     * this method is check the scroll position and change the menu
     */
    private void checkScrollPosition() {
        if (oldPosition != scrollPositon){
            oldPosition = scrollPositon;
            for (MenuTabBean mtb: categoryList)
                mtb.setSelected(false);
            categoryList.get(scrollPositon).setSelected(true);
            tabAdapter.notifyDataSetChanged();
            binding.rvCategory.scrollToPosition(scrollPositon);
        }
    }

    /**
     * In this method first we get data from local database if there is any data then match with restaurantid and
     */

    private void checkData() {
        ArrayList<LocalCartBean> localList = new ArrayList<>();
        localList = db.getCartItems();
        if (localList != null && localList.size() > 0){

            if (restaurantId.equalsIgnoreCase(localList.get(0).getRestaurantId())){
                isSameRestaurant = true;
                for (LocalCartBean lcb: localList){
                    boolean isFound = false;
                    for (MenuBean mb:  menuList){
                        if (lcb.getCategoryId().equalsIgnoreCase(mb.getCategoryUuid())){
                            for (MenuListBean mlb: mb.getMenuList()){
                                if (lcb.getItemId() == mlb.getItemId()){
                                    mlb.setQuantity(mlb.getQuantity() + lcb.getQty());
                                    totalAmount = totalAmount + (lcb.getQty()*lcb.getPrice());
                                    totalItems++;
                                    isFound = true;
                                    break;
                                }
                                if (isFound)
                                    break;
                            }
                        }
                        if (isFound)
                            break;
                    }
                }
            } else
                isSameRestaurant = false;

        } else
            isSameRestaurant = true;
        filterList = menuList;
        setData();
        setBottomPrice();
    }

    /**
     * this method is used to check menu list and set data into adapters
     */
    private void setData() {
        if (categoryList != null && categoryList.size() > 0)
            categoryList.clear();
        if (filterList != null && filterList.size()>0) {
            for (MenuBean mb: filterList)
                categoryList.add(new MenuTabBean(false, mb.getCategoryName()));
            categoryList.get(0).setSelected(true);

            tabAdapter = new MenuTabAdapter(mContext, categoryList);
            binding.rvCategory.setAdapter(tabAdapter);
            tabAdapter.notifyDataSetChanged();

            mAdapter = new MenuAdapter(mContext, filterList, this);
            binding.rvMenu.setAdapter(mAdapter);
            mAdapter.notifyDataSetChanged();
            binding.rvMenu.setVisibility(View.VISIBLE);
            binding.rvCategory.setVisibility(View.VISIBLE);
            binding.shimmerRv.stopShimmer();
            binding.shimmerRv.setVisibility(View.GONE);
            binding.llData.setVisibility(View.VISIBLE);
        }else {
            binding.rvMenu.setVisibility(View.GONE);
            binding.rvCategory.setVisibility(View.GONE);
        }
    }


    /**
     * this method is call Api for get menu items
     */

    private void callGetRestaurantApi() {

        RestaurantRequestBean bean = new RestaurantRequestBean();
        if (fromWhich.equalsIgnoreCase(getString(R.string.take_away)))
            bean.setOrderType(Constants.TAKE_AWAY);
        else if (fromWhich.equalsIgnoreCase(getString(R.string.delivery)))
            bean.setOrderType(Constants.HOME_DELIVERY);
        else if (fromWhich.equals(getString(R.string.dine_in)))
            bean.setOrderType(Constants.DINE_IN);
        else if (fromWhich.equals(getString(R.string.cafe)))
            bean.setOrderType(Constants.CAFE);
        bean.setRestaurantId(restaurantId);
        bean.setStart(0);
        bean.setLength(1000);
        bean.setSearchKey("");

        String json = new Gson().toJson(bean);

        try {
            new NetworkManager(ArrayList.class, new NetworkManager.OnCallback<MenuBean>() {
                @Override
                public void onSuccess(boolean success, ResponseClass responseClass, int which) {
                    if (responseClass.isSuccess()){
                        Type objType = new TypeToken<ResponseClass<ArrayList<MenuBean>>>() {
                        }.getType();
                        ResponseClass<ArrayList<MenuBean>> responseClass1 = new Gson().fromJson(responseClass.getCompletePacket(), objType);

                        menuList = responseClass1.getResponsePacket();
                        if(menuList.size()>0 && menuList != null) {
                            binding.txtNoData.setVisibility(View.GONE);
                            checkData();
                        } else {
                            binding.txtNoData.setVisibility(View.VISIBLE);
                        }

                    } else {
                        Utils.showCenterToast(mContext, responseClass.getMessage());
                    }
                }

                @Override
                public void onFailure(boolean success, String response, int which) {
                    Utils.showCenterToast(mContext, getString(R.string.internet_connection_not_found) );
                }
            }).callAPIJson(mContext, Constants.VAL_POST, AppUrls.GET_ITEM_LIST_WITH_CAT_SUBCAT, json, "Loading...", true, AppUrls.REQUESTTAG_ITEMLIST);

        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
    }

    /**
     * this method is call Api for save order
     */
    private void callSaveOrderApi(String json) {

        try {
            new NetworkManager(String.class, new NetworkManager.OnCallback<String>() {
                @Override
                public void onSuccess(boolean success, ResponseClass responseClass, int which) {
                    if (responseClass.isSuccess()){
//                        Utils.showCenterToast(mContext, responseClass.getMessage());
                        String orderId = (String) responseClass.getResponsePacket();

                        Intent intent = new Intent(mContext, CartActi.class);
                        intent.putExtra(Constants.ORDER_ID, orderId);
                        intent.putExtra(Constants.RESTAURANT_ID, restaurantId);
                        intent.putExtra(Constants.RESTAURANT_NAME, restaurantName);
                        intent.putExtra(Constants.FROM_WHICH, fromWhich);
                        intent.putExtra(Constants.DELIVERY_MIN_ORDER_VALUE, minmumAmount);
                        startActivity(intent);

                    } else {
                        Utils.showCenterToast(mContext, responseClass.getMessage());
                    }
                }

                @Override
                public void onFailure(boolean success, String response, int which) {
                    Utils.showCenterToast(mContext, getString(R.string.internet_connection_not_found) );
                }
            }).callAPIJson(mContext, Constants.VAL_POST, AppUrls.SAVE_ORDER, json, "Loading...", true, AppUrls.REQUESTTAG_SAVEORDER);

        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
    }

    @Override
    public Filter getFilter() {
        return new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence charSequence) {
                String charString = charSequence.toString();
                if (charString.isEmpty()) {
                    if (isVegOnly)
                        filterList = vegOnlyList;
                    else
                        filterList = menuList;
                } else {
                    ArrayList<MenuBean> filteredList = new ArrayList<>();
                    ArrayList<MenuBean> searchList = new ArrayList<>();
                    if (isVegOnly)
                        searchList = vegOnlyList;
                    else
                        searchList = menuList;
                    for (MenuBean row : searchList) {

                        // name match condition. this might differ depending on your requirement
                        // here we are looking for name or phone number match
                        MenuBean mainBean = new MenuBean();
                        boolean isItemFound = false;
                        if (row.getMenuList() != null && row.getMenuList().size() > 0) {
                            ArrayList<MenuListBean> itemList = new ArrayList<>();
                            for (MenuListBean ib : row.getMenuList()) {
                                if (ib.getTitle().toLowerCase().contains(charString.toLowerCase()) || (ib.getDescription() != null && ib.getDescription().toLowerCase().contains(charString.toLowerCase()))) {
                                    itemList.add(ib);
                                }
                            }
                            if (itemList.size() > 0) {
                                mainBean.setCategoryName(row.getCategoryName());
                                mainBean.setCategoryUuid(row.getCategoryUuid());
                                mainBean.setCategoryViewType(row.getCategoryViewType());
                                mainBean.setMenuList(itemList);
                                isItemFound = true;
                            }
                        }

                        if (row.getSubCategoryList() != null && row.getSubCategoryList().size() > 0){
                            ArrayList<MenuBean> subCatList = new ArrayList<>();
                            for (MenuBean subRow: row.getSubCategoryList()){
                                MenuBean subMenuBean = new MenuBean();
                                if (subRow.getMenuList() != null && subRow.getMenuList().size() > 0) {
                                    ArrayList<MenuListBean> subItemList = new ArrayList<>();
                                    for (MenuListBean ib : subRow.getMenuList()) {
                                        if (ib.getTitle().toLowerCase().contains(charString.toLowerCase()) || (ib.getDescription() != null && ib.getDescription().toLowerCase().contains(charString.toLowerCase()))) {
                                            subItemList.add(ib);
                                        }
                                    }
                                    if (subItemList.size() > 0) {
                                        subMenuBean.setCategoryName(subRow.getCategoryName());
                                        subMenuBean.setCategoryUuid(subRow.getCategoryUuid());
                                        subMenuBean.setCategoryViewType(subRow.getCategoryViewType());
                                        subMenuBean.setMenuList(subItemList);
                                        isItemFound = true;
                                        subCatList.add(subMenuBean);
                                    }
                                }
                            }
                            if (subCatList.size() > 0) {
                                if (mainBean.getMenuList() == null) {
                                    mainBean.setCategoryName(row.getCategoryName());
                                    mainBean.setCategoryUuid(row.getCategoryUuid());
                                    mainBean.setCategoryViewType(row.getCategoryViewType());
                                }
                                mainBean.setSubCategoryList(subCatList);
                            }
                        }
                        if (isItemFound)
                            filteredList.add(mainBean);

                    }
                    filterList = filteredList;
                }

                FilterResults filterResults = new FilterResults();
                filterResults.values = filterList;
                return filterResults;
            }

            @Override
            protected void publishResults(CharSequence charSequence, FilterResults filterResults) {
                filterList = (ArrayList<MenuBean>) filterResults.values;
//                mAdapter = new CategoryAdapter(mContext, filterList);
//                rvAddItems.setAdapter(mAdapter);
//                mAdapter.notifyDataSetChanged();
                setData();
            }
        };
    }




    private void customCleverTapEvent(int position){
        HashMap<String, Object> CategoryViewedAction = new HashMap<String, Object>();
        CategoryViewedAction.put("Category Id", filterList.get(position).getCategoryUuid());
        CategoryViewedAction.put("Category Name", filterList.get(position).getCategoryName());
//        CategoryViewedAction.put("Location Id", restaurantId);
        CategoryViewedAction.put("Location Name", restaurantName);
        AppInitialization.getInstance().clevertapDefaultInstance.pushEvent(fromWhich+" Category Viewed", CategoryViewedAction);
    }

    private void customCleverItemAddEvent(MenuListBean itemBean, String categoryName){
        new Thread(new Runnable() {
            @Override
            public void run() {
                HashMap<String, Object> itemAddToCartAction = new HashMap<String, Object>();
                itemAddToCartAction.put("Item Id", itemBean.getItemId());
                itemAddToCartAction.put("Item Name", itemBean.getTitle());
//                itemAddToCartAction.put("SubCategory Name", subCategoryname);
                itemAddToCartAction.put("Cateegory Name", categoryName);
//        itemAddToCartAction.put("Location Id", restaurantId);
                itemAddToCartAction.put("Location Name", restaurantName);
                itemAddToCartAction.put("Price", itemBean.getFinalPrice());
                AppInitialization.getInstance().clevertapDefaultInstance.pushEvent(fromWhich+" Item Added To Cart", itemAddToCartAction);
            }
        }).start();

    }

    private void customCleverSearchedEvent(String searchKeyword){
        new Thread(new Runnable() {
            @Override
            public void run() {
                HashMap<String, Object> searchedAction = new HashMap<String, Object>();
                searchedAction.put("keyword", searchKeyword);
                AppInitialization.getInstance().clevertapDefaultInstance.pushEvent(fromWhich+" Searched", searchedAction);
            }
        }).start();

    }



}
