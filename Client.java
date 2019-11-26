import java.io.*;
import java.net.*;

class Client
{
	public static String instFile = "instructions.txt";		// file from which instructions are to read
	public static void main(String argv[])
	{
		try
		{
			
			// reading the instruction file
			FileInputStream instFileReader = new FileInputStream(instFile);
			BufferedReader inst = new BufferedReader(new InputStreamReader(instFileReader));
			String fileName = inst.readLine();
			String serverInfo = inst.readLine();
			String proxyPath = serverInfo;
			while((serverInfo = inst.readLine()) != null){
				proxyPath = proxyPath + "," + serverInfo;
			}

			String serverIp = "";
			int serverPort = 0;
			
			// extrating the information of the first server in the path
			String firstServer = proxyPath.split("," , 2)[0];			
			String restPath = "";			
			if(proxyPath.split("," , 2).length > 1){
				restPath = proxyPath.split("," , 2)[1];
			}
			
			serverIp = firstServer.split(" ",2)[0];
			serverPort = Integer.parseInt(firstServer.split(" ",2)[1]);

				
			

			Client client = new Client();
			BufferedReader inFromUser = new BufferedReader(new InputStreamReader(System.in));
			
			
			// creating a socket with the client
			Socket socket = new Socket(serverIp, serverPort);
			
			DataOutputStream outToServer = new DataOutputStream(socket.getOutputStream());
        	outToServer.writeBytes("HI\n");
			
			BufferedReader inFromServer = new BufferedReader(new InputStreamReader(socket.getInputStream()));
			
			

			if(inFromServer.readLine().equals("OK"))
			{
				

				outToServer.writeBytes(fileName + "\n");
				outToServer.writeBytes(restPath + "\n");
				String res = inFromServer.readLine();
				
				if(!res.equals("FNF") && !res.equals("PDE")){
					
					// receiving the contents and creating a file
					int fileSize = Integer.parseInt(res) + 100;
					outToServer.writeBytes("OK\n");
					
					int bytesRead;
	    			int current = 0;
					byte [] mybytearray  = new byte [fileSize];
					InputStream inputStream = socket.getInputStream();
					
						
	    			FileOutputStream fos;
	    			BufferedOutputStream bos;
					fos = new FileOutputStream(fileName);
					bos = new BufferedOutputStream(fos);
					bytesRead = inputStream.read(mybytearray,0,mybytearray.length);
					current = bytesRead;


					do {
						bytesRead = inputStream.read(mybytearray, current, (mybytearray.length-current));
						if(bytesRead >= 0)
						{
							current += bytesRead;	
						} 
					}while(bytesRead > -1);
					

					bos.write(mybytearray, 0 , current);
					bos.flush();
					System.out.println("File " + "new" + fileName + " downloaded");
				}
				else if(res.equals("FDE"))
				{
					System.out.println("File Not Found!");
				}
				else
				{
					System.out.println("Path doesn't exist");
				}

				

			}
			else
			{
				System.out.println("MISSION ABORT!!");
			}
		}catch(Exception e){
			System.out.println(e);
		}
	}
}