package classes;

import java.util.Comparator;

public class NodeComparator implements Comparator<Node> {
	public int compare(Node a, Node b) {
		return a.getHeuristicValue() - b.getHeuristicValue();
	}
}
