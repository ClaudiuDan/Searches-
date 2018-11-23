import java.io.FileNotFoundException;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;


public class Solution {
	public static void main (String[] args) throws FileNotFoundException {
		Solution solution = new Solution ();
		Parser parser = new Parser();
		Problem problem = parser.getProblem();
		Problem.Strategy strategy = parser.getStrategy();
		solution.printSolution(solution.treeSearch(problem, strategy));
	}
	
	Node treeSearch (Problem problem, Problem.Strategy strategy) {
		Fringe fringe = new Fringe();
		Node root = init(problem);
		fringe.add(root);
		while (true) {
			if (fringe.isEmpty(strategy) == true)
				return null;
			Node node = fringe.pop(strategy);
			System.out.println(node.action + " " + node.depth);
			if (problem.isGoalState(node))
				return node;
			List<Node> toAdd = expand (node, problem, strategy);
			fringe.addAll(toAdd);
		}
	}
	
	List<Node> expand (Node node, Problem problem, Problem.Strategy strategy) {
		switch (strategy) {
			case BFS:
				return expandBFS(node, problem);
			case DFS:
				return expandDFS(node, problem);
			case IDFS:
				return expandIterativeDFS(node, problem, strategy);
		}
		return null;
	}
	
	void printSolution (Node node) {
		while (node.parent != null) {
			System.out.println (node.action);
			node = node.parent;
		}
	}
	
	List<Node> expandBFS (Node parent, Problem problem) {
		List<Node> toExpand = new LinkedList<>();
		for (Problem.Actions action : Problem.Actions.values()) {
			Node node = createNode (parent, action, problem);
			if (node != null)
				toExpand.add(node);
		}
		return toExpand;
	}
	
	List<Node> expandDFS (Node parent, Problem problem) {
		List<Node> toExpand = new LinkedList<>();
		int n = Problem.Actions.values().length;
		int r = (new Random()).nextInt(n), i = r;
		for (int ct = 0; ct < n; ct++) {
			Node node = createNode (parent, Problem.Actions.values()[i], problem);
			if (node != null)
				toExpand.add(node);
			i = (i + 1) % n;
		}
		return toExpand;
	}
	
	List<Node> expandIterativeDFS (Node parent, Problem problem, Problem.Strategy strategy) {
		if (parent.depth < strategy.depth)
			return expandDFS (parent, problem);
		strategy.depth++;
		List<Node> list = new LinkedList<>(); list.add(init(problem));
		return list;
	}
	Node createNode (Node parent, Problem.Actions action, Problem problem) {
		Node node = new Node();
		node.depth = parent.depth + 1;
		node.action = action;
		node.parent = parent;
		node.pathCost = problem.pathCost;
		node.state.agent.y = parent.state.agent.y + action.y;
		node.state.agent.x = parent.state.agent.x + action.x;
		node.state.config = copyChar(parent.state.config);
		if (node.state.agent.y < 0 || node.state.agent.y >= parent.state.config.length ||
			node.state.agent.x < 0 || node.state.agent.x >= parent.state.config[parent.state.agent.y].length)
			return null;
		node.state.config[parent.state.agent.y][parent.state.agent.x] = node.state.config[node.state.agent.y][node.state.agent.x];
		node.state.config[node.state.agent.y][node.state.agent.x] = '0';
		return node;
	}
	
	class Fringe extends LinkedList<Node> {
		Node pop (Problem.Strategy strategy) {
			switch (strategy) {
				case BFS:
					return pop();
				case DFS:
					return removeLast();
				case IDFS:
					return removeLast();
			}
			return null;
		}
		boolean isEmpty (Problem.Strategy strategy) {
			switch (strategy) {
				case IDFS:
					return false;
			default:
				break;
			}
			return isEmpty();
		}
	}

	char[][] copyChar (char[][] toCopy) {
		char[][] state = new char[toCopy.length][];
		for (int i = 0; i < toCopy.length; i++) {
			state[i] = new char[toCopy[i].length];
			for (int j = 0; j < toCopy[i].length; j++) 
				state[i][j] = toCopy[i][j];
		}
		return state;
	}
	
	private Node init (Problem problem) {
		Node root = new Node();
		root.state.config = copyChar(problem.startState);
		root.state.agent = new Position(problem.agentPos);
		return root;
	}
}
