package org.hao.Server;

import org.hao.Server.Request.RequestHandler;
import org.hao.Server.Common.ThreadsWorkerPool;
import java.net.ServerSocket;
import java.net.Socket;

/**
 * Open Socket and Port to accept multiple client, use threadsWorkerPool to receive client's request,
 * also use controller to update connect message.
 * @author Ninghao Zhu 1446180
 */
public class Server {
    private static ServerSocket s = null;
    private static final ThreadsWorkerPool threadsWorkerPool = new ThreadsWorkerPool();
    private static MultiController controller;
    private static int clientCount = 0;

    public static void start(int args) {
        new Thread(() -> {
            try {
                s = new ServerSocket(args);
                System.out.println("Server started on port " + s.getLocalPort());
                if (controller != null) {
                    controller.addConnectionMessage("Server started on port " + s.getLocalPort());
                }

                while (true) {
                    Socket s1 = s.accept();
                    int clientId = ++clientCount;
                    String clientAddr = s1.getRemoteSocketAddress().toString();

                    String connectMsg = "Client #" + clientId + " connected: " + clientAddr;
                    System.out.println(connectMsg);
                    if (controller != null) {
                        controller.addConnectionMessage(connectMsg);
                    }

                    threadsWorkerPool.execute(new RequestHandler(s1, controller, clientId));
                }
            } catch (Exception e) {
                e.printStackTrace();
                if (controller != null) {
                    controller.addConnectionMessage("Server start failed: " + e.getMessage());
                }
                System.exit(-1);
            }
        }).start();
    }

    public static void setController(MultiController controller) {
        Server.controller = controller;
    }
}
