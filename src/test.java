
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.BitSet;
import java.util.LinkedList;
import java.util.List;

public class test {

	private static List<BigInteger> genPrimes(int nbrOfPrimes) {
		List<BigInteger> primes = new ArrayList<BigInteger>(nbrOfPrimes);
		BigInteger prime = BigInteger.ONE;
		boolean found;
		while (primes.size() < nbrOfPrimes) {
			found = false;
			for (BigInteger big : primes) {
				if (prime.mod(big).equals(BigInteger.ZERO)) {
					found = true;
					break;
				}
			}
			if (!found && !prime.equals(BigInteger.ONE)) {
				primes.add(prime);
			}
			prime = prime.add(BigInteger.ONE);
		}
		return primes;
	}

	private static void genRs(List<BigInteger> rs, List<BigInteger> r2s,
			int lValue, BigInteger nValue, List<BigInteger> primes,
			List<BitSet> m, int jump) {
		System.out.println("Generating Rs...");
		int k = 1;
		while (rs.size() < lValue) {
			BigInteger ki = new BigInteger(Integer.toString(k));
			int stop = k + k;
			int j = 1;
			while (j <= stop && rs.size() < lValue) {
				BigInteger ji = new BigInteger(Integer.toString(j));
				BigInteger r = squareRoot(ki.multiply(nValue)).add(ji);
				BigInteger r2 = r.pow(2).mod(nValue);
				BitSet binrow = genBinaryRow(r2, primes);
				if (binrow != null && !m.contains(binrow)) {
					m.add(binrow);
					rs.add(r);
					r2s.add(r2);
				}
				j += jump;
			}
			k += jump;
		}
		System.out.println("R:s generated");
	}

	private static BigInteger squareRoot(BigInteger x) {
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

	private static BitSet genBinaryRow(BigInteger number,
			List<BigInteger> primes) {
		BitSet row = new BitSet(primes.size());
		for (int j = 0; j < primes.size(); j++) {
			BigInteger big = primes.get(j);
			int nDivs = 0;
			while (number.mod(big).equals(BigInteger.ZERO)) {
				number = number.divide(big);
				nDivs++;
			}
			if (nDivs % 2 != 0) {
				row.set(j);
			}
		}
		return number.compareTo(BigInteger.ONE) == 0 ? row : null;
	}

	private static boolean trySolution(BitSet x, BigInteger N,
			List<BigInteger> rs, List<BigInteger> r2s, int rows) {
		BigInteger x2 = BigInteger.ONE;
		BigInteger y2 = BigInteger.ONE;
		for (int j = 0; j < rows; j++) {
			if (x.get(j)) {
				x2 = x2.multiply(rs.get(j));
				y2 = y2.multiply(r2s.get(j));
			}
		}
		x2 = x2.mod(N);
		y2 = squareRoot(y2).mod(N);

		BigInteger gcd = N.gcd(y2.subtract(x2));
		if (!gcd.equals(BigInteger.ONE) && !gcd.equals(N)) {
			BigInteger factor1 = gcd;
			BigInteger factor2 = N.divide(factor1);
			System.out.println("\nFactor 1: " + factor1);
			System.out.println("Factor 2: " + factor2);
			return true;
		}
		return false;
	}

	private static BitSet createFromString(String s) {
		BitSet t = new BitSet(s.length());
		for (int i = 0; i < s.length(); i++) {
			if (s.charAt(i) == '1') {
				t.set(i);
			}
		}
		return t;
	}

	private static void testSolutions(List<BitSet> m, List<BigInteger> rs,
			List<BigInteger> r2s, List<BigInteger> primes, BigInteger N) {
		System.out.println("Testing solutions...");
		try {
			BufferedReader br = new BufferedReader(new FileReader(
					"HelpFunction/output.txt"));
			int solutionsN = Integer.parseInt(br.readLine());
			System.out.println("Start testing: ");
			for (int i = 0; i < solutionsN; i++) {
				String solution = br.readLine().replaceAll("\\s+", "");

				if (i % 20 == 0)
					System.out.print(i + " ");

				BitSet x = createFromString(solution);

				if (trySolution(x, N, rs, r2s, m.size()))
					return;

			}
			br.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private static void createInputFile(List<BitSet> list, int M, int N)
			throws IOException {
		System.out.println("Generating input file...");
		try {
			File f = new File("HelpFunction/input.txt");
			BufferedWriter writer = new BufferedWriter(new FileWriter(f));
			writer.write(M + " " + N + "\n");
			String s;
			for (int i = 0; i < M; i++) {
				BitSet currentLine = list.get(i);
				for (int j = 0; j < N; j++) {
					s = currentLine.get(j) ? "1" : "0";
					writer.write(s + " ");
				}
				writer.write("\n");
			}
			writer.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		System.out.println("Input file generated");
	}

	private static ArrayList<BigInteger> readFile(String s) {
		ArrayList<BigInteger> data = new ArrayList<BigInteger>();

		try {
			BufferedReader br = new BufferedReader(new FileReader(s));
			int limit = Integer.parseInt(br.readLine());
			for (int i = 0; i < limit; i++){
				data.add(new BigInteger(br.readLine()));
			}
			br.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return data;
	}

	private static String findSolutions() throws IOException,
			InterruptedException {
		StringBuilder sb = new StringBuilder();
		if (!new File("./HelpFunction/gauss").exists()) {
			System.out.println("Compiling GaussBin");
			Process p = Runtime.getRuntime().exec("g++ -o gauss GaussBin.cpp", null,
					new File("./HelpFunction"));
			p.waitFor();
		}
		
		String command = "./HelpFunction/gauss";
		ProcessBuilder pb = new ProcessBuilder(command,
				"./HelpFunction/input.txt", "./HelpFunction/output.txt");
		System.out.println("Running GaussBin");
		Process process = pb.start();

		BufferedReader br = new BufferedReader(new InputStreamReader(
				process.getInputStream()));
		String line = null;
		while ((line = br.readLine()) != null){
			sb.append(line + System.getProperty("line.separator"));
		}

		int errCode = process.waitFor();
		if (errCode != 0){
			System.out.println("Error from process " + errCode);
		}
		
		System.out.println("GaussBin finished");
		return sb.toString();
	}

	public static void main(String[] args){
		ArrayList<BigInteger> numbers = readFile("Data/input.txt");
		long start = System.currentTimeMillis();
		BigInteger N = numbers.get(5);
		int diff = 10;
		int l = (1 << 10) + diff;
		List<BigInteger> primes = genPrimes(l - diff);
		System.out.println("Primes generated " + (System.currentTimeMillis()-start) + " ms");
		int jump = 1;
		List<BitSet> m = new LinkedList<BitSet>();
		List<BigInteger> r2s = new LinkedList<BigInteger>();
		List<BigInteger> rs = new LinkedList<BigInteger>();
		genRs(rs, r2s, l, N, primes, m, jump);
		System.out.println("R:s generated " + (System.currentTimeMillis()-start) + " ms");
		try {
			createInputFile(m, m.size(), primes.size());
			findSolutions();
			System.out.println((System.currentTimeMillis()-start) + " ms");
		} catch (IOException | InterruptedException e) {
			e.printStackTrace();
		}
		System.out.println("The number is " + N.toString());
		testSolutions(m, rs, r2s, primes, N);
		long end = System.currentTimeMillis();
		System.out.println("It took " + (end-start) + " ms");
	}
}
