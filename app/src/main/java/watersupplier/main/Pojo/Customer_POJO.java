package watersupplier.main.Pojo;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by Aman Gupta on 20/1/17.
 */

public class Customer_POJO implements Parcelable{
    public int customerId;
    public String customer_name,address,mobile,customer_spPrice,credit_line,push_key;

    public Customer_POJO(int customerId,String customer_name,String address,String mobile,String customer_spPrice,String credit_line,String push_key){
        this.customerId=customerId;
        this.customer_name=customer_name;
        this.address=address;
        this.mobile = mobile;
        this.customer_spPrice = customer_spPrice;
        this.credit_line = credit_line;
        this.push_key = push_key;

    }
public Customer_POJO(){

}

    protected Customer_POJO(Parcel in) {
        customerId = in.readInt();
        customer_name = in.readString();
        address = in.readString();
        mobile = in.readString();
        customer_spPrice = in.readString();
        credit_line = in.readString();
        push_key = in.readString();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(customerId);
        dest.writeString(customer_name);
        dest.writeString(address);
        dest.writeString(mobile);
        dest.writeString(customer_spPrice);
        dest.writeString(credit_line);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<Customer_POJO> CREATOR = new Creator<Customer_POJO>() {
        @Override
        public Customer_POJO createFromParcel(Parcel in) {
            return new Customer_POJO(in);
        }

        @Override
        public Customer_POJO[] newArray(int size) {
            return new Customer_POJO[size];
        }
    };
}
