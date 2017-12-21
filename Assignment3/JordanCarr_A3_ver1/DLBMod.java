//Jordan Carr
//cs1501
//Started - 5/25/17
//Modified - 6/21/17

import java.util.*;
import java.lang.*;

public class DLBMod implements DictInterfaceMod
{
	private Node list;
	
	public DLBMod()
	{
		list = new Node('A');
		list.code = new Integer(65);
	}
	
	//Node class
	//Holds references to child nodes,
	//sibling nodes, and the value of the node
	private class Node
	{
		private Node child;
		private Node sibling;
		private char value;
		private Integer code;
		
		public Node(char val)
		{
			value = val;
			child = null;
			sibling = null;
			code = null;
		}
	}
	
	//Add a word to the DLB
	public boolean add(String s, Integer dictCode)
	{
		Node currentNode = list;
		//Cycle through the letters of the string
		boolean newChain = false;
		boolean newChild = false;
		for(int i = 0; i < s.length(); i++)
		{
			//Create a new node for the current letter
			char letter = s.charAt(i);

			//If this is not a new chain of letters to be added,
			//Cycle through the letters of the current line of nodes
			if(!newChain)
			{
				while(currentNode != null)
				{
					//If the letter being searched for is found,
					//make its child the current node
					if(currentNode.value == letter)
					{
						//Make sure the child is not null
						if(currentNode.child != null)
						{
							currentNode = currentNode.child;
							break;
						}
						else
						{
							newChild = true;
							break;
						}
					}
					//Else, if it's sibling isn't null make that the current node
					//and keep searching
					else if(currentNode.sibling != null)
					{
						currentNode = currentNode.sibling;
					}
					//If the sibling is null, a new chain of letters must be formed
					else
					{
						newChain = true;
						break;
					}
				}
				//Set up the new chain of letters by making the current node's sibling
				//the letter being sought and then making that the next current node
				if(newChain)
				{
					currentNode.sibling = new Node(letter);
					currentNode = currentNode.sibling;
				}
				//This is the problem!!!!!!!!!!
				else if(newChild)
				{
					newChain = true;
					//currentNode.child = new Node(letter);
					//currentNode = currentNode.child;
					//currentNode.code = 0;
				}
			}
			//Add a new chain of letters by making the current node's child
			//the letter and then making that the next current node
			else
			{
				currentNode.child = new Node(letter);
				currentNode = currentNode.child;
				//currentNode.code = 0;
			}
		}
		
		//Add a code to the end of a string
		//to signify the end of a word.
		//System.out.println("Add " + dictCode);
		if(currentNode.code == null)
		{
			currentNode.code = dictCode;
			return true;
		}
		//Already in the list
		else
		{
			return false;
		}
		//If all of the above fail for whatever reason, return false.
		//This is mainly to get the program to compile but it should not
		//reach this point
		//return false;
	}
	
	//Search the DLB for a given StringBuilder
	//Modification: returns the stored code of the word if it is there
	//Returns null if it is not in the DLB
	public Integer searchPrefix(StringBuilder s)
	{
		Node currentNode = list;
		for(int i = 0; i < s.length(); i++)
		{
			//boolean letterFound = false;
			//Loop through the list til the letter
			//is found or null is reached
			while(currentNode != null)
			{
				//If the value is found, go to the next
				//level of the list and search for the
				//next letter
				if(currentNode.value == s.charAt(i))
				{
					//letterFound = true;
					//If the child is null, the prefix is not found
					//so return 0
					if(currentNode.child == null && i != s.length() - 1)
					{
						return null;
					}
					else
					{
						//If the end of the string has been reached, return the code(could be null)
						if(i == s.length() -1)
						{
							//System.out.println("Output:" + currentNode.code);
							return currentNode.code;
						}
						//Else go to the next node
						else
						{
							currentNode = currentNode.child;
							break;
						}
					}
				}
				//If not found, go to the next letter
				//in the current list until null is reached
				//or the letter is found
				else
				{
					if(currentNode.sibling != null)
					{
						currentNode = currentNode.sibling;
					}
					//If the sibling is null, the prefix is not
					//found so return null
					else
					{
						return null;
					}
				}
			}
		}
		//Should not have to get to this point
		return null;
	}
	
	//This method will not be utilized, but will be kept in case it will be in the future
	
	//Search the DLB for a given StringBuilder between two specific locations
	/*public int searchPrefix(StringBuilder s, int start, int end)
	{
		//Flags for finding a word and finding a prefix
		boolean word = false;
		boolean prefix = false;
		Node currentNode = list;
		//Note that start and end provide the range of indexes
		//for which this loop will search through
		for(int i = start; i <= end; i++)
		{
			boolean letterFound = false;
			//Loop through the list til the letter
			//is found or null is reached
			while(currentNode != null)
			{
				//If the value is found, go to the next
				//level of the list and search for the
				//next letter
				if(currentNode.value == s.charAt(i))
				{
					letterFound = true;
					//If the child is null, the prefix is not found
					//so return 0
					if(currentNode.child == null)
					{
						return 0;
					}
					else
					{
						currentNode = currentNode.child;
						break;
					}
				}
				//If not found, go to the next letter
				//in the current list until null is reached
				//or the letter is found
				else
				{
					if(currentNode.sibling != null)
					{
						currentNode = currentNode.sibling;
					}
					//If the sibling is null, the prefix is not
					//found so return 0
					else
					{
						return 0;
					}
				}
			}
			//If it has gotten to the end of s, search the next line of search
			//for a * or another letter. 
			//This will determine whether s is a word, prefix, or both
			if(letterFound && i == end)
			{
				//Search for another letter and/or *
				//If the former is found, it is a prefix
				//If the later is found, it is a word
				//Note, it can be both.
				//Also, the first value of currentNode
				//should never be null based on the way words
				//and prefixes were constructed in the add method,
				//meaning it should either be another letter or a *
				while(currentNode != null)
				{
					if(currentNode.value != '*')
						prefix = true;
					else
						word = true;
					currentNode = currentNode.sibling;
				}
			}
		}
		
		//Return the appropriate values based on the above findings
		if(prefix && word)
		{
			return 3;
		}
		else if(prefix)
		{
			return 1;
		}
		else
		{
			return 2;
		}
	}*/
}