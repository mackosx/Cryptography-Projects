import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Scanner;

public class QuadraticSieve {

	int L = 100;
	ArrayList<Integer> F = new ArrayList<Integer>();

	int[][] binaryMatrix;
	int[][] factorCount;
	int primeIndex = 0;
	int rValueCount = 0;

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
		long start = System.currentTimeMillis();
		try {
			Scanner in = new Scanner(new File("prim_2_24.txt"));
			while (in.hasNext()) {
				int prime = in.nextInt();
				if (prime < L) {
					F.add((Integer) prime);
				}
			}
			in.close();
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		System.out.println(F);

		binaryMatrix = new int[L][F.size()];
		factorCount = new int[L][F.size()];
		// test selected numbers r = floor(sqrt(k * N)) + j for k=1,2,..
		// j=1,2,..

		// calculate r
		// test r^2
		// increment
		// long r_limit = 10000000;

		int chunk = 1000;
		int current = 0;
		int jMax = chunk;
		int kMax = chunk;
		BigInteger N = new BigInteger("323");
		LinkedList<Long> rList = new LinkedList<Long>();
		long oldMax = 1;
		long newMax = 1;
		// set oldmax to 0; newmax = 0;

		while (rValueCount < L) {
			oldMax = newMax;
			newMax += chunk;
			// TODO: if k and j are large,change to bIGINTTT
			for (long k = 1; k < newMax; k++) {
				// calculate square of of k.N
				BigInteger newK = new BigInteger(((Long) k).toString());
				BigInteger firstTerm = squareRoot(N.multiply(newK));
				for (long j = oldMax; j < newMax; j++) {

					BigInteger r = firstTerm.add(new BigInteger(((Long) j).toString()));
					BigInteger y = r.modPow(new BigInteger("2"), N);
					// if y is b-smooth, add y to r list
					if (isSmooth(F.size(), y)) {
						rValueCount++;
						if (rValueCount == L)
							break;
					}

				}
				if (rValueCount == L)
					break;
			}
			if (rValueCount == L)
				break;
			// should do a check for size of list
			for (long k = 1; k < newMax; k++) {
				BigInteger newK = new BigInteger(((Long) k).toString());
				BigInteger firstTerm = squareRoot(N.multiply(newK));
				for (long j = 1; j < oldMax; j++) {
					BigInteger r = firstTerm.add(new BigInteger(((Long) j).toString()));
					BigInteger y = r.modPow(new BigInteger("2"), N);
					// if y is b-smooth, add y to r list
					if (isSmooth(F.size(), y)) {
						rValueCount++;
						if (rValueCount == L)
							break;
					}
				}
				if (rValueCount == L)
					break;
			}

		}
		StringBuffer b = new StringBuffer("");
		b.append(L + " " + F.size() + "\n");
		for (int i = 0; i < factorCount.length; i++) {
			for (int j = 0; j < factorCount[0].length; j++) {
				b.append(factorCount[i][j]);
				if(j == factorCount[0].length - 1)
					b.append("\n");
				else
					b.append(" ");
			}
		}
		//System.out.println("output string: " + b);
		BufferedWriter bw = null;
		try {
			
			bw = new BufferedWriter(new FileWriter("output.txt"));
			bw.write(b.toString());
			System.out.println("Generated factor matrix.");
			ProcessBuilder pb = new ProcessBuilder("GaussBin.exe", "output.txt", "binMat.txt");
			Process p = pb.start();
			p.waitFor();
			System.out.println("Executed GaussBin.exe.");
		} catch (IOException e) {
			e.printStackTrace();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
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
		System.out.println(numSolutions);
		BigInteger sum = new BigInteger("0");
		int solutionsTried = 0;
		System.out.println(reader.nextLine());
		System.out.println(reader.nextLine());
		System.out.println(reader.nextLine());
		String[] vals = reader.nextLine().split(" ");
		System.out.println(vals.length);
//		while(solutionsTried < numSolutions){
//			for (int i = 0; i < binaryMatrix.length; i++) {
//				String[] vals = reader.nextLine().split(" ");
//				for (int j = 0; j < binaryMatrix[0].length; j++) {
//					BigInteger curr = new BigInteger(vals[j]).pow(factorCount[i][j]);
//					sum = sum.add(curr);
//				}
//				System.out.println(sum);
//				sum = BigInteger.ZERO;
//			}
//			solutionsTried++;
//		}
//		
		
			
		
		
		
		//output to file here
		// current += chunk;
		System.out.println("Time taken: " + (System.currentTimeMillis() - start)/1000 + "s");
	}

	/**
	 * 
	 * @param B
	 *            the upper limit of the list of prime factors to check
	 * @param y
	 *            the number to compute smoothness on
	 * @return
	 */
	public boolean isSmooth(int B, BigInteger y) {
		System.out.println("smooth criminal");
		// current factor we are checking

		// while number is divisible by prime factors, fill up array
		for (int j = 0; j < F.size(); j++) {
			Integer currentPrime = F.get(j);
			BigInteger divisor = new BigInteger(currentPrime.toString());
			// is y divisible by the current prime factor
			while (y.remainder(divisor).intValue() == 0) {
				factorCount[rValueCount][j]++;
				y = y.divide(divisor);
				System.out.println("current y: " + y);
			}
			if (y.intValue() != 1) {
				if (j == F.size() - 1) {
					/*
					 * reached the end of F and didnt find a factorization, so
					 * reset factor array and return false
					 */
					for (int i = 0; i < factorCount[rValueCount].length; i++) {
						factorCount[rValueCount][i] = 0;
					}
					return false;
				}

			} else {
				// rValueCount++;
				System.out.println("Added an r-value. Count: " + rValueCount);
				return true;
			}

		}

		// if the remainder is prime outside of F, reset array

		return false;

	}

	public static void main(String[] args) {
		QuadraticSieve s = new QuadraticSieve();
		s.run();

	}

}
