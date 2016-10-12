package scheduler;
import javax.lang.model.element.Element;
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
    public int size(){
        return this.processes.size();
    }
    public void sort(){
        Collections.sort(this.processes);
        setRandomNumFile();
        for(int i=0; i<this.processes.size(); i++){
            this.processes.get(i).setProcessId(i);
            int cpuTime = this.processes.get(i).getC();
            int arrivalTime = this.processes.get(i).getA();
            this.processes.get(i).setCurrentTime(arrivalTime);
            this.processes.get(i).setRemainingTime(cpuTime-1);
            this.processes.get(i).setBurstTime(randomOS(processes.get(i).getB()));
        }
        sc.close();
    }
    public Process getProcess(int i){
        return processes.get(i);
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
    public Process remove(){
        Process p = this.fifoQueue.remove();
        return p;
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
    public Iterator<Process> iterator (){
        Iterator<Process> iterator = null;
        if(!this.fifoQueue.isEmpty()){
            iterator = this.fifoQueue.iterator();
        }
        return iterator;
    }
    @Override
    public String toString(){
        String string = "";
        if(string.equals("Hello")){
            for(Process p : this.fifoQueue){
                string  = string + p.toString() + "\n\n";
            }
        }
        else{
            for(int i=0; i<processes.size(); i++){
                string = string + processes.get(i).toString()+"\n\n";
            }
        }

        return string;
    }
}
