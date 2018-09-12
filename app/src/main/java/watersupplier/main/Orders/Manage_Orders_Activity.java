package watersupplier.main.Orders;

import android.Manifest;
import android.animation.ArgbEvaluator;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.os.Vibrator;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.telephony.SmsManager;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.Iterator;

import watersupplier.main.Firsebase_Operations.FirebaseInstance;
import watersupplier.main.Main.Common_ActionBar_Abstract;
import watersupplier.main.Pojo.Customer_POJO;
import watersupplier.main.Pojo.Orders_POJO;
import watersupplier.main.Pojo.Product_POJO;
import watersupplier.main.R;
import watersupplier.main.Utils.AppUtills;

/**
 * Created by Aman Gupta on 10/2/17.
 */

public class Manage_Orders_Activity extends Common_ActionBar_Abstract implements View.OnClickListener,TextWatcher {

    //UI variables
    private EditText cans_return, new_cans, money_recieved, deposit, return_date,invisible;
    private ImageView date_icon;
    private TextView customer_name, cans_with_customer, total, balance, customer_name_val, cans_with_customer_val, total_val, balance_val;
    private Button btn_send_sms, btn_close_account;
    private ObjectAnimator textColorAnim;

    //Non-UI variables
    private FirebaseDatabase database;
    private DatabaseReference databaseReference,databaseReference_prod;
    private ArrayList<Orders_POJO>orders_pojosAL;
    private ArrayList<String> al;
    //string which will store parceable value coming from order search list
    private String cust_name_string, creditLine_string, cust_spPrice_string, phoneNo_string, order_push_key,get_pushKey,spPrice_intWalkin;

    //int which will store final calculation
    private int total_int, balance_int, cans_with_customer_int, new_cans_int, cans_return_int, money_recieved_int, spPrice_int;

    private static final int MY_PERMISSIONS_REQUEST_SEND_SMS = 0;
    private Orders_POJO orders_pojo;
    private Customer_POJO customer_pojo;
    private int order_id = 0;
    private ArrayList<Integer> orderID_AL;
    private String TAG = "Manage_Orders_Activity";
    private int year, month, day;
    private String display_date_format,order_date;
    private Calendar calander;
    private SmsManager sms;
    String url = "http://httpbin.org/html";

    //Array list ,which will fetch firebase data and save it
    private ArrayList<String> total_amountAL,balanceAL,cans_with_custAL,money_recievedAL,new_cansAL,cans_returnAL;
//    private String last_total_amount,last_bal,last_cans_with_cust,last_new_cans,last_money_recieved,last_cans_return;
    private int last_total_int, last_balance_int, last_cans_with_customer_int, last_new_cans_int, last_cans_return_int, last_money_recieved_int, last_spPrice_int;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manage_orders);
        initialization();
        generateEvents();
        screenWidth();
        Event_Listners();
//        volly();
//        setText();
    }

    private void volly() {

        // Request a string response
        StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {

                        // Result handling
                        System.out.println(response.substring(0,100));
                        Log.d(TAG,"Aman response=="+response.substring(0,500));
                        cans_return.setText(response.substring(1,200));

                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

                // Error handling
                System.out.println("Something went wrong!");
                error.printStackTrace();

            }
        });

// Add the request to the queue
        Volley.newRequestQueue(this).add(stringRequest);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_with_back, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        AppUtills.givefinishEffect(this);
        finish();
        return super.onOptionsItemSelected(item);

    }

    private void setText() {


        cans_return.isFocusable();

//        total_val.setText(orders_pojo.total_amount);
        // i have around 200 items to show in a way that most likes comes on top all the time

       /* databaseReference.orderByChild('likes').on("child_added", function(snapshot) {
            var data = snapshot.val();
            html.push('<div>'+data.likes+'</div>');
            document.getElementById('data').innerHTML = html.join('');
        }*/
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                orders_pojosAL.clear();
                JSONArray jsonElements = null;
                try {
                    if (dataSnapshot.getValue() != null && !dataSnapshot.getValue().equals("")) {
                        Gson gson = new Gson();
                        String json = gson.toJson(dataSnapshot.getValue());
                        JSONObject jsonObject = new JSONObject(json.toString());
                        Iterator iterator = jsonObject.keys();
                        orders_pojosAL.clear();
                        while (iterator.hasNext()) {
                            String key = (String) iterator.next();
                            JSONObject jsonObjectChild = jsonObject.getJSONObject(key);
//                                ArrayList<String> BillPrefAL = new ArrayList<String>();


                            orders_pojo = new Orders_POJO(
                                    jsonObjectChild.optInt("order_id"),
                                    jsonObjectChild.optString("cans_with_customer"),
                                    jsonObjectChild.optString("total_amount"),
                                    jsonObjectChild.optString("balance"),
                                    jsonObjectChild.optString("cans_return"),
                                    jsonObjectChild.optString("new_cans_given"),
                                    jsonObjectChild.optString("money_received"),
                                    jsonObjectChild.optString("deposit"),
                                    jsonObjectChild.optString("return_date"),
                                    jsonObjectChild.optString("customer_name"), key,
                                    jsonObjectChild.optString("order_date"),
                                    jsonObjectChild.optString("mobile"));
                            orders_pojosAL.clear();
                            orders_pojosAL.add(orders_pojo);
//                            orders_pojosAL.get(orders_pojosAL.size()-1);


                            //get-set Text DEPOSIT
                            if (orders_pojo.deposit != null) {
                                deposit.setText(orders_pojo.deposit + "");
                            } else
                                deposit.setText("");


                            //initializing AL
                            total_amountAL = new ArrayList<String>();
//                            total_amountAL.clear();

                            balanceAL = new ArrayList<String>();
//                            balanceAL.clear();

                            cans_with_custAL = new ArrayList<String>();
//                            cans_with_custAL.clear();

                            new_cansAL = new ArrayList<String>();
//                            new_cansAL.clear();

                            money_recievedAL = new ArrayList<String>();
//                            money_recievedAL.clear();

                            cans_returnAL = new ArrayList<String>();
//                            cans_returnAL.clear();
//                        if(cans_with_custAL.contains("")){
//                            cans_with_custAL.clear();
//                        }

//                            Log.d(TAG, "Aman Chk last_bal ==" + orders_pojo.balance);

//                            Log.d(TAG, "Aman CHK TOTAL POJO ==" + total_amountAL);
//                            Log.d(TAG, "Aman CHK BALANCE POJO ==" + orders_pojo.balance);
                        }
                        //adding data in AL
                        total_amountAL.add(orders_pojo.total_amount);
                        balanceAL.add(orders_pojo.balance);
                        cans_with_custAL.add(orders_pojo.cans_with_customer);
                        new_cansAL.add(orders_pojo.new_cans_given);
                        money_recievedAL.add(orders_pojo.money_received);
                        cans_returnAL.add(orders_pojo.cans_return);


                            last_total_int = Integer.parseInt((total_amountAL.get(total_amountAL.size()-1)));
                            last_balance_int = Integer.parseInt(balanceAL.get(balanceAL.size() - 1));
                            last_cans_with_customer_int = Integer.parseInt(cans_with_custAL.get(cans_with_custAL.size() - 1));
                            last_new_cans_int = Integer.parseInt(new_cansAL.get(new_cansAL.size() - 1));
                            last_money_recieved_int = Integer.parseInt(money_recievedAL.get(money_recievedAL.size() - 1));
                            last_cans_return_int = Integer.parseInt(cans_returnAL.get(cans_returnAL.size() - 1));
//                            Log.d(TAG, "Aman Chk last_total_int==" + last_total_int);


//                        Collections.reverse(total_amountAL);

//                        Log.d(TAG, "Aman Chk mobile ==" + orders_pojo.mobile);

                       /* Log.d(TAG, "Aman Chk last_bal ==" + last_balance_int);
                        Log.d(TAG,"Aman Chk last_cans_with_cust =="+last_cans_with_customer_int);
                        Log.d(TAG,"Aman Chk last_new_cans =="+last_new_cans_int);
                        Log.d(TAG,"Aman Chk last_money_recieved =="+last_money_recieved_int);
                        Log.d(TAG,"Aman Chk last_cans_return =="+last_cans_return_int);*/
                        total_amountAL.clear();
                        balanceAL.clear();
                        cans_with_custAL.clear();
                        money_recievedAL.clear();
                        new_cansAL.clear();
                        cans_returnAL.clear();

                            //get-set Text TOTAL

                            total_val.setText(last_balance_int + "");
                       /* if (total_amountAL.isEmpty()) {
                            total_val.setText(last_total_int + "");
                        }
                        else
                            total_val.setText(last_balance_int+"");*/

                            //get-set Text CANS WITH CUSTOMER

                            cans_with_customer_val.setText(last_cans_with_customer_int + "");
                       /* if (last_cans_with_customer_int != 0) {
                            cans_with_customer_val.setText(last_cans_with_customer_int + "");
                        }
                        else
                            cans_with_customer_val.setText("0");*/


                            //get-set Text BALANCE
                            balance_val.setText(last_balance_int + "");
                       /* if (last_balance_int != 0) {
//                            int final2 =last_total_int - last_money_recieved_int;
                            balance_val.setText(last_balance_int + "");
                        }
                        else
                            balance_val.setText("0");
                        }*/


                    }

                       Collections.sort(orders_pojosAL, new Comparator<Orders_POJO>() {
                            @Override
                            public int compare(Orders_POJO lhs, Orders_POJO rhs) {
                                return lhs.order_id - rhs.order_id;
                            }
                        });

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




        customer_name_val.setText(cust_name_string + "");

        Calendar calendar = Calendar.getInstance();
//        Integer.parseInt(creditLine_string);
        if(!creditLine_string.isEmpty()) {
            int sys_date = Integer.parseInt(creditLine_string);
            calendar.add(Calendar.DATE, +sys_date);
        }
        else
            calendar.add(Calendar.DATE,0);

        Date date = new Date(calendar.getTimeInMillis());
        java.text.SimpleDateFormat formatter = null;
        formatter = new java.text.SimpleDateFormat(AppUtills.DATE_FORMAT_DDMMMYYYY);
        display_date_format = formatter.format(date);
        return_date.setText(display_date_format);


    }

    /* @Override
     public void onCancelled(DatabaseError databaseError) {

     }
 });
}*/
    private void initialization() {
        getSupportActionBar().setTitle(R.string.orders_page);
        //get Intent Text

        cust_name_string = getIntent().getStringExtra(getString(R.string.customer_name_spelling));
        creditLine_string = getIntent().getStringExtra("credit_line");
            cust_spPrice_string = getIntent().getStringExtra("customer_spPrice");
        phoneNo_string = getIntent().getStringExtra("phone");
        get_pushKey = getIntent().getStringExtra("pushKey");
//        Log.d(TAG,"Aman CHK cust_name_string=="+cust_name_string);
//        Log.d(TAG,"Aman CHK creditLine_string=="+creditLine_string);
        Log.d(TAG,"Aman CHK cust_spPrice_string=="+cust_spPrice_string);
//        Log.d(TAG,"Aman CHK phoneNo_string=="+phoneNo_string);


        database = FirebaseInstance.getInstance();
        databaseReference = database.getReference(getString(R.string.app_name)).child(AppUtills.getUserName(Manage_Orders_Activity.this)).
                child(getString(R.string.orders_page)).child(cust_name_string);
        databaseReference_prod=database.getReference(getString(R.string.app_name)).child(AppUtills.getUserName(Manage_Orders_Activity.this)).
                child(getString(R.string.product_page));
        orders_pojo = new Orders_POJO();
        orderID_AL = new ArrayList<Integer>();
        orders_pojosAL = new ArrayList<Orders_POJO>();
        cans_return = (EditText) findViewById(R.id.cans_return);
        new_cans = (EditText) findViewById(R.id.new_cans);
        money_recieved = (EditText) findViewById(R.id.money_recieved);
        deposit = (EditText) findViewById(R.id.deposit);
        return_date = (EditText) findViewById(R.id.return_date);
        invisible = (EditText) findViewById(R.id.invisible);
        date_icon = (ImageView) findViewById(R.id.date_icon);

        customer_name = (TextView) findViewById(R.id.customer_name);
        cans_with_customer = (TextView) findViewById(R.id.cans_with_customer);
        balance = (TextView) findViewById(R.id.balance);
        total = (TextView) findViewById(R.id.total);
        customer_name_val = (TextView) findViewById(R.id.customer_name_val);
        cans_with_customer_val = (TextView) findViewById(R.id.cans_with_customer_val);
        balance_val = (TextView) findViewById(R.id.balance_val);
        total_val = (TextView) findViewById(R.id.total_val);

        btn_close_account = (Button) findViewById(R.id.btn_close_account);
        btn_send_sms = (Button) findViewById(R.id.btn_send_sms);
        setText();

        textColorAnim = ObjectAnimator.ofInt(customer_name_val, "textColor", Color.BLACK, Color.TRANSPARENT);
        textColorAnim.setDuration(500);
        textColorAnim.setEvaluator(new ArgbEvaluator());
        textColorAnim.setRepeatCount(ValueAnimator.INFINITE);
        textColorAnim.setRepeatMode(ValueAnimator.REVERSE);
//        textColorAnim.start();

        //date logic below
        calander = Calendar.getInstance();
        year = calander.get(Calendar.YEAR);
        month = (calander.get(Calendar.MONTH));
        if (!creditLine_string.isEmpty()) {
            day = calander.get(Calendar.DAY_OF_MONTH) + Integer.parseInt(creditLine_string);
        } else{
            day = calander.get(Calendar.DAY_OF_MONTH);}


        //date logic below
        calander = Calendar.getInstance();
        calander.add(Calendar.DATE,0);
        Date date = new Date(calander.getTimeInMillis());
        java.text.SimpleDateFormat formatter = null;
        formatter = new java.text.SimpleDateFormat(AppUtills.DATE_FORMAT_DDMMMYYYY);
        display_date_format = formatter.format(date);
        order_date = display_date_format.toString();

        year = calander.get(Calendar.YEAR);
        month = (calander.get(Calendar.MONTH));
        day = calander.get(Calendar.DAY_OF_MONTH);

        new_cans.addTextChangedListener(this);

    }

    private void generateEvents() {
        btn_close_account.setOnClickListener(this);
        btn_send_sms.setOnClickListener(this);
        return_date.setOnClickListener(this);
        date_icon.setOnClickListener(this);
        new_cans.addTextChangedListener(this);
    }

    private void screenWidth() {
    }

    private void Event_Listners() {
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                if (dataSnapshot.getValue() != null && !dataSnapshot.getValue().equals("")) {

//                    Log.d(TAG,"Aman Chk CUST_ID =="+dataSnapshot);

                    for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {

                        Orders_POJO pojo = postSnapshot.getValue(Orders_POJO.class);


                        orderID_AL.add(pojo.order_id);
//                        Log.d(TAG,"Aman Chk ORDR_ID =="+order_id);


                    }
                } else {

                    order_id = 1;
//                    Log.d(TAG,"Aman Chk ORDR_ID =="+order_id);

                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        databaseReference_prod.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                if (dataSnapshot.getValue() != null && !dataSnapshot.getValue().equals("")) {

                    for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {

                        Product_POJO pojo = postSnapshot.getValue(Product_POJO.class);

                        spPrice_intWalkin =pojo.price+"";

//                        orderID_AL.add(pojo.order_id);



                    }
                }

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }




    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            /*case R.id.btn_send_sms:
                if (cans_return.getText().toString().isEmpty()) {
                    cans_return.requestFocus();
                    cans_return.setError(getResources().getString(R.string.cannot_be_empty));
                } else if (new_cans.getText().toString().isEmpty()) {
                    new_cans.requestFocus();
                    new_cans.setError(getResources().getString(R.string.cannot_be_empty));
                } else if (money_recieved.getText().toString().isEmpty()) {
                    money_recieved.requestFocus();
                    money_recieved.setError(getResources().getString(R.string.cannot_be_empty));
                } else if (deposit.getText().toString().isEmpty()) {
                    deposit.requestFocus();
                    deposit.setError(getResources().getString(R.string.cannot_be_empty));
                } else if (return_date.getText().toString().isEmpty()) {
                    return_date.requestFocus();
                    return_date.setError(getResources().getString(R.string.cannot_be_empty));
                } else {
                    if (AppUtills.isNetworkAvailable(Manage_Orders_Activity.this)) {
//                        save();
//                        Editable s =null;
//                        afterTextChanged(s);
                    } else
//                    save();
                        Snackbar.make(findViewById(android.R.id.content), getResources().getString(R.string.CheckYourInternetConnection), Snackbar.LENGTH_LONG)
                                .setActionTextColor(getResources().getColor(R.color.red_ToastColor))
                                .show();
                }
                break;*/
            case R.id.return_date:
                if (return_date.isFocusableInTouchMode()) {
                    showDialog(999);
                    Snackbar.make(findViewById(android.R.id.content), getResources().getString(R.string.CheckYourInternetConnection), Snackbar.LENGTH_LONG)
                            .setActionTextColor(getResources().getColor(R.color.red_ToastColor))
                            .show();

                }
                break;
            case R.id.date_icon:
                showDialog(999);
                Toast.makeText(this,"Date already according to credit line!",Toast.LENGTH_SHORT).show();

                break;
            case R.id.btn_close_account:
                if (deposit.getText().toString().isEmpty()) {
                    deposit.requestFocus();
                    deposit.setError(getResources().getString(R.string.cannot_be_empty));
                }
                else if(!cans_with_customer_val.getText().equals("0")){
                    Toast.makeText(this,"Cans with Customer should be 0 !",Toast.LENGTH_SHORT).show();

                }
                else if(!balance_val.getText().equals("0")){
                    Toast.makeText(this,"Balance should be 0 ",Toast.LENGTH_SHORT).show();

                }
                else {
                    Intent intent = new Intent(Manage_Orders_Activity.this, Close_Account_Activity.class);
                    intent.putExtra(getString(R.string.deposit_given), deposit.getText().toString());
                    intent.putExtra(getString(R.string.customer_name_spelling), cust_name_string + "");
                    intent.putExtra(getString(R.string.mobile_spelling),phoneNo_string);
                    intent.putExtra("pushKey",get_pushKey);
                    intent.putExtra("AL",orderID_AL);
                    startActivity(intent);
                    AppUtills.giveIntentEffect(this);
                    this.finish();
                }
                break;
        }

    }

    @Override
    @Deprecated
    protected Dialog onCreateDialog(int id) {
        return new DatePickerDialog(this, datePickerListener, year, month, day);
    }

    private DatePickerDialog.OnDateSetListener datePickerListener = new DatePickerDialog.OnDateSetListener() {
        public void onDateSet(DatePicker view, int selectedYear, int selectedMonth, int selectedDay) {
            String date = "";
            date = selectedDay + "-" + (selectedMonth + 1) + "-" + selectedYear;

            return_date.setText(AppUtills.dateFormatConverter(date, AppUtills.DATE_FORMAT_DDMMYYYY, AppUtills.DATE_FORMAT_DDMMMYYYY));

        }
    };

    private void save() {
        if (orderID_AL.size() != 0) {
            order_id = getOrderID();
        }
//        last_total_int, last_balance_int, last_cans_with_customer_int, last_new_cans_int, last_cans_return_int, last_money_recieved_int, last_spPrice_int
           /* new_cans_int = new Integer(new_cans.getText().toString());
            cans_return_int = new Integer(cans_return.getText().toString());
            money_recieved_int = new Integer(money_recieved.getText().toString());
            spPrice_int = Integer.parseInt(cust_spPrice_string);*/


/*
                if(last_cans_with_customer_int!=0&& new_cans_int!=0){
                    cans_with_customer_int = last_cans_with_customer_int+ (new_cans_int - cans_return_int);
//                    cans_with_customer_int = new_cans_int;
//                    Toast.makeText(this,"No cans with customer,so cans return will be 0!", Toast.LENGTH_SHORT).show();
                }
                else if(new_cans_int==0){
                    cans_with_customer_int = Math.abs(last_cans_with_customer_int-cans_return_int);
                }
                else
                    cans_with_customer_int = new_cans_int;

                //from 2nd time
                if(last_balance_int!=0||last_balance_int>0) {
                    if(last_total_int>money_recieved_int||last_total_int==money_recieved_int){
                        balance_int = last_total_int - money_recieved_int;
                        total_int = last_balance_int+new_cans_int * spPrice_int;
                    }
                }
                //for 1st time
                else  {

                    total_int = new_cans_int * spPrice_int;

                }


         if(money_recieved_int>total_int){
             balance_int=0;
        }
        else if(last_total_int==0){
            balance_int=Math.abs(total_int-money_recieved_int);
        }
        else
            balance_int = last_total_int - money_recieved_int;
        Log.d(TAG,"Aman CANS=="+cans_with_customer_int);
        Log.d(TAG,"Aman TOTAL=="+total_int);
        Log.d(TAG,"Aman BALANCE=="+balance_int);

        if(last_total_int<money_recieved_int&&last_balance_int!=0){
            Toast.makeText(this, "Money recieved cannot be grater", Toast.LENGTH_SHORT).show();
        }

        else {*/
          /*  Editable s=null;

        setTextOnNewCansGiven(s);
        Log.d(TAG,"Aman CANS1=="+cans_with_customer_int);
        Log.d(TAG,"Aman TOTAL2=="+total_int);
        Log.d(TAG,"Aman BALANCE2=="+balance_int);

            Orders_POJO orders_pojo = new Orders_POJO(order_id, cans_with_customer_int + "", total_int + "", balance_int + "",
                    cans_return.getText().toString(),
                    new_cans.getText().toString(),
                    money_recieved.getText().toString(),
                    deposit.getText().toString(),
                    return_date.getText().toString(), cust_name_string.toString(), "");


            databaseReference.push().setValue(orders_pojo);
            Toast.makeText(this, getResources().getString(R.string.saved_sucess), Toast.LENGTH_SHORT).show();
            send_SMS();
            finish();*/
        }



    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {

//        setTextOnNewCansGiven(s);
    }

    private void setTextOnNewCansGiven(CharSequence s) {
        String str1=null,str2=null,str3=null;

        int NEW_CANS_GIVEN,BALANCE,AMOUNT;
        if (orderID_AL.size() != 0) {
            order_id = getOrderID();
        }
        if(!s.toString().isEmpty() && !s.toString().trim().equals(" ") ) {

//            TOTAL = Integer.parseInt(s.toString().trim());
//            BALANCE = Integer.parseInt(s.toString().trim());
                NEW_CANS_GIVEN = Integer.parseInt(s.toString().trim());

            Log.d(TAG, "Aman Chk last_bal ==" + last_balance_int);
            Log.d(TAG, "Aman Chk last_total_int ==" + last_total_int);
            Log.d(TAG,"Aman Chk last_cans_with_cust =="+last_cans_with_customer_int);
            Log.d(TAG,"Aman Chk last_new_cans =="+last_new_cans_int);
            Log.d(TAG,"Aman Chk last_money_recieved =="+last_money_recieved_int);
            Log.d(TAG,"Aman Chk last_cans_return =="+last_cans_return_int);


            new_cans_int = new Integer(new_cans.getText().toString());
            if(!cans_return.getText().toString().isEmpty()){
                cans_return_int = new Integer(cans_return.getText().toString());
            }
            else
                cans_return_int = 0;

            if(!money_recieved.getText().toString().trim().isEmpty()) {
                money_recieved_int = new Integer(money_recieved.getText().toString().trim());
            }else
                money_recieved_int =0;

            if(cust_spPrice_string==""||cust_spPrice_string.isEmpty()){
                spPrice_int = Integer.parseInt(spPrice_intWalkin);
            }
            else {
                spPrice_int = Integer.parseInt(cust_spPrice_string);
            }



            //CANS_WITH_CUSTOMER
            if(last_cans_with_customer_int!=0 ||last_cans_with_customer_int>0 && last_cans_with_customer_int>cans_return_int||
                    last_cans_with_customer_int==cans_return_int){
                cans_with_customer_int = last_cans_with_customer_int+ (new_cans_int - cans_return_int);

            }
            else if(new_cans_int==0){
                cans_with_customer_int = Math.abs(last_cans_with_customer_int-cans_return_int);
            }
            else
                cans_with_customer_int=NEW_CANS_GIVEN;

            //TOTAL
            if(last_balance_int!=0||last_balance_int>0){
                total_int = NEW_CANS_GIVEN * spPrice_int+last_balance_int;
                total_val.setText(total_int+"");
            }
            else  {
                total_int = NEW_CANS_GIVEN * spPrice_int;
                total_val.setText(total_int+"");
            }

            money_recieved.addTextChangedListener(new TextWatcher() {

                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                }

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {

                }

                @Override
                public void afterTextChanged(Editable s) {

                    int MONEY_RECIEVED;

                    if (!s.toString().isEmpty() && !s.toString().trim().equals(" ")) {
                        MONEY_RECIEVED = Integer.parseInt(s.toString().trim());


                        money_recieved_int = MONEY_RECIEVED;
                        //BALANCE
                        if (last_total_int != 0 || last_total_int > 0 &&
                                last_money_recieved_int < total_int || last_money_recieved_int == total_int) {
                            balance_int = total_int - MONEY_RECIEVED;
                            balance_val.setText(balance_int+"");
                        } else if (last_balance_int == 0 || total_int < money_recieved_int || total_int == money_recieved_int) {
                            balance_int = total_int - MONEY_RECIEVED;
                            balance_val.setText(balance_int+"");
                        }
                    }
                }
            });

        }
        else {
            cans_with_customer_val.setText("0");
            total_val.setText("0");
            balance_val.setText("0");
        }


    }

    @Override
    public void afterTextChanged(Editable s) {
        setTextOnNewCansGiven(s.toString());



        btn_send_sms.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

//                setTextOnNewCansGiven(s);
                Orders_POJO orders_pojo = new Orders_POJO(order_id, cans_with_customer_int + "", total_int + "", balance_int + "",
                        cans_return.getText().toString(),
                        new_cans.getText().toString(),
                        money_recieved.getText().toString(),
                        deposit.getText().toString(),
                        return_date.getText().toString(), cust_name_string.toString(), "",order_date.toString(),phoneNo_string.toString());


                databaseReference.push().setValue(orders_pojo);
                Toast.makeText(Manage_Orders_Activity.this, getResources().getString(R.string.saved_sucess), Toast.LENGTH_SHORT).show();
                send_SMS();
//                Intent intent = new Intent(Manage_Orders_Activity.this, Order_Search_ListActivity.class);
//                startActivity(intent);
                finish();
            }
        });

    }


    private int getOrderID() {
        return Collections.max(orderID_AL) + 1;
    }

    private void send_SMS() {


        try {
            sms = SmsManager.getDefault();
//            Log.d(TAG, "Aman Chk phone ==" + phoneNo_string);

            String sms_text ="******Account Summary******"+" "+
                    "Customer Name:-" + cust_name_string + " ," +
                    "Cans Return:-" + cans_return.getText().toString() + " ," +
                    "New Cans:-" + new_cans.getText().toString() + " ," +
                    "Money Recieved:-" + money_recieved.getText().toString() + " ," +
                    "Cans with Customer:-" + cans_with_customer_int + " ," +
                    "Deposit Given:-" + deposit.getText().toString() + " ," +
                    "Balance:-" + balance_int + " ," +
                    "Total Amount:-" + total_int;
//          Log.d(TAG,"Aman chk sms"+sms_text);
            sms.sendTextMessage(phoneNo_string, null, sms_text, null, null);
            Toast.makeText(this, "SMS sent!", Toast.LENGTH_SHORT).show();

           /* if (ContextCompat.checkSelfPermission(Manage_Orders_Activity.this, Manifest.permission.SEND_SMS)!= PackageManager.PERMISSION_GRANTED) {
                if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.SEND_SMS)) {
                } else {
                    ActivityCompat.requestPermissions(this,
                            new String[]{Manifest.permission.SEND_SMS},
                            MY_PERMISSIONS_REQUEST_SEND_SMS);
                }
            }*/

        } catch (Exception e) {
            /*Snackbar.make(findViewById(android.R.id.content), "SMS failed, please try again later!", Snackbar.LENGTH_LONG)
                    .setActionTextColor(getResources().getColor(R.color.red_ToastColor))
                    .show();*/

            Toast.makeText(this, "SMS failed, please try again later!", Toast.LENGTH_SHORT).show();
            e.printStackTrace();
        }

    }

    @Override
    public void onBackPressed() {
        AppUtills.givefinishEffect(this);
        this.finish();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST_SEND_SMS:
                int currentAPIVersion = Build.VERSION.SDK_INT;

                if (currentAPIVersion >= android.os.Build.VERSION_CODES.M) {
                    if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                        Toast.makeText(this, "SEND_SMS DONE", Toast.LENGTH_SHORT).show();
                    }
                }
        else {

                    Toast.makeText(this, "SEND_SMS Denied", Toast.LENGTH_SHORT).show();
                }
                break;
            default:
                super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }

        }


    }


