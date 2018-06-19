package com.example.janej.cst2355_foodnutrition;

import android.app.FragmentTransaction;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import android.support.design.widget.Snackbar;
import android.widget.Toast;

public class FoodList extends AppCompatActivity {

    private ArrayList<FoodInformation> foodArray = new ArrayList<>();
    private ListView foodListView;
    private Button addFoodButton;
    private Button calAvgButton;
    private Button calTotalButton;
    private FoodDatabaseHelper fdHelper;
    private SQLiteDatabase db;
    private FoodAdapter foodAdapter;
    private Cursor c;
    private FrameLayout tabletLayOut;
    private Boolean onTablet;
    private Toolbar foodToolbar;
    private ProgressBar pBar;
    private Long dataID;
    private int viewPosition;
    private Boolean clickAdd;
    private Handler handler;
    private int timerCounter;
    private Runnable runnable;
    private double total;
    private int caloriesLastDay;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_food_list);

        pBar = findViewById(R.id.foodProgressBar);
        foodListView = findViewById(R.id.foodListView);
        addFoodButton = findViewById(R.id.addFoodButton);
        calAvgButton = findViewById(R.id.calculateAverageCal);
        calTotalButton = findViewById(R.id.calculateTotalCal);
        foodToolbar = findViewById(R.id.foodToolbar);
        setSupportActionBar(foodToolbar);

        tabletLayOut=findViewById(R.id.foodTableFrameLayout);
        if(tabletLayOut == null) onTablet=false;
        else onTablet=true;

        foodAdapter =new FoodAdapter(this);
        foodListView.setAdapter(foodAdapter);

        timerCounter=0;
        handler = new Handler(Looper.getMainLooper());
        runnable = new Runnable() {
            @Override
            public void run() {
                if(timerCounter<100) {
                    addFoodButton.setEnabled(false);
                    calAvgButton.setEnabled(false);
                    calTotalButton.setEnabled(false);
                    //This idea is taken from: https://stackoverflow.com/questions/36918219/how-to-disable-user-interaction-while-progressbar-is-visible-in-android
                    //How to disable user interaction while ProgressBar is visible in android?
                    getWindow().setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE,
                                         WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
                    if (timerCounter == 0)
                        Toast.makeText(FoodList.this, R.string.loading_database, Toast.LENGTH_LONG).show();
                    if (timerCounter == 25)
                        Toast.makeText(FoodList.this, R.string.loading_database, Toast.LENGTH_SHORT).show();
                    if (timerCounter == 50)
                        Toast.makeText(FoodList.this, R.string.loading_database, Toast.LENGTH_SHORT).show();
                    if (timerCounter == 75)
                        Toast.makeText(FoodList.this, R.string.loading_database, Toast.LENGTH_SHORT).show();
                }
                if(timerCounter==100) {
                    addFoodButton.setEnabled(true);
                    calAvgButton.setEnabled(true);
                    calTotalButton.setEnabled(true);
                    getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
                    Toast.makeText(FoodList.this, R.string.loading_complete, Toast.LENGTH_LONG).show();
                }
                timerCounter++;
                handler.postDelayed(this,90);
            }
        };
        handler.postDelayed(runnable, 1000);

        BackgroundRun br = new BackgroundRun();
        br.execute();

        addFoodButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                clickAdd = true;
                Bundle bd = new Bundle();
                bd.putInt("TODO",1);//tell fragment set all fields to empty

                if(onTablet==true){
                    NutritionDetails nd = new NutritionDetails(FoodList.this);
                    nd.setArguments(bd);
                    FragmentTransaction ft =  getFragmentManager().beginTransaction();
                    ft.replace(R.id.foodTableFrameLayout,nd);
                    ft.addToBackStack(null);
                    ft.commit();
                }
                else{
                    Intent phoneIntent = new Intent(FoodList.this, NutritionOnPhone.class);
                    phoneIntent.putExtra("bundle",bd);
                    startActivityForResult(phoneIntent,21);
                }
            }
        });

        foodListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                clickAdd = false;
                viewPosition = i;
                dataID = l;
                FoodInformation fi = foodArray.get(i);
                String [] tempArray = new String[5];
                tempArray[0] = fi.food;
                tempArray[1] = fi.time;
                tempArray[2] = fi.calories;
                tempArray[3] = fi.totalFat;
                tempArray[4] = fi.totalCarbohydrate;

                Bundle foodBundle = new  Bundle();
                foodBundle.putInt("TODO", 2);//tell fragment load these info into fields
                foodBundle.putStringArray("foodInfo", tempArray);
                foodBundle.putInt("viewPosition",viewPosition);
                foodBundle.putLong("id",dataID);

                if(onTablet==true){
                    NutritionDetails nd = new NutritionDetails(FoodList.this);
                    nd.setArguments(foodBundle);
                    FragmentTransaction ft =  getFragmentManager().beginTransaction();
                    ft.replace(R.id.foodTableFrameLayout,nd);
                    ft.addToBackStack(null);
                    ft.commit();
                }
                else{
                    Intent phoneIntent = new Intent(FoodList.this, NutritionOnPhone.class);
                    phoneIntent.putExtra("bundle",foodBundle);
                    startActivityForResult(phoneIntent,21);
                }
            }
        });

        calAvgButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                c = db.rawQuery("select * from " + fdHelper.tableName,null);

                total = 0.00;
                for(int i = 0; i<foodArray.size(); i++){
                    c.moveToPosition(i);
                    total += Double.parseDouble(c.getString(c.getColumnIndex(FoodDatabaseHelper.Key_Calories)));
                }
                c.close();

                final AlertDialog.Builder builder = new AlertDialog.Builder(FoodList.this);
                LayoutInflater inflater =  LayoutInflater.from(getApplicationContext());
                View view =inflater.inflate(R.layout.custom_dialog_food, null);

                TextView item1Title = view.findViewById(R.id.item_title);
                item1Title.setText(getString(R.string.item1_title));

                TextView item1BodyMessage = view.findViewById(R.id.item_body_message);
                item1BodyMessage.setText(getString(R.string.item1_body_message) + " " + String.format("%.2f",total/foodArray.size()));
                item1BodyMessage.setGravity(Gravity.CENTER_VERTICAL | Gravity.CENTER_HORIZONTAL);

                builder.setView(view)
                        .setPositiveButton(R.string.dialog_ok_button, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int id) {}
                        });
                AlertDialog dialog = builder.create();
                dialog.show();
            }
        });

        calTotalButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //This idea is taken from: https://stackoverflow.com/questions/3747490/android-get-date-before-7-days-one-week
                //Android get date before 7 days (one week)
                SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
                String today = dateFormat.format(new Date()); //date of today

                Date myDate = null;
                try {
                    myDate = dateFormat.parse(today);
                } catch (ParseException e) {
                    e.printStackTrace();
                }
                Date newDate = new Date(myDate.getTime() - 1);//get date of yesterday
                String yesterday = dateFormat.format(newDate);

                c = db.rawQuery("SELECT * FROM " + FoodDatabaseHelper.tableName + " Where "+ FoodDatabaseHelper.Key_Time + " like '" + yesterday + "%'",null);
                c.moveToFirst();

                caloriesLastDay = 0;
                while(!c.isAfterLast()){
                    caloriesLastDay += Integer.parseInt(c.getString(c.getColumnIndex(FoodDatabaseHelper.Key_Calories)));
                    c.moveToNext();
                }
                c.close();

                final AlertDialog.Builder builder2 = new AlertDialog.Builder(FoodList.this);
                LayoutInflater inflater2 = LayoutInflater.from(getApplicationContext());
                View view2 =inflater2.inflate(R.layout.custom_dialog_food, null);

                TextView item2Title = view2.findViewById(R.id.item_title);
                item2Title.setText((getString(R.string.item2_title)));

                TextView item2BodyMessage = view2.findViewById(R.id.item_body_message);
                item2BodyMessage.setText(getString(R.string.item2_message_today)+today+getString(R.string.item2_yesterday)
                +yesterday+getString(R.string.item2_message_yesterday)+caloriesLastDay);
                item2BodyMessage.setGravity(Gravity.CENTER_VERTICAL | Gravity.CENTER_HORIZONTAL);

                builder2.setView(view2)
                        .setPositiveButton(R.string.dialog_ok_button, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int id) {}
                        });
                AlertDialog dialog2 = builder2.create();
                dialog2.show();
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu (Menu m){
        getMenuInflater().inflate(R.menu.food_toolbar_menu, m);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem mi) {
        switch (mi.getItemId()) {
            case R.id.action_one:
                Intent intent1 = new Intent(FoodList.this,DashBoardOfActivityTracking.class);
                startActivity(intent1);
                break;

            case R.id.action_two:
                Intent intent2 = new Intent(FoodList.this, ThermostatActivity.class);
                startActivity(intent2);
                break;

            case R.id.action_three:
                Intent intent3 = new Intent(FoodList.this, AutomobileActivity.class);
                startActivity(intent3);
                break;

            case R.id.action_four:
                final AlertDialog.Builder builder = new AlertDialog.Builder(this);
                LayoutInflater inflater = this.getLayoutInflater();
                View view =inflater.inflate(R.layout.custom_dialog_food, null);

                TextView helpMenuTitle = view.findViewById(R.id.item_title);
                helpMenuTitle.setText((getString(R.string.help_menu_title)));

                TextView helpMenuMessage = view.findViewById(R.id.item_body_message);
                helpMenuMessage.setText(getString(R.string.help_menu_message));
                helpMenuMessage.setTextSize(18);

                builder.setView(view)
                        .setPositiveButton(R.string.dialog_ok_button, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int id) {}
                        });
                AlertDialog dialog = builder.create();
                dialog.show();
                break;
        }
        return true;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == 1) {
            String[] extraInfo = data.getStringArrayExtra("saveInfo");
            FoodInformation recordInfo = new FoodInformation(extraInfo[0], extraInfo[1], extraInfo[2], extraInfo[3], extraInfo[4]);
            saveInfo(recordInfo);
        }
        else if (resultCode ==2) {
            Bundle args = data.getBundleExtra("forDelete");
            Long keyID = args.getLong("id");
            int viewPosition = args.getInt("viewPosition");
            deleteInfo(keyID, viewPosition);
        }
    }

    public void saveInfo(FoodInformation foodTemp){
        ContentValues values = new ContentValues();
        values.put(FoodDatabaseHelper.Key_FOOD, foodTemp.food);
        values.put(FoodDatabaseHelper.Key_Time, foodTemp.time);
        values.put(FoodDatabaseHelper.Key_Calories, foodTemp.calories);
        values.put(FoodDatabaseHelper.Key_Fat, foodTemp.totalFat);
        values.put(FoodDatabaseHelper.Key_Carbohydrate, foodTemp.totalCarbohydrate);

        if(clickAdd==true){ //if add button been clicked, means adding a new record into database
            db.insert(FoodDatabaseHelper.tableName, null, values);
            c = db.rawQuery("select * from " + fdHelper.tableName,null);
            foodArray.add(foodTemp);
            foodAdapter.notifyDataSetChanged();//update listview
            if(onTablet==true)
                Snackbar.make(findViewById(R.id.foodTabletXML), R.string.food_info_saved, Snackbar.LENGTH_LONG).show();
            else
                Snackbar.make(findViewById(R.id.foodListLayout), R.string.food_info_saved, Snackbar.LENGTH_LONG).show();
        }
        else{ //otherwise user is reading existing data and may modify current info
            db.update(FoodDatabaseHelper.tableName, values, FoodDatabaseHelper.Key_ID+" = " + dataID, null);
            c = db.rawQuery("select * from " + fdHelper.tableName,null);
            foodArray.set(viewPosition,foodTemp);
            foodAdapter.notifyDataSetChanged();
            if(onTablet==true)
                Snackbar.make(findViewById(R.id.foodTabletXML), R.string.food_info_updated, Snackbar.LENGTH_LONG).show();
            else
                Snackbar.make(findViewById(R.id.foodListLayout), R.string.food_info_updated, Snackbar.LENGTH_LONG).show();
        }
        sortInfo(); //after insert or update, sort the listview
        foodAdapter.notifyDataSetChanged();
    }

    public void deleteInfo(Long id, int viewPosition){
        db.delete(FoodDatabaseHelper.tableName, FoodDatabaseHelper.Key_ID + "=" + id, null);
        c = db.rawQuery("select * from " + fdHelper.tableName,null);
        foodArray.remove(viewPosition);
        foodAdapter.notifyDataSetChanged();
        if(onTablet==true)
            Snackbar.make(findViewById(R.id.foodTabletXML), R.string.food_info_deleted, Snackbar.LENGTH_LONG).show();
        else
            Snackbar.make(findViewById(R.id.foodListLayout), R.string.food_info_deleted, Snackbar.LENGTH_LONG).show();
    }

    public void sortInfo(){
        Collections.sort(foodArray, new CustomComparator());
    }

    public class CustomComparator implements Comparator<FoodInformation> {
        @Override
        public int compare(FoodInformation obj1, FoodInformation obj2) {
            SimpleDateFormat myFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            long sortDay = 0;
            try {
                Date day1 = myFormat.parse(obj1.time);
                Date day2 = myFormat.parse(obj2.time);
                sortDay = day2.getTime() - day1.getTime();
            } catch (ParseException e) {
                e.printStackTrace();
            }
            return (int)sortDay;
        }
    }

    private class FoodAdapter extends ArrayAdapter<FoodInformation> {
        public FoodAdapter(Context ctx) {
            super(ctx, 0);
        }

        public int getCount(){
            return foodArray.size();
        }

        @Override
        public FoodInformation getItem(int position){
            return foodArray.get(position);
        }

        public View getView(int position, View convertView, ViewGroup parent){
            LayoutInflater inflater = FoodList.this.getLayoutInflater();
            View result = null ;
            result = inflater.inflate(R.layout.food_eaten, null);
            TextView message = result.findViewById(R.id.food_info);

            FoodInformation fi = getItem(position);
            String textOnView = getString(R.string.view_time) + fi.time+"\n" +
                    getString(R.string.view_food) + fi.food + "\n" + getString(R.string.view_calories) + fi.calories
                    + ", " + getString(R.string.view_total_fat) + fi.totalFat+"g\n"
                    + getString(R.string.view_total_carbohydrate) + fi.totalCarbohydrate+"g";
            message.setText(textOnView);
            return result;
        }

        public long getItemId(int position){
            c = db.rawQuery("select * from " + fdHelper.tableName,null);
            c.moveToPosition(position);
            String x;
            x = c.getString(c.getColumnIndex(FoodDatabaseHelper.Key_ID));
            return Long.parseLong(x);
        }
    }

    class BackgroundRun extends AsyncTask<String, Integer, String> {

        @Override
        protected String doInBackground(String... args) {
            while (timerCounter<25)
            onProgressUpdate(25);

            fdHelper = new FoodDatabaseHelper(getApplicationContext());
            db = fdHelper.getWritableDatabase();

            while (timerCounter<50)
            onProgressUpdate(50);

            c = db.rawQuery("select * from " + fdHelper.tableName,null);
            c.moveToFirst();

            while (timerCounter<75)
            onProgressUpdate(75);

            while(!c.isAfterLast()){
                FoodInformation fi = new FoodInformation();
                fi.food = c.getString(c.getColumnIndex(FoodDatabaseHelper.Key_FOOD));
                fi.time = c.getString(c.getColumnIndex(FoodDatabaseHelper.Key_Time));
                fi.calories = c.getString(c.getColumnIndex(FoodDatabaseHelper.Key_Calories));
                fi.totalFat = c.getString(c.getColumnIndex(FoodDatabaseHelper.Key_Fat));
                fi.totalCarbohydrate = c.getString(c.getColumnIndex(FoodDatabaseHelper.Key_Carbohydrate));
                foodArray.add(fi);
                c.moveToNext();
            }

            while (timerCounter<100)
            onProgressUpdate(100);

            return null;
        }

        @Override
        protected void onProgressUpdate(Integer ...value){
            pBar.setProgress(value[0]);
            pBar.setVisibility(View.VISIBLE);
        }

        @Override
        protected void onPostExecute(String s){
            pBar.setVisibility(View.INVISIBLE);
            sortInfo();
            foodAdapter.notifyDataSetChanged();
        }
    }
}