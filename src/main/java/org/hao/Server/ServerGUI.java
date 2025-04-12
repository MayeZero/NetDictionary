package org.hao.Server;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import org.hao.Server.Data.Dictionary;
import org.hao.Server.Data.DictionaryFile;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Initialize DictionaryFile and launch ServerGUI, start Server to receive request.
 * @author Ninghao Zhu 1446180
 */
public class ServerGUI extends Application {

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) throws IOException {
        List<String> args = getParameters().getRaw();

        if (args.size() != 2) {
            System.out.println("Usage: java -jar TCPMultiServer.jar <port> <dictionary-file>");
            return;
        }

        int port;
        String dictionaryFile;

        try {
            port = Integer.parseInt(args.get(0));
        } catch (NumberFormatException e) {
            System.out.println("Invalid port, using default 1230.");
            port = 1230;
        }

        dictionaryFile = args.get(1);
        DictionaryFile.initialize(dictionaryFile);
        DictionaryFile dictionaryfile = DictionaryFile.getInstance();
        ConcurrentHashMap<String, List<String>> dictionary = dictionaryfile.ReadFile();
        Dictionary.initialize(dictionary);

        FXMLLoader loader = new FXMLLoader(getClass().getResource("/MultiController.fxml"));
        AnchorPane root = loader.load();
        MultiController controller = loader.getController();
        Server.setController(controller);

        Server.start(port);

        Scene scene = new Scene(root);
        primaryStage.setTitle("NetDictionary Server");
        primaryStage.setScene(scene);
        primaryStage.show();
    }
}
