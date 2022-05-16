package com.alternabank.engine.xml.event;

import com.alternabank.engine.customer.CustomerManager;
import com.alternabank.engine.user.UserManager;
import com.alternabank.engine.xml.XMLLoader;
import com.alternabank.engine.xml.generated.AbsCustomer;

import java.util.List;
import java.util.function.Function;
import java.util.function.Predicate;

public class XMLCustomerLoadFailureEvent extends XMLLoadFailureEvent<AbsCustomer> {

    public XMLCustomerLoadFailureEvent(XMLLoader source, List<XMLLoadFailureEvent.Cause<AbsCustomer>> causes, AbsCustomer trigger) {
        super(source, causes, trigger);
    }

    public enum Cause implements XMLLoadFailureEvent.Cause<AbsCustomer> {

        EMPTY_CUSTOMER_NAME((absCustomer) -> absCustomer.getName().isEmpty(),
                (absCustomer) -> "Found customer with empty name!"),
        CUSTOMER_NAME_COLLISION((absCustomer) -> UserManager.getInstance().getAdmin().getCustomerManager().customerExists(absCustomer.getName()),
                (absCustomer) -> String.format("Found multiple customers with identical name \"%s\"", absCustomer.getName())),
        CUSTOMER_NEGATIVE_BALANCE((absCustomer) -> absCustomer.getAbsBalance() < 0,
                (absCustomer) -> String.format("Customer named \"%s\" has a negative balance of \"%d\" which is not allowed", absCustomer.getName(), absCustomer.getAbsBalance()));

        private final Predicate<AbsCustomer> predicate;
        private final Function<AbsCustomer, String> errorMessageGenerator;

        Cause(Predicate<AbsCustomer> predicate, Function<AbsCustomer, String> errorMessageGenerator) {
            this.predicate = predicate;
            this.errorMessageGenerator = errorMessageGenerator;
        }

        @Override
        public Predicate<AbsCustomer> getPredicate() {
            return predicate;
        }

        @Override
        public String getErrorMessage(AbsCustomer trigger) {
            return errorMessageGenerator.apply(trigger);
        }

    }

}
