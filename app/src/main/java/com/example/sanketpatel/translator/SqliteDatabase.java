package com.example.sanketpatel.translator;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Sanket Patel on 27-09-2018.
 */


public class SqliteDatabase extends SQLiteOpenHelper {

    private static final int DATABASE_VERSION = 5;
    private static final String DATABASE_NAME = "product";
    private static final String TABLE_PRODUCTS = "products";

    private static final String COLUMN_ID = "_id";
    private static final String COLUMN_PRODUCTNAME = "productname";
    private static final String COLUMN_QUANTITY = "quantity";
    private static final String COLUMN_URI = "uri";

    public SqliteDatabase(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }


    public interface OnDatabaseChangeListener {
        void itemAdded(Product product);

        void itemChanged(Product product);

        void itemRemoved(int itemID);

    }

    public OnDatabaseChangeListener onDatabaseChangeListener;

    @Override
    public void onCreate(SQLiteDatabase db) {
        String CREATE_PRODUCTS_TABLE =
                "CREATE	TABLE " + TABLE_PRODUCTS + "(" + COLUMN_ID +
                        " INTEGER PRIMARY KEY," + COLUMN_PRODUCTNAME +
                        " TEXT," + COLUMN_QUANTITY + " INTEGER," + COLUMN_URI + " TEXT)";

        db.execSQL(CREATE_PRODUCTS_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_PRODUCTS);
        onCreate(db);
    }

    public List<Product> listProducts() {
        String sql = "select * from " + TABLE_PRODUCTS;
        SQLiteDatabase db = this.getReadableDatabase();
        List<Product> storeProducts = new ArrayList<>();
        Cursor cursor = db.rawQuery(sql, null);
        if (cursor.moveToFirst()) {
            do {
                int id = Integer.parseInt(cursor.getString(0));
                String name = cursor.getString(1);
                String quantity = (cursor.getString(2));
                String uri = (cursor.getString(3));
                Product product = new Product(id, name, quantity, uri);
                storeProducts.add(product);

            } while (cursor.moveToNext());
        }
        cursor.close();
        return storeProducts;
    }

    public void addProduct(Product product) {

        ContentValues values = new ContentValues();
        values.put(COLUMN_PRODUCTNAME, product.getName());
        values.put(COLUMN_QUANTITY, product.getQuantity());
        values.put(COLUMN_URI, product.getUri());
        SQLiteDatabase db = this.getWritableDatabase();
        db.insert(TABLE_PRODUCTS, null, values);

        if (onDatabaseChangeListener != null){
            onDatabaseChangeListener.itemAdded(product);
        }
    }

    public void updateProduct(Product product) {
        ContentValues values = new ContentValues();
        values.put(COLUMN_PRODUCTNAME, product.getName());
        values.put(COLUMN_QUANTITY, product.getQuantity());
        values.put(COLUMN_URI, product.getUri());

        if (onDatabaseChangeListener != null){
            onDatabaseChangeListener.itemChanged(product);
        }

        SQLiteDatabase db = this.getWritableDatabase();
        db.update(TABLE_PRODUCTS, values, COLUMN_ID + "	= ?", new String[]{String.valueOf(product.getId())});
    }

    public Product findProduct(String name) {
        String query = "Select * FROM " + TABLE_PRODUCTS + " WHERE " + COLUMN_PRODUCTNAME + " = " + "name";
        SQLiteDatabase db = this.getWritableDatabase();
        Product mProduct = null;
        Cursor cursor = db.rawQuery(query, null);
        if (cursor.moveToFirst()) {
            int id = Integer.parseInt(cursor.getString(0));
            String productName = cursor.getString(1);
            String productQuantity = (cursor.getString(2));
            String uri = (cursor.getString(3));
            mProduct = new Product(id, productName, productQuantity, uri);
        }
        cursor.close();
        return mProduct;
    }

    public void setOnDatabaseChangeListener(OnDatabaseChangeListener onDatabaseChangeListener) {
        this.onDatabaseChangeListener = onDatabaseChangeListener;
    }

    public void deleteProduct(int id) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_PRODUCTS, COLUMN_ID + "	= ?", new String[]{String.valueOf(id)});
        if (onDatabaseChangeListener != null){
            onDatabaseChangeListener.itemRemoved(id);
        }

    }
}

