import java.net.*;
import java.nio.file.Files;
import java.io.*;
import java.util.*;
import java.util.logging.Logger;
public class ServiceClient implements Runnable {

    private Socket clientSocket;
    private BufferedReader in = null;

    public ServiceClient(Socket client) {
        this.clientSocket = client;
    }

    @Override
    public void run() {
        try {
            in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
            String clientSelection;
            while ((clientSelection = in.readLine()) != null) {
                switch (clientSelection) {
                    case "1":
                        receiveFile();
                        continue;
                    case "2":
                        String outGoingFileName;
                        while ((outGoingFileName = in.readLine()) != null) {
                            sendFile(outGoingFileName);
                        }
			continue;
		    case "3":
			System.exit(1);	

                       break;
                    default:
                        System.out.println("Incorrect command received.");
                        break;
                }
               
            }

        } catch (IOException ex) {
          
        }
    }

    public void receiveFile() {
        try {
            int bytesRead;
            int score = 0;
            //String clientName = "";
            DataInputStream clientData = new DataInputStream(clientSocket.getInputStream());

            String fileName = clientData.readUTF();
            OutputStream output = new FileOutputStream(fileName);
            long size = clientData.readLong();
            byte[] buffer = new byte[1024];
            while (size > 0 && (bytesRead = clientData.read(buffer, 0, (int) Math.min(buffer.length, size))) != -1) {
            	//System.out.println("string from file" + clientData.read(buffer));
                output.write(buffer, 0, bytesRead);
                size -= bytesRead;
                //System.out.println("size "+ size);
            }

            output.close();
            clientData.close();

            
            String content = "File "+fileName+" received from Student ";
            BufferedReader reader, answerFileReader;
            String clientName = "";
    		try {
    			reader = new BufferedReader(new FileReader(fileName));
    			answerFileReader = new BufferedReader(new FileReader("answers.txt"));
    			String line = reader.readLine();
    			System.out.println("Student Name : " +line);
    			String answer = answerFileReader.readLine();
    			clientName += line;
    			System.out.println("" + content + clientName);
    			int questionNumber = 1;
    			while (questionNumber <=10 && line != null && answer != null) {
    				// read next line
    				line = reader.readLine();
    				System.out.println(line);
    				answer = answerFileReader.readLine();
    				if(answer.equalsIgnoreCase(line)) {
    					score++;
    				}
    				questionNumber++;
    			}
    			reader.close();
    			answerFileReader.close();
    			System.out.println(" ========================================= ");
    			System.out.println("  Score of Student "+ clientName + " is : " + score   );
    			System.out.println(" ========================================= ");
    		    switch(score) {
    		    case 10:
        			System.out.println("  Grade of Student "+ clientName + " is : A"    );
    		        break;
    		    case 9:
        			System.out.println("  Grade of Student "+ clientName + " is : A"   );
    		        break;
    		    case 8:
        			System.out.println("  Grade of Student "+ clientName + " is : B"    );
    		        break;

    		    case 7:
        			System.out.println("  Grade of Student "+ clientName + " is : B"   );
    		        break;

    		    case 6:
        			System.out.println("  Grade of Student "+ clientName + " is : C"    );
    		        break;

    		    case 5:
        			System.out.println("  Grade of Student "+ clientName + " is : C"   );
    		        break;

    		    case 4:
        			System.out.println("  Grade of Student "+ clientName + " is : D"    );
    		        break;

    		    case 3:
        			System.out.println("  Grade of Student "+ clientName + " is : D"   );
    		        break;

    		    default:
        			System.out.println("  Grade of Student "+ clientName + " is : F"   );
    		        break;
        		

    		    }
    		} catch (IOException e) {
    			e.printStackTrace();
    		}
        } catch (IOException ex) {
            System.err.println("Client error. Connection closed.");
        }
    }

    public void sendFile(String fileName) {
        try {
           
            File myFile = new File(fileName);  //handle file reading
            byte[] mybytearray = new byte[(int) myFile.length()];

            FileInputStream fis = new FileInputStream(myFile);
            BufferedInputStream bis = new BufferedInputStream(fis);
            
            DataInputStream dis = new DataInputStream(bis);
            dis.readFully(mybytearray, 0, mybytearray.length);

           
            OutputStream os = clientSocket.getOutputStream();  //handle file send over socket

            DataOutputStream dos = new DataOutputStream(os); //Sending file name and file size to the server
            dos.writeUTF(myFile.getName());
            dos.writeLong(mybytearray.length);
            dos.write(mybytearray, 0, mybytearray.length);
            dos.flush();
            System.out.println("File "+fileName+" sent to Student.");
        } catch (Exception e) {
            System.err.println("File does not exist!");
        } 
    }
}