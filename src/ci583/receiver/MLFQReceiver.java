package ci583.receiver;

import java.util.ArrayList;
import java.util.List;

public class MLFQReceiver extends ModRegReceiver {
    private final List<ModuleRegister> youngList; // young processes list
    private final List<ModuleRegister> oldList; // old processes list

    /**
     * Constructs a multi-level feedback queue receiver.
     * The constructor calls the constructor of the superclass then initializes the two lists for young and old processes.
     *
     * @param quantum
     */
    public MLFQReceiver(long quantum) {
        super(quantum);
        youngList = new ArrayList<>();  // initialize the young list
        oldList = new ArrayList<>();    // initialize the old list
    }

    /**
     * Adds a new process to the list of young processes.
     *
     * @param m
     */
    @Override
    public void enqueue(ModuleRegister m) {
        youngList.add(m); //add modules to the young queue first
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





    /**
     * schedule the module registration processes based on multi-level feedback queue logic
     * returns a list of completed processes
     * the method alternates between two queues:
     *  Young queue: if a process is new  it starts and moves to the old queue after the quantum expires
     *  Old queue: if the young queue is empty old queue processes move back to the young queue after the quantum
     *
     * @return  list of completed processes
     */
    @Override
    public List<ModuleRegister> startRegistration() {
        //list to store the completed processes
        List<ModuleRegister> completedProcesses = new ArrayList<>();

        // while both queues aren't empty perform module registration process
        while (!youngList.isEmpty() || !oldList.isEmpty()) {

                // activeList is used as a "flag" to allow for clear transitions of processes between queues to prevent starvation
                List<ModuleRegister> activeList;

                // using the activeList as a flag determine which list to use depending on which list is empty
                if (!youngList.isEmpty()) {
                    activeList = youngList;
                } else {
                    activeList = oldList;
                }

                // remove the first process in the selected active queue
                ModuleRegister process = activeList.remove(0);

                // take the process and get its state
                ModuleRegister.State state = process.getState();

                // perform action based on the process's state
                switch (state) {
                    case NEW:
                        process.start();
                        // move the process to the opposite queue after the quantum time expires
                        // if it was in the young queue move it to the old queue
                        // otherwise  if it was in the old queue move back to the young queue
                        // this is done to ensure the process does not stay in one list too long
                        if (activeList == youngList) {
                            oldList.add(process); // move from young to old
                        } else {
                            youngList.add(process);// move from old to young
                        }

                        pauseForQuantum();
                        break;

                    case TERMINATED:
                        // add the process to the completed list if terminated
                        completedProcesses.add(process);
                        break;

                    default:
                        // if the state of the process is anything else but above 2 cases interrupt it
                        process.interrupt();
                        // add to the opposite list same as the new case
                        if (activeList == youngList) {
                            oldList.add(process);
                        } else {
                            youngList.add(process);
                        }
                        pauseForQuantum();
                        break;
                }
        }
        return completedProcesses;
    }


}
