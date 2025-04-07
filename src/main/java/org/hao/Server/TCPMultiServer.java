package org.hao.Server;

import java.net.*;
import java.io.*;
import org.hao.Server.Request.RequestHandler;

public class TCPMultiServer {
    public static void main(String[] args) throws IOException{
        // TODO Auto-generated method stub
        ServerSocket s = new ServerSocket(1230);
        while(true) {
            Socket s1 = s.accept();

            RequestHandler.handleRequest(s1);
        }
    }
}

