package com.alternabank.engine.xml;

import com.alternabank.engine.customer.state.CustomerManagerState;
import com.alternabank.engine.loan.state.LoanManagerState;
import com.alternabank.engine.loan.request.LoanRequest;
import com.alternabank.engine.transaction.UnilateralTransaction;
import com.alternabank.engine.user.Admin;
import com.alternabank.engine.xml.event.*;
import com.alternabank.engine.xml.event.listener.*;
import com.alternabank.engine.xml.generated.*;

import javax.swing.event.EventListenerList;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;
import java.nio.file.*;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class XMLFileLoader implements XMLLoader{

    private final Admin admin;
    private boolean resumeLoad = false;
    private Path filePath;
    private Path lastLoadedFilePath = null;
    private AbsDescriptor loadedDescriptor;
    private final EventListenerList eventListeners = new EventListenerList();

    public XMLFileLoader(Admin admin) {
        this.admin = admin;
    }

    @Override
    public Admin getAdmin() {
        return admin;
    }

    @Override
    public void loadSystemFromFile(Path filePath) {
        this.filePath = filePath;
        boolean validFilePath = checkFilePath();
        if(validFilePath) {
            CustomerManagerState customerManagerState = admin.getCustomerManager().createCustomerManagerState();
            LoanManagerState loanManagerState = admin.getLoanManager().createLoanManagerState();
            admin.getLoanManager().reset();
            admin.getCustomerManager().reset();
            resumeLoad = true;
            loadDescriptor();
            loadCustomers();
            if (resumeLoad)
                loadLoanCategories();
            if (resumeLoad)
                loadLoans();

            if (resumeLoad) {
                lastLoadedFilePath = filePath;
                resumeLoad = false;
                admin.getTimeManager().resetCurrentTime();
                Arrays.stream(eventListeners.getListeners(XMLLoadSuccessListener.class)).forEach(listener -> listener.loadedSuccessfully(new XMLLoadSuccessEvent(this)));
            }
            else {
                admin.getCustomerManager().restoreCustomerManager(customerManagerState);
                admin.getLoanManager().restoreLoanManager(loanManagerState);
            }
        }
    }

    @Override
    public void addFileLoadFailureListener(XMLFileLoadFailureListener listener) {
        eventListeners.add(XMLFileLoadFailureListener.class, listener);
    }

    @Override
    public void addCategoryLoadFailureListener(XMLCategoryLoadFailureListener listener) {
        eventListeners.add(XMLCategoryLoadFailureListener.class, listener);
    }

    @Override
    public void addCustomerLoadFailureListener(XMLCustomerLoadFailureListener listener) {
        eventListeners.add(XMLCustomerLoadFailureListener.class, listener);
    }

    @Override
    public void addLoanLoadFailureListener(XMLLoanLoadFailureListener listener) {
        eventListeners.add(XMLLoanLoadFailureListener.class, listener);
    }

    @Override
    public void addLoadSuccessListener(XMLLoadSuccessListener listener) {
        eventListeners.add(XMLLoadSuccessListener.class, listener);
    }

    private boolean checkFilePath() {
        boolean valid;

        List<XMLLoadFailureEvent.Cause<Path>> loadFailureCauses = Stream.of(XMLFileLoadFailureEvent.Cause.values())
                .filter(cause -> cause.getPredicate().test(filePath))
                .collect(Collectors.toList());

        valid = loadFailureCauses.isEmpty();

        if(!valid)
            Arrays.stream(eventListeners.getListeners(XMLFileLoadFailureListener.class)).forEach(listener -> listener.fileLoadFailed(new XMLFileLoadFailureEvent(this, loadFailureCauses, filePath)));

        return valid;
    }

    @Override
    public void stopLoading() {
        this.resumeLoad = false;
    }

    @Override
    public Path getLastLoadedFilePath() {
        return lastLoadedFilePath;
    }

    private void loadDescriptor() {
        try {
            JAXBContext jaxbContext = JAXBContext.newInstance(AbsDescriptor.class);
            Unmarshaller jaxbUnmarshaller = jaxbContext.createUnmarshaller();
            this.loadedDescriptor = (AbsDescriptor) jaxbUnmarshaller.unmarshal(filePath.toFile());
        } catch (JAXBException e) {
            e.printStackTrace();
        }
    }

    private void loadLoanCategories() {
        loadedDescriptor.getAbsCategories().getAbsCategory().stream()
                .filter(loadedCategory -> resumeLoad && checkCategory(loadedCategory))
                .forEach(loadedCategory -> {
                    admin.getLoanManager().addCategory(loadedCategory); });
    }

    private boolean checkCategory(String loadedCategory) {
        boolean valid;
        List<XMLLoadFailureEvent.Cause<String>> loadFailureCauses = Stream.of(XMLCategoryLoadFailureEvent.Cause.values())
                .filter(cause -> cause.getPredicate().test(loadedCategory))
                .collect(Collectors.toList());

        valid = loadFailureCauses.isEmpty();

        if(!valid)
            Arrays.stream(eventListeners.getListeners(XMLCategoryLoadFailureListener.class)).forEach(listener -> listener.categoryLoadFailed(new XMLCategoryLoadFailureEvent(this, loadFailureCauses, loadedCategory)));
        return valid;
    }

    private void loadCustomers() {
        loadedDescriptor.getAbsCustomers().getAbsCustomer().stream()
                .filter(loadedCustomer -> resumeLoad && checkCustomer(loadedCustomer))
                .forEach(loadedCustomer -> {
                    admin.getCustomerManager().createCustomer((loadedCustomer.getName()))
                        .getAccount().executeTransaction(UnilateralTransaction.Type.DEPOSIT, loadedCustomer.getAbsBalance()); });
    }

    private boolean checkCustomer(AbsCustomer loadedCustomer) {
        boolean valid;

        List<XMLLoadFailureEvent.Cause<AbsCustomer>> loadFailureCauses = Stream.of(XMLCustomerLoadFailureEvent.Cause.values())
                .filter(cause -> cause.getPredicate().test(loadedCustomer))
                .collect(Collectors.toList());

        valid = loadFailureCauses.isEmpty();

        if(!valid)
            Arrays.stream(eventListeners.getListeners(XMLCustomerLoadFailureListener.class)).forEach(listener -> listener.customerLoadFailed(new XMLCustomerLoadFailureEvent(this, loadFailureCauses, loadedCustomer)));

        return valid;
    }

    private void loadLoans() {
        loadedDescriptor.getAbsLoans().getAbsLoan().stream()
                .filter(loadedLoan -> resumeLoad && checkLoan(loadedLoan))
                .forEach(loadedLoan -> {
                    admin.getCustomerManager().getCustomersByName().get(loadedLoan.getAbsOwner())
                        .postLoanRequest(LoanRequest.createByInterestPerPayment(loadedLoan.getAbsOwner(), loadedLoan.getAbsCategory(),
                                loadedLoan.getAbsCapital(), loadedLoan.getAbsPaysEveryYaz(), loadedLoan.getAbsIntristPerPayment(),
                                loadedLoan.getAbsTotalYazTime(), loadedLoan.getId())); });
    }

    private boolean checkLoan(AbsLoan loadedLoan) {
        boolean valid;

        List<XMLLoadFailureEvent.Cause<AbsLoan>> loadFailureCauses = Stream.of(XMLLoanLoadFailureEvent.Cause.values())
                .filter(cause -> cause.getPredicate().test(loadedLoan))
                .collect(Collectors.toList());

        valid = loadFailureCauses.isEmpty();

        if(!valid)
            Arrays.stream(eventListeners.getListeners(XMLLoanLoadFailureListener.class)).forEach(listener -> listener.loanLoadFailed(new XMLLoanLoadFailureEvent(this, loadFailureCauses, loadedLoan)));

        return valid;
    }

}
