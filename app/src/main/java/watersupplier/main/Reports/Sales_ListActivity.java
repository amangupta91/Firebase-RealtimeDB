package watersupplier.main.Reports;

import android.animation.ArgbEvaluator;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.HashMap;

import watersupplier.main.Adapter.Cans_in_Market_ListAdapter;
import watersupplier.main.Main.Common_ActionBar_Abstract;
import watersupplier.main.Pojo.Orders_POJO;
import watersupplier.main.R;
import watersupplier.main.Utils.AppUtills;

/**
 * Created by Aman Gupta on 5/5/17.
 */

public class Sales_ListActivity extends Common_ActionBar_Abstract {

    //UI variables
    private TextView customer_name,total_cans_sold,total_cans_sold_val,from_date,to_date,date_header,credit,debit;
    private Button btn_print;
    private ListView sales_list;

    //Non UI variables
    private ObjectAnimator textColorAnim1,textColorAnim2;
    private int width,height;
    private View view;
    private Cans_in_Market_ListAdapter adapter;
    private ArrayList<Orders_POJO> getAL;
    private String TAG = "Sales_ListActivity";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_customer_statement_list);
        initialization();
        generateEvents();
        screenWidth();
        setListAdapter();
        sortAsPerCustomer();
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
                this.finish();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    private void initialization() {
        getSupportActionBar().setTitle(getResources().getString(R.string.sales));
        customer_name = (TextView) findViewById(R.id.customer_name);
        total_cans_sold = (TextView) findViewById(R.id.total_outstanding);
//        total_cans_sold_val = (TextView) findViewById(R.id.total_outstanding_val);
        from_date = (TextView) findViewById(R.id.from_date);
        to_date = (TextView) findViewById(R.id.to_date);
        date_header = (TextView) findViewById(R.id.date_header);
        credit = (TextView) findViewById(R.id.credit);
        debit = (TextView) findViewById(R.id.debit);
        btn_print = (Button) findViewById(R.id.btn_print);
        sales_list = (ListView) findViewById(R.id.customer_account_list);

        textColorAnim1 = ObjectAnimator.ofInt(total_cans_sold, "textColor",getResources().getColor(R.color.red_ToastColor),getResources().getColor(R.color.colorPrimaryDark));
        textColorAnim1.setDuration(800);
        textColorAnim1.setEvaluator(new ArgbEvaluator());
        textColorAnim1.setRepeatCount(ValueAnimator.INFINITE);
        textColorAnim1.setRepeatMode(ValueAnimator.REVERSE);
        textColorAnim1.start();

        textColorAnim2 = ObjectAnimator.ofInt(total_cans_sold_val, "textColor", getResources().getColor(R.color.red_ToastColor),getResources().getColor(R.color.colorPrimaryDark));
        textColorAnim2.setDuration(800);
        textColorAnim2.setEvaluator(new ArgbEvaluator());
        textColorAnim2.setRepeatCount(ValueAnimator.INFINITE);
        textColorAnim2.setRepeatMode(ValueAnimator.REVERSE);
        textColorAnim2.start();

        getAL=new ArrayList<Orders_POJO>();
        Bundle data=getIntent().getExtras();

        getAL.clear();

        getAL=data.getParcelableArrayList("orderAl");

        btn_print.setVisibility(view.GONE);
        from_date.setVisibility(view.GONE);
        credit.setVisibility(view.GONE);
        debit.setText(getString(R.string.number_of_cans));


        customer_name.setText(getResources().getString(R.string.from_date)+"   : "+getIntent().getStringExtra("from_date").toString());
        to_date.setText(getResources().getString(R.string.to_date)+"   : "+getIntent().getStringExtra("to_date").toString());
        total_cans_sold.setText(getResources().getString(R.string.total_cans_sold)+" : ");
//        total_cans_sold_val.setText("");
    }

    private void generateEvents() {
    }

    private void screenWidth() {

        height = getResources().getDisplayMetrics().heightPixels;
        width = getResources().getDisplayMetrics().widthPixels;

        date_header.setWidth((int) (width/2));
//        credit.setWidth((int) (width/2.8));
        debit.setWidth((int) (width/2));
    }

    private void setListAdapter() {
        adapter = new Cans_in_Market_ListAdapter(Sales_ListActivity.this, Sales_ListActivity.this, getAL,null,null);
        sales_list.setAdapter(adapter);
        adapter.notifyDataSetChanged();
    }

    private void sortAsPerCustomer() {
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }
}
