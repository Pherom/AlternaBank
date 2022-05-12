package com.alternabank.graphical.ui.application.admin.header;

import com.alternabank.engine.xml.event.XMLLoadSuccessEvent;
import com.alternabank.graphical.ui.application.admin.AdminViewController;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;

public class AdminViewHeaderController {

    @FXML private AdminViewController adminViewComponentController;
    @FXML private Button advanceTimeButton;

    @FXML
    public void onAdvanceTimeRequest(ActionEvent event) {
        adminViewComponentController.onAdvanceTimeRequest(event);
    }

    @FXML
    public void onLoadFileRequest(ActionEvent event) {
        adminViewComponentController.onLoadFileRequest(event);
    }

    public void loadedSuccessfully(XMLLoadSuccessEvent event) {
        advanceTimeButton.setDisable(false);
    }

    public void setAdminViewController(AdminViewController controller) {
        this.adminViewComponentController = controller;
    }

}
