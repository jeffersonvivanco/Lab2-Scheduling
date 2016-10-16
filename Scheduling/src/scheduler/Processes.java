package scheduler;
import javax.lang.model.element.Element;
import java.util.*;
import java.io.*;
/**
 * Created by jeffersonvivanco on 10/7/16.
 */

public class Processes {

    private ArrayList<Process> processes;
    private File randomNumFile;
    private Scanner sc;


    public Processes(){
        this.processes = new ArrayList<Process>();

    }

    public void add(Process p){
        this.processes.add(p);
    }
    public void remove(int i){
        this.processes.remove(i);
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
            processes.get(i).setReadyTime(arrivalTime);
            processes.get(i).setRemainingTime(cpuTime);
            this.processes.get(i).setBurstTime(randomOS(processes.get(i).getB()));
        }
        sc.close();
    }
    public Process getProcess(int i){
        return processes.get(i);
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

    @Override
    public String toString(){
        String string = "";
        for(int i=0; i<processes.size(); i++){
            string = string + processes.get(i).toString()+"\n\n";
        }
        return string;
    }
}
