package ci583.receiver;

import java.util.ArrayList;
import java.util.List;

public class MLFQReceiver extends ModRegReceiver {
    private final List<ModuleRegister> youngList;
    private final List<ModuleRegister> oldList;

    /**
     * Constructs a multi-level feedback queue receiver.
     * The constructor calls the constructor of the superclass then initializes the two lists for young and old processes.
     *
     * @param quantum
     */
    public MLFQReceiver(long quantum) {
        super(quantum);
        youngList = new ArrayList<>();
        oldList = new ArrayList<>();
    }

    /**
     * Adds a new process to the list of young processes.
     *
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
     * - If the state is NEW, start the process then sleep for QUANTUM milliseconds
     * then put the process at the back of the list of OLD processes.
     * - If the state is TERMINATED, add it to the results list.
     * - If the state is anything else then interrupt the process to wake it up then
     * sleep for QUANTUM milliseconds, then put the process at the back of the queue.
     * - If the list of YOUNG processes is empty, do the same except take the process from the
     * list of OLD processes and, after it does its 'work' put it at the end of the list of
     * YOUNG processes.
     * When both lists are empty, return the list of completed processes.
     *
     * @return
     */
    @Override
    public List<ModuleRegister> startRegistration() {
        List<ModuleRegister> completedProcesses = new ArrayList<>();

        while (!youngList.isEmpty() || !oldList.isEmpty()) {
            List<ModuleRegister> activeList;
            if (!youngList.isEmpty()) {
                activeList = youngList;
            } else {
                activeList = oldList;
            }
            ModuleRegister process = activeList.remove(0);
            ModuleRegister.State state = process.getState();

            switch (state) {
                case NEW:
                    process.start();
                    if (activeList == youngList) {
                        oldList.add(process);
                    } else {
                        youngList.add(process);
                    }
                    sleepForQuantum();
                    break;

                case TERMINATED:
                    completedProcesses.add(process);
                    break;

                default:
                    process.interrupt();
                    // Add to the opposite list
                    if (activeList == youngList) {
                        oldList.add(process);
                    } else {
                        youngList.add(process);
                    }
                    sleepForQuantum();
                    break;
            }
        }

        return completedProcesses;
    }

    private void sleepForQuantum() {
        try {
            Thread.sleep(QUANTUM);
        } catch (InterruptedException e) {
            System.err.println(e);
        }
    }
}

