package co.unal.sqliteempresas.dao;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import androidx.annotation.Nullable;

public class ConexionSQLiteHelper extends SQLiteOpenHelper {

    public ConexionSQLiteHelper(@Nullable Context context, @Nullable String name, @Nullable SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        sqLiteDatabase.execSQL(COMPANIESDao.CREATE_TABLE_COMPANIES);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int versionAntigua, int versionNueva) {
        sqLiteDatabase.execSQL(COMPANIESDao.DROP_TABLE_COMPANIES);
        onCreate(sqLiteDatabase);
    }
}
