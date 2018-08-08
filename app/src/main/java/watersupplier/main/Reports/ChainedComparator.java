package watersupplier.main.Reports;



import java.util.Arrays;
import java.util.Comparator;
import java.util.List;

import watersupplier.main.Pojo.Orders_POJO;


public class ChainedComparator implements Comparator<Orders_POJO> {

    private List<Comparator<Orders_POJO>> listComparators;

    @SafeVarargs
    public ChainedComparator(Comparator<Orders_POJO>... comparators) {
        this.listComparators = Arrays.asList(comparators);
    }

    @Override
    public int compare(Orders_POJO b1, Orders_POJO b2) {
        for (Comparator<Orders_POJO> comparator : listComparators) {
            int result = comparator.compare(b1, b2);
            if (result != 0) {
                return result;
            }
        }
        return 0;
    }

}
