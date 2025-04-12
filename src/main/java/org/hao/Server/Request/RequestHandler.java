package org.hao.Server.Request;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.hao.Server.Data.Dictionary;
import org.hao.Server.Data.DictionaryFile;
import org.hao.Server.MultiController;

import java.io.*;
import java.net.Socket;
import java.util.List;

/**
 * RequestHandler for request from Client to Server
 * @author Ninghao Zhu 1446180
 */
public class RequestHandler implements Runnable {
    private Socket socket = null;
    private final MultiController controller;
    private final int clientId;

    public RequestHandler(Socket socket, MultiController controller, int clientId) {
        this.socket = socket;
        this.controller = controller;
        this.clientId = clientId;
    }

    @Override
    public void run() {
        BufferedReader in = null;
        BufferedWriter out = null;

        try {
            in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            out = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));

            ObjectMapper mapper = new ObjectMapper();
            String jsonRequest;

            while ((jsonRequest = in.readLine()) != null) {
                Message message = mapper.readValue(jsonRequest, Message.class);
                RequestType requestType = message.getType();
                System.out.println("Received: " + message);

                switch (requestType) {
                    case query:
                        Output(query(message), out);
                        break;
                    case add:
                        Output(add(message), out);
                        break;
                    case remove:
                        Output(remove(message), out);
                        break;
                    case addmeanings:
                        Output(addmeanings(message), out);
                        break;
                    case update:
                        Output(update(message), out);
                        break;
                }
            }

        } catch (java.net.SocketException se) {
            System.out.println("Client disconnected unexpectedly: " + se.getMessage());
        } catch (IOException e) {
            System.err.println("IO error in RequestHandler: " + e.getMessage());
            e.printStackTrace();
        } finally {
            try {
                String disconnectionMsg = "Client #" + clientId + " disconnected: " + socket.getRemoteSocketAddress();
                System.out.println(disconnectionMsg);
                if (controller != null) {
                    controller.handleClientDisconnection(disconnectionMsg);
                }
                if (in != null) in.close();
                if (out != null) out.close();
                if (socket != null && !socket.isClosed()) socket.close();
                System.out.println("Connection closed with client: " + socket.getRemoteSocketAddress());
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private Response query(Message message) throws IOException {
        String query = message.getWord();
        System.out.println("Received query: " + query);

        controller.addWorkerMessage("Client #" + clientId + " queried word: \"" + query + "\"");

        Dictionary dictionary = Dictionary.getDictionary();
        List<String> meaningForWord = dictionary.getMeaningsForWord(query);
        String csv = String.join(",", meaningForWord);
        return new Response(true, csv);
    }

    private Response add(Message message) throws IOException {
        Dictionary dictionary = Dictionary.getDictionary();
        DictionaryFile df = DictionaryFile.getInstance();
        String word = message.getWord();
        List<String> meanings = message.getMeanings();

        controller.addWorkerMessage("Client #" + clientId + " added word: \"" + word + "\" with meanings: " + meanings);
        System.out.println("Received add: " + word + " " + meanings);

        if (word == null || word.isEmpty() || meanings == null || meanings.isEmpty()) {
            return new Response(false, "Please provide a word and its meanings.");
        }

        Response addResult = dictionary.addWordAndMeanings(word, meanings);
        if (addResult.isSuccess()) {
            df.SaveFile(dictionary.getMap());
        }

        return addResult;
    }

    private Response remove(Message message) {
        String removeword = message.getWord();
        System.out.println("Received remove: " + removeword);
        controller.addWorkerMessage("Client #" + clientId + " removed word: \"" + removeword + "\"");
        Dictionary dictionary = Dictionary.getDictionary();
        DictionaryFile df = DictionaryFile.getInstance();

        Response result = dictionary.removeWordAndMeanings(removeword);
        if (result.isSuccess()) {
            df.SaveFile(dictionary.getMap());
        }

        return result;
    }

    private Response addmeanings(Message message) throws IOException {
        Dictionary dictionary = Dictionary.getDictionary();
        DictionaryFile df = DictionaryFile.getInstance();
        String word = message.getWord();
        List<String> meanings = message.getMeanings();
        controller.addWorkerMessage("Client #" + clientId + " added meanings to word: \"" + word + "\", meanings: " + meanings);

        if (meanings == null || meanings.isEmpty()) {
            return new Response(false, "Please enter meanings");
        }

        Response result = dictionary.addAdditionalMeanings(word, meanings);
        if (result.isSuccess()) {
            df.SaveFile(dictionary.getMap());
        }

        return result;
    }

    private Response update(Message message) throws IOException {
        Dictionary dictionary = Dictionary.getDictionary();
        DictionaryFile df = DictionaryFile.getInstance();

        String word = message.getWord();
        String existMeaning = message.getExistMeaning();
        String newMeaning = message.getNewMeaning();
        controller.addWorkerMessage("Client #" + clientId + " updated word: \"" + word + "\", from: \"" + existMeaning + "\" to: \"" + newMeaning + "\"");

        if (word == null || word.isEmpty() || existMeaning == null || existMeaning.isEmpty() || newMeaning == null || newMeaning.isEmpty()) {
            return new Response(false, "Please provide a word and both existing and new meanings.");
        }

        Response result = dictionary.updateMeaning(word, existMeaning, newMeaning);
        if (result.isSuccess()) {
            df.SaveFile(dictionary.getMap());
        }

        return result;
    }

    private void Output(Response response, BufferedWriter out) throws IOException {
        ObjectMapper mapper = new ObjectMapper();
        String json = mapper.writeValueAsString(response);
        out.write(json);
        out.newLine();
        out.flush();
        System.out.println("Response sent to client: " + json);
    }
}
