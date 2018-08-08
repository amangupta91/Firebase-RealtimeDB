package watersupplier.main.Generic;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import watersupplier.main.Main.Common_ActionBar_Abstract;
import watersupplier.main.Main.First_Menu;
import watersupplier.main.R;

/**
 * Created by aman on 25/10/17.
 */

public class Passcode_SignUp_Activity extends Common_ActionBar_Abstract implements View.OnClickListener {
    //UI variables
    private EditText enter_passcode,confirm_passcode;
    private Button btn_Save;

    //non UI variables

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_passcode_signup);
        initialization();
        generateEvents();
    }

    private void initialization() {
         enter_passcode = (EditText) findViewById(R.id.enter_passcode);
         confirm_passcode = (EditText) findViewById(R.id.confirm_passcode);
        btn_Save = (Button) findViewById(R.id.btn_Save);
        getSupportActionBar().setDisplayHomeAsUpEnabled(false);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
    }

    private void generateEvents() {
        btn_Save.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        if(enter_passcode.getText().toString().trim().length()!=4){
            enter_passcode.setError("Password should be 4 digit");
            enter_passcode.requestFocus();
        }
        else if(confirm_passcode.getText().toString().trim().length()!=4){
            confirm_passcode.setError("Password should be 4 digit");
            confirm_passcode.requestFocus();
        }
        else if(enter_passcode.getText().toString().trim().equals(confirm_passcode.getText().toString().trim())){
            savePasscode();
        }
        else{
            confirm_passcode.setError("Password Mismatch");
            confirm_passcode.requestFocus();
        }

    }

    private void savePasscode() {
        if(!enter_passcode.getText().toString().trim().isEmpty()||!confirm_passcode.getText().toString().trim().isEmpty()){
            SharedPreferences sharedPreferences=getSharedPreferences("Passcode", Context.MODE_PRIVATE);
            SharedPreferences.Editor editor=sharedPreferences.edit();
            editor.putString("isPasscodeSaved",getString(R.string.yes_Spelling));
            editor.putString("passcode", confirm_passcode.getText().toString());
            editor.commit();
            Intent intentToHomePage=new Intent(Passcode_SignUp_Activity.this, First_Menu.class);
            startActivity(intentToHomePage);
            Toast.makeText(Passcode_SignUp_Activity.this,"Passcode Saved!",Toast.LENGTH_SHORT).show();
            finish();
        }
        else{
            Toast.makeText(Passcode_SignUp_Activity.this,"Enter passcode",Toast.LENGTH_SHORT).show();
        }

    }
}
