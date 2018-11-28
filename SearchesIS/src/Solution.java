import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;


public class Solution {
	public static void main (String[] args) throws FileNotFoundException {
		Parser parser = new Parser();
		/*System.out.println("START STATE with agent represented by '1', agent position: ("  + problem.agentPos.y + ", " + problem.agentPos.x + ")");
		for (int i = 0; i < problem.startState.length; i++) {
			for (int j = 0; j < problem.startState.length; j++)
				if (problem.agentPos.y == i && problem.agentPos.x == j)
					System.out.print("1");
				else
					System.out.print(problem.startState[i][j]);
			System.out.println();
		}
		System.out.println();
		System.out.println("GOAL STATE");
		for (int i = 0; i < problem.goalState.length; i++) {
			for (int j = 0; j < problem.goalState.length; j++)
					System.out.print(problem.goalState[i][j]);
			System.out.println();
		}
		System.out.println();*/

		//solution.printSolution(solution.treeSearch(problem, strategy));
		//System.out.println("TOTAL NODES VISITED: " + solution.nodesVisited);
		for (int i = 1; i <= 15; i++) {
			Problem problem = parser.getProblem();
			Problem.Strategy strategy = parser.getStrategy();
			Solution solution = new Solution ();
			//for (int j = 0; j < 50; j++)//{
			solution.treeSearch(problem, strategy);
			//System.out.println(solution.maximumDepth);
			System.out.println(solution.nodesInMemory);//}
		}
		
	}
	int nodesVisited = 1, nodesExpanded = 0, maximumDepth = -1, nodesInMemory = -1;
	Node treeSearch (Problem problem, Problem.Strategy strategy) {
		Fringe fringe = new Fringe();
		Node root = init(problem);
		fringe.add(root);
		while (true) {
			if (fringe.isEmpty() == true)
				return null;
			//System.out.println("pop");
			if (fringe.size() > nodesInMemory)
				nodesInMemory = fringe.size();
			Node node = fringe.pop(problem, strategy);
			//System.out.println(node.action + " " + node.depth);
			if (maximumDepth < node.depth)
				maximumDepth = node.depth;
			if (problem.isGoalState(node))
				return node;
			/*if (nodesExpanded > 3000000) {
				nodesExpanded = -1;
				return null;
			}*/
			//System.out.println(nodesExpanded);
			nodesExpanded++;
			List<Node> toAdd = expand (node, problem, strategy, fringe);
			nodesVisited += toAdd.size();
			fringe.addAll(toAdd);
		}
	}
	
	List<Node> expand (Node node, Problem problem, Problem.Strategy strategy, Fringe fringe) {
		switch (strategy) {
			case BFS:
				return expandBFS(node, problem);
			case DFS:
				return expandDFS(node, problem);
			case IDFS:
				return expandIterativeDFS(node, problem, strategy, fringe.isEmpty());
			case AStar:
				return expandBFS (node, problem);
		}
		return null;
	}
	
	void printSolution (Node node) {
		List<Node> solutionPath = new ArrayList<>();
		while (node.parent != null) {
			//System.out.println (node.action);
			solutionPath.add(node);
			node = node.parent;
		}
		solutionPath.add(node);
		Collections.reverse(solutionPath);
		for (int i = 1; i < solutionPath.size(); i++) {
			Node n = solutionPath.get(i), nP = solutionPath.get(i - 1);
			System.out.print("(" + n.action + ")" + " Agent moves from " + "(" + nP.state.agent.y + ", " + nP.state.agent.x + ") to "
					+ "(" + n.state.agent.y + ", " + n.state.agent.x + ") " );
			if (nP.state.config[n.state.agent.y][n.state.agent.x] != '0')
				System.out.print("--- Block " + nP.state.config[n.state.agent.y][n.state.agent.x] + " moved to " + "(" + nP.state.agent.y + ", " + nP.state.agent.x + ") " );
			System.out.println();
		}
		//System.out.print("(" + solutionPath.get(i).state.agent.y + ", " + n.state.agent.x);
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
	
	List<Node> expandIterativeDFS (Node parent, Problem problem, Problem.Strategy strategy, boolean isEmpty) {
		int i = 0;
		i = (i + 1) % 10;
		if (parent.depth < strategy.depth)
			return expandBFS (parent, problem);
		List<Node> list = new LinkedList<>();
		//System.out.println(strategy.depth);
		if (isEmpty) {
			strategy.depth++;
			list.add(init(problem));
			return list;
		}
		return list;
	}
	Node createNode (Node parent, Problem.Actions action, Problem problem) {
		Node node = new Node();
		node.depth = parent.depth + 1;
		node.action = action;
		node.parent = parent;
		node.pathCost = node.parent.pathCost + problem.pathCost;
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
		Node pop (Problem problem, Problem.Strategy strategy) {
			switch (strategy) {
				case BFS:
					return pop();
				case DFS:
					return removeLast();
				case IDFS:
					return removeLast();
				case AStar: {
					return remove(getMinimumIndex(problem, this)); 
				}
			}
			return null;
		}
	}

	int getMinimumIndex (Problem problem, Fringe fringe) {
		int minimum = Integer.MAX_VALUE, pos = 0;
		Iterator<Node> it = fringe.iterator();
		Node node;
		for (int i = 0; i < fringe.size(); i++) {
			node = it.next();
			int estimatedCost = estimatedCost(problem, node);
			if (minimum > estimatedCost + node.pathCost) {
				minimum = estimatedCost + node.pathCost;
				pos = i;
			}
		}
		return pos;
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
	
	private int estimatedCost (Problem problem, Node node) {
		int score = 0, i, j;
		for (i = 0; i < node.state.config.length; i++)  {
			for (j = 0; j < node.state.config[i].length; j++) {
				if (node.state.config[i][j] != '0')
					score += getManhattan (new Position (i, j), findPosition (problem, node.state.config[i][j]));
			}
		}
		return score;
	}
	
	private int getManhattan (Position p1, Position p2) {
		return Math.abs(p1.y - p2.y) + Math.abs(p1.x - p2.x);
	}
	
	private Position findPosition (Problem problem, char c) {
		for (int i = 0; i < problem.goalState.length; i++) 
			for (int j = 0; j < problem.goalState[i].length; j++)
				if (problem.goalState[i][j] == c) 
					return new Position (i, j);
				
		return null;
	}
	
	private Node init (Problem problem) {
		Node root = new Node();
		root.state.config = copyChar(problem.startState);
		root.state.agent = new Position(problem.agentPos);
		return root;
	}
}
