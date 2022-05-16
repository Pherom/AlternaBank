package com.alternabank.graphical.ui.application.user.information;

import com.alternabank.engine.customer.CustomerManager;
import com.alternabank.engine.user.User;
import com.alternabank.engine.user.UserManager;
import com.alternabank.graphical.ui.application.loan.LoanViewController;
import javafx.fxml.FXML;

import java.util.stream.Collectors;

public class UserViewInformationController {

    @FXML private LoanViewController loanerLoanViewComponentController;
    @FXML private LoanViewController borrowerLoanViewComponentController;

    public void populate(User selectedUser) {
        CustomerManager.Customer userAsCustomer = (CustomerManager.Customer) selectedUser;
        loanerLoanViewComponentController.populate(UserManager.getInstance().getAdmin().getLoanManager().getLoanDetails().stream().filter(loanDetails -> loanDetails.getInvestmentByLenderName().containsKey(selectedUser.getName())).collect(Collectors.toSet()));
        borrowerLoanViewComponentController.populate(UserManager.getInstance().getAdmin().getLoanManager().getLoanDetails().stream().filter(loanDetails -> loanDetails.getBorrowerName().equals(selectedUser.getName())).collect(Collectors.toSet()));
    }

}
