package org.spincast.core.server;

import java.io.File;

public class UploadedFileDefault implements UploadedFile {

    private final File file;
    private final String fileName;

    public UploadedFileDefault(File file, String fileName) {
        this.file = file;
        this.fileName = fileName;
    }

    @Override
    public File getFile() {
        return this.file;
    }

    @Override
    public String getFileName() {
        return this.fileName;
    }

    @Override
    public String toString() {
        return getFileName();
    }
}
