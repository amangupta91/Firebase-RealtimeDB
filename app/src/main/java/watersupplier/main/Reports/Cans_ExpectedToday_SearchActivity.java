package watersupplier.main.Reports;

import android.animation.ArgbEvaluator;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Color;
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
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;

import dmax.dialog.SpotsDialog;
import watersupplier.main.Customer.Customer_List_Activity;
import watersupplier.main.Firsebase_Operations.FirebaseInstance;
import watersupplier.main.Main.Common_ActionBar_Abstract;
import watersupplier.main.Pojo.Customer_POJO;
import watersupplier.main.Pojo.Orders_POJO;
import watersupplier.main.Pojo.Product_POJO;
import watersupplier.main.R;
import watersupplier.main.Utils.AppUtills;

/**
 * Created by Aman Gupta on 20/4/17.
 */

public class Cans_ExpectedToday_SearchActivity extends Common_ActionBar_Abstract implements View.OnClickListener{

    //UI variables
    private EditText editText_date;
    private Button btn_generate,btn_export,btn_export_mail;
    private ImageView date_icon;

    //Non-UI variables
    private ObjectAnimator textColorAnim1,textColorAnim2;
    private FirebaseDatabase database;
    private DatabaseReference databaseReference,databaseReference_cust;
    private String TAG = "Cans_ExpectedToday_SearchActivity";
    private int year, month, day;
    public String display_date_format,mobileNo,FileName="";
    private Calendar calander;
    private enum SelectedActionButton{GENERATE,EXPORT,EXPORT_MAIL};
    private Cans_ExpectedToday_SearchActivity.SelectedActionButton selectedActionButton;
    private ArrayList<Orders_POJO> orders_pojosAL;
    private Orders_POJO orders_pojo;
    private ArrayList<Customer_POJO> customer_pojoArrayList;
    private Customer_POJO customer_pojo;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cans_in_market_search);
        initialization();
        generateEvents();
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

    private void generateEvents() {
        btn_export_mail.setOnClickListener(this);
        btn_export.setOnClickListener(this);
        btn_generate.setOnClickListener(this);
        editText_date.setOnClickListener(this);
        date_icon.setOnClickListener(this);
    }

    private void initialization() {
        getSupportActionBar().setTitle(R.string.cans_expected_today);
        editText_date = (EditText) findViewById(R.id.editText_date);
        btn_generate = (Button) findViewById(R.id.btn_generate);
        btn_export = (Button) findViewById(R.id.btn_export);
        btn_export_mail = (Button) findViewById(R.id.btn_export_mail);
        date_icon = (ImageView) findViewById(R.id.date_icon);

        database = FirebaseInstance.getInstance();

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

        textColorAnim2 = ObjectAnimator.ofInt(btn_generate, "textColor", getResources().getColor(R.color.text_shadow_white),
                getResources().getColor(R.color.red_ToastColor));
        textColorAnim2.setDuration(800);
        textColorAnim2.setEvaluator(new ArgbEvaluator());
        textColorAnim2.setRepeatCount(ValueAnimator.INFINITE);
        textColorAnim2.setRepeatMode(ValueAnimator.REVERSE);
        textColorAnim2.start();
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
                selectedActionButton= Cans_ExpectedToday_SearchActivity.SelectedActionButton.GENERATE;
                Event_Listners2();
                Log.d(TAG, "Aman chk Al22==" + orders_pojosAL.size());
//                new GenerateAsync().execute();
                break;
            case R.id.btn_export:
                selectedActionButton = Cans_ExpectedToday_SearchActivity.SelectedActionButton.EXPORT;
                Event_Listners2();
                Log.d(TAG, "Aman chk Al2==" + orders_pojosAL.size());
//                new GenerateAsync().execute();
                break;
            case R.id.btn_export_mail:
                selectedActionButton = Cans_ExpectedToday_SearchActivity.SelectedActionButton.EXPORT_MAIL;
                Event_Listners2();

                break;
            default:
                break;
        }
    }

    private void Event_Listners2() {
//        fetch_mobileNo();
        databaseReference = database.getReference(getString(R.string.app_name)).child(AppUtills.getUserName(Cans_ExpectedToday_SearchActivity.this)).
                child(getString(R.string.orders_page));
//        orders_pojosAL.clear();
        final AlertDialog progressDialog = new SpotsDialog(Cans_ExpectedToday_SearchActivity.this);
        progressDialog.show();

        if (AppUtills.isNetworkAvailable(Cans_ExpectedToday_SearchActivity.this)) {
            databaseReference.addValueEventListener(new ValueEventListener() {
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

//                                        cans_with_custAL.add(orders_pojo.cans_with_customer);
                                    orders_pojosAL.add(orders_pojo);


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
                                    System.out.println(pair.getKey() + " = " + pair.getValue());
                                    try {
                                        SimpleDateFormat formatter = new SimpleDateFormat(AppUtills.DATE_FORMAT_DDMMMYYYY);
                                        String str1 = display_date_format;
                                        Date from_date = formatter.parse(str1);

                                        String str2 = orders_pojo.return_date;
                                        Date order_date = formatter.parse(str2);

                                        if (order_date.compareTo(from_date) == 0) {
//                                            orders_pojosAL.clear();
                                            orders_pojosAL.add((Orders_POJO) pair.getValue());
                                            Log.d(TAG, "Aman chk Al==" + orders_pojosAL.size());
//
                                        }
                                       /*else{
                                        Snackbar.make(findViewById(android.R.id.content), "No Reports found!!", Snackbar.LENGTH_LONG)
                                                .setActionTextColor(getResources().getColor(R.color.red_ToastColor))
                                                .show();}*/

                                    } catch (ParseException e1) {
                                        e1.printStackTrace();
                                    }

//                                    it.remove(); // avoids a ConcurrentModificationException
                                }


                                if (!orders_pojosAL.isEmpty()) {
                                    switch (selectedActionButton) {
                                        case GENERATE:
                                            Intent intent = new Intent(Cans_ExpectedToday_SearchActivity.this, Cans_ExpectedToday_ListActivity.class);
                                            intent.putParcelableArrayListExtra("orderAl", orders_pojosAL);
                                            intent.putExtra("selected_date", display_date_format);
                                            startActivity(intent);
                                            Log.d(TAG, "Aman chk intent");
                                            AppUtills.giveIntentEffect(Cans_ExpectedToday_SearchActivity.this);
                                            break;


                                        case EXPORT:
                                            CsvBuilder(orders_pojosAL);
                                            Snackbar.make(findViewById(android.R.id.content), "Report Exported Successfully", Snackbar.LENGTH_LONG)
                                                    .setActionTextColor(getResources().getColor(R.color.red_ToastColor))
                                                    .show();
                                            break;

                                        case EXPORT_MAIL:
                                            CsvBuilder(orders_pojosAL);
                                            AppUtills.sendMail(FileName + ".Csv", Cans_ExpectedToday_SearchActivity.this, AppUtills.Email, FileName);

                                            break;

                                        default:
                                            break;
                                    }
                                }

                               /* Snackbar.make(findViewById(android.R.id.content), "No Reports found!!", Snackbar.LENGTH_LONG)
                                        .setActionTextColor(getResources().getColor(R.color.red_ToastColor))
                                        .show();*/
//                                new GenerateAsync().execute();
                            }

                            if (orders_pojosAL.isEmpty()) {
                                Snackbar.make(findViewById(android.R.id.content), "No Reports found!!", Snackbar.LENGTH_LONG)
                                        .setActionTextColor(getResources().getColor(R.color.red_ToastColor))
                                        .show();
                            }


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


                            String[]csvDataHeadCommonReportName={"Cans Expected Today Report "+display_date_format};
                            CsvArray.add(csvDataHeadCommonReportName);
                    /*String[]csvDataMonth={FromDateEdt.getText().toString()};
                    CsvArray.add(csvDataMonth);*/




                            CsvArray.add(Enter);

                            String[]csvHeader={

                                    getResources().getString(R.string.customer_name_spelling),
                                    getResources().getString(R.string.mobile_spelling),
                                    getResources().getString(R.string.quantity_spelling),
                            };
                            CsvArray.add(csvHeader);


                            for (int i = 0; i < orders_pojosAL.size(); i++) {

                                String[]csvData={

                                        AppUtills.capitalizeFirstLetter(orders_pojosAL.get(i).customer_name),
                                        orders_pojosAL.get(i).mobile.toString(),
                                        orders_pojosAL.get(i).cans_with_customer.toString(),


                                };

                                CsvArray.add(csvData);
                            }

                            FileName="Cans Expected Today Report -"+display_date_format;

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

    private class GenerateAsync extends AsyncTask<Void,Void,Void>{
        ProgressDialog progressDialog;
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressDialog=new ProgressDialog(Cans_ExpectedToday_SearchActivity.this);
            progressDialog.setMessage("Please wait"+"...");
            progressDialog.setCancelable(false);
        }

        @Override
        protected Void doInBackground(Void... params) {
            switch (selectedActionButton) {

               /* case EXPORT:
                    Log.d(TAG, "Aman Chk pojo==" + orders_pojosAL.size());
                    CsvBuilder(orders_pojosAL);
                    break;

                case EXPORT_MAIL:
                    CsvBuilder(orders_pojosAL);

                    break;


                default:
                    break;*/
            }
            return null;
        }

        private void CsvBuilder(ArrayList<Orders_POJO> orders_pojosAL) {

            ArrayList<String[]>CsvArray=new ArrayList<String[]>();
//            String monthString = new DateFormatSymbols().getMonths()[Defaultdatemonth];

            if (orders_pojosAL!=null) {


                if (orders_pojosAL.size()!=0) {



                    String[] Enter={""};


                    String[]csvDataHeadCommonReportName={"Cans Expected Today Report "+display_date_format};
                    CsvArray.add(csvDataHeadCommonReportName);
                    /*String[]csvDataMonth={FromDateEdt.getText().toString()};
                    CsvArray.add(csvDataMonth);*/




                    CsvArray.add(Enter);

                    String[]csvHeader={

                            getResources().getString(R.string.customer_name_spelling),
                            getResources().getString(R.string.mobile_spelling),
                            getResources().getString(R.string.quantity_spelling),
                    };
                    CsvArray.add(csvHeader);


                    for (int i = 0; i < orders_pojosAL.size(); i++) {

                        String[]csvData={

                                AppUtills.capitalizeFirstLetter(orders_pojosAL.get(i).customer_name),
                                orders_pojosAL.get(i).return_date,
                                orders_pojosAL.get(i).cans_with_customer.toString(),


                        };

                        CsvArray.add(csvData);
                    }

                    FileName="Cans Expected Today Report -"+display_date_format;

                    AppUtills.csvFileCreate(CsvArray, "WiseBill/reportexport", FileName);
                }

            }
        }

        @Override
        protected void onPostExecute(Void aVoid){
            progressDialog.cancel();
//            Log.d(TAG, "Aman Chk AL==" + orders_pojosAL);
            if (orders_pojosAL.size()!=0)
            {
                /*switch (selectedActionButton) {
                    case GENERATE:
                        Intent intent =new Intent(Cans_ExpectedToday_SearchActivity.this,Cans_ExpectedToday_ListActivity.class);
                        intent.putParcelableArrayListExtra("orderAl",orders_pojosAL);
                        intent.putExtra("selected_date",display_date_format);
                        startActivity(intent);
                        AppUtills.giveIntentEffect(Cans_ExpectedToday_SearchActivity.this);
                        break;


                    case EXPORT:
                        Snackbar.make(findViewById(android.R.id.content),  "Report Exported Successfully", Snackbar.LENGTH_LONG)
                                .setActionTextColor(getResources().getColor(R.color.red_ToastColor))
                                .show();
                        break;

                    case EXPORT_MAIL:

                        AppUtills.sendMail(FileName+".Csv",Cans_ExpectedToday_SearchActivity.this,AppUtills.Email,FileName);

                        break;

                    default:
                        break;
                }*/

            }else{
                Snackbar.make(findViewById(android.R.id.content), "No Reports found!!", Snackbar.LENGTH_LONG)
                        .setActionTextColor(getResources().getColor(R.color.red_ToastColor))
                        .show();
            }
            super.onPostExecute(aVoid);


        }
    }
}
