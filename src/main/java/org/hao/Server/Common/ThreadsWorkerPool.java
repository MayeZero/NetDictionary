package org.hao.Server.Common;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;


// Threads Worker Pool, use Worker Factory to Create Worker, use Worker to execute Request.
public class ThreadsWorkerPool {
    BlockingQueue<Runnable> queue = new LinkedBlockingQueue<Runnable>();
    Integer nums = 64;
    WorkerFactory workerFactory = new WorkerFactory(nums);

    public ThreadsWorkerPool() {
        workerFactory.start();
    }

    public void execute(Runnable task){
        if (task == null) {
            throw new IllegalArgumentException("Task submitted to pool cannot be null!");
        }
        try {
            queue.put(task);
//            System.out.println(task.getClass().getSimpleName() + " submitted!");
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public void shutdown(){
        workerFactory.shutdown();
        workerFactory.interrupt();
    }

    class WorkerFactory extends Thread {
        private final Worker[] workers;
        public WorkerFactory(Integer nums) {
            workers = new Worker[nums];
            for (int i = 0; i < nums; i++) {
                workers[i] = new Worker(i);
                workers[i].setWorkerId(i);
            }
        }

        public void run() {
            for (int i = 0; i < nums; i++) {
                workers[i].start();
//                System.out.println(workers[i].getClass().getSimpleName() + workers[i].getWorkerId() + " started!");
            }
        }

        public Worker getWorker() {
            String threadName = Thread.currentThread().getName();
            return workers[Integer.parseInt(threadName)];
        }

        public void shutdown() {
            for (Worker w : workers) {
                w.interrupt();
            }
        }
    }

    class Worker extends Thread {
        private Integer id;

        public Worker(Integer id) {
            setName(String.valueOf(id));
            setWorkerId(id);
        }

        public void run() {
            try {
                while(!isInterrupted()) {
                    Runnable task = queue.poll(100, TimeUnit.SECONDS);
                    if (task != null) {
                        task.run();
                        System.out.println("Worker " + id + task.getClass().getSimpleName() + " run!");
                    }
                }
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }

        public Integer getWorkerId() {
            return id;
        }

        public void setWorkerId(Integer id) {
            this.id = id;
        }
    }
}
