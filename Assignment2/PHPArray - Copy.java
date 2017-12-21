//Jordan Carr
//Assignment 2
//Started 6/4/17
//Modified 6/17/17

import java.util.*;
import java.lang.*;

public class PHPArray<V> implements Iterable<V>
{;
	PHPArray.Node<V>[] nodeArray;
	//Create a temporary node array used only for resizing the array above
	PHPArray.Node<V>[] temp;
	Node<V> root;
	Node<V> tail;
	int M; //Size of the array
	int N; //Number of items in the array
	private final int ALPHA_SIZE = 256;
	ArrayList<String> keyList;
	ArrayList<V> valList;
	//An iterator for the Pairs and the each method
	Node<V> pairIterator;
	
	//Constructor
	public PHPArray(int size)
	{
		nodeArray = (Node<V>[]) new Node<?>[size];
		temp = null;
		M = size;
		N = 0;
		root = null;
		keyList = new ArrayList<String>();
		valList = new ArrayList<V>();
		tail = root;
		pairIterator = root;
	}
	//Class for returning key-value pairs
	public static class Pair<V>
	{
		String key;
		V value;
		public Pair(String kee, V val)
		{
			key = kee;
			value = val;
		}
		
	}
	//Node inner class to form a doubly linked list
	private static class Node<V>
	{
		Node<V> next;
		V value;
		String key;
		Node<V> prev;
		
		public Node(String kee, V val)
		{
			value = val;
			next = null;
			key = kee;
			prev = null;
		}
	}
	//Hash the item into the array and add it into the linked list
	public void put(String kee, V val)
	{
		//If the load factor is >= 1/2, double the size of the table
		if((double)N/(double)M >= (1.0/2.0))
		{
			resize(2*M);
		}
		int hashNum = hash(kee);
		//If that index is null, add it there
		if(nodeArray[hashNum] == null)
		{
			nodeArray[hashNum] = new Node<V>(kee, val);
			//If root is null, make this the root
			if(root == null)
			{
				root = nodeArray[hashNum];
				tail = root;
				pairIterator = root;
			}
			//Else, add it to the list
			else
			{
				tail.next = nodeArray[hashNum];
				nodeArray[hashNum].prev = tail;
				tail = nodeArray[hashNum];
			}
			keyList.add(kee);
			valList.add(val);
			N++;
		}
		//If not, check the value/key there
		else
		{
			//If pair already there, update the value
			if(kee.equals(nodeArray[hashNum].key))
			{
				//Replae that value in the arrayList
				int index = valList.indexOf(nodeArray[hashNum].value);
				valList.remove(index);
				valList.add(index, val);
				nodeArray[hashNum].value = val;
			}
			//Else, increment by one til one is found
			else
			{
				int index = hashNum;
				boolean found = false;
				while(nodeArray[index] != null && !found)
				{
					index = (index + 1) % M;
					//Make sure that key isn't already there after probing for it
					if(nodeArray[index] != null && kee.equals(nodeArray[index].key))
					{
						found = true;
					}
				}
				if(found)
				{
					int valIndex = valList.indexOf(nodeArray[index].value);
					valList.remove(valIndex);
					valList.add(valIndex, val);
					nodeArray[index].value = val;
				}
				else
				{
					//Add the pair at index
					nodeArray[index] = new Node<V>(kee, val);
					tail.next = nodeArray[index];
					nodeArray[index].prev = tail;
					tail = nodeArray[index];
					keyList.add(kee);
					valList.add(val);
					N++;
				}
			}
		}
	}
	//If user passes an int as a key, convert it to a string and then call put from above
	public void put(int kee, V val)
	{
		String newKee = Integer.toString(kee);
		put(newKee, val);
	}
	//Get an item from the array
	public V get(String kee)
	{
		//Hash the key
		int hashNum = hash(kee);
		//If that index is null, return null
		if(nodeArray[hashNum] == null)
		{
			return null;
		}
		//Else, check the value
		else
		{
			//If they keys are equal, return the value
			if(nodeArray[hashNum].key.equals(kee))
			{
				return nodeArray[hashNum].value;
			}
			//Else, increment the value til the key is found
			//or null is reached, which means the key is not there
			else
			{
				int index = hashNum;
				//Increment the value
				while(nodeArray[index] != null)
				{
					//Check the keys
					if(nodeArray[index].key.equals(kee))
					{
						return nodeArray[index].value;
					}
					index = (index + 1) % M;
				}
				return null;
			}
		}
	}
	
	//If an int is passed as the key, convet it to a string and search as normal
	public V get(int kee)
	{
		return get(Integer.toString(kee));
	}
	//Delete an item from the array
	public void unset(String kee)
	{
		//Hash the key
		int hashNum = hash(kee);
		
		//Throw exception if the key points to nothing
		if(nodeArray[hashNum] == null)
		{
			throw new RuntimeException("Item not in the array.");
		}
		else
		{
			//Check the key to see if it is equal
			if(kee.equals(nodeArray[hashNum].key))
			{
				delete(nodeArray[hashNum], hashNum);
			}
			//Else increment the index until the 
			//key is found or null is reached
			else
			{
				int index = hashNum;
				boolean found = false;
				//Increment index til key is found or null is reached
				while(nodeArray[index] != null && !found)
				{
					index = (index + 1) % M;
					if(kee.equals(nodeArray[index].key))
						found = true;
				}
				//If found, delete that key
				if(found)
					delete(nodeArray[index], index);
				//Else, throw an exception
				else
					throw new RuntimeException("Item not in the array");
			}
		}
	}
	//If an int is passed as the key, convert it to a string and unset as normal
	public void unset(int kee)
	{
		unset(Integer.toString(kee));
	}
	//Private method to delete a node from the list
	private void delete(Node<V> node, int hashNum)
	{
		//Reset links to nodes
		//If it is the root, set the next node to root
		if(node == root)
		{
			//Check to make sure it is not the only node in the list
			//Note- this case is only fulfilled if it is the only node in the list, where both
			//tail and root point to the same node
			if(node.next == null)
			{
				root = null;
				tail = null;
			}
			else
			{
				node.next.prev = null;
				root = node.next;
				pairIterator = root;
			}
		}
		//If it is the tail, set the previous to tail
		else if(node == tail)
		{
			tail = node.prev;
			node.prev.next = null;
		}
		//Otherwise, relink the nodes as normal
		else
		{
			node.prev.next = node.next;
			node.next.prev = node.prev;
		}
		//Remove the key and value from the ArrayLists
		keyList.remove(node.key);
		valList.remove(node.value);
		node = null;
		
		//Remove the node from the array
		nodeArray[hashNum] = null;
		N--;
		
		//Rehash the nodes that come after it in the cluster
		int index = (hashNum + 1) % M;
		while(nodeArray[index] != null)
		{
			int newHash = hash(nodeArray[index].key);
			//If null, put it there
			if(nodeArray[newHash] == null)
			{
				nodeArray[newHash] = nodeArray[index];
				nodeArray[index] = null;
			}
			//Else, increase the index til you find an empty spot
			else
			{
				while(nodeArray[newHash] != null)
				{
					newHash = (newHash + 1) % M;
				}
				nodeArray[newHash] = nodeArray[index];
				nodeArray[index] = null;
			}
			index = (index + 1) % M;
		}
	}
	//Return an ArrayList of the keys from the linked list
	public ArrayList<String> keys()
	{
		return keyList;
	}
	//Return an ArrayList of the values from the linked list
	public ArrayList<V> values()
	{
		return valList;
	}
	//Quicksort the array and assign intergers as new key values in ascending order
	//Also mutates order of the linked list to the sorted order
	//Only works on objects that extend Comparable
	public void sort()
	{
		//Normal operation, if the size of the array is greater than 1
		if(N > 1)
		{
			Node<V>[] sorter = (Node<V>[]) new Node<?>[N];
			Node<V> sorterNode = root;
			//Copy everything to a temporary array for sorting
			for(int b = 0; b < N; b++)
			{
				sorter[b] = sorterNode;
				sorterNode = sorterNode.next;
			}
			//Using a variation of quicksort and compareTo
			quickSort(sorter, 0, sorter.length-1);
			Node<V>[] temp = (Node<V>[]) new Node<?>[M];
			Node<V> tempRoot;
			Node<V> tempTail;
			ArrayList<String> tempKeys = new ArrayList<String>();
			ArrayList<V> tempVals = new ArrayList<V>();
			
			//Create new nodes based on Integer keys
			for(int i = 0; i < N; i++)
			{
				sorter[i] = new Node<V>(Integer.toString(i), sorter[i].value);
				//Redo the key and val array lists
				tempKeys.add(sorter[i].key);
				tempVals.add(sorter[i].value);
			}
			//Set the connects of the linked list appropriately, similar to the put method
			tempRoot = sorter[0];
			tempTail = sorter[N - 1];
			for(int j = 0; j < N; j++)
			{
				//Only set next for the root
				if(sorter[j] == tempRoot)
				{
					sorter[j].next = sorter[j+1];
				}
				//Only set prev for the tail
				else if(sorter[j] == tempTail)
				{
					sorter[j].prev = sorter[j-1];
				}
				//Set next and prev otherwise
				else
				{
					sorter[j].next = sorter[j+1];
					sorter[j].prev = sorter[j-1];
				}
			}
			
			//Hash the sorted values into the temporary array
			//Notice there are no checks for duplicates since there cannot be any
			//in this implementation
			for(int k = 0; k < sorter.length; k++)
			{
				int hashNum = hash(sorter[k].key);
				if(temp[hashNum] == null)
				{
					temp[hashNum] = sorter[k];
				}
				else
				{
					int index = hashNum;
					while(temp[index] != null)
					{
						index = (index + 1) % M;
					}
					temp[index] = sorter[k];
				}
			}
			//Reset all of the class data from the temporary values
			nodeArray = temp;
			root = tempRoot;
			tail = tempTail;
			keyList = tempKeys;
			valList = tempVals;
			pairIterator = root;
		}
		//If there is only 1 node, just hash that value to a temporary array and
		//make that array the new one for the object
		else if(N == 1)
		{
			Node<V> tempNode = new Node<V>(Integer.toString(0), root.value);
			//Hash the value into a new array
			int hashNum = hash(tempNode.key);
			Node<V>[] temp = (Node<V>[]) new Node<?>[N];
			temp[hashNum] = tempNode;
			nodeArray = temp;
			root = tempNode;
			tail = tempNode;
			pairIterator = root;
		}
		//Nothing to sort, return an error
		else
		{
			System.out.println("There is nothing to sort!");
		}
	}
	//Similar to the above method but keys are not replaced with integers
	public void asort()
	{
		//Normal operation, if the size of the array is greater than 1
		if(N > 1)
		{
			Node<V>[] sorter = (Node<V>[]) new Node<?>[N];
			Node<V> sorterNode = root;
			//Copy everything to a temporary array for sorting
			for(int b = 0; b < N; b++)
			{
				sorter[b] = sorterNode;
				sorterNode = sorterNode.next;
			}
			//Using a variation of quicksort and compareTo
			quickSort(sorter, 0, sorter.length-1);
			Node<V>[] temp = (Node<V>[]) new Node<?>[M];
			Node<V> tempRoot;
			Node<V> tempTail;
			ArrayList<String> tempKeys = new ArrayList<String>();
			ArrayList<V> tempVals = new ArrayList<V>();
			
			//Create new nodes
			for(int i = 0; i < N; i++)
			{
				//Redo to ensure all of the pointers are null
				sorter[i] = new Node<V>(sorter[i].key, sorter[i].value);
				//Redo the key and val array lists
				tempKeys.add(sorter[i].key);
				tempVals.add(sorter[i].value);
			}
			//Set the connects of the linked list appropriately, similar to the put method
			tempRoot = sorter[0];
			tempTail = sorter[N - 1];
			for(int j = 0; j < N; j++)
			{
				//Only set next for the root
				if(sorter[j] == tempRoot)
				{
					sorter[j].next = sorter[j+1];
				}
				//Only set prev for the tail
				else if(sorter[j] == tempTail)
				{
					sorter[j].prev = sorter[j-1];
				}
				//Set next and prev otherwise
				else
				{
					sorter[j].next = sorter[j+1];
					sorter[j].prev = sorter[j-1];
				}
			}
			
			//Hash the sorted values into the temporary array
			//Notice there are no checks for duplicates since there cannot be any
			//in this implementation
			for(int k = 0; k < sorter.length; k++)
			{
				int hashNum = hash(sorter[k].key);
				if(temp[hashNum] == null)
				{
					temp[hashNum] = sorter[k];
				}
				else
				{
					int index = hashNum;
					while(temp[index] != null)
					{
						index = (index + 1) % M;
					}
					temp[index] = sorter[k];
				}
			}
			//Reset all of the class data from the temporary values
			nodeArray = temp;
			root = tempRoot;
			tail = tempTail;
			keyList = tempKeys;
			valList = tempVals;
			pairIterator = root;
		}
		//If there is only 1 node, just hash that value to a temporary array and
		//make that array the new one for the object
		else if(N == 1)
		{
			Node<V> tempNode = new Node<V>(Integer.toString(0), root.value);
			//Hash the value into a new array
			int hashNum = hash(tempNode.key);
			Node<V>[] temp = (Node<V>[]) new Node<?>[N];
			temp[hashNum] = tempNode;
			nodeArray = temp;
			root = tempNode;
			tail = tempNode;
			pairIterator = root;
		}
		//Nothing to sort, return an error
		else
		{
			System.out.println("There is nothing to sort!");
		}
	}
	//Return an iterator for the linked list
	public Iterator<V> iterator()
	{
		return new NodeIterator();
	}
	//Private class for iterating for the nodes
	private class NodeIterator implements Iterator<V>
	{
		private Node<V> nextNode;
		private String currKey;
		private NodeIterator()
		{
			nextNode = root;
			if(root != null)
				currKey = root.key;
		}
		public boolean hasNext()
		{
			return nextNode!= null;
		}
		public V next()
		{
			if(hasNext())
			{
				Node<V> returnNode = nextNode;
				currKey = returnNode.key;
				nextNode = nextNode.next;
				return returnNode.value;
			}
			else
				return null;
		}
		public void remove()
		{
			throw new UnsupportedOperationException("remove() is not supported");
		}
	}
	//Return another Pair from the linked list
	public Pair<V> each()
	{
		//Create a pair from the current node in the array
		//Return said pair
		if(pairIterator == null)
		{
			return null;
		}
		else
		{
			Node<V> returnNode = pairIterator;
			pairIterator = pairIterator.next;
			return new Pair<V>(returnNode.key, returnNode.value);
		}
	}
	//Reset the iteration of the pairs
	public void reset()
	{
		//Reinitialize the iterator from above
		pairIterator = root;
	}
	//Return the hashed index from the key
	private int hash(String key)
	{
		int hashNum = 0;
		for(int i = 0; i < key.length(); i++)
		{
			hashNum = (key.charAt(i) + hashNum * ALPHA_SIZE) % M;
		}
		hashNum = Math.abs(hashNum);
		return hashNum;
	}
	//Resize the array if need be
	private void resize(int newSize)
	{
		int oldM = M;
		M = newSize;
		temp = (Node<V>[]) new Node<?>[newSize];
		//Rehash everything in the old array into the new array
		//Move said nodes into the new array
		//Do not create new nodes or modify the linked list
		for(int i = 0; i < oldM; i++)
		{
			//Rehash the table
			//Note: only moving the nodes to the new list, not modifying them
			//or redoing the linked list
			if(nodeArray[i]!= null)
			{
				//Hash the key
				int hashNum = hash(nodeArray[i].key);
				//If that location is null, add it there
				if(temp[hashNum] == null)
				{
					temp[hashNum] = nodeArray[i];
				}
				//Else, increment by one until one is found
				else
				{
					int index = hashNum;
					while(temp[index] != null)
					{
						index = (index + 1) % newSize;
					}
					//Set that location as the node
					temp[index] = nodeArray[i];	
				}
			}
		}
		nodeArray= temp;
		temp = null;
	}
	//Show table contents of the array
	public void showTable()
	{
		for(int i = 0; i < M; i++)
		{
			//If null, print null
			if(nodeArray[i] == null)
			{
				System.out.println(i + ": null");
			}
			//Else, print the value and key of the node
			else
			{
				System.out.println(i + ": Key: " + nodeArray[i].key + " Value: " + nodeArray[i].value);
			}
		}
	}
	//Return the length of the table
	public int length()
	{
		return N;
	}
	
	//Transpose the keys and values into a new array
	public PHPArray<String> array_flip()
	{
		PHPArray<String> newArray = new PHPArray<String>(M);
		Node<V> node = root;
		//Loop through the nodes and add them to the flipped array
		while(node != null)
		{
			//Cast V to String. Throws an exception if not compatible
			newArray.put((String)node.value, node.key);
			node = node.next;
		}
		return newArray;
	}
	//Version of quickSort meant to sort the node array by value
	private void quickSort(Node<V>[] array, int low, int high)
	{
		if(low >= high)
			return;
		
		int i = low;
		int j = high;
		int pivot = (high);
		while(i <= j)
		{
			while(((Comparable)array[i].value).compareTo((Comparable)array[pivot].value) < 0)
			{
				i++;
			}
			while(((Comparable)array[j].value).compareTo((Comparable)array[pivot].value) > 0)
			{
				j--;
			}
			if(i <= j)
			{
				Node<V> temp = array[i];
				array[i] = array[j];
				array[j] = temp;
				i++;
				j--;
			}
		}
		
		if(low < j)
			quickSort(array, low, j);
		if(i < high)
			quickSort(array, i, high);
	}
}