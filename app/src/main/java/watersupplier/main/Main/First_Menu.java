package watersupplier.main.Main;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;

import watersupplier.main.Configuration.Configuration_Main_Activity;
import watersupplier.main.Customer.Customer_List_Activity;
import watersupplier.main.Generic.FacebookLogin;
import watersupplier.main.Generic.Login_Activity;
import watersupplier.main.Generic.SignUp_Activity;
import watersupplier.main.Orders.Order_Search_ListActivity;
import watersupplier.main.Pojo.First_Menu_POJO;
import watersupplier.main.Product.Product_Setup;
import watersupplier.main.R;
import watersupplier.main.Reports.Reports_Main_Activity;
import watersupplier.main.Utils.AppUtills;

public class First_Menu extends Common_ActionBar_Abstract implements View.OnClickListener, PopupMenu.OnMenuItemClickListener {

    //UI variable
    private GridView gridView;
    private RelativeLayout product_layout, orders_layout;
    private ImageView orders, products, cutomers, config, reports;
    private FacebookLogin facebookLogin;


    //NonUi variable
    private ArrayList<String> menuNameAL;
    private ArrayList<Integer> menuIconsAl;
    private ArrayList<First_Menu_POJO> arraylist;
    private int width, height;
    private String TAG = "First_Menu";

    //firebase
    private FirebaseDatabase Database;
    private DatabaseReference mDatabase;
    public FirebaseApp myApp = null;
    private FirebaseOptions firebaseOptions = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setContentView(R.layout.activity_first_menu);
        getSupportActionBar().setIcon(R.drawable.delete);
        getSupportActionBar().setDisplayHomeAsUpEnabled(false);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.delete);
        initialization();
        generateEvents();
        screenWidth();
//        setHomeScreenInstance(First_Menu.this);

        super.onCreate(savedInstanceState);

    }


    private void initialization() {
        facebookLogin = new FacebookLogin(First_Menu.this);
        products = (ImageView) findViewById(R.id.products);
        orders = (ImageView) findViewById(R.id.orders);
        cutomers = (ImageView) findViewById(R.id.cutomers);
        config = (ImageView) findViewById(R.id.config);
        reports = (ImageView) findViewById(R.id.reports);


        menuIconsAl = new ArrayList<Integer>();
        menuNameAL = new ArrayList<String>();

        menuIconsAl.add(R.drawable.product);
        menuNameAL.add(R.string.product_page + "");

        menuIconsAl.add(R.drawable.customer);
        menuNameAL.add(R.string.customer_page + "");

        menuIconsAl.add(R.drawable.order);
        menuNameAL.add(R.string.orders_page + "");

        menuIconsAl.add(R.drawable.configuration);
        menuNameAL.add(R.string.configuration_page + "");

        menuIconsAl.add(R.drawable.report);
        menuNameAL.add(R.string.report_page + "");
    }

    private void generateEvents() {
        orders.setOnClickListener(First_Menu.this);
        products.setOnClickListener(First_Menu.this);
        cutomers.setOnClickListener(First_Menu.this);
        config.setOnClickListener(First_Menu.this);
        reports.setOnClickListener(this);
    }

    private void screenWidth() {


        width = getResources().getDisplayMetrics().widthPixels;
        height = getResources().getDisplayMetrics().heightPixels;

        if (getResources().getConfiguration().orientation == getResources().getConfiguration().ORIENTATION_PORTRAIT) {
            products.setMinimumWidth(width);
            products.setMaxHeight(height);
            orders.setMaxWidth(width);
            orders.setMaxHeight(height);

        }


    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu, menu);

//        logoutBtn.setOnClickListener(FirstMenu.this);
//
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        View menuItemView = findViewById(R.id.action_logout_btn);

        switch (item.getItemId()) {

            case R.id.action_logout_btn:
                PopupMenu popupMenu = new PopupMenu(First_Menu.this, menuItemView);
                popupMenu.setOnMenuItemClickListener(First_Menu.this);
                popupMenu.inflate(R.menu.popup_menu_exit);
                popupMenu.show();

                break;

        }


        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.products:
                Intent intentToProduct = new Intent(First_Menu.this, Product_Setup.class);
                startActivity(intentToProduct);
                AppUtills.giveIntentEffect(this);
                break;
            case R.id.cutomers:
                Intent intentToCust = new Intent(First_Menu.this, Customer_List_Activity.class);
                startActivity(intentToCust);
                AppUtills.giveIntentEffect(this);
                break;
            case R.id.orders:
                Intent intentToOrders = new Intent(First_Menu.this, Order_Search_ListActivity.class);
                startActivity(intentToOrders);
                AppUtills.giveIntentEffect(this);
                break;
            case R.id.config:
                Intent intentToConfig = new Intent(First_Menu.this, Configuration_Main_Activity.class);
                startActivity(intentToConfig);
                AppUtills.giveIntentEffect(this);
                break;
            case R.id.reports:
                Intent intentToReport = new Intent(First_Menu.this, Reports_Main_Activity.class);
                startActivity(intentToReport);
                AppUtills.giveIntentEffect(this);
                break;

        }
    }

    @Override
    public void onBackPressed() {
//        super.onBackPressed();
        closeDialog();

//        finish();
    }

    private void closeDialog() {
        AlertDialog.Builder builder1 = new AlertDialog.Builder(First_Menu.this);
        builder1.setMessage("Do you want to exit?");
        builder1.setTitle("Exit Alert");
        builder1.setIcon(R.drawable.exit);
        builder1.setCancelable(false);

        builder1.setPositiveButton(
                "Yes", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
//                        First_Menu.super.onBackPressed();
                        AppUtills.givefinishEffect(First_Menu.this);
                        java.lang.System.exit(0);

                    }


                });

        builder1.setNegativeButton(
                "No", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                    }
                });

        AlertDialog alert11 = builder1.create();
        alert11.show();
    }

    @Override
    public boolean onMenuItemClick(MenuItem item) {
        View menuItemView = findViewById(R.id.action_logout_btn);

        switch (item.getItemId()) {

            case R.id.item_LogOut:
                alertDialog();
                break;
            case R.id.item_close:
                finish();
                break;


        }
        return false;
    }

    private void alertDialog() {
        AlertDialog.Builder builder1 = new AlertDialog.Builder(First_Menu.this);
        builder1.setMessage("Do you Really want to logout?");
        builder1.setTitle("Logout Profile");
        builder1.setIcon(R.drawable.warning);
        builder1.setCancelable(true);

        builder1.setPositiveButton(
                "Yes", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        logout_actions();
                    }

                    private void logout_actions() {
                        //login
                        SharedPreferences sharedPreferences = getSharedPreferences("Login", Context.MODE_PRIVATE);
                        SharedPreferences.Editor editor = sharedPreferences.edit();
                        editor.remove(getResources().getString(R.string.isLogedIn_Spelling));
                        editor.remove(getResources().getString(R.string.isSignup_Spelling));
                        editor.commit();

                        SharedPreferences sharedPreferences2 = getSharedPreferences("Passcode", Context.MODE_PRIVATE);
                        SharedPreferences.Editor editor2 = sharedPreferences2.edit();
                        editor2.remove("isPasscodeSaved");
                        editor2.remove("passcode");
                        editor2.commit();

                        facebookLogin.signOut();
                        SharedPreferences sharedPreferences3 = getSharedPreferences("Login", Context.MODE_PRIVATE);
                        SharedPreferences.Editor editor3 = sharedPreferences2.edit();
                        editor3.remove("FBLoginID");
                        editor3.remove("FBLoginName");
                        editor3.remove("FBLoginEmail");
                        editor3.commit();

                Intent intentToLoginPage = new Intent(First_Menu.this, Login_Activity.class);
                startActivity(intentToLoginPage);
                finish();
                    }
                });

        builder1.setNegativeButton(
                "No", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                    }
                });

        AlertDialog alert11 = builder1.create();
        alert11.show();

    }



}