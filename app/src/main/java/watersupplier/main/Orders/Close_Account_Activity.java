package watersupplier.main.Orders;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Vibrator;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.content.ContextCompat;
import android.telephony.SmsManager;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;

import watersupplier.main.Customer.Add_Edit_Customer_Activity;
import watersupplier.main.Customer.Customer_List_Activity;
import watersupplier.main.Firsebase_Operations.FirebaseInstance;
import watersupplier.main.Main.Common_ActionBar_Abstract;
import watersupplier.main.Pojo.Orders_POJO;
import watersupplier.main.R;
import watersupplier.main.Utils.AppUtills;

/**
 * Created by Aman Gupta on 20/1/17.
 */

public class Close_Account_Activity extends Common_ActionBar_Abstract implements View.OnClickListener {

    //UI variables
    private TextView customer_name_val,deposit_given_val;
    private EditText deposit_refund;
    private Button btn_send_sms,btn_close_account;
    private String phoneNo_string,cust_name_string;
    private int deposit_given_int;
    private ArrayList<Orders_POJO> orders_pojos;
    private Orders_POJO pojo;
    private View view;
    private SmsManager sms;
    private String get_pushKey;
//    private int refund_int=0;
    private String TAG = "Close_Account_Activity";

    //Non UI variables
    private FirebaseDatabase database;
    private DatabaseReference databaseReference,Db_Ref_customers,Db_Ref_Orders;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_close_account);
        initialization();
        generateEvents();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_with_back, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        AppUtills.givefinishEffect(this);
        finish();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        AppUtills.givefinishEffect(this);
        finish();
        return super.onOptionsItemSelected(item);

    }

    private void initialization() {
        getSupportActionBar().setTitle(R.string.orders_page);

        pojo = new Orders_POJO();
        customer_name_val = (TextView) findViewById(R.id.customer_name_val);
        deposit_given_val = (TextView) findViewById(R.id.deposit_given_val);
        deposit_given_int = Integer.parseInt(getIntent().getStringExtra(getString(R.string.deposit_given)));
        cust_name_string = getIntent().getStringExtra(getString(R.string.customer_name_spelling));
        customer_name_val.setText(cust_name_string);
        phoneNo_string = getIntent().getStringExtra(getString(R.string.mobile_spelling));
        get_pushKey = getIntent().getStringExtra("pushKey");
//        Log.d(TAG,"Aman CHK pushkey=="+get_pushKey);

        orders_pojos = (ArrayList<Orders_POJO>) getIntent().getSerializableExtra("AL");

        deposit_given_val.setText(deposit_given_int+"");
//        Log.d(TAG,"Aman chk AL=="+orders_pojos.get(1));
        database = FirebaseInstance.getInstance();
        databaseReference = database.getReference(getString(R.string.app_name)).child(AppUtills.getUserName(Close_Account_Activity.this)).child(getString(R.string.orders_page));
        Db_Ref_customers = database.getReference(getString(R.string.app_name)).child(AppUtills.getUserName(Close_Account_Activity.this)).child(getString(R.string.customer_page));
        Db_Ref_Orders = database.getReference(getString(R.string.app_name)).child(AppUtills.getUserName(Close_Account_Activity.this)).
                child(getString(R.string.orders_page)).child(cust_name_string);

        deposit_refund = (EditText) findViewById(R.id.deposit_refund);
        btn_send_sms = (Button) findViewById(R.id.btn_send_sms);
        btn_close_account = (Button) findViewById(R.id.btn_close_account);
    }

    private void generateEvents() {
        btn_send_sms.setOnClickListener(this);
        btn_close_account.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {

        switch (v.getId()){

            case R.id.btn_send_sms:
                if (deposit_refund.getText().toString().isEmpty()) {
                    deposit_refund.requestFocus();
                    deposit_refund.setError(getResources().getString(R.string.cannot_be_empty));
                }
                else
                    SMS();
                break;
            case R.id.btn_close_account:
                if (deposit_refund.getText().toString().isEmpty()) {
                    deposit_refund.requestFocus();
                    deposit_refund.setError(getResources().getString(R.string.cannot_be_empty));
                }
                else
                    close_account();
                break;
        }

    }

    private void close_account() {
        int refund_int = Integer.parseInt(deposit_refund.getText().toString());
         if (refund_int >deposit_given_int){
//             Toast.makeText(this, "Advance Refund should be Rs"+deposit_given_int, Toast.LENGTH_SHORT).show();


             Snackbar.make(findViewById(android.R.id.content), "Advance Refund should be Rs "+deposit_given_int, Snackbar.LENGTH_LONG)
                     .setActionTextColor(getResources().getColor(R.color.red_ToastColor))
                     .show();
        }
         else

             deleteDialog();

    }

    private void deleteDialog() {
        final AlertDialog.Builder builder=new AlertDialog.Builder(Close_Account_Activity.this);
        builder.setMessage("Do you want to close Account of "+cust_name_string+"?");
        builder.setTitle("Close Account");
        builder.setIcon(ContextCompat.getDrawable(Close_Account_Activity.this,R.drawable.warning));
        builder.setPositiveButton(getString(R.string.yes_Spelling), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {

                if (AppUtills.isNetworkAvailable(Close_Account_Activity.this)) {
                    DeleteListners();
                    DeleteOrders();

                }else{
                    Toast.makeText(Close_Account_Activity.this,getString(R.string.CheckYourInternetConnection),Toast.LENGTH_SHORT).show();
                }

            }

            private void DeleteOrders() {
                Db_Ref_Orders.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        JSONArray jsonElements = null;
                        try {
                            if (dataSnapshot.getValue() != null && !dataSnapshot.getValue().equals("")) {
                                Gson gson = new Gson();
                                String json = gson.toJson(dataSnapshot.getValue());
                                JSONObject jsonObject = new JSONObject(json.toString());
                                Iterator iterator = jsonObject.keys();
                                while (iterator.hasNext()) {
                                    String key = (String) iterator.next();
                                    JSONObject jsonObjectChild = jsonObject.getJSONObject(key);
//                                ArrayList<String> BillPrefAL = new ArrayList<String>();


                                    Orders_POJO orders_pojo = new Orders_POJO(
                                            jsonObjectChild.getInt("order_id"),
                                            jsonObjectChild.getString("cans_with_customer"),
                                            jsonObjectChild.getString("total_amount"),
                                            jsonObjectChild.getString("balance"),
                                            jsonObjectChild.getString("cans_return"),
                                            jsonObjectChild.getString("new_cans_given"),
                                            jsonObjectChild.getString("money_received"),
                                            jsonObjectChild.getString("deposit"),
                                            jsonObjectChild.getString("return_date"),
                                            jsonObjectChild.getString("customer_name"), key,
                                            jsonObject.getString("order_date"),
                                            jsonObjectChild.optString("mobile"));

//                            Log.d(TAG,"Aman CHK PUSH ID=="+orders_pojo.push_key);
//                            Log.d(TAG,"Aman CHK Name=="+orders_pojo.customer_name);
//                            Log.d(TAG,"Aman CHK Total =="+orders_pojo.total_amount);
//                            Log.d(TAG,"Aman CHK cans with cust =="+orders_pojo.cans_with_customer);

                                    //here deleting all the customer related Data
                                    Db_Ref_Orders.child(orders_pojo.push_key).addValueEventListener(new ValueEventListener() {
                                        @Override
                                        public void onDataChange(DataSnapshot dataSnapshot) {

                                            dataSnapshot.getRef().removeValue();

//                                            finish();
                                        }

                                        @Override
                                        public void onCancelled(DatabaseError databaseError) {

                                        }
                                    });

                                }

                            }

                          /*  Collections.sort(orders_pojosAL, new Comparator<Orders_POJO>() {
                                @Override
                                public int compare(Orders_POJO lhs, Orders_POJO rhs) {
                                    return lhs.order_id - rhs.order_id;
                                }
                            });*/

                        } catch (Exception e) {
                            e.printStackTrace();
                        } finally {
                            try {
//                        progressDialog.dismiss();
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                    }



                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }


                });
            }

            private void DeleteListners() {
                Db_Ref_customers.child(get_pushKey).addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {

                        dataSnapshot.getRef().removeValue();
                        // Get instance of Vibrator from current Context

                        finish();
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });
            }


        });

        builder.setNegativeButton(getString(R.string.no_Spelling), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();
            }
        });
        builder.show();
    }


    private void send_sms() {
        int refund_int = Integer.parseInt(deposit_refund.getText().toString());
        if (deposit_given_int!=refund_int){
            Snackbar.make(findViewById(android.R.id.content), "Advance Refund should be Rs "+deposit_given_int, Snackbar.LENGTH_LONG)
                    .setActionTextColor(getResources().getColor(R.color.red_ToastColor))
                    .show();
        }
        else if (deposit_given_int==refund_int){

            SMS();
        }
    }

    private void SMS() {


        /*//Getting intent and PendingIntent instance
        Intent intent=new Intent(this,Manage_Orders_Activity.class);
        PendingIntent pi=PendingIntent.getActivity(this, 0, intent,0);*/

            try {
                sms = SmsManager.getDefault();
//            Log.d(TAG, "Aman Chk phone ==" + phoneNo_string);

                String sms_text = "****** Account Close ******"+" "+
                        "Customer Name:- " + cust_name_string + " ," +
                        "Deposit Given:- " + deposit_given_int + " ," +
                        "Deposit Refund:- " + deposit_refund.getText().toString() + " ";

          Log.d(TAG,"Aman chk sms"+sms_text);
//                Log.d(TAG,"Aman chk 1"+cust_name_string);
//                Log.d(TAG,"Aman chk 2=="+deposit_given_int);
//                Log.d(TAG,"Aman chk 3=="+ deposit_refund.getText().toString());
                sms.sendTextMessage(phoneNo_string, null, sms_text, null, null);
                Snackbar.make(findViewById(android.R.id.content), "SMS sent!", Snackbar.LENGTH_LONG)
                        .setActionTextColor(getResources().getColor(R.color.red_ToastColor))
                        .show();

           /* if (ContextCompat.checkSelfPermission(Manage_Orders_Activity.this, Manifest.permission.SEND_SMS)!= PackageManager.PERMISSION_GRANTED) {
                if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.SEND_SMS)) {
                } else {
                    ActivityCompat.requestPermissions(this,
                            new String[]{Manifest.permission.SEND_SMS},
                            MY_PERMISSIONS_REQUEST_SEND_SMS);
                }
            }*/

            } catch (Exception e) {
//                Toast.makeText(this, "SMS failed, please try again later!", Toast.LENGTH_SHORT).show();
                Snackbar.make(findViewById(android.R.id.content), "SMS failed, please try again later!", Snackbar.LENGTH_LONG)
                        .setActionTextColor(getResources().getColor(R.color.red_ToastColor))
                        .show();
                e.printStackTrace();
            }
            // Get instance of Vibrator from current Context
            Vibrator v = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
            // Vibrate for 400 milliseconds
            v.vibrate(400);
        }


}
