//Jordan Carr
//Substitute encryption file
//Created 7/2/17
//Modified 7/12/17

import java.util.*;


public class Substitute implements SymCipher
{
	private byte [] key;
	private Random R;
	
	//Generate random key
	public Substitute()
	{
		byte[] temp = new byte[256];
		//Fill the array with all 256 char values
		for(int i = 0; i < temp.length; i++)
		{
			temp[i] = (byte) i;
		}
		//Convert the temporary array to a byte array;
		//key = new String(temp).getBytes();
		key = temp;
		//Randomly permute the array
		R = new Random();
		int num;
		for(int i = 0; i < key.length; i++)
		{
			//Generate index
			num = R.nextInt(255) + 0;
			
			//Swap
			byte tempo = key[i];
			key[i] = key[num];
			key[num] = tempo;
		}
	}
	
	//Use a provided key
	public Substitute(byte [] bytes)
	{
		key = bytes;
	}
	
	//Return the key
	public byte[] getKey()
	{
		return key;
	}
	
	//BETA version
	public byte [] encode(String s)
	{
		byte[] bytes = new byte[s.length()];
		int index;
		System.out.println("Original String: " + s);
		byte[] sBytes = s.getBytes();
		//Swap the indexed key values with those in the string
		System.out.print("Orignal Bytes: ");
		for(int i = 0; i < sBytes.length; i++)
			System.out.print(sBytes[i] + " ");
		System.out.println("");
		for(int i = 0; i < s.length(); i++)
		{
			index = (int)s.charAt(i);
			bytes[i] = (byte)(key[index] & 0xff);
		}
		//Print the encoded bytes
		System.out.print("Encoded Bytes: ");
		for(int i = 0; i < bytes.length; i++)
			System.out.print(bytes[i] + " ");
		System.out.println("");
		return bytes;
	}
	
	public String decode(byte [] bytes)
	{
		//Print the encoded bytes
		System.out.print("Encoded Bytes: ");
		for(int i = 0; i < bytes.length; i++)
			System.out.print(bytes[i] + " ");
		System.out.println("");
		
		byte[] input = new byte[bytes.length];
		//Create an array of char bytes in the original order
		char[] chars = new char[256];
		for(int i = 0; i < chars.length; i++)
		{
			chars[i] = (char) i;
		}
		byte[] orig = new String(chars).getBytes();
		//Create the inverse key
		byte[] iKey = new byte[256];
		int index;
		for(int i = 0; i < chars.length; i++)
		{
			index = (int) (key[i] & 0xff);
			iKey[index] = orig[i];
		}
		//Decode the message by swapping the key values to the original ones using the inverse key
		for(int i = 0; i < bytes.length; i++)
		{
			index = (int)(bytes[i] & 0xff);
			input[i] = iKey[index];
		}
		//Print the decoded bytes
		System.out.print("Decoded Bytes: ");
		for(int i = 0; i < input.length; i++)
			System.out.print(input[i] + " ");
		System.out.println("");
		
		//Make all of the bytes into unsigned chars to avoid negative numbers
		char[] output = new char[input.length];
		for(int i = 0; i < input.length; i++)
		{
			output[i] = (char) (input[i] & 0xff);
		}
		
		//Print the string and return it
		System.out.println("Decoded String: " + new String(input));
		return new String(input);
	}
}