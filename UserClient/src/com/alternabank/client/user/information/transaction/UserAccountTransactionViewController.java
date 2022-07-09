package com.alternabank.client.user.information.transaction;

import com.alternabank.client.user.information.UserViewInformationController;
import com.alternabank.client.util.DoubleTextFormatter;
import com.alternabank.client.account.AccountLedgerListViewController;
import com.alternabank.client.util.http.HttpClientUtil;
import com.alternabank.client.util.time.ServerTimeUtil;
import com.alternabank.dto.account.AccountDetails;
import com.alternabank.dto.time.ServerTime;
import com.alternabank.dto.transaction.TransactionRecord;
import com.alternabank.dto.transaction.type.UnilateralTransactionTypeData;
import javafx.beans.binding.ListBinding;
import javafx.beans.binding.StringBinding;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.control.TextInputDialog;
import okhttp3.HttpUrl;
import okhttp3.RequestBody;

import java.io.IOException;
import java.net.URL;
import java.util.*;
import java.util.stream.Collectors;

public class UserAccountTransactionViewController implements Initializable {

    @FXML private Label accountBalanceLabel;

    @FXML private Button depositButton;

    @FXML private Button withdrawalButton;

    @FXML private AccountLedgerListViewController accountLedgerListViewComponentController;

    private UserViewInformationController userViewInformationComponentController;

    public void setUserViewInformationController(UserViewInformationController controller) {
        this.userViewInformationComponentController = controller;

        userViewInformationComponentController.getUserViewController().accountDetailsProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue != null) {
                if (!ServerTimeUtil.isRewindMode()) {
                    accountLedgerListViewComponentController.getTransactionRecords().addAll(0, newValue.getTransactionRecords());
                } else {
                    List<TransactionRecord> transactionRecords = new ArrayList<>(newValue.getTransactionRecords());
                    Collections.reverse(transactionRecords);
                    accountLedgerListViewComponentController.setTransactionRecords(transactionRecords);
                }
            }
            else accountLedgerListViewComponentController.getTransactionRecords().clear();
        });

        /*accountLedgerListViewComponentController.transactionRecordsProperty().bind(new ListBinding<TransactionRecord>() {

            { super.bind(accountLedgerListViewComponentController.transactionRecordsProperty(), userViewInformationComponentController.getUserViewController().accountDetailsProperty()); }

            @Override
            protected ObservableList<TransactionRecord> computeValue() {
                AccountDetails accountDetails = userViewInformationComponentController.getUserViewController().accountDetailsProperty().get();
                return accountDetails != null ? FXCollections.observableList(accountDetails.getTransactionRecords()) : null;
            }
        });*/

        accountBalanceLabel.textProperty().bind(new StringBinding() {

            { super.bind(accountBalanceLabel.textProperty(), userViewInformationComponentController.getUserViewController().accountDetailsProperty());}

            @Override
            protected String computeValue() {
                return String.format("%.2f", userViewInformationComponentController.getUserViewController().getAccountBalance());
            }
        });
    }

    private Optional<Double> showUnilateralTransactionDialog(UnilateralTransactionTypeData type) {
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
        showUnilateralTransactionDialog(UnilateralTransactionTypeData.DEPOSIT).ifPresent(total ->
        {
            String finalUrl = HttpUrl.parse("http://localhost:8080/AlternaBank/deposit")
                            .newBuilder().addQueryParameter("total", total.toString()).build().toString();
            try {
                HttpClientUtil.runPutSync(finalUrl, RequestBody.create(new byte[0], null));
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        });
    }

    @FXML
    public void onWithdrawalRequest(ActionEvent event) {
        showUnilateralTransactionDialog(UnilateralTransactionTypeData.WITHDRAWAL).ifPresent(total -> {
            String finalUrl = HttpUrl.parse("http://localhost:8080/AlternaBank/withdraw")
                    .newBuilder().addQueryParameter("total", total.toString()).build().toString();
            try {
                HttpClientUtil.runPutSync(finalUrl, RequestBody.create(new byte[0], null));
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        });
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        depositButton.disableProperty().bind(ServerTimeUtil.rewindMode);
        withdrawalButton.disableProperty().bind(ServerTimeUtil.rewindMode);
    }
}
