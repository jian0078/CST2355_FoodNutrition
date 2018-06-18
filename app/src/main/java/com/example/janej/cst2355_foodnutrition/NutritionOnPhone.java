package com.example.janej.cst2355_foodnutrition;


import android.app.Activity;
import android.app.FragmentTransaction;
import android.os.Bundle;

public class NutritionOnPhone extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_nutrition_on_phone);

        Bundle bundle = getIntent().getBundleExtra("bundle");

        NutritionDetails nd = new NutritionDetails();
        nd.setArguments(bundle);

        FragmentTransaction ft =  getFragmentManager().beginTransaction();
        ft.replace(R.id.emptyFrameLayout, nd);
        ft.commit();
    }
}
