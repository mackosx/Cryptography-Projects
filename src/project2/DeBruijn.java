package project2;

import java.util.LinkedList;

public class DeBruijn {
	int q;
	int L;
	int length;
	int[] connections;
	LinkedList<Integer> register = new LinkedList<Integer>();
	private int[] sequence;
	int currentBit = 0;
	public DeBruijn(int q, int L, int[] connections, LinkedList<Integer> initial){
		this.q = q;
		this.L = L;
		this.connections = connections;
		this.register = initial;
		length = (int)Math.pow(q, L);
		sequence = new int[length - 1];
		generateSequence();
	}
	public void shift(){
		int bit = nextBit();
		register.addLast(bit);
		sequence[currentBit] = register.removeFirst().intValue();
		currentBit++;
	}
	public int nextBit(){
		int sum = 0;
		for (int i = 0; i < connections.length; i++) {
			if(connections[i]==1)
				sum+=register.get(i);
		}
		return sum % q;
	}
	public int[] sequence(){
		return this.sequence;
	}
	public String toString(){
		String s = "";
		for (int i = 0; i < sequence.length; i++) {
			s+=sequence[i];
		}
		return s;
	}
	private void generateSequence(){
		for (int i = 0; i < (int)Math.pow(q, L) - 1; i++) {
			shift();
		}
	}
	public static void main(String[] args) {
		int[] connections1 = {1,1,0,0};
		LinkedList<Integer> reg1 = new LinkedList<Integer>();
		reg1.add(0);
		reg1.add(0);
		reg1.add(0);
		reg1.add(1);
		DeBruijn s1 = new DeBruijn(2, 4, connections1, reg1);
		int[] connections2 = {1,1,0,1,3};
		LinkedList<Integer> reg2 = new LinkedList<Integer>();
		reg2.add(0);
		reg2.add(0);
		reg2.add(0);
		reg2.add(0);
		reg2.add(1);
		DeBruijn s2 = new DeBruijn(5, 4, connections2, reg2);
		System.out.println(s1.toString());
		System.out.println(s2.toString());
		for (int i = 0; i < connections2.length; i++) {
			
		}
		
	}
	
}
