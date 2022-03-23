public class Server {
    String type;
    int id, limit, cores, memory, disk;
    float hourlyRate;

    public Server(){
        type = "undefined";
        id = -1;
        limit = -1;
        hourlyRate = -1;
        cores = -1;
        memory = -1;
        disk = -1;
    }

    public Server(String type, String id, String limit, String hourlyRate, String cores, String memory, String disk){
        this.type = type;
        this.id = Integer.parseInt(id);
        this.limit = Integer.parseInt(limit);
        this.hourlyRate = Float.parseFloat(hourlyRate);
        this.cores = Integer.parseInt(cores);
        this.memory = Integer.parseInt(memory);
        this.disk = Integer.parseInt(disk);
    }
}
