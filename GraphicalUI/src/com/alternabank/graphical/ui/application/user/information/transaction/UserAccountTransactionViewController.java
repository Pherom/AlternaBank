package com.alternabank.graphical.ui.application.user.information.transaction;

import com.alternabank.engine.transaction.Transaction;
import com.alternabank.engine.transaction.UnilateralTransaction;
import com.alternabank.engine.user.UserManager;
import com.alternabank.graphical.ui.application.user.information.UserViewInformationController;
import com.alternabank.graphical.ui.application.user.util.DoubleTextFormatter;
import javafx.application.Platform;
import javafx.beans.binding.StringBinding;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.util.StringConverter;
import javafx.util.converter.DoubleStringConverter;

import java.net.URL;
import java.util.Optional;
import java.util.ResourceBundle;
import java.util.function.UnaryOperator;
import java.util.regex.Pattern;

public class UserAccountTransactionViewController implements Initializable {

    @FXML private Label accountBalanceLabel;

    @FXML private Button depositButton;

    @FXML private Button withdrawalButton;

    @FXML private ListView<Transaction.Record> accountLedgerListView;

    private UserViewInformationController userViewInformationComponentController;

    public void setUserViewInformationController(UserViewInformationController controller) {
        this.userViewInformationComponentController = controller;
        Platform.runLater(() -> {
            accountLedgerListView.itemsProperty().bind(userViewInformationComponentController.getUserViewController().accountLedgerProperty());
            accountBalanceLabel.textProperty().bindBidirectional(userViewInformationComponentController.getUserViewController().accountBalanceProperty(), new StringConverter<Number>() {
                @Override
                public String toString(Number balance) {
                    return balance == null ? "#" : String.format("%.2f", balance.doubleValue());
                }

                @Override
                public Double fromString(String string) {
                    return string.equals("#") ? null : Double.valueOf(string);
                }
            });
        });
    }

    private Optional<Double> showUnilateralTransactionDialog(UnilateralTransaction.Type type) {
        Optional<Double> total = Optional.empty();
        TextInputDialog depositAmountInputDialog = new TextInputDialog();
        depositAmountInputDialog.setHeaderText(type.toString());
        depositAmountInputDialog.setContentText("Enter total funds:");
        TextField editor = depositAmountInputDialog.getEditor();
        editor.setTextFormatter(new DoubleTextFormatter(0.0));
        if(depositAmountInputDialog.showAndWait().isPresent()) {
            total = Optional.of(Double.parseDouble(editor.getText()));
        }
        
        return total;
    }

    @FXML
    public void onDepositRequest(ActionEvent event) {
        showUnilateralTransactionDialog(UnilateralTransaction.Type.DEPOSIT).ifPresent(total -> UserManager.getInstance().getAdmin().getCustomerManager().depositFunds(UserManager.getInstance().getCurrentUser().getName(), total));
    }

    @FXML
    public void onWithdrawalRequest(ActionEvent event) {
        showUnilateralTransactionDialog(UnilateralTransaction.Type.WITHDRAWAL).ifPresent(total -> UserManager.getInstance().getAdmin().getCustomerManager().withdrawFunds(UserManager.getInstance().getCurrentUser().getName(), total));
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        accountLedgerListView.setCellFactory(tc -> new ListCell<Transaction.Record>() {
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
