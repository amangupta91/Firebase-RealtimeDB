package watersupplier.main.Pojo;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by Aman Gupta on 11/2/17.
 */

public class Orders_POJO implements Parcelable{

    public int order_id;
    public String cans_with_customer,total_amount,balance,cans_return,new_cans_given,money_received,deposit,return_date,customer_name,push_key,order_date,mobile;

    public Orders_POJO(int order_id,String cans_with_customer,String total_amount,String balance,String cans_return,String new_cans_given,
                       String money_received,String deposit,String return_date,String customer_name,String push_key,String order_date,String mobile){
        this.order_id = order_id;
        this.cans_with_customer =cans_with_customer;
        this.total_amount=total_amount;
        this.balance = balance;
        this.cans_return=cans_return;
        this.new_cans_given=new_cans_given;
        this.money_received=money_received;
        this.deposit=deposit;
        this.return_date=return_date;
        this.customer_name=customer_name;
        this.push_key = push_key;
        this.order_date = order_date;
        this.mobile = mobile;

    }
    public Orders_POJO(){}

    protected Orders_POJO(Parcel in) {
        order_id = in.readInt();
        cans_with_customer = in.readString();
        total_amount = in.readString();
        balance = in.readString();
        cans_return = in.readString();
        new_cans_given = in.readString();
        money_received = in.readString();
        deposit = in.readString();
        return_date = in.readString();
        customer_name = in.readString();
        push_key = in.readString();
        order_date = in.readString();
        mobile = in.readString();
    }

    public static final Creator<Orders_POJO> CREATOR = new Creator<Orders_POJO>() {
        @Override
        public Orders_POJO createFromParcel(Parcel in) {
            return new Orders_POJO(in);
        }

        @Override
        public Orders_POJO[] newArray(int size) {
            return new Orders_POJO[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(order_id);
        dest.writeString(cans_with_customer);
        dest.writeString(total_amount);
        dest.writeString(balance);
        dest.writeString(cans_return);
        dest.writeString(new_cans_given);
        dest.writeString(money_received);
        dest.writeString(deposit);
        dest.writeString(return_date);
        dest.writeString(customer_name);
        dest.writeString(push_key);
        dest.writeString(order_date);
        dest.writeString(mobile);
    }
}
