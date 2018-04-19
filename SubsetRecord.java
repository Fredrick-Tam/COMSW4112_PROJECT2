import java.util.*;
public class SubsetRecord {
    ArrayList<String> index;
    int numberOfBasicTerms; // n
    double product; // product of selectivities
    boolean noBranch;
    double bestCost;
    int leftChild;
    int rightChild;

    public SubsetRecord(int n, double p, ArrayList<String> subset) {
        index = subset;
        numberOfBasicTerms = n; // {f1, f2, f3, f4}
        product = p;
        noBranch = false;
        bestCost = 0;
        leftChild = 0;
        rightChild = 0;
    }
}
