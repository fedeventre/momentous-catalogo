package examen.momentoustech.com.catalogo.manager;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;


import java.util.ArrayList;
import java.util.List;

import examen.momentoustech.com.catalogo.data.DatabaseHelper;
import examen.momentoustech.com.catalogo.data.ProductItemContract;
import examen.momentoustech.com.catalogo.data.ProductItemContract.ProductEntry;
import examen.momentoustech.com.catalogo.model.Producto;

/**
 * Created by fede on 10/26/16.
 */

public class DatabaseManager {

    private static final String TAG = "DatabaseManager";
    private static DatabaseManager mInstance = new DatabaseManager();
    private SQLiteDatabase mDatabase;
    private DatabaseHelper mDatabaseHelper;

    public static DatabaseManager getInstance() {
        return mInstance;
    }

    private DatabaseManager() {
    }

    public void init(Context context) {
        mDatabaseHelper = new DatabaseHelper(context);
        mDatabase = mDatabaseHelper.getWritableDatabase();
    }

    public void close() {
        mDatabase.close();
    }

    /*
     * MANAGE PRODUCTO
     */
    private long createFeedItem(Producto producto) {

        ContentValues values = new ContentValues();

        values.put(ProductEntry.COLUMN_NAME_PRODUCT_ID, producto.getProductorId());
        values.put(ProductEntry.COLUMN_NAME_NAME, producto.getName());
        values.put(ProductEntry.COLUMN_NAME_DETAIL, producto.getDetail());
        values.put(ProductEntry.COLUMN_NAME_IMAGE_URL, producto.getImage());

        return mDatabase.insert(ProductEntry.TABLE_NAME, null, values);
    }

    public Producto getProductById(String id) {
        Producto producto = null;
        String selection = ProductEntry.COLUMN_NAME_PRODUCT_ID + " = ?";
        String[] selectionArgs = {id};

        Cursor cursor = mDatabase.query(ProductEntry.TABLE_NAME, ProductItemContract.SELECT_ALL, selection,
                selectionArgs, null, null, null);

        if (cursor != null) {
            cursor.moveToFirst();
            if (cursor.getCount() > 0) {
                producto = feedItemFromCursor(cursor);
            }
        }


        return producto;
    }

    public List<Producto> searchProductByName(String name) {
        List<Producto> productosList = new ArrayList<>();
        String selection = ProductEntry.COLUMN_NAME_NAME + " LIKE ?";
        String[] selectionArgs = {"%"+name+"%"};

        Cursor cursor = mDatabase.query(ProductEntry.TABLE_NAME, ProductItemContract.SELECT_ALL, selection,
                selectionArgs, null, null, null);

        if (cursor != null) {
            while (cursor.moveToNext()) {
                Producto producto = feedItemFromCursor(cursor);
                productosList.add(producto);
            }
        }


        return productosList;
    }

    public List<Producto> getProductos(boolean asc, int limit,) {
        List<Producto> productosList = new ArrayList<>();
        String orderBy = asc ? ProductEntry.COLUMN_NAME_NAME +  " ASC" : null;

        Cursor cursor = mDatabase.query(ProductEntry.TABLE_NAME, ProductItemContract.SELECT_ALL,
                null, null, null, null, orderBy);

        while (cursor.moveToNext()) {
            Producto producto = feedItemFromCursor(cursor);
            productosList.add(producto);
        }


        return productosList;
    }

    public void saveProduct(List<Producto> feedItems) {
        Log.i(TAG, "Saving " + feedItems.size() + " Products items");
        for (Producto producto : feedItems) {
            Producto prod = getProductById(producto.getProductorId());

            if (prod == null) {
                createFeedItem(producto);
            }
        }

    }

    public void deleteAllProducts() {
        Log.i(TAG, "Deleting all Products items");
        mDatabase.delete(ProductEntry.TABLE_NAME, null, null);
    }

    private Producto feedItemFromCursor(Cursor cursor) {
        return new Producto(
                cursor.getString(cursor.getColumnIndex(ProductEntry.COLUMN_NAME_PRODUCT_ID)),
                cursor.getString(cursor.getColumnIndex(ProductEntry.COLUMN_NAME_NAME)),
                cursor.getString(cursor.getColumnIndex(ProductEntry.COLUMN_NAME_IMAGE_URL)),
                cursor.getString(cursor.getColumnIndex(ProductEntry.COLUMN_NAME_DETAIL))
        );
    }


}
