import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.Scanner;

public class Parser {
	Scanner reader;
	
	Parser () throws FileNotFoundException { reader = new Scanner (new FileReader (new File ("problem.txt")));}
	public Problem getProblem () throws FileNotFoundException {
		int n = reader.nextInt(), m = reader.nextInt();
		char[][] startState = new char[n][m], goalState = new char[n][m];
		String s = reader.nextLine();
		
		for (int i = 0; i < n; i++) {
			s = reader.nextLine();
			for (int j = 0; j < m; j++)
				startState[i][j] = s.charAt(j);
		}
		s = reader.nextLine();
		for (int i = 0; i < n; i++) {
			s = reader.nextLine();
			for (int j = 0; j < m; j++)
				goalState[i][j] = s.charAt(j);
		}
		
		return new Problem (startState, goalState, new Position(reader.nextInt(), reader.nextInt()));
	}
	
	public Problem.Strategy getStrategy () {
		reader.nextLine();
		String strategy = reader.nextLine();
		reader.nextLine();
		return Problem.Strategy.valueOf(strategy);
	}
}
