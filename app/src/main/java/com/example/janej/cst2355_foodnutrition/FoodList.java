package com.example.janej.cst2355_foodnutrition;



import android.app.Activity;
import android.app.FragmentTransaction;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import java.util.ArrayList;
import android.support.design.widget.Snackbar;

public class FoodList extends Activity {

    private static final String ACTIVITY_NAME = "FoodList";
    private ArrayList<FoodInformation> foodArray = new ArrayList<>();
    private ListView foodListView;
    private Button addFoodButton;
    private FoodDatabaseHelper fdHelper;
    private SQLiteDatabase writeableDB;
    private FoodAdapter foodAdapter;
    private Cursor c;
    private FrameLayout tabletLayOut;
    private Boolean onTablet;
    private ProgressBar pBar;
    private Long singleId;
    private int position;
    private Boolean clickAdd;
    private Handler handler;
    private int timerCounter;
    private Runnable runnable;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_food_list);

        pBar = findViewById(R.id.progressBar);
        foodListView = findViewById(R.id.foodListView);
        addFoodButton = findViewById(R.id.addFood);

        tabletLayOut=findViewById(R.id.tableFrameLayout);
        if(tabletLayOut == null){
            onTablet=false;
            Log.i(ACTIVITY_NAME,"On Phone Layout");
        }
        else{
            onTablet=true;
            Log.i(ACTIVITY_NAME,"On Tablet Layout");
        }

        foodAdapter =new FoodAdapter(this);
        foodListView.setAdapter(foodAdapter);

        timerCounter=0;
        handler = new Handler();
        runnable = new Runnable() {
            @Override
            public void run() {
                timerCounter++;
                if (timerCounter>100000) timerCounter=0;
                handler.postDelayed(this, 100);
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
                bd.putInt("TODO",1);

                if(onTablet==true){
                    NutritionDetails nd = new NutritionDetails(FoodList.this);
                    nd.setArguments(bd);
                    FragmentTransaction ft =  getFragmentManager().beginTransaction();
                    ft.replace(R.id.tableFrameLayout,nd);
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
                position = i;
                FoodInformation fi = foodArray.get(i);
                String [] tempArray = new String[8];
                tempArray[0] = fi.food;
                tempArray[1] = fi.day;
                tempArray[2] = fi.hour;
                tempArray[3] = fi.minute;
                tempArray[4] = fi.calories;
                tempArray[5] = fi.totalFat;
                tempArray[6] = fi.totalCarbohydrate;
                tempArray[7] = String.valueOf(i);
                singleId = foodAdapter.getItemId(i);

                Bundle foodBundle = new  Bundle();
                foodBundle.putInt("TODO", 2);
                foodBundle.putStringArray("foodInfo", tempArray);
                foodBundle.putLong("id",singleId);

                if(onTablet==true){
                    NutritionDetails nd = new NutritionDetails(FoodList.this);
                    nd.setArguments(foodBundle);
                    FragmentTransaction ft =  getFragmentManager().beginTransaction();
                    ft.replace(R.id.tableFrameLayout,nd);
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
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == 1) {
            Bundle extras = data.getExtras();
            String[] tempArray = (String[]) extras.get("saveInfo");
            FoodInformation tempEntry = new FoodInformation(tempArray[0], tempArray[1], tempArray[2], tempArray[3], tempArray[4],
                    tempArray[5],tempArray[6]);
            saveInfo(tempEntry);
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
        values.put(FoodDatabaseHelper.Key_Day, foodTemp.day);
        values.put(FoodDatabaseHelper.Key_Hour, foodTemp.hour);
        values.put(FoodDatabaseHelper.Key_Minute, foodTemp.minute);
        values.put(FoodDatabaseHelper.Key_Calories, foodTemp.calories);
        values.put(FoodDatabaseHelper.Key_Fat, foodTemp.totalFat);
        values.put(FoodDatabaseHelper.Key_Carbohydrate, foodTemp.totalCarbohydrate);

        if(clickAdd==true){
            writeableDB.insert(FoodDatabaseHelper.tableName, null, values);
            c = writeableDB.rawQuery("select * from " + fdHelper.tableName,null);
            c.moveToFirst();
            foodArray.add(foodTemp);
            foodAdapter.notifyDataSetChanged();

            Snackbar.make(findViewById(R.id.foodListLayout), "A new food info is added", Snackbar.LENGTH_LONG).show();
        }
        else{
            writeableDB.update(FoodDatabaseHelper.tableName, values, FoodDatabaseHelper.Key_ID+" = ?", null);
            c = writeableDB.rawQuery("select * from " + fdHelper.tableName,null);
            c.moveToFirst();
            foodArray.set(position,foodTemp);
            foodAdapter.notifyDataSetChanged();

            Snackbar.make(findViewById(R.id.foodListLayout), "Food info has been modified", Snackbar.LENGTH_LONG).show();
        }
    }

    public void deleteInfo(Long id, int viewPostion){
        writeableDB.delete(FoodDatabaseHelper.tableName, FoodDatabaseHelper.Key_ID + "=" + id, null);
        c = writeableDB.rawQuery("select * from " + fdHelper.tableName,null);
        foodArray.remove(viewPostion);
        foodAdapter.notifyDataSetChanged();

        Snackbar.make(findViewById(R.id.foodListLayout), "Food info has been removed", Snackbar.LENGTH_LONG).show();
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
            String textOnView = fi.day+", ";
            if (Integer.parseInt(fi.hour)>0 && Integer.parseInt(fi.hour)<10){
                textOnView += "0";
            }
            textOnView += fi.hour +":";
            if (Integer.parseInt(fi.minute)>0 && Integer.parseInt(fi.minute)<10) {
                textOnView+="0";
            }
            textOnView += fi.minute;
            if(Integer.parseInt(fi.hour)>=12){
                textOnView+=" PM";
            }
            else{
                textOnView+=" AM";
            }
            textOnView = textOnView + "\nFood:" + fi.food + "\nCalories:"+fi.calories+ ", Total Fat:"
                    +fi.totalFat+"g, Total Carbohydrate:"+fi.totalCarbohydrate+"g";
            message.setText(textOnView);
            return result;
        }

        public long getItemId(int position){
            c.moveToPosition(position);
            String x;
            x = c.getString(c.getColumnIndex(FoodDatabaseHelper.Key_ID));
            return Long.parseLong(x);
        }
    }

    class BackgroundRun extends AsyncTask<String, Integer, String> {

        @Override
        protected String doInBackground(String... args) {
            fdHelper = new FoodDatabaseHelper(getApplicationContext());
            writeableDB = fdHelper.getWritableDatabase();

            while (timerCounter<10);
            onProgressUpdate(25);

            c = writeableDB.rawQuery("select * from " + fdHelper.tableName,null);
            c.moveToFirst();

            while (timerCounter<20);
            onProgressUpdate(50);

            while(!c.isAfterLast()){
                FoodInformation fi = new FoodInformation();
                fi.food = c.getString(c.getColumnIndex(FoodDatabaseHelper.Key_FOOD));
                fi.day = c.getString(c.getColumnIndex(FoodDatabaseHelper.Key_Day));
                fi.hour = c.getString(c.getColumnIndex(FoodDatabaseHelper.Key_Hour));
                fi.minute = c.getString(c.getColumnIndex(FoodDatabaseHelper.Key_Minute));
                fi.calories = c.getString(c.getColumnIndex(FoodDatabaseHelper.Key_Calories));
                fi.totalFat = c.getString(c.getColumnIndex(FoodDatabaseHelper.Key_Fat));
                fi.totalCarbohydrate = c.getString(c.getColumnIndex(FoodDatabaseHelper.Key_Carbohydrate));

                while (timerCounter<30);
                onProgressUpdate(75);

                foodArray.add(fi);
                c.moveToNext();
            }

            while (timerCounter<4);
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
            foodAdapter.notifyDataSetChanged();
        }
    }
}
