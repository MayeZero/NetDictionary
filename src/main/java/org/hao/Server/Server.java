package org.hao.Server;

import org.hao.Server.Request.RequestHandler;
import org.hao.Server.Common.ThreadsWorkerPool;

import java.net.ServerSocket;
import java.net.Socket;

public class Server {
    private static ServerSocket s = null;
    private static final ThreadsWorkerPool threadsWorkerPool = new ThreadsWorkerPool();

    public static void start(String[] args){
        try {
            //Register server on input port
            Server.s = new ServerSocket(1230);

            //wait for connection
            do {
                Socket s1 = Server.s.accept();
                System.out.println("Receive connection from: " + s1.getRemoteSocketAddress().toString());
                threadsWorkerPool.execute(new RequestHandler(s1));
            } while (true);

        }
        catch (Exception e) {
            System.out.println("Input port is invalid or busy.");
            //e.printStackTrace();
            System.exit(-1);
        }
    }
}
