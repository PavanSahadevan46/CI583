package ci583.receiver;
/**
 * The Priority Receiver. This receiver takes the next process from the head of a priority queue
 * (an instance of java.util.PriorityQueue, allows it to run then puts it back into the queue (unless
 * the state of process is TERMINATED). Thus, the Priority Receiver is identical to the Round Robin
 * receiver apart from the fact that processes have a priority (HIGH, MED or LOW) and are held in a
 * priority queue.
 *
 * @author Jim Burton
 */
import java.util.ArrayList;
import java.util.List;

import java.util.PriorityQueue;
import java.util.Comparator;


public class PReceiver extends ModRegReceiver {

    // priority queue to hold processes
    private PriorityQueue<ModuleRegister> queue;

    /**
     * Constructs a new Priority Scheduler. The constructor needs to call the constructor of the
     * superclass then initialise the priority queue. To initialise the priority queue, first define
     * a Comparator that compares two processes. The comparator should return -1 if two processes have
     * the same priority. This is so that when a process is added to the queue it ends up behind any
     * other processes with the same priority. If the two priorities are not equal, the Comparator should
     * return -1 if p1 is less than p2, and 1 if p1 is greater than p2.
     *
     * @param quantum
     */
    public PReceiver(long quantum) {
        super(quantum);

        // comparator to sort module register objects by priority
        // if two processes have the same priority, return -1 to keep them in the existing order
        // otherwise, return -1 or 1 based on priority comparison

        Comparator<ModuleRegister> comparePrio = (p1, p2) -> {
            if (p1.getPriority() == p2.getPriority()) {
                return -1; // same priority
            } else {
//                System.out.println("prio" + Integer.compare(p1.getPriority(), p2.getPriority()));
                return  Integer.compare(p1.getPriority(), p2.getPriority());

            }
        };

        // create new priority queue with comparator
        this.queue = new PriorityQueue<>(comparePrio);
    }

    /**
     * Add a ModuleRegister process to the queue, to be scheduled for registration
     */
    @Override
    public void enqueue(ModuleRegister m) {
        queue.offer(m);
    }



    /**
     * Schedule the processes. This method needs to:
     * + create an empty list which will hold the completed processes. This will be the
     * return value of the method.
     * + while the queue is not empty:
     * - use the priority queue's `poll' method to take the next process from the queue and get its State.
     * - if the state is NEW, start the process then sleep for QUANTUM milliseconds
     * then put the process at the back of the queue.
     * - if the state is TERMINATED, add it to the results list.
     * - if the state is anything else then interrupt the process to wake it up then
     * sleep for QUANTUM milliseconds, then put the process at the back of the queue.
     * + when the queue is empty, return the list of completed processes.
     *
     * @return
     */



    /**
     * schedule the processes for registration based on their priority
     * method returns a list of completed processes
     *
     * @return list of completed processes
     */
    @Override
    public List<ModuleRegister> startRegistration() {

        // create a  list to hold completed processes
        ArrayList<ModuleRegister> results = new ArrayList<>();

        // while there are processes in the queue
        while (!queue.isEmpty()) {
            // poll the next process and get its state
            ModuleRegister process = queue.poll();
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