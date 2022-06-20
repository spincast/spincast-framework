package org.spincast.plugins.undertow;

import com.google.inject.assistedinject.Assisted;

public interface SpincastClassPathResourceManagerFactory {

    public SpincastClassPathFileResourceManager createFileManage(String path);

    public SpincastClassPathDirResourceManager createDirManager(@Assisted("rootUrl") String rootUrl,
                                                                @Assisted("dirPath") String dirPath);

}
