package watersupplier.main.Reports;

import android.animation.ArgbEvaluator;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.app.AlertDialog;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
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

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;

import dmax.dialog.SpotsDialog;
import watersupplier.main.Adapter.Cans_in_Market_ListAdapter;
import watersupplier.main.Firsebase_Operations.FirebaseInstance;
import watersupplier.main.Main.Common_ActionBar_Abstract;
import watersupplier.main.Orders.Order_Search_ListActivity;
import watersupplier.main.Pojo.Customer_POJO;
import watersupplier.main.Pojo.Orders_POJO;
import watersupplier.main.R;
import watersupplier.main.Utils.AppUtills;

import static watersupplier.main.R.id.customer_name_val;

/**
 * Created by Aman Gupta on 11/4/17.
 */

public class Cans_in_Market_ListActivity extends Common_ActionBar_Abstract implements View.OnClickListener{
    //UI variables
    private TextView date,date_val,total_cans,total_cans_val,customer_name,issue_date,quantity;
    private ListView market_cans_list;


    //Non UI variables
    private ObjectAnimator textColorAnim1,textColorAnim2;
    private int width,height;
    private Cans_in_Market_ListAdapter adapter;
    private ArrayList<Orders_POJO> getAL;
    private String TAG = "Cans_in_Market_ListActivity";


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        
        setContentView(R.layout.activity_cans_in_market_list);
        initialization();
        generateEvents();
//        Event_Listners();
        screenWidth();
        setListAdapter();
        sortAsPerCustomer();
        super.onCreate(savedInstanceState);
    }


    private void sortAsPerCustomer() {

        Collections.sort(getAL,new ChainedComparator(new Customer_comparator()));
    }

    private void setListAdapter() {

        adapter = new Cans_in_Market_ListAdapter(Cans_in_Market_ListActivity.this, Cans_in_Market_ListActivity.this, getAL,null,null);
        market_cans_list.setAdapter(adapter);
        adapter.notifyDataSetChanged();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }

    private void generateEvents() {
    }

    private void initialization() {

        getSupportActionBar().setTitle(getResources().getString(R.string.cans_in_market));
        date = (TextView) findViewById(R.id.date);
        date_val = (TextView) findViewById(R.id.date_val);
        total_cans = (TextView) findViewById(R.id.total_cans);
        total_cans_val = (TextView) findViewById(R.id.total_cans_val);
        customer_name = (TextView) findViewById(R.id.customer_name);
        issue_date = (TextView) findViewById(R.id.issue_date);
        quantity = (TextView) findViewById(R.id.quantity);
        market_cans_list = (ListView) findViewById(R.id.market_cans_list);

        textColorAnim1 = ObjectAnimator.ofInt(total_cans, "textColor",getResources().getColor(R.color.red_ToastColor),getResources().getColor(R.color.colorPrimaryDark));
        textColorAnim1.setDuration(800);
        textColorAnim1.setEvaluator(new ArgbEvaluator());
        textColorAnim1.setRepeatCount(ValueAnimator.INFINITE);
        textColorAnim1.setRepeatMode(ValueAnimator.REVERSE);
        textColorAnim1.start();

        textColorAnim2 = ObjectAnimator.ofInt(total_cans_val, "textColor",getResources().getColor(R.color.red_ToastColor),getResources().getColor(R.color.colorPrimaryDark));
        textColorAnim2.setDuration(800);
        textColorAnim2.setEvaluator(new ArgbEvaluator());
        textColorAnim2.setRepeatCount(ValueAnimator.INFINITE);
        textColorAnim2.setRepeatMode(ValueAnimator.REVERSE);
        textColorAnim2.start();


        getAL=new ArrayList<Orders_POJO>();
        Bundle data=getIntent().getExtras();

        getAL.clear();
        getAL=data.getParcelableArrayList("orderAl");

        int TotalCans=0;

        for (int i = 0; i < getAL.size(); i++) {

            TotalCans = TotalCans+Integer.parseInt(getAL.get(i).cans_with_customer.toString());

        }
        total_cans.setText(getString(R.string.cans_in_market)+" :");
        total_cans_val.setText(TotalCans+"");

        date_val.setText(getIntent().getStringExtra("selected_date".toString()));
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_with_back,menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case R.id.item_back:
                AppUtills.givefinishEffect(this);
                finish();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    private void screenWidth() {
        height = getResources().getDisplayMetrics().heightPixels;
        width = getResources().getDisplayMetrics().widthPixels;

        customer_name.setWidth((int) (width/2.2));
        issue_date.setWidth((int) (width/2.9));
        quantity.setWidth((int) (width/2.5));
    }

    @Override
    public void onClick(View v) {

    }
}
