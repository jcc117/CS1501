/*************************************************************************
 *  Compilation:  javac LZW.java
 *  Execution:    java LZW - < input.txt   (compress)
 *  Execution:    java LZW + < input.txt   (expand)
 *  Dependencies: BinaryIn.java BinaryOut.java
 *
 *  Compress or expand binary input from standard input using LZW.
 *
 *
 *************************************************************************/

//Jordan Carr

public class LZWmod {
	private static int W = 9;         		// codeword width
    private static final int R = 256;        // number of input chars
    private static int L = (int)Math.pow(2, W);       // number of codewords = 2^W

    public static void compress(){ 
		DLBMod dlb = new DLBMod();
        for (int i = 0; i < R; i++)
            dlb.add(new String("" + (char) i), new Integer(i));
        int code = R + 1;  // R is codeword for EOF
		
		//Build patterns to compress the file
		char input = BinaryStdIn.readChar();
		Integer patCode = null;
		StringBuilder pat = new StringBuilder();
		Integer prevCode = patCode;
		pat.append(input);
		while(!BinaryStdIn.isEmpty())
		{
			patCode = dlb.searchPrefix(pat);		//Search for the pattern
			if(patCode == null)						//Pattern is not in the dictionary
			{
				if(code == L && W < 16)				//Check to increment codeword resize
				{
					W++;							//Increment number of bits
					L = (int)Math.pow(2, W);		//Increment number of codewords
				}
				if(code < Math.pow(2, 16))
					dlb.add(pat.toString(), new Integer(code++));				//Add the new pattern to the dictionary if it's 16 bits or under and the dicitonary is not full
				BinaryStdOut.write(prevCode, W);	//Write the previous code to the file
				pat = new StringBuilder();			//Reset the StringBuilder
				pat.append(input);
			}
			else									//Pattern is in the dictionary
			{
				prevCode = patCode;					//Save the current encoding
				if(!BinaryStdIn.isEmpty())
				{
					input = BinaryStdIn.readChar();		//Read another char from the file
					pat.append(input);
				}
			}
		}
		
		//Account if there is another codeword to add and codewords that are yet to be written to the file after the end of the input file has been reached
		if(pat.length() > 0)
		{
			if(dlb.searchPrefix(pat) != null)
			{
				BinaryStdOut.write(dlb.searchPrefix(pat), W);	//The pattern is already in the dictionary
			}
			else
			{
				if(code == L && W < 16)				//Check to increment codeword resize
				{
					W++;							//Increment number of bits
					L = (int)Math.pow(2, W);					//Increment number of codewords
				}
				if(code < Math.pow(2, 16))
					dlb.add(pat.toString(), new Integer(code++));				//Add the new pattern to the dictionary if it's 16 bits or under and the dicitonary is not full
				BinaryStdOut.write(prevCode, W);	//Write the previous code to the file
				pat = new StringBuilder();			//Reset the StringBuilder
				pat.append(input);
				BinaryStdOut.write(dlb.searchPrefix(pat), W);
			}
		}
		BinaryStdIn.close();
        BinaryStdOut.write(R, W);				//Append the end of line character
        BinaryStdOut.close();
    } 


    public static void expand() {
        String[] st = new String[(int)Math.pow(2, 16)];
        int i; // next available codeword value

        // initialize symbol table with all 1-character strings
        for (i = 0; i < R; i++)
            st[i] = "" + (char) i;
        st[i++] = "";                        // (unused) lookahead for EOF

        int codeword = BinaryStdIn.readInt(W);
        String val = st[codeword];
		

        while (true) {
			if ((i+1) == L && W < 16) 
			{
				W++;						//If the number of available codewords runs out, increase the number of bits
				L = (int)Math.pow(2, W);	//Increase the maximum number of codewords
			}
            BinaryStdOut.write(val);
            codeword = BinaryStdIn.readInt(W);
            if (codeword == R) break;
            String s = st[codeword];
            if (i == codeword) s = val + val.charAt(0);   // special case hack
			if(i < Math.pow(2, 16))
				st[i++] = val + s.charAt(0);
            val = s;
        }
        BinaryStdOut.close();
    }



    public static void main(String[] args){
        if      (args[0].equals("-")) compress();
        else if (args[0].equals("+")) expand();
        else throw new RuntimeException("Illegal command line argument");
    }

}