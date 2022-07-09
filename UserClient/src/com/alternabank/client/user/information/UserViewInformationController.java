package com.alternabank.client.user.information;

import com.alternabank.client.loan.LoanRequestFormController;
import com.alternabank.client.user.UserViewController;
import com.alternabank.client.user.information.notification.LoanStatusChangeNotificationsRefresher;
import com.alternabank.client.user.information.transaction.UserAccountTransactionViewController;
import com.alternabank.client.loan.LoanViewController;
import com.alternabank.client.util.http.HttpClientUtil;
import com.alternabank.client.util.time.ServerTimeUtil;
import com.alternabank.dto.customer.LoanStatusChangeNotificationsAndVersion;
import com.alternabank.dto.loan.InvestmentDetails;
import com.alternabank.dto.loan.status.LoanStatusData;
import com.alternabank.dto.time.ServerTime;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.stage.Stage;
import okhttp3.HttpUrl;
import okhttp3.RequestBody;
import okhttp3.Response;
import org.controlsfx.control.Notifications;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;
import java.util.Timer;
import java.util.TimerTask;

public class UserViewInformationController implements Initializable {

    private TimerTask loanStatusChangeNotificationsRefresher;

    private Timer timer;

    private UserViewController userViewComponentController;
    @FXML private UserAccountTransactionViewController userAccountTransactionViewComponentController;
    @FXML private LoanViewController loanerLoanViewComponentController;
    @FXML private LoanViewController borrowerLoanViewComponentController;

    @FXML private Button sellSelectedInvestmentButton;

    @FXML private Button buyInvestmentsButton;

    @FXML private Button postNewLoanButton;

    public UserViewController getUserViewController() {
        return userViewComponentController;
    }

    public void setUserViewController(UserViewController controller) {
        this.userViewComponentController = controller;
        loanerLoanViewComponentController.loanDetailsProperty().bind(userViewComponentController.loanerLoanDetailsProperty());
        borrowerLoanViewComponentController.loanDetailsProperty().bind(userViewComponentController.borrowerLoanDetailsProperty());
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        Platform.runLater(() -> this.userAccountTransactionViewComponentController.setUserViewInformationController(this));
        loanerLoanViewComponentController.selectedLoanDetailsProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue != null) {
                boolean investmentForSale = newValue.getInvestmentsByLenderName().get(getUserViewController().getAppController().getUsername()).stream().anyMatch(InvestmentDetails::getForSale);
                sellSelectedInvestmentButton.setDisable(investmentForSale || newValue.getStatus() != LoanStatusData.ACTIVE);
            }
            else sellSelectedInvestmentButton.setDisable(true);
        });
        sellSelectedInvestmentButton.disableProperty().bind(ServerTimeUtil.rewindMode);
        buyInvestmentsButton.disableProperty().bind(ServerTimeUtil.rewindMode);
        postNewLoanButton.disableProperty().bind(ServerTimeUtil.rewindMode);
    }

    public void startRefreshers() {
        startLoanStatusChangeNotificationsRefresher();
    }

    @FXML
    private void onNewLoanRequest(ActionEvent event) throws IOException {
        Stage stage = new Stage();
        FXMLLoader fxmlLoader = new FXMLLoader();
        URL url = getClass().getResource("../../loan/LoanRequestForm.fxml");
        fxmlLoader.setLocation(url);
        Parent loanRequestForm = fxmlLoader.load(url.openStream());
        LoanRequestFormController loanRequestFormController = fxmlLoader.getController();
        loanRequestFormController.setAppController(getUserViewController().getAppController());
        Scene scene = new Scene(loanRequestForm, 350, 350);
        stage.setResizable(false);
        stage.setScene(scene);
        stage.showAndWait();
    }

    @FXML
    private void onBuyInvestmentsRequest(ActionEvent event) throws IOException {
        Stage stage = new Stage();
        FXMLLoader fxmlLoader = new FXMLLoader();
        URL url = getClass().getResource("BuyInvestmentsForm.fxml");
        fxmlLoader.setLocation(url);
        Parent buyInvestmentsRequestForm = fxmlLoader.load(url.openStream());
        BuyInvestmentsFormController buyInvestmentsFormController = fxmlLoader.getController();
        buyInvestmentsFormController.setUserViewController(getUserViewController());
        Scene scene = new Scene(buyInvestmentsRequestForm, 1100, 700);
        stage.setScene(scene);
        stage.showAndWait();
    }

    private void showPostInvestmentForSaleErrorAlert() {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Error!");
        alert.setHeaderText("Could not post investment for sale!");
        alert.setContentText("We had trouble while attempting to post your investment for sale :(");
        alert.showAndWait();
    }

    @FXML
    private void onSellSelectedInvestmentRequest(ActionEvent event) throws  IOException {
        String finalUrl = HttpUrl.parse("http://localhost:8080/AlternaBank/sell-investment")
                .newBuilder().addQueryParameter("loan", loanerLoanViewComponentController.getSelectedLoanDetails().getId()).build().toString();
        Response response = HttpClientUtil.runPutSync(finalUrl, RequestBody.create(new byte[0], null));
        if (response.code() != 200) {
            showPostInvestmentForSaleErrorAlert();
        }
        else {
            sellSelectedInvestmentButton.setDisable(true);
        }
    }

    public void updateLoanStatusChangeNotifications(LoanStatusChangeNotificationsAndVersion loanStatusChangeNotificationsAndVersion) {
        loanStatusChangeNotificationsAndVersion.getLoanStatusChangeNotifications().forEach(notification -> Platform.runLater(() -> Notifications.create().text(notification.toString()).showInformation()));
    }

    private void startLoanStatusChangeNotificationsRefresher() {
        loanStatusChangeNotificationsRefresher = new LoanStatusChangeNotificationsRefresher(this::updateLoanStatusChangeNotifications);
        timer = new Timer();
        timer.schedule(loanStatusChangeNotificationsRefresher, 500, 500);
    }
}
