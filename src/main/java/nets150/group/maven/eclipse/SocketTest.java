package nets150.group.maven.eclipse;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.Scanner;

public class SocketTest {
    
    private Socket socket; 
    private Scanner in; 
    private PrintWriter out;
    
    public SocketTest(String servername, int port) throws UnknownHostException, IOException {
        socket = new Socket(servername, port);
        System.out.println("connected");
        
        in = new Scanner(socket.getInputStream());
        out = new PrintWriter(socket.getOutputStream(), true);
        
    }
    
    

}
