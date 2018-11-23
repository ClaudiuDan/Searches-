
final public class Problem {
	public char[][] startState, goalState;
	public int pathCost = 1;
	Position agentPos;
	
	Problem (char[][] startState, char[][] goalState, Position agentPos) {
		this.startState = startState;
		this.goalState = goalState;
		this.agentPos = agentPos;
	}
	
	public enum Actions {
	    LEFT (0, -1),
	    UP (-1, 0),
	    RIGHT (0, 1),
	    DOWN (1, 0);
	    int y, x;
	    Actions(int y, int x) {
	    	this.y = y;
	    	this.x = x;
	    }
	}
	
	public enum Strategy {
		BFS,
		DFS,
		IDFS;
		int depth = 0;
	}
	public boolean isGoalState (Node node) {
		for (int i = 0; i < node.state.config.length; i++) { //System.out.println();
			for (int j = 0; j < node.state.config[i].length; j++) {
				//System.out.print (node.state.config[i][j] + " ");
				if (node.state.config[i][j] != goalState[i][j])
					return false;
			}
		}
		return true;
	}
	
	public int getPathCost () {return 0;}
	
}
