package watersupplier.main.Product;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Vibrator;
import android.preference.PreferenceManager;
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
import watersupplier.main.Main.Common_ActionBar_Abstract;
import watersupplier.main.Orders.Close_Account_Activity;
import watersupplier.main.Orders.Manage_Orders_Activity;
import watersupplier.main.Orders.Volly_Response_Activity;
import watersupplier.main.Pojo.Orders_POJO;
import watersupplier.main.Pojo.Product_POJO;
import watersupplier.main.R;
import watersupplier.main.Utils.AppUtills;

/**
 * Created by Aman Gupta on 18/1/17.
 */
public class Product_Setup extends Common_ActionBar_Abstract implements View.OnClickListener {

    //UI variables
    private EditText product_name,product_desc,price;
    private Button save,to_volly;

    //Non-UI variables
    private ArrayList<Product_POJO> product_pojoArrayList;
    private Product_POJO product_pojo;
    private FirebaseDatabase database;
    private DatabaseReference databaseReference;
    private  String pushkey;
    private String TAG = "Product_Setup";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_product_setup);
        initialization();
        generateEvents();
        screenWidth();

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_with_back, menu);
        return super.onCreateOptionsMenu(menu);
    }

    private void screenWidth() {
    }

    private void generateEvents() {

        save.setOnClickListener(Product_Setup.this);
        to_volly.setOnClickListener(Product_Setup.this);
    }

    private void initialization() {
        getSupportActionBar().setTitle(R.string.product_page);
        database = FirebaseDatabase.getInstance();
        databaseReference=database.getReference(getString(R.string.app_name)).child(AppUtills.getUserName(Product_Setup.this)).child(getString(R.string.product_page));
        product_pojoArrayList = new ArrayList<Product_POJO>();
        product_pojo = new Product_POJO();

        product_name = (EditText) findViewById(R.id.product_name);
        product_desc = (EditText) findViewById(R.id.product_desc);
        price = (EditText) findViewById(R.id.price);
        save = (Button) findViewById(R.id.btn_Save);
        to_volly = (Button) findViewById(R.id.to_volly);
        setText();

    }

    private void setText() {


        databaseReference.addValueEventListener(new ValueEventListener() {
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


                           Product_POJO product_pojo = new Product_POJO(
                                   jsonObjectChild.getString("product_Name"),
                                   jsonObjectChild.getString("product_Description"),
                                   jsonObjectChild.getInt("price"),key);

                            pushkey = key;

//                            Log.d(TAG,"Aman CHK product_Name =="+product_pojo.product_Name);


                            if(product_pojo.product_Name!=null){
                               product_name.setText(product_pojo.product_Name);
                            }
                            else
                                product_name.setText("");

                            if (product_pojo.product_Description!=null){
                                product_desc.setText(product_pojo.product_Description);
                            }
                            else
                                product_desc.setText("");

                            if (product_pojo.price!=0){
                                price.setText(product_pojo.price+"");
                            }
                            else if(product_pojo.price ==0 ){
                                price.setText(0+"");
                            }


                        }

                    }


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


    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.btn_Save:
                if(product_name.getText().toString().trim().isEmpty()||product_name.getText().toString().trim().equals("")){
                    product_name.requestFocus();
                    product_name.setError(getResources().getString(R.string.cannot_be_empty));
                }
                else if(price.getText().toString().trim().isEmpty()||price.getText().toString().trim().equals("")){
                    price.requestFocus();
                    price.setError(getResources().getString(R.string.cannot_be_empty));
                }


                else {
                    if (AppUtills.isNetworkAvailable(Product_Setup.this)) {
//                        if(!PreferenceManager.getDefaultSharedPreferences(Product_Setup.this).contains(getString(R.string.price_spelling))) {
                            save();
                            setText();

                           /* double price = Double.parseDouble(String.valueOf(product_pojo.price));
                            SharedPreferences sharedPreferences=getSharedPreferences("prod_price", Context.MODE_PRIVATE);
                            SharedPreferences.Editor editor=sharedPreferences.edit();
                            editor.putString(getString(R.string.price_spelling), price+"");
                            editor.commit();*/
//                        }
                    }
                    else
//                    save();
                        Snackbar.make(findViewById(android.R.id.content), getResources().getString(R.string.CheckYourInternetConnection), Snackbar.LENGTH_LONG)
                                .setActionTextColor(getResources().getColor(R.color.red_ToastColor))
                                .show();
                }
                break;
            case R.id.to_volly:
                Intent intent = new Intent(Product_Setup.this, Volly_Response_Activity.class);
                startActivity(intent);
                this.finish();
                break;

        }
    }

    private void save() {
        if(!product_name.getText().toString().isEmpty()){
            product_pojo = new Product_POJO(product_name.getText().toString().trim(),
                    product_desc.getText().toString().trim(),
                    Integer.parseInt(price.getText().toString()),"");
//            Log.d(TAG,"Aman CHK key =="+pushkey);
            if(pushkey!=null){
                databaseReference.child(pushkey).setValue(product_pojo);
                Toast.makeText(this, getResources().getString(R.string.updated_sucess),Toast.LENGTH_SHORT).show();
                // Get instance of Vibrator from current Context
                android.os.Vibrator v = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
                // Vibrate for 400 milliseconds
                v.vibrate(400);
                finish();
            }
            else {
                databaseReference.push().setValue(product_pojo);
                Toast.makeText(this, getResources().getString(R.string.saved_sucess), Toast.LENGTH_SHORT).show();
                finish();
            }
        }

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        AppUtills.givefinishEffect(this);
        finish();
        return super.onOptionsItemSelected(item);
    }
    @Override
    public void onBackPressed() {
        super.onBackPressed();
        AppUtills.givefinishEffect(this);
        finish();
    }
}
