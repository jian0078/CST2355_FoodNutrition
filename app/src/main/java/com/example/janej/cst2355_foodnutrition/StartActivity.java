package com.example.janej.cst2355_foodnutrition;



import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

public class StartActivity extends AppCompatActivity {
    FloatingActionButton flButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start);
        flButton = findViewById(R.id.infoButton);
        Toolbar toolbar = findViewById(R.id.start_toolbar);
        setSupportActionBar(toolbar);

        //Help Button
        flButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(v.getContext());
                builder.setTitle(R.string.alertFinalProject);
                builder.setMessage("Yi Jiang - Food Nutrition Tracker\n");
// Add the button
                builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                    }
                });
                // Create the AlertDialog
                AlertDialog dialog = builder.create();
                dialog.show();
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu m) {
        super.onCreateOptionsMenu(m);
        getMenuInflater().inflate(R.menu.toolbar_menu, m );
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem mi) {
        super.onOptionsItemSelected(mi);
        int id = mi.getItemId();
        switch (id) {
            case R.id.action_two:
                //Nutrition Tracker
                Intent intent2 = new Intent(StartActivity.this, FoodList.class);
                startActivity(intent2);
                break;
//            case R.id.action_one:
//                //Activity Tracking
//                Intent intent1 = new Intent(StartActivity.this,DashBoardOfActivityTracking.class);
//                startActivity(intent1);
//                break;
//
//            case R.id.action_three:
//                //Start Thermostat Activity
//                Intent intent3 = new Intent(getApplicationContext(), ThermostatActivity.class);
//                startActivity(intent3);
//                break;
//            case R.id.action_four:
//                //Start Automobile Activity
//                Intent intent4 = new Intent(getApplicationContext(), AutomobileActivity.class);
//                startActivity(intent4);
//                break;
            case R.id.action_five:
                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setTitle(R.string.alertFinalProject);
                builder.setMessage(getResources().getString(R.string.intro));
// Add the button
                builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                    }
                });
                // Create the AlertDialog
                AlertDialog dialog = builder.create();
                dialog.show();
                break;
        }
        return true;
    }


    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public void onStart(){
        super.onStart();
    }
    @Override
    public void onPause(){
        super.onPause();
    }

    @Override
    public void onStop(){
        super.onStop();
    }

    @Override
    public void onDestroy(){
        super.onDestroy();
    }


}