package watersupplier.main.Adapter;

import android.content.Context;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.TextView;
import android.widget.Toast;

import com.daimajia.swipe.SwipeLayout;

import java.util.ArrayList;

import watersupplier.main.Customer.Customer_List_Activity;
import watersupplier.main.Pojo.Customer_POJO;
import watersupplier.main.R;
import watersupplier.main.Reports.Customer_Account_SearchActivity;

/**
 * Created by Aman Gupta on 2/2/17.
 */

public class Customer_ListAdapter extends BaseAdapter implements Filterable {

    private ArrayList<Customer_POJO> customer_pojoAL;
    private Object instanceOf;
    private Context context;
    private View view;
    private int width;
    private String TAG = "Customer_ListAdapter";
    private int selectedIndex = -1;
    private ArrayList<Customer_POJO> CloneAL;
    private ValueFilter valueFilter;

    public Customer_ListAdapter(Context context, Object instanceOf, ArrayList<Customer_POJO> customer_pojoAL) {
        width = context.getResources().getDisplayMetrics().widthPixels;
        this.context = context;
        this.customer_pojoAL = customer_pojoAL;
        this.instanceOf = instanceOf;

        CloneAL = new ArrayList<>();
        CloneAL.addAll(this.customer_pojoAL);
        getFilter();

    }


    @Override
    public int getCount() {

        if (instanceOf != null && instanceOf instanceof Customer_List_Activity) {
//            return customer_pojoAL.size();
        }
        return customer_pojoAL.size();
    }

    @Override
    public Object getItem(int position) {
        if (instanceOf != null && instanceOf instanceof Customer_List_Activity) {

        }
        return customer_pojoAL.get(position);
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
        ViewHolder holder = null;

        if (view == null) {
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(context.LAYOUT_INFLATER_SERVICE);
            row = inflater.inflate(R.layout.single_row_for_customer, parent, false);
            holder = new ViewHolder(row);
            row.setTag(holder);
        } else {
            holder = (ViewHolder) row.getTag();
        }

       /* if (position % 2 == 0) {
            row.setBackgroundColor(ContextCompat.getColor(context, R.color.fab_stroke_end_outer_color));
        } else {
            row.setBackgroundColor(ContextCompat.getColor(context, R.color.fab_stroke_top_inner_color));

        }*/
//        highlightItem(position, row, holder);
        holder.txt_customer_name1.setWidth((int) (width /2.7));
        holder.txt_address1.setWidth((int) (width / 3.6));
        holder.txt_mobile1.setWidth((int) (width /2));

        if (instanceOf != null && instanceOf instanceof Customer_List_Activity) {

            if (context.getResources().getConfiguration().orientation == context.getResources().getConfiguration().ORIENTATION_PORTRAIT) {
                holder.txt_customer_name1.setText(customer_pojoAL.get(position).customer_name+"");
                holder.txt_address1.setText(customer_pojoAL.get(position).address+"");
                holder.txt_mobile1.setText(customer_pojoAL.get(position).mobile);
//                Log.d(TAG,"Aman CHK CONTENT"+customer_pojoAL.get(position).customer_name+"");
            }
        }
        else if (instanceOf != null && instanceOf instanceof Customer_Account_SearchActivity) {
            holder.txt_customer_name1.setWidth((int) (width/3.8));
            holder.txt_address1.setWidth((int) (width /3.4));
            holder.txt_mobile1.setWidth((int) (width /2));

            if (context.getResources().getConfiguration().orientation == context.getResources().getConfiguration().ORIENTATION_PORTRAIT) {
                holder.txt_customer_name1.setText(customer_pojoAL.get(position).customer_name+"");
                holder.txt_address1.setText(customer_pojoAL.get(position).address+"");
                holder.txt_mobile1.setText(customer_pojoAL.get(position).mobile);
//                Log.d(TAG,"Aman CHK CONTENT"+customer_pojoAL.get(position).customer_name+"");
            }
        }
        //handling buttons event
        holder.btnEdit.setOnClickListener(onEditListener(position, holder));
        holder.btnDelete.setOnClickListener(onDeleteListener(position, holder));

        if (position == selectedIndex) {
            row.setBackgroundColor(context.getResources().getColor(R.color.red_ToastColor));
        }
        Animation animation = AnimationUtils.loadAnimation(context, (position > selectedIndex) ? R.anim.up_from_bottom : R.anim.down_from_top);
        row.startAnimation(animation);


            return row;


    }

    private View.OnClickListener onDeleteListener(final int position, final ViewHolder holder) {
        return new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                showEditDialog(position, holder);
            }

            private void showEditDialog(int position, ViewHolder holder) {

                Toast.makeText(context,"delete",Toast.LENGTH_SHORT).show();
            }
        };
    }

    private View.OnClickListener onEditListener(final int position, final ViewHolder holder) {

        return new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                test(position,holder);

            }
        };
    }

    private void test(int position, ViewHolder holder) {
        Toast.makeText(context,"edit",Toast.LENGTH_SHORT).show();
    }

    private void highlightItem(int position, View result, ViewHolder holder) {

        if (position == selectedIndex) {

//            result.setBackgroundColor(context.getResources().getColor(R.color.red_ToastColor));
            holder.txt_customer_name1.setTextColor(context.getResources().getColor(R.color.Black));
            holder.txt_address1.setTextColor(context.getResources().getColor(R.color.Black));
            holder.txt_mobile1.setTextColor(context.getResources().getColor(R.color.Black));

        }

    }

    @Override
    public Filter getFilter() {
        if (valueFilter == null) {
            valueFilter = new ValueFilter();
        }
        return valueFilter;
    }

    public class ViewHolder {
        private TextView txt_customer_name1, txt_address1, txt_mobile1;
        private SwipeLayout swipeLayout;
        private View btnDelete;
        private View btnEdit;


        public ViewHolder(View view) {
            txt_customer_name1 = (TextView) view.findViewById(R.id.txt_customer_name1);
            txt_address1 = (TextView) view.findViewById(R.id.txt_address1);
            txt_mobile1 = (TextView) view.findViewById(R.id.txt_mobile1);
            swipeLayout = (SwipeLayout)view.findViewById(R.id.swipe_layout);
            btnDelete = view.findViewById(R.id.delete);
            btnEdit = view.findViewById(R.id.edit_query1);
//            verticalLine1 = (TextView) view.findViewById(R.id.verticalLine1);


            txt_customer_name1.setGravity(Gravity.CENTER);
            txt_address1.setGravity(Gravity.CENTER);
            txt_mobile1.setGravity(Gravity.CENTER);
            swipeLayout.setShowMode(SwipeLayout.ShowMode.PullOut);

        }
    }

    private class ValueFilter extends Filter {
        @Override
        protected FilterResults performFiltering(CharSequence constraint) {
            FilterResults results = new FilterResults();


            if (!constraint.toString().equals("")) {

                if (constraint != null && constraint.length() > 0) {
                    ArrayList<Customer_POJO> filterList = new ArrayList<Customer_POJO>();
                    for (int i = 0; i < customer_pojoAL.size(); i++) {
                        if ((customer_pojoAL.get(i).customer_name).contains(constraint.toString())||
                                customer_pojoAL.get(i).mobile.contains(constraint.toString())) {
                            Customer_POJO customer_pojo = new Customer_POJO();
                            customer_pojo.customer_name = customer_pojoAL.get(i).customer_name;
                            customer_pojo.address = customer_pojoAL.get(i).address;
                            customer_pojo.mobile = customer_pojoAL.get(i).mobile;
                            customer_pojo.customer_spPrice = customer_pojoAL.get(i).customer_spPrice;
                            customer_pojo.credit_line = customer_pojoAL.get(i).credit_line;

                            filterList.add(customer_pojo);
//                            Log.d(TAG,"Aman CHK AL22"+filterList.size());
                        }
                    }
                    results.count = filterList.size();
                    results.values = filterList;
                } else {
                    results.count = customer_pojoAL.size();
                    results.values = customer_pojoAL;
                }

                return results;
            } else {
                results.count = customer_pojoAL.size();
                results.values = customer_pojoAL;
                // publishResults("", results);
                return results;
            }
        }

        @Override
        protected void publishResults(CharSequence constraint, FilterResults results) {


            if (results!=null){
                customer_pojoAL=(ArrayList<Customer_POJO>) results.values;

            }else{
                customer_pojoAL=CloneAL;
            }


            notifyDataSetChanged();
        }

    }
}
