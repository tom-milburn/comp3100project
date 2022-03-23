import java.io.*;
import java.net.Socket;
import java.util.ArrayList;

public class MyClient {
    public static void main(String args[]) throws IOException {
        Socket s = new Socket("localhost", 50000);
        DataOutputStream dout = new DataOutputStream(s.getOutputStream());
        BufferedReader br = new BufferedReader(new InputStreamReader(s.getInputStream()));

        String response = "";
        String[] responseArray = {};
        ArrayList<Server> currentServers = new ArrayList<Server>();
        Job currentJob = new Job();
        Server chosenServer = new Server();

        response = send("HELO", dout, br);
        response = send("AUTH" + System.getProperty("user.name"), dout, br);
        response = send("REDY", dout, br);

        while (!response.equals("NONE")) { // goto QUIT when recieve NONE
            responseArray = response.split("\s");
            if (responseArray[0].equals("JOBN")) {
                currentJob = new Job(responseArray[1], responseArray[2], responseArray[3], responseArray[4],
                        responseArray[5], responseArray[6]);
                response = send("GETSCapable " + currentJob.cores + " " + currentJob.memory + " " + currentJob.disk,
                        dout, br);
                while(true){
                    response = send("OK", dout, br);
                    if(response.equals(".")){
                        chosenServer = currentServers.get(currentServers.size()-1); //choose last server for now
                        response = send("SCHD "+currentJob.id+" "+chosenServer.type+" "+chosenServer.id, dout, br);
                        currentServers.clear(); //remove all servers for next job
                        break;
                    }
                    String[] server = response.split("\s");
                    currentServers.add(new Server(server[0], server[1], server[3], server[4], server[5], server[6]));
                }
            }
            break;
            //response = send("REDY", dout, br); //client all done, ready for next job
        }

        response = send("QUIT", dout, br);

        dout.close();
        s.close();

    }

    public static String send(String message, DataOutputStream dout, BufferedReader br) throws IOException {
        dout.write((message + "\n").getBytes());
        String response = br.readLine();
        System.out.println(response);
        return response;
    }
}
