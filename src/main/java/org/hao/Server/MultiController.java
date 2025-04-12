package org.hao.Server;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.TextArea;

/**
 * Controller for ServerGUI
 * @author Ninghao Zhu 1446180
 */
public class MultiController {
    @FXML
    private TextArea connectionText;
    @FXML
    private TextArea workerText;

    // present Connect Message
    public void addConnectionMessage(String message) {
        Platform.runLater(() -> {
            connectionText.appendText(message + "\n");
            connectionText.setScrollTop(Double.MAX_VALUE);
        });
    }

    // show operation Message
    public void addWorkerMessage(String message) {
        Platform.runLater(() -> {
            workerText.appendText(message + "\n");
            workerText.setScrollTop(Double.MAX_VALUE);
        });
    }

    public void handleClientDisconnection(String clientAddress) {
        addConnectionMessage(clientAddress);
    }
}

