package com.example.titaniumturtle;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.widget.Toast;

import androidx.annotation.Nullable;

public class Database extends SQLiteOpenHelper {

    private Context context;
    private static final String DB_NAME = "App_Collection.db";
    private static final int DB_VERSION = 1;

    private static final String TABLE_NAME = "My_Collection";
    private static final String COLUMN_ID = "_id";
    private static final String COLUMN_NAME = "NAME";
    private static final String COLUMN_NOTE = "NOTE";
    private static final String COLUMN_PIC = "PIC";


    public Database(@Nullable Context context) {
        super(context, DB_NAME, null, DB_VERSION);
        this.context = context;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String query =          "CREATE TABLE " + TABLE_NAME +
                                " (" + COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                                COLUMN_NAME + " TEXT, " +
                                COLUMN_NOTE + " TEXT, " +
                                COLUMN_PIC + " TEXT);";
        db.execSQL(query);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
        onCreate(db);
    }

    void addItem(String name, String note, String pic){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues cv = new ContentValues();

        cv.put(COLUMN_NAME, name);
        cv.put(COLUMN_NOTE, note);
        cv.put(COLUMN_PIC, pic);
        long result = db.insert(TABLE_NAME, null, cv);

        if(result == -1){
            Toast.makeText(context, R.string.InsertFail, Toast.LENGTH_SHORT).show();
        }else{
            Toast.makeText(context, R.string.InsertSucceed, Toast.LENGTH_SHORT).show();
        }
    }

    public void updateItem(String row_id, String name, String note, String pic){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put(COLUMN_NAME, name);
        cv.put(COLUMN_NOTE, note);
        cv.put(COLUMN_PIC, pic);

        long result = db.update(TABLE_NAME, cv, "_id=?", new String[]{row_id});

        if(result == -1){
            Toast.makeText(context, R.string.UpdateFail, Toast.LENGTH_LONG).show();
        }else{
            Toast.makeText(context, R.string.UpdateComplete, Toast.LENGTH_LONG).show();
        }
    }

    public void deleteItem(String row_id){
        SQLiteDatabase db = this.getWritableDatabase();
        long result = db.delete(TABLE_NAME, "_id=?", new String[]{row_id});

        if(result == -1){
            Toast.makeText(context, R.string.FailDelete, Toast.LENGTH_LONG).show();
        }else{
            Toast.makeText(context, R.string.CompleteDelete, Toast.LENGTH_LONG).show();
        }
    }

    Cursor readAllData(){
        String query = "Select * from " + TABLE_NAME;
        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = null;
        if(db != null){
            cursor = db.rawQuery(query, null);
        }
        return cursor;
    }
}
