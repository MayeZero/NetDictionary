package org.hao.Server;

import java.net.*;
import java.io.*;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

import org.hao.Server.Data.Dictionary;
import org.hao.Server.Data.DictionaryFile;
import org.hao.Server.Request.RequestHandler;

public class TCPMultiServer {
    public static void main(String[] args) throws IOException{

        //initialize DictionaryFile and ReadFile to dictionary
        DictionaryFile.initialize("dictionary.json");
        DictionaryFile save = DictionaryFile.getInstance();
        ConcurrentHashMap<String, List<String>> dictionary = save.ReadFile();
        Dictionary.initialize(dictionary);

//        // TODO Auto-generated method stub
//        ServerSocket s = new ServerSocket(1230);
//        while(true) {
//            Socket s1 = s.accept();
//
//            RequestHandler.handleRequest(s1);
//        }
        Server.start(args);
    }
}

