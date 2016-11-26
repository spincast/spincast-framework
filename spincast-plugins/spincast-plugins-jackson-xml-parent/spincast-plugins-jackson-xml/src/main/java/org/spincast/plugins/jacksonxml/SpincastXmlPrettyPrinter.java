package org.spincast.plugins.jacksonxml;

import javax.xml.stream.XMLStreamException;

import org.codehaus.stax2.XMLStreamWriter2;

import com.fasterxml.jackson.dataformat.xml.util.DefaultXmlPrettyPrinter;
import com.google.inject.Inject;
import com.google.inject.Provider;

public class SpincastXmlPrettyPrinter extends DefaultXmlPrettyPrinter {

    protected static final long serialVersionUID = 1L;

    private final Provider<SpincastXmlPrettyPrinter> spincastXmlPrettyPrinterProvider;
    private final Indenter spincastXmlIndenter;
    private final SpincastXmlManagerConfig spincastXmlManagerConfig;

    @Inject
    public SpincastXmlPrettyPrinter(Provider<SpincastXmlPrettyPrinter> spincastXmlPrettyPrinterProvider,
                                    Indenter spincastXmlIndenter,
                                    SpincastXmlManagerConfig spincastXmlManagerConfig) {
        this.spincastXmlPrettyPrinterProvider = spincastXmlPrettyPrinterProvider;
        this.spincastXmlIndenter = spincastXmlIndenter;
        this.spincastXmlManagerConfig = spincastXmlManagerConfig;
    }

    @Inject
    protected void init() {
        indentObjectsWith(getSpincastXmlIndenter());
        indentArraysWith(getSpincastXmlIndenter());
    }

    protected SpincastXmlManagerConfig getSpincastXmlManagerConfig() {
        return this.spincastXmlManagerConfig;
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

    @Override
    public void writePrologLinefeed(XMLStreamWriter2 sw) throws XMLStreamException {
        sw.writeRaw(getSpincastXmlManagerConfig().getPrettyPrinterNewlineChars());
    }

}
