//Jordan Carr
//Graphs class, Assignment 5
//Created 7/15/17
//Modified 7/26/17

//A weighted directed graph
//Dependencies: DirectedEdge.java(modified Author's code), Queue.java (author's code),
//				LazyPrimMSTTrace.java(modified author's code), MinPQ.java (author's code)
//				DijkstraSP.java(modified author's code), Stack.java(author's code), IndexMinPQ.java(author's code)

//Details of the modifications of the author's code can be seen in the source files
//Author's code is unmodified unless stated

import java.util.*;
import java.io.*;
import java.lang.*;

public class Graph
{
	private static int V;		//Number of vertices
	private static int E;		//Number of edges
	private static DirectedEdge[] list;		//Keep track of all edges in the graph
	private static DirectedEdge[] downList;	//Keep track of downed nodes
	private static ArrayList<int[]> components;	//Keep track of components in the graph
	private static ArrayList<String> paths;		//Keep track of distinct paths in the paths method
	private static int NUM_PATHS;		//Keep track of whether distinct paths between vertices of a given weight were found or not
	
	public static void main(String args[]) throws IOException
	{
		//Read in the graph from the input file
		if(args == null)
		{
			System.out.println("Graph Not Specified");
			System.exit(0);
		}
		Scanner fileScan = new Scanner(new FileInputStream(args[0]));
		V = fileScan.nextInt();
		E  = fileScan.nextInt();
		list = new DirectedEdge[V];
		downList = new DirectedEdge[V];
		
		//Set up all edges on the graph
		while(fileScan.hasNext())
		{
			int vertex = fileScan.nextInt();
			int edge = fileScan.nextInt();
			int w = fileScan.nextInt();
			
			//Set the initial edge given by the file
			if(list[vertex] == null)
			{
				list[vertex] = new DirectedEdge(vertex, edge, w);
			}
			else
			{
				DirectedEdge temp = list[vertex];
				while(temp.getNext() != null)
				{
					temp = temp.getNext();
				}
				temp.setNext(new DirectedEdge(vertex, edge, w));
			}
			
			//Set the edge going in the opposite direction
			if(list[edge] == null)
			{
				list[edge] = new DirectedEdge(edge, vertex, w);
			}
			else
			{
				DirectedEdge temp = list[edge];
				while(temp.getNext() != null)
				{
					temp = temp.getNext();
				}
				temp.setNext(new DirectedEdge(edge, vertex, w));
			}
		}
		
		fileScan.close();
		
		//Begin the menu loop
		boolean using = true;
		Scanner input = new Scanner(System.in);
		while(using)
		{
			System.out.print("Graph Menu: ");
			String cmd = input.nextLine();
			StringTokenizer str = new StringTokenizer(cmd, " ");
			String pH = str.nextToken();
			
			//Quit(Q) Command
			if(cmd.equalsIgnoreCase("Q"))
				using = false;
			
			//*****************************************************************************************************************************************************
			//Report(R) Command
			else if(cmd.equalsIgnoreCase("R"))
			{
				System.out.println("------------------------------------------------------------------------------------");
				System.out.println("Report");
				Integer[] visited = new Integer[V];
				int comp = 0;
				Queue<Integer> q = new Queue<Integer>();
				
				//List all of the up vertices
				System.out.println("The following edges are currently up:");
				for(int i = 0; i < downList.length; i++)
				{
					if(downList[i] == null)
						System.out.print(i + " ");
				}
				System.out.println("");
				
				//List all of the downed vertices
				System.out.println("The following edges are currently down:");
				for(int i = 0; i < downList.length; i++)
				{
					if(downList[i] != null)
						System.out.print(i + " ");
				}
				System.out.println("");
				
				System.out.println("The following components and their edges are calculated via BFS search:");
				while(!allVisited(visited))
				{
					//Find an element of the component
					for(int i = 0; i < visited.length; i++)
					{
						//Ignore the vertex if it is down
						if(downList[i] != null)
						{
							visited[i] = downList[i].from();
						}
						//The vertex has no edges
						else if(visited[i] == null && downList[i] == null && list[i] == null)
						{
							System.out.println("Component " + comp++);
							System.out.println("Vertex " + i + " has no edges\n");
							visited[i] = -1;
						}
						else if(visited[i] == null)
						{
							q.enqueue(list[i].from());
							break;
						}
					}
					if(!q.isEmpty())
						System.out.println("Component " + comp++);
					//BFS through a given components
					while(!q.isEmpty())
					{
						DirectedEdge temp = list[q.dequeue()];
						//Check if the vertex has already been visited
						if(visited[temp.from()] == null)
						{
							System.out.print(temp.from() + ": ");
							//Mark the vertex as visited
							visited[temp.from()] = temp.from();
							while(temp != null)
							{
								System.out.print(temp);
								//Put the children on the queue
								q.enqueue(temp.to());
								temp = temp.getNext();
							}
							System.out.println("");
						}
					}
					System.out.println("");
				}
				
				//Print if the graph is disconnected or not
				if(comp > 1)
					System.out.println("The graph is currently disconnected");
				else
					System.out.println("The graph is currently connected");
				
				System.out.println("------------------------------------------------------------------------------------");
				
			}
			//%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
			
			//****************************************************************************************************************************************************
			//MST(M) Command
			//Does not support components yet
			else if(cmd.equalsIgnoreCase("m"))
			{
				//Create a mst object using lazy prim and print its contents
				System.out.println("------------------------------------------------------------------------------------");
				System.out.println("Minimum Spanning Tree");
				int comp = 0;
				Integer[] visited = new Integer[V];
				Queue<Integer> q = new Queue<Integer>();
				
				//Create an mst for all given components using BFS to search for them
				while(!allVisited(visited))
				{
					DirectedEdge[] comps = new DirectedEdge[V];
					//Find a vertex of the component that already hasn't been visited
					for(int i = 0; i < visited.length; i++)
					{
						//Ignore the vertex if is down
						if(downList[i] != null)
						{
							visited[i] = downList[i].from();
						}
						else if(visited[i] == null && downList[i] == null && list[i] == null)
						{
							System.out.println("Vertex " + i + " has no edges");
							visited[i] = -1;
						}
						else if(visited[i] == null)
						{
							q.enqueue(list[i].from());
							break;
						}
					}
					
					//Put all vertices from the component into the temporary graph(comps)
					while(!q.isEmpty())
					{
						DirectedEdge temp = list[q.dequeue()];
						if(visited[temp.from()] == null)
						{
							visited[temp.from()] = temp.from();
							//Add the edge to the array
							if(comps[temp.from()] == null)
							{
								comps[temp.from()] = temp;
							}
							
							//Add children to the queue to be further processed
							while(temp != null)
							{
								q.enqueue(temp.to());
								temp = temp.getNext();
							}
							
						}
						
					}
					
					System.out.println("Component " + comp++);
					System.out.println("The following edges make up the MST:");
					
					//Calculate number of edges in this graph
					int newE = 0;
					for(int i = 0; i < comps.length; i++)
					{
						DirectedEdge temp2 = comps[i];
						while(temp2 != null)
						{
							temp2 = temp2.getNext();
							newE++;
						}
					}
					
					//Find the mst for the component and output its results
					LazyPrimMSTTrace mst = new LazyPrimMSTTrace(comps, V, E);
					Queue<DirectedEdge> qu = mst.edges();
					while(!qu.isEmpty())
					{
						System.out.println(qu.dequeue());
					}
					System.out.println("Total Weight: " + mst.weight() + "\n");
					System.out.println("------------------------------------------------------------------------------------");
				}
			}
			//%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
			
			//****************************************************************************************************************************************************
			//Shortest Path(S) Command
			else if(pH.equalsIgnoreCase("s"))
			{
				//Using Dijkstra's SP algorithm
				//Modified version of the author's code
				try
				{
					System.out.println("------------------------------------------------------------------------------------");
					//Parse input tokens from the user
					int from = Integer.parseInt(str.nextToken());
					int to = Integer.parseInt(str.nextToken());
					
					System.out.println("Shortest path from " + from + " to " + to);
					
					//Create a DijkstraSP object
					DijkstraSP dij = new DijkstraSP(list, from, V);
					
					Iterable<DirectedEdge> path = dij.pathTo(to);
					if(path == null)
					{
						System.out.println("No path from " + from + " to " + to + " exists");
					}
					else
					{
						for(DirectedEdge e : path)
						{
							System.out.println(e);
						}
					}
				}
				catch(NoSuchElementException e)
				{
					System.out.println("Invalid Command: insufficient number of arguments");
				}
				catch(NumberFormatException e2)
				{
					System.out.println("Invalid Command: invalid vertex formatting");
				}
				catch(IndexOutOfBoundsException e3)
				{
					System.out.println("Invalid Command: invalid vertex id's");
				}
				System.out.println("------------------------------------------------------------------------------------");
				
			}
			//%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
			
			//*****************************************************************************************************************************************************
			//Distinct Paths(P) Command
			else if(pH.equalsIgnoreCase("p"))
			{
				try
				{
					//Reinitialize paths for the new call to this command
					System.out.println("------------------------------------------------------------------------------------");
					paths = new ArrayList<String>();
					
					//Get the arguments
					int from = Integer.parseInt(str.nextToken());
					int to = Integer.parseInt(str.nextToken());
					double maxW = Double.parseDouble(str.nextToken());
					
					System.out.println("Paths from " + from + " to " + to + " of maximum weight " + maxW);
					
					DirectedEdge temp = list[from];
					NUM_PATHS = 0;		//Set the number of found paths to 0
					Integer[] visited = new Integer[V];	//Keeps track of which edges were visited in a path
					//Print a message if no paths were found
					pathFinder(temp, to, maxW, 0.0, visited);
					if(NUM_PATHS == 0)
						System.out.println("No paths between " + from + " and " + to + " of maximum weight " + maxW + " were found");
					else
						System.out.println("Number of paths found: " + NUM_PATHS);
					
					
				}
				catch(NoSuchElementException e)
				{
					System.out.println("Invalid Command: insufficient number of arguments");
				}
				catch(NumberFormatException e2)
				{
					System.out.println("Invalid Command: invalid vertex formatting");
				}
				catch(IndexOutOfBoundsException e3)
				{
					System.out.println("Invalid Command: invalid vertex id's");
				}
				System.out.println("------------------------------------------------------------------------------------");
			}
			//%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
			
			//*****************************************************************************************************************************************************
			//Down(D) Command
			else if(pH.equalsIgnoreCase("d"))
			{
				//Take a vertex off of the network
				try
				{
					System.out.println("------------------------------------------------------------------------------------");
					int vertex = Integer.parseInt(str.nextToken());
					//Check if the vertex is already down
					if(downList[vertex] != null)
					{
						System.out.println("Vertex " + vertex + " is already down");
					}
					else
					{
						//Store the vertex's information in the list of down vertexes
						downList[vertex] = list[vertex];
						DirectedEdge temp = downList[vertex];
						
						//Delete the opposite direction edges from the graph
						while(temp != null)
						{
							int to = temp.to();
							int from = temp.from();
							
							DirectedEdge temp2 = list[to];
							//Find the edge in the graph
							while(temp2 != null && temp2.to() != from)
							{
								temp2 = temp2.getNext();
							}
							deleteEdge(temp2, to);
							
							temp = temp.getNext();
						}
						list[vertex] = null;
						System.out.println("Vertex " + vertex + " is now down");
					}
				}
				catch(NoSuchElementException e)
				{
					System.out.println("Invalid Command: insufficient number of arguments");
				}
				catch(NumberFormatException e2)
				{
					System.out.println("Invalid Command: invalid vertex formatting");
				}
				catch(IndexOutOfBoundsException e3)
				{
					System.out.println("Invalid Command: invalid vertex id's");
				}
				System.out.println("------------------------------------------------------------------------------------");
			}
			//%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
			
			//*****************************************************************************************************************************************************
			//Up(U) Command
			else if(pH.equalsIgnoreCase("u"))
			{
				//Put a vertex back online
				try
				{
					System.out.println("------------------------------------------------------------------------------------");
					int vertex = Integer.parseInt(str.nextToken());
					//Check if the vertex is already up
					if(list[vertex] != null)
					{
						System.out.println("Vertex " + vertex + " is already up");
					}
					else
					{
						list[vertex] = downList[vertex];
						DirectedEdge temp = list[vertex];
						//Re-add the edges going in the opposite directions
						while(temp != null)
						{
							addEdge(temp.to(), temp.from(), temp.weight());
							temp = temp.getNext();
						}
						
						//Remove the edge from the down list
						downList[vertex] = null;
						System.out.println("Vertex " + vertex + " is now up");
					}
				}
				catch(NoSuchElementException e)
				{
					System.out.println("Invalid Command: insufficient number of arguments");
				}
				catch(NumberFormatException e2)
				{
					System.out.println("Invalid Command: invalid vertex formatting");
				}
				catch(IndexOutOfBoundsException e3)
				{
					System.out.println("Invalid Command: invalid vertex id's");
				}
				System.out.println("------------------------------------------------------------------------------------");
			}
			//%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
			
			//*****************************************************************************************************************************************************
			//Change Weight(C) Command
			//Note: must change to check for downed vertices
			else if(pH.equalsIgnoreCase("c"))
			{
				try
				{
					System.out.println("------------------------------------------------------------------------------------");
					//Get what edge to modify
					String t1 = str.nextToken();
					String t2 = str.nextToken();
					String t3 = str.nextToken();
					int from = Integer.parseInt(t1);
					int to = Integer.parseInt(t2);
					double weight = Double.parseDouble(t3);
					
					//Check for downed vertices
					if(downList[from] != null || downList[to] != null)
					{
						System.out.println("Invalid Command: contains downed vertices");
					}
					else
					{
						//Find the vertices in the graph
						DirectedEdge temp = list[from];
						while(temp != null && temp.to() != to)
						{
							temp = temp.getNext();
						}
						processWeightChange(temp, weight, from, to);
						
						//Do the same for the edge in the opposite direction
						temp = list[to];
						while(temp != null && temp.to() != from)
						{
							temp = temp.getNext();
						}
						processWeightChange(temp, weight, to, from);
					}
				}
				catch(NoSuchElementException e)
				{
					//For parsing tokens
					System.out.println("Invalid Command: insufficient number of arguments");
				}
				catch(IndexOutOfBoundsException e2)
				{
					//For getting proper vertex id's
					System.out.println("Invalid Command: invalid vertex id's");
				}
				catch(NumberFormatException e3)
				{
					//For getting properly formatted number arguments
					System.out.println("Invalid Command: invalid vertex formatting");
				}
				System.out.println("------------------------------------------------------------------------------------");
			}
			//%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
			//Invalid Command
			else
				System.out.println("Invalid Command");
		}
	}
	
	//^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^
	//Helper functions
	//^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^
	
	//Process the weight change of an edge in one direction
	private static void processWeightChange(DirectedEdge temp, double weight, int from, int to)
	{
		//If the edge is not found, create it(unless weight is 0, in which case do nothing)
		if(temp == null && weight > 0)
		{
			//Create a new edge if none exists
			addEdge(from, to, weight);
			System.out.println("Edge " + from + "->" + to + " with weight " + weight + " has been created");
		}
		else if(temp != null && weight > 0)
		{
			//Change the weight of the existing edge
			temp.changeWeight(weight);
			System.out.println("New weight of Edge " + from + "->" + to + " is now " + weight);
		}
		else if(temp != null && weight <= 0)
		{
			//Delete the edge
			deleteEdge(temp, from);
			System.out.println("Edge " + from + "->" + to + " has been deleted");
		}
	}
	//Delete an edge from the graph
	private static void deleteEdge(DirectedEdge temp, int from)
	{
		//Check if it is the root
		if(temp == list[from])
		{
			list[from] = temp.getNext();
		}
		//Check if it is the end/end of the list
		else if(temp.getNext() == null)
		{
			DirectedEdge temp2 = list[from];
			while(temp2.getNext() != temp)
			{
				temp2 = temp2.getNext();
			}
			//Remove from end of the list
			temp2.setNext(null);
		}
		//In the middle of the list
		else
		{
			DirectedEdge temp2 = list[from];
			while(temp2.getNext() != temp)
			{
				temp2 = temp2.getNext();
			}
			//Remove from end of the list
			temp2.setNext(temp.getNext());
		}
	}
	//Add an edge to the graph
	private static void addEdge(int from, int to, double weight)
	{
		DirectedEdge newEdge = new DirectedEdge(from, to, weight);
			
		//Add to the list
		newEdge.setNext(list[from]);
		list[from] = newEdge;
	}
	//Check if all vertices were visited in a search
	private static boolean allVisited(Integer[] visited)
	{
		for(int i = 0; i < visited.length; i++)
		{
			if(visited[i] == null)
				return false;
		}
		return true;
	}
	
	//Paths recursive algorithm
	//Calculates the distinct number of paths from vertex a to b of max weight w
	private static void pathFinder(DirectedEdge edge, int to, double maxW, double weight, Integer[] visited)
	{
		while(edge != null)
		{
			//Check if the target was found
			if(edge.to() == to && (weight + edge.weight()) <= maxW)
			{
				paths.add(edge.toString());
				System.out.println("Path found:");
				for(int i = 0; i < paths.size(); i++)
				{
					System.out.println(paths.get(i));
				}
				System.out.println("Weight: " + (weight + edge.weight()));
				System.out.println("");
				paths.remove(paths.size() - 1);
				NUM_PATHS++;
			}
			else if(((weight + edge.weight()) <= maxW) && visited[edge.to()] == null)
			{
				weight += edge.weight();
				paths.add(edge.toString());	//Add the edge to the path
				visited[edge.from()] = edge.from();	//Mark the edge as visited
				DirectedEdge newEdge = list[edge.to()];	
				pathFinder(newEdge, to, maxW, weight, visited);	//Visit the next vertex
				paths.remove(paths.size() - 1);	//Remove the edge from the path
				visited[edge.from()] = null;	//Mark the path as not visited
				weight -= edge.weight();
			}
			edge = edge.getNext();
		}
	}
}