package com.example.mmh_49.employeemanagement;


import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;

/**
 * Created by MMH-49 on 7/8/2020.
 */

public class SqliteDatabase extends SQLiteOpenHelper {

    private	static final int DATABASE_VERSION =	6;
    private	static final String	DATABASE_NAME = "Employee";
    private	static final String TABLE_EMPLOYEES = "Employees";

    private static final String COLUMN_ID = "_id";
    private static final String COLUMN_NAME = "employeeName";
    private static final String COLUMN_AGE = "employeeAge";
    private static final String COLUMN_GENDER = "employeeGender";
    private static final String COLUMN_IMAGE = "employeeImage";

    public SqliteDatabase(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(android.database.sqlite.SQLiteDatabase db) {

        String	CREATE_CONTACTS_TABLE = "CREATE	TABLE " + TABLE_EMPLOYEES + "(" + COLUMN_ID + " INTEGER PRIMARY KEY," + COLUMN_NAME + " TEXT," + COLUMN_AGE + " INTEGER," + COLUMN_GENDER + " TEXT," + COLUMN_IMAGE + " TEXT"  + ")";
        db.execSQL(CREATE_CONTACTS_TABLE);

    }

    @Override
    public void onUpgrade(android.database.sqlite.SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_EMPLOYEES);
        onCreate(db);
    }

    public ArrayList<EmployeeModel> listEmployees(){  ///Data retrieve
        String sql = "select * from " + TABLE_EMPLOYEES;
        android.database.sqlite.SQLiteDatabase db = this.getReadableDatabase();
        ArrayList<EmployeeModel> storeEmployees = new ArrayList<>(); ///Empty list array... to keep the retrieve values
        Cursor cursor = db.rawQuery(sql, null);

        if(cursor.moveToFirst()){
            do{
                int id = Integer.parseInt(cursor.getString(0));
                String name = cursor.getString(1);
                String age = cursor.getString(2);
                String gender = cursor.getString(3);
                String image = cursor.getString(4);
                storeEmployees.add(new EmployeeModel(id, name, age, gender, image)); ///Retrieve data
            }while (cursor.moveToNext());
        }
        cursor.close();
        return storeEmployees;
    }

    public void addEmployees(EmployeeModel employeeModel){
        ContentValues values = new ContentValues();  ///contents...
        values.put(COLUMN_NAME, employeeModel.getName());
        values.put(COLUMN_AGE, employeeModel.getAge());
        values.put(COLUMN_GENDER, employeeModel.getGender());
        values.put(COLUMN_IMAGE, employeeModel.getImg());

        android.database.sqlite.SQLiteDatabase db = this.getWritableDatabase();
        db.insert(TABLE_EMPLOYEES, null, values);
    }

    public void updateEmployee(EmployeeModel employeeModel){
        ContentValues values = new ContentValues();
        values.put(COLUMN_NAME, employeeModel.getName());
        values.put(COLUMN_AGE, employeeModel.getAge());
        values.put(COLUMN_GENDER, employeeModel.getGender());
        values.put(COLUMN_IMAGE, String.valueOf(employeeModel.getImg()));
        android.database.sqlite.SQLiteDatabase db = this.getWritableDatabase();
        db.update(TABLE_EMPLOYEES, values, COLUMN_ID	+ "	= ?", new String[] { String.valueOf(employeeModel.getId())});
    }

    public EmployeeModel findEmployees(String name){
        String query = "Select * FROM "	+ TABLE_EMPLOYEES + " WHERE " + COLUMN_NAME + " = " + "name";
        android.database.sqlite.SQLiteDatabase db = this.getWritableDatabase();
        EmployeeModel employeeModel = null;

        Cursor cursor = db.rawQuery(query,	null);
        if	(cursor.moveToFirst()){
            int id = Integer.parseInt(cursor.getString(0));
            String employeeName = cursor.getString(1);
            String employeeAge = cursor.getString(2);
            String employeeGender = cursor.getString(3);
            String employeeImage = cursor.getString(4);
            employeeModel = new EmployeeModel(id, employeeName, employeeAge, employeeGender, employeeImage);
        }
        cursor.close();
        return employeeModel;
    }

    public void deleteEmployee(int id){
        android.database.sqlite.SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_EMPLOYEES, COLUMN_ID	+ "	= ?", new String[] { String.valueOf(id)});
    }
}