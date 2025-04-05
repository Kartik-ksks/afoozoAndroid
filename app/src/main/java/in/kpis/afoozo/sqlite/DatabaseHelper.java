package in.kpis.afoozo.sqlite;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.text.TextUtils;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import in.kpis.afoozo.bean.CustomizationBean;
import in.kpis.afoozo.bean.CustomizationOptionsBean;
import in.kpis.afoozo.bean.LocalCartBean;
import in.kpis.afoozo.bean.StealDealCartBean;

import java.lang.reflect.Type;
import java.util.ArrayList;


/**
 * Created by KHEMRAJ on 8/7/2018.
 */
public class DatabaseHelper extends SQLiteOpenHelper {

    // Database Version
    private static final int DATABASE_VERSION = 1;

    // Database Name
    public static final String DATABASE_NAME = "Afoozo_db";

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        // create notes table
        db.execSQL(LocalCartBean.CREATE_TABLE);
        db.execSQL(StealDealCartBean.CREATE_TABLE);

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // Drop older table if existed
        db.execSQL("DROP TABLE IF EXISTS " + LocalCartBean.TABLE_NAME);
        db.execSQL("DROP TABLE IF EXISTS " + StealDealCartBean.TABLE_NAME);

        // Create tables again
        onCreate(db);
    }

    public void deleteTicketRecord(SQLiteDatabase db){
//        db.execSQL("DROP TABLE IF EXISTS " + RecordsModel.TABLE_NAME);
//        db.execSQL(RecordsModel.CREATE_TABLE);
    }

    public void resetFactorySetting(SQLiteDatabase db){
        db.execSQL("DROP TABLE IF EXISTS " + LocalCartBean.TABLE_NAME);
        db.execSQL("DROP TABLE IF EXISTS " + StealDealCartBean.TABLE_NAME);
//
        db.execSQL(LocalCartBean.CREATE_TABLE);

    }

    public long insertCartItem(String restaurantId, String categoryId, long productId, int productQty, double price, String instruction, ArrayList<CustomizationBean> customList) {
        // get writable database as we want to write data
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        // `id` and `timestamp` will be inserted automatically.
        // no need to add them
        values.put(LocalCartBean.COLUMN_RESTAURANT_ID, restaurantId);
        values.put(LocalCartBean.COLUMN_CATEGORY_ID, categoryId);
        values.put(LocalCartBean.COLUMN_ITEM_ID, productId);
        values.put(LocalCartBean.COLUMN_QTY, productQty);
        values.put(LocalCartBean.COLUMN_PRICE, price);
        values.put(LocalCartBean.COLUMN_INSTRUCTION, instruction);

        if (customList != null && customList.size() > 0){
            String bean = new Gson().toJson(customList);
            values.put(LocalCartBean.COLUMN_CUSTOMIZATION, bean);
        }

        // insert row
        long id = db.insert(LocalCartBean.TABLE_NAME, null, values);

        // close db connection
        db.close();

        // return newly inserted row id
        return id;
    }

    public long insertStealDealItem(String restaurantId, long productId, String itemName, int productQty, int date, double price) {
        // get writable database as we want to write data
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        // `id` and `timestamp` will be inserted automatically.
        // no need to add them
        values.put(StealDealCartBean.COLUMN_RESTAURANT_ID, restaurantId);
        values.put(StealDealCartBean.COLUMN_ITEM_ID, productId);
        values.put(StealDealCartBean.COLUMN_ITEM_NAME, itemName);
        values.put(StealDealCartBean.COLUMN_QTY, productQty);
        values.put(StealDealCartBean.COLUMN_DATE, date);
        values.put(StealDealCartBean.COLUMN_PRICE, price);


        // insert row
        long id = db.insert(StealDealCartBean.TABLE_NAME, null, values);

        // close db connection
        db.close();

        // return newly inserted row id
        return id;
    }

    public boolean checkUpdateItem(String restaurantId, String categoryId, long productId, double price, String instruction, ArrayList<CustomizationBean> customList) {
        // get writable database as we want to write data
        boolean isNewItem = true;
        boolean isSameItem = false;
        boolean isSameCust = false;
        int id = 0;
        int qty = 0;
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();


        ArrayList<LocalCartBean> itemList = getCartItemsById(productId);

        if (itemList != null && itemList.size() > 0){
            for (LocalCartBean cb: itemList){
                if (cb.getCustomization() != null && cb.getCustomization().size() > 0){
                    if (customList != null && customList.size() > 0) {

                        if (cb.getCustomization().size() == customList.size()) {
                            isSameItem = true;
                            for (CustomizationBean custom1 : cb.getCustomization()) {
                                isSameCust = false;
                                for (CustomizationBean custom2 : customList) {
                                    if (custom1.getRecordId() == custom2.getRecordId()) {
                                        isSameCust = true;
                                        if (custom1.getCustomizationOptions().size() == custom2.getCustomizationOptions().size()) {
                                            for (CustomizationOptionsBean cob1: custom1.getCustomizationOptions()){
                                                boolean isSameOption = false;
                                                for (CustomizationOptionsBean cob2 : custom2.getCustomizationOptions()) {
                                                    if (cob1.getId() == cob2.getId()) {
                                                        isSameOption = true;
                                                        break;
                                                    }
                                                }
                                                if (!isSameOption){
                                                    isSameCust = false;
                                                    break;
                                                }

                                            }
                                        } else {
                                            isSameCust = false;
                                            break;
                                        }
                                    }
                                }
                                if (!isSameCust) {
                                    isSameItem = false;
                                    break;
                                }
                            }
                        }
                    }
                } else {
                    if (customList == null )
                        isSameItem = true;
                }

                if (isSameItem) {
                    isNewItem = false;
                    id = cb.getId();
                    qty = cb.getQty();
                    break;
                }
            }
        }

        if (isNewItem)
            insertCartItem(restaurantId, categoryId, productId, 1, price, instruction, customList);
        else
            updateItemQtyById(qty + 1, id);

        // close db connection
        db.close();

        // return newly inserted row id
        if (isNewItem)
            return true;
        else
            return false;
    }

    public boolean checkUpdateQty(long productId, int qty, ArrayList<CustomizationBean> customList) {
        // get writable database as we want to write data
        boolean isSameItem = false;
        boolean isSameCust = false;
        int id = 0;
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();


        ArrayList<LocalCartBean> itemList = getCartItemsById(productId);

        if (itemList != null && itemList.size() > 1){
            for (LocalCartBean cb: itemList){
                if (cb.getCustomization() != null && cb.getCustomization().size() > 0){
                    if (customList != null && customList.size() > 0) {
                        if (cb.getCustomization().size() == customList.size()) {
                            isSameItem = true;
                            for (CustomizationBean custom1 : cb.getCustomization()) {
                                isSameCust = false;
                                for (CustomizationBean custom2 : customList) {
                                    if (custom1.getRecordId() == custom2.getRecordId()) {
                                        isSameCust = true;
                                        if (custom1.getCustomizationOptions().size() == custom2.getCustomizationOptions().size()) {
                                            for (CustomizationOptionsBean cob1: custom1.getCustomizationOptions()){
                                                boolean isSameOption = false;
                                                for (CustomizationOptionsBean cob2 : custom2.getCustomizationOptions()) {
                                                    if (cob1.getName().equalsIgnoreCase(cob2.getName())) {
                                                        isSameOption = true;
                                                        break;
                                                    }
                                                }
                                                if (!isSameOption){
                                                    isSameCust = false;
                                                    break;
                                                }

                                            }
                                        } else {
                                            isSameCust = false;
                                            break;
                                        }
                                    }
                                }
                                if (!isSameCust) {
                                    isSameItem = false;
                                    break;
                                }
                            }
                        }
                    }
                } else {
                    if (customList == null )
                        isSameItem = true;
                }

                if (isSameItem) {
                    id = cb.getId();
                    break;
                }
            }
        } else if (itemList != null && itemList.size() == 1){
            id = itemList.get(0).getId();
        }

        // return newly inserted row id
        if (qty > 0)
            updateItemQtyById(qty, id);
        else
            deleteItemById(id);

        return true;
    }

    public boolean checkUpdateInstruction(long productId, String instruction, ArrayList<CustomizationBean> customList) {
        // get writable database as we want to write data
        boolean isSameItem = false;
        boolean isSameCust = false;
        int id = 0;
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();


        ArrayList<LocalCartBean> itemList = getCartItemsById(productId);

        if (itemList != null && itemList.size() > 1){
            for (LocalCartBean cb: itemList){
                if (cb.getCustomization() != null && cb.getCustomization().size() > 0){
                    if (customList != null && customList.size() > 0) {
                        if (cb.getCustomization().size() == customList.size()) {
                            isSameItem = true;
                            for (CustomizationBean custom1 : cb.getCustomization()) {
                                isSameCust = false;
                                for (CustomizationBean custom2 : customList) {
                                    if (custom1.getRecordId() == custom2.getRecordId()) {
                                        isSameCust = true;
                                        if (custom1.getCustomizationOptions().size() == custom2.getCustomizationOptions().size()) {
                                            for (CustomizationOptionsBean cob1: custom1.getCustomizationOptions()){
                                                boolean isSameOption = false;
                                                for (CustomizationOptionsBean cob2 : custom2.getCustomizationOptions()) {
                                                    if (cob1.getName().equalsIgnoreCase(cob2.getName())) {
                                                        isSameOption = true;
                                                        break;
                                                    }
                                                }
                                                if (!isSameOption){
                                                    isSameCust = false;
                                                    break;
                                                }

                                            }
                                        } else {
                                            isSameCust = false;
                                            break;
                                        }
                                    }
                                }
                                if (!isSameCust) {
                                    isSameItem = false;
                                    break;
                                }
                            }
                        }
                    }
                } else {
                    if (customList == null )
                        isSameItem = true;
                }

                if (isSameItem) {
                    id = cb.getId();
                    break;
                }
            }
        } else if (itemList != null && itemList.size() == 1){
            id = itemList.get(0).getId();
        }

        // return newly inserted row id
        if (id > 0)
            updateInstructionById(instruction, id);

        return true;
    }

    public String checkAndGetInstruction(long productId, ArrayList<CustomizationBean> customList) {
        // get writable database as we want to write data
        boolean isSameItem = false;
        boolean isSameCust = false;
        String instruction = "";
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();


        ArrayList<LocalCartBean> itemList = getCartItemsById(productId);

        if (itemList != null && itemList.size() > 1){
            for (LocalCartBean cb: itemList){
                if (cb.getCustomization() != null && cb.getCustomization().size() > 0){
                    if (customList != null && customList.size() > 0) {
                        if (cb.getCustomization().size() == customList.size()) {
                            isSameItem = true;
                            for (CustomizationBean custom1 : cb.getCustomization()) {
                                isSameCust = false;
                                for (CustomizationBean custom2 : customList) {
                                    if (custom1.getRecordId() == custom2.getRecordId()) {
                                        isSameCust = true;
                                        if (custom1.getCustomizationOptions().size() == custom2.getCustomizationOptions().size()) {
                                            for (CustomizationOptionsBean cob1: custom1.getCustomizationOptions()){
                                                boolean isSameOption = false;
                                                for (CustomizationOptionsBean cob2 : custom2.getCustomizationOptions()) {
                                                    if (cob1.getName().equalsIgnoreCase(cob2.getName())) {
                                                        isSameOption = true;
                                                        break;
                                                    }
                                                }
                                                if (!isSameOption){
                                                    isSameCust = false;
                                                    break;
                                                }

                                            }
                                        } else {
                                            isSameCust = false;
                                            break;
                                        }
                                    }
                                }
                                if (!isSameCust) {
                                    isSameItem = false;
                                    break;
                                }
                            }
                        }
                    }
                } else {
                    if (customList == null )
                        isSameItem = true;
                }

                if (isSameItem) {
                    instruction = cb.getSpecialInstruction();
                    break;
                }
            }
        } else if (itemList != null && itemList.size() == 1){
            instruction = itemList.get(0).getSpecialInstruction();
        }

        // return newly inserted row id

        return instruction;
    }

    public ArrayList<LocalCartBean> getCartItemsById(long productId) {
        ArrayList<LocalCartBean> notes = new ArrayList<>();

        // Select All Query
        String selectQuery = "SELECT * FROM " + LocalCartBean.TABLE_NAME + " WHERE " + LocalCartBean.COLUMN_ITEM_ID + " = '" + productId + "'";

        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        // looping through all rows and adding to list
        if (cursor.moveToFirst()) {
            do {
                LocalCartBean note = new LocalCartBean();
                note.setId(cursor.getInt(cursor.getColumnIndex(LocalCartBean.COLUMN_ID)));
                note.setRestaurantId(cursor.getString(cursor.getColumnIndex(LocalCartBean.COLUMN_RESTAURANT_ID)));
                note.setCategoryId(cursor.getString(cursor.getColumnIndex(LocalCartBean.COLUMN_CATEGORY_ID)));
                note.setItemId(cursor.getLong(cursor.getColumnIndex(LocalCartBean.COLUMN_ITEM_ID)));
                note.setQty(cursor.getInt(cursor.getColumnIndex(LocalCartBean.COLUMN_QTY)));
                note.setPrice(cursor.getDouble(cursor.getColumnIndex(LocalCartBean.COLUMN_PRICE)));
                note.setSpecialInstruction(cursor.getString(cursor.getColumnIndex(LocalCartBean.COLUMN_INSTRUCTION)));
                if (!TextUtils.isEmpty(cursor.getString(cursor.getColumnIndex(LocalCartBean.COLUMN_CUSTOMIZATION)))) {
                    Type type = new TypeToken<ArrayList<CustomizationBean>>() {}.getType();
                    ArrayList<CustomizationBean> list = new Gson().fromJson(cursor.getString(cursor.getColumnIndex(LocalCartBean.COLUMN_CUSTOMIZATION)), type);
                    note.setCustomization(list);
                }

//                note.setId(cursor.getInt(cursor.getColumnIndex(AddCustomeBean.COLUMN_SUBSCRIPTION)));

                notes.add(note);
            } while (cursor.moveToNext());
        }

        // close db connection
        db.close();

        // return notes list
        return notes;
    }

    public String getInstruction(long productId) {
        ArrayList<LocalCartBean> notes = new ArrayList<>();

        // Select All Query
        String selectQuery = "SELECT * FROM " + LocalCartBean.TABLE_NAME + " WHERE " + LocalCartBean.COLUMN_ITEM_ID + " = '" + productId + "'";

        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        // looping through all rows and adding to list
        if (cursor.moveToFirst()) {
            do {
                LocalCartBean note = new LocalCartBean();
                note.setId(cursor.getInt(cursor.getColumnIndex(LocalCartBean.COLUMN_ID)));
                note.setRestaurantId(cursor.getString(cursor.getColumnIndex(LocalCartBean.COLUMN_RESTAURANT_ID)));
                note.setCategoryId(cursor.getString(cursor.getColumnIndex(LocalCartBean.COLUMN_CATEGORY_ID)));
                note.setItemId(cursor.getLong(cursor.getColumnIndex(LocalCartBean.COLUMN_ITEM_ID)));
                note.setQty(cursor.getInt(cursor.getColumnIndex(LocalCartBean.COLUMN_QTY)));
                note.setPrice(cursor.getDouble(cursor.getColumnIndex(LocalCartBean.COLUMN_PRICE)));
                note.setSpecialInstruction(cursor.getString(cursor.getColumnIndex(LocalCartBean.COLUMN_INSTRUCTION)));
                if (!TextUtils.isEmpty(cursor.getString(cursor.getColumnIndex(LocalCartBean.COLUMN_CUSTOMIZATION)))) {
                    Type type = new TypeToken<ArrayList<CustomizationBean>>() {}.getType();
                    ArrayList<CustomizationBean> list = new Gson().fromJson(cursor.getString(cursor.getColumnIndex(LocalCartBean.COLUMN_CUSTOMIZATION)), type);
                    note.setCustomization(list);
                }

//                note.setId(cursor.getInt(cursor.getColumnIndex(AddCustomeBean.COLUMN_SUBSCRIPTION)));

                notes.add(note);
            } while (cursor.moveToNext());
        }

        // close db connection
        db.close();

        // return notes list
        if (notes.size() >0)
            return notes.get(0).getSpecialInstruction();
        else
            return "";
    }

    public String getInstructionById(long id) {
        ArrayList<LocalCartBean> notes = new ArrayList<>();

        // Select All Query
        String selectQuery = "SELECT * FROM " + LocalCartBean.TABLE_NAME + " WHERE " + LocalCartBean.COLUMN_ID + " = '" + id + "'";

        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        // looping through all rows and adding to list
        if (cursor.moveToFirst()) {
            do {
                LocalCartBean note = new LocalCartBean();
                note.setId(cursor.getInt(cursor.getColumnIndex(LocalCartBean.COLUMN_ID)));
                note.setRestaurantId(cursor.getString(cursor.getColumnIndex(LocalCartBean.COLUMN_RESTAURANT_ID)));
                note.setCategoryId(cursor.getString(cursor.getColumnIndex(LocalCartBean.COLUMN_CATEGORY_ID)));
                note.setItemId(cursor.getLong(cursor.getColumnIndex(LocalCartBean.COLUMN_ITEM_ID)));
                note.setQty(cursor.getInt(cursor.getColumnIndex(LocalCartBean.COLUMN_QTY)));
                note.setPrice(cursor.getDouble(cursor.getColumnIndex(LocalCartBean.COLUMN_PRICE)));
                note.setSpecialInstruction(cursor.getString(cursor.getColumnIndex(LocalCartBean.COLUMN_INSTRUCTION)));
                if (!TextUtils.isEmpty(cursor.getString(cursor.getColumnIndex(LocalCartBean.COLUMN_CUSTOMIZATION)))) {
                    Type type = new TypeToken<ArrayList<CustomizationBean>>() {}.getType();
                    ArrayList<CustomizationBean> list = new Gson().fromJson(cursor.getString(cursor.getColumnIndex(LocalCartBean.COLUMN_CUSTOMIZATION)), type);
                    note.setCustomization(list);
                }

//                note.setId(cursor.getInt(cursor.getColumnIndex(AddCustomeBean.COLUMN_SUBSCRIPTION)));

                notes.add(note);
            } while (cursor.moveToNext());
        }

        // close db connection
        db.close();

        // return notes list
        if (notes.size() >0)
            return notes.get(0).getSpecialInstruction();
        else
            return "";
    }

    public ArrayList<LocalCartBean> getCartItems() {
        ArrayList<LocalCartBean> notes = new ArrayList<>();

        // Select All Query
        String selectQuery = "SELECT  * FROM " + LocalCartBean.TABLE_NAME ;

        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        // looping through all rows and adding to list
        if (cursor.moveToFirst()) {
            do {
                LocalCartBean note = new LocalCartBean();
                note.setId(cursor.getInt(cursor.getColumnIndex(LocalCartBean.COLUMN_ID)));
                note.setRestaurantId(cursor.getString(cursor.getColumnIndex(LocalCartBean.COLUMN_RESTAURANT_ID)));
                note.setCategoryId(cursor.getString(cursor.getColumnIndex(LocalCartBean.COLUMN_CATEGORY_ID)));
                note.setItemId(cursor.getLong(cursor.getColumnIndex(LocalCartBean.COLUMN_ITEM_ID)));
                note.setQty(cursor.getInt(cursor.getColumnIndex(LocalCartBean.COLUMN_QTY)));
                note.setPrice(cursor.getDouble(cursor.getColumnIndex(LocalCartBean.COLUMN_PRICE)));
                note.setSpecialInstruction(cursor.getString(cursor.getColumnIndex(LocalCartBean.COLUMN_INSTRUCTION)));
                if (!TextUtils.isEmpty(cursor.getString(cursor.getColumnIndex(LocalCartBean.COLUMN_CUSTOMIZATION)))) {
                    Type type = new TypeToken<ArrayList<CustomizationBean>>() {}.getType();
                    ArrayList<CustomizationBean> list = new Gson().fromJson(cursor.getString(cursor.getColumnIndex(LocalCartBean.COLUMN_CUSTOMIZATION)), type);
                    note.setCustomization(list);
                }

//                note.setId(cursor.getInt(cursor.getColumnIndex(AddCustomeBean.COLUMN_SUBSCRIPTION)));

                notes.add(note);
            } while (cursor.moveToNext());
        }

        // close db connection
        db.close();

        // return notes list
        return notes;
    }

    public ArrayList<StealDealCartBean> getStealDealItems() {
        ArrayList<StealDealCartBean> notes = new ArrayList<>();

        // Select All Query
        String selectQuery = "SELECT  * FROM " + StealDealCartBean.TABLE_NAME ;

        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        // looping through all rows and adding to list
        if (cursor.moveToFirst()) {
            do {
                StealDealCartBean note = new StealDealCartBean();
                note.setRestaurantId(cursor.getString(cursor.getColumnIndex(StealDealCartBean.COLUMN_RESTAURANT_ID)));
                note.setStealDealItemId(cursor.getLong(cursor.getColumnIndex(StealDealCartBean.COLUMN_ITEM_ID)));
                note.setItemName(cursor.getString(cursor.getColumnIndex(StealDealCartBean.COLUMN_ITEM_NAME)));
                note.setStealDealItemQty(cursor.getInt(cursor.getColumnIndex(StealDealCartBean.COLUMN_QTY)));
                note.setDate(cursor.getInt(cursor.getColumnIndex(StealDealCartBean.COLUMN_DATE)));
                note.setPrice(cursor.getDouble(cursor.getColumnIndex(StealDealCartBean.COLUMN_PRICE)));


//                note.setId(cursor.getInt(cursor.getColumnIndex(AddCustomeBean.COLUMN_SUBSCRIPTION)));

                notes.add(note);
            } while (cursor.moveToNext());
        }

        // close db connection
        db.close();

        // return notes list
        return notes;
    }

    public boolean updateItemQtyById(int qty, int id) {
        // get writable database as we want to write data
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        // `id` and `timestamp` will be inserted automatically.
        // no need to add them
        values.put(LocalCartBean.COLUMN_QTY, qty);

        // insert row
        db.update(LocalCartBean.TABLE_NAME, values, LocalCartBean.COLUMN_ID + " = ?",new String[] { "" + id } );

        // close db connection
        db.close();

        // return newly inserted row id
        return true;
    }

    public boolean updateStealDealItemQty(long itemId, int qty) {
        // get writable database as we want to write data
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        // `id` and `timestamp` will be inserted automatically.
        // no need to add them
        values.put(StealDealCartBean.COLUMN_QTY, qty);

        // insert row
        db.update(StealDealCartBean.TABLE_NAME, values, StealDealCartBean.COLUMN_ITEM_ID + " = ?",new String[] { "" + itemId } );

        // close db connection
        db.close();

        // return newly inserted row id
        return true;
    }

    public boolean updateItemQty(int qty, long id) {
        // get writable database as we want to write data
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        // `id` and `timestamp` will be inserted automatically.
        // no need to add them
        values.put(LocalCartBean.COLUMN_QTY, qty);

        // insert row
        db.update(LocalCartBean.TABLE_NAME, values, LocalCartBean.COLUMN_ITEM_ID + " = ?",new String[] { "" +id } );

        // close db connection
        db.close();

        // return newly inserted row id
        return true;
    }

    public boolean updateInstructionById(String instruction, int id) {
        // get writable database as we want to write data
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        // `id` and `timestamp` will be inserted automatically.
        // no need to add them
        values.put(LocalCartBean.COLUMN_INSTRUCTION, instruction);

        // insert row
        db.update(LocalCartBean.TABLE_NAME, values, LocalCartBean.COLUMN_ID + " = ?",new String[] { "" + id } );

        // close db connection
        db.close();

        // return newly inserted row id
        return true;
    }

    public boolean updateInstruction(String instruction, long id) {
        // get writable database as we want to write data
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        // `id` and `timestamp` will be inserted automatically.
        // no need to add them
        values.put(LocalCartBean.COLUMN_INSTRUCTION, instruction);

        // insert row
        db.update(LocalCartBean.TABLE_NAME, values, LocalCartBean.COLUMN_ITEM_ID + " = ?",new String[] { "" +id } );

        // close db connection
        db.close();

        // return newly inserted row id
        return true;
    }

    public void deleteAllItems() {
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL("delete from "+ LocalCartBean.TABLE_NAME);
//        db.execSQL("delete from " + );
        db.close();
    }

    public void deleteAllStealDealItems() {
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL("delete from "+ StealDealCartBean.TABLE_NAME);
//        db.execSQL("delete from " + );
        db.close();
    }

    public void deleteItem(long productId) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(LocalCartBean.TABLE_NAME, LocalCartBean.COLUMN_ITEM_ID + " = ?",
                new String[]{"" + productId});
//        db.execSQL("delete from " + );
        db.close();
    }

    public void deleteStealDealItem(long productId) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(StealDealCartBean.TABLE_NAME, StealDealCartBean.COLUMN_ITEM_ID + " = ?",
                new String[]{"" + productId});
//        db.execSQL("delete from " + );
        db.close();
    }

    public void deleteItemById(long id) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(LocalCartBean.TABLE_NAME, LocalCartBean.COLUMN_ID + " = ?",
                new String[]{"" + id});
//        db.execSQL("delete from " + );
        db.close();
    }
}
