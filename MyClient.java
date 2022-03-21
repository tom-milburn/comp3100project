import java.io.*;
import java.net.Socket;

public class MyClient {
    public static void main(String args[]) throws IOException{
        Socket s = new Socket("localhost", 50000);
        DataOutputStream dout = new DataOutputStream(s.getOutputStream());
        BufferedReader br = new BufferedReader(new InputStreamReader(s.getInputStream()));

        String response = "";
        String data = "";
        Job currentJob = new Job();

        response = send("HELO", dout, br);
        response = send("AUTH" + System.getProperty("user.name"), dout, br);
        response = send("REDY", dout, br);

        while(!response.equals("NONE")){ //goto QUIT when recieve NONE
            String[] responseArray = response.split("\s");
            
            while(!response.equals(".")){
                if(responseArray[0].equals("JOBN")){
                    currentJob = new Job(responseArray[1], responseArray[2], responseArray[3], responseArray[4], responseArray[5], responseArray[6]);
                    response = send("GETSCapable "+responseArray[4]+" "+responseArray[5]+" "+responseArray[6], dout, br);
                } else if(responseArray[0].equals("DATA")){
                    response = send("OK", dout, br);
                    data = response;
                } else {
                    response = send("OK", dout, br);
                }
            }

            response = send("SCHD", dout, br); //TODO: finish schd message

            response = send("REDY", dout, br); //client all done, ready for next job
        }

        response = send("QUIT", dout, br);

        dout.close();
        s.close();
    
    }

    public static String send(String message, DataOutputStream dout, BufferedReader br) throws IOException{
        dout.write((message+"\n").getBytes());
        String response = br.readLine();
        System.out.println(response);
        return response;
    }
}
