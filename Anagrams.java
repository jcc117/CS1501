//Jordan Carr
//cs1501
//Started - 5/18/17
//Modified - 5/31/17

import java.util.*;
import java.lang.*;
import java.io.*;

public class Anagrams
{
	public static StringBuilder build;
	public static boolean available[];
	public static ArrayList<ArrayList<String>> anagrams;
	public static DictInterface D;
	public static ArrayList<String> originalStrings;
	public static int numSpaces;
	
	public static void main(String args[]) throws IOException
	{
		//Open the dictionary.txt file and create a DictInterface object
		//Catch any possible user input errors
		Scanner fileScan = new Scanner(new FileInputStream("dictionary.txt"));
		String st;
		try
		{
			if(args[2].equals("dlb"))
			{
				D = new DLB();
			}
			else if(args[2].equals("orig"))
			{
				D = new MyDictionary();
			}
			else
			{
				System.out.println("Error: invalid DictInterface argument");
				System.exit(0);
			}
		}catch(IndexOutOfBoundsException e)
		{
			System.out.println("No DictInterface argument given");
			System.exit(0);
		}
		
		//Store everything from dictionary.txt in a DictInterface object D
		while(fileScan.hasNext())
		{
			st = fileScan.nextLine();
			D.add(st);
		}
		fileScan.close();
		
		//Open the input file
		Scanner fileScan2 = new Scanner(new FileInputStream(args[0]));
		ArrayList<StringBuilder> strings = new ArrayList<StringBuilder>();
		originalStrings = new ArrayList<String>();
		
		//Put the strings from the input file
		//into an ArrayList of StringBuilders
		//Also copy those strings into a second ArrayList
		//originalStrings so they can be printed to the output.
		//Those in strings will be modified later to make
		//finding its anagrams easier.
		int x = 0;
		while(fileScan2.hasNext())
		{
			strings.add(new StringBuilder(fileScan2.nextLine()));
			originalStrings.add(strings.get(x).toString());
			x++;
		}
		
		//Modify the StringBuilders for finding the anagrams
		//Sort the strings in alphabetical order with alphaSort,
		//an alphabetical version of quick sort. Also, delete any
		//spaces within the string to make finding one word anagrams
		//more efficient/possible.
		for(int p = 0; p < (strings.size()); p++)
		{
			int high = strings.get(p).length() - 1;
			alphaSort(strings.get(p), 0, high);
			delSpaces(strings.get(p));
		}
		fileScan2.close();
		
		//Cycle through all of the strings in the input file
		//and find their anagrams
		PrintWriter writeFile = new PrintWriter(args[1]);
		int j;
		for(j = 0; j < (strings.size()); j++)
		{
			anagrams = new ArrayList<ArrayList<String>>();
			anagrams.add(new ArrayList<String>());
			findAnagrams((StringBuilder)strings.get(j));
			writeToFile(writeFile, anagrams, originalStrings.get(j));
		}
		
		writeFile.close();
	}
	
	//Write all of the given anagrams for a string to writeFile
	public static void writeToFile(PrintWriter writeFile, ArrayList<ArrayList<String>> anagrams, String string) throws IOException
	{
		writeFile.println("Here are the results for " + string);
		
		//Loop through all bins of the anagrams to format the output by the number of words
		//that makes up the anagram
		for(int i = 0; i < anagrams.size(); i++)
		{
			//If there is nothing in the bin, ignore it
			if(anagrams.get(i).size() != 0)
			{
				writeFile.println((i + 1) + " word solutions");
				for(int j = 0; j < anagrams.get(i).size(); j++)
				{
					writeFile.println(anagrams.get(i).get(j));
				}
			}
		}
	}
	
	//Recursive method to find all of the anagrams for a particular string
	//Returns an ArrayList of said anagrams
	//Returns null if there are no anagrams for a string
	public static void findAnagrams(StringBuilder string)
	{
		//Find all of the single word anagrams
		build = new StringBuilder();
		available = new boolean[string.length()];
		int i;
		for(i = 0; i < available.length; i++)
		{
			available[i] = true;
		}
		//Pos is the position of the char within string
		//It will recursively call through each pos and try each available
		//for each pos
		int pos = 0;
		numSpaces = 0;
		//Find all of the anagrams
		findMwAnagrams(string, pos, 0);
		//Commented out is the optional findSwAnagrams method
		//Only finds single word anagrams, which findMwAnagrams does
		//on its own
		//findSwAnagrams(string, pos);	
	}
	
	//Finds all the single word anagrams for an alphabetically sorted StringBuilder
	private static void findSwAnagrams(StringBuilder string, int pos)
	{
		//Append a letter to the build
		int i;
		for(i = 0; i < (string.length()); i++)
		{
			if(available[i])
			{
				build.append(string.charAt(i));
				available[i] = false;
				
				int j = D.searchPrefix(build);
				//Debug code
				//System.out.println(build.toString());
				switch (j)
				{
					//No good
					case 0:
						break;
					//Prefix, keep adding letters
					case 1:
						findSwAnagrams(string, pos + 1);
						break;
					//A word, add it to the list
					case 2:
						if(pos == (string.length() - 1) && !anagrams.get(0).contains(build.toString()))
						{
							//anagrams.add(build.toString());
							anagrams.get(0).add(build.toString());
						}
						else
							findSwAnagrams(string, pos + 1);
						break;
					//A word and prefix, keep adding letters
					case 3:
						if(pos == (string.length() - 1) && !anagrams.get(0).contains(build.toString()))
						{
							//anagrams.add(build.toString());
							anagrams.get(0).add(build.toString());
						}
						else
							findSwAnagrams(string, pos + 1);
						
				}
				//Remove the letter and try the next one
				build.deleteCharAt(pos);
				available[i] = true;
			}
		}
	}

	//Finds all multi-word anagrams
	//Now modified to find single word anagrams as well
	private static void findMwAnagrams(StringBuilder string, int pos, int start)
	{
		//Append a letter to the build
		int i;
		for(i = 0; i < (string.length()); i++)
		{
			if(available[i])
			{
				build.append(string.charAt(i));
				available[i] = false;
				
				int j = D.searchPrefix(build, start, build.length() - 1);
				//System.out.println(build.toString());
				switch (j)
				{
					//No good
					case 0:
						break;
					//Prefix, keep adding letters
					case 1:
						findMwAnagrams(string, pos + 1, start);
						break;
					//A word
					//If not all the letters were used, add a space
					//and keep adding letters
					case 2:
						//If all letters not used, add space and try to build another word
						if(!allUsed())
						{
							//Update pos as pos+2
							numSpaces++;
							build.append(' ');
							//Make sure the correct bin exists to add the anagram to
							while((anagrams.size() -1) < numSpaces)
							{
								anagrams.add(new ArrayList<String>());
							}
							findMwAnagrams(string, pos + 2, pos + 2);
							//Remove the space
							build.deleteCharAt(pos + 1);
							numSpaces--;
						}
						//If all letters used and numSpaces != 0 and not already in the list, add to the list
						else if(!anagrams.get(numSpaces).contains(build.toString()) && allUsed())
						{
							anagrams.get(numSpaces).add(build.toString());
						}
						break;
					//A word and prefix
					case 3:
						if(!allUsed())
						{
							//Update numSpaces and add a space to the StringBuilder
							numSpaces++;
							build.append(' ');
							//Make sure the correct bin exists to add the anagram to
							while((anagrams.size() - 1) < numSpaces)
							{
								anagrams.add(new ArrayList<String>());
							}
							findMwAnagrams(string, pos + 2, pos + 2);
							//Delete the space and try it as if you were just continuing to add chars to the word
							build.deleteCharAt(pos + 1);
							numSpaces--;
							findMwAnagrams(string, pos + 1, start);
						}
						//If all letters used and not already in the list, add to list
						else if(!anagrams.get(numSpaces).contains(build.toString()) && allUsed())
						{
							anagrams.get(numSpaces).add(build.toString());
						}					
				}
				//Remove the letter and try the next one
				build.deleteCharAt(pos);
				available[i] = true;
			}
		}
	}

	//Sort the characters of a StringBuilder in alphabetical order
	//using quick sort
	private static void alphaSort(StringBuilder string, int low, int high)
	{
		//Quick sort the string
		int i = low;
		int j = high;
		int pivot = string.charAt(low + (high - low)/2);
		while(i <= j)
		{
			while(string.charAt(i) < pivot)
			{
				i++;
			}
			while(string.charAt(j) > pivot)
			{
				j--;
			}
			if(i <= j)
			{
				char temp = string.charAt(i);
				string.setCharAt(i, string.charAt(j));
				string.setCharAt(j, temp);
				i++;
				j--;
			}
		}
		
		if(low < j)
			alphaSort(string, low, j);
		if(i < high)
			alphaSort(string, i, high);
	}
	
	private static void delSpaces(StringBuilder string)
	{
		//Get rid of any space chars in order to make constructing
		//single words detectable. Spaces will be inserted between
		//words as needed when making multi-word anagrams
		for(int k = string.length() - 1; k >= 0; k--)
		{
			if(string.charAt(k) == ' ')
			{
				string.deleteCharAt(k);
			}
		}
	}
	//Check whether all letters in the argument string have been used or not
	private static boolean allUsed()
	{
		for(int i = 0; i < available.length; i++)
		{
			if(available[i] == true)
			{
				return false;
			}
		}
		
		return true;
	}
}