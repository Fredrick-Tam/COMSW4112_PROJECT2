/**
 * Fredrick Kofi Tam, UNI: fkt2105
 * Teresa Choe, UNI: tc2716
 * Project 2
 *
 */

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.lang.reflect.Array;
import java.util.*;

public class DbQuery {

	// global variables declared here
	public static int r; // cost of accessing an array element
	public static int t; // cost of performing an if test
	public static int l; // cost of performing a logical "and"
	public static int m; // cost of a branch misprediction
	public static int a; // cost of writing an answer to the answer array
	public static int f; // cost of applying function fi


	public static void main(String[] args) throws FileNotFoundException {

		// read in query.txt and config.txt from command line
		String query = args[0];
		String config = args[1];


		// make scanner objects to parse config and query files
		File inputConfig = new File(config);
		Scanner inConfig = new Scanner(inputConfig);

		File inputQuery = new File(query);
		Scanner inQuery = new Scanner(inputQuery);

		// looping through config.txt to get config params and set them
		// as global variables
		while (inConfig.hasNextLine()) {
			String line = inConfig.nextLine();
			String[] lines = line.split(" ");

			// set t,l,m,a and f from config.txt
			if (lines[0].equals("r")) {
				r = Integer.parseInt(lines[2]);
			} else if (lines[0].equals("t")) {
				t = Integer.parseInt(lines[2]);
			} else if (lines[0].equals("l")) {
				l = Integer.parseInt(lines[2]);
			} else if (lines[0].equals("m")) {
				m = Integer.parseInt(lines[2]);
			} else if (lines[0].equals("a")) {
				a = Integer.parseInt(lines[2]);
			} else if (lines[0].equals("f")) {
				f = Integer.parseInt(lines[2]);
			}
		}

		// parse query.txt and find optimal cost for every line of selectivities
		while (inQuery.hasNextLine())
		{
			
		String line = inQuery.nextLine();
		String[] lines = line.split(" ");
		double[] selectivities = new double[lines.length];

		for (int i = 0; i < lines.length; i++) {
			selectivities[i] = Double.parseDouble(lines[i]);
		}

		String[] funcs = {"t1[o1[i]]", "t2[o2[i]]", "t3[o1[3]]", "t4[o4[i]]", "t5[o5[i]]", "t6[o6[i]]", "t7[o7[i]]", "t8[o8[i]]", "t9[o9[i]]", "t10[o10[i]]"};


		createStringPlans(selectivities.length, funcs);

		funcs = Arrays.copyOfRange(funcs, 0, selectivities.length);

		int k = selectivities.length;

		SubsetRecord[] A = createSubsets(k, selectivities, funcs);

		optimalPlan(A, selectivities);
	}
	System.out.println("==================================================================");
	}

	public static int index(SubsetRecord[] A, ArrayList<String> i) {
		HashMap<ArrayList<String>, Integer> indexes = new HashMap<ArrayList<String>, Integer>();

		for (int j = 0; j < A.length; j++) {
			indexes.put(A[j].index, j);
		}

		int index = -1;

		if (indexes.containsKey(i)){
			index = indexes.get(i);
		} else {
			index = -1;
		}
		return index;
	}

	public static ArrayList<String> getRightMost(SubsetRecord[] A, ArrayList<String> i) {
		int subset = index(A, i);

		if (A[subset].rightChild == null) {
			return A[subset].index;
		}
		return getRightMost(A, A[subset].rightChild);
	}

	public static String getPlan(SubsetRecord[] A, ArrayList<String> i) {
		int subset = index(A, i);

		if (i == null) {
			return "";
		}

		String leftChildCode = getPlan(A, A[subset].leftChild);
		String rightChildCode = getPlan(A, A[subset].rightChild);

		if (leftChildCode.equals("") && rightChildCode.equals("")) {
			;
		} else if (leftChildCode.equals("") && !rightChildCode.equals("")) {
			A[subset].code = rightChildCode;
		} else if (rightChildCode.equals("") && !leftChildCode.equals("")) {
			A[subset].code = leftChildCode;
		} else {
			A[subset].code = leftChildCode + " && " + rightChildCode;
		}
		return A[subset].code;

	}


	// uses subset table to calculate optimal plan
	public static void optimalPlan(SubsetRecord[] A, double[] ex) {

		// loop through all subsets of A
		for (int i = 0; i < A.length; i++) {
			ArrayList<String> s = A[i].index;
			ArrayList<ArrayList<String>> noIntersection = new ArrayList<ArrayList<String>>();

			// for given subset s, find s' where s interesection s' = null
			for (int j = 0; j< A.length; j++) {
				if (Collections.disjoint(s,A[j].index)) {
					noIntersection.add(A[j].index);
				}
			}

			// loop through s' 
			for (int k = 0; k < noIntersection.size(); k++) {
				
				ArrayList<String> sPrime = noIntersection.get(k);

				SubsetRecord subset = A[i];
				SubsetRecord subsetPrime = A[i];

				// setting subsetPrime to s' value in table
				for (int l = 0; l < A.length; l++) {
					if (sPrime.equals(A[l].index)) {
						subsetPrime = A[l];
					}
				}

				// fins out if s dominates s' in terms of c-metric
				if (cMetricDominated(subset.product, subset.numberOfBasicTerms, subsetPrime.product, subsetPrime.numberOfBasicTerms)) {
					// do nothing

				// find out if s dominates s' in terms of d-metric
				} else if (subsetPrime.product <= 0.5 && dMetricDominated(subset.product, subset.numberOfBasicTerms, subsetPrime.product, subsetPrime.numberOfBasicTerms)) {
					// do Nothing
				} else {
					// calculate cost of combined plan (s && s')
					double cost = combinedPlanCost(subsetPrime.numberOfBasicTerms, subsetPrime.product, subset.bestCost);
					// get union of subsets s U s'
					ArrayList<String> union = new ArrayList<String>();
					union.addAll(subsetPrime.index);
					union.addAll(subset.index);

					// sort union so that it matches order convention
					Collections.sort(union);
					// find subset record for union values
					// if combined cost is smaller than cost of subset
					// update its cost and left and right children
					int w = index(A, union);
					if (cost < A[w].bestCost) {
						A[w].bestCost = cost;
						A[w].leftChild = subsetPrime.index;
						A[w].rightChild = subset.index;
					}
				}
			}
		} 
		System.out.println("==================================================================");
		for (int v = 0; v < ex.length; v++) {
			System.out.print(ex[v] + " ");
		}
		System.out.println();
		System.out.println("------------------------------------------------------------------");

		
		ArrayList<String> rightMost = new ArrayList<String>();
		rightMost = getRightMost(A,A[A.length-1].index);

		// get best plan for algorithm
		String plan = getPlan(A, A[A.length-1].index);

		// if right most plan uses no branch cost
		if (A[index(A, rightMost)].noBranch == true) {
			noBranchPlan(plan);
		} else {
			// if right most plan does not use branch cost
			// generates plan
			branchPlan(plan);
		}
		System.out.println("------------------------------------------------------------------");
		System.out.print("cost: ");
		System.out.println(A[A.length-1].bestCost);
	}

	public static void branchPlan(String plan) {
		System.out.println("if(" + plan + ") {");
		System.out.println("	answer[j++] = i;");
		System.out.println("}");
	}

	public static void noBranchPlan(String plan) {
		String[] parts = plan.split(" && ");

		if (parts.length == 2) {
			System.out.println("if("+parts[0]+") {");
			System.out.println("	answer[j] = i;");
			System.out.println("	j += (" + parts[1] +");");
			System.out.println("}");
		} else {
			System.out.print("if(");
			System.out.print("(" + parts[0] + ")");

			for (int k = 1; k < parts.length-1; k++) {
				System.out.print(" && " + "(" + parts[k] + ")");
			}
			System.out.println(") {");
			System.out.println("	answer[j] = i;");
			System.out.print("	j += (");
			System.out.print(parts[parts.length-1]);
			System.out.println(");");
			System.out.println("}");
		}

	}
	// creates A[2^k-1] of subset records
	public static SubsetRecord[] createSubsets(int k, double[] ex, String[] funcs) {
		// make subset array
		SubsetRecord[] subsets = new SubsetRecord[(int) Math.pow(2, k) - 1];

		// generates all bitmap plans and then populates them to subset array
		ArrayList<ArrayList<Double>> bitmapPlans = createPlans(k, ex);
		ArrayList<ArrayList<String>> stringPlans = createStringPlans(k, funcs);

		for (int i = 0; i < bitmapPlans.size(); i++) {
			int numberofBasicTerms = bitmapPlans.get(i).size();

			// calcualte combined selectivity of subset
			double prod = selectivityProd(bitmapPlans.get(i));

			// creating subset record
			SubsetRecord subset = new SubsetRecord(numberofBasicTerms, prod, stringPlans.get(i));

			// get logical and no branch costs
			double logicalAndCost = logicalAndCost(numberofBasicTerms, prod);
			double noBranchCost = noBranchCost(numberofBasicTerms);

			// set subset cost to logical cost
			subset.bestCost = logicalAndCost;

			// if no branch cost is lower, set subset cost to that
			if (subset.bestCost > noBranchCost) {
				subset.bestCost = noBranchCost;
				subset.noBranch = true;
			}

			// Add subset to subset array
			subsets[i] = subset;
		}
		return subsets;
	}

	// helper method to find combined selectivity
	public static double selectivityProd(ArrayList<Double> list) {
		double prod = 1;
		for (int k = 0; k < list.size(); k++) {
			prod *= list.get(k);
		}
		return prod;
	}

	// finds out if s dominates s' in c-metric
	public static boolean cMetricDominated(double p, int k, double pPrime, int kPrime) {
		double c = (p - 1) / (fixedCost(k));
		double cPrime = (pPrime -1) / (fixedCost(kPrime));
		double[] s = {c, p};
		double[] sPrime = {cPrime, pPrime};

		if (s[0] > sPrime[0] && s[1] > sPrime[1]) {
			return true;
		} else { 
			return false;
		}
	}

	// finds out if s dominates s' in d-metric
	public static boolean dMetricDominated(double p, int k, double pPrime, int kPrime) {
		double c = (fixedCost(k));
		double cPrime = (fixedCost(kPrime));
		double[] s = {c, p};
		double[] sPrime = {cPrime, pPrime};

		if (s[0] > sPrime[0] && s[1] > sPrime[1]) {
			return true;
		} else { 
			return false;
		}
	}

	// calculates the no branch cost of a plan
	public static double noBranchCost(int k) {
		double cost = k * r + (k - 1) * l + a;
		for (int i = 0; i < k; i++) {
			cost += f;
		}
		return cost;
	}

	// calculates the logical and cost of a plan
	public static double logicalAndCost(int k, double prod) {
		double cost = k * r + (k - 1) * l + t;

		for (int i = 0; i < k; i++) {
			cost += f;
		}
		cost += prod * a;

		double q = 0;

		if (prod <= 0.5) {
			q = prod;
		} else {
			q = 1 - prod;
		}
		cost += m * q;
		return cost;
	}

	// calculates fixed cost
	public static double fixedCost(int k) {
		double cost = k * r + (k - 1) * l + t;

		for (int i = 0; i < k; i++) {
			cost += f;
		}
		return cost;
	}

	// calculates the combined plan cost
	public static double combinedPlanCost(int k, double p, double C) {
		double cost = fixedCost(k);

		double q;
		if (p > 0.5) {
			q = 1 - p;
		} else {
			q = p;
		}
		cost += m * q;
		cost += p * C;
		return cost;
	}

	// Creates a bitmap that generates all subsets
	public static ArrayList<ArrayList<Double>> createPlans(int k, double[] f) {
		int numberOfPlans = (int) Math.pow(2, k);
		ArrayList<ArrayList<Double>> allPlans = new ArrayList<>();

		// Generates all bit values from 0 to 2^k-1
		for (int i = 1; i < numberOfPlans; i++) {
			String bit = String.format("%" + k + "s", Integer.toBinaryString(i)).replace(' ', '0');

			ArrayList<Double> plan = new ArrayList<>();

			// Generates all subsets depending on bit value
			for (int n = 0; n < bit.length(); n++) {
				if (bit.charAt(n) == '1') {
					plan.add(f[n]);
				}
			}

			allPlans.add(plan);
		}

		// for (ArrayList<Double> x : allPlans) System.out.println(x);
		return allPlans;
	}

	// creates a string representation of & terms
	public static ArrayList<ArrayList<String>> createStringPlans(int k, String[] f) {
		int numberOfPlans = (int) Math.pow(2, k);
		ArrayList<ArrayList<String>> allPlans = new ArrayList<>();

		// Generates all bit values from 0 to 2^k-1
		for (int i = 1; i < numberOfPlans; i++) {
			String bit = String.format("%" + k + "s", Integer.toBinaryString(i)).replace(' ', '0');

			ArrayList<String> plan = new ArrayList<>();

			// Generates all subsets depending on bit value
			for (int n = 0; n < bit.length(); n++) {
				if (bit.charAt(n) == '1') {
					plan.add(f[n]);
				}
			}

			allPlans.add(plan);
		}

		// for (ArrayList<String> x : allPlans) System.out.println(x);
		return allPlans;
	}

}