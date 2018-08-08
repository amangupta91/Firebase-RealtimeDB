package watersupplier.main.Orders;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Vibrator;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.Iterator;

import watersupplier.main.Customer.Add_Edit_Customer_Activity;
import watersupplier.main.Firsebase_Operations.FirebaseInstance;
import watersupplier.main.Main.Common_ActionBar_Abstract;
import watersupplier.main.Pojo.Customer_POJO;
import watersupplier.main.Pojo.Product_POJO;
import watersupplier.main.R;
import watersupplier.main.Utils.AppUtills;

/**
 * Created by Aman Gupta on 22/2/17.
 */

public class Add_Walkin_Customer_Activity extends Common_ActionBar_Abstract implements View.OnClickListener {

    //UI variable
    private EditText customer_name, address, mobile, price, credit_line;
    private Button btn_Next;
    private View view;

    //Non UI varibles
    private FirebaseDatabase database;
    private DatabaseReference databaseReference,databaseReference2;
    private String TAG = "Add_Walkin_Customer_Activity";
    private Customer_POJO customer_pojo;
    private int prod_default_price;
    private int custID =0;
    private String mobile_string;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_customer);
        initialization();
        generateEvents();
        Event_listners();
    }

    private void Event_listners() {

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
                            prod_default_price = product_pojo.price;

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
        getMenuInflater().inflate(R.menu.menu_with_back, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        finish();
        return super.onOptionsItemSelected(item);

    }


    private void generateEvents() {
        btn_Next.setOnClickListener(this);
    }

    private void initialization() {

        getSupportActionBar().setTitle(getString(R.string.walkin_customer));
        customer_name = (EditText) findViewById(R.id.txt_customer_name);
        address = (EditText) findViewById(R.id.address);
        mobile = (EditText) findViewById(R.id.mobile);
        price = (EditText) findViewById(R.id.price);
        credit_line = (EditText) findViewById(R.id.credit_line);
        btn_Next = (Button) findViewById(R.id.btn_Save);


        price.setVisibility(view.GONE);
        credit_line.setVisibility(view.GONE);
        btn_Next.setText("Next");


        mobile_string = getIntent().getStringExtra("mobile_string");
        mobile.setText(mobile_string);
        customer_pojo = new Customer_POJO();
        database = FirebaseInstance.getInstance();
        databaseReference=database.getReference(getString(R.string.app_name)).child(AppUtills.getUserName(Add_Walkin_Customer_Activity.this)).child("Walkin Customers");
        databaseReference2 =database.getReference(getString(R.string.app_name)).child(AppUtills.getUserName(Add_Walkin_Customer_Activity.this)).child(getString(R.string.product_page));
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_Save:
                if (customer_name.getText().toString().isEmpty()) {
                    customer_name.requestFocus();
                    customer_name.setError(getResources().getString(R.string.cannot_be_empty));
                } else if (mobile.getText().toString().isEmpty()) {
                    mobile.requestFocus();
                    mobile.setError(getResources().getString(R.string.cannot_be_empty));
                }
                else if (mobile.getText().length()<10) {
                    mobile.requestFocus();
                    mobile.setError(getResources().getString(R.string.mobile_number_length));
                }else {
                    if (AppUtills.isNetworkAvailable(Add_Walkin_Customer_Activity.this)) {
                        save();
                        Intent intentToConfig = new Intent(Add_Walkin_Customer_Activity.this, Manage_Orders_Activity.class);
                        intentToConfig.putExtra(getString(R.string.customer_name_spelling),customer_name.getText().toString());
                        intentToConfig.putExtra("credit_line","0".toString());
                        intentToConfig.putExtra("customer_spPrice",prod_default_price+"");
                        intentToConfig.putExtra("phone",mobile.getText().toString());
                        startActivity(intentToConfig);
                        AppUtills.giveIntentEffect(this);
                        finish();
                       /* Snackbar.make(findViewById(android.R.id.content), "On the Way!!", Snackbar.LENGTH_LONG)
                                .setActionTextColor(getResources().getColor(R.color.red_ToastColor))
                                .show();*/
                    } else
//                    save();
                        Toast.makeText(this, getResources().getString(R.string.CheckYourInternetConnection), Toast.LENGTH_SHORT).show();


                }
        }
    }

    private void save() {

        if (!customer_name.getText().toString().isEmpty()) {
          Customer_POJO customer_pojo = new Customer_POJO( 1, AppUtills.capitalizeFirstLetter(customer_name.getText().toString()),
                    address.getText().toString(),
                    mobile.getText().toString(),
                    "","","");

            databaseReference.push().setValue(customer_pojo);
            Toast.makeText(this,getResources().getString(R.string.saved_sucess),Toast.LENGTH_SHORT).show();
           /* // Get instance of Vibrator from current Context
            android.os.Vibrator v = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
            // Vibrate for 400 milliseconds
            v.vibrate(400);*/
            finish();
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        AppUtills.givefinishEffect(this);
        finish();
    }
}