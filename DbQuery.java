/**
 * Fredrick Kofi Tam, UNI: fkt2105
 * Teresa Choe, UNI: tc2716
 * Project 2
 *
 */

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.util.*;

public class DbQuery {

	// global variables declared here
	public static int r;
	public static int t;
	public static int l;
	public static int m;
	public static int a;
	public static int f;


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
		while (inConfig.hasNextLine())
		{
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

		for (int i=0; i < lines.length; i++) {
			selectivities[i] = Double.parseDouble(lines[i]);
		}

		// sample tests to see if helpers were giving expected answers
	 	double[] f = {4.0, 4.0};
	 	System.out.println(noBranchCost(2, f));

	 	double[] p = {0.8, 0.5};
	 	System.out.println(logicalAndCost(2, f, p));

	 	System.out.println(fixedCost(2, f));

	 	double[] p1 = {0.3, 0.2};
	 	System.out.println(combinedPlanCost(2, p1, f, 2, p,f));

	}

	// calculates the no branch cost of a plan
	public static double noBranchCost(int k, double [] f) {
		double cost = k*r + (k-1)*l + a;
		for (int i = 0; i < f.length; i++) {
			cost += f[i];
		}
		return cost;
	}

	// calculates the logical and cost of a plan
	public static double logicalAndCost(int k, double [] f, double [] p) {
		double cost = k*r + (k-1)*l + t;

		for (int i = 0; i < f.length; i++) {
			cost += f[i];
		}

		double q = 1;

		for (int j =0; j < p.length; j++) {
			q *= p[j];
		}

		cost += q*a;

		if (q > 0.5) {
			q = 1 - q;
		}

		cost += m*q;

		return cost;
	}

	// calculates fixed cost
	public static double fixedCost(int k, double [] f) {
		double cost = k*r + (k-1)*l + t;

		for (int i = 0; i < f.length; i++) {
			cost += f[i];
		} 

		return cost;

	}

	// calculates the combined plan cost
	public static double combinedPlanCost(int k, double [] p, double [] f, int k1, double [] p1, double [] f1) {
		double cost = fixedCost(k,f);

		double p_val = 1;
		double q = 1;

		for (int i = 0; i < p.length; i++) {
			q *= p[i];
			p_val *= p[i];
		} 

		if (q > 0.5) {
			q = 1-q;
		}

		cost += m*q;

		cost += p_val * logicalAndCost(k1,f1,p1);

		return cost;

	}
}