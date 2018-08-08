package watersupplier.main.Reports;

import android.animation.ArgbEvaluator;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
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
import java.util.Collections;

import watersupplier.main.Adapter.Cans_in_Market_ListAdapter;
import watersupplier.main.Main.Common_ActionBar_Abstract;
import watersupplier.main.Pojo.Orders_POJO;
import watersupplier.main.R;
import watersupplier.main.Utils.AppUtills;

/**
 * Created by Aman Gupta on 24/4/17.
 */

public class Customer_Account_ListActivity extends Common_ActionBar_Abstract implements View.OnClickListener{
    //UI variables
    private TextView customer_name,total_outstanding,from_date,to_date,date_header,credit,debit;
    private Button btn_print;
    private ListView customer_account_list;

    //Non UI variables
    private ObjectAnimator textColorAnim1,textColorAnim2;
    private int width,height;
    private Cans_in_Market_ListAdapter adapter;
    private ArrayList<Orders_POJO> getAL;
    private String TAG = "Customer_Account_ListActivity";

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
        getSupportActionBar().setTitle(getResources().getString(R.string.customer_account_statement));
        customer_name = (TextView) findViewById(R.id.customer_name);
        total_outstanding = (TextView) findViewById(R.id.total_outstanding);
        from_date = (TextView) findViewById(R.id.from_date);
        to_date = (TextView) findViewById(R.id.to_date);
        date_header = (TextView) findViewById(R.id.date_header);
        credit = (TextView) findViewById(R.id.credit);
        debit = (TextView) findViewById(R.id.debit);
        btn_print = (Button) findViewById(R.id.btn_print);
        customer_account_list = (ListView) findViewById(R.id.customer_account_list);

        textColorAnim1 = ObjectAnimator.ofInt(total_outstanding, "textColor",getResources().getColor(R.color.red_ToastColor),getResources().getColor(R.color.colorPrimaryDark));
        textColorAnim1.setDuration(800);
        textColorAnim1.setEvaluator(new ArgbEvaluator());
        textColorAnim1.setRepeatCount(ValueAnimator.INFINITE);
        textColorAnim1.setRepeatMode(ValueAnimator.REVERSE);
        textColorAnim1.start();

        getAL=new ArrayList<Orders_POJO>();
        Bundle data=getIntent().getExtras();

        getAL.clear();
        getAL=data.getParcelableArrayList("orderAl");

        customer_name.setText(getResources().getString(R.string.customer_name_spelling)+" : "+getIntent().getStringExtra("cust_name").toString());
        from_date.setText(getResources().getString(R.string.from_date)+"   : "+getIntent().getStringExtra("from_date").toString());
        to_date.setText(getResources().getString(R.string.to_date)+"   : "+getIntent().getStringExtra("to_date").toString());
        total_outstanding.setText(getString(R.string.total_outstanding)+" :"+getIntent().getStringExtra("total_outstanding").toString());

    }

    private void generateEvents() {
        btn_print.setOnClickListener(this);
    }

    private void screenWidth() {
        height = getResources().getDisplayMetrics().heightPixels;
        width = getResources().getDisplayMetrics().widthPixels;

        date_header.setWidth((int) (width/2.5));
        credit.setWidth((int) (width/2.8));
        debit.setWidth((int) (width/2.8));
    }

    private void setListAdapter() {

        adapter = new Cans_in_Market_ListAdapter(Customer_Account_ListActivity.this, Customer_Account_ListActivity.this, getAL,null,null);
        customer_account_list.setAdapter(adapter);
        adapter.notifyDataSetChanged();
    }

    private void sortAsPerCustomer() {
//        Collections.sort(getAL,new ChainedComparator(new Customer_comparator()));
    }

    @Override
    public void onClick(View v) {

    }
}
