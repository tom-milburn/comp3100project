public class Job {
    //Job class is used to store the current job for easy access to all
    //related information such as id and required resources
    int submitTime, id, estRunTime, cores, memory, disk;

    public Job() { // default constructor - set values to -1 to indicate not real job
        this.submitTime = -1;
        this.id = -1;
        this.estRunTime = -1;
        this.cores = -1;
        this.memory = -1;
        this.disk = -1;
    }

    public Job(String submitTime, String id, String estRunTime, String cores, String memory, String disk) {
        //convert strings from server to integers for easy comparisons
        this.submitTime = Integer.parseInt(submitTime);
        this.id = Integer.parseInt(id);
        this.estRunTime = Integer.parseInt(estRunTime);
        this.cores = Integer.parseInt(cores);
        this.memory = Integer.parseInt(memory);
        this.disk = Integer.parseInt(disk);
    }
}