package com.example.janej.cst2355_foodnutrition;



import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.Fragment;
import android.app.TimePickerDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.icu.util.Calendar;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TimePicker;
import android.widget.Toast;

public class NutritionDetails extends Fragment implements AdapterView.OnItemSelectedListener{

    private Boolean phone;
    private EditText enterFoodItem;
    private Spinner selectDay;
    private EditText enterTime;
    private EditText enterCalories;
    private EditText enterTotalFat;
    private EditText enterTotalCarbohydrate;
    private Button saveButton;
    private Button deleteButton;
    private Button cancelButton;
    private String[] weekDays={"Monday","Tuesday","Wednesday","Thursday","Friday","Saturday","Sunday"};
    //0--foodItem, 1--day, 2--hour, 3--minute, 4--Cal, 5--fat, 6--Carbohydrate, 7--viewPosition
    public String[] foodKeeperArray = new String[8];
    private int todo;
    private Long id;
    private int viewPosition;

    public NutritionDetails(){phone = true;}

    @SuppressLint("ValidFragment")
    public NutritionDetails(FoodList x){
        phone = false;
    }
    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.activity_nutrition_details, container, false);

        final Bundle args = this.getArguments();
        id = args.getLong("id");
        viewPosition = args.getInt("viewPosition");

        enterFoodItem = view.findViewById(R.id.enterFoodItem);

        selectDay = view.findViewById(R.id.selectDay);
        ArrayAdapter<String> i = new ArrayAdapter (getContext(), android.R.layout.simple_spinner_item,weekDays);
        i.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        selectDay.setAdapter(i);
        selectDay.setOnItemSelectedListener(this);

        enterTime = view.findViewById(R.id.enterTime);
        enterTime.setFocusable(false);
        enterTime.setOnClickListener(new View.OnClickListener() {
            @TargetApi(Build.VERSION_CODES.N)
            @RequiresApi(api = Build.VERSION_CODES.N)
            @Override
            public void onClick(View v) {
                Calendar currentTime = Calendar.getInstance();
                int hour = currentTime.get(Calendar.HOUR_OF_DAY);
                int minute = currentTime.get(Calendar.MINUTE);

                TimePickerDialog mTimePicker = new TimePickerDialog(getContext(), new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker timePicker, int selectedHour, int selectedMinute) {
                        foodKeeperArray[2] = String.valueOf(selectedHour);
                        foodKeeperArray[3] = String.valueOf(selectedMinute);
                        String tempTime = "";
                        if (Integer.parseInt(foodKeeperArray[2])>0 && Integer.parseInt(foodKeeperArray[2])<10)
                            tempTime = "0";

                        tempTime = tempTime + foodKeeperArray[2] +":";

                        if (Integer.parseInt(foodKeeperArray[3])>0 && Integer.parseInt(foodKeeperArray[3])<10)
                            tempTime+="0";

                        tempTime = tempTime + foodKeeperArray[3];
                        enterTime.setText(tempTime);
                    }
                }, hour, minute, true);//True -  24 hour time
                mTimePicker.setTitle("Select Time");
                mTimePicker.show();
            }
        });

        enterCalories = view.findViewById(R.id.enterCalories);
        enterTotalFat = view.findViewById(R.id.enterTotalFat);
        enterTotalCarbohydrate =view.findViewById(R.id.enterTotalCarbohydrate);
        saveButton = view.findViewById(R.id.saveButton);
        deleteButton = view.findViewById(R.id.deleteButton);
        cancelButton = view.findViewById(R.id.cancelButton);

        todo = args.getInt("TODO");
        if(todo == 1){
            foodKeeperArray[2]="00";
            foodKeeperArray[3]="00";
            enterTime.setText(foodKeeperArray[2]+":"+foodKeeperArray[3]);
            deleteButton.setEnabled(false);
        }
        if(todo == 2){
            foodKeeperArray = args.getStringArray("foodInfo");
            enterFoodItem.setText(foodKeeperArray[0]);
            selectDay.setSelection(whichDay(foodKeeperArray[1]));
            enterTime.setText(foodKeeperArray[2]+":"+foodKeeperArray[3]);
            enterCalories.setText(foodKeeperArray[4]);
            enterTotalFat.setText(foodKeeperArray[5]);
            enterTotalCarbohydrate.setText(foodKeeperArray[6]);
        }

        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v){
                if (phone==true) {
                    if (checkFood()==true && checkCalories()==true && checkFat()==true && checkCarbohydrate()==true) {
                        Intent intent = new Intent();
                        intent.putExtra("saveInfo", foodKeeperArray);
                        getActivity().setResult(1, intent); //save existing info
                        getActivity().finish();
                    }
                }
                else {
                    if (checkFood()==true && checkCalories()==true && checkFat()==true && checkCarbohydrate()==true){
                        ((FoodList) getActivity()).saveInfo(houseOfInfo(foodKeeperArray));
                        getFragmentManager().popBackStackImmediate();
                    }
                }
            }
        });

        deleteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (phone==true) {
                    if (checkFood()==true) {
                        Intent intent = new Intent();
                        intent.putExtra("forDelete", args);
                        getActivity().setResult(2, intent);
                        getActivity().finish();
                    }
                }
                else {
                    ((FoodList) getActivity()).deleteInfo(id,viewPosition);
                    getActivity().getFragmentManager().beginTransaction().remove(NutritionDetails.this).commit();
                }
            }
        });

        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(v.getContext());
                builder.setTitle(R.string.dialog);
                builder.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        getActivity().finish();
                    }
                });
                builder.setNegativeButton(R.string.no, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                    }
                });
                AlertDialog dialog = builder.create();
                dialog.show();
            }
        });
        return view;
    }
    @Override
    public void onItemSelected(AdapterView<?> arg0, View arg1, int position, long id) {
        foodKeeperArray[1] = weekDays[position];
    }

    @Override
    public void onNothingSelected(AdapterView<?> arg0) {}

    public Boolean checkFood(){
        Boolean h = false;
        foodKeeperArray[0] = enterFoodItem.getText().toString();
        if(foodKeeperArray[0].isEmpty()){
            h = false;
            Toast toast = Toast.makeText(getContext(), "Please Enter a Food", Toast.LENGTH_LONG);
            toast.show();
        }
        else{
            for(int i = 0; i<foodKeeperArray[0].length();i++){
                if(Character.isLetter(foodKeeperArray[0].charAt(i)) || foodKeeperArray[0].charAt(i) == ' '){
                    h=true;
                    continue;
                }
                else{
                    h=false;
                    Toast toast = Toast.makeText(getContext(), "Please enter a VALID Food Item", Toast.LENGTH_LONG);
                    toast.show();
                    break;
                }
            }
        }
        return h;
    }

    public Boolean checkCalories(){
        Boolean h;
        foodKeeperArray[4] = enterCalories.getText().toString();
        if(foodKeeperArray[4].isEmpty()){
            h = false;
            Toast toast = Toast.makeText(getContext(), "Please Enter Calories Info", Toast.LENGTH_LONG);
            toast.show();
        }
        else
            h = true;

        return h;
    }

    public Boolean checkFat(){
        Boolean h;
        foodKeeperArray[5] = enterTotalFat.getText().toString();
        if(foodKeeperArray[5].isEmpty()){
            h = false;
            Toast toast = Toast.makeText(getContext(), "Please Enter Fat Info", Toast.LENGTH_LONG);
            toast.show();
        }
        else
            h = true;

        return h;
    }

    public Boolean checkCarbohydrate(){
        Boolean h;
        foodKeeperArray[6] = enterTotalCarbohydrate.getText().toString();
        if(foodKeeperArray[6].isEmpty()){
            h = false;
            Toast toast = Toast.makeText(getContext(), "Please Enter Carbohydrate Info", Toast.LENGTH_LONG);
            toast.show();
        }
        else
            h = true;

        return h;
    }

    public int whichDay(String a){
        int i=0;
        if(a.equals("Monday")){
            i=0;
        }
        else if(a.equals("Tuesday")){
            i=1;
        }
        else if(a.equals("Wednesday")){
            i=2;
        }
        else if(a.equals("Thursday")){
            i=3;
        }
        else if(a.equals("Friday")){
            i=4;
        }
        else if(a.equals("Saturday")){
            i=5;
        }
        else if(a.equals("Sunday")){
            i=6;
        }
        return i;
    }

    public FoodInformation houseOfInfo(String[] x){
        return new FoodInformation(x[0],x[1],x[2],x[3],x[4],x[5],x[6]);
    }
}