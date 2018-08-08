package watersupplier.main.Customer;

import android.content.Context;
import android.os.Bundle;
import android.os.Vibrator;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseException;
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

import watersupplier.main.Adapter.Customer_ListAdapter;
import watersupplier.main.Main.Common_ActionBar_Abstract;
import watersupplier.main.Firsebase_Operations.FirebaseInstance;
import watersupplier.main.Pojo.Customer_POJO;
import watersupplier.main.Pojo.Product_POJO;
import watersupplier.main.Product.Product_Setup;
import watersupplier.main.R;
import watersupplier.main.Utils.AppUtills;

/**
 * Created by Aman Gupta on 20/1/17.
 */

public class Add_Edit_Customer_Activity extends Common_ActionBar_Abstract implements View.OnClickListener {

    //UI variable
    private EditText customer_name, address, mobile, price, credit_line;
    private Button btn_Save;

    //NonUi variable
    private ArrayList<Customer_POJO> customer_pojoArrayList;
    private ArrayList<Integer> customerID_AL;
    private Customer_POJO customer_pojo;
    private Product_POJO product_pojo;
    private FirebaseDatabase database;
    private DatabaseReference databaseReference,databaseReference2;
    private int customerID = 0;
    private String TAG = "Add_Edit_Customer_Activity";
    private String Edit_Customer = "";
    private String key,prod_price;
    private ArrayList<String> mobile_noAL;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_customer);
        initialization();
        generateEvents();
        screenWidth();
        Event_Listners();
    }


    private void Event_Listners() {

        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
//                customer_pojoArrayList.clear();
                JSONArray jsonElements = null;
                try {
                    if (dataSnapshot.getValue() != null && !dataSnapshot.getValue().equals("")) {
                        Gson gson = new Gson();
                        String json = gson.toJson(dataSnapshot.getValue());
                        JSONObject jsonObject = new JSONObject(json.toString());
                        Iterator iterator = jsonObject.keys();
                        mobile_noAL.clear();
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
//                            customer_pojoArrayList.add(customer_pojo);
                                    mobile_noAL.add(customer_pojo.mobile);
//                                Log.d(TAG,"Aman MOBILE=="+mobile_noAL.size());
                        }

                    }

                } catch (Exception e) {
                    e.printStackTrace();
                } finally {
                    try {
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        //fetching products database
        databaseReference2.addValueEventListener(new ValueEventListener() {
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

                            Product_POJO product_pojo = new Product_POJO(
                                    jsonObjectChild.getString("product_Name"),
                                    jsonObjectChild.getString("product_Description"),
                                    jsonObjectChild.getInt("price"),key);
//
                            if (!Edit_Customer.equalsIgnoreCase(getResources().getString(R.string.edit_customer))){
                                getSupportActionBar().setTitle(R.string.add_customer);
                                if(product_pojo.price!=0) {
                                    price.setText(product_pojo.price + "");
                                }
                                else if(product_pojo.price==0){
                                    price.setText(0+"");
                                }
                            }
                        }

                    }

                } catch (Exception e) {
                    e.printStackTrace();
                } finally {
                    try {
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

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_with_back,menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        AppUtills.givefinishEffect(this);
        finish();
        return super.onOptionsItemSelected(item);

    }

    private void initialization() {
        //for edit customer

        customer_name = (EditText) findViewById(R.id.txt_customer_name);
        address = (EditText) findViewById(R.id.address);
        mobile = (EditText) findViewById(R.id.mobile);
        price = (EditText) findViewById(R.id.price);
        credit_line = (EditText) findViewById(R.id.credit_line);
        btn_Save = (Button) findViewById(R.id.btn_Save);

        Edit_Customer = getIntent().getStringExtra(getResources().getString(R.string.selectedOperationSpelling));
        key = getIntent().getStringExtra("pushKey");

//        prod_price=getSharedPreferences("prod_price",Context.MODE_PRIVATE).getString(getString(R.string.price_spelling),"");
//        Log.d(TAG,"Aman PRICE:: "+prod_price);
        customer_pojoArrayList = new ArrayList<Customer_POJO>();
        mobile_noAL = new ArrayList<String>();
        customerID_AL = new ArrayList<Integer>();
        customer_pojo = new Customer_POJO();
        product_pojo = new Product_POJO();
        database = FirebaseInstance.getInstance();
        databaseReference=database.getReference(getString(R.string.app_name)).child(AppUtills.getUserName(Add_Edit_Customer_Activity.this)).child(getString(R.string.customer_page));
        databaseReference2 =database.getReference(getString(R.string.app_name)).child(AppUtills.getUserName(Add_Edit_Customer_Activity.this)).child(getString(R.string.product_page));

        if (Edit_Customer.equalsIgnoreCase(getResources().getString(R.string.edit_customer))){
            getSupportActionBar().setTitle(R.string.edit_customer);

            if (getIntent().getParcelableExtra(getString(R.string.parcelableValueSpelling))!=null){
                customer_pojo=getIntent().getParcelableExtra(getString(R.string.parcelableValueSpelling));

                customer_name.setText(customer_pojo.customer_name);
                address.setText(customer_pojo.address);
                mobile.setText(customer_pojo.mobile);
                price.setText(customer_pojo.customer_spPrice);
                credit_line.setText(customer_pojo.credit_line);



            }
            btn_Save.setText(getString(R.string.updateSpelling));
        }

    }

    private void generateEvents() {
        btn_Save.setOnClickListener(this);
    }

    private void screenWidth() {
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_Save:
                if (customer_name.getText().toString().isEmpty()){
                    customer_name.requestFocus();
                    customer_name.setError(getResources().getString(R.string.cannot_be_empty));
                }
                else if (mobile.getText().toString().isEmpty()){
                    mobile.requestFocus();
                    mobile.setError(getResources().getString(R.string.cannot_be_empty));
                }
                else if (mobile.getText().length()<10) {
                    mobile.requestFocus();
                    mobile.setError(getResources().getString(R.string.mobile_number_length));
                }
                else if (price.getText().toString().isEmpty()){
                    price.requestFocus();
                    price.setError(getResources().getString(R.string.cannot_be_empty));
                }
                else if (credit_line.getText().toString().isEmpty()){
                    credit_line.requestFocus();
                    credit_line.setError(getResources().getString(R.string.cannot_be_empty));
                }
                else{
                    if (AppUtills.isNetworkAvailable(Add_Edit_Customer_Activity.this)) {
                        save();
                    }
                    else
//                    save();
                        Snackbar.make(findViewById(android.R.id.content), getResources().getString(R.string.CheckYourInternetConnection), Snackbar.LENGTH_LONG)
                                .setActionTextColor(getResources().getColor(R.color.red_ToastColor))
                                .show();
                }
        }
    }

    private void save() {
        if (customerID_AL.size()!=0){
            customerID = getCustomerID();
        }

        if (!customer_name.getText().toString().isEmpty()){
           customer_pojo = new Customer_POJO(customerID,AppUtills.capitalizeFirstLetter(customer_name.getText().toString()),
                    AppUtills.capitalizeFirstLetter(address.getText().toString()),
                    mobile.getText().toString(),
                    price.getText().toString(),
                    credit_line.getText().toString(),"");
            try {
                if (Edit_Customer.equals(getString(R.string.edit_customer))) {
                    Event_Listners();
                    Log.d(TAG,"Aman chk mob=="+customer_pojo.mobile);
//                    if(mobile_noAL.contains(mobile.getText().toString())) {
                    if(mobile.getText().toString().equals(customer_pojo.mobile) || mobile_noAL.contains(mobile.getText().toString())) {
                            databaseReference.child(key).setValue(customer_pojo);
                            Toast.makeText(this, getResources().getString(R.string.updated_sucess), Toast.LENGTH_SHORT).show();
                            // Get instance of Vibrator from current Context

                            finish();
                        }
                       /* else
                            Snackbar.make(findViewById(android.R.id.content), getResources().getString(R.string.mobile_number_already_exist), Snackbar.LENGTH_LONG)
                                    .setActionTextColor(getResources().getColor(R.color.red_ToastColor))
                                    .show();*/
//                    }
                  /* else if (!mobile_noAL.contains(mobile.getText().toString())) {
                        databaseReference.child(key).setValue(customer_pojo);
                        Toast.makeText(this, getResources().getString(R.string.updated_sucess), Toast.LENGTH_SHORT).show();
                        // Get instance of Vibrator from current Context
                        android.os.Vibrator v = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
                        // Vibrate for 400 milliseconds
                        v.vibrate(400);
                        finish();
                    }*/

                    else
                        Snackbar.make(findViewById(android.R.id.content), getResources().getString(R.string.mobile_number_already_exist), Snackbar.LENGTH_LONG)
                                .setActionTextColor(getResources().getColor(R.color.red_ToastColor))
                                .show();




                }
                else {
                    Event_Listners();
//                    Log.d(TAG,"Aman MOBILE=="+customer_pojo.mobile);
                    if(!mobile_noAL.contains(mobile.getText().toString())) {
                        databaseReference.push().setValue(customer_pojo);
                        Toast.makeText(this, getResources().getString(R.string.saved_sucess), Toast.LENGTH_SHORT).show();
                        // Get instance of Vibrator from current Context

                        finish();
                    }
                    else
                        Snackbar.make(findViewById(android.R.id.content), getResources().getString(R.string.mobile_number_already_exist), Snackbar.LENGTH_LONG)
                                .setActionTextColor(getResources().getColor(R.color.red_ToastColor))
                                .show();
                }
            }
            catch (DatabaseException e){
                e.printStackTrace();
            }

        }
//        customerID=0;
    }

    private int getCustomerID() {
       return Collections.max(customerID_AL)+1;
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        AppUtills.givefinishEffect(this);
        finish();
    }
}
