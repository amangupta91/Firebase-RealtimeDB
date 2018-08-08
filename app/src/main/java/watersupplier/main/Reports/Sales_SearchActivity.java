package watersupplier.main.Reports;

import android.animation.ArgbEvaluator;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.text.Editable;
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
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;

import dmax.dialog.SpotsDialog;
import watersupplier.main.Adapter.Customer_ListAdapter;
import watersupplier.main.Firsebase_Operations.FirebaseInstance;
import watersupplier.main.Main.Common_ActionBar_Abstract;
import watersupplier.main.Pojo.Customer_POJO;
import watersupplier.main.Pojo.Orders_POJO;
import watersupplier.main.R;
import watersupplier.main.Utils.AppUtills;

/**
 * Created by Aman Gupta on 5/5/17.
 */

public class Sales_SearchActivity extends Common_ActionBar_Abstract implements View.OnClickListener{

    //UI variables
    private EditText customer_name_search, from_date,to_date;
    private View view;
    private Button btn_generate,btn_export,btn_export_mail,button_close;
    private ImageView from_date_icon,to_date_icon;

    //Non UI variables
    private FirebaseDatabase database;
    private DatabaseReference databaseReference,databaseReference_customers;
    private String TAG = "Sales_SearchActivity";
    private int year, month, day,to_year,to_month,to_day;
    public String from_date_string,to_date_string,mobileNo,FileName="";
    private Calendar calander;
    static final int FROM_DATE_DIALOG = 1;
    static final int TO_DATE_DIALOG = 2;
    int cur = 0;
    private DatePicker datePicker;
    private enum SelectedActionButton{GENERATE,EXPORT,EXPORT_MAIL};
    private Sales_SearchActivity.SelectedActionButton selectedActionButton;
    private ArrayList<Orders_POJO> orders_pojosAL;
    private Orders_POJO orders_pojo;
    private ObjectAnimator textColorAnim2;
    private Customer_ListAdapter customer_listAdapter;
    ArrayList<String> dateAl;
    private int width,height;
    private Double cans,Total_cans=0.0;
    private String date22;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_customer_account_search);
        initialization();
        generateEvents();
        setDefaultDate();
    }

    private void setDefaultDate() {
        {

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
        getSupportActionBar().setTitle(getString(R.string.sales));

        database = FirebaseInstance.getInstance();
        databaseReference = database.getReference(getString(R.string.app_name)).child(AppUtills.getUserName(Sales_SearchActivity.this)).
                child(getString(R.string.orders_page));
        customer_name_search = (EditText) findViewById(R.id.customer_name_search);
        customer_name_search.setVisibility(view.GONE);

        from_date = (EditText) findViewById(R.id.from_date);
        to_date = (EditText) findViewById(R.id.to_date);
        btn_generate = (Button) findViewById(R.id.btn_generate);
        btn_export = (Button) findViewById(R.id.btn_export);
        btn_export_mail = (Button) findViewById(R.id.btn_export_mail);
        from_date_icon = (ImageView) findViewById(R.id.from_date_icon);
        to_date_icon = (ImageView) findViewById(R.id.to_date_icon);

        textColorAnim2 = ObjectAnimator.ofInt(btn_generate, "textColor", getResources().getColor(R.color.text_shadow_white),getResources().getColor(R.color.red_ToastColor));
        textColorAnim2.setDuration(800);
        textColorAnim2.setEvaluator(new ArgbEvaluator());
        textColorAnim2.setRepeatCount(ValueAnimator.INFINITE);
        textColorAnim2.setRepeatMode(ValueAnimator.REVERSE);
        textColorAnim2.start();

        orders_pojosAL = new ArrayList<Orders_POJO>();
        dateAl = new ArrayList<>();
    }

    private void generateEvents() {
        btn_export_mail.setOnClickListener(this);
        btn_export.setOnClickListener(this);
        btn_generate.setOnClickListener(this);
        from_date.setOnClickListener(this);
        to_date.setOnClickListener(this);
        from_date_icon.setOnClickListener(this);
        to_date_icon.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
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
                selectedActionButton= Sales_SearchActivity.SelectedActionButton.GENERATE;
                Event_Listners2();
                break;
            case R.id.btn_export:
                selectedActionButton = Sales_SearchActivity.SelectedActionButton.EXPORT;
                Event_Listners2();
//                new GenerateAsync().execute();
                break;
            case R.id.btn_export_mail:
                selectedActionButton = Sales_SearchActivity.SelectedActionButton.EXPORT_MAIL;
                Event_Listners2();
                break;
            default:
                break;
        }
        
    }

    private void Event_Listners2() {
        databaseReference = database.getReference(getString(R.string.app_name)).child(AppUtills.getUserName(Sales_SearchActivity.this)).
                child(getString(R.string.orders_page));
//        orders_pojosAL.clear();
        final AlertDialog progressDialog = new SpotsDialog(Sales_SearchActivity.this);
        progressDialog.show();

        if (AppUtills.isNetworkAvailable(Sales_SearchActivity.this)) {
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

                                    SimpleDateFormat formatter = new SimpleDateFormat(AppUtills.DATE_FORMAT_DDMMMYYYY);
                                    String str1 = from_date_string;
                                    Date from_date = formatter.parse(str1);

                                    String str2 = jsonObjectChild1.optString("order_date");
                                    Date order_date = formatter.parse(str2);

                                    String str3 = to_date_string;
                                    Date to_date = formatter.parse(str3);

                                    if (order_date.after(from_date) && order_date.before(to_date)) {
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
//                                            orders_pojosAL.add(pair.);
//                                            Log.d(TAG,"Aman chk map=="+(Orders_POJO) order_map.values());
                                    }
                                        else if (order_date.equals(from_date) && order_date.before(to_date)) {
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
                                        } else if (order_date.after(from_date) && order_date.equals(to_date)) {
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
                                        } else if (order_date.equals(from_date) && order_date.equals(to_date)) {
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
                                        }
                                }
                            }
//                            Log.d(TAG, "Aman Chk AL size==" + orders_pojosAL.size());

                            HashMap<String, ArrayList<Double>> order_map=new HashMap<String, ArrayList<Double>>();
                            order_map.clear();
                            for (int i=0; i<orders_pojosAL.size();i++) {
                                if (!order_map.containsKey(orders_pojosAL.get(i).order_date)) {
                                    ArrayList<Double> new_cans_sold = new ArrayList<Double>();

                                    new_cans_sold.clear();
                                    new_cans_sold.add(Double.parseDouble(orders_pojosAL.get(i).new_cans_given));
//                                    Log.d(TAG, "Aman Chk creditAL==" + new_cans_sold.size());
                                    order_map.put(orders_pojosAL.get(i).order_date, new_cans_sold);

//
                                } else {
                                    ArrayList<Double> new_cans_sold = order_map.get(orders_pojosAL.get(i).order_date);
                                    new_cans_sold.add(Double.valueOf(orders_pojosAL.get(i).new_cans_given));
                                    order_map.put(orders_pojosAL.get(i).order_date, new_cans_sold);
//
//                                    Log.d(TAG, "Aman Chk MAP==" + order_map.size());

                                }

                            }

                            Iterator myIterator1 = order_map.keySet().iterator();
//                            Log.i(TAG, "order_map "+order_map);
                            while(myIterator1.hasNext()) {
                                String key=(String) myIterator1.next();
                                dateAl.add(key);

                            } // while(myVeryOwnIterator.hasNext()) Closes here...
                            Collections.sort(dateAl);
                            orders_pojosAL.clear();

                            for (int k=0;k<dateAl.size(); k++){
                                Orders_POJO orders_pojo=new Orders_POJO();
                                orders_pojo.order_date=dateAl.get(k);
                                date22 = dateAl.get(k);
                                ArrayList<Double> value=order_map.get(dateAl.get(k));
                                cans=0.0;
                                for (int j=0; j<value.size(); j++){
                                    cans+=value.get(j);
                                }
                                orders_pojo.new_cans_given= String.valueOf(cans);
                                orders_pojosAL.add(orders_pojo);
                                Log.d(TAG,"Aman chk MAP=="+order_map.get(dateAl.get(k)));


                            }
                            for (int i = 0; i < orders_pojosAL.size(); i++) {

                                Total_cans = Total_cans+Double.parseDouble(orders_pojosAL.get(i).new_cans_given);

                            }

                            if(!orders_pojosAL.isEmpty()) {
                                switch (selectedActionButton) {
                                    case GENERATE:
                                        Intent intent = new Intent(Sales_SearchActivity.this, Sales_ListActivity.class);
                                        intent.putParcelableArrayListExtra("orderAl", orders_pojosAL);
                                        intent.putExtra("from_date", from_date_string);
                                        intent.putExtra("to_date", to_date_string);
                                        intent.putExtra("Total_cans",Total_cans+"");
                                        startActivity(intent);
                                        AppUtills.giveIntentEffect(Sales_SearchActivity.this);
                                        break;


                                    case EXPORT:
                                        CsvBuilder(orders_pojosAL);
                                        Snackbar.make(findViewById(android.R.id.content), "Report Exported Successfully", Snackbar.LENGTH_LONG)
                                                .setActionTextColor(getResources().getColor(R.color.red_ToastColor))
                                                .show();
                                        break;

                                    case EXPORT_MAIL:
                                        CsvBuilder(orders_pojosAL);
                                        AppUtills.sendMail(FileName + ".Csv", Sales_SearchActivity.this, AppUtills.Email, FileName);

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



//                            String[] Enter={""};


                            String[]csvDataHeadCommonReportName={getResources().getString(R.string.sales)+"-"+from_date_string.toString(),to_date_string.toString()};
                            CsvArray.add(csvDataHeadCommonReportName);

                            String[]csvDate={"From Date",from_date_string.toString(),"To Date",to_date_string.toString()};
                            CsvArray.add(csvDate);

//                            CsvArray.add(Enter);

                            String[]csvHeader={

                                    getResources().getString(R.string.order_date_spelling),
                                    getResources().getString(R.string.number_of_cans),
                                    getResources().getString(R.string.total_cans_sold)
                            };
                            CsvArray.add(csvHeader);


                                    String[] csvData = {

                                            date22.toString(),
                                            String.valueOf(cans),
                                            String.valueOf(Total_cans),


                                    };

                                    CsvArray.add(csvData);


                            FileName=getString(R.string.sales)+" Report"+"-"+from_date_string+"To"+to_date_string;

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
                Log.d(TAG,"Aman check FROM_DATE_DIALOG="+id);
                cur = FROM_DATE_DIALOG;
                // set date picker as current date
                return new DatePickerDialog(this, datePickerListener, year, month, day);
            case TO_DATE_DIALOG:
                cur = TO_DATE_DIALOG;
                Log.d(TAG,"Aman check TO_DATE_DIALOG="+id);
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
