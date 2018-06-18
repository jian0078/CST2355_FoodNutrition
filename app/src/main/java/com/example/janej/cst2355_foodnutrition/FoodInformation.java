package com.example.janej.cst2355_foodnutrition;


import android.app.Activity;

public class FoodInformation extends Activity {

    public String food,day,hour,minute,calories,totalFat,totalCarbohydrate;

    public FoodInformation(){}

    public FoodInformation(String a,String b,String c,String d,String e,String f,String g){

        this.food = a;
        this.day = b;
        this.hour = c;
        this.minute = d;
        this.calories = e;
        this.totalFat = f;
        this.totalCarbohydrate = g;

    }
}
