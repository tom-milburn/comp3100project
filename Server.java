public class Server {
    //Server class is used to store the servers recieved from ds-sim
    //as well as store the details of the selected server to have the job scheduled
    String type, status;
    int id, limit, cores, memory, disk;

    public Server() {
        //default constructor - set values to -1 or undefinded to indicate not real server
        type = "undefined";
        id = -1;
        status = "undefined";
        limit = -1;
        cores = -1;
        memory = -1;
        disk = -1;
    }

    public Server(String type, String id, String status, String limit, String cores, String memory, String disk) {
        //convert to relevant data type for use in comparisons or messages to ds-sim
        this.type = type;
        this.id = Integer.parseInt(id);
        this.status = status;
        this.limit = Integer.parseInt(limit);
        this.cores = Integer.parseInt(cores);
        this.memory = Integer.parseInt(memory);
        this.disk = Integer.parseInt(disk);
    }
}
