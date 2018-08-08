package watersupplier.main.Reports;

import java.util.Comparator;

import watersupplier.main.Pojo.Orders_POJO;

/**
 * Created by Aman Gupta on 19/4/17.
 */

public class Customer_comparator implements Comparator<Orders_POJO>{


    @Override
    public int compare(Orders_POJO o1, Orders_POJO o2) {

        return o1.customer_name.compareTo(o2.customer_name);
    }
}
