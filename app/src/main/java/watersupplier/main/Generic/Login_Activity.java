package watersupplier.main.Generic;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Iterator;

import watersupplier.main.Main.Common_ActionBar_Abstract;
import watersupplier.main.Firsebase_Operations.FirebaseInstance;
import watersupplier.main.Main.First_Menu;
import watersupplier.main.R;
import watersupplier.main.Utils.AppUtills;

/**
 * Created by Aman Gupta on 25/1/17.
 */

public class Login_Activity extends Common_ActionBar_Abstract implements View.OnClickListener{

    //UI variables
    private EditText user_id,password;
    private Button signin_btn;
    private TextView signup_text;

    //firebase
    private FirebaseDatabase Database;
    private DatabaseReference databaseReference;
    public FirebaseApp myApp=null;
    private  FirebaseOptions firebaseOptions=null;
    private boolean isSignedUpAlready = false;
    private String TAG = "Login_Activity";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);//to disable keyboard

        //for login screen
        String LoginsStatus=getSharedPreferences("Login",Context.MODE_PRIVATE).getString("isLogin","");
        String isSignUp=getSharedPreferences("Login",Context.MODE_PRIVATE).getString("isSignup","");
        Log.d(TAG,"Aman Check Login :: "+LoginsStatus+"  "+isSignUp);

        //for Passcode screen
        String PasscodeStatus = getSharedPreferences("Passcode",Context.MODE_PRIVATE).getString("isPasscodeSaved","");
        String passCode = getSharedPreferences("Passcode",Context.MODE_PRIVATE).getString("passcode","");
        Log.d(TAG,"Aman Check pass :: "+PasscodeStatus+"  "+passCode);
//        Log.d(TAG,"Aman Check pass2 :: "+LoginsStatus+"  "+isSignUp);

//        if (LoginsStatus.equals("Yes")) {

            if (isSignUp.equals(getString(R.string.yes_Spelling)) && !PasscodeStatus.equals(getString(R.string.yes_Spelling))){
                Intent intent=new Intent(Login_Activity.this,Passcode_SignUp_Activity.class);
                startActivity(intent);
                finish();
            }
             if(PasscodeStatus.equals(getString(R.string.yes_Spelling))){
                Intent intent=new Intent(Login_Activity.this,Passcode_Activity.class);
                startActivity(intent);
                finish();
            }

        initialization();
        generateEvents();
    }

    private void initialization() {
        firebaseOptions = new FirebaseOptions.Builder()
                .setDatabaseUrl("https://water-supplier-603d3.firebaseio.com/")
                .setApiKey("AIzaSyAjl3q-ikC3SLyYRykjD8jKto-mhWEsV7g")
                .setApplicationId("water-supplier-603d3").build();

        //  myApp= FirebaseApp.initializeApp(getApplicationContext(), firebaseOptions);
        Database = FirebaseInstance.getInstance();

        databaseReference=Database.getReference(getResources().getString(R.string.app_name));
        /***
         * below statement to keeping data sync for OFFLINE FIREBASE implimentation
         * Data Limit = 10 mb ( used least recently)
         * Location = FireBase Cache
         *
         * */
        databaseReference.keepSynced(true);
        signup_text = (TextView) findViewById(R.id.signup_text);
        user_id = (EditText) findViewById(R.id.user_id);
        password = (EditText) findViewById(R.id.password);
        signin_btn = (Button) findViewById(R.id.signin_btn);
        getSupportActionBar().setDisplayHomeAsUpEnabled(false);
        password.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if(!user_id.getText().toString().isEmpty()||!password.getText().toString().isEmpty()){
                    signin_btn.setBackgroundColor(getResources().getColor(R.color.login_btn_color));
                }
                else if(user_id.getText().toString().isEmpty() && password.getText().toString().isEmpty()) {
                    signin_btn.setBackgroundColor(getResources().getColor(R.color.grey_font));
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
                if(user_id.getText().toString().isEmpty()||password.getText().toString().isEmpty()){
                   signin_btn.setBackgroundColor(getResources().getColor(R.color.grey_font));
                }
            }
        });
        user_id.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if(!user_id.getText().toString().isEmpty()||!password.getText().toString().isEmpty()){
                    signin_btn.setBackgroundColor(getResources().getColor(R.color.login_btn_color));
                }
                else if(user_id.getText().toString().isEmpty() && password.getText().toString().isEmpty()) {
                    signin_btn.setBackgroundColor(getResources().getColor(R.color.grey_font));
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
                if(user_id.getText().toString().isEmpty()||password.getText().toString().isEmpty()){
                    signin_btn.setBackgroundColor(getResources().getColor(R.color.grey_font));
                }
            }
        });




    }
    private void generateEvents() {
        signup_text.setOnClickListener(Login_Activity.this);
        signin_btn.setOnClickListener(Login_Activity.this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.signin_btn:
                if (user_id.getText().toString().isEmpty()){
                    user_id.setError("Please enter Username");
                }
               else if (password.getText().toString().isEmpty()){
                password.setError("Please enter Password");
            }

                else{
                    if (AppUtills.isNetworkAvailable(Login_Activity.this)) {
                        LoginListners();
                    }else{
                        Snackbar.make(findViewById(android.R.id.content), getResources().getString(R.string.CheckYourInternetConnection), Snackbar.LENGTH_LONG)
                                .setActionTextColor(getResources().getColor(R.color.red_ToastColor))
                                .show();
                    }

                }
                break;
            case R.id.signup_text:
                        Intent intent = new Intent(Login_Activity.this, SignUp_Activity.class);
                        startActivity(intent);
                finish();
                break;

        }

    }

    private void LoginListners() {
       /* Log.d(TAG,"Aman CHk DB"+databaseReference.child(user_id.getText().toString()).child(getResources().getString(R.string.LoginSpelling)).
                child(user_id.getText().toString()));*/

        databaseReference.child(user_id.getText().toString()).child(getResources().getString(R.string.LoginSpelling)).
                child(user_id.getText().toString()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                String UsrName="";
                String Password="";
                if (dataSnapshot.hasChildren()) {
                    Iterator<DataSnapshot> iterator = dataSnapshot.getChildren().iterator();

                    while (iterator.hasNext()) {
                        DataSnapshot profiles = (DataSnapshot) iterator.next();
                        try {
                            if (profiles.getKey().equals("User_id")){
                                UsrName= (String) profiles.getValue();
                                Log.d(TAG,"Aman Chk TOTAL =="+UsrName);
                            }
                            if(profiles.getKey().equals("Password")){
                                Password= (String) profiles.getValue();
                            }
                            if (profiles.getKey().equals("Shop_Name")){

                                isSignedUpAlready=true;

                            }
                        } catch (Exception e){
                            e.printStackTrace();
                        }
                    }// while (it.hasNext()) closes her...


                    if (UsrName.trim().equals(user_id.getText().toString().trim()) && Password.equals(password.getText().toString())){

                        Toast.makeText(Login_Activity.this, getString(R.string.loginSucess), Toast.LENGTH_SHORT).show();
                        saveInSharedPrefernce();


                        if (isSignedUpAlready){

                            Log.d(TAG,"Aman CHK SP=="+isSignedUpAlready);
                            Intent intentToHomePage=new Intent(Login_Activity.this,Passcode_SignUp_Activity.class);
                            startActivity(intentToHomePage);

                            SharedPreferences sharedPreferences=getSharedPreferences("Login", Context.MODE_PRIVATE);
                            SharedPreferences.Editor editor=sharedPreferences.edit();
                            editor.putString(getString(R.string.UserId_Spelling), user_id.getText().toString());
                            editor.commit();
                            finish();

                        }/*else{

                            Intent intentToSignUp = new Intent(Login_Activity.this, SignUp_Activity.class);
//                            startActivity(intentToSignUp);
                            finish();

                        }*/
                    }else{
                        Snackbar.make(findViewById(android.R.id.content), getResources().getString(R.string.invalid_user), Snackbar.LENGTH_LONG)
                                .setActionTextColor(getResources().getColor(R.color.red_ToastColor))
                                .show();
                    }
                }//if closes here..
                else{
                    Snackbar.make(findViewById(android.R.id.content), getResources().getString(R.string.invalid_user), Snackbar.LENGTH_LONG)
                            .setActionTextColor(getResources().getColor(R.color.red_ToastColor))
                            .show();
                }

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }

        }  );

    }

    private void saveInSharedPrefernce() {
        SharedPreferences sharedPreferences=getSharedPreferences("Login", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor=sharedPreferences.edit();
        editor.putString(getResources().getString(R.string.isLogedIn_Spelling),getResources().getString(R.string.yes_Spelling));
        editor.putString(getResources().getString(R.string.isSignup_Spelling),getResources().getString(R.string.yes_Spelling));

        editor.putString(getString(R.string.UserId_Spelling), user_id.getText().toString().trim());
        editor.commit();
//        editor.commit();
//        editor.commit();

//        Log.d(TAG,"Aman CHK SP=="+sharedPreferences.getString("Login",Context.MODE_PRIVATE));
    }
}
