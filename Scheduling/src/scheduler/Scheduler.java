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

        //Variables
        int precededingCPUBurst = 0;
        int cpuTime = 0;
        int arrivalTime = 0;
        int ioTime = 0;
        int finishingTime = 0;
        int turnaroundTime = 0;
        int waitingTime = 0;
        int processId = 0;
        int cpuBurst = 0;
        int ioBurst = 0;
        int processingTime = 0;
        Iterator<Process> iterator = fifoqueue.iterator();
        for(Process p : fifoqueue){
            if(p.getProcessId() == 0){
                precededingCPUBurst = 1;
            }
            cpuTime = p.getC();
            arrivalTime = p.getA();
            cpuBurst = p.getBurstTime();
            ioBurst = p.getM() * precededingCPUBurst;
            while(cpuTime > 0){
                if(cpuTime > 1){
                    processingTime = processingTime + cpuBurst + ioBurst;
                    ioTime++;
                }
                else{
                    processingTime = processingTime + cpuBurst;
                }
                cpuTime --;
            }
            finishingTime = processingTime;
            turnaroundTime = finishingTime  - arrivalTime;
            p.setFinishingTime(finishingTime);
            p.setIoTime(ioTime);
            p.setTurnaroundTime(turnaroundTime);
            precededingCPUBurst = cpuBurst;
        }
        System.out.println(fifoqueue.toString());

    }


}
