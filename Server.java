import java.io.*;
import java.net.*;
import java.util.*;

class Server
{
	ServerSocket welcomeSocket;
	public static int port;				// stores the port number at which the server is operating									
	
	Server(int port)
	{
		try
		{
			welcomeSocket = new ServerSocket(port);
		}
		catch(Exception e)
		{
			System.out.println(e);
		}
	}

	public static void main(String argv[]) throws Exception
	{
		
		// Taking port number as input from the user
		BufferedReader inFromUser = new BufferedReader(new InputStreamReader(System.in));
		System.out.print("On which port should I run the server : ");
		port = Integer.parseInt(inFromUser.readLine());

		

		Server fileServer = new Server(port);
		System.out.println("The server is running on port " + port);
		while(true)
		{
			
			// accepting a new socket connection
			Socket connection = fileServer.welcomeSocket.accept();
			
			// creating a saperate thread for that connection
			(new ServerThread(connection, fileServer)).start();
		}
	}
}


// Thread Class
class ServerThread extends Thread
{
	Socket socket;
	
	ServerThread(Socket socket, Server file_server)
	{
		this.socket = socket;
	}

	public void run()
	{
		try
		{
			
			
			BufferedReader inFromClient = new BufferedReader(new InputStreamReader(socket.getInputStream()));
			DataOutputStream outToClient = new DataOutputStream(socket.getOutputStream());
			inFromClient.readLine();
			
			System.out.println("Request received ");

			outToClient.writeBytes("OK\n");
			
			String fileName = inFromClient.readLine();
			String proxyPath = inFromClient.readLine();
			

			// Checking if this is the end of the path
			if(proxyPath.equals("")){
				
				OutputStream outStream = socket.getOutputStream();

				FileInputStream fis;
	    		BufferedInputStream bis;

				File myFile = null;

				
				try
				{
					// tranfering file if it exists
					myFile = new File (fileName);	
					
					
					byte [] mybytearray  = new byte [(int)myFile.length()];

					if((int)myFile.length() == 0){
						throw new NullPointerException("demo");
					}
					outToClient.writeBytes(Long.toString(myFile.length()) + "\n");

					inFromClient.readLine();
					fis = new FileInputStream(myFile);
				    bis = new BufferedInputStream(fis);
				    bis.read(mybytearray,0,mybytearray.length);		 
				    System.out.println("Sending " + fileName);
				    outStream.write(mybytearray,0,mybytearray.length);
				    outStream.flush();
				    System.out.println("Done.");
				    socket.close();	
				}
				catch(Exception e)
				{
					// throwing error if file doesn't exist
					outToClient.writeBytes("FNF\n");
					System.out.println("File that is requested is not found!");
				}
			    
			}
			else
			{
				// finding the next server in the path
				String nextServer = proxyPath.split("," , 2)[0];			
				String restPath = "";			
				if(proxyPath.split("," , 2).length > 1)
				{
					restPath = proxyPath.split("," , 2)[1];
				}
				
				try{

					String serverIp = nextServer.split(" ",2)[0];
					int serverPort = Integer.parseInt(nextServer.split(" ",2)[1]);

					// creating a new socket connection with the next server on the path
					Socket nextsocket = new Socket(serverIp, serverPort);
					DataOutputStream outToServer = new DataOutputStream(nextsocket.getOutputStream());
	        		outToServer.writeBytes("HI\n");

	        		BufferedReader inFromServer = new BufferedReader(new InputStreamReader(nextsocket.getInputStream()));

	        		if(inFromServer.readLine().equals("OK"))
					{
						
						
						// sending filename and rest of the path to the next server
						outToServer.writeBytes(fileName + "\n");
						outToServer.writeBytes(restPath + "\n");
					
						String res = inFromServer.readLine();
						
						if(!res.equals("FNF") && !res.equals("PDE")){
							// receiving data from the next server and passing it on to the client
							int fileSize = Integer.parseInt(res) * 100;
							
							outToServer.writeBytes("OK\n");
								
							
							outToClient.writeBytes(Integer.toString(fileSize) + "\n");
								
							inFromClient.readLine();
								
							int bytesRead;
			    			int current = 0;
							byte [] mybytearray  = new byte [fileSize];
							InputStream inputStream = nextsocket.getInputStream();
							OutputStream outStream = socket.getOutputStream();
							
			    		
							bytesRead = inputStream.read(mybytearray,0,10000);
						
							current = bytesRead;
							
							outStream.write(mybytearray,0,10000);
							

							do {
								bytesRead = inputStream.read(mybytearray, current, 10000);
								int incurr = current;
								
								if(bytesRead >= 0)
								{
									current += bytesRead;	
								}
								outStream.write(mybytearray,incurr,10000);
							}while(bytesRead > 0);
						}
						else 
						{
							outToClient.writeBytes(res + "\n");
						}
						

						
						
						socket.close();
						
					}
					else
					{
						System.out.println("MISSION ABORT!!");
					}
				}catch(Exception e){
					// throwing errors
					System.out.println("Path do not Exist!!");
					outToClient.writeBytes("PDE\n");
				}				

					
				

			}

			
			
		}catch(Exception e){
			System.out.println(e);
		}
	}
}
