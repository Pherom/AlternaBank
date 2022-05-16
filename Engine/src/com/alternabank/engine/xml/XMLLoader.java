package com.alternabank.engine.xml;

import com.alternabank.engine.user.Admin;
import com.alternabank.engine.xml.event.listener.*;

import java.nio.file.Path;
import java.util.List;

public interface XMLLoader {

    Admin getAdmin();

    void loadSystemFromFile(Path filePath);

    void addFileLoadFailureListener(XMLFileLoadFailureListener listener);

    void addCategoryLoadFailureListener(XMLCategoryLoadFailureListener listener);

    void addCustomerLoadFailureListener(XMLCustomerLoadFailureListener listener);

    void addLoanLoadFailureListener(XMLLoanLoadFailureListener listener);

    void addLoadSuccessListener(XMLLoadSuccessListener listener);

/*    List<XMLFileLoadFailureListener> getFileLoadFailureListeners();

    List<XMLCategoryLoadFailureListener> getCategoryLoadFailureListeners();

    List<XMLCustomerLoadFailureListener> getCustomerLoadFailureListeners();

    List<XMLLoanLoadFailureListener> getLoanLoadFailureListeners();

    List<XMLLoadSuccessListener> getLoadSuccessListeners();*/

    void stopLoading();

    Path getLastLoadedFilePath();

}
