package scheduler;
import java.io.*;
import java.util.*;
/**
 * Created by jeffersonvivanco on 10/3/16.
 * Class:
 */
public class Scheduler {

    public static void main(String[] args){
        Scheduler scheduler = new Scheduler();
        File f = new File("/Users/jeffersonvivanco/IdeaProjects/Lab2-Scheduling/FCFSInputs/input-1.txt");
        Processes processes = new Processes();
        String [] inputLine = null;
        try{
            Scanner sc = new Scanner(f);

            while(sc.hasNextLine()){
                inputLine = sc.nextLine().split("\\(|\\)");
            }
            sc.close();
        }catch(FileNotFoundException e){
            e.printStackTrace();
        }

        for(int i=0; i<inputLine.length; i++){

            if(inputLine[i].matches(".*[0-9].*") && inputLine[i].length() > 4){
                Process p = new Process(inputLine[i]);
                processes.add(p);
            }
        }
        scheduler.fcfsScheduling(processes);

    }
    public void fcfsScheduling(Processes processes){
        processes.setFifoQueue();
        Queue<Process> fifoqueue = processes.getFifoQueue();



    }


}
