package scheduler;

/**
 * Created by jeffersonvivanco on 10/3/16.
 */
public class Process implements Comparable<Process> {

    private String processNumber;
    private int A; //Arrival time of the process
    private int B; //
    private int C; //Total cpu time needed
    private int M;
    private int burstTime = 0;
    private int finishingTime = 0;
    private int turnaroundTime = 0;
    private int ioTime  = 0;
    private int waitingTime = 0;
    private int processId =0;
    private boolean blockstate = false;
    private boolean readystate = true;
    private int blockedTime;
    private int readyTime;
    private boolean terminated = false;
    private String state = "unstarted";


    private int remainingTime = 0;


    public Process(String processNumber){

        this.processNumber = processNumber;
        String [] processNumberArray = this.processNumber.split(" ");
        this.A = Integer.parseInt(processNumberArray[0]);
        this.B = Integer.parseInt(processNumberArray[1]);
        this.C = Integer.parseInt(processNumberArray[2]);
        this.M = Integer.parseInt(processNumberArray[3]);

    }

    public String getProcessNumber(){
        return this.processNumber;
    }
    public int getA(){
        return this.A;
    }
    public int getB(){
        return this.B;
    }
    public int getC(){
        return this.C;
    }
    public int getM(){
        return this.M;
    }
    public int getBurstTime(){
        return this.burstTime;
    }
    public int getProcessId(){
        return this.processId;
    }
    public boolean getReadystate(){
        return this.readystate;
    }
    public boolean getBlockedstate(){
        return this.blockstate;
    }
    public int getWaitingTime(){
        return this.waitingTime;
    }
    public int getBlockedTime(){
        return this.blockedTime;
    }
    public int getReadyTime(){
        return this.readyTime;
    }
    public boolean isTerminated(){
        return this.terminated;
    }
    public int getRemainingTime(){
        return this.remainingTime;
    }
    public int getFinishingTime() {
        return this.finishingTime;
    }
    public int getIoTime(){
        return this.ioTime;
    }
    public int getTurnaroundTime(){return this.turnaroundTime;}
    public String getState(){
        return this.state;
    }


    public void setFinishingTime(int f){
        this.finishingTime = f;
    }
    public void setTurnaroundTime(int t){
        this.turnaroundTime = t;
    }
    public void setIoTime(int i){
        this.ioTime = i;
    }
    public void setWaitingTime(int w){
        this.waitingTime = w;
    }
    public void setBurstTime(int b){
        this.burstTime = b;
    }
    public void setProcessId(int id){
        this.processId  = id;
    }
    public void setBlockstate(boolean b){
        this.blockstate = b;
    }
    public void setReadystate(boolean b){
        this.readystate = b;
    }
    public void setBlockedTime(int bt){
        this.blockedTime = bt;
    }
    public void setReadyTime(int rt){
        this.readyTime  = rt;
    }
    public void setTerminated(boolean t){
        this.terminated  = t;
    }
    public void setRemainingTime(int rt){
        this.remainingTime = rt;
    }
    public void setState(String s){this.state = s; }



    public String getProcNumString(){
        return "("+this.getA()+" "+this.getB()+" "+this.getC()+" "+this.getM()+")";
    }
    public int compareTo(Process o){
        if(this.A < o.getA()){
            return -1;
        }
        else if(this.A > o.getA()){
            return 1;
        }
        else{
            return 0;
        }
    }
    @Override
    public String toString(){
        return "Process: "+this.processId +":\n"
                +processNumber+"\nFinishing time: "+this.finishingTime
                +"\nTurnaround time: "+this.turnaroundTime
                +"\nI/O time: "+this.ioTime
                +"\nWaiting time: "+this.waitingTime;
    }
}
