package com.alternabank.webapp.servlets.account;

import com.alternabank.dto.account.AccountDetails;
import com.alternabank.engine.account.Account;
import com.alternabank.engine.customer.CustomerManager;
import com.alternabank.engine.loan.Loan;
import com.alternabank.engine.loan.LoanManager;
import com.alternabank.engine.time.TimeManager;
import com.alternabank.webapp.util.WebAppUtils;
import com.google.gson.Gson;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.util.Optional;

@WebServlet(name = "AccountServlet", urlPatterns = "/account")
public class AccountServlet extends HttpServlet {
    private void processAdminRequestForLoanAccountDetails(HttpServletRequest req, HttpServletResponse resp, String loanIDFromParameter, Optional<Integer> ledgerVersion) throws ServletException, IOException {
        LoanManager loanManager = WebAppUtils.getLoanManager(getServletContext());
        TimeManager timeManager = WebAppUtils.getTimeManager(getServletContext());
        AccountDetails accountDetails = null;
        synchronized (getServletContext()) {
            if (!timeManager.isRewindMode()) {
                Account account = loanManager.getLoansByID().get(loanIDFromParameter).getAccount();
                if (ledgerVersion.isPresent())
                    accountDetails = account.toDTO(ledgerVersion.get());
                else accountDetails = account.toDTO();
            }
            else {
                if (!ledgerVersion.isPresent())
                    accountDetails = loanManager.getLoanDetails().get(loanIDFromParameter).getAccountDetails();
            }
        }
        if (accountDetails != null) {
            String accountDetailsJSON = WebAppUtils.GSON_INSTANCE.toJson(accountDetails);
            resp.getWriter().print(accountDetailsJSON);
            resp.setStatus(HttpServletResponse.SC_OK);
        }
        else resp.setStatus(HttpServletResponse.SC_CONFLICT);
    }

    private void processUserRequestForLoanAccountDetails(HttpServletRequest req, HttpServletResponse resp, String username, String loanIDFromParameter, Optional<Integer> ledgerVersion) throws ServletException, IOException {
        CustomerManager customerManager = WebAppUtils.getCustomerManager(getServletContext());
        LoanManager loanManager = WebAppUtils.getLoanManager(getServletContext());
        TimeManager timeManager = WebAppUtils.getTimeManager(getServletContext());
        AccountDetails accountDetails = null;
        resp.setStatus(HttpServletResponse.SC_OK);

        synchronized (getServletContext()) {
            CustomerManager.Customer customer = customerManager.getCustomersByName().get(username);
            Loan loan = customer.getPostedLoan(loanIDFromParameter);
            if (loan != null) {
                if (!timeManager.isRewindMode()) {
                    Account account = loan.getAccount();
                    if (ledgerVersion.isPresent())
                        accountDetails = account.toDTO(ledgerVersion.get());
                    else accountDetails = account.toDTO();
                }
                else {
                    if (!ledgerVersion.isPresent())
                        accountDetails = loanManager.getLoanDetails(timeManager.getCurrentTime()).get(loanIDFromParameter).getAccountDetails();
                }
            }
            else resp.setStatus(HttpServletResponse.SC_CONFLICT);
        }

        if (accountDetails != null) {
            String accountDetailsJSON = WebAppUtils.GSON_INSTANCE.toJson(accountDetails);
            resp.getWriter().print(accountDetailsJSON);
        }

    }

    protected void processRequestForLoanAccountDetails(HttpServletRequest req, HttpServletResponse resp, String loanIDFromParameter, Optional<Integer> ledgerVersion) throws ServletException, IOException {
        if (WebAppUtils.isAdmin(req)) {
            processAdminRequestForLoanAccountDetails(req, resp, loanIDFromParameter, ledgerVersion);
        }
        else {
            String username = WebAppUtils.getUsername(req);
            if (username != null) {
                processUserRequestForLoanAccountDetails(req, resp, username, loanIDFromParameter, ledgerVersion);
            }
            else {
                resp.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            }
        }
    }
    protected void processRequestForCustomerAccountDetails(HttpServletRequest req, HttpServletResponse resp, Optional<Integer> ledgerVersion) throws  ServletException, IOException {
        String username = WebAppUtils.getUsername(req);
        if (username != null) {
            CustomerManager customerManager = WebAppUtils.getCustomerManager(getServletContext());
            TimeManager timeManager = WebAppUtils.getTimeManager((getServletContext()));
            AccountDetails accountDetails = null;
            synchronized (getServletContext()) {
                if (!timeManager.isRewindMode()) {
                    Account account = customerManager.getCustomersByName().get(username).getAccount();
                    if (ledgerVersion.isPresent())
                        accountDetails = account.toDTO(ledgerVersion.get());
                    else accountDetails = account.toDTO();
                }
                else {
                    if (!ledgerVersion.isPresent())
                        accountDetails = customerManager.getCustomerDetails(timeManager.getCurrentTime()).get(username).getAccountDetails();
                }
            }
            if (accountDetails != null) {
                String accountDetailsJSON = WebAppUtils.GSON_INSTANCE.toJson(accountDetails);
                resp.getWriter().print(accountDetailsJSON);
                resp.setStatus(HttpServletResponse.SC_OK);
            }
            else resp.setStatus(HttpServletResponse.SC_CONFLICT);
        }
        else {
            resp.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        }
    }
    protected void processRequest(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String loanIDFromParameter = req.getParameter("loan-id");
        Optional<Integer> ledgerVersion = Optional.empty();
        try {
            String ledgerVerParam = req.getParameter("ledger-ver");
            if (ledgerVerParam != null && !ledgerVerParam.isEmpty())
                ledgerVersion = Optional.of(Integer.parseInt(req.getParameter("ledger-ver")));
            if (loanIDFromParameter == null || loanIDFromParameter.isEmpty()) {
                if (WebAppUtils.isAdmin(req)) {
                    resp.setStatus(HttpServletResponse.SC_CONFLICT);
                } else processRequestForCustomerAccountDetails(req, resp, ledgerVersion);
            } else {
                processRequestForLoanAccountDetails(req, resp, loanIDFromParameter, ledgerVersion);
            }
        } catch (NumberFormatException numberFormatException) {
            resp.setStatus(HttpServletResponse.SC_CONFLICT);
        }
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        processRequest(req, resp);
    }
}
