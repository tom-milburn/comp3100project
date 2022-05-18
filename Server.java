public class Server {
    String type, status;
    int id, limit, cores, memory, disk;

    public Server(){
        type = "undefined";
        id = -1;
        status = "undefined";
        limit = -1;
        cores = -1;
        memory = -1;
        disk = -1;
    }

    public Server(String type, String id, String status, String limit, String cores, String memory, String disk){
        this.type = type;
        this.id = Integer.parseInt(id);
        this.status = status;
        this.limit = Integer.parseInt(limit);
        this.cores = Integer.parseInt(cores);
        this.memory = Integer.parseInt(memory);
        this.disk = Integer.parseInt(disk);
    }
}
