package com.olliesafety2.simpleapplocker.DataBase;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class Password_Database extends SQLiteOpenHelper {

    // this is database class, database is sqlite
    // embedded in android studio

    // database name
    public static final String DATABASE_NAME = "pass_data.db";
    // table name
    public static final String TABLE_NAME = "password_table";

    // columns
    public static final String col0 = "id";
    public static final String col1 = "username";
    public static final String col2 = "useremail";
    public static final String col3 = "phone_num";
    public static  final String col4= "user_type";
    public static final String col5 = "status";
    public static final String col6 = "adminEmail";
    public static final String col7 = "weeklyDate";
    public static final String col8 = "weeklySpeed";
    public static final String col9 = "monthlyDate";
    public static final String col10 = "monthlySpeed";


    // constructor
    public Password_Database(Context context) {
        super(context, DATABASE_NAME, null, 1);
        // SQLiteDatabase db = this.getWritableDatabase();
    }

    // create table
    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE " + TABLE_NAME + "(id TEXT ,username TEXT,useremail TEXT,phone_num TEXT,user_type TEXT,status TEXT,adminEmail TEXT,weeklyDate TEXT,weeklySpeed TEXT,monthlyDate TEXT,monthlySpeed TEXT) ");
    }
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
    }
    // insert data into table
    public boolean insertData(String id) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(col0, id);
        long result = db.insert(TABLE_NAME, null, contentValues);
        db.close();
        if (result == -1) {
            return false;
        } else {
            return true;
        }
    }
    // read data from table
    public Cursor getAllData() {

        SQLiteDatabase db = this.getWritableDatabase();
        Cursor res = db.rawQuery("Select * from " + TABLE_NAME, null);
        return res;
    }
    // update data in table

    public boolean updateData( String id) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(col0,id);
        db.update(TABLE_NAME, contentValues, null,null);
        return true;
    }
    public boolean updateUserData(String username, String useremail,String phoneNum,String user_type) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(col1, username);
        contentValues.put(col2, useremail);
        contentValues.put(col3, phoneNum);
        contentValues.put(col4, user_type);
        db.update(TABLE_NAME, contentValues, null, null);
        return true;
    }
    public boolean updateStatus(String status) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(col5, status);
        db.update(TABLE_NAME, contentValues, null, null);
        return true;
    }
    public boolean updateWeeklyDate(String data) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(col7, data);
        db.update(TABLE_NAME, contentValues, null, null);
        return true;
    }
    public boolean updateWeeklySpeed(String data) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(col8, data);
        db.update(TABLE_NAME, contentValues, null, null);
        return true;
    }
    public boolean updateMonthlyDate(String data) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(col9, data);
        db.update(TABLE_NAME, contentValues, null, null);
        return true;
    }
    public boolean updateMonthlySpeed(String data) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(col10, data);
        db.update(TABLE_NAME, contentValues, null, null);
        return true;
    }
    public boolean updateAdminEmail(String email) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(col6, email);
        db.update(TABLE_NAME, contentValues, null, null);
        return true;
    }
    public String getAdminEmail(){
        String adminEmail="";
        SQLiteDatabase db=this.getReadableDatabase();
        Cursor cursor=db.rawQuery("select "+col6+" from "+TABLE_NAME+" where id=1 ",null );
        if (cursor.moveToFirst()) {
             adminEmail = cursor.getString(cursor.getColumnIndex(col6));
        } else {
            adminEmail = "noAdmin";
        }
        if(adminEmail==null){
            adminEmail = "noAdmin";
        }
        return adminEmail;

    }
    public String getPhoneNum(){
        String PhoneNum="";
        SQLiteDatabase db=this.getReadableDatabase();
        Cursor cursor=db.rawQuery("select "+col3+" from "+TABLE_NAME+" where id=1 ",null );
        if (cursor.moveToFirst()) {
            PhoneNum = cursor.getString(cursor.getColumnIndex(col3));
        }
        return PhoneNum;

    }

    // delete data from table
    public Integer deleteData(String name) {
        SQLiteDatabase db = this.getWritableDatabase();
        int i = db.delete(TABLE_NAME, col1 + " =?", new String[]{name});
        return i;
    }
    public Boolean deleteDataBase(Context context) {
        return context.deleteDatabase(DATABASE_NAME);
    }
}
