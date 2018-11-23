

public class Node {
	State state = new State();
	Node parent;
	Problem.Actions action;
	int depth, pathCost;
	class State {
		char[][] config;
		Position agent = new Position(0,0);
	}
}
