package com.alternabank.graphical.ui.application.account;

import com.alternabank.engine.transaction.Transaction;
import javafx.beans.property.ListProperty;
import javafx.beans.property.SimpleListProperty;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;

import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;

public class AccountLedgerListViewController implements Initializable {

    private ListProperty<Transaction.Record> transactionRecords = new SimpleListProperty<>();

    @FXML private ListView<Transaction.Record> accountLedgerListViewComponent;

    public ListProperty<Transaction.Record> transactionRecordsProperty() {
        return transactionRecords;
    }

    public List<Transaction.Record> getTransactionRecords() {
        return transactionRecords.get();
    }

    public void setTransactionRecords(List<Transaction.Record> transactionRecords) {
        this.transactionRecords.set(FXCollections.observableList(transactionRecords));
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        accountLedgerListViewComponent.itemsProperty().bind(transactionRecords);
        accountLedgerListViewComponent.setCellFactory(tc -> new ListCell<Transaction.Record>() {
            @Override
            protected void updateItem(Transaction.Record record, boolean empty) {
                super.updateItem(record, empty);
                this.getStyleClass().add("record-cell");
                this.getStyleClass().remove("successful");
                this.getStyleClass().remove("failed");
                if(!empty) {
                    setText(record.toString());
                    switch (record.getStatus()) {
                        case SUCCESSFUL:
                            this.getStyleClass().add("successful");
                            break;
                        case FAILED:
                            this.getStyleClass().add("failed");
                            break;
                    }
                }
                else {
                    setText(null);
                }
            }
        });
    }
}
