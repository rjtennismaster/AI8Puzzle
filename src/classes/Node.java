package classes;

import java.util.*;

public class Node {
	//fields

	private ArrayList<ArrayList<Object>> state;
	
	private Node parent;
	
	private String move;
	
	private int blankTileRow;
	
	private int blankTileColumn;
	
	private int heuristicValue;
	
	
	//Constructors
	public Node(ArrayList<ArrayList<Object>> state, Node parent) {
		this.state = state;
		this.parent = parent;
	}
	
	public Node(ArrayList<ArrayList<Object>> state, Node parent, String move) {
		this.state = state;
		this.parent = parent;
		this.move = move;
	}
	
	//Utility
	
	//gets the value of a tile in the state
	public String tileValue(int row, int col) {
		return this.getState().get(row).get(col).toString();
	}
	
	//finds and sets the position of the blank tile
	public void confirmBlankTilePosition() {
		for (int row = 0; row < 3; row++) {
			for (int col = 0; col < 3; col++) {
				if (this.tileValue(row, col).equals("b")) {
					this.setBlankTileRow(row);
					this.setBlankTileColumn(col);
				}
			}
		}
	}
	
	//depending on the heuristic chosen, returns its value in respect to this node
	public int chosenHeuristicValue() {
		if (Puzzle.heuristicID == 0) {
			return this.misplacedTilesHeuristic(Puzzle.goalState);
		}
		else {
			return this.distanceHeuristic(Puzzle.goalState);
		}
	}
	
	//returns misplaced tiles heuristic value
	private int misplacedTilesHeuristic(ArrayList<ArrayList<Object>> goal) {
		int differences = 0;
		
		for (int row = 0; row < 3; row++) {
			for (int col = 0; col < 3; col++) {
				if (this.tileValue(row, col).equals(goal.get(row).get(col).toString())) {
					//do nothing
				}
				else {
					differences++;
				}
			}
		}
		
		return differences;
	}
	
	//returns distance heuristic value
	private int distanceHeuristic(ArrayList<ArrayList<Object>> goal) {
		int distance = 0;
		String element;
		
		for (int row = 0; row < 3; row++) {
			for (int col = 0; col < 3; col++) {
				if (this.tileValue(row, col).equals(goal.get(row).get(col).toString())) {
					//do nothing
				}
				else {
					element = this.tileValue(row, col);
					distance += this.distanceHelper(element, row, col, goal);
				}
			}
		}
		
		return distance;
	}
	
	private int distanceHelper(String element, int Xcoord, int Ycoord, ArrayList<ArrayList<Object>> goal) {
		int distance = 0;
		
		for (int row = 0; row < 3; row++) {
			for (int col = 0; col < 3; col++) {
				if (goal.get(row).get(col).toString().equals(element)) {
					distance = Math.abs(Xcoord - row) + Math.abs(Ycoord - col);
				}
			}
		}
		
		return distance;
	}

	//Getters/Setters
	public ArrayList<ArrayList<Object>> getState() {
		return state;
	}

	public Node getParent() {
		return parent;
	}

	public String getMove() {
		return move;
	}

	public int getBlankTileRow() {
		return blankTileRow;
	}

	public int getBlankTileColumn() {
		return blankTileColumn;
	}

	public int getHeuristicValue() {
		return heuristicValue;
	}

	public void setState(ArrayList<ArrayList<Object>> state) {
		this.state = state;
	}

	public Node setParent(Node parent) {
		this.parent = parent;
		return this;
	}

	public void setMove(String move) {
		this.move = move;
	}

	public void setBlankTileRow(int blankTileRow) {
		this.blankTileRow = blankTileRow;
	}

	public void setBlankTileColumn(int blankTileColumn) {
		this.blankTileColumn = blankTileColumn;
	}

	public void setHeuristicValue(int heuristicValue) {
		this.heuristicValue = heuristicValue;
	}

	@Override
	public String toString() {
		return "Node [state=" + state + ", parent=" + parent + ", move=" + move
				+ ", blankTileRow=" + blankTileRow + ", blankTileColumn="
				+ blankTileColumn + ", heuristicValue=" + heuristicValue + "]";
	}
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
}
