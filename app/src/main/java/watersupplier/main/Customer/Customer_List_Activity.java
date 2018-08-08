package watersupplier.main.Customer;

import android.app.AlertDialog;
import android.app.SearchManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.content.ContextCompat;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.SearchView;
import android.widget.TextView;
import android.widget.Toast;

import com.daimajia.swipe.SwipeLayout;
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

import dmax.dialog.SpotsDialog;
import watersupplier.main.Adapter.Customer_ListAdapter;
import watersupplier.main.Main.Common_ActionBar_Abstract;
import watersupplier.main.Firsebase_Operations.FirebaseInstance;
import watersupplier.main.Orders.Close_Account_Activity;
import watersupplier.main.Pojo.Customer_POJO;
import watersupplier.main.Pojo.Orders_POJO;
import watersupplier.main.R;
import watersupplier.main.Utils.AppUtills;

/**
 * Created by Aman Gupta on 20/1/17.
 */

public class Customer_List_Activity extends Common_ActionBar_Abstract implements View.OnClickListener,AdapterView.OnItemClickListener,TextWatcher{
    //UI variables
    private TextView customer_name,address,mobile;
    private ListView customer_list;
    private ArrayList<Customer_POJO> customer_pojoArrayList;
    private Customer_POJO customer_pojo;
    private FloatingActionButton add_cust;
    private SwipeLayout swipeLayout;
    private View btnDelete,btnEdit;

//    private View view;

    //Non-UI variables
    private int width,height;
    private FirebaseDatabase database;
    private DatabaseReference databaseReference,databaseReference_orders;
    private Customer_ListAdapter customer_listAdapter;
    private int SelectedPosition=-1;
    private String cust_name_string,last_balance,last_cans_with_customer,cust_name_ordr;
    private ArrayList<String> balanceAL,cans_with_custAL;
    private  Orders_POJO orders_pojo;

    // Refresh menu item
    private MenuItem refreshMenuItem;
    private String TAG = "Customer_List_Activity";


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_customer_list);
        initialization();
        generateEvents();
        screenWidth();
        Event_Listners();
    }

    private void Event_Listners() {
        if (AppUtills.isNetworkAvailable(Customer_List_Activity.this)) {

            final AlertDialog progressDialog = new SpotsDialog(Customer_List_Activity.this);
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


                                     customer_pojo = new Customer_POJO(
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

                            customer_listAdapter = new Customer_ListAdapter(Customer_List_Activity.this, Customer_List_Activity.this, customer_pojoArrayList);
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

    private void initialization() {
        getSupportActionBar().setTitle(getResources().getString(R.string.customer_page));
        customer_name = (TextView) findViewById(R.id.customer_name);
        swipeLayout = (SwipeLayout)findViewById(R.id.swipe_layout);

        address = (TextView) findViewById(R.id.address);
        mobile = (TextView) findViewById(R.id.mobile);
        customer_list = (ListView) findViewById(R.id.customer_list);
        add_cust = (FloatingActionButton) findViewById(R.id.add_cust);
        database = FirebaseInstance.getInstance();
        databaseReference=database.getReference(getString(R.string.app_name)).child(AppUtills.getUserName(Customer_List_Activity.this)).child(getString(R.string.customer_page));
        customer_pojoArrayList = new ArrayList<Customer_POJO>();
        balanceAL = new ArrayList<String>();
        cans_with_custAL = new ArrayList<String>();
    }

    private void generateEvents() {
    customer_list.setOnItemClickListener(Customer_List_Activity.this);
        add_cust.setOnClickListener(this);
//        btnDelete.setOnClickListener(this);
//        btnEdit.setOnClickListener(this);

    }

    private void screenWidth() {
        height = getResources().getDisplayMetrics().heightPixels;
        width = getResources().getDisplayMetrics().widthPixels;

        customer_name.setWidth((int) (width/2.7));
        address.setWidth((int) (width/3.6));
        mobile.setWidth((int) (width/2));



    }

    @Override
    public void onClick(View v) {

        switch (v.getId()){
            case R.id.add_cust:
                Intent intenttoAdd = new Intent(Customer_List_Activity.this, Add_Edit_Customer_Activity.class);
                intenttoAdd.putExtra(getString(R.string.selectedOperationSpelling),getString(R.string.add_customer));
                startActivity(intenttoAdd);
                AppUtills.giveIntentEffect(this);
                break;
            case R.id.delete:
                Toast.makeText(this,"edit",Toast.LENGTH_SHORT).show();
                break;

            case R.id.edit_query1:
                Toast.makeText(this,"edit",Toast.LENGTH_SHORT).show();
                break;
        }

    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        SelectedPosition=position;
        if (customer_listAdapter!=null) {
            customer_listAdapter.set_Selection(position);
            customer_listAdapter.notifyDataSetChanged();
        }
//        Log.d(TAG,"Aman key1=="+customer_pojoArrayList.get(SelectedPosition).push_key);
        Customer_POJO pojo = customer_pojoArrayList.get(position);
        Snackbar.make(view, "You have selected:- "+pojo.customer_name, Snackbar.LENGTH_LONG).setAction("No action", null).show();
        /*Snackbar snackbar = null;
        snackbar.make(view, "You have selected:- "+pojo.customer_name, Snackbar.LENGTH_LONG).setAction("No action", null);
        snackbar.setActionTextColor(getResources().getColor(R.color.colorPrimaryDark));
        snackbar.show();*/
        cust_name_string=customer_pojoArrayList.get(SelectedPosition).customer_name;
//        Log.d(TAG,"Aman Chk Name=="+cust_name_string);
        databaseReference_orders = database.getReference(getString(R.string.app_name)).child(AppUtills.getUserName(Customer_List_Activity.this)).
                child(getString(R.string.orders_page)).child(cust_name_string);
        btnEdit =  view.findViewById(R.id.edit_query1);
        btnDelete = view.findViewById(R.id.delete);

        btnEdit.setOnClickListener(onEditListener(position, view));
    }

private View.OnClickListener onEditListener(final int position, final View view) {


        return new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                qw(position,view);
//                Toast.makeText(Customer_List_Activity.this,"edit",Toast.LENGTH_SHORT).show();

            }
        };
    }

    private void qw(int position, View view) {
        Toast.makeText(Customer_List_Activity.this,"edit1",Toast.LENGTH_SHORT).show();
        Log.d(TAG,"Aman fbcsdebcvf");
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_with_allicons,menu);


        return super.onCreateOptionsMenu(menu);
    }

    @Override
    protected void onResume(){

        super.onResume();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
           /* case R.id.item_add:
                Intent intenttoAdd = new Intent(Customer_List_Activity.this, Add_Edit_Customer_Activity.class);
                intenttoAdd.putExtra(getString(R.string.selectedOperationSpelling),getString(R.string.add_customer));
                startActivity(intenttoAdd);

                break;*/
            case R.id.item_edit:
                if (SelectedPosition!=-1){
                  Intent intenttoEdit = new Intent(Customer_List_Activity.this,Add_Edit_Customer_Activity.class);
                    intenttoEdit.putExtra(getString(R.string.parcelableValueSpelling),customer_pojoArrayList.get(SelectedPosition));
                    intenttoEdit.putExtra(getString(R.string.selectedOperationSpelling),getString(R.string.edit_customer));
                    intenttoEdit.putExtra("pushKey",customer_pojoArrayList.get(SelectedPosition).push_key);
                    startActivity(intenttoEdit);
                    AppUtills.giveIntentEffect(this);
                    SelectedPosition =-1;
                }
                else{
                    Snackbar.make(findViewById(android.R.id.content), getResources().getString(R.string.toastAtLeastOneCustomerShouldbeSelected), Snackbar.LENGTH_LONG)
                            .setActionTextColor(getResources().getColor(R.color.red_ToastColor))
                            .show();
                }

                break;
            case R.id.item_delete:
                if (SelectedPosition!=-1) {
                    deleteDialog();
                }else{
                    Snackbar.make(findViewById(android.R.id.content), getResources().getString(R.string.toastAtLeastOneCustomerShouldbeSelected), Snackbar.LENGTH_LONG)
                            .setActionTextColor(getResources().getColor(R.color.red_ToastColor))
                            .show();
                }

                break;
            case R.id.item_back:
                finish();
                break;
            case R.id.item_search:
//                refreshMenuItem.expandActionView();
                break;
        }

        return super.onOptionsItemSelected(item);
    }

    private void deleteDialog() {
        final AlertDialog.Builder builder=new AlertDialog.Builder(Customer_List_Activity.this);
        builder.setMessage("Are You Sure You want  to delete "+customer_pojoArrayList.get(SelectedPosition).customer_name+"?");
        builder.setTitle("Delete Customer");
        builder.setIcon(ContextCompat.getDrawable(Customer_List_Activity.this,R.drawable.warning));
        builder.setPositiveButton(getString(R.string.yes_Spelling), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {

                if (AppUtills.isNetworkAvailable(Customer_List_Activity.this)) {
                    DeleteListners();
                    customer_pojoArrayList.remove(SelectedPosition);
                    SelectedPosition = -1;
                    customer_listAdapter.notifyDataSetChanged();

                }else{
                    Toast.makeText(Customer_List_Activity.this,getString(R.string.CheckYourInternetConnection),Toast.LENGTH_SHORT).show();
                }

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

    private void DeleteListners() {

        databaseReference.child(customer_pojoArrayList.get(SelectedPosition).push_key).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                dataSnapshot.getRef().removeValue();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });

    }

    private void check_cust_balance() {

    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        AppUtills.givefinishEffect(this);
        finish();
    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {

    }

    @Override
    public void afterTextChanged(Editable editable) {
        if (customer_listAdapter!=null) {

            if (!editable.toString().equals("")) {
                customer_listAdapter.getFilter().filter(editable.toString());
            }else
            {
                customer_listAdapter = new Customer_ListAdapter(Customer_List_Activity.this, Customer_List_Activity.this, customer_pojoArrayList);
                customer_list.setAdapter(customer_listAdapter);
                customer_listAdapter.notifyDataSetChanged();
            }
        }
    }

}
