package classes;

import java.util.*;
import java.io.*;

public class Puzzle {
	
	//starting config to make sure there is a 3x3 matrix. You can still use commands to set the state.
	private static ArrayList<Object> currentRow1 = new ArrayList<>(Arrays.asList("b", 1, 2));
	private static ArrayList<Object> currentRow2 = new ArrayList<>(Arrays.asList(3, 4, 5));
	private static ArrayList<Object> currentRow3 = new ArrayList<>(Arrays.asList(6, 7, 8));
	
	private static ArrayList<Object> goalRow1 = new ArrayList<>(Arrays.asList("b", 1, 2));
	private static ArrayList<Object> goalRow2 = new ArrayList<>(Arrays.asList(3, 4, 5));
	private static ArrayList<Object> goalRow3 = new ArrayList<>(Arrays.asList(6, 7, 8));
	
	//used in search
	private static int maxNodes = Integer.MAX_VALUE;
	private static int nodesConsidered;
	private static int movesMade;
	private static int depth;
	private static Random random = new Random(2);
	
	static int heuristicID = 0;
	
	
	private static Node currentNode = new Node(new ArrayList<>(Arrays.asList(currentRow1, currentRow2, currentRow3)), null);
	static ArrayList<ArrayList<Object>> goalState = new ArrayList<>(Arrays.asList(goalRow1, goalRow2, goalRow3));
	
	public static void main(String[] args) {
		
		System.out.println("Enter your command file path");
		
		try (BufferedReader reader = new BufferedReader(new InputStreamReader(System.in))) {
			
			String path = reader.readLine();
			
		
			final ArrayList<String[]> lines = new ArrayList<>();
			
			final File file = new File(path);
        
			final Scanner scan = new Scanner(file);
            // read the file commands into a list of commands
            // and tokenize them into String[]
        	
            while (scan.hasNextLine()) {
                // tokenize the line with whitespace
                final String[] line = scan.nextLine().trim().split("\\s+");
                // ignore blank lines
                if (line.length > 0) {
                    lines.add(line);
                }
            }
            scan.close();

        // handle commands
        for (String[] line : lines) {
                // handle input commands randomizeState, setState, etc. in the switch block
                final String command = line[0];
                if (command.equalsIgnoreCase("randomizeState")) {
                    final int moveCount = Integer.parseInt(line[1]);
                    // apply randomizeState function
                    randomizeState(moveCount);
                } else if (command.equalsIgnoreCase("setState")) {
                    // access the puzzle rows in line[1], line[2], line[3]
                	ArrayList<Object> list1 = listOfObjects(line[1]);
                	ArrayList<Object> list2 = listOfObjects(line[2]);
                	ArrayList<Object> list3 = listOfObjects(line[3]);
                    // for input such as "b12 345 678"
                	ArrayList<ArrayList<Object>> newState = new ArrayList<>(Arrays.asList(list1, list2, list3));
                    // call setState using these values
                	currentNode.setState(newState);
                } else if (command.equalsIgnoreCase("move")) {
                    final String direction = line[1];
                    
                 // move the puzzle in the specified direction
                    if (direction.equalsIgnoreCase("up")) {
                    	currentNode = moveUp(currentNode);
                    }
                    else if (direction.equalsIgnoreCase("down")) {
                    	currentNode = moveDown(currentNode);
                    }
                    else if (direction.equalsIgnoreCase("left")) {
                    	currentNode = moveLeft(currentNode);
                    }
                    else if (direction.equalsIgnoreCase("right")) {
                    	currentNode = moveRight(currentNode);
                    }
                    else {
                    	System.out.printf("Invalid direction: %s", direction);
                    }
                } else if (command.equalsIgnoreCase("printState")) {
                    // print the state of the puzzle
                	printState(currentNode);
                } else if (command.equalsIgnoreCase("maxNodes")) {
                    final int max = Integer.parseInt(line[1]);
                    // set the maximum nodes
                     	maxNodes = max;
                } else if (command.equalsIgnoreCase("solve")) {
                    final String algorithm = line[1];
                    if (algorithm.equalsIgnoreCase("A-star")) {
                        // h1 or h2
                        final String heuristic = line[2];
                        // call A-star here with the specified heuristic
                        if (heuristic.equalsIgnoreCase("h1")) {
                        	heuristicID = 0;
                        	long timeStart = System.currentTimeMillis();
                        	aStarSearch();
                        	long timeEnd = System.currentTimeMillis();
                        	long timeDiff = timeEnd - timeStart;
							double secondsPassed = timeDiff / 1000.0;
							System.out.println("Time Taken " + secondsPassed);
                        }
                        else if (heuristic.equalsIgnoreCase("h2")) {
                        	heuristicID = 1;
                        	long timeStart = System.currentTimeMillis();
                        	aStarSearch();
                        	long timeEnd = System.currentTimeMillis();
                        	long timeDiff = timeEnd - timeStart;
							double secondsPassed = timeDiff / 1000.0;
							System.out.println("Time Taken " + secondsPassed);
                        }
                        else {
                        	System.out.printf("Invalid heuristic : %s", heuristic);
                        }
                    } else if (algorithm.equalsIgnoreCase("beam")) {
                        final int k = Integer.parseInt(line[2]);
                        long timeStart = System.currentTimeMillis();
                        beamSearch(k);
                        long timeEnd = System.currentTimeMillis();
                    	long timeDiff = timeEnd - timeStart;
						double secondsPassed = timeDiff / 1000.0;
						System.out.println("Time Taken " + secondsPassed);
                    } else {
                        System.out.printf("Invalid algorithm: %s", algorithm);
                    }
                } else {
                    System.out.printf("Invalid Command: %s", command);
                }
        }
		} catch (Exception e) {
                System.err.println(e.getMessage());
                e.printStackTrace();
        } 
	}
	
	//A Star Search on current node 
	public static void aStarSearch() {
		//reset all tracking fields
		nodesConsidered = 0;
		movesMade = 0;
		depth = 0;
		
		//Hashset to keep track of moves we've already made
		Set<String> explored = new HashSet<>();
		
		//Priority queue where considered moves are added. Moves with lower heuristic value are prioritized.
		PriorityQueue<Node> pq = new PriorityQueue<>(new NodeComparator());
		pq.add(currentNode);
		
		//List to log the moves that we've made
		List<String> moveLog = new ArrayList<>();
		
		loop1: while (!pq.isEmpty()) {
			currentNode = pq.poll();
			
			//if we found the goal, print records
			if (copy(currentNode.getState()).toString().equals(goalState.toString())) {
				System.out.println("A star search: The Goal was Found!");
				printState(currentNode);
				System.out.println("\n");
				System.out.println("Nodes considered: " + nodesConsidered);
				System.out.println("# moves made: " + movesMade + "\n");
				
				System.out.println(moveLog);
				
				break loop1;
			}
			//if we haven't found the goal yet, decide whether or not to explore this node
			else {
				//if we haven't seen this move yet
				if (!explored.contains(currentNode.getState().toString())) {
					explored.add(currentNode.getState().toString());
					movesMade++;
					
					if (Objects.nonNull(currentNode.getMove())) {
						moveLog.add(currentNode.getMove());
					}
					
					ArrayList<Node> children = new ArrayList<>(successors());
					depth++;
					
					nodesConsidered += children.size();
					
					//if we've considered too many nodes, stop
					if (nodesConsidered > maxNodes) {
						throw new IllegalThreadStateException("Too many nodes considered");
					}
					
					//set the kids' heuristic values
					for (Node kid: children) {
						kid.setHeuristicValue(kid.chosenHeuristicValue() - depth);
					}
					
					//add kids to the queue
					for (Node kid: children) {
						pq.add(kid);
					}
				}
				else {
					//do nothing since we've already seen the move
				}
			}
		}
	}
	
	//Beam Search on current node. beamWidth is the max number of children that can be considered for each parent. Eval func can 
	//be either number of misplaced tiles or sum of distances from goal
	public static void beamSearch(int beamWidth) {
		//reset all tracking fields
		nodesConsidered = 0;
		movesMade = 0;
		
		//Hashset to keep track of moves we've already made
		Set<String> explored = new HashSet<>();
		
		//FIFO queue that stores considered moves
		Queue<Node> q = new LinkedList<>();
		q.add(currentNode);
		
		//List to log the moves that we've made
		List<String> moveLog = new ArrayList<>();
		
		loop1: while (!q.isEmpty()) {
			currentNode = q.poll();
			
			//if we found the goal, print records
			if (copy(currentNode.getState()).toString().equals(goalState.toString())) {
				System.out.println("Beam search : The Goal was Found!");
				printState(currentNode);
				System.out.println("\n");
				System.out.println("Nodes considered: " + nodesConsidered);
				System.out.println("# moves made: " + movesMade + "\n");
				
				System.out.println(moveLog);
				
				break loop1;
			}
			//if we haven't found the goal yet, decide whether or not to explore this node
			else {
				//if we haven't seen this move yet
				if (!explored.contains(currentNode.getState().toString())) {
					explored.add(currentNode.getState().toString());
					movesMade++;
					
					if (Objects.nonNull(currentNode.getMove())) {
						moveLog.add(currentNode.getMove());
					}
					
					List<Node> children = new ArrayList<>(successors());
					
					//set kids' evaluation function values so we can sort them
					for (Node kid : children) {
						kid.setHeuristicValue(kid.chosenHeuristicValue());
					}
					
					//sort the kids in increasing evaluation function value, so we only consider the *beamWidth* best ones
					Collections.sort(children, new NodeComparator());
					
					//add the best kids to the queue
					int before = q.size();
					int count = 0;
					
					loop2: while (count < beamWidth) {
						if (count < children.size()) {
							q.add(children.get(count));
							count++;
						}
						else {
							break loop2;
						}
					}
					
					nodesConsidered += q.size() - before;
					
					if (nodesConsidered > maxNodes) {
						throw new IllegalThreadStateException("Too many nodes considered");
					}
				}
				else {
					//do nothing since we've already seen the move
				}
			}
		}
	}
	
	//returns a list of successor nodes
	public static ArrayList<Node> successors() {
		ArrayList<Node> kids = new ArrayList<>();
		
		try {
			kids.add(moveUp(currentNode));
		} catch (IllegalArgumentException e) {
			//try to move down next
		}
		
		try {
			kids.add(moveDown(currentNode));
		} catch (IllegalArgumentException e) {
			//try to move left next
		}
		
		try {
			kids.add(moveLeft(currentNode));
		} catch (IllegalArgumentException e) {
			//try to move right next
		}
		
		try {
			kids.add(moveRight(currentNode));
		} catch (IllegalArgumentException e) {
			//finished
		}
		
		return kids;
	}
	
	//move the blank tile up
	public static Node moveUp(Node parent) {
		parent.confirmBlankTilePosition();
		//if the blank tile isn't in the top row, move it up

		if (parent.getBlankTileRow() > 0) {
			Node child = new Node(copy(parent.getState()), parent, "Up");
			child.confirmBlankTilePosition();
			
			Object temp = child.getState().get(child.getBlankTileRow() - 1).get(child.getBlankTileColumn());
			
			//set what's above to blank tile
			child.getState().get(child.getBlankTileRow() - 1).set(child.getBlankTileColumn(), "b");
			
			//set what's below to what was above
			child.getState().get(child.getBlankTileRow()).set(child.getBlankTileColumn(), temp);
			
			return child;
		}
		else {
			throw new IllegalArgumentException("Cannot move up");
		}
	}
	
	//move the blank tile down
	public static Node moveDown(Node parent) {
		parent.confirmBlankTilePosition();
		//if the blank tile isn't in the bottom row, move it down
		
		if (parent.getBlankTileRow() < 2) {
			Node child = new Node(copy(parent.getState()), parent, "Down");
			child.confirmBlankTilePosition();
			
			Object temp = child.getState().get(child.getBlankTileRow() + 1).get(child.getBlankTileColumn());
			
			//set what's below to blank tile
			child.getState().get(child.getBlankTileRow() + 1).set(child.getBlankTileColumn(), "b");
			
			//set what's above to what was below
			child.getState().get(child.getBlankTileRow()).set(child.getBlankTileColumn(), temp);
			
			return child;
		}
		else {
			throw new IllegalArgumentException("Cannot move down");
		}
	}
	
	//move the blank tile left
	public static Node moveLeft(Node parent) {
		parent.confirmBlankTilePosition();
		//if the blank tile isn't in the left column, move it left
		
		if (parent.getBlankTileColumn() > 0) {
			Node child = new Node(copy(parent.getState()), parent, "Left");
			child.confirmBlankTilePosition();
			
			Object temp = child.getState().get(child.getBlankTileRow()).get(child.getBlankTileColumn() - 1);
			
			//set what's on the left of the blank tile
			child.getState().get(child.getBlankTileRow()).set(child.getBlankTileColumn() - 1, "b");
			
			//set what's on the right to what was on the left
			child.getState().get(child.getBlankTileRow()).set(child.getBlankTileColumn(), temp);
			
			return child;
		}
		else {
			throw new IllegalArgumentException("Cannot move left");
		}
	}
	
	//move the blank tile right
	public static Node moveRight(Node parent) {
		parent.confirmBlankTilePosition();
		//if the blank tile isn't in the right column, move it right
		
		if (parent.getBlankTileColumn() < 2) {
			Node child = new Node(copy(parent.getState()), parent, "Right");
			child.confirmBlankTilePosition();
			
			Object temp = child.getState().get(child.getBlankTileRow()).get(child.getBlankTileColumn() + 1);
			
			//set what's on the right of the blank tile
			child.getState().get(child.getBlankTileRow()).set(child.getBlankTileColumn() + 1, "b");
			
			//set what's on the left to what was on the right
			child.getState().get(child.getBlankTileRow()).set(child.getBlankTileColumn(), temp);
			
			return child;
		}
		else {
			throw new IllegalArgumentException("Cannot move right");
		}
	}
	
	//Set the current state to n moves from the current state
	public static void randomizeState(int n) {
		int count = 0;
		
		loop: while (true) {
			int num = random.nextInt(4);
			
			if (count >= n) {
				break loop;
			}
			
			try {
				currentNode = makeRandomMove(currentNode, num);
			} catch (IllegalArgumentException e) {
				continue;
			}
			
			count++;
		}
	}
	
	//Non-seeded random state
	public static void trulyRandomizeState(int n) {
		Random random2 = new Random();
		
		int count = 0;
		
		loop: while (true) {
			int num = random2.nextInt(4);
			
			if (count >= n) {
				break loop;
			}
			
			try {
				currentNode = makeRandomMove(currentNode, num);
			} catch (IllegalArgumentException e) {
				continue;
			}
			
			count++;
		}
	}
	
	//make a random move
	private static Node makeRandomMove(Node parent, int randomNum) {
		Node child = new Node(copy(parent.getState()), parent);
		if (randomNum == 0) {
			child = moveUp(child);
		}
		else if (randomNum == 1) {
			child = moveDown(child);
		}
		else if (randomNum == 2) {
			child = moveLeft(child);
		}
		else {
			child = moveRight(child);
		}
		
		return child;
	}
	
	//prints the state of the puzzle
	public static void printState(Node node) {
		for (int row = 0; row < 3; row++) {
			System.out.println("");
			for (int col = 0; col < 3; col++) {
				System.out.print(node.tileValue(row, col));
				System.out.print(" ");
			}
		}
		System.out.println("");
	}
	
	//make ArrayList of objects
	public static ArrayList<Object> listOfObjects(String s) {
		ArrayList<Object> result = new ArrayList<>();
		
		for (Character c : s.toCharArray()) {
			result.add(c);
		}
		return result;
	}
	
	//make a copy of a state
	public static ArrayList<ArrayList<Object>> copy(ArrayList<ArrayList<Object>> original) {
		ArrayList<Object> list1 = new ArrayList<>();
		ArrayList<Object> list2 = new ArrayList<>();
		ArrayList<Object> list3 = new ArrayList<>();
		
		ArrayList<ArrayList<Object>> copy = new ArrayList<>(Arrays.asList(list1, list2, list3));
		
		for (int row = 0; row < 3; row++) {
			for (int col = 0; col < 3; col++) {
				copy.get(row).add(original.get(row).get(col));
			}
		}
		
		return copy;
	}
	
	//EXPERIMENTS
	public static void aStar500MaxNodes() {
		maxNodes = 500;
		trulyRandomizeState(300);
		heuristicID = 0;
    	long timeStart = System.currentTimeMillis();
    	aStarSearch();
    	long timeEnd = System.currentTimeMillis();
    	long timeDiff = timeEnd - timeStart;
		double secondsPassed = timeDiff / 1000.0;
		System.out.println("Time Taken " + secondsPassed);
	}
	
	public static void aStar5000MaxNodes() {
		maxNodes = 5000;
		trulyRandomizeState(300);
		heuristicID = 0;
    	long timeStart = System.currentTimeMillis();
    	aStarSearch();
    	long timeEnd = System.currentTimeMillis();
    	long timeDiff = timeEnd - timeStart;
		double secondsPassed = timeDiff / 1000.0;
		System.out.println("Time Taken " + secondsPassed);
	}
	
	public static void aStar50000MaxNodes() {
		maxNodes = 50000;
		trulyRandomizeState(300);
		heuristicID = 0;
    	long timeStart = System.currentTimeMillis();
    	aStarSearch();
    	long timeEnd = System.currentTimeMillis();
    	long timeDiff = timeEnd - timeStart;
		double secondsPassed = timeDiff / 1000.0;
		System.out.println("Time Taken " + secondsPassed);
	}
	
	public static void beam500MaxNodes() {
		maxNodes = 500;
		trulyRandomizeState(300);
    	long timeStart = System.currentTimeMillis();
    	beamSearch(3);
    	long timeEnd = System.currentTimeMillis();
    	long timeDiff = timeEnd - timeStart;
		double secondsPassed = timeDiff / 1000.0;
		System.out.println("Time Taken " + secondsPassed);
	}
	
	public static void beam5000MaxNodes() {
		maxNodes = 5000;
		trulyRandomizeState(300);
    	long timeStart = System.currentTimeMillis();
    	beamSearch(3);
    	long timeEnd = System.currentTimeMillis();
    	long timeDiff = timeEnd - timeStart;
		double secondsPassed = timeDiff / 1000.0;
		System.out.println("Time Taken " + secondsPassed);
	}
	
	public static void beam50000MaxNodes() {
		maxNodes = 50000;
		trulyRandomizeState(300);
    	long timeStart = System.currentTimeMillis();
    	beamSearch(3);
    	long timeEnd = System.currentTimeMillis();
    	long timeDiff = timeEnd - timeStart;
		double secondsPassed = timeDiff / 1000.0;
		System.out.println("Time Taken " + secondsPassed);
	}
	
	public static void compareHeuristics300Moves() {
		randomizeState(300);
		heuristicID = 0;
    	long timeStart = System.currentTimeMillis();
    	aStarSearch();
    	long timeEnd = System.currentTimeMillis();
    	long timeDiff = timeEnd - timeStart;
		double secondsPassed = timeDiff / 1000.0;
		System.out.println("Time Taken h1 " + secondsPassed);
		
		randomizeState(300);
		heuristicID = 1;
    	long timeStart2 = System.currentTimeMillis();
    	aStarSearch();
    	long timeEnd2 = System.currentTimeMillis();
    	long timeDiff2 = timeEnd2 - timeStart2;
		double secondsPassed2 = timeDiff2 / 1000.0;
		System.out.println("Time Taken h2 " + secondsPassed2);
	}
	
	public static void compareHeuristics600Moves() {
		randomizeState(600);
		heuristicID = 0;
    	long timeStart = System.currentTimeMillis();
    	aStarSearch();
    	long timeEnd = System.currentTimeMillis();
    	long timeDiff = timeEnd - timeStart;
		double secondsPassed = timeDiff / 1000.0;
		System.out.println("Time Taken h1 " + secondsPassed);
		
		randomizeState(600);
		heuristicID = 1;
    	long timeStart2 = System.currentTimeMillis();
    	aStarSearch();
    	long timeEnd2 = System.currentTimeMillis();
    	long timeDiff2 = timeEnd2 - timeStart2;
		double secondsPassed2 = timeDiff2 / 1000.0;
		System.out.println("Time Taken h2 " + secondsPassed2);
	}
	
	public static void compareHeuristics1000Moves() {
		randomizeState(1000);
		heuristicID = 0;
    	long timeStart = System.currentTimeMillis();
    	aStarSearch();
    	long timeEnd = System.currentTimeMillis();
    	long timeDiff = timeEnd - timeStart;
		double secondsPassed = timeDiff / 1000.0;
		System.out.println("Time Taken h1 " + secondsPassed);
		
		randomizeState(1000);
		heuristicID = 1;
    	long timeStart2 = System.currentTimeMillis();
    	aStarSearch();
    	long timeEnd2 = System.currentTimeMillis();
    	long timeDiff2 = timeEnd2 - timeStart2;
		double secondsPassed2 = timeDiff2 / 1000.0;
		System.out.println("Time Taken h2 " + secondsPassed2);
	}
	
	public static void beam300Moves() {
		randomizeState(300);
		heuristicID = 1;
    	long timeStart3 = System.currentTimeMillis();
    	//can change to whatever number btwn 1 and 4 that you want
    	beamSearch(4);
    	long timeEnd3 = System.currentTimeMillis();
    	long timeDiff3 = timeEnd3 - timeStart3;
		double secondsPassed3 = timeDiff3 / 1000.0;
		System.out.println("Time Taken beam " + secondsPassed3);
	}
	
	public static void beam600Moves() {
		randomizeState(600);
		heuristicID = 1;
    	long timeStart3 = System.currentTimeMillis();
    	beamSearch(4);
    	long timeEnd3 = System.currentTimeMillis();
    	long timeDiff3 = timeEnd3 - timeStart3;
		double secondsPassed3 = timeDiff3 / 1000.0;
		System.out.println("Time Taken beam " + secondsPassed3);
	}
	
	public static void beam1000Moves() {
		randomizeState(1000);
		heuristicID = 1;
    	long timeStart3 = System.currentTimeMillis();
    	beamSearch(4);
    	long timeEnd3 = System.currentTimeMillis();
    	long timeDiff3 = timeEnd3 - timeStart3;
		double secondsPassed3 = timeDiff3 / 1000.0;
		System.out.println("Time Taken beam " + secondsPassed3);
	}
	
}
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	

