package project3;

import java.util.Arrays;
import java.util.LinkedList;

public class CorrelationAttack {
	/*
	 * L1 = 13, C1(D) = 1 + D + D2 + D4 + D6 + D7 + D10 + D11 + D13, L2 = 15,
	 * C2(D) = 1 + D2 + D4 + D6 + D7 + D10 + D11 + D13 + D15, L3 = 17, C3(D) = 1
	 * + D2 + D4 + D5 + D8 + D10 + D13 + D16 + D17
	 */
	int[][] c = { { 1, 1, 0, 1, 0, 1, 1, 0, 0, 1, 1, 0, 1 }, { 0, 1, 0, 1, 0, 1, 1, 0, 0, 1, 1, 0, 1, 0, 1 },
			{ 0, 1, 0, 1, 1, 0, 0, 1, 0, 1, 0, 0, 1, 0, 0, 1, 1 } };

	double[] pStarValues;
	LinkedList<LinkedList<Double>> duplicates = new LinkedList<>();
	String[] correctStates;
	int currentState = 0;
	int[][] correctSequences;
	int[] duplicate;
	int hammingDistance(int[] u, int[] z) {
		int N = z.length;
		int numDiff = 0;
		for (int i = 0; i < N; i++) {
			if (u[i] != z[i])
				numDiff++;
		}
		return numDiff;

	}

	// calculates the correlation between two binary sequences using hamming distance
	double correlation(int hamming, int N) {
		return 1.0 - (hamming / (double) N);
	}

	// generate N digits from an LFSR
	int[] sequence(int[] initial, int N, int[] c) {
		LinkedList<Integer> register = new LinkedList<Integer>();
		int[] s = new int[N];
		for (int i = 0; i < initial.length; i++) {
			register.add(initial[i]);
			s[i] = initial[i];
		}
		
		// DEBUG
		// for (int j = 0; j < initial.length; j++) {
		// System.out.print(initial[j]);
		// }
		// System.out.println();
		// generate all N digits of the sequence from the given initial state
		for (int i = initial.length; i < N; i++) {
			int sum = 0;
			for (int j = 0; j < c.length; j++) {
				if (c[j] == 1) {
					sum += register.get(j);
				}
			}
			s[i] = sum % 2;
			register.addLast(sum % 2);
			register.removeFirst();
		}

		return s;
	}

	boolean check(int[][] seqs, int N, String z, int[] zCheck) {
		// generate the keystream sequence from our selected initial states
		String ourKeyStreamMMM = "";
		int[] stream = new int[N];
		for (int i = 0; i < N; i++) {
			int zeroes = 0;
			int ones = 0;
			
			if (duplicate[i] == 1)
				ones++;
			else
				zeroes++;
			
			if (seqs[1][i] == 1)
				ones++;
			else
				zeroes++;
			
			if (seqs[2][i] == 1)
				ones++;
			else
				zeroes++;
			
			// majority logic
			if (zeroes > ones){
				ourKeyStreamMMM = ourKeyStreamMMM + "0";
			} else {
				ourKeyStreamMMM = ourKeyStreamMMM + "1";
				stream[i] = 1;

			}
		}
		System.out.println("Actual output: \n" + z);
		System.out.println("Our output: \n"+ourKeyStreamMMM);
		System.out.println("Correlation: \t"+correlation(hammingDistance(zCheck, stream), N));
		if (ourKeyStreamMMM.equals(z))
			return true;
		else
			return false;

	}

	void attack(String key) {
		duplicates.add(new LinkedList<Double>());
		duplicates.add(new LinkedList<Double>());
		duplicates.add(new LinkedList<Double>());

		System.out.println("Beginning attack...");
		int N = key.length();
		correctStates = new String[c.length];
		pStarValues = new double[c.length];
		correctSequences = new int[c.length][N];
		int[] z = new int[N];
		
		// convert z from string to array
		for (int i = 0; i < N; i++) {
			z[i] = Character.getNumericValue(key.charAt(i));
		}
		// outer loop for the three registers
		for (int i = 0; i < c.length; i++) {
			pStarValues[i] = 0.5;
			// check that the binary is maximum all ones the length of Ci
			while (Integer.toBinaryString(currentState).length() <= c[i].length) {
				String current = Integer.toBinaryString(currentState);
				//System.out.println(current);

				int[] u0 = new int[c[i].length];
				// start at right side of array and fill up with binary and left
				// pad with 0s
				for (int j = u0.length; j > u0.length - current.length(); j--) {
					u0[j - 1] = Character.getNumericValue(current.charAt(u0.length - j));
				}
				// calculate p*
				//int[] test = new int[13];
				int[] u = sequence(u0, N, c[i]);
				double pStar = correlation(hammingDistance(u, z), N);

//				if(Arrays.equals(test, u0)){
//					for (int j = 0; j < u.length; j++) {
//						System.out.print(u[i]);
//					}
//					System.out.println();
//					System.out.println(pStar);
//					System.out.println(hammingDistance(u, z));
//					//break;
//				}
				

				//System.out.println(pStar);
//finding furthest pstar?
//				if(pStar>0.65){
//					System.out.println("\n"+pStar);
//					System.out.println(pStarValues[i]+"\n");
//				}
		
				if (Math.abs(0.5 - pStar) > Math.abs(0.5 - pStarValues[i])) {
					duplicates.get(i).clear();
					pStarValues[i] = pStar;
					correctSequences[i] = u;
					String init = "";
					for (int j = 0; j < u0.length; j++) {
						init = init + u0[j];
					}
					correctStates[i] = init;

				} else if(Math.abs(0.5 - pStar) == Math.abs(0.5 - pStarValues[i])){
					duplicates.get(i).add(pStar);
					duplicate = u;
				}

				currentState++;
			}
			currentState = 0;
		}
		System.out.println("Attack complete.");
		System.out.println(pStarValues[0]);
		System.out.println(correctStates[0]);

		printArray(correctSequences[0]);

		System.out.println(pStarValues[1]);
		System.out.println(correctStates[1]);
		
		printArray(correctSequences[1]);

		System.out.println(pStarValues[2]);
		System.out.println(correctStates[2]);
		
		printArray(correctSequences[2]);

		if(check(correctSequences, N, key, z))
			System.out.println("WOOHOOO");
		else
			System.out.println("SACREBLEU");

		//printArray(duplicate);

	}
	// Helper method
	void printArray(int[] a){
		for (int i = 0; i < a.length; i++) {
			System.out.print(a[i]);
		}
		System.out.println();
	}

	public static void main(String[] args) {
		CorrelationAttack a = new CorrelationAttack();
		String key = "10010000111011001011010110010010110101101011100110"
				+ "10101010010000001100100110001110111011011100010001"
				+ "11111101010100000100001010111011000011110001110000" + "1010000110010011000001000110001110111101010";
		a.attack(key);
	}

}
