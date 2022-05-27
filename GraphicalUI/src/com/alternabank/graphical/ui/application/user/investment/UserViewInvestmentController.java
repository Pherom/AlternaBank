package com.alternabank.graphical.ui.application.user.investment;

import com.alternabank.graphical.ui.application.loan.LoanViewController;
import com.alternabank.graphical.ui.application.user.UserViewController;
import com.alternabank.graphical.ui.application.user.util.DoubleTextFormatter;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import org.controlsfx.control.CheckListView;
import org.controlsfx.validation.ValidationSupport;
import org.controlsfx.validation.Validator;
import org.controlsfx.validation.decoration.StyleClassValidationDecoration;
import org.controlsfx.validation.decoration.ValidationDecoration;

import java.net.URL;
import java.util.Optional;
import java.util.ResourceBundle;
import java.util.stream.IntStream;

public class UserViewInvestmentController implements Initializable {

    private UserViewController userViewComponentController;
    @FXML private CheckListView<String> loanCategoryCheckListView;

    @FXML private TextField loanInvestmentTotalTextField;

    @FXML private TextField loanMinimumInterestTextField;

    private ValidationSupport validationSupport = new ValidationSupport();

    public void setUserViewController(UserViewController controller) {
        this.userViewComponentController = controller;
        validationSupport.registerValidator(loanInvestmentTotalTextField, Validator.createPredicateValidator((String text) -> {
            double textAsDouble = Double.parseDouble(text);
            return textAsDouble > 0 && textAsDouble <= userViewComponentController.getAccountBalance();
        }, "Total investment must be higher than 0 and lower than account balance"));
        validationSupport.initInitialDecoration();
        Platform.runLater(() -> loanCategoryCheckListView.itemsProperty().bind(userViewComponentController.getAppController().availableLoanCategoriesProperty()));
    }

    public void prepareForNewInvestmentRequest() {
        IntStream.range(0, loanCategoryCheckListView.getItems().size()).forEach(index -> loanCategoryCheckListView.getCheckModel().check(index));
        loanInvestmentTotalTextField.clear();
        loanMinimumInterestTextField.clear();
    }

    public Optional<ButtonType> showQuitConfirmationDialog() {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.getButtonTypes().setAll(ButtonType.YES, ButtonType.NO);
        alert.setContentText("Are you sure you want to cancel this investment request?");
        alert.setHeaderText("Cancel investment request?");
        alert.setTitle("Investment Request Cancellation");
        return alert.showAndWait();
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        loanInvestmentTotalTextField.setTextFormatter(new DoubleTextFormatter(null));
        loanMinimumInterestTextField.setTextFormatter(new DoubleTextFormatter(null));
        validationSupport.registerValidator(loanInvestmentTotalTextField, true, Validator.createEmptyValidator("Total investment is required"));
        validationSupport.registerValidator(loanMinimumInterestTextField, false, Validator.createPredicateValidator((String text) -> text.isEmpty() || Double.parseDouble(text) > 0, "Minimum interest rate must be higher than 0"));
    }
}
