import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Scanner;

public class QuadraticSieve {
	// L = 2^10
	int L = (int) Math.pow(2, 10);
	int factorBase = L - 5;
	
	// Factor base containing all primes we need
	ArrayList<Integer> F = new ArrayList<Integer>();
	// Hash for keeping track of duplicates
	HashSet<String> M = new HashSet<String>();
	
	BigInteger[] rList = new BigInteger[L];

	int[][] factorCount;
	int rValueCount = 0;
	int duplicates = 0;
	boolean solutionFound = false;

	public BigInteger squareRoot(BigInteger x) {
		BigInteger right = x, left = BigInteger.ZERO, mid;
		while (right.subtract(left).compareTo(BigInteger.ONE) > 0) {
			mid = (right.add(left)).shiftRight(1);
			if (mid.multiply(mid).compareTo(x) > 0)
				right = mid;
			else
				left = mid;
		}
		return left;
	}
	
	public void run() {
		//L = 300;
		Scanner in = null;
		try {
			in = new Scanner(new File("prim_2_24.txt"));
			while (in.hasNext()) {
				int prime = in.nextInt();
				if (prime < factorBase) {
					F.add((Integer) prime);
				} else
					break;
			}
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} finally {
			in.close();
		}
		long start = System.currentTimeMillis();

		BigInteger N = new BigInteger("127423368713324519534591");

		rValueCount = 0;
		factorCount = new int[L][F.size()];
		// Build up array of r values
		System.out.println("Calculating suitable r-values...");
		for (long k = 1; rValueCount < L; k++) {
			// Calculate the floor(sqrt(k*N)) term, then increment j and add
			BigInteger firstTerm = squareRoot(N.multiply(BigInteger.valueOf(k)));
			for (long j = 1; j < k*2 && rValueCount < L; j++) {

				BigInteger r = firstTerm.add(BigInteger.valueOf(j));
				// if y is b-smooth, add y to r list
				if (isSmooth(F, r, N)) {
					rValueCount++;
					rList[rValueCount - 1] = r;

				}
				
			}
		}
		System.out.println("Done.");
		// Build the input file for the gaussian elimination .exe
		StringBuffer b = new StringBuffer("");
		b.append(L + " " + F.size() + "\n");
		for (int i = 0; i < factorCount.length; i++) {
			for (int j = 0; j < factorCount[0].length; j++) {
				b.append(factorCount[i][j]);
				if (j == factorCount[0].length - 1)
					b.append("\n");
				else
					b.append(" ");
			}
		}
		BufferedWriter bw = null;
		try {

			bw = new BufferedWriter(new FileWriter("output.txt"));
			bw.write(b.toString());
			bw.close();
			System.out.println("Generated factor matrix.");
			ProcessBuilder pb = new ProcessBuilder("GaussBin.exe", "output.txt", "binMat.txt");
			Process p = pb.start();
			p.waitFor();
			System.out.println("Executed GaussBin.exe.");
		} catch (IOException e) {
			e.printStackTrace();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		Scanner reader = null;
		try {
			File f = new File("binMat.txt");
			reader = new Scanner(f);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		int numSolutions = Integer.parseInt(reader.nextLine());
		System.out.println("Number of potential solutions: " + numSolutions);
		BigInteger[] solution = new BigInteger[2];

		// Go through and check all solution rows in matrix
		while (reader.hasNextLine()) {
			String[] vals = reader.nextLine().split(" ");
			BigInteger RHS = BigInteger.ONE;
			BigInteger LHS = BigInteger.ONE;
			int[] totalFactors = new int[F.size()];
			for (int i = 0; i < vals.length; i++) {
				if (vals[i].equals("1")) {
					// add current row of factors into the total
					for (int k = 0; k < factorCount[i].length; k++) {
						totalFactors[k] += factorCount[i][k];
					}
					// multiply in the current prime raised to the power
					LHS = LHS.multiply(rList[i]).mod(N);
				}
			}
			for (int k = 0; k < totalFactors.length; k++) {
				/* since we have all even exponents, and the resulting equation
				* doesn't actually have to be squared
				* we can divide all exponents in half to remove a factor of 2
				*/
				int power = totalFactors[k] / 2;
				RHS = RHS.multiply(BigInteger.valueOf(F.get(k)).pow(power)).mod(N);

			}

			// Calculate gcd and extract factors of N
			BigInteger gcd = N.gcd(RHS.subtract(LHS));
			System.out.println("GCD: " + gcd + " RHS: " + RHS + " LHS: " + LHS);
			if (!gcd.equals(BigInteger.ONE) && !RHS.equals(LHS)) {
				// There is a bandage here where if they are somehow equal,
				// don't have solution
				// but we don't know why they would be equal
				solution[0] = gcd;
				solution[1] = N.divide(gcd);
				solutionFound = true;
				break;
			}
		}

		System.out.println("Duplicate binary rows: " + duplicates);

		if (!solutionFound)
			System.out.println("No Solution Found. :(");
		else
			System.out.printf("The factors of %s are %d and %d\n", N.toString(), solution[0], solution[1]);
		reader.close();

		System.out.println("Time taken: " + (System.currentTimeMillis() - start) / 1000.0 + "s");
	}

	/**
	 * 
	 * @param F
	 *           the factor base over which to check smoothness
	 * @param y
	 *            the number to compute smoothness on
	 * @return
	 */
	public boolean isSmooth(ArrayList<Integer> F, BigInteger r, BigInteger N) {
		// While number is divisible by prime factors, fill up array
		BigInteger y = r.pow(2).mod(N);

		int[] currentRow = new int[factorCount[0].length];
		String row = "";

		for (int j = 0; j < F.size(); j++) {
			BigInteger divisor = BigInteger.valueOf(F.get(j));

			// temp var to keep track of total powers of current prime
			int count = 0;
			// continue to divide y by prime while it has factors of the prime
			
			while (y.mod(divisor).equals(BigInteger.ZERO)) {
				count++;
				y = y.divide(divisor);
			}
			currentRow[j] = count;
			// Build binary string to add to hash
			row += count % 2;

			if (!y.equals(BigInteger.ONE)) {
				if (j == F.size() - 1) {
					// reached the end of F and didnt find a factorization, so
					// return false
					return false;
				}
				// Check hash for duplicate
			} else {
				if (M.add(row.toString())) {
					// Row was not duplicate and is also B-smooth
					// add to factor array
					for (int i = 0; i < currentRow.length; i++) {
						factorCount[rValueCount][i] = currentRow[i];
					}

					return true;

				} else {
					// Y factors within our factorbase
					duplicates++;

					return false;
				}
			}
		}
		return false;

	}

	public static void main(String[] args) {
		QuadraticSieve s = new QuadraticSieve();
		s.run();

	}

}
