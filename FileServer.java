import java.net.*;
import java.io.*;
import java.util.*;
import java.nio.file.Files;
import java.util.logging.Logger;
import java.util.stream.Stream;
public class FileServer {

    private static ServerSocket serverSocket;
    private static Socket clientSocket = null;

    public static void main(String[] args) throws IOException {

        try {
            serverSocket = new ServerSocket(2540);
            System.out.println("Server started ...   ");
            BufferedReader reader;
            String clientName = "";
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
        }catch (Exception e) {
            System.err.println("Port already in use.");
            System.exit(1);
        }
   
  
        while (true) {
            try {
                clientSocket = serverSocket.accept();
               System.out.println("Accepted connection from Client : " + clientSocket);

                Thread t = new Thread(new ServiceClient(clientSocket));

                t.start();

            } catch (Exception e) {
                System.err.println("Error in connection attempt.");
            }
        }
    }
}