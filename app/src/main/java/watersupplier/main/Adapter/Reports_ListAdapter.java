package watersupplier.main.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

import watersupplier.main.R;
import watersupplier.main.Reports.Reports_Main_Activity;

/**
 * Created by Aman Gupta on 30/3/17.
 */

public class Reports_ListAdapter extends BaseAdapter{

    private Object instanceOf;
    private Context context;
    private View view;
    private ArrayList<String> menu_namesAL1;
    private ArrayList<Integer> menu_iconAL1;
    private ArrayList<String> detailsAL1;

    public Reports_ListAdapter(Context context,Object instanceOf, ArrayList<String> menu_namesAL1,ArrayList<Integer> menu_iconAL1,ArrayList<String> detailsAL1) {
        this.context = context;
        this.instanceOf = instanceOf;
        this.menu_namesAL1 = menu_namesAL1;
        this.menu_iconAL1 = menu_iconAL1;
        this.detailsAL1 = detailsAL1;
    }
    @Override
    public int getCount() {
        return menu_iconAL1.size();
    }

    @Override
    public Object getItem(int position) {
        return null;
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        View row = view;
        Reports_ListAdapter.ViewHolder holder = null;

        if (view == null) {
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(context.LAYOUT_INFLATER_SERVICE);
            row = inflater.inflate(R.layout.single_row_for_reports, parent, false);

            holder = new Reports_ListAdapter.ViewHolder(row);
            row.setTag(holder);
        } else {
            holder = (Reports_ListAdapter.ViewHolder) row.getTag();
        }

        if (instanceOf != null && instanceOf instanceof Reports_Main_Activity) {

            if (context.getResources().getConfiguration().orientation == context.getResources().getConfiguration().ORIENTATION_PORTRAIT) {
                holder.menu_names.setText(menu_namesAL1.get(position));
                holder.detailed.setText(detailsAL1.get(position));
                holder.image.setImageResource(menu_iconAL1.get(position));
//
            }
        }
        /*if (position == selectedIndex) {
            row.setBackgroundColor(context.getResources().getColor(R.color.red_ToastColor));
        }*/
        int selected = -1;
        Animation animation = AnimationUtils.loadAnimation(context, (position>selected) ? R.anim.down_from_top : R.anim.up_from_bottom);
        row.startAnimation(animation);


        return row;
    }

    public class ViewHolder {
        private TextView menu_names,detailed;
        private ImageView image;

        public ViewHolder(View view) {
            menu_names = (TextView) view.findViewById(R.id.menu_names);
            detailed = (TextView) view.findViewById(R.id.detailed);
            image = (ImageView) view.findViewById(R.id.image);

//            txt_customer_name1.setGravity(Gravity.CENTER);
//            txt_mobile1.setGravity(Gravity.CENTER);

        }
    }
}
