package com.alternabank.webapp.servlets.loan;

import com.alternabank.dto.loan.request.LoanRequest;
import com.alternabank.engine.customer.CustomerManager;
import com.alternabank.engine.loan.LoanManager;
import com.alternabank.webapp.util.WebAppUtils;
import com.google.gson.Gson;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;

@WebServlet(name = "PostLoanServlet", urlPatterns = "/post-loan")
public class PostLoanServlet extends HttpServlet {

    private void processRequest(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String username = WebAppUtils.getUsername(req);
        boolean success;
        if (username != null) {
            LoanRequest loanRequest = WebAppUtils.GSON_INSTANCE.fromJson(req.getReader(), LoanRequest.class);
            LoanManager loanManager = WebAppUtils.getLoanManager(getServletContext());
            synchronized (getServletContext()) {
                if (!loanManager.getAvailableCategories().contains(loanRequest.getCategory()))
                    loanManager.addCategory(loanRequest.getCategory());
            }
            CustomerManager customerManager = WebAppUtils.getCustomerManager(getServletContext());
            synchronized (getServletContext()) {
                success = customerManager.getCustomersByName().get(username).postLoanRequest(loanRequest);
                if (success)
                    resp.setStatus(HttpServletResponse.SC_OK);
                else {
                    resp.setStatus(HttpServletResponse.SC_CONFLICT);
                }
            }
        }
        else resp.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        processRequest(req, resp);
    }
}
