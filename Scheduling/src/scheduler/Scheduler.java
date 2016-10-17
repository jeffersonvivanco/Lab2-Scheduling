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
        File f = new File("/Users/jeffersonvivanco/IdeaProjects/Lab2-Scheduling/FCFSInputs/input-3.txt");
        Processes processes1 = new Processes();
        Processes processes2 = new Processes();
        Processes processes3 = new Processes();
        Processes processes4  = new Processes();


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
                Process p1 = new Process(inputLine[i]);
                Process p2 = new Process(inputLine[i]);
                Process p3 = new Process(inputLine[i]);
                Process p4 = new Process(inputLine[i]);
                processes1.add(p1);
                processes2.add(p2);
                processes3.add(p3);
                processes4.add(p4);
            }
        }
        System.out.println("The original input was: "+processes1.getProcsNumsString());
        System.out.println("The sorted input is: "+processes1.getProcsNumsString()+"\n\n");


        scheduler.fcfsScheduling(processes1);
        scheduler.roundRobinScheduling(processes2);
        scheduler.uniprogrammedScheduling(processes3);
        scheduler.sjfScheduling(processes4);



    }
    public void fcfsScheduling(Processes processes) {


        processes.sort();


        int clock = 0;

        int totalNumOfProcesses = processes.size();

        ArrayList<Process> terminatedList = new ArrayList<Process>();

        Queue<Process> readyQ = new LinkedList<Process>();

        Queue<Process> blockedQ = new LinkedList<Process>();

        int timeWithCpuAndIo = 0;
        int timeWithCpu = 0;
        int timeWithIo = 0;
        int index = 0;

        while(terminatedList.size() != totalNumOfProcesses){

//            System.out.println(clock);//Checking the clock

            for(int i=0; i<processes.size() && index<processes.size();i++){
                if(processes.getProcess(index).getA() <= clock){
                    readyQ.add(processes.getProcess(index));
                    index++;
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
                        timeWithCpu = timeWithCpu+cpuBurstTime;
                        timeWithCpuAndIo = timeWithCpuAndIo + cpuBurstTime+ioBurstTime;
                        timeWithIo = timeWithIo + ioBurstTime;
                        int finishTime = cpuBurstTime+ioBurstTime;
                        pr.setFinishingTime(currentFinishTime+finishTime+waitTime);

                        pr.setRemainingTime(pr.getRemainingTime()-cpuBurstTime);

                        pr.setBlockedTime(ioBurstTime);
                        pr.setReadyTime(pr.getBlockedTime() + clock + 1);

                        blockedQ.add(pr);

                        clock = clock+cpuBurstTime;

                    }
                    else{
                        pr.setFinishingTime(pr.getBurstTime() + pr.getFinishingTime()+pr.getA() + waitTime);
                        pr.setTurnaroundTime(pr.getFinishingTime() - pr.getA());
                        pr.setRemainingTime(pr.getRemainingTime() - cpuBurstTime);
                        terminatedList.add(pr);
                        clock = clock + cpuBurstTime;
                        timeWithCpu = timeWithCpu+cpuBurstTime;
                        timeWithCpuAndIo = timeWithCpuAndIo + cpuBurstTime;

                    }

                }

            }
            else{
                clock++;
            }

            if(!blockedQ.isEmpty()){
                Process tr = blockedQ.peek();
                if(tr.getReadyTime() <= clock){
                    tr = blockedQ.remove();
                    readyQ.add(tr);
                }
            }


        }
        System.out.println("The scheduling algorithm used was First Come First Served.\n");
        double totalWaitTime = 0; //Incremented below and used to calculate average wait time.
        double totalTurnaroundTime = 0;
        for(int i=0; i<terminatedList.size(); i++){
            totalWaitTime  = totalWaitTime + terminatedList.get(i).getWaitingTime();
            totalTurnaroundTime  = totalTurnaroundTime + terminatedList.get(i).getTurnaroundTime();
            System.out.println(terminatedList.get(i)+"\n");
        }
        System.out.println("Summary Data: ");
        System.out.println("\tFinishing time: "+clock);
        double cpuUtilization = (timeWithCpu+0.0)/(timeWithCpuAndIo);
        System.out.printf("\tCPU Utilization: %f\n",cpuUtilization);
        double ioUtilization = (timeWithIo+0.0)/(timeWithCpuAndIo);
        System.out.printf("\tI/O Utilization: %f\n", ioUtilization);
        System.out.printf("\tThroughput: %f\n", ((terminatedList.size()/(clock+0.0))*100.0));
        System.out.printf("\tAverage turnaround time: %f\n", totalTurnaroundTime/terminatedList.size());
        System.out.printf("\tAverage waiting time: %f\n\n", totalWaitTime/terminatedList.size());


    }

    public void roundRobinScheduling(Processes processes) {


        processes.sort();

        int clock = 0;

        int totalNumOfProcesses = processes.size();

        ArrayList<Process> terminatedList = new ArrayList<Process>();

        Queue<Process> readyQ = new LinkedList<Process>();

        Queue<Process> blockedQ = new LinkedList<Process>();

        int timeWithCpuAndIo = 0;
        int timeWithCpu = 0;
        int timeWithIo = 0;

        int index = 0;

        while(terminatedList.size() != totalNumOfProcesses){

//            System.out.println(clock);//Checking the clock

            for(int i=0; i<processes.size() && index<processes.size();i++){
                if(processes.getProcess(index).getA() <= clock){
                    readyQ.add(processes.getProcess(index));
                    index++;
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
                        timeWithCpu = timeWithCpu+cpuBurstTime;
                        timeWithCpuAndIo = timeWithCpuAndIo + cpuBurstTime+ioBurstTime;
                        timeWithIo = timeWithIo + ioBurstTime;
                        int finishTime = cpuBurstTime+ioBurstTime;
                        pr.setFinishingTime(currentFinishTime+finishTime+waitTime);

                        pr.setRemainingTime(pr.getRemainingTime()-cpuBurstTime);

                        pr.setBlockedTime(ioBurstTime);
                        pr.setReadyTime(pr.getBlockedTime() + clock + 1);

                        blockedQ.add(pr);

                        clock = clock+cpuBurstTime;

                    }
                    else{
                        pr.setFinishingTime(pr.getBurstTime() + pr.getFinishingTime()+pr.getA() + waitTime);
                        pr.setTurnaroundTime(pr.getFinishingTime() - pr.getA());
                        pr.setRemainingTime(pr.getRemainingTime() - cpuBurstTime);
                        terminatedList.add(pr);
                        clock = clock + cpuBurstTime;
                        timeWithCpu = timeWithCpu+cpuBurstTime;
                        timeWithCpuAndIo = timeWithCpuAndIo + cpuBurstTime;
                    }

                }

            }
            else{
                clock++;
            }

            if(!blockedQ.isEmpty()){
                Process tr = blockedQ.peek();
                if(tr.getReadyTime() <= clock){
                    tr = blockedQ.remove();
                    readyQ.add(tr);
                }
            }


        }
        System.out.println("The scheduling algorithm used was Round Robin.\n");
        double totalWaitTime = 0; //Incremented below and used to calculate average wait time.
        double totalTurnaroundTime = 0;
        for(int i=0; i<terminatedList.size(); i++){
            totalWaitTime  = totalWaitTime + terminatedList.get(i).getWaitingTime();
            totalTurnaroundTime  = totalTurnaroundTime + terminatedList.get(i).getTurnaroundTime();
            System.out.println(terminatedList.get(i)+"\n");
        }
        System.out.println("Summary Data: ");
        System.out.println("\tFinishing time: "+clock);
        double cpuUtilization = (timeWithCpu+0.0)/(timeWithCpuAndIo);
        System.out.printf("\tCPU Utilization: %f\n",cpuUtilization);
        double ioUtilization = (timeWithIo+0.0)/(timeWithCpuAndIo);
        System.out.printf("\tI/O Utilization: %f\n", ioUtilization);
        System.out.printf("\tThroughput: %f\n", ((terminatedList.size()/(clock+0.0))*100.0));
        System.out.printf("\tAverage turnaround time: %f\n", totalTurnaroundTime/terminatedList.size());
        System.out.printf("\tAverage waiting time: %f\n\n", totalWaitTime/terminatedList.size());
    }
    public void uniprogrammedScheduling(Processes processes){

        processes.sort();

        int clock = 0;




        for(int i=0; i<processes.size(); i++){
            Process pr = processes.getProcess(i);
            pr.setWaitingTime(clock - pr.getA());

            while(processes.getProcess(i).getRemainingTime() > 0){


                if(pr.getRemainingTime() > 1 && pr.getReadyTime() <= clock){
                    int ioBurstTime = pr.getBurstTime()*pr.getM();
                    int currentFinishTime = ioBurstTime + pr.getBurstTime();
                    pr.setFinishingTime(pr.getFinishingTime()+currentFinishTime);
                    pr.setIoTime(pr.getIoTime() + ioBurstTime);
                    pr.setRemainingTime(pr.getRemainingTime() - pr.getBurstTime());
                    pr.setReadyTime(clock + ioBurstTime + 1);
                    clock++;
                }
                else if(pr.getRemainingTime() <= 1 && pr.getReadyTime() <= clock){
                    int currentFinishTime = pr.getBurstTime() + pr.getWaitingTime();
                    pr.setFinishingTime(pr.getFinishingTime()+currentFinishTime+pr.getA());
                    pr.setTurnaroundTime(pr.getFinishingTime() - pr.getA());
                    pr.setRemainingTime(pr.getRemainingTime() - pr.getBurstTime());
                    pr.setReadyTime(clock + pr.getBurstTime());
                    clock++;
                }
                else{
                    clock++;
                }
            }
        }




        System.out.println("The scheduling algorithm used was Uniprocessing.\n");
        System.out.println(processes);


    }
    public void sjfScheduling(Processes processes){

        processes.sort();

        int clock = 0;

        int totalNumOfProcesses = processes.size();

        ArrayList<Process> terminatedList = new ArrayList<Process>();

        Queue<Process> readyQ = new LinkedList<Process>();


        Queue<Process> blockedQ = new LinkedList<Process>();

        int index = 0;

        while(terminatedList.size() != totalNumOfProcesses){

//            System.out.println(clock);//Checking the clock

            for(int i=0; i<processes.size() && index<processes.size();i++){
                if(processes.getProcess(index).getA() <= clock){
                    readyQ.add(processes.getProcess(index));
                    index++;
                }
            }

            if(!readyQ.isEmpty()){
                Process pr  = readyQ.peek();
                int minRT = pr.getRemainingTime();


                for(Process p : readyQ){
                    if(p.getRemainingTime() < minRT){
                        minRT = p.getRemainingTime();
                        pr = p;
                    }
                }



                if(pr.getReadyTime() <= clock){
                    readyQ.remove(pr);

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

                        blockedQ.add(pr);

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

            if(!blockedQ.isEmpty()){
                Process tr = blockedQ.peek();
                if(tr.getReadyTime() <= clock){
                    tr = blockedQ.remove();
                    readyQ.add(tr);
                }
            }



        }
        System.out.println("The scheduling algorithm used was shortest job first.\n");
        for(int i=0; i<terminatedList.size(); i++){
            System.out.println(terminatedList.get(i)+"\n");
        }

    }
    public void sortByRemainingTime(ArrayList<Process> processes){
        Comparator<Process> sortByRemainingTime = new Comparator<Process>() {
            @Override
            public int compare(Process o1, Process o2) {
                if(o1.getRemainingTime() < o2.getRemainingTime()){
                    return 1;
                }
                else if(o1.getRemainingTime() > o2.getRemainingTime()){
                    return -1;
                }
                else{
                    return 0;
                }
            }
        };
        Collections.sort(processes, sortByRemainingTime);
    }

}
