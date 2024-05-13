package ci583.receiver;
/**
 * The Round Robin Module Registration Receiver. This receiver takes the next process from the head of a list,
 * allows it to run then puts it back at the end of the list (unless the state of process is
 * TERMINATED).
 *
 * @author Jim Burton
 */

import org.w3c.dom.ls.LSOutput;

import java.util.ArrayList;
import java.util.List;

public class RRReceiver extends ModRegReceiver {

    // a list to hold the order of processes
    private List<ModuleRegister> queue;

    /**
     * Create a new RRReceiver with the given quantum. The constructor needs to call the constructor
     * of the superclass, then initialise the list of processes.
     * @param quantum amount of time to run RRReceiver
     */
    public RRReceiver(long quantum) {
        super(quantum);
        // initalize the queue
        queue = new ArrayList<>();
    }


    /**
     * Add a ModuleRegister process to the queue, to be scheduled for registration
     */
    @Override
    public void enqueue(ModuleRegister m) {
        queue.add(m);
    }


    /**
     * Schedule the processes, start registration. This method needs to:
     * + create an empty list which will hold the completed processes. This will be the
     *   return value of the method.
     * + while the queue is not empty:
     *   - take the next process from the queue and get its State.
     *   - if the state is NEW, start the process then sleep for QUANTUM milliseconds
     *     then put the process at the back of the queue.
     *   - if the state is TERMINATED, add it to the results list.
     *   - if the state is anything else then interrupt the process to wake it up then
     *     sleep for QUANTUM milliseconds, then put the process at the back of the queue.
     *  + when the queue is empty, return the list of completed processes.
     * @return
     */


    /**
     * schedules the processes based on the round-robin algorithm
     * this method:
     * - creates an empty list for completed processes
     * - while the queue is not empty:
     *   - retrieves the next process from the head of the queue
     *   - based on the process state:
     *     - if new: starts the process, sleeps for quantum then adds it to the end of the queue
     *     - if terminated: adds the process to the results list
     *     - otherwise: interrupts the process, sleeps for quantum then adds it to the end of the queue
     * - returns the list of completed processes when the queue is empty
     *
     * @return a list of completed ModuleRegister processes
     */


    @Override
    public List<ModuleRegister> startRegistration() {

        // create list to hold completed processes
        ArrayList<ModuleRegister> results = new ArrayList<>();

        // while the queue has processes
        while (!queue.isEmpty()) {
            // remove a process from the queue and get its state
            ModuleRegister process = queue.remove(0);
            ModuleRegister.State state = process.getState();

            // switch between the state of the process
            switch (state) {
                case NEW:
                    // start process, sleep for quantum and add it to the back of the queue
                    process.start();
                    pauseForQuantum();
                    queue.add(process);
                    break;
                case TERMINATED:
                    // add it to the completed queue
                    results.add(process);
                    break;
                default:
                    // if process is anything but new or terminated interrupt the process
                    // sleep for quantum and add it to the back of the queue
                    process.interrupt();
                    pauseForQuantum();
                    queue.add(process);
                    break;
            }
        }

        return results;
    }

}

