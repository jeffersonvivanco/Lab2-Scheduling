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
        File f = new File("/Users/jeffersonvivanco/IdeaProjects/Lab2-Scheduling/FCFSInputs/input-4.txt");
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
    public void fcfsScheduling(Processes processes){
        processes.sort();
        boolean isDone = false;

        int time = 0;
        int numTerminated = 0;
        int finishingTime = 0;
        while (!isDone){

            for(int i=0; i<processes.size() && !isDone && processes.getProcess(i).getA()<=time; i++){


                if(processes.getProcess(i).isTerminated()){
                    continue;
                }
                if(processes.getProcess(i).getRemainingTime()>=0 && processes.getProcess(i).getReadystate() && !processes.getProcess(i).isTerminated()){

                    int arrivalTime = processes.getProcess(i).getA();
                    int cpuTime = processes.getProcess(i).getC();
                    int cpuBurstTime = processes.getProcess(i).getBurstTime();
                    int ioBurstTime = processes.getProcess(i).getM()*cpuBurstTime;
                    if((time - processes.getProcess(i).getCurrentTime()) >= 1){

                        processes.getProcess(i).setWaitingTime((time - processes.getProcess(i).getCurrentTime())+processes.getProcess(i).getWaitingTime());
                        processes.getProcess(i).setCurrentTime(time);

                    }

                    if(processes.getProcess(i).getRemainingTime() > 0 ){
                        finishingTime = cpuBurstTime + ioBurstTime;
                        processes.getProcess(i).setFinishingTime(processes.getProcess(i).getFinishingTime()+finishingTime);

                        processes.getProcess(i).setIoTime(processes.getProcess(i).getIoTime()+ioBurstTime);
                        processes.getProcess(i).setReadystate(false);
                        processes.getProcess(i).setReadyTime(time + ioBurstTime);

                        if(cpuBurstTime > processes.getProcess(i).getRemainingTime()){
                            processes.getProcess(i).setRemainingTime(cpuBurstTime);
                        }
                        else
                            processes.getProcess(i).setRemainingTime(processes.getProcess(i).getRemainingTime()-cpuBurstTime);




                    }
                    else if(processes.getProcess(i).getRemainingTime() <= 0 ){

                        finishingTime = cpuBurstTime;

                        processes.getProcess(i).setFinishingTime(finishingTime + processes.getProcess(i).getWaitingTime()+
                                processes.getProcess(i).getFinishingTime()+processes.getProcess(i).getA());
                        int turnaroundTime = processes.getProcess(i).getFinishingTime() - processes.getProcess(i).getA();
                        processes.getProcess(i).setTurnaroundTime(turnaroundTime);
                        processes.getProcess(i).setTerminated(true);

                        numTerminated++;

                    }
                    else{
                        //do nothing
                    }

                }
                time = time + 1;

                if(processes.getProcess(i).getRemainingTime() >=0 && !processes.getProcess(i).getReadystate() && !processes.getProcess(i).isTerminated()){
                    if(time == processes.getProcess(i).getReadyTime()){

                        processes.getProcess(i).setReadystate(true);

                        processes.getProcess(i).setCurrentTime(time+1);

                    }
                    else{
//                        processes.getProcess(i).setReadyTime(processes.getProcess(i).getReadyTime()-1);
                    }


                }
                if(numTerminated == processes.size()){
                    isDone = true;
                }

            }


        }

    }


}
