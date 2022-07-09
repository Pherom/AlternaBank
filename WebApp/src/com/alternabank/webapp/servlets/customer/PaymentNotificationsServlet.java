package com.alternabank.webapp.servlets.customer;

import com.alternabank.dto.customer.PaymentNotificationsAndVersion;
import com.alternabank.engine.customer.CustomerManager;
import com.alternabank.engine.time.TimeManager;
import com.alternabank.webapp.util.WebAppUtils;
import com.google.gson.Gson;
import jakarta.servlet.Servlet;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.util.OptionalInt;

@WebServlet(name = "PaymentNotificationsServlet", urlPatterns = "/payment-notifications")
public class PaymentNotificationsServlet extends HttpServlet {

    protected void processRequest(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        resp.setContentType("application/json");
        if (WebAppUtils.isAdmin(req)) {
            resp.setStatus(HttpServletResponse.SC_CONFLICT);
        }
        else {
            String username = WebAppUtils.getUsername(req);
            if (username != null) {
                TimeManager timeManager = WebAppUtils.getTimeManager(getServletContext());
                CustomerManager customerManager = WebAppUtils.getCustomerManager(getServletContext());
                String paymentNotificationsVersionFromParameterString = req.getParameter("version");
                OptionalInt paymentNotificationsVersionFromParameter = OptionalInt.empty();
                try {
                    if (paymentNotificationsVersionFromParameterString != null && !paymentNotificationsVersionFromParameterString.isEmpty())
                        paymentNotificationsVersionFromParameter = OptionalInt.of(Integer.parseInt(paymentNotificationsVersionFromParameterString));
                    PaymentNotificationsAndVersion paymentNotificationsAndVersion = null;
                    synchronized (getServletContext()) {
                        int paymentNotificationsVersion;
                        if (!timeManager.isRewindMode()) {
                            CustomerManager.Customer customer = customerManager.getCustomersByName().get(username);
                            paymentNotificationsVersion = customer.getPaymentNotificationsVersion();
                            if (paymentNotificationsVersionFromParameter.isPresent()) {
                                paymentNotificationsAndVersion = new PaymentNotificationsAndVersion(customer.getPaymentNotifications(paymentNotificationsVersionFromParameter.getAsInt()), paymentNotificationsVersion);
                            } else
                                paymentNotificationsAndVersion = new PaymentNotificationsAndVersion(customer.getPaymentNotifications(), paymentNotificationsVersion);
                        }
                        else {
                            if (!paymentNotificationsVersionFromParameter.isPresent())
                                paymentNotificationsAndVersion = customerManager.getCustomerDetails(timeManager.getCurrentTime()).get(username).getPaymentNotificationsAndVersion();
                        }
                    }
                    if (paymentNotificationsAndVersion != null) {
                        String paymentNotificationsAndVersionJSON = WebAppUtils.GSON_INSTANCE.toJson(paymentNotificationsAndVersion);
                        resp.getWriter().print(paymentNotificationsAndVersionJSON);
                        resp.setStatus(HttpServletResponse.SC_OK);
                    }
                    else resp.setStatus(HttpServletResponse.SC_CONFLICT);
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
