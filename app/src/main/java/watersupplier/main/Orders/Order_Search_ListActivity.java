package watersupplier.main.Orders;

import android.app.AlertDialog;
import android.app.Application;
import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Filter;
import android.widget.ListView;
import android.widget.SearchView;
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
import java.util.Locale;

import dmax.dialog.SpotsDialog;
import watersupplier.main.Adapter.Customer_ListAdapter;
import watersupplier.main.Adapter.Order_ListAdapter;
import watersupplier.main.Customer.Customer_List_Activity;
import watersupplier.main.Firsebase_Operations.FirebaseInstance;
import watersupplier.main.Main.Common_ActionBar_Abstract;
import watersupplier.main.Main.First_Menu;
import watersupplier.main.Pojo.Customer_POJO;
import watersupplier.main.R;
import watersupplier.main.Utils.AppUtills;

/**
 * Created by Aman Gupta on 20/1/17.
 */

public class Order_Search_ListActivity extends Common_ActionBar_Abstract implements View.OnClickListener,AdapterView.OnItemClickListener,TextWatcher{

    //UI variables
    private TextView customer_name,mobile,not_found;
    private ListView customer_list;
    private Button btn_addCust;
    private EditText search;
    private ArrayList<Customer_POJO> customer_pojoArrayList;
    private Customer_POJO customer_pojo;
    private android.support.v7.widget.SearchView searchView;
    private SearchManager searchManager;

    //Non-UI variables
    private int width,height;
    private FirebaseDatabase database;
    private DatabaseReference databaseReference,databaseReference_walkin;
    private Order_ListAdapter customer_listAdapter;
    private View view;
    private int SelectedPosition=-1;
    private String TAG = "Order_Search_ListActivity";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        setContentView(R.layout.activity_order_list);
        super.onCreate(savedInstanceState);
        initialization();
        generateEvents();
        screenWidth();
        Event_Listners();
    }

    private void initialization() {
        getSupportActionBar().setTitle(getResources().getString(R.string.orders_page));
        customer_name = (TextView) findViewById(R.id.ordr_customer_name);
        mobile = (TextView) findViewById(R.id.ordr_mobile);
        customer_list = (ListView) findViewById(R.id.ordr_customer_list);
        btn_addCust = (Button) findViewById(R.id.btn_addCust);
        not_found = (TextView) findViewById(R.id.not_found);
        not_found.setVisibility(View.GONE);
        not_found.setText(getResources().getString(R.string.no_cust_found));
        database = FirebaseInstance.getInstance();
        databaseReference=database.getReference(getString(R.string.app_name)).child(AppUtills.getUserName(Order_Search_ListActivity.this)).child(getString(R.string.customer_page));
        databaseReference_walkin=database.getReference(getString(R.string.app_name)).child(AppUtills.getUserName(Order_Search_ListActivity.this)).child("Walkin Customers");
        customer_pojoArrayList = new ArrayList<Customer_POJO>();
        search = (EditText) findViewById(R.id.search);
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);//to disable keyboard

    }

    private void generateEvents() {
        customer_list.setOnItemClickListener(Order_Search_ListActivity.this);
        search.addTextChangedListener(this);
        btn_addCust.setOnClickListener(this);

//        searchView.setQueryRefinementEnabled(true);
    }

    private void screenWidth() {
        height = getResources().getDisplayMetrics().heightPixels;
        width = getResources().getDisplayMetrics().widthPixels;

        customer_name.setWidth((int) (width/1.7));
        mobile.setWidth((int) (width/2.5));
    }

    private void Event_Listners() {

            if (AppUtills.isNetworkAvailable(Order_Search_ListActivity.this)) {

                final AlertDialog progressDialog = new SpotsDialog(Order_Search_ListActivity.this);
                progressDialog.setIcon(R.drawable.search);
//             progressDialog.setMessage("Please Wait...");
            progressDialog.show();
                databaseReference.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        customer_pojoArrayList.clear();
                        JSONArray jsonElements = null;
                        try {
                            if (dataSnapshot.getValue() != null && !dataSnapshot.getValue().equals("")) {
                                Gson gson = new Gson();
                                String json = gson.toJson(dataSnapshot.getValue());
                                JSONObject jsonObject = new JSONObject(json.toString());
                                Iterator iterator = jsonObject.keys();
                                customer_pojoArrayList.clear();
                                while (iterator.hasNext()) {
                                    String key = (String) iterator.next();
                                    JSONObject jsonObjectChild = jsonObject.getJSONObject(key);
//                                ArrayList<String> BillPrefAL = new ArrayList<String>();


                                    Customer_POJO customer_pojo = new Customer_POJO(
                                            jsonObjectChild.optInt("customerId"),
                                            jsonObjectChild.optString("customer_name"),
                                            jsonObjectChild.optString("address"),
                                            jsonObjectChild.optString("mobile"),
                                            jsonObjectChild.optString("customer_spPrice"),
                                            jsonObjectChild.optString("credit_line"),key);
                                    customer_pojoArrayList.add(customer_pojo);

//                                Log.d(TAG,"Aman CHK AL"+key);
                                }

                                Collections.sort(customer_pojoArrayList, new Comparator<Customer_POJO>() {
                                    @Override
                                    public int compare(Customer_POJO lhs, Customer_POJO rhs) {
                                        return lhs.customerId - rhs.customerId;
                                    }
                                });

                                customer_listAdapter = new Order_ListAdapter(Order_Search_ListActivity.this, Order_Search_ListActivity.this, customer_pojoArrayList);
                                customer_list.setAdapter(customer_listAdapter);
                                customer_listAdapter.notifyDataSetChanged();


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

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });

                databaseReference_walkin.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
//                        customer_pojoArrayList.clear();
                        JSONArray jsonElements = null;
                        try {
                            if (dataSnapshot.getValue() != null && !dataSnapshot.getValue().equals("")) {
                                Gson gson = new Gson();
                                String json = gson.toJson(dataSnapshot.getValue());
                                JSONObject jsonObject = new JSONObject(json.toString());
                                Iterator iterator = jsonObject.keys();
//                                customer_pojoArrayList.clear();
                                while (iterator.hasNext()) {
                                    String key = (String) iterator.next();
                                    JSONObject jsonObjectChild = jsonObject.getJSONObject(key);
//                                ArrayList<String> BillPrefAL = new ArrayList<String>();


                                    Customer_POJO customer_pojo = new Customer_POJO(
                                            jsonObjectChild.getInt("customerId"),
                                            jsonObjectChild.getString("customer_name"),
                                            jsonObjectChild.getString("address"),
                                            jsonObjectChild.getString("mobile"),
                                            jsonObjectChild.getString("customer_spPrice"),
                                            jsonObjectChild.getString("credit_line"),key);
                                    customer_pojoArrayList.add(customer_pojo);

//                                Log.d(TAG,"Aman CHK AL"+key);
                                }

                                Collections.sort(customer_pojoArrayList, new Comparator<Customer_POJO>() {
                                    @Override
                                    public int compare(Customer_POJO lhs, Customer_POJO rhs) {
                                        return lhs.customerId - rhs.customerId;
                                    }
                                });

                                customer_listAdapter = new Order_ListAdapter(Order_Search_ListActivity.this, Order_Search_ListActivity.this, customer_pojoArrayList);
                                customer_list.setAdapter(customer_listAdapter);
                                customer_listAdapter.notifyDataSetChanged();


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

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });
            }else{
                Snackbar.make(findViewById(android.R.id.content), getResources().getString(R.string.CheckYourInternetConnection), Snackbar.LENGTH_LONG)
                        .setActionTextColor(getResources().getColor(R.color.red_ToastColor))
                        .show();
            }


    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        // Associate searchable configuration with the SearchView
        getMenuInflater().inflate(R.menu.menu_with_back,menu);


        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case R.id.item_back:
                AppUtills.givefinishEffect(this);
                this.finish();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onResume() {
        customer_list.requestFocus();
        search.setFocusableInTouchMode(true);
//        searchView.setFocusableInTouchMode(true);
        if (customer_listAdapter!=null) {
            customer_listAdapter.set_Selection(-1);
            customer_listAdapter.notifyDataSetChanged();
        }
        super.onResume();
    }

    @Override
    public void onBackPressed() {
        AppUtills.givefinishEffect(this);
        this.finish();
    }
    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {



    }

    @Override
    public void afterTextChanged(Editable s) {


        if(s.toString().isEmpty()||search.getText().toString().isEmpty()){
//            not_found.setVisibility(View.GONE);
            customer_listAdapter = new Order_ListAdapter(Order_Search_ListActivity.this, Order_Search_ListActivity.this, customer_pojoArrayList);
            customer_list.setAdapter(customer_listAdapter);
            customer_listAdapter.notifyDataSetChanged();

        }

        if (customer_listAdapter!=null) {

//            if (!s.toString().equals("")) {
            customer_listAdapter.getFilter().filter(s.toString(), new Filter.FilterListener() {

                @Override
                public void onFilterComplete(int count) {
                    //THIS INNER CLASS WILL GIVE THE   EXACT COUNT OF LISTVIEW AFTER FILTER IS COMPLETED....

                    if (count < 1){//i.e. after searching User is unable to see anything....then show error message that No prods found...
                        not_found.setVisibility(View.VISIBLE);
                    }
                    else
                        not_found.setVisibility(View.GONE);

                }//onFilterComplete closes here....

            });

//            }
        }



    }

    @Override
    public void onClick(View v) {

        switch (v.getId()){
            case R.id.btn_addCust:
//                int number = Integer.parseInt(search.getText().toString());
                boolean digitsOnly = TextUtils.isDigitsOnly(search.getText());
                if(digitsOnly) {
                    Intent intent = new Intent(Order_Search_ListActivity.this, Add_Walkin_Customer_Activity.class);
                    intent.putExtra("mobile_string",search.getText().toString());
                    startActivity(intent);
                    AppUtills.giveIntentEffect(this);
                }
                else if(!digitsOnly){
                    Intent intent = new Intent(Order_Search_ListActivity.this, Add_Walkin_Customer_Activity.class);
                    startActivity(intent);
                    AppUtills.giveIntentEffect(this);
                }
                else {
                    Intent intent = new Intent(Order_Search_ListActivity.this, Add_Walkin_Customer_Activity.class);
                    startActivity(intent);
                    AppUtills.giveIntentEffect(this);
                }

//                finish();
        }
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        SelectedPosition=position;
//        Log.d(TAG,"Aman chk cust1=="+customer_pojoArrayList.get(SelectedPosition).customer_name);

        if (customer_listAdapter!=null) {
            customer_listAdapter.set_Selection(position);
            customer_listAdapter.notifyDataSetChanged();
            Customer_POJO pojo = customer_pojoArrayList.get(position);
//            Snackbar.make(view, "You have selected:- "+pojo.customer_name, Snackbar.LENGTH_LONG).setAction("No action", null).show();
        }
         if(SelectedPosition !=-1) {

             Intent intentToConfig = new Intent(Order_Search_ListActivity.this, Manage_Orders_Activity.class);
             intentToConfig.putExtra(getString(R.string.customer_name_spelling),customer_pojoArrayList.get(SelectedPosition).customer_name);
             intentToConfig.putExtra("credit_line",customer_pojoArrayList.get(SelectedPosition).credit_line);
             intentToConfig.putExtra("customer_spPrice",customer_pojoArrayList.get(SelectedPosition).customer_spPrice);
             intentToConfig.putExtra("phone",customer_pojoArrayList.get(SelectedPosition).mobile);
             intentToConfig.putExtra("pushKey",customer_pojoArrayList.get(SelectedPosition).push_key);
             startActivity(intentToConfig);
             AppUtills.giveIntentEffect(this);
        }

    }



}
