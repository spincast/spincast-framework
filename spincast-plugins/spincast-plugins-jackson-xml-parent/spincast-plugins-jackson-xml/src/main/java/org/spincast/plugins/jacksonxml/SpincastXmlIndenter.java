package org.spincast.plugins.jacksonxml;

import java.io.IOException;
import java.util.Arrays;

import javax.xml.stream.XMLStreamException;

import org.codehaus.stax2.XMLStreamWriter2;

import com.fasterxml.jackson.core.JsonGenerationException;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.dataformat.xml.util.DefaultXmlPrettyPrinter.Indenter;
import com.google.inject.Inject;

/**
 * Custom XML indenter.
 * Allow us to specify how many spaces to use for indentation and what
 * newline character to use.
 *
 * Based on com.fasterxml.jackson.dataformat.xml.util.DefaultXmlPrettyPrinter$Lf2SpacesIndenter
 */
public class SpincastXmlIndenter implements Indenter {

    private final SpincastXmlManagerConfig spincastXmlManagerConfig;

    @Inject
    public SpincastXmlIndenter(SpincastXmlManagerConfig spincastXmlManagerConfig) {
        this.spincastXmlManagerConfig = spincastXmlManagerConfig;
    }

    protected SpincastXmlManagerConfig getSpincastXmlManagerConfig() {
        return this.spincastXmlManagerConfig;
    }

    @Override
    public boolean isInline() {
        return false;
    }

    final static int SPACE_COUNT = 64;
    final static char[] SPACES = new char[SPACE_COUNT];

    static {
        Arrays.fill(SPACES, ' ');
    }

    @Override
    public void writeIndentation(XMLStreamWriter2 sw, int level) throws XMLStreamException {
        sw.writeRaw(getNewlineChars());
        int spaceNbr = level * getIndentationSpaceNumber();
        while (level > SPACE_COUNT) { // should never happen but...
            sw.writeRaw(SPACES, 0, SPACE_COUNT);
            level -= SPACES.length;
        }
        sw.writeRaw(SPACES, 0, spaceNbr);
    }

    @Override
    public void writeIndentation(JsonGenerator jg, int level) throws IOException,
                                                              JsonGenerationException {
        jg.writeRaw(getNewlineChars());
        int spaceNbr = level * getIndentationSpaceNumber();
        while (level > SPACE_COUNT) { // should never happen but...
            jg.writeRaw(SPACES, 0, SPACE_COUNT);
            level -= SPACES.length;
        }
        jg.writeRaw(SPACES, 0, spaceNbr);
    }

    protected int getIndentationSpaceNumber() {
        return getSpincastXmlManagerConfig().getPrettyPrinterIndentationSpaceNumber();
    }

    protected String getNewlineChars() {
        return getSpincastXmlManagerConfig().getPrettyPrinterNewlineChars();
    }

}
