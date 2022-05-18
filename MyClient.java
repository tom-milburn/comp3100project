import java.io.*;
import java.net.Socket;
import java.util.ArrayList;

public class MyClient {
    public static void main(String args[]) throws IOException {
        // set up connection
        Socket s = new Socket("localhost", 50000);
        DataOutputStream dout = new DataOutputStream(s.getOutputStream());
        BufferedReader br = new BufferedReader(new InputStreamReader(s.getInputStream()));

        String response = ""; // store incoming messages from server
        String[] responseArray = {}; // store split server response as components
        ArrayList<Server> capableServers = new ArrayList<Server>(); // store current capable servers
        Job currentJob = new Job(); // store current job
        Server chosenServer = new Server(); // store server selected for current job
        int serverCount = 0; // number of servers - used for importing

        // hand-shaking
        response = send("HELO", dout, br);
        response = send("AUTH " + System.getProperty("user.name"), dout, br);
        response = send("REDY", dout, br);

        // main loop - schedule all jobs
        while (true) {
            if (response.contains("NONE")) { // no more jobs - quit
                break;
            }

            // recieved job - store job and get capable servers
            if (response.contains("JOBN") || response.contains("JOBP")) {
                responseArray = response.split("\s");
                currentJob = new Job(responseArray[1], responseArray[2], responseArray[3], responseArray[4],
                        responseArray[5], responseArray[6]);
                response = send("GETS Capable " + currentJob.cores + " " + currentJob.memory + " " + currentJob.disk,
                        dout, br);
                responseArray = response.split("\s");
                serverCount = Integer.parseInt(responseArray[1]);
                response = send("OK", dout, br); // to recieve all server data

                capableServers.clear();
                while (true) { // loop for saving server data
                    serverCount--;
                    String[] server = response.split("\s");
                    if (server.length > 1) { // ensure no more data message '.' is not attempted to be added
                        capableServers.add(new Server(server[0], server[1], server[2], server[3], server[4], server[5], server[6]));
                    }
                    if (serverCount > 0) {
                        response = br.readLine();
                    } else
                        break;
                }

                response = send("OK", dout, br); // to recieve '.' at end

                chosenServer = selectServer(currentJob, capableServers);

                // schedule current job and get next job
                response = send("SCHD " + currentJob.id + " " + chosenServer.type + " " + chosenServer.id, dout, br);
                response = send("REDY", dout, br);
            }

            // recieved completed job or server status info - client still ready
            else if (response.contains("JCPL") || response.contains("RESF") || response.contains("RESR")) {
                response = send("REDY", dout, br);
            } else
                break; // recieved unhandled message from server
        }

        // quit server and close connection
        response = send("QUIT", dout, br);
        dout.close();
        s.close();

    }

    public static String send(String message, DataOutputStream dout, BufferedReader br) throws IOException {
        // handles all sending messages to server and recieveing responces
        dout.write((message + "\n").getBytes());
        String response = br.readLine();
        System.out.println("C: " + message);
        System.out.println("S: " + response);
        return response;
    }

    public static Server selectServer(Job job, ArrayList<Server> servers) {
        //
        for (Server s : servers) {
            if(s.status.equals("idle") || s.status.equals("inactive")){
                return s;
            }
        }
        return servers.get(0);
    }
}