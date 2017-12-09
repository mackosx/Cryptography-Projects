package project2;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.util.LinkedList;

public class DeBruijn {
	int q;
	int L;
	int length;
	int[] connections;
	LinkedList<Integer> register = new LinkedList<Integer>();
	private int[] sequence;
	int currentBit = 0;

	public DeBruijn(int q, int L, int[] connections, LinkedList<Integer> initial) {
		this.q = q;
		this.L = L;
		this.connections = connections;
		this.register = initial;
		length = (int) Math.pow(q, L);
		sequence = new int[length];
	}

	public void shift() {
		int bit = getNextBit();
		register.addLast(bit);
		register.removeFirst();
		if (currentBit >= length - 1)
			currentBit = 0;
		else
			currentBit++;
	}

	public int getNextBit() {
		int sum = 0;

		if (currentBit == length - 2) {
			return 0;
		} else if (currentBit == length - 1) {
			return 1;
		} else {
			for (int i = 0; i < connections.length; i++) {
				sum += connections[i] * register.get(i);
			}
			return ((sum % q) + q) % q;
		}
	}

	public int[] getSequence() {
		return this.sequence;
	}

	public String toString() {
		String s = "";
		for (int i = 0; i < sequence.length; i++) {
			s += sequence[i];
		}
		return s;
	}

	public static void main(String[] args) {
		// initialize de bruijn sequences with connection polynomials
		LinkedList<Integer> reg1 = new LinkedList<Integer>();
		reg1.add(0);
		reg1.add(0);
		reg1.add(0);
		reg1.add(1);
		int[] connections1 = { 1, 1, 0, 0 };
		DeBruijn s1 = new DeBruijn(2, 4, connections1, new LinkedList<Integer>(reg1));
		int[] connections2 = { -3, -2, 0, -1 };
		DeBruijn s2 = new DeBruijn(5, 4, connections2, new LinkedList<Integer>(reg1));
		int count = 0;
		StringBuilder b = new StringBuilder("");
		BufferedWriter bw = null;
		boolean found = false;
		// CHECKER FOR Z5 sequence, make sure its a DeBruijn seq
		for (int j = 1; j <= 624; j++) {
			found = false;
			String check = String.format("%4s", Integer.toString(j, 5)).replace(' ', '0');
			for (int k = 0; k < 625; k++) {
				String r = String.format("%d%d%d%d", s2.register.get(0), s2.register.get(1), s2.register.get(2),
						s2.register.get(3));
				s2.shift();
				if (r.equals(check)) {
					found = true;
					break;
				}
			}
			if (!found) {
				System.out.println("not De bruijn\ndidnt find " + check);
				break;
			}
		}
		if (found) {
			System.out.println("is de bruijn");
		}
		// reset registers
		s1.register = new LinkedList<Integer>(reg1);
		s2.register = new LinkedList<Integer>(reg1);
		s2.currentBit = 0;
		s1.currentBit = 0;
		System.out.println(s2.register);
		// generate Z_10 sequence and add to string
		while (count < 10000) {
			int x = s1.register.get(0);
			int y = s2.register.get(0);
			int next = (5 * x) + y;
			b.append(next);
			count++;
			s1.shift();
			s2.shift();
		}
		b.append(b.substring(0, 3));
		System.out.println(b.length());
		// write sequence to file and run de bruijn checker
		try {
			bw = new BufferedWriter(new FileWriter("number.txt"));
			bw.write(b.toString());
			bw.close();
			int pb = new ProcessBuilder("Check_LE4.exe").inheritIO().start().waitFor();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
