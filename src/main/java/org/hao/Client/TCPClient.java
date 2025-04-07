package org.hao.Client;

import org.hao.Client.Request.RequestHandler;
import java.net.*;
import java.io.*;

public class TCPClient {

    public static void main(String[] args) throws IOException {
        Socket s1 = new Socket("localhost", 1230);

        RequestHandler.handleRequest(s1);

        s1.close();
    }
}

