package scheduler;
import java.util.*;
import java.io.*;
/**
 * Created by jeffersonvivanco on 10/7/16.
 */

public class Processes {
    private Queue<Process>fifoQueue;
    private ArrayList<Process> processes;
    private File randomNumFile;
    private Scanner sc;


    public Processes(){
        this.processes = new ArrayList<Process>();

    }
    public void add(Process p){
        this.processes.add(p);
    }
    public void setFifoQueue(){
        setRandomNumFile();
        this.fifoQueue = new LinkedList<Process>();
        Collections.sort(this.processes);
        for(int i=0; i<this.processes.size(); i++){
            this.processes.get(i).setProcessId(i);
            this.processes.get(i).setBurstTime(randomOS(processes.get(i).getB()));
            this.fifoQueue.add(processes.get(i));
        }
        sc.close();
    }
    public void setRandomNumFile(){
        this.randomNumFile = new File("/Users/jeffersonvivanco/IdeaProjects/Lab2-Scheduling/random-numbers.txt");
        try{
            this.sc = new Scanner(randomNumFile);
        }catch (FileNotFoundException e){
            e.printStackTrace();
        }
    }
    public int randomOS(int U){
        int X = sc.nextInt();
        int result = (1+(X%U));
        int v = 0;
        return result;
    }
    public Queue<Process> getFifoQueue(){
        return this.fifoQueue;
    }

    @Override
    public String toString(){
        String string = "";
        for(int i=0; i<processes.size(); i++){
            string = string + processes.get(i).toString()+"\n";
        }
        return string;
    }
}
