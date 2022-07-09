package com.alternabank.admin.client.account;

import com.alternabank.dto.transaction.TransactionRecord;
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

    private ListProperty<TransactionRecord> transactionRecords = new SimpleListProperty<>(FXCollections.observableArrayList());

    @FXML private ListView<TransactionRecord> accountLedgerListViewComponent;

    public ListProperty<TransactionRecord> transactionRecordsProperty() {
        return transactionRecords;
    }

    public List<TransactionRecord> getTransactionRecords() {
        return transactionRecords.get();
    }

    public void setTransactionRecords(List<TransactionRecord> transactionRecords) {
        this.transactionRecords.set(FXCollections.observableList(transactionRecords));
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        accountLedgerListViewComponent.itemsProperty().bind(transactionRecords);
        accountLedgerListViewComponent.setCellFactory(tc -> new ListCell<TransactionRecord>() {
            @Override
            protected void updateItem(TransactionRecord record, boolean empty) {
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
