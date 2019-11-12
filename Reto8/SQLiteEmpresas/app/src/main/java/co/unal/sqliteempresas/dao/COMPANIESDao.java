package co.unal.sqliteempresas.dao;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

public class COMPANIESDao {

    public final static String DB_COMPANY = "COMPANY";
    public final static String TABLE_COMPANIES = "COMPANIES";
    public final static String FIELD_ID_COMPANY = "id_company";
    public final static String FIELD_NAME_COMPANY = "name_company";
    public final static String FIELD_URL_COMPANY = "url_company";
    public final static String FIELD_PHONE_COMPANY = "phone_company";
    public final static String FIELD_EMAIL_COMPANY = "email_company";
    public final static String FIELD_LIST_PRODUCT_COMPANY = "list_product_company";
    public final static String FIELD_AREA_COMPANY = "area_company";

    public final static String CREATE_TABLE_COMPANIES = "CREATE TABLE " + TABLE_COMPANIES + " ("
            + FIELD_ID_COMPANY + " INTEGER PRIMARY KEY AUTOINCREMENT, "
            + FIELD_NAME_COMPANY + " TEXT, "
            + FIELD_URL_COMPANY + " TEXT, "
            + FIELD_PHONE_COMPANY + " TEXT, "
            + FIELD_EMAIL_COMPANY + " TEXT, "
            + FIELD_LIST_PRODUCT_COMPANY + " TEXT, "
            + FIELD_AREA_COMPANY + " TEXT)";

    public final static String DROP_TABLE_COMPANIES = "DROP TABLE IF EXISTS" + TABLE_COMPANIES;

    public List loadAll(ConexionSQLiteHelper conn) {

        SQLiteDatabase db = conn.getReadableDatabase();

        List<COMPANIES> listCompanies = new ArrayList<COMPANIES>();
        Cursor cursor = db.rawQuery("SELECT * FROM " + TABLE_COMPANIES, null);
        while (cursor.moveToNext()) {
            COMPANIES companies = new COMPANIES();
            companies.setId(cursor.getInt(0));
            companies.setName(cursor.getString(1));
            companies.setUrl(cursor.getString(2));
            companies.setPhone(cursor.getString(3));
            companies.setEmail(cursor.getString(4));
            companies.setListProduct(cursor.getString(5));
            companies.setArea(cursor.getString(6));
            listCompanies.add(companies);
        }

        db.close();
        return listCompanies;
    }

    public long create(ConexionSQLiteHelper conn, COMPANIES valueObject) {
        SQLiteDatabase db = conn.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(FIELD_NAME_COMPANY, valueObject.getName());
        values.put(FIELD_URL_COMPANY, valueObject.getUrl());
        values.put(FIELD_PHONE_COMPANY, valueObject.getPhone());
        values.put(FIELD_EMAIL_COMPANY, valueObject.getEmail());
        values.put(FIELD_LIST_PRODUCT_COMPANY, valueObject.getListProduct());
        values.put(FIELD_AREA_COMPANY, valueObject.getArea());

        Long id = db.insert(TABLE_COMPANIES, FIELD_ID_COMPANY, values);
        db.close();
        return id;
    }

    public int save(ConexionSQLiteHelper conn, COMPANIES valueObject) {
        SQLiteDatabase db = conn.getWritableDatabase();

        String[] args = {valueObject.getId().toString()};
        ContentValues values = new ContentValues();
        values.put(FIELD_NAME_COMPANY, valueObject.getName());
        values.put(FIELD_URL_COMPANY, valueObject.getUrl());
        values.put(FIELD_PHONE_COMPANY, valueObject.getPhone());
        values.put(FIELD_EMAIL_COMPANY, valueObject.getEmail());
        values.put(FIELD_LIST_PRODUCT_COMPANY, valueObject.getListProduct());
        values.put(FIELD_AREA_COMPANY, valueObject.getArea());

        int rows = db.update(TABLE_COMPANIES, values, FIELD_ID_COMPANY + "=?", args);
        db.close();
        return rows;
    }

    public int delete(ConexionSQLiteHelper conn, COMPANIES valueObject) {
        SQLiteDatabase db = conn.getWritableDatabase();
        String[] args = {valueObject.getId().toString()};
        int rows = db.delete(TABLE_COMPANIES, FIELD_ID_COMPANY + "=?", args);
        db.close();
        return rows;
    }

    public List searchMatching(ConexionSQLiteHelper conn, COMPANIES valueObject) {
        SQLiteDatabase db = conn.getReadableDatabase();

        String name = valueObject.getName();
        if (!name.isEmpty()) {
            name = concatForLike(name);
        }

        String area1 = "", area2 = "", area3 = "";
        String[] area = valueObject.getArea().split(";");
        if (area.length == 3) {
            area1 = concatForLike(area[0]);
            area2 = concatForLike(area[1]);
            area3 = concatForLike(area[2]);
        } else if(area.length == 2){
            area1 = concatForLike(area[0]);
            area2 = concatForLike(area[1]);
            area3 = area1;
        } else if(!area[0].isEmpty()) {
            area1 = concatForLike(area[0]);
            area2 = area1;
            area3 = area1;
        }

        List<COMPANIES> listCompanies = new ArrayList<COMPANIES>();
        String[] args = {name, area1, area2, area3};
        Cursor cursor = db.query(TABLE_COMPANIES, null, FIELD_NAME_COMPANY + " LIKE ?" + " OR (" + FIELD_AREA_COMPANY + " LIKE ?" + " AND " + FIELD_AREA_COMPANY + " LIKE ?" + " AND " + FIELD_AREA_COMPANY + " LIKE ?)", args, null, null, null);

        while (cursor.moveToNext()) {
            COMPANIES companies = new COMPANIES();
            companies.setId(cursor.getInt(0));
            companies.setName(cursor.getString(1));
            companies.setUrl(cursor.getString(2));
            companies.setPhone(cursor.getString(3));
            companies.setEmail(cursor.getString(4));
            companies.setListProduct(cursor.getString(5));
            companies.setArea(cursor.getString(6));
            listCompanies.add(companies);
        }

        db.close();
        return listCompanies;
    }

    private String concatForLike(String tmp) {
        return "%" + tmp + "%";
    }
}
