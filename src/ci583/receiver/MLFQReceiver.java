package ci583.receiver;

import java.util.ArrayList;
import java.util.List;

public class MLFQReceiver extends ModRegReceiver {
    private List<ModuleRegister> youngList;
    private List<ModuleRegister> oldList;

    /**
     * Constructs a multi-level feedback queue receiver.
     * The constructor calls the constructor of the superclass then initializes the two lists for young and old processes.
     * @param quantum
     */
    public MLFQReceiver(long quantum) {
        super(quantum);
        youngList = new ArrayList<>();
        oldList = new ArrayList<>();
    }

    /**
     * Adds a new process to the list of young processes.
     * @param m
     */
    @Override
    public void enqueue(ModuleRegister m) {
        youngList.add(m);
    }

    /**
     * Schedule the module registration processes.
     * This method creates an empty list which will hold the completed processes.
     * This will be the return value of the method.
     * While one of the queues is not empty:
     * - If the list of YOUNG processes is not empty, take the next process and get its State.
     *   - If the state is NEW, start the process then sleep for QUANTUM milliseconds
     *     then put the process at the back of the list of OLD processes.
     *   - If the state is TERMINATED, add it to the results list.
     *   - If the state is anything else then interrupt the process to wake it up then
     *     sleep for QUANTUM milliseconds, then put the process at the back of the queue.
     * - If the list of YOUNG processes is empty, do the same except take the process from the
     *   list of OLD processes and, after it does its 'work' put it at the end of the list of
     *   YOUNG processes.
     * When both lists are empty, return the list of completed processes.
     * @return
     */
    @Override
    public List<ModuleRegister> startRegistration() {
        {

            List<ModuleRegister> completedProcesses = new ArrayList<>();

            while (!youngList.isEmpty() || !oldList.isEmpty()) {
                ModuleRegister process;

                if(!youngList.isEmpty()){
                    process = youngList.remove(0);
                    System.out.println("Processing young: " + process.getName());

                }else{
                    process = oldList.remove(0);
                    System.out.println("Processing old: " + process.getName());
                }

                ModuleRegister.State state = process.getState();

                switch (state) {
                    case NEW:
                        process.start();
                        sleepForQuantum();
                        oldList.add(process);
                        System.out.println("Moved to old: " + process.getName());
                        break;
                    case TERMINATED:
                        completedProcesses.add(process);
                        System.out.println("Completed: " + process.getName());
                        break;
                    default:
                        process.interrupt();
                        sleepForQuantum();
                        if (!youngList.isEmpty()) {
                            oldList.add(process);
                            System.out.println("Interrupted and moved to old: " + process.getName());
                        } else {
                            youngList.add(process);
                            System.out.println("Interrupted and moved to young: " + process.getName());
                        }
                        break;
                }

            }
            return completedProcesses;
        }
    }
    private void sleepForQuantum() {
        try {
            Thread.sleep(QUANTUM);
        } catch (InterruptedException e) {
            System.err.println("Error: Interrupted during sleep");
        }
    }
}
