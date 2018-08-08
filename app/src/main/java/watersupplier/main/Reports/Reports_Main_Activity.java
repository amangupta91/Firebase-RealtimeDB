package watersupplier.main.Reports;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v7.widget.CardView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;

import watersupplier.main.Adapter.Reports_ListAdapter;
import watersupplier.main.Main.Common_ActionBar_Abstract;
import watersupplier.main.Main.First_Menu;
import watersupplier.main.R;
import watersupplier.main.Utils.AppUtills;

/**
 * Created by Aman Gupta on 30/3/17.
 */

public class Reports_Main_Activity extends Common_ActionBar_Abstract implements AdapterView.OnItemClickListener{

    //UI Variable
    private ListView reports_listView;
    private Reports_ListAdapter reports_listAdapter;

    //Non-UI variable

    private ArrayList<String> menu_namesAL1;
    private ArrayList<Integer> menu_iconAL1;
    private ArrayList<String> detailsAL1;
    private int SelectedPosition=-1;
    private FirebaseDatabase database;
    private DatabaseReference databaseReference;
    private String TAG = "Reports_Main_Activity";
    private Bundle savedInstanceState;

    @Override
    protected void onCreate( Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
            setContentView(R.layout.activity_reports_main);
            initialization();
            generateEvents();
            screenWidth();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_with_back, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case R.id.item_back:
               finish();
                break;


        }

        return super.onOptionsItemSelected(item);

    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        AppUtills.givefinishEffect(this);
        finish();
    }

    private void initialization() {
        getSupportActionBar().setTitle(R.string.report_page);
        reports_listView = (ListView) findViewById(R.id.reports_list);
        menu_iconAL1 = new ArrayList<Integer>();
        menu_namesAL1 = new ArrayList<String>();
        detailsAL1= new ArrayList<String>();

    }

    @Override
    protected void onResume() {
        super.onResume();
        addMenuIcons();
        reports_listAdapter = new Reports_ListAdapter(Reports_Main_Activity.this,Reports_Main_Activity.this,menu_namesAL1,menu_iconAL1,detailsAL1);
            reports_listView.setAdapter(reports_listAdapter);
            reports_listAdapter.notifyDataSetChanged();
            database = FirebaseDatabase.getInstance();
    }

    private void addMenuIcons() {
        menu_iconAL1.clear();
        menu_namesAL1.clear();
        detailsAL1.clear();

        menu_iconAL1.add(R.drawable.cans_in_market);
        menu_namesAL1.add(getString(R.string.cans_in_market));
        detailsAL1.add("click to know number of cans is out in market..");

        menu_iconAL1.add(R.drawable.cans_expected_today);
        menu_namesAL1.add(getString(R.string.cans_expected_today));
        detailsAL1.add("click to know number of cans arriving for the day..");

        menu_iconAL1.add(R.drawable.customer_account_statement);
        menu_namesAL1.add(getString(R.string.customer_account_statement));
        detailsAL1.add("click to know detailed credit & debit amount for customer ..");

        menu_iconAL1.add(R.drawable.sales_report);
        menu_namesAL1.add(getString(R.string.sales));
        detailsAL1.add("click to know sales report..");



    }

    private void generateEvents() {
            reports_listView.setOnItemClickListener(this);
    }

    private void screenWidth() {
    }


    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        SelectedPosition=position;
        if(SelectedPosition==0){
            Intent intent = new Intent(Reports_Main_Activity.this,Cans_in_Market_SearchActivity.class);
            startActivity(intent);
            AppUtills.giveIntentEffect(this);
        }
        else if(SelectedPosition==1){
            Intent intent = new Intent(Reports_Main_Activity.this,Cans_ExpectedToday_SearchActivity.class);
            startActivity(intent);
            AppUtills.giveIntentEffect(this);
        }
        else if(SelectedPosition==2){
            Intent intent = new Intent(Reports_Main_Activity.this,Customer_Account_SearchActivity.class);
            startActivity(intent);
            AppUtills.giveIntentEffect(this);
        }
        else if(SelectedPosition==3){
            Intent intent = new Intent(Reports_Main_Activity.this,Sales_SearchActivity.class);
            startActivity(intent);
            AppUtills.giveIntentEffect(this);
        }


    }
}
