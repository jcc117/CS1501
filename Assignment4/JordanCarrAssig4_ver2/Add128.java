//Jordan Carr
//Add128 class file
//Created 7/2/17
//Modified 7/12/17

import java.util.*;

public class Add128 implements SymCipher
{
	//Key for this cipher and random number generator to generator random keys
	private byte [] key;
	private Random R = new Random();
	
	//Create a random key using random bytes
	public Add128()
	{
		key = new byte[128];
		R.nextBytes(key);
	}
	
	//Use a predetermined key
	public Add128(byte [] newKey)
	{
		key = newKey;
	}
	
	//Return the key
	public byte[] getKey()
	{
		return key;
	}
	
	//Add the byte value key to the string, converted to an array of bytes
	public byte [] encode(String s)
	{
		//Print the orignal string
		System.out.println("Original String: " + s);
		//Convert the string to an array of bytes
		byte[] input = s.getBytes();
		
		//Print the bytes of the string
		System.out.print("String Bytes: ");
		for(int i = 0; i < input.length; i++)
			System.out.print(input[i] + " ");
		System.out.println("");
		//Encrypt the message by adding the key value to the string's byte values
		byte[] output = new byte[input.length];
		for(int i = 0; i < input.length; i++)
		{
			//The value is modded so it will wrap around itself if the string is longer than 128
			output[i] = (byte)(input[i] + key[i % 128]);
		}
		//Print the encrypted bytes
		System.out.print("Encoded bytes: ");
		for(int i = 0; i < output.length; i++)
			System.out.print(output[i] + " ");
		System.out.println("");
		return output;
	}
	
	//Subtract the byte value key from the string and convert to a String
	public String decode(byte [] bytes)
	{
		//Print the encoded bytes
		System.out.print("Encoded Bytes: ");
		for(int i = 0; i < bytes.length; i++)
			System.out.print(bytes[i] + " ");
		System.out.println("");
		//Decode the message by subtracting the key value from the array's value
		byte[] output = new byte[bytes.length];
		for(int i = 0; i < bytes.length; i++)
		{
			output[i] = (byte)(bytes[i] - key[i%128]);
		}
		//Print the decoded bytes
		System.out.print("Decoded Bytes: ");
		for(int i = 0; i < output.length; i++)
			System.out.print(output[i] + " ");
		System.out.println("");
		//Print the string and return it
		System.out.println("Decoded String: " + new String(output));
		return new String(output);
	}
}