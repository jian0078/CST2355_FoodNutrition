package com.example.janej.cst2355_foodnutrition;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class FoodDatabaseHelper extends SQLiteOpenHelper {
    private static String DATABASE_NAME = "FoodNutritionInformation.db";
    private static int VERSION_NUM = 1;
    public static final String tableName = "FoodTable";
    public final static String Key_ID = "_id";
    public final static String Key_FOOD = "Food";
    public final static String Key_Day = "Day";
    public final static String Key_Hour = "Hour";
    public final static String Key_Minute = "Minute";
    public final static String Key_Calories = "Calories";
    public final static String Key_Fat = "TotalFat";
    public final static String Key_Carbohydrate = "TotalCarbohydrate";


    public FoodDatabaseHelper(Context ctx) {
        super(ctx, DATABASE_NAME, null, VERSION_NUM);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE " + tableName +
                " ( _id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "Food text, Day text, Hour text, Minute text, Calories text, " +
                "TotalFat text, TotalCarbohydrate text);");
    }

    @Override
    public void onOpen(SQLiteDatabase db){ }

    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + tableName);
        onCreate(db);
    }
}



