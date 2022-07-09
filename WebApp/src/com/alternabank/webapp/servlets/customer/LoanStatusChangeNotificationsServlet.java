package com.alternabank.webapp.servlets.customer;

import com.alternabank.dto.customer.LoanStatusChangeNotificationsAndVersion;
import com.alternabank.dto.customer.PaymentNotificationsAndVersion;
import com.alternabank.engine.customer.CustomerManager;
import com.alternabank.webapp.util.WebAppUtils;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;

@WebServlet(name = "LoanStatusChangeNotificationsServlet", urlPatterns = "/loan-status-notifications")
public class LoanStatusChangeNotificationsServlet extends HttpServlet {

    protected void processRequest(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        resp.setContentType("application/json");
        if (WebAppUtils.isAdmin(req)) {
            resp.setStatus(HttpServletResponse.SC_CONFLICT);
        }
        else {
            String username = WebAppUtils.getUsername(req);
            if (username != null) {
                CustomerManager customerManager = WebAppUtils.getCustomerManager(getServletContext());
                int loanStatusChangeNotificationsVersionFromParameter;
                try {
                    loanStatusChangeNotificationsVersionFromParameter = Integer.parseInt(req.getParameter("version"));
                    LoanStatusChangeNotificationsAndVersion loanStatusChangeNotificationsAndVersion;
                    synchronized (getServletContext()) {
                        CustomerManager.Customer customer = customerManager.getCustomersByName().get(username);
                        int loanStatusChangeNotificationsVersion = customer.getLoanStatusChangeNotificationsVersion();
                        loanStatusChangeNotificationsAndVersion = new LoanStatusChangeNotificationsAndVersion(customer.getLoanStatusChangeNotifications(loanStatusChangeNotificationsVersionFromParameter), loanStatusChangeNotificationsVersion);
                    }
                    String loanStatusChangeNotificationsAndVersionJSON = WebAppUtils.GSON_INSTANCE.toJson(loanStatusChangeNotificationsAndVersion);
                    resp.getWriter().print(loanStatusChangeNotificationsAndVersionJSON);
                    resp.setStatus(HttpServletResponse.SC_OK);
                } catch (NumberFormatException numberFormatException) {
                    resp.setStatus(HttpServletResponse.SC_CONFLICT);
                }
            }
            else resp.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        }
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        processRequest(req, resp);
    }
}
