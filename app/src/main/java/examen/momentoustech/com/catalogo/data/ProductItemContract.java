package examen.momentoustech.com.catalogo.data;

import android.provider.BaseColumns;

/**
 * Created by fede on 10/26/16.
 */

public final class ProductItemContract {

    private static final String TEXT_TYPE = " TEXT";
    private static final String COMMA_SEP = ",";


    public static final String[] SELECT_ALL = new String[]{
            ProductEntry.COLUMN_NAME_PRODUCT_ID,
            ProductEntry.COLUMN_NAME_NAME,
            ProductEntry.COLUMN_NAME_DETAIL,
            ProductEntry.COLUMN_NAME_IMAGE_URL
    };

    public static final String SQL_CREATE_TABLE = "CREATE TABLE "
            + ProductEntry.TABLE_NAME + " ("
            + ProductEntry.COLUMN_NAME_PRODUCT_ID + TEXT_TYPE + COMMA_SEP
            + ProductEntry.COLUMN_NAME_NAME + TEXT_TYPE + COMMA_SEP
            + ProductEntry.COLUMN_NAME_DETAIL + TEXT_TYPE + COMMA_SEP
            + ProductEntry.COLUMN_NAME_IMAGE_URL + TEXT_TYPE
            + ");";

    public static abstract class ProductEntry implements BaseColumns {
        public static final String TABLE_NAME = "producto";

        public static final String COLUMN_NAME_PRODUCT_ID = "productId";
        public static final String COLUMN_NAME_NAME = "name";
        public static final String COLUMN_NAME_DETAIL = "detail";
        public static final String COLUMN_NAME_IMAGE_URL = "imageURL";
        }

}
