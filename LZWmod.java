/**
 * This program was a modification of the LZW.java program. This LZWmod.java
 * was created for Assignment 3 of Sherif Khattab's CS1501 Fall 2018.
 * Usage : java LZWmod < (inputfile) > (outputfile)
 * @author Daniel Weinschenk
 */
import java.util.Arrays;

public class LZWmod 
{
    private static final int R = 256;  // number of input chars
    private static int L = 512;       // number of codewords = 2^W
    private static int W = 9;         // codeword width
    private static boolean reset = false; // reset or not
    /**
     * Compresses the input file using a variable length codeword. When all the available codewords are used
     * we add one bit length to the codeword up to 16 bits. When the length uses all 2^16 codewords, then 
     * we reset the codebook to enable new codewords being used. 
     */
    public static void compress()
    { 
        char input =  BinaryStdIn.readChar(); // reads char (1 byte)
        TSTmod<Integer> st = new TSTmod<Integer>();
        for (int i = 0; i < R; i++) // initialize codebook to first 255 chars
        {
        		StringBuilder sb = new StringBuilder("" + (char) i); 
            st.put(sb, i);
        }
        int code = R+1; // code to 257 (we are saving 256 for terminating char)
        if(reset) // if reset, then start with 1 bit, if not 0 bit
        		BinaryStdOut.write(1,1);
        else
        		BinaryStdOut.write(0,1);
        StringBuilder sb = new StringBuilder(Character.toString(input)); 
        while (!BinaryStdIn.isEmpty()) 
        {	
    			input = BinaryStdIn.readChar();
        		while(st.get(sb.append(input)) != null && !BinaryStdIn.isEmpty()) // add to sb until not found
        		{
        			input = BinaryStdIn.readChar();
        		}
        		if(code < L) // sb now found phrase plus one letter
        		{
        			sb = sb.deleteCharAt(sb.length()-1); // delete the letter
        			BinaryStdOut.write(st.get(sb), W); // write the code
        			sb = sb.append(input); // append the letter
                st.put(sb, code); // add the new pattern
                code++;
                if(code == L) // if we added the 2^Wth word
                {
                   modifyCodeLength(W+1); 
                   if(reset && W == 9) // if reset and we were and W+1 = 17
                   {
                	   		st.empty(); // reset tree
                	   		for (int i = 0; i < R; i++) // re initialize 255 chars
                	        {
                	        		StringBuilder sb2 = new StringBuilder("" + (char) i); 
                	            st.put(sb2, i);
                	        }
                	   		code = R+1;
                   }
                }
                sb = new StringBuilder(Character.toString(input));
        		}
        		if(BinaryStdIn.isEmpty() && sb.length() == 1) // if last letter, add it
        		{
        			BinaryStdOut.write(st.get(sb), W);
        		}
        }
        BinaryStdOut.write(R, W); // place terminating marker (256) at end of codebook
        BinaryStdOut.close();
    } 
    /**
     * Expands the compressed file 
     */
	public static void expand() {
		if(BinaryStdIn.readInt(1) == 1) // read first bit to judge reset or not
			reset = true; 
		else
			reset = false; 
        String[] st = new String[L];
        int i;
        for (i = 0; i < R; i++) // initialize 255 chras
            st[i] = "" + (char) i;
        st[i++] = "";                      
        	int codeword = BinaryStdIn.readInt(W);
        String val = st[codeword];
        while (true) {
            BinaryStdOut.write(val);
            if(i == L-1) // if i is one less than L (since we didn't enter for the first codeword)
            {
                modifyCodeLength(W+1);
                if(reset && W == 9)// if reset and we were and W+1 = 17
                {
                		for(int j = 0; j < st.length;j++) // reset st array
                			st[j] = null;
                		 for(int j  = 0; j < R; j++)  // reinitialize 255 chars
                	            st[j] = "" + (char) j;
                		 i = R; 
                }
                st = Arrays.copyOf(st, L); // copy array to new size
            }
            codeword = BinaryStdIn.readInt(W);
            if (codeword == R) break;
            String s = st[codeword];
            if (i == codeword) s = val + val.charAt(0); 
            if (i < L) 
            {
            		st[i++] = val + s.charAt(0);
            }
            val = s;
        }
        BinaryStdOut.close();
    }
	
	/**
	 * Resizes the codeword length W, thus changing the total amount of codewords possible L ( 2^W)
	 * If the resize is 17 and reset was prompted in compression, then we reset to 9 length codeword.
	 * If not, we don't resize and don't add any new codewords to the symbol table. 
	 * @param newLength the newlength of the codeword
	 */

	private static void modifyCodeLength(int newLength) 
    {
    		if(newLength == 17)
    		{
    			if(reset)// if reset was prompted
    			{
    				W = 9; //reset length to original 9 bit length
    				L = 512;
    				return; 
    			}
    			else
    				return;
    		}
		W = newLength; 
		L = (int) Math.pow(2,W); // 2^W
	}



    public static void main(String[] args) 
    { 
    		if(args[0].equals("-")) 
    		{
    			if(args[1].equals("r"))
        			reset = true;
    			compress();
    		}        		
        else if(args[0].equals("+")) 
        		expand();
        else 
        		throw new RuntimeException("Illegal command line argument");
    }

}
