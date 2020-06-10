package com.example.workmanagersample.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import java.util.ArrayList;

public class DbHelper extends SQLiteOpenHelper {
    private static String dbName = "database.db";
    private static String tbName = "images";
    public static final String CONTACTS_COLUMN_ID = "id";
    public static final String CONTACTS_COLUMN_IMAGE = "image";
    public static final String CONTACTS_COLUMN_TYPE = "type";

    public DbHelper(Context context) {
        super(context, dbName, null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("create table images (id integer primary key,type integer, image blob)");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("drop table if exists images");
        onCreate(db);
    }

    public boolean insertImage(byte[] imageBytes, int type) {
        clearTable();
        SQLiteDatabase writableDatabase = this.getWritableDatabase();

        ContentValues contentValues = new ContentValues();
        contentValues.put(CONTACTS_COLUMN_IMAGE, imageBytes);
        contentValues.put(CONTACTS_COLUMN_TYPE, type);

        writableDatabase.insert(tbName, null, contentValues);
        writableDatabase.close();
        return true;
    }

    public void clearTable() {
        SQLiteDatabase writableDatabase = this.getWritableDatabase();
        String countSql = "select count(*) from " + tbName;
        Cursor cursor = writableDatabase.rawQuery(countSql, null);
        int tableCount = 0;
        if (cursor.moveToFirst()) {
            tableCount = cursor.getInt(0);
            cursor.close();
        }
        if (tableCount > 1000) {
            String sql = "delete from " + tbName;
            writableDatabase.execSQL(sql);
        }
        if (!cursor.isClosed()) {
            cursor.close();
        }
        writableDatabase.close();

    }

    public Bitmap getLastImage(int count, int type) {
        SQLiteDatabase readableDatabase = this.getReadableDatabase();
        String sql = "select * from images where type = " + type + " order by id DESC limit " + count;
        Cursor cursor = readableDatabase.rawQuery(sql, null);
        if (cursor.moveToFirst()) {
            byte[] bytes = cursor.getBlob(2);
            cursor.close();
            return BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
        }
        if (!cursor.isClosed()) {
            cursor.close();
        }
        return null;
    }

    public ArrayList<Bitmap> getImages(int type) {
        SQLiteDatabase readableDatabase = this.getReadableDatabase();
        ArrayList<Bitmap> pictures = new ArrayList<>();
        String sql = "select * from images where type = " + type + " order by id DESC";
        Cursor cursor = readableDatabase.rawQuery(sql, null);
        if (cursor.moveToFirst()) {
            do {
                byte[] bytes = cursor.getBlob(2);
                pictures.add(BitmapFactory.decodeByteArray(bytes, 0, bytes.length));
            } while (cursor.moveToNext());
        }

        if (!cursor.isClosed()) {
            cursor.close();
        }
        return pictures;
    }

}
