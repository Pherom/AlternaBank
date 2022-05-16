package com.alternabank.graphical.ui.application.customer;

import com.alternabank.engine.customer.dto.CustomerDetails;
import javafx.fxml.FXML;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;

public class CustomerViewController {

    @FXML
    private TableColumn<CustomerDetails, String> customerNameTableColumn;

    @FXML
    private TableView<CustomerDetails> customerViewComponent;

}
