package com.alternabank.engine.xml;

import com.alternabank.engine.customer.CustomerManager;
import com.alternabank.engine.customer.dto.CustomerManagerState;
import com.alternabank.engine.loan.LoanManager;
import com.alternabank.engine.loan.dto.LoanManagerState;
import com.alternabank.engine.loan.request.LoanRequest;
import com.alternabank.engine.time.TimeManager;
import com.alternabank.engine.transaction.UnilateralTransaction;
import com.alternabank.engine.xml.event.*;
import com.alternabank.engine.xml.event.listener.*;
import com.alternabank.engine.xml.generated.*;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;
import java.nio.file.*;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class XMLFileLoader implements XMLLoader{

    private boolean resumeLoad = false;
    private Path filePath;
    private Path lastLoadedFilePath = null;
    private AbsDescriptor loadedDescriptor;
    private final List<XMLFileLoadFailureListener> fileLoadFailureListeners = new LinkedList<>();
    private final List<XMLCategoryLoadFailureListener> categoryLoadFailureListeners = new LinkedList<>();
    private final List<XMLCustomerLoadFailureListener> customerLoadFailureListeners = new LinkedList<>();
    private final List<XMLLoanLoadFailureListener> loanLoadFailureListeners = new LinkedList<>();
    private final List<XMLLoadSuccessListener> loadSuccessListeners = new LinkedList<>();
    private final List<String> loadedCustomerNames = new LinkedList<>();
    private final List<String> loadedLoanIDs = new LinkedList<>();
    private final List<String> loadedCategoryNames = new LinkedList<>();
    private final List<String> lastLoadedCustomerNames = new LinkedList<>();
    private final List<String> lastLoadedLoanIDs = new LinkedList<>();
    private final List<String> lastLoadedCategoryNames = new LinkedList<>();

    @Override
    public void loadSystemFromFile(Path filePath) {
        this.filePath = filePath;
        boolean validFilePath = checkFilePath();
        if(validFilePath) {
            CustomerManagerState customerManagerState = CustomerManager.getInstance().createCustomerManagerState();
            LoanManagerState loanManagerState = LoanManager.getInstance().createLoanManagerState();
            undoLastLoad();
            resumeLoad = true;
            loadDescriptor();
            loadCustomers();
            if (resumeLoad)
                loadLoanCategories();
            if (resumeLoad)
                loadLoans();

            if (resumeLoad && !(loadedCustomerNames.isEmpty() || loadedCategoryNames.isEmpty() || loadedLoanIDs.isEmpty())) {
                lastLoadedFilePath = filePath;
                lastLoadedCustomerNames.addAll(loadedCustomerNames);
                lastLoadedLoanIDs.addAll(loadedLoanIDs);
                lastLoadedCategoryNames.addAll(loadedCategoryNames);
                loadedCustomerNames.clear();
                loadedLoanIDs.clear();
                loadedCategoryNames.clear();
                resumeLoad = false;
                TimeManager.getInstance().resetCurrentTime();
                loadSuccessListeners.forEach(listener -> listener.loadedSuccessfully(new XMLLoadSuccessEvent(this)));
            }
            else {
                CustomerManager.getInstance().restoreCustomerManager(customerManagerState);
                LoanManager.getInstance().restoreLoanManager(loanManagerState);
            }
        }
    }

    @Override
    public void addFileLoadFailureListener(XMLFileLoadFailureListener listener) {
        fileLoadFailureListeners.add(listener);
    }

    @Override
    public void addCategoryLoadFailureListener(XMLCategoryLoadFailureListener listener) {
        categoryLoadFailureListeners.add(listener);
    }

    @Override
    public void addCustomerLoadFailureListener(XMLCustomerLoadFailureListener listener) {
        customerLoadFailureListeners.add(listener);
    }

    @Override
    public void addLoanLoadFailureListener(XMLLoanLoadFailureListener listener) {
        loanLoadFailureListeners.add(listener);
    }

    @Override
    public void addLoadSuccessListener(XMLLoadSuccessListener listener) {
        loadSuccessListeners.add(listener);
    }

    @Override
    public List<XMLFileLoadFailureListener> getFileLoadFailureListeners() {
        return Collections.unmodifiableList(fileLoadFailureListeners);
    }

    @Override
    public List<XMLCategoryLoadFailureListener> getCategoryLoadFailureListeners() {
        return Collections.unmodifiableList(categoryLoadFailureListeners);
    }

    @Override
    public List<XMLCustomerLoadFailureListener> getCustomerLoadFailureListeners() {
        return Collections.unmodifiableList(customerLoadFailureListeners);
    }

    @Override
    public List<XMLLoanLoadFailureListener> getLoanLoadFailureListeners() {
        return Collections.unmodifiableList(loanLoadFailureListeners);
    }

    @Override
    public List<XMLLoadSuccessListener> getLoadSuccessListeners() {
        return Collections.unmodifiableList(loadSuccessListeners);
    }

    private boolean checkFilePath() {
        boolean valid;

        List<XMLLoadFailureEvent.Cause<Path>> loadFailureCauses = Stream.of(XMLFileLoadFailureEvent.Cause.values())
                .filter(cause -> cause.getPredicate().test(filePath))
                .collect(Collectors.toList());

        valid = loadFailureCauses.isEmpty();

        if(!valid)
            fileLoadFailureListeners.forEach(listener -> listener.fileLoadFailed(new XMLFileLoadFailureEvent(this, loadFailureCauses, filePath)));

        return valid;
    }

    @Override
    public void stopLoading() {
        this.resumeLoad = false;
    }

    @Override
    public Path getPathOfLastLoadedFile() {
        return lastLoadedFilePath;
    }

    @Override
    public Path getLastLoadedFilePath() {
        return lastLoadedFilePath;
    }

    private void undoLastLoad() {
        lastLoadedLoanIDs.removeIf(id -> LoanManager.getInstance().removeLoan(id));
        lastLoadedCategoryNames.removeIf(category -> LoanManager.getInstance().removeCategory(category));
        lastLoadedCustomerNames.removeIf(name -> CustomerManager.getInstance().removeCustomer(name));
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
                    loadedCategoryNames.add(loadedCategory);
                    LoanManager.getInstance().addCategory(loadedCategory); });
    }

    private boolean checkCategory(String loadedCategory) {
        boolean valid;
        List<XMLLoadFailureEvent.Cause<String>> loadFailureCauses = Stream.of(XMLCategoryLoadFailureEvent.Cause.values())
                .filter(cause -> cause.getPredicate().test(loadedCategory))
                .collect(Collectors.toList());

        valid = loadFailureCauses.isEmpty();

        if(!valid)
            categoryLoadFailureListeners.forEach(listener -> listener.categoryLoadFailed(new XMLCategoryLoadFailureEvent(this, loadFailureCauses, loadedCategory)));
        return valid;
    }

    private void loadCustomers() {
        loadedDescriptor.getAbsCustomers().getAbsCustomer().stream()
                .filter(loadedCustomer -> resumeLoad && checkCustomer(loadedCustomer))
                .forEach(loadedCustomer -> {
                    loadedCustomerNames.add(loadedCustomer.getName());
                    CustomerManager.getInstance().createCustomer((loadedCustomer.getName()))
                        .getAccount().executeTransaction(UnilateralTransaction.Type.DEPOSIT, loadedCustomer.getAbsBalance()); });
    }

    private boolean checkCustomer(AbsCustomer loadedCustomer) {
        boolean valid;

        List<XMLLoadFailureEvent.Cause<AbsCustomer>> loadFailureCauses = Stream.of(XMLCustomerLoadFailureEvent.Cause.values())
                .filter(cause -> cause.getPredicate().test(loadedCustomer))
                .collect(Collectors.toList());

        valid = loadFailureCauses.isEmpty();

        if(!valid)
            customerLoadFailureListeners.forEach(listener -> listener.customerLoadFailed(new XMLCustomerLoadFailureEvent(this, loadFailureCauses, loadedCustomer)));

        return valid;
    }

    private void loadLoans() {
        loadedDescriptor.getAbsLoans().getAbsLoan().stream()
                .filter(loadedLoan -> resumeLoad && checkLoan(loadedLoan))
                .forEach(loadedLoan -> {
                    loadedLoanIDs.add(loadedLoan.getId());
                    CustomerManager.getInstance().getCustomersByName().get(loadedLoan.getAbsOwner())
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
            loanLoadFailureListeners.forEach(listener -> listener.loanLoadFailed(new XMLLoanLoadFailureEvent(this, loadFailureCauses, loadedLoan)));

        return valid;
    }

}
