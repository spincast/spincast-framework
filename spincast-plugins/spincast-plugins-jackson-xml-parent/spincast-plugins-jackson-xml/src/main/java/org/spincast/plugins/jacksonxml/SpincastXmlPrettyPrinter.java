package org.spincast.plugins.jacksonxml;

import com.fasterxml.jackson.dataformat.xml.util.DefaultXmlPrettyPrinter;
import com.google.inject.Inject;
import com.google.inject.Provider;

public class SpincastXmlPrettyPrinter extends DefaultXmlPrettyPrinter {

    protected static final long serialVersionUID = 1L;

    private final Provider<SpincastXmlPrettyPrinter> spincastXmlPrettyPrinterProvider;
    private final Indenter spincastXmlIndenter;

    @Inject
    public SpincastXmlPrettyPrinter(Provider<SpincastXmlPrettyPrinter> spincastXmlPrettyPrinterProvider,
                                    Indenter spincastXmlIndenter) {
        this.spincastXmlPrettyPrinterProvider = spincastXmlPrettyPrinterProvider;
        this.spincastXmlIndenter = spincastXmlIndenter;
    }

    @Inject
    protected void init() {
        indentObjectsWith(getSpincastXmlIndenter());
        indentArraysWith(getSpincastXmlIndenter());
    }

    protected Provider<SpincastXmlPrettyPrinter> getSpincastXmlPrettyPrinterProvider() {
        return this.spincastXmlPrettyPrinterProvider;
    }

    protected Indenter getSpincastXmlIndenter() {
        return this.spincastXmlIndenter;
    }

    @Override
    public SpincastXmlPrettyPrinter createInstance() {
        return getSpincastXmlPrettyPrinterProvider().get();
    }

}
