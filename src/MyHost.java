/* Implement this class. */

import java.util.PriorityQueue;

public class MyHost extends Host {
    // elementele sunt sortate dupa prioritate si apoi dupa timpul de start
    private final PriorityQueue<Task> tasks = new PriorityQueue<>(20, (task1, task2) -> {
        if (task2.getPriority() < task1.getPriority()) {
            return -1;
        } else if (task2.getPriority() > task1.getPriority()) {
            return 1;
        } else {
            return Integer.compare(task1.getStart(), task2.getStart());
        }
    });

    // inchiderea hostului
    private boolean isShutdown = false;
    // taskul care ruleaza
    private Task task;
    // timpul de start al taskului care ruleaza
    private double start_time = 0.0;
    // exista un task care ruleaza
    private boolean isRunning = false;

    @Override
    public void run() {
        while (true) {
            synchronized (this) {
                // asteapta pana cand apare un task in coada goala sau hostul este inchis
                while (tasks.isEmpty() && !isShutdown) {
                    try {
                        wait();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }

            // hostul este inchis
            if (isShutdown) {
                return;
            }
            
            // se extrage taskul cu cea mai mare prioritate din coada
            task = tasks.poll();
            // se calculeaza timpul lui de start
            start_time = Timer.getTimeDouble();
            // exista un task care ruleaza
            isRunning = true;

            try {
                // se asteapta timpul necesar executiei taskului
                sleep(task.getLeft());
                task.setLeft(0);
            } catch (InterruptedException e) {
                // preemptare
                // se calculeaza cat timp mai are de executat taskul curent
                double time = Timer.getTimeDouble();
                task.setLeft(task.getLeft() - (long) ((time - start_time) * 1000));
                
                // se readauga in coada
                tasks.add(task);
                isRunning = false;
            }

            // taskul si-a terminat executia
            if (task.getLeft() <= 0) {
                task.finish();
                isRunning = false;
            }
        }
    }

    @Override
    public void addTask(Task task) {
        synchronized (this) {
            // preemptare
            // se verifica daca taskul primit are prioritate mai mare decat taskul curent si preemptibil
            if (isRunning == true && this.task.isPreemptible() && task.getPriority() > this.task.getPriority()) {
                this.interrupt();
            }

            // se adauga noul task in coada
            tasks.add(task);
            notifyAll();
        }
    }

    @Override
    public int getQueueSize() {
        if (isRunning) {
            // se adauga si taskul care ruleaza
            return tasks.size() + 1;
        } else {
            return tasks.size();
        }
    }

    @Override
    public long getWorkLeft() {
        synchronized (this) {
            long workLeft = 0;

            // se adauga timpul ramas din executia taskului curent
            if (isRunning) {
                double time = Timer.getTimeDouble();
                workLeft += task.getLeft() - (long) ((time - start_time) * 1000);
            }

            // si restul timpilor
            for (Task task : tasks) {
                workLeft += task.getLeft();
            }

            return workLeft;
        }
    }

    @Override
    public void shutdown() {
        synchronized (this) {
            // hostul se inchide
            isShutdown = true;
            notifyAll();
        }
    }
}
