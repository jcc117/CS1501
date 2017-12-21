//Jordan Carr
//Modified version of ImprovedChatClient for use as a secure chat client
//Modified 7/3/17


import java.util.*;
import java.io.*;
import java.net.*;
import javax.swing.*;
import java.awt.event.*;
import java.awt.*;
import java.math.*;

public class SecureChatClient extends JFrame implements Runnable, ActionListener {

    public static final int PORT = 8765;

    BufferedReader myReader;
    PrintWriter myWriter;
    JTextArea outputArea;
    JLabel prompt;
    JTextField inputField;
    String myName, serverName;
	Socket connection;
	ObjectOutputStream output;
	ObjectInputStream input;
	BigInteger E, N, D;
	SymCipher cipher;

    public SecureChatClient () throws IOException
    {
        try {

        myName = JOptionPane.showInputDialog(this, "Enter your user name: ");
        serverName = JOptionPane.showInputDialog(this, "Enter the server name: ");
        InetAddress addr =
                InetAddress.getByName(serverName);
        connection = new Socket(addr, PORT);   // Connect to server with new
                                               // Socket
		output = new ObjectOutputStream(connection.getOutputStream());
		output.flush();
		input = new ObjectInputStream(connection.getInputStream());
        
		/*myReader =
             new BufferedReader(
                 new InputStreamReader(
                     connection.getInputStream()));   // Get Reader and Writer

        myWriter =
             new PrintWriter(
                 new BufferedWriter(
                     new OutputStreamWriter(connection.getOutputStream())), true);*/

       //myWriter.println(myName);   // Send name to Server.  Server will need
                                    // this to announce sign-on and sign-off
                                    // of clients
		
		
		E = (BigInteger)input.readObject();		//Get E and N
		System.out.println("E: " + E);
		N = (BigInteger)input.readObject();
		System.out.println("N: " + N);
		String type = (String)input.readObject(); 	//Get the cipher type
		System.out.println("Cipher: " + type);
		if(type.equals("Add"))				//Create the cipher object
		{
			cipher = new Add128();
		}
		else
		{
			cipher = new Substitute();
		}
		
		byte[] key = cipher.getKey();				//Get the key of the cipher
		System.out.print("Key: ");				//Print the key
		for(int i = 0; i < key.length; i++)
			System.out.print(key[i] + " ");
		System.out.println("");
		
		BigInteger keyNum = new BigInteger(1, key);	//Turn the key into a BigInteger
		
		//RSA encode keyNum
		BigInteger encryp = keyNum.modPow(E, N);
		output.writeObject(encryp);					//Send the key to the server
		output.writeObject(cipher.encode(myName));	//Send the encrypted name to the server
		
		
		
		//All encryption based stuff ends here
        this.setTitle(myName);      // Set title to identify chatter

        Box b = Box.createHorizontalBox();  // Set up graphical environment for
        outputArea = new JTextArea(8, 30);  // user
        outputArea.setEditable(false);
        b.add(new JScrollPane(outputArea));

        outputArea.append("Welcome to the Chat Group, " + myName + "\n");

        inputField = new JTextField("");  // This is where user will type input
        inputField.addActionListener(this);

        prompt = new JLabel("Type your messages below:");
        Container c = getContentPane();

        c.add(b, BorderLayout.NORTH);
        c.add(prompt, BorderLayout.CENTER);
        c.add(inputField, BorderLayout.SOUTH);

        Thread outputThread = new Thread(this);  // Thread is to receive strings
        outputThread.start();                    // from Server

		addWindowListener(
                new WindowAdapter()
                {
                    public void windowClosing(WindowEvent e)
                    { String closing = "CLIENT CLOSING";
						try
						{
							output.writeObject(cipher.encode(closing)); //Encrypt the closing message
						}
						catch(Exception e3)
						{
							System.out.println("Closing message failed");
						}
                      System.exit(0);
                     }
                }
            );

        setSize(500, 200);
        setVisible(true);

        }
        catch (Exception e)
        {
            System.out.println("Problem starting client!");
        }
    }

    public void run()
    {
        while (true)
        {
             try {
				//Decode all incoming messages
                byte[] currMsg = (byte[])input.readObject();
			    outputArea.append(cipher.decode(currMsg)+"\n");
             }
             catch (Exception e)
             {
                System.out.println(e +  ", closing client!");
                break;
             }
        }
        System.exit(0);
    }

    public void actionPerformed(ActionEvent e)
    {
        String currMsg = e.getActionCommand();      // Get input value
        inputField.setText("");
		//Encode all outgoing messages
		String msg = myName + ":" + currMsg;
		try
		{	
			output.writeObject(cipher.encode(msg));
		}
		catch(Exception e3)
		{
			System.out.println("Message failed");
		}
        //myWriter.println(myName + ":" + currMsg);   // Add name and send it
    }                                               // to Server

    public static void main(String [] args) throws IOException
    {
         SecureChatClient JR = new SecureChatClient();
         JR.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
    }
}

