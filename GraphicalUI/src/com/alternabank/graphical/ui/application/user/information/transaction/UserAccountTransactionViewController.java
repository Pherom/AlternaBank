package com.alternabank.graphical.ui.application.user.information.transaction;

import com.alternabank.engine.transaction.UnilateralTransaction;
import com.alternabank.engine.user.UserManager;
import com.alternabank.graphical.ui.application.account.AccountLedgerListViewController;
import com.alternabank.graphical.ui.application.user.information.UserViewInformationController;
import com.alternabank.graphical.ui.application.util.DoubleTextFormatter;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.util.StringConverter;

import java.util.Optional;

public class UserAccountTransactionViewController {

    @FXML private Label accountBalanceLabel;

    @FXML private Button depositButton;

    @FXML private Button withdrawalButton;

    @FXML private AccountLedgerListViewController accountLedgerListViewComponentController;

    private UserViewInformationController userViewInformationComponentController;

    public void setUserViewInformationController(UserViewInformationController controller) {
        this.userViewInformationComponentController = controller;
        Platform.runLater(() -> {
            accountLedgerListViewComponentController.transactionRecordsProperty().bind(userViewInformationComponentController.getUserViewController().accountLedgerProperty());
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
}
