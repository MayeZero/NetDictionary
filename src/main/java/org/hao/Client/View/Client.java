package org.hao.Client.View;

import java.io.*;
import java.net.Socket;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;

public class Client extends Thread {
    private final static BlockingQueue<Runnable> queue = new LinkedBlockingQueue<Runnable>();
    private final static ConcurrentHashMap<Runnable,String> responseMap = new ConcurrentHashMap<>();
    private static Socket s1 = null;
    public static Socket sharedSocket;

    public static void enqueueRequest(Runnable request) {
        if (request== null) {
            throw new IllegalArgumentException("request submitted to pool cannot be null!");
        }
        try {
            Client.queue.clear();
            Client.queue.put(request);
//            System.out.println(request.getClass().getSimpleName() + " submitted!");
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public static Socket getSocket() {
        return sharedSocket;
    }

    public static String getResponse(Runnable request){
        return Client.responseMap.get(request);
    }

    public static void setResponse(Runnable request, String response){
        Client.responseMap.put(request, response);
    }

    @Override
    public void run() {
        try {
            s1 = new Socket("localhost", 1230);  // Connect to the server
            Client.sharedSocket = s1;
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        try {
            // Process tasks in the queue
            while (true) {
                System.out.println("Polling for task...");
                Runnable task = queue.poll(100, TimeUnit.SECONDS);
                if (task != null) {
                    System.out.println("Task received: " + task.getClass().getSimpleName());
                    task.run();
                    System.out.println(task.getClass().getSimpleName() + " run!");
                } else {
                    System.out.println("No task received, waiting...");
                }
            }
        } catch (InterruptedException e) {
            System.out.println("Thread interrupted.");
            throw new RuntimeException(e);
        }

    }
//    @Override
//    public void run() {
//        try {
//            s1 = new Socket("localhost", 1230);
//            Client.sharedSocket = s1;
//        } catch (IOException e) {
//            throw new RuntimeException(e);
//        }
//
//        InputStream inputStream = null;
//        OutputStream outputStream = null;
//        try {
//            inputStream = s1.getInputStream();
//            outputStream = s1.getOutputStream();
//        } catch (IOException e) {
//            System.out.println("Fail to open IO stream!");
//            return;
//        }
//
//        while (true) {
//            try {
//                while(!isInterrupted()) {
//                    Runnable task = queue.poll(100, TimeUnit.SECONDS);
//                    System.out.println(task);
//                    if (task != null) {
//                        task.run();
//                        System.out.println(task.getClass().getSimpleName() + " run!");
//                    }
//                }
//            } catch (InterruptedException e) {
//                throw new RuntimeException(e);
//            }
//        }
//    }
}
