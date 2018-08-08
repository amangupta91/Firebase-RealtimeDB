package watersupplier.main.Generic;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.Toast;

import watersupplier.main.Main.Common_ActionBar_Abstract;
import watersupplier.main.Main.First_Menu;
import watersupplier.main.R;

/**
 * Created by aman on 25/10/17.
 */

public class Passcode_Activity extends Common_ActionBar_Abstract implements View.OnClickListener {
    
    //UI variables
    private EditText passcode;
    private String TAG = "Passcode_Activity";
    
    //Non-Ui variables


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_passcode);
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);//to disable keyboard
        initialization();
        generateEvents();
    }

    private void generateEvents() {
    }

    private void initialization() {
        passcode = (EditText) findViewById(R.id.passcode);
        getSupportActionBar().setDisplayHomeAsUpEnabled(false);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        passcode.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (passcode.getText().length()==4){

                    String PasscodeStatus = getSharedPreferences("Passcode", Context.MODE_PRIVATE).getString("isPasscodeSaved","");
                    String passCode = getSharedPreferences("Passcode",Context.MODE_PRIVATE).getString("passcode","");
                    Log.d(TAG,"Aman Check pass :: "+PasscodeStatus+"  "+passCode);
                    if(passcode.getText().toString().trim().equals(passCode)||
                            passcode.getText().toString().trim()==passCode ){
                        Intent intent=new Intent(Passcode_Activity.this,First_Menu.class);
                        startActivity(intent);
                        finish();
                    }
                    else{
                        Toast.makeText(Passcode_Activity.this,"Wrong Passcode",Toast.LENGTH_SHORT).show();
                    }

                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
    }

    @Override
    public void onClick(View v) {

        if (passcode.getText().length()==4){
            Intent intent=new Intent(Passcode_Activity.this,First_Menu.class);
            startActivity(intent);
            finish();
        }
    }
}
