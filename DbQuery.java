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

		// while (inQuery.hasNextLine())
		// {
		// will put all code below this when it works
		// }

		String line = inQuery.nextLine();
		String[] lines = line.split(" ");
		double[] selectivities = new double[lines.length];

		for (int i = 0; i < lines.length; i++) {
			selectivities[i] = Double.parseDouble(lines[i]);
		}

		// sample tests to see if helpers were giving expected answers
		double[] f = {4.0, 4.0};
		System.out.println(noBranchCost(2));

		double[] p = {0.8, 0.5};
		System.out.println(logicalAndCost(2, 0.4));

		System.out.println(fixedCost(2));

		double[] p1 = {0.3, 0.2};
		System.out.print("combined plan cost:");
		System.out.println(combinedPlanCost(2, 0.06, 2, 0.4));

		String[] funcs = {"f1", "f2", "f3", "f4", "f5", "f6", "f7", "f8", "f9", "f10"};

		double[] ex = {0.8, 0.5, 0.3, 0.2};
		createStringPlans(ex.length, funcs);

		funcs = Arrays.copyOfRange(funcs, 0, ex.length);

		// hashmap mapping functions to selectivities
		// 	HashMap<String, Double> funcSelect = new HashMap<String, Double>();
		// for (int i = 0; i < funcs.length; i++) {
		// 	funcSelect.put(funcs[i], ex[i]);
		// }
		// prints out function with respective selectivities
		// System.out.println(Arrays.asList(funcSelect));

		int k = ex.length;
		SubsetRecord[] A = createSubsets(k, ex, funcs);

		for (int i = 0; i < A.length; i++) {
			System.out.println(A[i].bestCost);
		}
	}

	public static SubsetRecord[] createSubsets(int k, double[] ex, String[] funcs) {
		// make subset array
		SubsetRecord[] subsets = new SubsetRecord[(int) Math.pow(2, k) - 1];

		// generates all bitmap plans and then populates them to subset array
		ArrayList<ArrayList<Double>> bitmapPlans = createPlans(k, ex);
		ArrayList<ArrayList<String>> stringPlans = createStringPlans(k, funcs);

		for (int i = 0; i < bitmapPlans.size(); i++) {
			System.out.println(bitmapPlans.get(i));
			int numberofBasicTerms = bitmapPlans.get(i).size();

			// calcualte combined selectivity of subset
			double prod = selectivityProd(bitmapPlans.get(i));

			// creating subset record
			SubsetRecord subset = new SubsetRecord(numberofBasicTerms, prod, stringPlans.get(i));

			// get logical and no branch costs
			double logicalAndCost = logicalAndCost(numberofBasicTerms, prod);
			double noBranchCost = noBranchCost(numberofBasicTerms);
			System.out.println(subset.index);

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

	public static double selectivityProd(ArrayList<Double> list) {
		double prod = 1;
		for (int k = 0; k < list.size(); k++) {
			prod *= list.get(k);
		}
		return prod;
	}

	public static double[] getCMetric(double p, int k) {
		double c = (p - 1) / (fixedCost(k));
		double[] CMetric = {c, p};
		return CMetric;
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

		if (prod > 0.5) {
			prod = 1 - prod;
		}

		cost += m * prod;

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
	public static double combinedPlanCost(int k, double p, int k1, double p1) {
		double cost = fixedCost(k);

		double q;
		if (p > 0.5) {
			q = 1 - p;
		} else {
			q = p;
		}

		cost += m * q;

		cost += p * logicalAndCost(k1, p1);

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

		for (ArrayList<Double> x : allPlans) System.out.println(x);
		return allPlans;
	}

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

		for (ArrayList<String> x : allPlans) System.out.println(x);
		return allPlans;
	}

}