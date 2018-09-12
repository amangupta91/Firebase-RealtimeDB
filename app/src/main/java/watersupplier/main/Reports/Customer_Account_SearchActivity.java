package watersupplier.main.Reports;

import android.animation.ArgbEvaluator;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import dmax.dialog.SpotsDialog;
import watersupplier.main.Adapter.Customer_ListAdapter;
import watersupplier.main.Adapter.Order_ListAdapter;
import watersupplier.main.Customer.Customer_List_Activity;
import watersupplier.main.Firsebase_Operations.FirebaseInstance;
import watersupplier.main.Main.Common_ActionBar_Abstract;
import watersupplier.main.Orders.Order_Search_ListActivity;
import watersupplier.main.Pojo.Customer_POJO;
import watersupplier.main.Pojo.Orders_POJO;
import watersupplier.main.R;
import watersupplier.main.Utils.AppUtills;

/**
 * Created by Aman Gupta on 24/4/17.
 */

public class Customer_Account_SearchActivity extends Common_ActionBar_Abstract implements View.OnClickListener,AdapterView.OnItemClickListener{

    //UI variables
//    private AutoCompleteTextView ;
    private EditText from_date,to_date,editText_search,customer_name_search;
    private Button btn_generate,btn_export,btn_export_mail,button_close;
    private ImageView from_date_icon,to_date_icon;
    private View view;
    private Dialog selectStudentDialog, AddStudentDialog;
    private ListView cust_listView;
    private TextView customer_name,address,mobile, empty_text;

    //Non UI variables
    private FirebaseDatabase database;
    private DatabaseReference databaseReference,databaseReference_customers;
    private String TAG = "Customer_Account_SearchActivity";
    private int year, month, day,to_year,to_month,to_day;
    public String from_date_string,to_date_string,mobileNo,FileName="";
    private Calendar calander;
    static final int FROM_DATE_DIALOG = 1;
    static final int TO_DATE_DIALOG = 2;
    int cur = 0;
    private DatePicker datePicker;
    private enum SelectedActionButton{GENERATE,EXPORT,EXPORT_MAIL};
    private Customer_Account_SearchActivity.SelectedActionButton selectedActionButton;
    private ArrayList<Orders_POJO> orders_pojosAL;
    private Orders_POJO orders_pojo;
    private ObjectAnimator textColorAnim2;
    private Customer_ListAdapter customer_listAdapter;
    private ArrayList<Customer_POJO> customer_pojoArrayList;
    private int width,height;
    ArrayList<String> dateAl,dateAL2;
    private Double credit,debit,total_outstanding=0.0;
    String total_outstanding_str;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_customer_account_search);
        initialization();
        generateEvents();
        setDefaultDate();
    }

    private void select_customer_dialog() {

        LayoutInflater layoutInflater = LayoutInflater.from(Customer_Account_SearchActivity.this);
        view = layoutInflater.inflate(R.layout.customer_search_dialog, null);
        selectStudentDialog = new Dialog(Customer_Account_SearchActivity.this, android.R.style.Theme_DeviceDefault_Dialog_NoActionBar);

//        selectStudentDialog.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);


        selectStudentDialog.setCancelable(false);
        selectStudentDialog.setContentView(view);
        selectStudentDialog.getWindow().getAttributes().windowAnimations = R.anim.intent_in_slide_out_left; //style id

        selectStudentDialog.show();
        initializeDialog(view);
    }

    private void initializeDialog(View view) {
        editText_search = (EditText) view.findViewById(R.id.editText_search);
        button_close = (Button) view.findViewById(R.id.button_close);
        cust_listView = (ListView) view.findViewById(R.id.listView_customerName);
        customer_name = (TextView) view.findViewById(R.id.customer_name);
        address = (TextView) view.findViewById(R.id.address);
        mobile = (TextView) view.findViewById(R.id.mobile);
        customer_pojoArrayList = new ArrayList<Customer_POJO>();
        height = getResources().getDisplayMetrics().heightPixels;
        width = getResources().getDisplayMetrics().widthPixels;
         empty_text = findViewById(R.id.empty_text);
        customer_name.setWidth((int) (width/3.8));
        address.setWidth((int) (width/3.2));
        mobile.setWidth((int) (width/2));

        button_close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectStudentDialog.dismiss();
            }
        });
        cust_listView.setOnItemClickListener(Customer_Account_SearchActivity.this);
        editText_search.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if(s.toString().isEmpty()||editText_search.getText().toString().isEmpty()){
                    customer_listAdapter = new Customer_ListAdapter(Customer_Account_SearchActivity.this, Customer_Account_SearchActivity.this, customer_pojoArrayList);
                    cust_listView.setAdapter(customer_listAdapter);
                    customer_listAdapter.notifyDataSetChanged();


                }
                if(customer_listAdapter!=null){
                    customer_listAdapter.getFilter().filter(s.toString());
                }
                else {
                    empty_text.setVisibility(View.VISIBLE);
                }
            }
        });
        setCustomerList();

    }

    private void setCustomerList() {
        customer_pojoArrayList.clear();
        databaseReference_customers.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                if (dataSnapshot.getValue() != null && !dataSnapshot.getValue().equals("")) {

                    for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {

                        Customer_POJO pojo = postSnapshot.getValue(Customer_POJO.class);
                        customer_pojoArrayList.add(pojo);
//                        Log.d(TAG,"Aman Chk DATA =="+dataSnapshot);

                        customer_listAdapter = new Customer_ListAdapter(Customer_Account_SearchActivity.this, Customer_Account_SearchActivity.this, customer_pojoArrayList);
                        cust_listView.setAdapter(customer_listAdapter);
                        customer_listAdapter.notifyDataSetChanged();

                    }
                }

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

        if(customer_listAdapter!=null){
//            Log.d(TAG,"AMAN position=="+customer_pojoArrayList.get(position).customer_name);
            selectStudentDialog.dismiss();
            customer_name_search.setText(customer_pojoArrayList.get(position).customer_name);

        }
    }

    private void setDefaultDate() {

        //FROM date logic below
        Calendar calander = Calendar.getInstance();
        calander.add(Calendar.DATE,0);
        calander.add(Calendar.MONTH,-1);
        Date date = new Date(calander.getTimeInMillis());
        java.text.SimpleDateFormat formatter = null;
        formatter = new java.text.SimpleDateFormat(AppUtills.DATE_FORMAT_DDMMMYYYY);
        from_date_string = formatter.format(date);

        year = calander.get(Calendar.YEAR);
        month = (calander.get(Calendar.MONTH));
        day = calander.get(Calendar.DAY_OF_MONTH);

        to_year = calander.get(Calendar.YEAR);
        to_month = (calander.get(Calendar.MONTH));
        to_day = calander.get(Calendar.DAY_OF_MONTH);

//        from_date_format=AppUtils.addPrefixBeforeDateNumber(Defaultdateyear)+"-"+AppUtils.addPrefixBeforeDateNumber(Defaultdatemonth)+"-"+AppUtils.addPrefixBeforeDateNumber(Defaultdateday);

        from_date.setText(from_date_string);

        //TO date logic below
        Calendar calendarForToDate=Calendar.getInstance();
        calendarForToDate.add(Calendar.DATE,0);
        Date date2 = new Date(calendarForToDate.getTimeInMillis());
        java.text.SimpleDateFormat formatter2 = null;
        formatter2 = new java.text.SimpleDateFormat(AppUtills.DATE_FORMAT_DDMMMYYYY);
        to_date_string = formatter2.format(date2);

        to_date.setText(to_date_string);
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

    private void initialization() {
        getSupportActionBar().setTitle(R.string.customer_account_statement);
        database = FirebaseInstance.getInstance();
        databaseReference_customers=database.getReference(getString(R.string.app_name)).child(AppUtills.getUserName(Customer_Account_SearchActivity.this)).
                child(getString(R.string.customer_page));
        from_date = (EditText) findViewById(R.id.from_date);
        to_date = (EditText) findViewById(R.id.to_date);
        btn_generate = (Button) findViewById(R.id.btn_generate);
        btn_export = (Button) findViewById(R.id.btn_export);
        btn_export_mail = (Button) findViewById(R.id.btn_export_mail);
        from_date_icon = (ImageView) findViewById(R.id.from_date_icon);
        to_date_icon = (ImageView) findViewById(R.id.to_date_icon);
        customer_name_search = (EditText) findViewById(R.id.customer_name_search);
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);//to disable keyboard

        textColorAnim2 = ObjectAnimator.ofInt(btn_generate, "textColor", getResources().getColor(R.color.text_shadow_white),getResources().getColor(R.color.red_ToastColor));
        textColorAnim2.setDuration(800);
        textColorAnim2.setEvaluator(new ArgbEvaluator());
        textColorAnim2.setRepeatCount(ValueAnimator.INFINITE);
        textColorAnim2.setRepeatMode(ValueAnimator.REVERSE);
        textColorAnim2.start();

        orders_pojosAL = new ArrayList<Orders_POJO>();
        dateAl = new ArrayList<>();
        dateAL2 = new ArrayList<>();


    }

    private void generateEvents() {
        btn_export_mail.setOnClickListener(this);
        btn_export.setOnClickListener(this);
        btn_generate.setOnClickListener(this);
        from_date.setOnClickListener(this);
        to_date.setOnClickListener(this);
        from_date_icon.setOnClickListener(this);
        to_date_icon.setOnClickListener(this);
        customer_name_search.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.from_date_icon:
                showDialog(FROM_DATE_DIALOG);
                break;
            case R.id.to_date_icon:
                showDialog(TO_DATE_DIALOG);
                break;
            case R.id.from_date:
                showDialog(FROM_DATE_DIALOG);
                break;
            case R.id.to_date:
                showDialog(TO_DATE_DIALOG);
                break;
            case R.id.btn_generate:
                if(customer_name_search.getText().toString().trim().isEmpty()||customer_name_search.getText().toString().equals("")){
                    customer_name_search.setError(getString(R.string.cannot_be_empty));
                    customer_name_search.requestFocus();
                }
                else {
                    selectedActionButton = Customer_Account_SearchActivity.SelectedActionButton.GENERATE;
                    Event_Listners2();
                }
//                new GenerateAsync().execute();
                break;
            case R.id.btn_export:
                if(customer_name_search.getText().toString().trim().isEmpty()||customer_name_search.getText().toString().equals("")){
                    customer_name_search.setError(getString(R.string.cannot_be_empty));
                    customer_name_search.requestFocus();
                }
                else{
                selectedActionButton = Customer_Account_SearchActivity.SelectedActionButton.EXPORT;
                Event_Listners2();
                }
//                new GenerateAsync().execute();
                break;
            case R.id.btn_export_mail:
                if(customer_name_search.getText().toString().trim().isEmpty()||customer_name_search.getText().toString().equals("")){
                    customer_name_search.setError(getString(R.string.cannot_be_empty));
                    customer_name_search.requestFocus();
                }
                else {
                    selectedActionButton = Customer_Account_SearchActivity.SelectedActionButton.EXPORT_MAIL;
                    Event_Listners2();
                }
                break;
            case R.id.customer_name_search:
                select_customer_dialog();
                break;
            default:
                break;
        }

    }

    private void Event_Listners2() {
//        fetch_mobileNo();
        databaseReference = database.getReference(getString(R.string.app_name)).child(AppUtills.getUserName(Customer_Account_SearchActivity.this)).
                child(getString(R.string.orders_page));
//        orders_pojosAL.clear();
        final AlertDialog progressDialog = new SpotsDialog(Customer_Account_SearchActivity.this);
        progressDialog.show();

        if (AppUtills.isNetworkAvailable(Customer_Account_SearchActivity.this)) {
            databaseReference.addValueEventListener(new ValueEventListener() {
                @SuppressLint("LongLogTag")
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {

//                    cans_with_custAL.clear();
                    JSONArray jsonElements = null;
                    try {
                        if (dataSnapshot.getValue() != null && !dataSnapshot.getValue().equals("")) {
                            orders_pojosAL.clear();
                            Gson gson = new Gson();
                            String json = gson.toJson(dataSnapshot.getValue());
                            JSONObject jsonObject = new JSONObject(json.toString());
//                            Log.d(TAG, "Aman Chk DATA jsonObject==" + jsonObject);
                            Iterator iterator = jsonObject.keys();
//                                customer_pojoArrayList.clear();
                            while (iterator.hasNext()) {
                                String key = (String) iterator.next();
//                                Log.d(TAG, "Aman Chk DATA key==" + key);
                                JSONObject jsonObjectChild = jsonObject.getJSONObject(key);
//                                Log.d(TAG, "Aman Chk DATA==" + dataSnapshot);

                                // JSONObject jsonObjectChild1 = jsonObject.getJSONObject(key);

                                Iterator iterator1 = jsonObjectChild.keys();
                                while (iterator1.hasNext()) {
                                    String key2 = (String) iterator1.next();
//                                    Log.d(TAG, "Aman Chk DATA Key2==" + key2);
                                    JSONObject jsonObjectChild1 = jsonObjectChild.getJSONObject(key2);
//                                    Log.d(TAG, "Aman Chk DATA jsonObjectChild1==" + jsonObjectChild1);

                                    SimpleDateFormat formatter = new SimpleDateFormat(AppUtills.DATE_FORMAT_DDMMMYYYY);
                                    String str1 = from_date_string;
                                    Date from_date = formatter.parse(str1);

                                    String str2 = jsonObjectChild1.optString("order_date");
                                    Date order_date = formatter.parse(str2);

                                    String str3 = to_date_string;
                                    Date to_date = formatter.parse(str3);

                                    if (order_date.after(from_date) && order_date.before(to_date)) {
                                        if (customer_name_search.getText().toString().equals(jsonObjectChild1.optString("customer_name"))) {
                                            orders_pojo = new Orders_POJO(
                                                    jsonObjectChild1.optInt("order_id"),
                                                    jsonObjectChild1.optString("cans_with_customer"),
                                                    jsonObjectChild1.optString("total_amount"),
                                                    jsonObjectChild1.optString("balance"),
                                                    jsonObjectChild1.optString("cans_return"),
                                                    jsonObjectChild1.optString("new_cans_given"),
                                                    jsonObjectChild1.optString("money_received"),
                                                    jsonObjectChild1.optString("deposit"),
                                                    jsonObjectChild1.optString("return_date"),
                                                    jsonObjectChild1.optString("customer_name"), key,
                                                    jsonObjectChild1.optString("order_date"),
                                                    jsonObjectChild1.optString("mobile"));

                                            orders_pojosAL.add(orders_pojo);
//                                            orders_pojosAL.add(pair.);
//                                            Log.d(TAG,"Aman chk map=="+(Orders_POJO) order_map.values());
                                        }
                                    }
                                    else if (order_date.equals(from_date) && order_date.before(to_date)) {
                                        if (customer_name_search.getText().toString().equals(jsonObjectChild1.optString("customer_name"))) {
                                            orders_pojo = new Orders_POJO(
                                                    jsonObjectChild1.optInt("order_id"),
                                                    jsonObjectChild1.optString("cans_with_customer"),
                                                    jsonObjectChild1.optString("total_amount"),
                                                    jsonObjectChild1.optString("balance"),
                                                    jsonObjectChild1.optString("cans_return"),
                                                    jsonObjectChild1.optString("new_cans_given"),
                                                    jsonObjectChild1.optString("money_received"),
                                                    jsonObjectChild1.optString("deposit"),
                                                    jsonObjectChild1.optString("return_date"),
                                                    jsonObjectChild1.optString("customer_name"), key,
                                                    jsonObjectChild1.optString("order_date"),
                                                    jsonObjectChild1.optString("mobile"));

                                            orders_pojosAL.add(orders_pojo);
//                                            orders_pojosAL.add(pair.);
//                                            Log.d(TAG,"Aman chk map=="+(Orders_POJO) order_map.values());
                                        }
                                    } else if (order_date.after(from_date) && order_date.equals(to_date)) {
                                        if (customer_name_search.getText().toString().equals(jsonObjectChild1.optString("customer_name"))) {
                                            orders_pojo = new Orders_POJO(
                                                    jsonObjectChild1.optInt("order_id"),
                                                    jsonObjectChild1.optString("cans_with_customer"),
                                                    jsonObjectChild1.optString("total_amount"),
                                                    jsonObjectChild1.optString("balance"),
                                                    jsonObjectChild1.optString("cans_return"),
                                                    jsonObjectChild1.optString("new_cans_given"),
                                                    jsonObjectChild1.optString("money_received"),
                                                    jsonObjectChild1.optString("deposit"),
                                                    jsonObjectChild1.optString("return_date"),
                                                    jsonObjectChild1.optString("customer_name"), key,
                                                    jsonObjectChild1.optString("order_date"),
                                                    jsonObjectChild1.optString("mobile"));

                                            orders_pojosAL.add(orders_pojo);
//                                            orders_pojosAL.add(pair.);
//                                            Log.d(TAG,"Aman chk map=="+(Orders_POJO) order_map.values());
                                        }
                                    } else if (order_date.equals(from_date) && order_date.equals(to_date)) {
                                        if (customer_name_search.getText().toString().equals(jsonObjectChild1.optString("customer_name"))) {
                                            orders_pojo = new Orders_POJO(
                                                    jsonObjectChild1.optInt("order_id"),
                                                    jsonObjectChild1.optString("cans_with_customer"),
                                                    jsonObjectChild1.optString("total_amount"),
                                                    jsonObjectChild1.optString("balance"),
                                                    jsonObjectChild1.optString("cans_return"),
                                                    jsonObjectChild1.optString("new_cans_given"),
                                                    jsonObjectChild1.optString("money_received"),
                                                    jsonObjectChild1.optString("deposit"),
                                                    jsonObjectChild1.optString("return_date"),
                                                    jsonObjectChild1.optString("customer_name"), key,
                                                    jsonObjectChild1.optString("order_date"),
                                                    jsonObjectChild1.optString("mobile"));

                                            orders_pojosAL.add(orders_pojo);
//                                            orders_pojosAL.add(pair.);
//                                            Log.d(TAG,"Aman chk map=="+(Orders_POJO) order_map.values());
                                        }
                                    }

                                }
//                                Log.d(TAG, "Aman Chk MAP==" + orders_pojosAL.size());
                            }
                            /**
                             * Below Hash Map is for credit amount /sorted date wise
                             *
                             * **/

                            HashMap<String, ArrayList<Double>> credit_map=new HashMap<String, ArrayList<Double>>();
                            credit_map.clear();
                            for (int i=0; i<orders_pojosAL.size();i++) {
//                                if(credit_map.containsValue(orders_pojosAL.get(i).money_received)) {
                                    if (!credit_map.containsKey(orders_pojosAL.get(i).order_date)) {
                                        ArrayList<Double> creditAL = new ArrayList<Double>();
//                                        ArrayList<Double> debitAl = new ArrayList<Double>();

                                        creditAL.clear();
//                                        debitAl.clear();
                                        creditAL.add(Double.parseDouble(orders_pojosAL.get(i).money_received));
//                                        debitAl.add(Double.parseDouble(orders_pojosAL.get(i).total_amount));

                                        credit_map.put(orders_pojosAL.get(i).order_date, creditAL);//FOR credit AMOUNT
//                                        credit_map.put(orders_pojosAL.get(i).order_date, debitAl);
//                                    Log.d(TAG, "Aman Chk MAP==" + order_map.size());

//
                                    } else {
                                        ArrayList<Double> creditAL = credit_map.get(orders_pojosAL.get(i).order_date);
//                                        ArrayList<Double> debitAl = credit_map.get(orders_pojosAL.get(i).order_date);

                                        creditAL.add(Double.valueOf(orders_pojosAL.get(i).money_received));//FOR CREDIT AMOUNT
//                                        debitAl.add(Double.parseDouble(orders_pojosAL.get(i).total_amount));

                                        credit_map.put(orders_pojosAL.get(i).order_date, creditAL);//FOR credit AMOUNT
//                                        credit_map.put(orders_pojosAL.get(i).order_date, debitAl);//FOR credit AMOUNT
//                                    Log.d(TAG, "Aman Chk MAP==" + credit_map);

                                    }
//                                }
                                /*else if(credit_map.containsValue(orders_pojosAL.get(i).total_amount)){
                                    if (!credit_map.containsKey(orders_pojosAL.get(i).order_date)) {
                                        ArrayList<Double> debitAl = new ArrayList<Double>();

                                        debitAl.clear();
                                        debitAl.add(Double.parseDouble(orders_pojosAL.get(i).total_amount));

                                        credit_map.put(orders_pojosAL.get(i).order_date, debitAl);
//                                    Log.d(TAG, "Aman Chk MAP==" + order_map.size());

//
                                    } else {
                                        ArrayList<Double> debitAl = credit_map.get(orders_pojosAL.get(i).order_date);

                                        debitAl.add(Double.parseDouble(orders_pojosAL.get(i).total_amount));

                                        credit_map.put(orders_pojosAL.get(i).order_date, debitAl);//FOR credit AMOUNT
//                                    Log.d(TAG, "Aman Chk MAP==" + credit_map);

                                    }
                                }*/

                            }
                            HashMap<String, ArrayList<Double>> debit_map=new HashMap<String, ArrayList<Double>>();
                            debit_map.clear();
                            for (int i=0; i<orders_pojosAL.size();i++) {
                                if (!debit_map.containsKey(orders_pojosAL.get(i).order_date)) {
                                    ArrayList<Double> debitAL = new ArrayList<Double>();

                                    debitAL.clear();
                                    debitAL.add(Double.parseDouble(orders_pojosAL.get(i).total_amount));

                                    debit_map.put(orders_pojosAL.get(i).order_date,debitAL);//FOR debit AMOUNT

//
                                } else {
                                    ArrayList<Double> debitAL = debit_map.get(orders_pojosAL.get(i).order_date);

                                    debitAL.add(Double.parseDouble(orders_pojosAL.get(i).total_amount));//FOR DEBIT AMOUNT

                                    debit_map.put(orders_pojosAL.get(i).order_date,debitAL);//FOR debit AMOUNT
                                }

                            }

                            Iterator myIterator1 = credit_map.keySet().iterator();
                            Iterator myIterator2 = debit_map.keySet().iterator();
//                            Log.i(TAG, "Aman order_map "+order_map);
                            while(myIterator1.hasNext()) {
                                String key=(String) myIterator1.next();
                                dateAl.add(key);
//                                Log.d(TAG, "Aman Chk dateAl==" + dateAl.size());

                            } // while(myVeryOwnIterator.hasNext()) Closes here...

                            dateAl.clear();
                            while (myIterator2.hasNext()){
                                String key2 = (String) myIterator2.next();
                                dateAl.add(key2);
                            }
                            Collections.sort(dateAl);
                            orders_pojosAL.clear();

                            for (int k=0;k<dateAl.size(); k++) {
                                Orders_POJO orders_pojo = new Orders_POJO();
                                orders_pojo.order_date = dateAl.get(k);

//                                date22 = dateAl.get(k);
                                ArrayList<Double> value = credit_map.get(dateAl.get(k));
                                ArrayList<Double> value2 = debit_map.get(dateAl.get(k));

//                                Log.d(TAG, "Aman Chk MAPPP CREDIT==" + value);
                                credit = 0.0;
                                debit = 0.0;
                                for (int j = 0; j < value.size(); j++) {
                                    credit += value.get(j);
//                                    debit+=value.get(j);
                                    Log.d(TAG, "Aman Chk CREDIT==" + credit);
//                                    Log.d(TAG, "Aman Chk DEBIT==" + debit);
                                }
                                for(int h=0; h< value2.size(); h++){
                                    debit +=value2.get(h);
                                    Log.d(TAG, "Aman Chk DEBIT==" + debit);
                                }
                                orders_pojo.money_received = String.valueOf(credit);
                                orders_pojo.total_amount = String.valueOf(debit);
                                orders_pojosAL.add(orders_pojo);
                            }

                                /**
                                 * Below Hash Map is for DEBIT amount /sorted date wise
                                 *
                                 * **/

                                /*Iterator myIterator2 = debit_map.keySet().iterator();
//                            Log.i(TAG, "Aman order_map "+order_map);
                                while(myIterator2.hasNext()) {
                                    String key=(String) myIterator2.next();
                                    dateAL2.add(key);

                                } // while(myVeryOwnIterator.hasNext()) Closes here...
                                Collections.sort(dateAL2);
                                orders_pojosAL.clear();

                                for (int d=0;d<dateAL2.size(); d++){
                                    Orders_POJO orders_pojo2=new Orders_POJO();
                                    orders_pojo2.order_date=dateAL2.get(d);

                                    ArrayList<Double> value_debit=debit_map.get(dateAL2.get(d));
                                Log.d(TAG, "Aman Chk MAPPP DEBIT==" + value_debit);
                                    debit = 0.0;
                                    for (int j=0; j<value_debit.size(); j++){
                                        debit+= value_debit.get(j);
//                                    Log.d(TAG, "Aman Chk CREDIT==" + credit);
                                    }
                                    orders_pojo.total_amount= String.valueOf(debit);
                                    orders_pojosAL.add(orders_pojo);


                            }*/

                            for (int i = 0; i < orders_pojosAL.size(); i++) {

                                Double cr = 0.0,dr=0.0;

                                cr = cr+Double.parseDouble(orders_pojosAL.get(i).money_received);
                                dr = dr+Double.parseDouble(orders_pojosAL.get(i).total_amount);
                                total_outstanding = Double.valueOf(dr-cr);
                                total_outstanding_str = String.valueOf(total_outstanding);


                            }
                            Log.d(TAG,"Aman chk total_outstanding"+total_outstanding);
                            if(!orders_pojosAL.isEmpty()) {
                                switch (selectedActionButton) {
                                    case GENERATE:
                                        Intent intent = new Intent(Customer_Account_SearchActivity.this, Customer_Account_ListActivity.class);
                                        intent.putParcelableArrayListExtra("orderAl", orders_pojosAL);
                                        intent.putExtra("total_outstanding",total_outstanding_str);
                                        intent.putExtra("cust_name", customer_name_search.getText().toString());
                                        intent.putExtra("from_date", from_date_string);
                                        intent.putExtra("to_date", to_date_string);
                                        startActivity(intent);
                                        AppUtills.giveIntentEffect(Customer_Account_SearchActivity.this);
                                        break;


                                    case EXPORT:
                                        CsvBuilder(orders_pojosAL);
                                        Snackbar.make(findViewById(android.R.id.content), "Report Exported Successfully", Snackbar.LENGTH_LONG)
                                                .setActionTextColor(getResources().getColor(R.color.red_ToastColor))
                                                .show();
                                        break;

                                    case EXPORT_MAIL:
                                        CsvBuilder(orders_pojosAL);
                                        AppUtills.sendMail(FileName + ".Csv", Customer_Account_SearchActivity.this, AppUtills.Email, FileName);

                                        break;

                                    default:
                                        break;
                                }
                            }
                            else
                                Snackbar.make(findViewById(android.R.id.content), "No Reports found!!", Snackbar.LENGTH_LONG)
                                        .setActionTextColor(getResources().getColor(R.color.red_ToastColor))
                                        .show();

//                                new GenerateAsync().execute();
                        }

                    } catch (Exception e) {
                        e.printStackTrace();
                    } finally {
                        try {
                            progressDialog.dismiss();
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }

                private void CsvBuilder(ArrayList<Orders_POJO> orders_pojosAL) {

                    ArrayList<String[]>CsvArray=new ArrayList<String[]>();
//            String monthString = new DateFormatSymbols().getMonths()[Defaultdatemonth];

                    if (orders_pojosAL!=null) {


                        if (orders_pojosAL.size()!=0) {



                            String[] Enter={""};


                            String[]csvDataHeadCommonReportName={getResources().getString(R.string.customer_account_statement)+"-"+from_date_string.toString(),to_date_string.toString()};
                            CsvArray.add(csvDataHeadCommonReportName);

                            String[]csvDate={"From Date",from_date_string.toString(),"To Date",to_date_string.toString()};
                            CsvArray.add(csvDate);

                            CsvArray.add(Enter);

                            String[]csvHeader={

                                    getResources().getString(R.string.customer_name_spelling),
                                    getResources().getString(R.string.date_spelling),
                                    getResources().getString(R.string.credit_spelling),
                                    getResources().getString(R.string.debit_spelling),
                                    getResources().getString(R.string.total_outstanding),
                            };
                            CsvArray.add(csvHeader);


                            for (int i = 0; i < orders_pojosAL.size(); i++) {

                                String[]csvData={

                                        AppUtills.capitalizeFirstLetter(orders_pojosAL.get(i).customer_name),
                                        to_date_string.toString(),
                                        orders_pojosAL.get(i).mobile.toString(),
                                        orders_pojosAL.get(i).cans_with_customer.toString(),


                                };

                                CsvArray.add(csvData);
                            }

                            FileName=getString(R.string.customer_account_statement)+"-"+from_date_string+"To"+to_date_string;

                            AppUtills.csvFileCreate(CsvArray, "WiseBill/reportexport", FileName);
                        }

                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });
        } else {
            Snackbar.make(findViewById(android.R.id.content), getResources().getString(R.string.CheckYourInternetConnection), Snackbar.LENGTH_LONG)
                    .setActionTextColor(getResources().getColor(R.color.red_ToastColor))
                    .show();
        }

    }


    @Override
    protected Dialog onCreateDialog(int id) {
        switch (id) {

            case FROM_DATE_DIALOG:
//                Log.d(TAG,"Aman check FROM_DATE_DIALOG="+id);
                cur = FROM_DATE_DIALOG;
                // set date picker as current date
                return new DatePickerDialog(this, datePickerListener, year, month, day);
            case TO_DATE_DIALOG:
                cur = TO_DATE_DIALOG;
//                Log.d(TAG,"Aman check TO_DATE_DIALOG="+id);
                // set date picker as current date

                return new DatePickerDialog(this, datePickerListener, to_year, to_month+1, to_day);

        }
        return null;
    }

    private DatePickerDialog.OnDateSetListener datePickerListener = new DatePickerDialog.OnDateSetListener() {

        // when dialog box is closed, below method will be called.
        public void onDateSet(DatePicker view, int selectedYear, int selectedMonth, int selectedDay) {





            if(cur == FROM_DATE_DIALOG){
                // set selected date into edittext
                year = selectedYear;
                month = selectedMonth;
                day = selectedDay;

                String date1;
                date1= day + "-" + (month + 1) + "-" + year;
                from_date_string = AppUtills.dateFormatConverter(date1, AppUtills.DATE_FORMAT_DDMMYYYY, AppUtills.DATE_FORMAT_DDMMMYYYY);
                from_date.setText(from_date_string);
            }
            else{
                to_year = selectedYear;
                to_month = selectedMonth;
                to_day = selectedDay;

                String date2;
                date2= to_day + "-" + (to_month + 1) + "-" + to_year;
                to_date_string = AppUtills.dateFormatConverter(date2, AppUtills.DATE_FORMAT_DDMMYYYY, AppUtills.DATE_FORMAT_DDMMMYYYY);
                to_date.setText(to_date_string);
            }

        }
    };

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        AppUtills.givefinishEffect(this);
        finish();
    }
}
