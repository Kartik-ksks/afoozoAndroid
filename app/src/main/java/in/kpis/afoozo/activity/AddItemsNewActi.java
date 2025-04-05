package in.kpis.afoozo.activity;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.appcompat.widget.Toolbar;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.viewpager.widget.ViewPager;

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

import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.kpis.afoozo.R;
import in.kpis.afoozo.adapter.CustomizationAdapter;
import in.kpis.afoozo.bean.CouponBean;
import in.kpis.afoozo.bean.CouponListBean;
import in.kpis.afoozo.bean.CustomizationBean;
import in.kpis.afoozo.bean.CustomizationOptionsBean;
import in.kpis.afoozo.bean.DeliveryOfferBean;
import in.kpis.afoozo.bean.LocalCartBean;
import in.kpis.afoozo.bean.MenuBean;
import in.kpis.afoozo.bean.MenuListBean;
import in.kpis.afoozo.bean.RestaurantRequestBean;
import in.kpis.afoozo.bean.SaveItemsBean;
import in.kpis.afoozo.bean.SaveOrderBean;
import in.kpis.afoozo.bean.ScanQrBean;
import com.kpis.afoozo.databinding.ActivityAddItemsNewBinding;
import com.kpis.afoozo.databinding.InstructionPopupLayoutBinding;
import in.kpis.afoozo.fragment.CategoryFrag;
import in.kpis.afoozo.interfaces.CategoryAddRemove;
import in.kpis.afoozo.interfaces.CustomizationClick;
import in.kpis.afoozo.okhttpServcice.AppUrls;
import in.kpis.afoozo.okhttpServcice.NetworkManager;
import in.kpis.afoozo.okhttpServcice.response.ResponseClass;
import in.kpis.afoozo.sqlite.DatabaseHelper;
import in.kpis.afoozo.util.Constants;
import in.kpis.afoozo.util.SharedPref;
import in.kpis.afoozo.util.Utils;

import java.io.UnsupportedEncodingException;
import java.lang.reflect.Type;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Calendar;

public class AddItemsNewActi extends AppCompatActivity implements CustomizationClick, Filterable, View.OnClickListener {

    private Toolbar toolbar;

    private Context mContext;

    private DatabaseHelper db;

    private BottomSheetBehavior sheetBehavior;

    private ActivityAddItemsNewBinding binding;

    private ArrayList<MenuBean> menuList = new ArrayList<>();
    public ArrayList<MenuBean> filterList;
    public ArrayList<MenuBean> vegOnlyList;

    public boolean isSameRestaurant;
    private boolean isOpen;
    private boolean isBottomSheetOpen;
    private boolean isSubCat;

    private int catPosition;
    private int subCatPosition;
    private int itemPosition;
    private int totalItems;
    private int ORDER_COMPLETE_REQUEST = 301;
    private int takeawayMinOrderValue;
    private int deliveryMinOrderValue;

    private double itemPrice;
    private double totalAmount;

    public String restaurantId;
    private String fromWhich;
    private String title = "";
    public boolean isFirst = false;

    private ArrayList<CustomizationBean> custList = new ArrayList<>();
    private CustomizationAdapter customizationAdapter;
    private ScanQrBean scanQrBean;
    private CategoryAddRemove callback;

    private Dialog alert;

    private AddressBean addressBean;
    private ArrayList<MenuListBean> selectedList;
    private boolean isVegOnly;

    public int showCatPosition;
    public int showItemPosition;
    public boolean showItemIsSubCat;
    public int showSubCatPosition;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_add_items_new);
        if (getIntent().getExtras() != null) {
            fromWhich = getIntent().getStringExtra(Constants.FROM_WHICH);
            if (fromWhich.equals(getString(R.string.dine_in))){
                scanQrBean = (ScanQrBean) getIntent().getSerializableExtra(Constants.SCAN_QR_BEAN);
                if (scanQrBean != null && !TextUtils.isEmpty(scanQrBean.getRestaurantUuid()))
                    restaurantId = scanQrBean.getRestaurantUuid();
                else
                    restaurantId = getIntent().getStringExtra(Constants.RESTAURANT_ID);
                isOpen = true;
            } else {
                if (fromWhich.equals(getString(R.string.delivery)))
                    addressBean = (AddressBean) getIntent().getSerializableExtra(Constants.ADDRESS_BEAN);
                restaurantId = getIntent().getStringExtra(Constants.RESTAURANT_ID);
                if (addressBean != null)
                    isOpen = getIntent().getBooleanExtra(Constants.IS_OPEN, false);
                else
                    isOpen = true;
                takeawayMinOrderValue = getIntent().getIntExtra(Constants.TAKEAWAY_MIN_ORDER_VALUE, 0);
                deliveryMinOrderValue = getIntent().getIntExtra(Constants.DELIVERY_MIN_ORDER_VALUE, 0);
            }
            isFirst = false;
            if(getIntent().getStringExtra(Constants.TITLE) != null) {
                title = getIntent().getStringExtra(Constants.TITLE);
                isFirst = true;
            }
        }

        mContext = AddItemsNewActi.this;
        db = new DatabaseHelper(mContext);

        String savedFor = SharedPref.getSavedDataForWhich(mContext);
        if (savedFor != null && !savedFor.equals(fromWhich)) {
            SharedPref.setSavedDataForWhich(mContext, fromWhich);
            db.deleteAllItems();
        } else
            SharedPref.setSavedDataForWhich(mContext, fromWhich);

        initialize();
    }

    @Override
    public void onBackPressed() {
        if (isBottomSheetOpen)
            toggleBottomSheet();
        else
            finish();
    }

    private void initialize() {
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(false);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(false);

        toolbar.setNavigationIcon(R.drawable.ic_back);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        binding.toolbar.activityTitle.setText(getString(R.string.menu));

        sheetBehavior = BottomSheetBehavior.from(binding.bottomSheet.llBottomSheet);

        binding.bottomSheet.rvBottomSheet.setLayoutManager(new LinearLayoutManager(mContext));
        binding.bottomSheet.rvBottomSheet.setItemAnimator(new DefaultItemAnimator());

        binding.bottomSheet.btnChoose.setOnClickListener(this);
        binding.bottomSheet.btnRepeat.setOnClickListener(this);

        binding.searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
//                if (mAdapter != null && list != null && list.size() > 0 )
//                    mAdapter.getFilter().filter(query);
                if (menuList != null && menuList.size() > 0)
                    getFilter().filter(query);
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
//                if (mAdapter != null && list != null && list.size() > 0 )
//                    mAdapter.getFilter().filter(newText);
                if (menuList != null && menuList.size() > 0)
                    getFilter().filter(newText);
                return false;
            }
        });

        binding.switchVeg.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                Utils.progressDialog(mContext, "");
                isVegOnly = isChecked;
                filterVegOnly(isChecked);
            }
        });

        binding.vpPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {

            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });

    }

    private void filterVegOnly(boolean isChecked) {
        if (!isChecked)
            filterList = menuList;
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

    @Override
    protected void onResume() {
        super.onResume();

        callGetRestaurantApi();
    }

    public void hideBottomSheet(View view){
        toggleBottomSheet();
    }

    private void toggleBottomSheet() {
        if (sheetBehavior.getState() != BottomSheetBehavior.STATE_EXPANDED) {
            sheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);
            isBottomSheetOpen = true;
        } else {
            sheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
            isBottomSheetOpen = false;
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


    public void addClick(boolean isSubCat, int catPosition, int subCatPosition, int itemPosition, CategoryAddRemove callBack) {
        this.isSubCat = isSubCat;
        this.catPosition = catPosition;
        this.subCatPosition = subCatPosition;
        this.itemPosition = itemPosition;
        this.callback = callBack;

        if (isSubCat)
            selectedList = filterList.get(catPosition).getSubCategoryList().get(subCatPosition).getMenuList();
        else
            selectedList = filterList.get(catPosition).getMenuList();

        if (selectedList.get(itemPosition).getCustomization() != null) {
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


    }

    public void removeClick(boolean isSubCat, int catPosition, int subCatPosition, int itemPosition, CategoryAddRemove callBack) {
        this.isSubCat = isSubCat;
        this.catPosition = catPosition;
        this.subCatPosition = subCatPosition;
        this.itemPosition = itemPosition;
        this.callback = callBack;

        if (isSubCat)
            selectedList = filterList.get(catPosition).getSubCategoryList().get(subCatPosition).getMenuList();
        else
            selectedList = filterList.get(catPosition).getMenuList();

        if (selectedList.get(itemPosition).getCustomization() != null){
//            Utils.showCenterToast(mContext, getString(R.string.go_to_cart_to_remove_cust_item));
            MenuListBean bean = selectedList.get(itemPosition);
            int qty = bean.getQuantity();
            ArrayList<LocalCartBean> cl = db.getCartItemsById(bean.getItemId());
            double lessPrice = 0;
            for (LocalCartBean lcb: cl){
                lessPrice = lessPrice + lcb.getPrice();
            }
            db.deleteItem(bean.getItemId());
            totalAmount = totalAmount - lessPrice;
            selectedList.get(itemPosition).setQuantity(0);
            totalItems = totalItems - qty;

            setBottomPrice();

            callback.categoryAddRemoveClick(isSubCat, itemPosition, subCatPosition, 0);
        } else {
            MenuListBean bean = selectedList.get(itemPosition);
            int qty = bean.getQuantity();
            if (qty > 1) {
                boolean isUpdate = db.updateItemQty(--qty, bean.getItemId());
            } else {
                db.deleteItem(bean.getItemId());
                --qty;
                --totalItems;
            }

            totalAmount = totalAmount - bean.getFinalPrice();
            selectedList.get(itemPosition).setQuantity(qty);

//            mAdapter.notifyDataSetChanged();
            setBottomPrice();

            callback.categoryAddRemoveClick(isSubCat, itemPosition, subCatPosition, qty);
        }
    }

    public void editClick(boolean isSubCat, int catPosition, int subCatPosition, int itemPosition, CategoryAddRemove callBack) {
        this.isSubCat = isSubCat;
        this.catPosition = catPosition;
        this.subCatPosition = subCatPosition;
        this.itemPosition = itemPosition;

        if (isSubCat)
            selectedList = filterList.get(catPosition).getSubCategoryList().get(subCatPosition).getMenuList();
        else
            selectedList = filterList.get(catPosition).getMenuList();

        showInstructionPopup();
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

    @Override
    public void onClick(View v) {

    }

    /**
     * This method is used for add non customizable items in local cart
     */
    public void addItemProcess() {
        MenuListBean bean = selectedList.get(itemPosition);
        int qty = bean.getQuantity();
        if (qty == 0) {
//            showInstructionPopup(bean.getItemId(), ++qty, bean.getPrice());
            if (isSubCat)
                db.insertCartItem(restaurantId, filterList.get(catPosition).getSubCategoryList().get(subCatPosition).getCategoryUuid(), bean.getItemId(), ++qty, bean.getFinalPrice(), "",null);
            else
                db.insertCartItem(restaurantId, filterList.get(catPosition).getCategoryUuid(), bean.getItemId(), ++qty, bean.getFinalPrice(), "",null);
            ++totalItems;

            totalAmount = totalAmount + bean.getFinalPrice();
            selectedList.get(itemPosition).setQuantity(qty);

//        mAdapter.notifyDataSetChanged();
            setBottomPrice();

            callback.categoryAddRemoveClick(isSubCat, itemPosition, subCatPosition, qty);
        } else {
            boolean isUpdate = db.updateItemQty(++qty, bean.getItemId());

            totalAmount = totalAmount + bean.getFinalPrice();
            selectedList.get(itemPosition).setQuantity(qty);

//        mAdapter.notifyDataSetChanged();
            setBottomPrice();

            callback.categoryAddRemoveClick(isSubCat, itemPosition, subCatPosition, qty);
        }

    }

    /**
     * Show Popup for special instruction
     * @param itemId  selected item id
     * @param qty   item quantity
     * @param price item price
     */
//    private void showInstructionPopup(long itemId, final int qty, double price) {
//        alert = new Dialog(mContext);
//        alert.requestWindowFeature(Window.FEATURE_NO_TITLE);
//        alert.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
//
//        Window dialogWindow = alert.getWindow();
//        WindowManager.LayoutParams lp = dialogWindow.getAttributes();
//        dialogWindow.setGravity(Gravity.CENTER);
//
//        lp.width = WindowManager.LayoutParams.MATCH_PARENT;
//
//        dialogWindow.setAttributes(lp);
//
//        InstructionPopupLayoutBinding successBinding = DataBindingUtil.inflate(LayoutInflater.from(mContext), R.layout.instruction_popup_layout, null, false);
//        alert.setContentView(successBinding.getRoot());
//
////        alert.setContentView(R.layout.buy_popup_layout);
//
//        successBinding.btnPopAdd.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                alert.dismiss();
//                db.insertCartItem(restaurantId, filterList.get(catPosition).getCategoryUuid(), itemId, qty, price, successBinding.etPopInst.getText().toString(),null);
//                ++totalItems;
//
//                totalAmount = totalAmount + price;
//                filterList.get(catPosition).getMenuList().get(itemPosition).setQuantity(qty);
//
////        mAdapter.notifyDataSetChanged();
//                setBottomPrice();
//
//                callback.categoryAddRemoveClick(isSubCat, itemPosition, subCatPosition, qty);
//            }
//        });
//
//
//        alert.getWindow().getAttributes().windowAnimations = R.style.Animation_Dialog;
//        alert.setCancelable(false);
//        alert.show();
//    }

    /**
     * This method is show popup for add instruction in selected item
     */
    private void showInstructionPopup () {
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

        successBinding.etPopInst.setText(db.getInstruction(selectedList.get(itemPosition).getItemId()));

        successBinding.btnPopAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                db.updateInstruction(successBinding.etPopInst.getText().toString(), selectedList.get(itemPosition).getItemId());
                alert.dismiss();
            }
        });


        alert.getWindow().getAttributes().windowAnimations = R.style.Animation_Dialog;
        alert.setCancelable(true);
        alert.show();
    }

    public class MyPagerAdapter extends FragmentPagerAdapter {
        private int NUM_ITEMS = filterList.size();

        public MyPagerAdapter(FragmentManager fragmentManager) {
            super(fragmentManager);
        }

        // Returns total number of pages
        @Override
        public int getCount() {
            return NUM_ITEMS;
        }

        // Returns the fragment to display for that page
        @Override
        public Fragment getItem(int position) {
//            switch (position) {
//                case 0: // Fragment # 0 - This will show FirstFragment
//                    return CategoryFrag.newInstance(0, "Page # 1");
//                case 1: // Fragment # 0 - This will show FirstFragment different title
//                    return CategoryFrag.newInstance(1, "Page # 2");
//                case 2: // Fragment # 1 - This will show SecondFragment
//                    return CategoryFrag.newInstance(2, "Page # 3");
//                default:
//                    return null;
//            }

            return CategoryFrag.newInstance(position, filterList.get(position));
        }

        // Returns the page title for the top indicator
        @Override
        public CharSequence getPageTitle(int position) {
            return filterList.get(position).getCategoryName();
        }

    }

    /**
     * Method for set total price and total items and show in bottom
     */
    private void setBottomPrice() {
        if (totalItems > 0){
//            sheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);
            binding.llCart.llBottom.setVisibility(View.VISIBLE);

            if (totalItems == 1) {
                binding.llCart.txtTotalItems.setText(totalItems + " " + getString(R.string.item));
                binding.llCart.txtCartTotal.setText(getString(R.string.Rs) + totalAmount);
            } else {
                binding.llCart.txtTotalItems.setText(totalItems + " " + getString(R.string.items));
                binding.llCart.txtCartTotal.setText(getString(R.string.Rs) + totalAmount);
            }
        } else
            binding.llCart.llBottom.setVisibility(View.GONE);
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

        cl = selectedList.get(itemPosition).getCustomization();
        custList = selectedList.get(itemPosition).getCustomization();
        binding.bottomSheet.txtTitleBS.setText(selectedList.get(itemPosition).getTitle());
        binding.bottomSheet.txtItemPrice.setText(getString(R.string.Rs) + new DecimalFormat("0.00").format(selectedList.get(itemPosition).getFinalPrice()));
        itemPrice = selectedList.get(itemPosition).getFinalPrice();


        for (CustomizationBean custBean: cl){
            ArrayList<CustomizationOptionsBean> opList = custBean.getCustomizationOptions();

            if (custBean.getCustomizationType().equals("SingleSelection")) {
                boolean isSelected = false;
                if (opList != null && opList.size() > 0) {
                    for (CustomizationOptionsBean opBean : opList) {
                        if (opBean.isAdded()) {
                            isSelected = true;
                            itemPrice = itemPrice + opBean.getPrice();
                        }

                    }
                    if (!isSelected) {
                        opList.get(0).setAdded(true);
                        itemPrice = itemPrice + opList.get(0).getPrice();
                    }
                }
            } else {
                if (opList != null && opList.size() > 0) {
                    for (CustomizationOptionsBean opBean : opList) {
                        if (opBean.isAdded())
                            itemPrice = itemPrice + opBean.getPrice();

                    }
                }
            }
//            if (opList != null && opList.size() > 0){
//                for (CustomizationOptionsBean opBean: opList){
//                    if (opBean.isAdded())
//                        itemPrice = itemPrice + opBean.getPrice();
//                }
//            }
        }

        setCustomizationPrice();

        if (custList != null && custList.size() > 0) {
            customizationAdapter = new CustomizationAdapter(mContext, custList, this);
            binding.bottomSheet.rvBottomSheet.setAdapter(customizationAdapter);
//            mAdapter.notifyDataSetChanged();
        }
    }

    private void setCustomizationPrice() {
        binding.bottomSheet.txtBottomPrice.setText(getString(R.string.item_total) + " " + getString(R.string.Rs) + new DecimalFormat("#.00").format(itemPrice));
    }

    private void showRepeatLayout() {
        binding.bottomSheet.llChooseCustom.setVisibility(View.GONE);
        binding.bottomSheet.llRepeat.setVisibility(View.VISIBLE);

        itemPrice = filterList.get(catPosition).getMenuList().get(itemPosition).getFinalPrice();

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
        String instruction = binding.bottomSheet.etInst.getText().toString();
        toggleBottomSheet();
//        bean.setRestaurantId(restaurantId);

        ArrayList<CustomizationBean> list = new ArrayList<>();
        for (CustomizationBean cb : custList) {
            ArrayList<CustomizationOptionsBean> optionsList = new ArrayList<>();
            for (CustomizationOptionsBean cmb : cb.getCustomizationOptions()) {
                if (cmb.isAdded())
                    optionsList.add(cmb);
            }
            if (optionsList.size() > 0){
                CustomizationBean bean = new CustomizationBean();
                bean.setRecordId(cb.getRecordId());
                bean.setCustomizationOptions(optionsList);
                list.add(bean);
            }
        }

        boolean isNew = false;
        int qty = selectedList.get(itemPosition).getQuantity();
        if (qty == 0) {
            isNew = true;
            if (isSubCat)
                db.insertCartItem(restaurantId, filterList.get(catPosition).getSubCategoryList().get(subCatPosition).getCategoryUuid(), selectedList.get(itemPosition).getItemId(), 1, itemPrice, instruction, list);
            else
                db.insertCartItem(restaurantId, filterList.get(catPosition).getCategoryUuid(), selectedList.get(itemPosition).getItemId(), 1, itemPrice, instruction, list);
        }
        else {
            if (isSubCat)
                isNew = db.checkUpdateItem(restaurantId, filterList.get(catPosition).getSubCategoryList().get(subCatPosition).getCategoryUuid(), selectedList.get(itemPosition).getItemId(), itemPrice, instruction, list);
            else
                isNew = db.checkUpdateItem(restaurantId, filterList.get(catPosition).getCategoryUuid(), selectedList.get(itemPosition).getItemId(), itemPrice, instruction, list);
        }

        selectedList.get(itemPosition).setQuantity(++qty);

        if (isNew)
            totalItems = totalItems+1;
        totalAmount = totalAmount + itemPrice;

//        mAdapter.notifyDataSetChanged();
        setBottomPrice();

        callback.categoryAddRemoveClick(isSubCat, itemPosition, subCatPosition, qty);
    }

    /**
     * In this method first we get data from local database if there is any data then match with restaurantid and
     */

    private void checkData() {
        totalItems = 0;
        totalAmount = 0;
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

                        } else if (mb.getSubCategoryList() != null && mb.getSubCategoryList().size() > 0){
                            for (MenuBean smb: mb.getSubCategoryList()){
                                if (lcb.getCategoryId().equalsIgnoreCase(smb.getCategoryUuid())){
                                    for (MenuListBean mlb: smb.getMenuList()){
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

        if (!TextUtils.isEmpty(title) && isFirst){
            boolean isFound = false;
            int i = 0;
            for (MenuBean mb:  menuList){
                if (mb.getMenuList() != null && mb.getMenuList().size() > 0) {
                    int j = 0;
                    for (MenuListBean mlb : mb.getMenuList()) {

                        if (mlb.getTitle().equalsIgnoreCase(title)) {
                            showCatPosition = i;
                            showItemPosition = j;
                            showItemIsSubCat = false;
                            isFound = true;
                            break;
                        }
                        j++;
                    }
                }else if (mb.getSubCategoryList() != null && mb.getSubCategoryList().size() > 0){
                    int k = 0;
                    for (MenuBean smb: mb.getSubCategoryList()){
                        if (smb.getMenuList() != null && smb.getMenuList().size() > 0) {
                            int j = 0;
                            for (MenuListBean ml : smb.getMenuList()) {
                                if (ml.getTitle().equalsIgnoreCase(title)){
                                    showCatPosition = i;
                                    showItemPosition = j;
                                    showSubCatPosition = k;
                                    showItemIsSubCat = true;
                                    isFound = true;
                                    break;
                                }
                                j++;
                            }
                        }

                        if (isFound) {
                            menuList.get(i).getSubCategoryList().get(k).setExpended(true);
                            break;
                        }

                        k++;

                    }
                }

                if (isFound)
                    break;
                i++;
            }
        }
        filterList = menuList;
        setData();
        setBottomPrice();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == ORDER_COMPLETE_REQUEST && resultCode == RESULT_OK){
            finish();
        }
    }

    /**
     * this method is used to check menu list and set data into adapters
     */
    private void setData() {
        if (filterList != null && filterList.size() > 0) {
            binding.vpPager.setVisibility(View.VISIBLE);
            MyPagerAdapter adapterViewPager = new MyPagerAdapter(getSupportFragmentManager());
            binding.vpPager.setAdapter(adapterViewPager);

            if (!TextUtils.isEmpty(title) && isFirst)
                binding.vpPager.setCurrentItem(showCatPosition);

//            binding.shimmerRv.stopShimmer();
//            binding.shimmerRv.setVisibility(View.GONE);
//            binding.llData.setVisibility(View.VISIBLE);
        } else {
            binding.vpPager.setVisibility(View.GONE);
        }

//        if (!TextUtils.isEmpty(title) && isFirst) {
//            binding.searchView.setQuery(title, true);
//        }
    }

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
                    orderBean.setAddressId(addressBean.getRecordId());
                } else if (fromWhich.equalsIgnoreCase(getString(R.string.dine_in))) {
                    orderBean.setOrderType(Constants.DINE_IN);
                    orderBean.setTableNumber(scanQrBean.getTableNumber());
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
     * Method is used to go Cart Screen
     * @param orderId == generated order id by save order api
     */

    private void goToCartScreen(String orderId) {
        Utils.dismissRetryAlert();
        Intent intent = new Intent(mContext, CartActi.class);
        intent.putExtra(Constants.ORDER_ID, orderId);
        intent.putExtra(Constants.RESTAURANT_ID, restaurantId);
        intent.putExtra(Constants.FROM_WHICH, fromWhich);
        intent.putExtra(Constants.TAKEAWAY_MIN_ORDER_VALUE, takeawayMinOrderValue);
        intent.putExtra(Constants.TAKEAWAY_MIN_ORDER_VALUE, takeawayMinOrderValue);
        intent.putExtra(Constants.DELIVERY_MIN_ORDER_VALUE, deliveryMinOrderValue);
        startActivityForResult(intent, ORDER_COMPLETE_REQUEST);
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
                        checkData();

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

                        DeliveryOfferBean deliveryOfferBean = (DeliveryOfferBean) Utils.getJsonToClassObject(SharedPref.getDeliveryOfferJson(mContext), DeliveryOfferBean.class);
                        if (deliveryOfferBean != null && !TextUtils.isEmpty(deliveryOfferBean.getOrderType()) && deliveryOfferBean.getOrderType().equalsIgnoreCase(fromWhich) && ((Calendar.getInstance().getTimeInMillis() - deliveryOfferBean.getLastRequestTime()) / 60000) < 60) {
                            if (!TextUtils.isEmpty(SharedPref.getCouponCode(mContext)))
                                callCouponCodeApi(SharedPref.getCouponCode(mContext), orderId);
                            else
                                goToCartScreen((String) responseClass.getResponsePacket());
                        } else {
                            goToCartScreen((String) responseClass.getResponsePacket());
                        }

                    } else {
                        Utils.dismissProgressDialog();
                        Utils.showCenterToast(mContext, responseClass.getMessage());
                    }
                }

                @Override
                public void onFailure(boolean success, String response, int which) {
                    Utils.dismissProgressDialog();
                    Utils.showCenterToast(mContext, getString(R.string.internet_connection_not_found) );
                }
            }).callAPIJson(mContext, Constants.VAL_POST, AppUrls.SAVE_ORDER, json, "Loading...", false, AppUrls.REQUESTTAG_SAVEORDER);

        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
    }

    public void callCouponCodeApi(String couponCode, String orderId){

        CouponListBean bean = new CouponListBean();
        bean.setOrderReferenceId(orderId);
        bean.setCouponCode(couponCode);

        Gson gson = new Gson();
        String json = gson.toJson(bean);

        try {
            new NetworkManager(CouponBean.class, new NetworkManager.OnCallback<CouponBean>() {
                @Override
                public void onSuccess(boolean success, ResponseClass responseClass, int which) {
                    if (responseClass.isSuccess()){
                        goToCartScreen(orderId);
                    } else {
                        Utils.dismissProgressDialog();
                        Utils.showCenterToast(mContext, responseClass.getMessage());
                    }
                }

                @Override
                public void onFailure(boolean success, String response, int which) {
                    Utils.dismissProgressDialog();
                    Utils.showCenterToast(mContext, getString(R.string.internet_connection_not_found) );
                }
            }).callAPIJson(mContext, Constants.VAL_POST, AppUrls.APPLY_COUPON_CODE, json, "Loading...", false, AppUrls.REQUESTTAG_APPLYCOUPONCODE);

        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }

    }


}
