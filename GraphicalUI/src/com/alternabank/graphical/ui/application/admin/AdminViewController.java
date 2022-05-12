package com.alternabank.graphical.ui.application.admin;

import com.alternabank.engine.xml.event.XMLLoadSuccessEvent;
import com.alternabank.engine.xml.event.listener.XMLLoadSuccessListener;
import com.alternabank.graphical.ui.application.admin.header.AdminViewHeaderController;
import com.alternabank.graphical.ui.application.AppController;
import javafx.fxml.FXML;

import javafx.event.ActionEvent;
import javafx.fxml.Initializable;

import java.net.URL;
import java.util.ResourceBundle;

public class AdminViewController implements Initializable {

    @FXML private AppController appController;
    @FXML private AdminViewHeaderController adminViewHeaderComponentController;

    public void setAppController(AppController controller) {
        this.appController = controller;
    }

    public void setAdminViewHeaderController(AdminViewHeaderController controller) {
        this.adminViewHeaderComponentController = controller;
        controller.setAdminViewController(this);
    }

    public void onAdvanceTimeRequest(ActionEvent event) {
        appController.onAdvanceTimeRequest(event);
    }

    public void onLoadFileRequest(ActionEvent event) {
        appController.onLoadFileRequest(event);
    }

    public void loadedSuccessfully(XMLLoadSuccessEvent event) {
        adminViewHeaderComponentController.loadedSuccessfully(event);
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        setAdminViewHeaderController(adminViewHeaderComponentController);
    }
}
