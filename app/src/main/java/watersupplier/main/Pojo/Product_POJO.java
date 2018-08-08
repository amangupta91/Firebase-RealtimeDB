package watersupplier.main.Pojo;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by Aman Gupta on 24/1/17.
 */

public class Product_POJO implements Parcelable{



    public int prdt_id,price;
    public String product_Name,product_Description,push_key;

    public Product_POJO(String product_Name,String product_Description,int price,String push_key){
        this.product_Name = product_Name;
        this.product_Description = product_Description;
        this.price = price;
        this.push_key = push_key;
    }

    public Product_POJO(Parcel in) {
        prdt_id = in.readInt();
        product_Name = in.readString();
        product_Description = in.readString();
        price = in.readInt();
        push_key = in.readString();
    }

     public Product_POJO(){

     }

    public static final Parcelable.Creator<Product_POJO> CREATOR = new Creator<Product_POJO>() {
        @Override
        public Product_POJO createFromParcel(Parcel in) {
            return new Product_POJO(in);
        }

        @Override
        public Product_POJO[] newArray(int size) {
            return new Product_POJO[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(prdt_id);
        dest.writeString(product_Name);
        dest.writeString(product_Description);
        dest.writeInt(price);
        dest.writeString(push_key);
    }

}
