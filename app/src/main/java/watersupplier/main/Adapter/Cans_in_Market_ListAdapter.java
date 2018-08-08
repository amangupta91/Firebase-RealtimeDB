package watersupplier.main.Adapter;

import android.content.Context;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.HashMap;

import watersupplier.main.Customer.Customer_List_Activity;
import watersupplier.main.Orders.Order_Search_ListActivity;
import watersupplier.main.Pojo.Customer_POJO;
import watersupplier.main.Pojo.Orders_POJO;
import watersupplier.main.R;
import watersupplier.main.Reports.Cans_ExpectedToday_ListActivity;
import watersupplier.main.Reports.Cans_in_Market_ListActivity;
import watersupplier.main.Reports.Customer_Account_ListActivity;
import watersupplier.main.Reports.Sales_ListActivity;

/**
 * Created by Aman Gupta on 11/4/17.
 */

public class Cans_in_Market_ListAdapter extends BaseAdapter{


    private ArrayList<Orders_POJO> order_pojoAL;
    private Object instanceOf;
    private Context context;
    private View view;
    private int width;
    private String TAG = "Cans_in_Market_ListAdapter";
    private int selectedIndex = -1;
    private ArrayList<Orders_POJO> CloneAL;
    private HashMap<String, ArrayList<String>> order_map;
    private ArrayList<String> dateAL;
//    private Order_ListAdapter.ValueFilter valueFilter;


    public Cans_in_Market_ListAdapter(Context context, Object instanceOf, ArrayList<Orders_POJO> order_pojoAL, HashMap<String, ArrayList<String>> order_map,ArrayList<String> dateAL) {
        width = context.getResources().getDisplayMetrics().widthPixels;
        this.context = context;
        this.order_pojoAL = order_pojoAL;
        this.order_map = order_map;
        this.dateAL = dateAL;
        this.instanceOf = instanceOf;

        CloneAL = new ArrayList<>();
//        order_map = new HashMap<>();
        CloneAL.addAll(this.order_pojoAL);
//        getFilter();

    }
    @Override
    public int getCount() {
        if (instanceOf != null && instanceOf instanceof Cans_in_Market_ListActivity) {
//            return customer_pojoAL.size();
        }
        return order_pojoAL.size();
    }

    @Override
    public Object getItem(int position) {
        return null;
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    public void set_Selection(int selectedItem) {
        this.selectedIndex = selectedItem;
    }


    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        View row = view;
        Cans_in_Market_ListAdapter.ViewHolder holder = null;
        int total_new_cans=0;
        ArrayList<String> CansAL = new ArrayList<String>();

        if (view == null) {
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(context.LAYOUT_INFLATER_SERVICE);
            row = inflater.inflate(R.layout.single_row_for_customer, parent, false);

            holder = new Cans_in_Market_ListAdapter.ViewHolder(row);
            row.setTag(holder);
        } else {
            holder = (Cans_in_Market_ListAdapter.ViewHolder) row.getTag();
        }

       /* if (position % 2 == 0) {
            row.setBackgroundColor(ContextCompat.getColor(context, R.color.fab_stroke_end_outer_color));
        } else {
            row.setBackgroundColor(ContextCompat.getColor(context, R.color.fab_stroke_top_inner_color));

        }*/
//        highlightItem(position, row, holder);
        holder.txt_customer_name1.setWidth((int) (width /2.2));
        holder.return_date.setWidth((int) (width /2.5));
        holder.quantity.setWidth((int) (width/2.9));

        if (instanceOf != null && instanceOf instanceof Cans_in_Market_ListActivity) {

            if (context.getResources().getConfiguration().orientation == context.getResources().getConfiguration().ORIENTATION_PORTRAIT) {
                holder.txt_customer_name1.setText(order_pojoAL.get(position).customer_name+"");
                holder.return_date.setText(order_pojoAL.get(position).cans_with_customer+"");
                holder.quantity.setText(order_pojoAL.get(position).order_date+"");

//                Log.d(TAG,"Aman CHK CONTENT=="+order_pojoAL.get(position).customer_name+"");
            }
        }

        else if (instanceOf != null && instanceOf instanceof Cans_ExpectedToday_ListActivity) {

            if (context.getResources().getConfiguration().orientation == context.getResources().getConfiguration().ORIENTATION_PORTRAIT) {
                holder.txt_customer_name1.setText(order_pojoAL.get(position).customer_name+"");
                holder.return_date.setText(order_pojoAL.get(position).cans_with_customer+"");
                holder.quantity.setText(order_pojoAL.get(position).mobile+"");

//                Log.d(TAG,"Aman CHK CONTENT=="+order_pojoAL.get(position).customer_name+"");
            }
        }

        else if (instanceOf != null && instanceOf instanceof Customer_Account_ListActivity) {

            if (context.getResources().getConfiguration().orientation == context.getResources().getConfiguration().ORIENTATION_PORTRAIT) {

                holder.txt_customer_name1.setWidth((int) (width /2.5));
                holder.return_date.setWidth((int) (width /2.8));
                holder.quantity.setWidth((int) (width/2.8));
                holder.txt_customer_name1.setText(order_pojoAL.get(position).order_date+"");
                holder.return_date.setText(order_pojoAL.get(position).total_amount+"");
                holder.quantity.setText(order_pojoAL.get(position).money_received+"");

//                Log.d(TAG,"Aman CHK CONTENT=="+order_pojoAL.get(position).customer_name+"");
            }
        }

        else if (instanceOf != null && instanceOf instanceof Sales_ListActivity) {

            if (context.getResources().getConfiguration().orientation == context.getResources().getConfiguration().ORIENTATION_PORTRAIT) {
                holder.quantity.setVisibility(view.GONE);
                holder.txt_customer_name1.setWidth((int) (width /2));
                holder.return_date.setWidth((int) (width /2));

                holder.txt_customer_name1.setText(order_pojoAL.get(position).order_date+"");
                holder.return_date.setText(order_pojoAL.get(position).new_cans_given+"");

//                Log.d(TAG,"Aman CHK CONTENT=="+order_pojoAL.get(position).customer_name+" & value: "+order_pojoAL.get(position).new_cans_given+"");
            }
        }

       /* if (position == selectedIndex) {
            row.setBackgroundColor(context.getResources().getColor(R.color.red_ToastColor));
        }*/
        Animation animation = AnimationUtils.loadAnimation(context, (position > selectedIndex) ? R.anim.down_from_top : R.anim.up_from_bottom);
        row.startAnimation(animation);


        return row;
}


    public class ViewHolder {
        private TextView txt_customer_name1,return_date,quantity;

        public ViewHolder(View view) {
            txt_customer_name1 = (TextView) view.findViewById(R.id.txt_customer_name1);
            return_date = (TextView) view.findViewById(R.id.txt_mobile1);
            quantity = (TextView) view.findViewById(R.id.txt_address1);

            txt_customer_name1.setGravity(Gravity.CENTER);
            return_date.setGravity(Gravity.CENTER);
            quantity.setGravity(Gravity.CENTER);

        }
    }
}
