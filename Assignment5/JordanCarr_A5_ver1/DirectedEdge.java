//Jordan Carr
//Assignment 5
//Author's Directed Edge class, modified by removing the main method and adding a next field with set and get methods
//Modified to be comparable
/*************************************************************************
 *  Compilation:  javac DirectedEdge.java
 *  Execution:    java DirectedEdge
 *
 *  Immutable weighted directed edge.
 *
 *************************************************************************/

/**
 *  The <tt>DirectedEdge</tt> class represents a weighted edge in an directed graph.
 *  <p>
 *  For additional documentation, see <a href="http://algs4.cs.princeton.edu/44sp">Section 4.4</a> of
 *  <i>Algorithms, 4th Edition</i> by Robert Sedgewick and Kevin Wayne.
 */

public class DirectedEdge implements Comparable<DirectedEdge>{ 
    private final int v;
    private final int w;
    private double weight;
	private DirectedEdge next;	//Modification - Pointer to the next edge in the vertex's list of edges

   /**
     * Create a directed edge from v to w with given weight.
     */
    public DirectedEdge(int v, int w, double weight) {
        this.v = v;
        this.w = w;
        this.weight = weight;
		next = null;
    }

   /**
     * Return the vertex where this edge begins.
     */
    public int from() {
        return v;
    }

   /**
     * Return the vertex where this edge ends.
     */
    public int to() {
        return w;
    }

   /**
     * Return the weight of this edge.
     */
    public double weight() { return weight; }
	
	//Added method
	//Change the weight of the edge
	public void changeWeight(double x)
	{
		weight = x;
	}

   /**
     * Return a string representation of this edge.
     */
    public String toString() {
        return "Edge- " + v + "->" + w + "; Weight- " + String.format("%5.2f", weight) + "; ";
    }
	
	//Set the next pointer
	public void setNext(DirectedEdge x)
	{
		next = x;
	}
	
	//Return the next pointer
	public DirectedEdge getNext()
	{
		return next;
	}
	
	public int compareTo(DirectedEdge that)
	{
		if (this.weight() < that.weight()) return -1;
		else if (this.weight() > that.weight()) return 1;
		else return 0;
	}
}