package com.example.janej.cst2355_foodnutrition;


import android.app.Activity;

public class FoodInformation extends Activity {

    public String food,time,calories,totalFat,totalCarbohydrate;

    public FoodInformation(){}

    public FoodInformation(String a,String b,String c,String d,String e){

        this.food = a;
        this.time = b;
        this.calories = c;
        this.totalFat = d;
        this.totalCarbohydrate = e;

    }
}