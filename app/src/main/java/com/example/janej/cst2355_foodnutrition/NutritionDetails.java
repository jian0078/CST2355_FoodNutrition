package com.example.janej.cst2355_foodnutrition;

import android.annotation.SuppressLint;
import android.app.Fragment;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import java.text.SimpleDateFormat;
import java.util.Date;

public class NutritionDetails extends Fragment {

    private Boolean phone;
    private EditText enterFoodItem;
    private EditText enterTime;
    private EditText enterCalories;
    private EditText enterTotalFat;
    private EditText enterTotalCarbohydrate;
    private Button saveButton;
    private Button deleteButton;
    private Button cancelButton;
    private FloatingActionButton floatingActionButton;
    //0--foodItem, 1--time, 2--Cal, 3--fat, 4--Carbohydrate, 5--viewPosition
    public String[] foodKeeperArray = new String[6];
    private int todo;
    private Long dataID;
    private int viewPosition;

    public NutritionDetails(){phone = true;}

    @SuppressLint("ValidFragment")
    public NutritionDetails(FoodList x){
        phone = false;
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.activity_nutrition_details, container, false);

        enterFoodItem = view.findViewById(R.id.enterFoodItem);
        enterTime = view.findViewById(R.id.timeStamp);
        enterTime.setEnabled(false);
        enterCalories = view.findViewById(R.id.enterCalories);
        enterTotalFat = view.findViewById(R.id.enterTotalFat);
        enterTotalCarbohydrate =view.findViewById(R.id.enterTotalCarbohydrate);
        saveButton = view.findViewById(R.id.saveButton);
        deleteButton = view.findViewById(R.id.deleteButton);
        cancelButton = view.findViewById(R.id.cancelButton);
        floatingActionButton = view.findViewById(R.id.floatingActionButton);

        final Bundle args = this.getArguments();
        dataID = args.getLong("id");
        viewPosition = args.getInt("viewPosition");
        todo = args.getInt("TODO");
        if(todo == 1){ //add button has been clicked, all fields set to empty
            //This idea is taken from: https://stackoverflow.com/questions/8077530/android-get-current-timestamp
            //Android Get Current timestamp?
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            foodKeeperArray[1] = dateFormat.format(new Date());
            enterTime.setText(foodKeeperArray[1]);
            deleteButton.setEnabled(false);
        }
        if(todo == 2){ //listview has been clicked, load info into every fields
            foodKeeperArray = args.getStringArray("foodInfo");
            enterFoodItem.setText(foodKeeperArray[0]);
            enterTime.setText(foodKeeperArray[1]);
            enterCalories.setText(foodKeeperArray[2]);
            enterTotalFat.setText(foodKeeperArray[3]);
            enterTotalCarbohydrate.setText(foodKeeperArray[4]);
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
                        getActivity().getFragmentManager().beginTransaction().remove(NutritionDetails.this).commit();
                    }
                }
            }
        });

        deleteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(v.getContext());
                builder.setTitle(R.string.delete_button_dialog);
                builder.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        if (phone==true) {
                            if (checkFood()==true) {
                                Intent intent = new Intent();
                                intent.putExtra("forDelete", args);
                                getActivity().setResult(2, intent); //delete this info
                                getActivity().finish();
                            }
                        }
                        else {
                            ((FoodList) getActivity()).deleteInfo(dataID,viewPosition);
                            getActivity().getFragmentManager().beginTransaction().remove(NutritionDetails.this).commit();
                        }
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

        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(v.getContext());
                builder.setTitle(R.string.cancel_button_dialog);
                builder.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        if(phone == true) getActivity().finish();
                        else getActivity().getFragmentManager().beginTransaction().remove(NutritionDetails.this).commit();
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

        floatingActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                LayoutInflater inflater = getActivity().getLayoutInflater();
                @SuppressLint("InflateParams") View view1 =inflater.inflate(R.layout.custom_dialog_food, null);

                TextView title = view1.findViewById(R.id.item_title);
                title.setText(getString(R.string.floating_button_title));

                TextView bodyMessage = view1.findViewById(R.id.item_body_message);
                bodyMessage.setText(getString(R.string.floating_button_body_message));

                builder.setView(view1)
                        .setPositiveButton(R.string.dialog_ok_button, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int id) {}
                        });
                AlertDialog dialog = builder.create();
                dialog.show();
            }
        });
        return view;
    }

    public Boolean checkFood(){
        Boolean h = false;
        foodKeeperArray[0] = enterFoodItem.getText().toString();
        if(foodKeeperArray[0].isEmpty()){
            h = false;
            Toast toast = Toast.makeText(getContext(), R.string.check_food_entered, Toast.LENGTH_LONG);
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
                    Toast toast = Toast.makeText(getContext(), R.string.check_valid_food, Toast.LENGTH_LONG);
                    toast.show();
                    break;
                }
            }
        }
        return h;
    }

    public Boolean checkCalories(){
        foodKeeperArray[2] = enterCalories.getText().toString();
        if(foodKeeperArray[2].isEmpty()){
            Toast toast = Toast.makeText(getContext(), R.string.check_valid_calories, Toast.LENGTH_LONG);
            toast.show();
            return false;
        }
        else
            return true;
    }

    public Boolean checkFat(){
        foodKeeperArray[3] = enterTotalFat.getText().toString();
        if(foodKeeperArray[3].isEmpty()){
            Toast toast = Toast.makeText(getContext(), R.string.check_valid_fat, Toast.LENGTH_LONG);
            toast.show();
            return false;
        }
        else
            return true;
    }

    public Boolean checkCarbohydrate(){
        foodKeeperArray[4] = enterTotalCarbohydrate.getText().toString();
        if(foodKeeperArray[4].isEmpty()){
            Toast toast = Toast.makeText(getContext(), R.string.check_valid_carbohydrate, Toast.LENGTH_LONG);
            toast.show();
            return false;
        }
        else
            return true;
    }

    public FoodInformation houseOfInfo(String[] x){
        return new FoodInformation(x[0],x[1],x[2],x[3],x[4]);
    }
}