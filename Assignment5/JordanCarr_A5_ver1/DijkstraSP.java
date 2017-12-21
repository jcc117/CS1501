//Jordan Carr
//Assignment 5
//Author's modified Dijkstra shortest path algorithm
//Removed main method

/*************************************************************************
 *  Compilation:  javac DijkstraSP.java
 *  Execution:    java DijkstraSP V E
 *  Dependencies: EdgeWeightedDigraph.java IndexMinPQ.java Stack.java DirectedEdge.java
 *
 *  Dijkstra's algorithm. Computes the shortest path tree.
 *  Assumes all weights are nonnegative.
 *
 *************************************************************************/

public class DijkstraSP {
    private double[] distTo;          // distTo[v] = distance  of shortest s->v path
    private DirectedEdge[] edgeTo;    // edgeTo[v] = last edge on shortest s->v path
    private IndexMinPQ<Double> pq;    // priority queue of vertices

    public DijkstraSP(DirectedEdge[] G, int s, int V) {	//Changed to take my own graph and V
        distTo = new double[V];
        edgeTo = new DirectedEdge[V];
        for (int v = 0; v < V; v++)
            distTo[v] = Double.POSITIVE_INFINITY;
        distTo[s] = 0.0;

        // relax vertices in order of distance from s
        pq = new IndexMinPQ<Double>(V);	//Mod - Takes argument V
        pq.insert(s, distTo[s]);
        while (!pq.isEmpty()) {
            int v = pq.delMin();
			
            //for (DirectedEdge e : G.adj(v))
                //relax(e);
			
			DirectedEdge e = G[v];	//Modified to fit my implementation of the graph
			while(e != null)		//to relax e
			{
				relax(e);
				e = e.getNext();
			}
        }

        // check optimality conditions
        assert check(G, s, V);
    }

    // relax edge e and update pq if changed
    private void relax(DirectedEdge e) {
        int v = e.from(), w = e.to();
        if (distTo[w] > distTo[v] + e.weight()) {
            distTo[w] = distTo[v] + e.weight();
            edgeTo[w] = e;
            if (pq.contains(w)) pq.change(w, distTo[w]);
            else                pq.insert(w, distTo[w]);
        }
    }

    // length of shortest path from s to v
    public double distTo(int v) {
        return distTo[v];
    }

    // is there a path from s to v?
    public boolean hasPathTo(int v) {
        return distTo[v] < Double.POSITIVE_INFINITY;
    }

    // shortest path from s to v as an Iterable, null if no such path
    public Iterable<DirectedEdge> pathTo(int v) {
        if (!hasPathTo(v)) return null;
        Stack<DirectedEdge> path = new Stack<DirectedEdge>();
        for (DirectedEdge e = edgeTo[v]; e != null; e = edgeTo[e.from()]) {
            path.push(e);
        }
        return path;
    }


    // check optimality conditions:
    // (i) for all edges e:            distTo[e.to()] <= distTo[e.from()] + e.weight()
    // (ii) for all edge e on the SPT: distTo[e.to()] == distTo[e.from()] + e.weight()
    private boolean check(DirectedEdge[] G, int s, int V) {	//Mod - takes V

        // check that edge weights are nonnegative
		// Modified to loop through my graph implementation
		for(int i = 0; i < G.length; i++)
		{
			DirectedEdge temp = G[i];
			while(temp != null)
			{
				if (temp.weight() < 0) {
                System.err.println("negative edge weight detected");
                return false;
            }
				temp = temp.getNext();
			}
		}

        // check that distTo[v] and edgeTo[v] are consistent
        if (distTo[s] != 0.0 || edgeTo[s] != null) {
            System.err.println("distTo[s] and edgeTo[s] inconsistent");
            return false;
        }
        for (int v = 0; v < V; v++) {
            if (v == s) continue;
            if (edgeTo[v] == null && distTo[v] != Double.POSITIVE_INFINITY) {
                System.err.println("distTo[] and edgeTo[] inconsistent");
                return false;
            }
        }

        // check that all edges e = v->w satisfy distTo[w] <= distTo[v] + e.weight()
        for (int v = 0; v < V; v++) {	//takes V
			DirectedEdge e = G[v];
			while(e != null)	//Modified to loop through my graph implementation
			{
				int w = e.to();
                if (distTo[v] + e.weight() < distTo[w]) {
                    System.err.println("edge " + e + " not relaxed");
                    return false;
				}
				e = e.getNext();
			}
        }

        // check that all edges e = v->w on SPT satisfy distTo[w] == distTo[v] + e.weight()
        for (int w = 0; w < V; w++) {	//takes V
            if (edgeTo[w] == null) continue;
            DirectedEdge temp = edgeTo[w];
            int h = temp.from();
            if (w != temp.to()) return false;
            if (distTo[h] + temp.weight() != distTo[w]) {
                System.err.println("edge " + temp + " on shortest path not tight");
                return false;
            }
        }
        return true;
    }


    /*public static void main(String[] args) {
        EdgeWeightedDigraph G;

        if (args.length == 0) {
            // read digraph from stdin
            G = new EdgeWeightedDigraph(new In());
        }
        else {
            // random digraph with V vertices and E edges, parallel edges allowed
            int V = Integer.parseInt(args[0]);
            int E = Integer.parseInt(args[1]);
            G = new EdgeWeightedDigraph(V, E);
        }

        // print graph
        StdOut.println("Graph");
        StdOut.println("--------------");
        StdOut.println(G);


        // run Dijksra's algorithm from vertex 0
        int s = 0;
        DijkstraSP sp = new DijkstraSP(G, s);
        StdOut.println();


        // print shortest path
        StdOut.println("Shortest paths from " + s);
        StdOut.println("------------------------");
        for (int v = 0; v < G.V(); v++) {
            if (sp.hasPathTo(v)) {
                StdOut.printf("%d to %d (%.2f)  ", s, v, sp.distTo(v));
                for (DirectedEdge e : sp.pathTo(v)) {
                    StdOut.print(e + "   ");
                }
                StdOut.println();
            }
            else {
                StdOut.printf("%d to %d         no path\n", s, v);
            }
        }
    }*/

}