package watersupplier.main.Reports;

import android.animation.ArgbEvaluator;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.firebase.client.core.utilities.ImmutableTree;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONObject;

import java.text.DateFormatSymbols;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;

import dmax.dialog.SpotsDialog;
import watersupplier.main.Adapter.Cans_in_Market_ListAdapter;
import watersupplier.main.Adapter.Order_ListAdapter;
import watersupplier.main.Firsebase_Operations.FirebaseInstance;
import watersupplier.main.Main.Common_ActionBar_Abstract;
import watersupplier.main.Orders.Order_Search_ListActivity;
import watersupplier.main.Pojo.Customer_POJO;
import watersupplier.main.Pojo.Orders_POJO;
import watersupplier.main.R;
import watersupplier.main.Utils.AppUtills;

/**
 * Created by Aman Gupta on 1/4/17.
 */

public class Cans_in_Market_SearchActivity extends Common_ActionBar_Abstract implements View.OnClickListener {

    //UI variable
    private EditText editText_date;
    private Button btn_generate,btn_export,btn_export_mail;
    private ImageView date_icon;

    //Non-UI variable
    private FirebaseDatabase database;
    private DatabaseReference databaseReference,databaseReference2;
    private String TAG = "Cans_in_Market_SearchActivity";
    private int year, month, day;
    public String display_date_format,customer_name1,FileName="";
    private Calendar calander;
    private enum SelectedActionButton{GENERATE,EXPORT,EXPORT_MAIL};
    private SelectedActionButton selectedActionButton;
    private ArrayList<Orders_POJO> orders_pojosAL;
    private Orders_POJO orders_pojo;
    private ArrayList<Customer_POJO> customer_pojoArrayList;
    private Customer_POJO customer_pojo;
    private ObjectAnimator textColorAnim2;
//    private ArrayList<String> cust_nameAL,cans_with_custAL;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cans_in_market_search);
        initialization();
        generateEvents();
//        screenWidth();

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
        getSupportActionBar().setTitle(R.string.cans_in_market);

        editText_date = (EditText) findViewById(R.id.editText_date);
        btn_generate = (Button) findViewById(R.id.btn_generate);
        btn_export = (Button) findViewById(R.id.btn_export);
        btn_export_mail = (Button) findViewById(R.id.btn_export_mail);
        date_icon = (ImageView) findViewById(R.id.date_icon);

        database = FirebaseInstance.getInstance();
        databaseReference=database.getReference(getString(R.string.app_name)).child(AppUtills.getUserName(Cans_in_Market_SearchActivity.this)).
                child(getString(R.string.customer_page));
       /* databaseReference2 = database.getReference(getString(R.string.app_name)).child(AppUtills.getUserName(Cans_in_Market_SearchActivity.this)).
                child(getString(R.string.orders_page)).child("Aman Gupta");*/

        textColorAnim2 = ObjectAnimator.ofInt(btn_generate, "textColor", getResources().getColor(R.color.text_shadow_white),
                getResources().getColor(R.color.red_ToastColor));
        textColorAnim2.setDuration(800);
        textColorAnim2.setEvaluator(new ArgbEvaluator());
        textColorAnim2.setRepeatCount(ValueAnimator.INFINITE);
        textColorAnim2.setRepeatMode(ValueAnimator.REVERSE);
        textColorAnim2.start();
        //date logic below
        calander = Calendar.getInstance();
        calander.add(Calendar.DATE,0);
        Date date = new Date(calander.getTimeInMillis());
        java.text.SimpleDateFormat formatter = null;
        formatter = new java.text.SimpleDateFormat(AppUtills.DATE_FORMAT_DDMMMYYYY);
        display_date_format = formatter.format(date);
        editText_date.setText(display_date_format);

        year = calander.get(Calendar.YEAR);
        month = (calander.get(Calendar.MONTH));
        day = calander.get(Calendar.DAY_OF_MONTH);

        orders_pojosAL = new ArrayList<Orders_POJO>();
        customer_pojoArrayList = new ArrayList<Customer_POJO>();
//        cust_nameAL = new ArrayList<String>();
//        cans_with_custAL = new ArrayList<String>();
//        Event_Listners();
    }

    private void generateEvents() {
        btn_export_mail.setOnClickListener(this);
        btn_export.setOnClickListener(this);
        btn_generate.setOnClickListener(this);
        editText_date.setOnClickListener(this);
        date_icon.setOnClickListener(this);
    }



    @Override
    public void onClick(View v) {

        switch (v.getId()){
            case R.id.date_icon:
                showDialog(999);
                break;
            case R.id.editText_date:
                showDialog(999);
                break;
            case R.id.btn_generate:
                selectedActionButton=SelectedActionButton.GENERATE;
                Event_Listners2();
//                new GenerateAsync().execute();
                break;
            case R.id.btn_export:
                selectedActionButton = SelectedActionButton.EXPORT;
                Event_Listners2();
//                new GenerateAsync().execute();
                break;
            case R.id.btn_export_mail:
                selectedActionButton = SelectedActionButton.EXPORT_MAIL;
                Event_Listners2();

                break;
            default:
                break;
        }

    }
    private class GenerateAsync extends AsyncTask<Void,Void,Void>{
//        final AlertDialog progressDialog = new SpotsDialog(Cans_in_Market_SearchActivity.this);
        ProgressDialog progressDialog;
        private String TAG = "GenerateAsync";

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
//            progressDialog.setIcon(R.drawable.search);
//            progressDialog.show();
            progressDialog=new ProgressDialog(Cans_in_Market_SearchActivity.this);
            progressDialog.setMessage("Please wait"+"...");
            progressDialog.setCancelable(false);
//            progressDialog.show();
        }

        @Override
        protected Void doInBackground(Void... params) {

            switch (selectedActionButton) {
                case EXPORT:
                    CsvBuilder(orders_pojosAL);
                    break;

                case EXPORT_MAIL:
                    CsvBuilder(orders_pojosAL);

                    break;


                default:
                    break;
            }
            return null;
        }

        private void CsvBuilder(ArrayList<Orders_POJO> orders_pojosAL) {

            ArrayList<String[]>CsvArray=new ArrayList<String[]>();
//            String monthString = new DateFormatSymbols().getMonths()[Defaultdatemonth];

            if (orders_pojosAL!=null) {


                if (orders_pojosAL.size()!=0) {



                    String[] Enter={""};


                    String[]csvDataHeadCommonReportName={"Cans in Market Report "+display_date_format};
                    CsvArray.add(csvDataHeadCommonReportName);
                    /*String[]csvDataMonth={FromDateEdt.getText().toString()};
                    CsvArray.add(csvDataMonth);*/




                    CsvArray.add(Enter);

                    String[]csvHeader={

                            getResources().getString(R.string.customer_name_spelling),
                            getResources().getString(R.string.issue_date_spelling),
                            getResources().getString(R.string.quantity_spelling),
                    };
                    CsvArray.add(csvHeader);


                    for (int i = 0; i < orders_pojosAL.size(); i++) {

                        String[]csvData={

                                AppUtills.capitalizeFirstLetter(orders_pojosAL.get(i).customer_name),
                                orders_pojosAL.get(i).order_date,
                                orders_pojosAL.get(i).cans_with_customer.toString(),


                        };

                        CsvArray.add(csvData);
                    }

                    FileName="Cans in Market Report -"+display_date_format;

                    AppUtills.csvFileCreate(CsvArray, "WiseBill/reportexport", FileName);
                }

            }
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            progressDialog.cancel();
//            Log.d(TAG, "Aman Chk AL==" + orders_pojosAL);
           /* if (orders_pojosAL.size()!=0)
            {
                *//*switch (selectedActionButton) {
                    case GENERATE:
                        Intent intent =new Intent(Cans_in_Market_SearchActivity.this,Cans_in_Market_ListActivity.class);
                        intent.putParcelableArrayListExtra("orderAl",orders_pojosAL);
                        intent.putExtra("selected_date",display_date_format);
//                        Log.d(TAG, "Aman Chk selected_date==" +display_date_format);
                        startActivity(intent);
                        AppUtills.giveIntentEffect(Cans_in_Market_SearchActivity.this);
//                        finish();
                        break;


                    case EXPORT:
                        Snackbar.make(findViewById(android.R.id.content),  "Report Exported Successfully", Snackbar.LENGTH_LONG)
                                .setActionTextColor(getResources().getColor(R.color.red_ToastColor))
                                .show();
                        break;

                    case EXPORT_MAIL:

                        AppUtills.sendMail(FileName+".Csv",Cans_in_Market_SearchActivity.this,AppUtills.Email,FileName);

                        break;

                    default:
                        break;
                }*//*

            }else{
                Snackbar.make(findViewById(android.R.id.content), "No Reports found!!", Snackbar.LENGTH_LONG)
                        .setActionTextColor(getResources().getColor(R.color.red_ToastColor))
                        .show();
            }*/
            super.onPostExecute(aVoid);


        }
    }

    private void Event_Listners2() {
        databaseReference2 = database.getReference(getString(R.string.app_name)).child(AppUtills.getUserName(Cans_in_Market_SearchActivity.this)).
                child(getString(R.string.orders_page));
//        orders_pojosAL.clear();
        final AlertDialog progressDialog = new SpotsDialog(Cans_in_Market_SearchActivity.this);
        progressDialog.show();

        if (AppUtills.isNetworkAvailable(Cans_in_Market_SearchActivity.this)) {
            databaseReference2.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {

//                    cans_with_custAL.clear();
                    JSONArray jsonElements = null;
                    try {
                            if (dataSnapshot.getValue() != null && !dataSnapshot.getValue().equals("")) {

//                            orders_pojosAL.clear();
                                Gson gson = new Gson();
                                String json = gson.toJson(dataSnapshot.getValue());
                                JSONObject jsonObject = new JSONObject(json.toString());

                                Iterator iterator = jsonObject.keys();
                                while (iterator.hasNext()) {
                                    String key = (String) iterator.next();
                                    JSONObject jsonObjectChild = jsonObject.getJSONObject(key);


                                    Iterator iterator1 = jsonObjectChild.keys();
                                    while (iterator1.hasNext()) {
                                        String key2 = (String) iterator1.next();
//                                    Log.d(TAG, "Aman Chk DATA Key2==" + key2);
                                        JSONObject jsonObjectChild1 = jsonObjectChild.getJSONObject(key2);


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
//                                        cans_with_custAL.add(orders_pojo.cans_with_customer);

                                    }

                                }
                                    HashMap<String, Orders_POJO> order_map = new HashMap<String, Orders_POJO>();
                                    order_map.clear();
                                    for (int i = 0; i < orders_pojosAL.size(); i++) {
//                                    if(order/_map.containsKey(orders_pojosAL.get(i).customer_name)){
                                        order_map.put(orders_pojosAL.get(i).customer_name, orders_pojosAL.get(i));
//                                    }else {
//
//                                    }
                                    }
                                    orders_pojosAL.clear();

                                    Iterator it = order_map.entrySet().iterator();
                                    while (it.hasNext()) {
                                        HashMap.Entry pair = (HashMap.Entry) it.next();
//                                    System.out.println(pair.getKey() + " = " + pair.getValue());
                                        try{
                                            SimpleDateFormat formatter = new SimpleDateFormat(AppUtills.DATE_FORMAT_DDMMMYYYY);
                                            String str1 = display_date_format;
                                            Date from_date = formatter.parse(str1);

                                            String str2 = orders_pojo.order_date;
                                            Date order_date = formatter.parse(str2);

                                            if (order_date.compareTo(from_date)<0)
                                            {
                                                orders_pojosAL.add((Orders_POJO) pair.getValue());
//                                                Log.d(TAG, "Aman:== from_date is Greater than  order_date");

                                            }

//                                            else
//                                                Toast.makeText(Cans_in_Market_SearchActivity.this,"not found",Toast.LENGTH_SHORT).show();

                                        }catch (ParseException e1){
                                            e1.printStackTrace();
                                        }

//                                    it.remove(); // avoids a ConcurrentModificationException
                                    }

                                    if(!orders_pojosAL.isEmpty()) {
                                        switch (selectedActionButton) {
                                            case GENERATE:
                                                Intent intent = new Intent(Cans_in_Market_SearchActivity.this, Cans_in_Market_ListActivity.class);
                                                intent.putParcelableArrayListExtra("orderAl", orders_pojosAL);
                                                intent.putExtra("selected_date", display_date_format);
                                                startActivity(intent);
//                                                Log.d(TAG,"Aman chk intent");
                                                AppUtills.giveIntentEffect(Cans_in_Market_SearchActivity.this);
                                                break;


                                            case EXPORT:
                                                Snackbar.make(findViewById(android.R.id.content), "Report Exported Successfully", Snackbar.LENGTH_LONG)
                                                        .setActionTextColor(getResources().getColor(R.color.red_ToastColor))
                                                        .show();
                                                break;

                                            case EXPORT_MAIL:

                                                AppUtills.sendMail(FileName + ".Csv", Cans_in_Market_SearchActivity.this, AppUtills.Email, FileName);

                                                break;

                                            default:
                                                break;
                                        }
                                        new GenerateAsync().execute();
                                    }
                                    else
                                        Snackbar.make(findViewById(android.R.id.content), "No Reports found!!", Snackbar.LENGTH_LONG)
                                                .setActionTextColor(getResources().getColor(R.color.red_ToastColor))
                                                .show();


                            }

                    }

                    catch (Exception e) {
                        e.printStackTrace();
                    } finally {
                        try {
                            progressDialog.dismiss();
                        } catch (Exception e) {
                            e.printStackTrace();
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
    @Deprecated
    protected Dialog onCreateDialog(int id) {
        return new DatePickerDialog(this, datePickerListener, year, month, day);
    }

    private DatePickerDialog.OnDateSetListener datePickerListener = new DatePickerDialog.OnDateSetListener() {
        public void onDateSet(DatePicker view, int selectedYear, int selectedMonth, int selectedDay) {
            String date1 = "";
            date1 = selectedDay + "-" + (selectedMonth + 1) + "-" + selectedYear;
            display_date_format = AppUtills.dateFormatConverter(date1, AppUtills.DATE_FORMAT_DDMMYYYY, AppUtills.DATE_FORMAT_DDMMMYYYY);

            editText_date.setText(AppUtills.dateFormatConverter(date1, AppUtills.DATE_FORMAT_DDMMYYYY, AppUtills.DATE_FORMAT_DDMMMYYYY));

        }
    };
    @Override
    public void onBackPressed() {
        super.onBackPressed();
        AppUtills.givefinishEffect(this);
        finish();
    }

}
