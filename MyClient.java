import java.io.*;
import java.net.Socket;
import java.util.ArrayList;

public class MyClient {
    public static void main(String args[]) throws IOException {
        // set up connection
        Socket s = new Socket("localhost", 50000);
        DataOutputStream dout = new DataOutputStream(s.getOutputStream());
        BufferedReader br = new BufferedReader(new InputStreamReader(s.getInputStream()));

        String response = ""; //store incoming messages from server
        String[] responseArray = {}; // store split server response as components
        ArrayList<Server> currentServers = new ArrayList<Server>(); //store current available servers
        Job currentJob = new Job(); //store current job
        Server chosenServer = new Server(); //store server selected for current job

        // hand-shaking 
        response = send("HELO", dout, br);
        response = send("AUTH " + System.getProperty("user.name"), dout, br);
        response = send("REDY", dout, br);

        // main loop - schedule all jobs
        while (true) {
            if(response.contains("NONE")){ // no more jobs - quit
                break;
            }
            else if (response.contains("JOBN")) { // recieved new job - store and get servers
                responseArray = response.split("\s");
                currentJob = new Job(responseArray[1], responseArray[2], responseArray[3], responseArray[4],
                        responseArray[5], responseArray[6]);
                response = send("GETS Capable " + currentJob.cores + " " + currentJob.memory + " " + currentJob.disk,
                        dout, br);
                response = send("OK", dout, br); //to recieve all server data
                response = send("OK", dout, br); //to recieve '.' at end
                
                // inner loop - store all server data
                while(true){
                    String[] server = response.split("\s");
                    if(server.length>1){
                        currentServers.add(new Server(server[0], server[1], server[3], server[4], server[5], server[6]));
                    }
                    response = br.readLine();
                    if(response.contains(".")){
                        break;
                    }
                }                
                chosenServer = chooseServer(currentServers);
                currentServers.clear();
            } 
            else if(response.contains(".")) { // no more to recieve - schedule job
                response = send("SCHD "+currentJob.id+" "+chosenServer.type+" "+chosenServer.id, dout, br);
                response = send("REDY", dout, br);
            } 
            else if(response.contains("JCPL")){ //recieved completed job info
                response = send("REDY", dout, br);
            }
            else break; // recieved unhandled message from server
        }

        // quit server and close connection
        response = send("QUIT", dout, br);
        dout.close();
        s.close();

    }

    public static String send(String message, DataOutputStream dout, BufferedReader br) throws IOException{
        // handles all sending messages to server and recieveing responces
        dout.write((message + "\n").getBytes());
        String response = br.readLine();
        System.out.println("C: "+message);
        System.out.println("S: "+response);
        return response;
    }

    public static Server chooseServer(ArrayList<Server> servers){
        // Given a list of all currently available servers 
        // Will return first server with the most cores (LRR)
        Server server = new Server();
        for (Server s : servers) {
            if(s.cores>server.cores){
                server = s;
            }
        }
        return server;
    }
}
