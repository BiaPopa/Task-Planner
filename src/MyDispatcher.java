/* Implement this class. */

import java.util.List;

public class MyDispatcher extends Dispatcher {
    private int lastID = -1;

    public MyDispatcher(SchedulingAlgorithm algorithm, List<Host> hosts) {
        super(algorithm, hosts);
    }

    // Round Robin
    private void RR(Task task) {
        int nodeId = (lastID + 1) % hosts.size();
        
        hosts.get(nodeId).addTask(task);

        lastID++;

    }

    // Shortest Queue
    private void SQ(Task task) {
        int minQueue = Integer.MAX_VALUE;
        int nodeId = -1;

        for (int i = 0; i < hosts.size(); i++) {
            if (hosts.get(i).getQueueSize() < minQueue) {
                minQueue = hosts.get(i).getQueueSize();
                nodeId = i;
            }
        }

        hosts.get(nodeId).addTask(task);
    }

    // Size Interval Task Assignment
    private void SITA(Task task) {
        if (task.getType().equals(TaskType.SHORT)) {
            hosts.get(0).addTask(task);
        } else if (task.getType().equals(TaskType.MEDIUM)) {
            hosts.get(1).addTask(task);
        } else {
            hosts.get(2).addTask(task);
        }
    }

    // Least Work Left
    private void LWL(Task task) {
        long minWork = Long.MAX_VALUE;
        int nodeId = -1;

        for (int i = 0; i < hosts.size(); i++) {
            if (hosts.get(i).getWorkLeft() < minWork) {
                minWork = hosts.get(i).getWorkLeft();
                nodeId = i;
            }
        }
        
        hosts.get(nodeId).addTask(task);
    }

    @Override
    public void addTask(Task task) {
        if (algorithm.equals(SchedulingAlgorithm.ROUND_ROBIN)) {
            RR(task);
        } else if (algorithm.equals(SchedulingAlgorithm.SHORTEST_QUEUE)) {
            SQ(task);
        } else if (algorithm.equals(SchedulingAlgorithm.SIZE_INTERVAL_TASK_ASSIGNMENT)) {
            SITA(task);
        } else {
            LWL(task);
        }
    }
}
