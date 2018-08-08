package watersupplier.main.Generic;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.facebook.CallbackManager;
import com.facebook.FacebookSdk;
import com.google.firebase.FirebaseOptions;
import com.google.firebase.database.DatabaseException;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import org.json.JSONException;
import org.json.JSONObject;

import watersupplier.main.Main.Common_ActionBar_Abstract;
import watersupplier.main.Firsebase_Operations.FirebaseInstance;
import watersupplier.main.Main.First_Menu;
import watersupplier.main.Pojo.SignUp_POJO;
import watersupplier.main.R;
import watersupplier.main.Utils.AppUtills;

/**
 * Created by Aman Gupta on 25/1/17.
 */

/**
 * Login Activity for Facebook login
 * add facebook Dependencies in gradle file compile 'com.facebook.android:facebook-android-sdk:4.16.1'
 * Initialize FacebookSDK
 * Create an app in Facebook Developer site generate app id
 * Register FacebookActivity in menifest and add facebook app id in  <meta-data
 * android:name="com.facebook.sdk.ApplicationId"
 * android:value="@string/facebook_appid" /> tag.
 * Use onFbLogin(callbackManager, fbLoginHandler) method from FacebookLogin class
 * Get LoginResult by register callbackmanager, getting Result From FacebookActivity in onActivityResult
 * Get userLoginDetails in Handler fbLoginHandler
 */

public class SignUp_Activity extends Common_ActionBar_Abstract implements View.OnClickListener {

    //UI variables
    private EditText user_id,password,confirm_password,shop_name,phone_no,email;
    private Button btn_Save,google_signIn_btn,facebook_signin_btn;

    //firebase
    private FirebaseDatabase Database;
    private DatabaseReference databaseReference;
    private  FirebaseOptions firebaseOptions=null;
    //Non-UI variables

    CallbackManager callbackManager;
    FacebookLogin facebookLogin;
    Context mContext;
    String name = null, email_id = null, id = null;
    SharedPreferences sharedPref;
    SharedPreferences.Editor edit;
    private  static String TAG = "SignUp_Activity";
    /**
     * Handle user FacebookLogin details in message object
     */
    Handler fbLoginHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            JSONObject json_object = (JSONObject) msg.obj;
            Log.d(TAG,"Aman json_object=="+json_object);
            try {
                name = json_object.getString("name");
                email_id = json_object.getString("email");
                id = json_object.getString("id");
                Log.d(TAG,"Aman name=="+name);
                Log.d(TAG,"Aman email_id=="+email_id);
                Log.d(TAG,"Aman id=="+id);
                //put logindetails in shared prefrence

                sharedPref = getSharedPreferences("Login", Context.MODE_PRIVATE);
                edit = sharedPref.edit();
                edit.putString("FBLoginName", name);
                edit.putString("FBLoginEmail", email_id);
                edit.putString("FBLoginID", id);
//                edit.putString("isFBLoginSaved",getString(R.string.yes_Spelling));
//                edit.putString(getResources().getString(R.string.isSignup_Spelling),getString(R.string.yes_Spelling));
                edit.commit();
                saveinDB();

//                AppUtills.setStringPrefrences(mContext, "FBLoginDtls", name, "name");
//                AppUtills.setStringPrefrences(mContext, "FBLoginDtls", email_id, "email");
//                AppUtills.setStringPrefrences(mContext, "FBLoginDtls", id, "id");

            } catch (JSONException e) {
                e.printStackTrace();
            }
            Intent intent = new Intent(SignUp_Activity.this, Passcode_SignUp_Activity.class);
            startActivity(intent);
            finish();
        }
    };

    private void saveinDB() {
        SignUp_POJO signUp_pojo = new SignUp_POJO(name,
                id,
                null,
                null,
                email_id);
        try {
//                mDatabase.child(editTextUsrName.getText().toString()).child(getResources().getString(R.string.LoginSpelling)).
// child(editTextUsrName.getText().toString()).setValue(profile_pojo);

            databaseReference.child(name).child(getResources().getString(R.string.LoginSpelling))
                    .child(name).setValue(signUp_pojo);

            saveInSharedPref();
        }
        catch (DatabaseException e){
            Toast.makeText(SignUp_Activity.this,"Username should not contain  special chars",Toast.LENGTH_SHORT).show();
            e.printStackTrace();
        }
    }


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);
        getSupportActionBar().hide();
        facebookSDKInitialize();
        initialization();
        generateEvents();
    }

    /**
     * Facebook SDKIntitialize
     */
    protected void facebookSDKInitialize() {
        FacebookSdk.sdkInitialize(getApplicationContext());
        callbackManager = CallbackManager.Factory.create();
    }

    /**
     * @param requestCode The request code that's received by the Activity
     * @param resultCode  The result code that's received by the Activity
     * @param data        The result data that's received by the Activity
     * @return true If the result could be handled.
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        callbackManager.onActivityResult(requestCode, resultCode, data);
        Toast.makeText(this,"",Toast.LENGTH_SHORT).show();
    }


    private void initialization() {
        facebookLogin = new FacebookLogin(SignUp_Activity.this);
        sharedPref = getSharedPreferences("Login", Context.MODE_PRIVATE);
        //get SharedPrefrece value
//        String FBid = AppUtills.getStringPrefrences(SignUp_Activity.this, "FBLoginDtls", "id");
        String FBid = sharedPref.getString("FBLoginID",null);
        if (FBid != null && !FBid.equalsIgnoreCase(" ")) {
            Intent intent = new Intent(SignUp_Activity.this, First_Menu.class);
            startActivity(intent);
            finish();
        }

        firebaseOptions = new FirebaseOptions.Builder()
                .setDatabaseUrl("https://water-supplier-603d3.firebaseio.com/")
                .setApiKey("AIzaSyAjl3q-ikC3SLyYRykjD8jKto-mhWEsV7g")
                .setApplicationId("water-supplier-603d3").build();

        //  myApp= FirebaseApp.initializeApp(getApplicationContext(), firebaseOptions);
        Database = FirebaseInstance.getInstance();
        user_id = (EditText) findViewById(R.id.user_id);
        password = (EditText) findViewById(R.id.password);
        confirm_password = (EditText) findViewById(R.id.confirm_password);
        shop_name = (EditText) findViewById(R.id.shop_name);
        phone_no = (EditText) findViewById(R.id.phone_no);
        email = (EditText) findViewById(R.id.email);
        btn_Save = (Button) findViewById(R.id.btn_Save);
        google_signIn_btn = (Button) findViewById(R.id.google_signIn_btn);
        facebook_signin_btn = (Button) findViewById(R.id.facebook_signin_btn);

        databaseReference=Database.getReference(getResources().getString(R.string.app_name));
        getSupportActionBar().setDisplayHomeAsUpEnabled(false);
    }


    private void generateEvents() {
        btn_Save.setOnClickListener(this);
        google_signIn_btn.setOnClickListener(this);
        facebook_signin_btn.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.btn_Save:
                if(user_id.getText().toString().isEmpty()){
                    user_id.requestFocus();
                    user_id.setError(getResources().getString(R.string.cannot_be_empty));
                }
                else if (password.getText().toString().isEmpty()){
                    password.requestFocus();
                    password.setError(getResources().getString(R.string.cannot_be_empty));
                }
                else if (confirm_password.getText().toString().isEmpty()){
                    confirm_password.requestFocus();
                    confirm_password.setError(getResources().getString(R.string.cannot_be_empty));
                }
                else if (shop_name.getText().toString().isEmpty()){
                    shop_name.requestFocus();
                    shop_name.setError(getResources().getString(R.string.cannot_be_empty));
                }
                else if (phone_no.getText().toString().isEmpty()){
                    phone_no.requestFocus();
                    phone_no.setError(getResources().getString(R.string.cannot_be_empty));
                }
                else if (phone_no.getText().length()<10) {
                    phone_no.requestFocus();
                    phone_no.setError(getResources().getString(R.string.mobile_number_length));
                }
                else if (email.getText().toString().isEmpty()){
                    email.requestFocus();
                    email.setError(getResources().getString(R.string.cannot_be_empty));
                }
                else {
                    if (AppUtills.isNetworkAvailable(SignUp_Activity.this)){
                        save();
                    }
                    else
                        Snackbar.make(findViewById(android.R.id.content), getResources().getString(R.string.CheckYourInternetConnection), Snackbar.LENGTH_LONG)
                                .setActionTextColor(getResources().getColor(R.color.red_ToastColor))
                                .show();
                }
                break;
            case R.id.google_signIn_btn:
                Snackbar.make(findViewById(android.R.id.content), "Under Construction", Snackbar.LENGTH_LONG)
                        .setActionTextColor(getResources().getColor(R.color.red_ToastColor))
                        .show();
                break;

            case R.id.facebook_signin_btn:
                // code for login through facebook
                facebookLogin.onFbLogin(callbackManager, fbLoginHandler);
                break;

        }
    }

    private void save() {
        if (password.getText().toString().trim().equals(confirm_password.getText().toString().trim())){
            SignUp_POJO signUp_pojo = new SignUp_POJO(user_id.getText().toString(),
                    password.getText().toString(),
                    shop_name.getText().toString(),
                    phone_no.getText().toString(),
                    email.getText().toString());
            try {
//                mDatabase.child(editTextUsrName.getText().toString()).child(getResources().getString(R.string.LoginSpelling)).
// child(editTextUsrName.getText().toString()).setValue(profile_pojo);

                databaseReference.child(user_id.getText().toString()).child(getResources().getString(R.string.LoginSpelling))
                        .child(user_id.getText().toString()).setValue(signUp_pojo);
//                databaseReference.child(shop_name.getText().toString()).setValue(signUp_pojo);
                saveInSharedPref();
            }
            catch (DatabaseException e){
                Toast.makeText(SignUp_Activity.this,"Username should not contain  special chars",Toast.LENGTH_SHORT).show();
                e.printStackTrace();
            }
        }
        else{
            confirm_password.requestFocus();
            confirm_password.setError("Password Mismatch");
        }
    }

    private void saveInSharedPref() {

        SharedPreferences sharedPreferences=getSharedPreferences("Login", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor=sharedPreferences.edit();
        editor.putString(getResources().getString(R.string.isSignup_Spelling),getString(R.string.yes_Spelling));
        editor.putString(getString(R.string.UserId_Spelling), user_id.getText().toString());
        editor.commit();
        Intent intentToHomePage=new Intent(SignUp_Activity.this, Passcode_SignUp_Activity.class);
        startActivity(intentToHomePage);
        Toast.makeText(SignUp_Activity.this,"Profile Saved!",Toast.LENGTH_SHORT).show();
        finish();

    }

    @Override
    public void onBackPressed() {
        Intent intent=new Intent(SignUp_Activity.this, Login_Activity.class);
        startActivity(intent);
        AppUtills.givefinishEffect(this);
        finish();
//        super.onBackPressed();
    }
}
