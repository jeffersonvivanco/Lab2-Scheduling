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
        File f = new File("/Users/jeffersonvivanco/IdeaProjects/Lab2-Scheduling/FCFSInputs/input-7.txt");
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
        System.out.println(processes);


    }
    public void fcfsScheduling(Processes processes) {

        processes.sort();

        int clock = 0;

        int totalNumOfProcesses = processes.size();

        ArrayList<Process> terminatedList = new ArrayList<Process>();

        Queue<Process> readyQ = new LinkedList<Process>();

        ArrayList<Process> blockedList = new ArrayList<Process>();

        while(terminatedList.size() != totalNumOfProcesses){

//            System.out.println(clock);//Checking the clock

            for(int i=0; i<processes.size(); i++){
                if(processes.getProcess(i).getA() <= clock){
                    readyQ.add(processes.getProcess(i));
                    processes.remove(i);
                }
            }

            if(!readyQ.isEmpty()){
                Process pr  = readyQ.peek();
                if(pr.getReadyTime() <= clock){
                    pr = readyQ.remove();

                    int cpuBurstTime = pr.getBurstTime();


                    int waitTime  = clock - pr.getReadyTime();



                    int currentWaitTime = pr.getWaitingTime();
                    pr.setWaitingTime(waitTime+currentWaitTime);




                    if(pr.getRemainingTime() > 1){
                        int ioBurstTime = cpuBurstTime*pr.getM();
                        int currentIo = pr.getIoTime();
                        pr.setIoTime(currentIo + ioBurstTime);


                        int currentFinishTime = pr.getFinishingTime();
                        int finishTime = cpuBurstTime+ioBurstTime;
                        pr.setFinishingTime(currentFinishTime+finishTime+waitTime);

                        pr.setRemainingTime(pr.getRemainingTime()-cpuBurstTime);

                        pr.setBlockedTime(ioBurstTime);
                        pr.setReadyTime(pr.getBlockedTime() + clock + 1);
                        blockedList.add(pr);
                        clock = clock+cpuBurstTime;

                    }
                    else{
                        pr.setFinishingTime(pr.getBurstTime() + pr.getFinishingTime()+pr.getA() + waitTime);
                        pr.setTurnaroundTime(pr.getFinishingTime() - pr.getA());
                        pr.setRemainingTime(pr.getRemainingTime() - cpuBurstTime);
                        terminatedList.add(pr);
                        clock = clock + cpuBurstTime;

                    }

                }

            }
            else{
                clock++;
            }


            for(int x=0; x<blockedList.size(); x++){
                if(blockedList.get(x).getReadyTime() <= clock){

                    Process tr = blockedList.remove(x);
                    readyQ.add(tr);
                }
            }

        }
//        Collections.sort(terminatedList);
        for(int i=0; i<terminatedList.size(); i++){
            System.out.println(terminatedList.get(i));
        }




    }


}
