package com.alternabank.engine.xml;

import com.alternabank.dto.loan.request.LoanRequest;
import com.alternabank.engine.customer.CustomerManager;
import com.alternabank.engine.customer.state.CustomerManagerState;
import com.alternabank.engine.loan.state.LoanManagerState;
import com.alternabank.engine.Engine;
import com.alternabank.engine.time.TimeManager;
import com.alternabank.engine.xml.generated.*;
import com.alternabank.engine.xml.result.*;
import com.alternabank.engine.xml.result.failue.XMLCategoryLoadFailureCause;
import com.alternabank.engine.xml.result.failue.XMLLoadFailureCause;
import com.alternabank.engine.xml.result.failue.XMLLoanLoadFailureCause;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;
import java.io.InputStream;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class XMLFileLoader implements XMLLoader{

    private final Engine engine;
    private InputStream inputStream;
    private CustomerManager.Customer currentUser;
    private AbsDescriptor loadedDescriptor;

    public XMLFileLoader(Engine engine) {
        this.engine = engine;
    }

    @Override
    public Engine getEngine() {
        return engine;
    }

    @Override
    public XMLLoadResult loadXML(String username, InputStream inputStream) {
        XMLLoadResult xmlLoadResult;
        this.currentUser = engine.getCustomerManager().getCustomersByName().get(username);
        this.inputStream = inputStream;
        CustomerManagerState customerManagerState = engine.getCustomerManager().createCustomerManagerState();
        LoanManagerState loanManagerState = engine.getLoanManager().createLoanManagerState();
        List<String> errorMessages = new LinkedList<String>();
        List<XMLLoadFailureCause<String, Engine>> categoryLoadFailureCauses;
        List<XMLLoadFailureCause<AbsLoan, Engine>> loanLoadFailureCauses;

        loadDescriptor();
        errorMessages.addAll(loadLoanCategories());
        errorMessages.addAll(loadLoans());

        if (errorMessages.isEmpty()) {
            xmlLoadResult = new XMLLoadResult(XMLLoadStatus.SUCCESS, "XML file loaded successfully");
        }
        else {
            engine.getCustomerManager().restoreCustomerManager(customerManagerState);
            engine.getLoanManager().restoreLoanManager(loanManagerState);
            StringBuilder errorMessageBuilder = new StringBuilder("XML file load failed");
            errorMessages.forEach(message -> errorMessageBuilder.append(String.format("%s%s", System.lineSeparator(), message)));
            xmlLoadResult = new XMLLoadResult(XMLLoadStatus.FAILURE, errorMessageBuilder.toString());
        }

        return xmlLoadResult;
    }

    private void loadDescriptor() {
        try {
            JAXBContext jaxbContext = JAXBContext.newInstance(AbsDescriptor.class);
            Unmarshaller jaxbUnmarshaller = jaxbContext.createUnmarshaller();
            this.loadedDescriptor = (AbsDescriptor) jaxbUnmarshaller.unmarshal(inputStream);
        } catch (JAXBException e) {
            e.printStackTrace();
        }
    }

    private List<String> loadLoanCategories() {
        List<String> categoryLoadErrorMessages = new LinkedList<>();
        loadedDescriptor.getAbsCategories().getAbsCategory().stream()
                .forEach(loadedCategory -> {
                    List<XMLLoadFailureCause<String, Engine>> loadFailureCauses = checkCategory(loadedCategory);
                    if (loadFailureCauses.isEmpty())
                        engine.getLoanManager().addCategory(loadedCategory);
                    else categoryLoadErrorMessages.addAll(loadFailureCauses.stream().map(cause -> cause.getErrorMessage(loadedCategory)).collect(Collectors.toList()));
                });
        return categoryLoadErrorMessages;
    }

    private List<XMLLoadFailureCause<String, Engine>> checkCategory(String loadedCategory) {
        List<XMLLoadFailureCause<String, Engine>> loadFailureCauses = Stream.of(XMLCategoryLoadFailureCause.values())
                .filter(cause -> cause.getPredicate().test(loadedCategory, engine))
                .collect(Collectors.toList());

        return loadFailureCauses;
    }

    private List<String> loadLoans() {
        List<String> loanLoadErrorMessages = new LinkedList<>();
        loadedDescriptor.getAbsLoans().getAbsLoan().stream()
                .forEach(loadedLoan -> {
                    List<XMLLoadFailureCause<AbsLoan, Engine>> loadFailureCauses = checkLoan(loadedLoan);
                    if (loadFailureCauses.isEmpty())
                        currentUser.postLoanRequest(LoanRequest.createByInterestPerPayment(currentUser.getName(), loadedLoan.getAbsCategory(),
                                loadedLoan.getAbsCapital(), loadedLoan.getAbsPaysEveryYaz(), loadedLoan.getAbsIntristPerPayment(),
                                loadedLoan.getAbsTotalYazTime(), loadedLoan.getId()));
                    else loanLoadErrorMessages.addAll(loadFailureCauses.stream().map(cause -> cause.getErrorMessage(loadedLoan)).collect(Collectors.toList()));
                });
        return loanLoadErrorMessages;
    }

    private List<XMLLoadFailureCause<AbsLoan, Engine>> checkLoan(AbsLoan loadedLoan) {
        List<XMLLoadFailureCause<AbsLoan, Engine>> loadFailureCauses = Stream.of(XMLLoanLoadFailureCause.values())
                .filter(cause -> cause.getPredicate().test(loadedLoan, engine))
                .collect(Collectors.toList());

        return loadFailureCauses;
    }

}
