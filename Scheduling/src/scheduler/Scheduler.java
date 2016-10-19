package scheduler;
import java.io.*;
import java.util.*;
/**
 * Created by jeffersonvivanco on 10/3/16.
 * Class:
 */
public class Scheduler {

    public static void main(String[] args){

        File f  = null;
        boolean verboseFlag = false;
        if(args.length == 1){
            f = new File(args[0]);
        }
        else if(args.length == 2){
            if(args[0].equals("--verbose")){
                verboseFlag = true;
                f = new File(args[1]);
            }
            else{
                System.out.println("Error: Please make sure you entered a verbose flag");
                System.exit(0);
            }
        }
        else{
            System.out.println("Error: Too many arguments");
            System.exit(0);
        }

        Scheduler scheduler = new Scheduler();

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


        scheduler.fcfsScheduling(processes1,verboseFlag);
        scheduler.roundRobinScheduling(processes2, verboseFlag);
        scheduler.uniprogrammedScheduling(processes3, verboseFlag);
        scheduler.sjfScheduling(processes4, verboseFlag);



    }
    public void fcfsScheduling(Processes processes, boolean verboseFlag) {


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
            if(verboseFlag){
                String str = "Before cycle: "+ clock+"    ";
                for(int i=0; i<processes.size(); i++){
                    str = str + " "+processes.getProcess(i).getState() + " "+processes.getProcess(i).getBurstTime();
                }
                System.out.println(str);
            }
            for(int i=0; i<processes.size() && index<processes.size();i++){
                if(processes.getProcess(index).getA() <= clock){
                    readyQ.add(processes.getProcess(index));
                    processes.getProcess(index).setState("ready");
                    index++;
                }
            }

            if(!readyQ.isEmpty()){
                Process pr  = readyQ.peek();

                if(pr.getReadyTime() <= clock){
                    pr.setState("running");
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
                        pr.setState("blocked");
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
                    tr.setState("Ready");
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
        System.out.printf("\tThroughput: %f processes per hundred cycles.\n", ((terminatedList.size()/(clock+0.0))*100.0));
        System.out.printf("\tAverage turnaround time: %f\n", totalTurnaroundTime/terminatedList.size());
        System.out.printf("\tAverage waiting time: %f\n\n", totalWaitTime/terminatedList.size());


    }

    public void roundRobinScheduling(Processes processes, boolean verboseFlag) {


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
            if(verboseFlag){
                String str = "Before cycle: "+ clock+"    ";
                for(int i=0; i<processes.size(); i++){
                    str = str + " "+processes.getProcess(i).getState() + " "+processes.getProcess(i).getBurstTime();
                }
                System.out.println(str);
            }
            for(int i=0; i<processes.size() && index<processes.size();i++){
                if(processes.getProcess(index).getA() <= clock){
                    readyQ.add(processes.getProcess(index));
                    processes.getProcess(i).setState("ready");
                    index++;
                }
            }

            if(!readyQ.isEmpty()){
                Process pr  = readyQ.peek();

                if(pr.getReadyTime() <= clock){
                    pr = readyQ.remove();
                    pr.setState("running");
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
                    tr.setState("blocked");
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
        System.out.printf("\tThroughput: %f processes per hundred cycles.\n", ((terminatedList.size()/(clock+0.0))*100.0));
        System.out.printf("\tAverage turnaround time: %f\n", totalTurnaroundTime/terminatedList.size());
        System.out.printf("\tAverage waiting time: %f\n\n", totalWaitTime/terminatedList.size());
    }
    public void uniprogrammedScheduling(Processes processes, boolean verboseFlag){

        processes.sort();

        int clock = 0;
        int timeWithCpuAndIo = 0;
        int timeWithCpu = 0;
        int timeWithIo = 0;

        for(int i=0; i<processes.size(); i++){

            Process pr = processes.getProcess(i);
            pr.setState("ready");
            pr.setWaitingTime(clock - pr.getA());

            while(processes.getProcess(i).getRemainingTime() > 0){
                if(verboseFlag){
                    String str = "Before cycle: "+ clock+"    ";
                    for(int x=0; x<processes.size(); x++){
                        str = str + " "+processes.getProcess(x).getState() + " "+processes.getProcess(x).getBurstTime();
                    }
                    System.out.println(str);
                }

                if(pr.getRemainingTime() > 1 && pr.getReadyTime() <= clock){
                    pr.setState("running");
                    int ioBurstTime = pr.getBurstTime()*pr.getM();
                    timeWithIo = timeWithIo + ioBurstTime;
                    timeWithCpu = timeWithCpu + pr.getBurstTime();
                    timeWithCpuAndIo = timeWithCpuAndIo + ioBurstTime+pr.getBurstTime();
                    int currentFinishTime = ioBurstTime + pr.getBurstTime();
                    pr.setFinishingTime(pr.getFinishingTime()+currentFinishTime);
                    pr.setIoTime(pr.getIoTime() + ioBurstTime);
                    pr.setRemainingTime(pr.getRemainingTime() - pr.getBurstTime());
                    pr.setReadyTime(clock + ioBurstTime + 1);
                    clock++;
                }
                else if(pr.getRemainingTime() <= 1 && pr.getReadyTime() <= clock){
                    pr.setState("blocked");
                    timeWithCpu = timeWithCpu + pr.getBurstTime();
                    timeWithCpuAndIo  = timeWithCpuAndIo + pr.getBurstTime();
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
        double totalWaitTime = 0; //Incremented below and used to calculate average wait time.
        double totalTurnaroundTime = 0;
        System.out.println("The scheduling algorithm used was Uniprocessing.\n");
        for(int i=0; i<processes.size(); i++){
            totalWaitTime  = totalWaitTime + processes.getProcess(i).getWaitingTime();
            totalTurnaroundTime  = totalTurnaroundTime + processes.getProcess(i).getTurnaroundTime();
            System.out.println(processes.getProcess(i)+"\n");
        }

        System.out.println("Summary Data: ");
        System.out.println("\tFinishing time: "+clock);
        double cpuUtilization = (timeWithCpu+0.0)/(timeWithCpuAndIo);
        System.out.printf("\tCPU Utilization: %f\n",cpuUtilization);
        double ioUtilization = (timeWithIo+0.0)/(timeWithCpuAndIo);
        System.out.printf("\tI/O Utilization: %f\n", ioUtilization);
        System.out.printf("\tThroughput: %f processes per hundred cycles.\n", ((processes.size()/(clock+0.0))*100.0));
        System.out.printf("\tAverage turnaround time: %f\n", totalTurnaroundTime/processes.size());
        System.out.printf("\tAverage waiting time: %f\n\n", totalWaitTime/processes.size());

    }
    public void sjfScheduling(Processes processes, boolean verboseFlag){

        processes.sort();

        int clock = 0;

        int totalNumOfProcesses = processes.size();

        ArrayList<Process> terminatedList = new ArrayList<Process>();

        Queue<Process> readyQ = new LinkedList<Process>();


        Queue<Process> blockedQ = new LinkedList<Process>();

        int index = 0;



        int timeWithCpuAndIo = 0;
        int timeWithCpu = 0;
        int timeWithIo = 0;

        while(terminatedList.size() != totalNumOfProcesses){

//            System.out.println(clock);//Checking the clock
            if(verboseFlag){
                String str = "Before cycle: "+ clock+"    ";
                for(int i=0; i<processes.size(); i++){
                    str = str + " "+processes.getProcess(i).getState() + " "+processes.getProcess(i).getBurstTime();
                }
                System.out.println(str);
            }
            for(int i=0; i<processes.size() && index<processes.size();i++){
                if(processes.getProcess(index).getA() <= clock){
                    readyQ.add(processes.getProcess(index));
                    processes.getProcess(i).setState("ready");
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
                        pr.setState("running");
                        timeWithCpu = timeWithCpu+ pr.getBurstTime();
                        timeWithCpuAndIo = timeWithCpuAndIo + ioBurstTime + pr.getBurstTime();
                        timeWithIo = timeWithIo + ioBurstTime;

                        int currentFinishTime = pr.getFinishingTime();
                        int finishTime = cpuBurstTime+ioBurstTime;
                        pr.setFinishingTime(currentFinishTime+finishTime+waitTime);

                        pr.setRemainingTime(pr.getRemainingTime()-cpuBurstTime);

                        pr.setBlockedTime(ioBurstTime);
                        pr.setReadyTime(pr.getBlockedTime() + clock + 1);

                        blockedQ.add(pr);
                        pr.setState("blocked");
                        clock = clock+cpuBurstTime;

                    }
                    else{
                        timeWithCpu = timeWithCpu + pr.getBurstTime();
                        timeWithCpuAndIo  = timeWithCpuAndIo + pr.getBurstTime();
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
                    tr.setState("ready");
                    readyQ.add(tr);
                }
            }



        }
        System.out.println("The scheduling algorithm used was shortest job first.\n");
        double totalWaitTime = 0; //Incremented below and used to calculate average wait time.
        double totalTurnaroundTime = 0;
        for(int i=0; i<terminatedList.size(); i++){
            totalWaitTime  = totalWaitTime + processes.getProcess(i).getWaitingTime();
            totalTurnaroundTime  = totalTurnaroundTime + processes.getProcess(i).getTurnaroundTime();
            System.out.println(terminatedList.get(i)+"\n");
        }


        System.out.println("Summary Data: ");
        System.out.println("\tFinishing time: "+clock);
        double cpuUtilization = (timeWithCpu+0.0)/(timeWithCpuAndIo);
        System.out.printf("\tCPU Utilization: %f\n",cpuUtilization);
        double ioUtilization = (timeWithIo+0.0)/(timeWithCpuAndIo);
        System.out.printf("\tI/O Utilization: %f\n", ioUtilization);
        System.out.printf("\tThroughput: %f processes per hundred cycles.\n", ((processes.size()/(clock+0.0))*100.0));
        System.out.printf("\tAverage turnaround time: %f\n", totalTurnaroundTime/processes.size());
        System.out.printf("\tAverage waiting time: %f\n\n", totalWaitTime/processes.size());

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
