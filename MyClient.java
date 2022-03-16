import java.io.*;
import java.net.Socket;

public class MyClient {
    public static void main(String args[]) throws IOException{
        Socket s = new Socket("localhost", 50000);
        DataInputStream din = new DataInputStream(s.getInputStream());
        DataOutputStream dout = new DataOutputStream(s.getOutputStream());
        BufferedReader sysReader = new BufferedReader(new InputStreamReader(System.in));
        BufferedReader socketReader = new BufferedReader(new InputStreamReader(s.getInputStream()));
    
        String incoming = "temp", outgoing = "testing";
        while (incoming.equals("BYE")){
            dout.write((outgoing+ "\n").getBytes());
            dout.flush();
            incoming = socketReader.readLine();
            System.out.println(incoming);
        }

        dout.close();
        s.close();
    
    }
}
