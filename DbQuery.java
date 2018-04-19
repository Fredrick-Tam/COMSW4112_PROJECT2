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

		String[] funcs = {"f1", "f2", "f3", "f4", "f5", "f6", "f7", "f8", "f9", "f10"};

		double[] ex = {0.8, 0.5, 0.3, 0.2};
		createStringPlans(ex.length, funcs);

		funcs = Arrays.copyOfRange(funcs, 0, ex.length);

		int k = ex.length;
		SubsetRecord[] A = createSubsets(k, ex, funcs);

		optimalPlan(A);
	}

	// uses subset table to calculate optimal plan
	public static void optimalPlan(SubsetRecord[] A) {

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
					for (int w = 0; w < A.length; w ++) {
						if (A[w].index.equals(union)) {
							if (cost < A[w].bestCost) {
								A[w].bestCost = cost;
								A[w].leftChild = subsetPrime.index;
								A[w].rightChild = subset.index;
							}
						}
					}
				}
			}
		} 

		System.out.print("optimal plan left child: ");
		System.out.println(A[A.length-1].leftChild);

		System.out.print("optimal plan right child: ");
		System.out.println(A[A.length-1].rightChild);

		System.out.print("did optimal plan use no-branch: ");
		System.out.println(A[A.length-1].noBranch);

		System.out.print("best cost for query: ");
		System.out.println(A[A.length-1].bestCost);

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