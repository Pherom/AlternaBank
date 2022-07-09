package com.alternabank.engine.xml;

import com.alternabank.engine.Engine;
import com.alternabank.engine.xml.result.XMLLoadResult;

import java.io.InputStream;

public interface XMLLoader {

    Engine getEngine();

    XMLLoadResult loadXML(String username, InputStream inputStream);

}
