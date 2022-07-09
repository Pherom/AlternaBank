package com.alternabank.webapp.servlets.customer;

import com.alternabank.dto.customer.CustomerDetails;
import com.alternabank.dto.loan.LoanDetails;
import com.alternabank.engine.customer.CustomerManager;
import com.alternabank.engine.loan.LoanManager;
import com.alternabank.engine.time.TimeManager;
import com.alternabank.webapp.util.WebAppUtils;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.util.List;
import java.util.Map;

@WebServlet(name = "CustomersServlet", urlPatterns = "/customers")
public class CustomersServlet extends HttpServlet {
    protected void processRequest(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        resp.setContentType("application/json");
        boolean isAuthorized = WebAppUtils.isAdmin(req) || WebAppUtils.getUsername(req) != null;
        if (isAuthorized) {
            CustomerManager customerManager = WebAppUtils.getCustomerManager(getServletContext());
            TimeManager timeManager = WebAppUtils.getTimeManager(getServletContext());
            Map<String, CustomerDetails> customerDetails;
            synchronized (getServletContext()) {
                if (!timeManager.isRewindMode())
                    customerDetails = customerManager.getCustomerDetails();
                else customerDetails = customerManager.getCustomerDetails(timeManager.getCurrentTime());
            }
            String customerDetailsJSON = WebAppUtils.GSON_INSTANCE.toJson(customerDetails);
            resp.getWriter().print(customerDetailsJSON);
            resp.setStatus(HttpServletResponse.SC_OK);
        }
        else resp.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        processRequest(req, resp);
    }
}
