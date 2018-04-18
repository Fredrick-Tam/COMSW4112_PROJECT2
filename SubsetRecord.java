public class SubsetRecord {
    int numberOfBasicTerms; // n
    int product; // product of selectivities
    boolean noBranch;
    double bestCost;
    int leftChild;
    int rightChild;

    public SubsetRecord(int n, int p) {
        numberOfBasicTerms = n; // {f1, f2, f3, f4}
        product = p;
        noBranch = false;
        bestCost = 0;
        leftChild = 0;
        rightChild = 0;
    }
}
