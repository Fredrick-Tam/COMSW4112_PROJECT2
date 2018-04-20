import java.util.*;
public class SubsetRecord {
    ArrayList<String> index;
    int numberOfBasicTerms; // n
    double product; // product of selectivities
    boolean noBranch;
    double bestCost;
    ArrayList<String> leftChild;
    ArrayList<String> rightChild;
    String code;

    public SubsetRecord(int n, double p, ArrayList<String> subset) {
        index = subset;
        numberOfBasicTerms = n; // {f1, f2, f3, f4}
        product = p;
        noBranch = false;
        bestCost = 0;
        leftChild = null;
        rightChild = null;
        code = generateCode(subset);
    }

    public String generateCode(ArrayList<String> subset) {
        String code = "(" + subset.get(0);

        for (int i = 1; i < subset.size(); i++) {
            code += " & " + subset.get(i);
        }

        code += ")";

        return code;
    }


}
