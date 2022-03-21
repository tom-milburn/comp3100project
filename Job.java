public class Job {
    int submitTime, id, estRunTime, cores, memory, disk;

    public Job(){ //default constructor
        this.submitTime = -1;
        this.id = -1;
        this.estRunTime = -1;
        this.cores = -1;
        this.memory = -1;
        this.disk = -1;
    }

    public Job(String submitTime, String id, String estRunTime, String cores, String memory, String disk){
        this.submitTime = Integer.parseInt(submitTime);
        this.id = Integer.parseInt(id);
        this.estRunTime = Integer.parseInt(estRunTime);
        this.cores = Integer.parseInt(cores);
        this.memory = Integer.parseInt(memory);
        this.disk = Integer.parseInt(disk);
    }
}
