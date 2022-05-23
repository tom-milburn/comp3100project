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
        ArrayList<Server> availServers = new ArrayList<Server>(); // store current available servers
        Job currentJob = new Job(); // store current job
        Server chosenServer = new Server(); // store server selected for current job

        // hand-shaking
        response = send("HELO", dout, br);
        response = send("AUTH " + System.getProperty("user.name"), dout, br);
        response = send("REDY", dout, br);

        // main loop - schedule all jobs
        while (true) {
            if (response == null || response.contains("NONE")) { // no more jobs - quit
                break;
            }

            // recieved job - store job and get capable servers
            if (response.contains("JOBN") || response.contains("JOBP")) {
                responseArray = response.split("\s");
                currentJob = new Job(responseArray[1], responseArray[2], responseArray[3], responseArray[4],
                        responseArray[5], responseArray[6]);

                // Attempt to get available servers
                availServers = getServers("Avail", currentJob, dout, br);

                // If not available servers - get all capable ones
                if (availServers.isEmpty()) {
                    capableServers = getServers("Capable", currentJob, dout, br);
                }

                // choose the best server from all options
                chosenServer = selectServer(currentJob, availServers, capableServers, dout, br);

                // schedule current job and get next job
                response = send("SCHD " + currentJob.id + " " + chosenServer.type + " " + chosenServer.id, dout, br);
                response = send("REDY", dout, br);
            }

            // job completed - client still ready
            else if (response.contains("JCPL")) {
                response = send("REDY", dout, br);
            }

            // recieved server status info - client still ready
            else if (response.contains("OK") || response.contains("RESF") || response.contains("RESR")) {
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

    public static Server selectServer(Job job, ArrayList<Server> availServers, ArrayList<Server> capServers,
            DataOutputStream dout, BufferedReader br)
            throws NumberFormatException, IOException {
        Server shortestWaitServer = new Server(); // keeps track of shortest wait server
        Integer shortestWaitTime = null; // keeps track of shortest wait servers wait time
        int serverRunningTime = 0; // stored time of currently running job on server 

        // do first fit if available
        if (availServers.size() != 0) { 
            return availServers.get(0);
        }

        //no available servers - schedule job on capable server with shortest wait
        for (Server s : capServers) {
            //get estimated wait time of waiting jobs
            int waitTime = Integer.parseInt(send("EJWT " + s.type + " " + s.id, dout, br));
            
            //check if running jobs
            if (Integer.parseInt(send("CNTJ " + s.type + " " + s.id + " 2", dout, br)) != 0) {
                //get estimated run time of running job
                int jobCount = Integer.parseInt(send("LSTJ " + s.type + " " + s.id, dout, br).split("\s")[1]);
                String response = send("OK", dout, br);
                while (jobCount > 1) {
                    jobCount--;
                    serverRunningTime = Integer.parseInt(response.split("\s")[4]);
                    response = br.readLine();
                }
                send("OK", dout, br);
                //add runtime to wait time
                waitTime += serverRunningTime;
            }
            // find shotest wait time/server
            if (shortestWaitTime == null || waitTime < shortestWaitTime) {
                shortestWaitTime = waitTime;
                shortestWaitServer = s;
            }
        }
        return shortestWaitServer;
    }

    public static ArrayList<Server> getServers(String request, Job job, DataOutputStream dout, BufferedReader br)
            throws IOException {
        ArrayList<Server> servers = new ArrayList<Server>(); // store servers to be returned

        String response = send("GETS " + request + " " + job.cores + " " + job.memory + " " + job.disk,
                dout, br);
        String[] responseArray = response.split("\s");
        int serverCount = Integer.parseInt(responseArray[1]); // used to import servers
        response = send("OK", dout, br); // to recieve all server data

        if (serverCount != 0) {
            while (true) { // loop for saving server data
                serverCount--;
                String[] server = response.split("\s");
                if (server.length > 1) { // ensure no more data message '.' is not attempted to be added
                    servers.add(new Server(server[0], server[1], server[2], server[3], server[4], server[5],
                            server[6]));
                }
                if (serverCount > 0) {
                    response = br.readLine(); // read next server info
                } else
                    break;
            }
            response = send("OK", dout, br); // to recieve '.' at end
        }
        return servers;
    }
}
