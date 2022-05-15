import java.net.*;
import java.nio.file.Files;
import java.io.*;
import java.util.*;

public class FileClient {

    private static Socket sock;
    private static String fileName;
    private static BufferedReader stdin;
    private static PrintStream os;

    public static void main(String[] args) throws IOException {
while(true) {
        try {
            sock = new Socket("localhost", 2540);
            stdin = new BufferedReader(new InputStreamReader(System.in));
        } catch (Exception e) {
            System.err.println("Cannot connect to the server, try again later.");
            System.exit(1);
        }

        os = new PrintStream(sock.getOutputStream());

        try {
              switch (Integer.parseInt(selectAction())) {
            case 1:
                os.println("1");
                sendFile();
                continue;
            case 2:
                os.println("2");
                System.out.print("Enter file name: ");
                fileName = stdin.readLine();
                os.println(fileName);
                receiveFile(fileName);
                continue;
		   case 3:
			 sock.close();
			System.exit(1);
	        }
        } catch (Exception e) {
            System.err.println("not valid input");
        }

     }
       
    }

    public static String selectAction() throws IOException {
        System.out.println("1. Send the Answers file.");
        System.out.println("2. Recieve Assignment file.");
	    System.out.println("3. Exit from the system.");
        System.out.print("\nStudent needs to select his/her action: ");

        return stdin.readLine();
    }


    public static void sendFile() {
        try {
            System.out.print("Enter Answer file name: ");
            fileName = stdin.readLine();

            File myFile = new File(fileName);
            byte[] mybytearray = new byte[(int) myFile.length()];
	    if(!myFile.exists()) {
		System.out.println("File does not exist..");
		return;
		}

            FileInputStream fis = new FileInputStream(myFile);
            BufferedInputStream bis = new BufferedInputStream(fis);

            DataInputStream dis = new DataInputStream(bis);
            dis.readFully(mybytearray, 0, mybytearray.length);
		
            OutputStream os = sock.getOutputStream();

            //Sending file name and file size to the server
            DataOutputStream dos = new DataOutputStream(os);
            dos.writeUTF(myFile.getName());
            dos.writeLong(mybytearray.length);
            dos.write(mybytearray, 0, mybytearray.length);
            dos.flush();
            System.out.println("File "+fileName+" sent to Server.");
        } catch (Exception e) {
            System.err.println("Exceptionnnn: "+e);
        }
    }

    public static void receiveFile(String fileName) {
        try {
            int bytesRead;
            InputStream in = sock.getInputStream();

            DataInputStream clientData = new DataInputStream(in);

            fileName = clientData.readUTF();
            OutputStream output = new FileOutputStream(fileName);
            long size = clientData.readLong();
            byte[] buffer = new byte[1024];
            while (size > 0 && (bytesRead = clientData.read(buffer, 0, (int) Math.min(buffer.length, size))) != -1) {
                output.write(buffer, 0, bytesRead);
                size -= bytesRead;
            }

            output.close();
            in.close();

            System.out.println("File "+fileName+" received from Server.");
            BufferedReader reader;
    		try {
    			reader = new BufferedReader(new FileReader("server.txt"));
    			String line = reader.readLine();
    			
    			
    			while (line != null) {
    				System.out.println(line);
    				
    				// read next line
    				line = reader.readLine();
    			}
    			reader.close();
        }	catch (IOException e) {
    			e.printStackTrace();
    		}
        } catch (IOException ex) {
		System.out.println("Exception: "+ex);
         }
    
}
}