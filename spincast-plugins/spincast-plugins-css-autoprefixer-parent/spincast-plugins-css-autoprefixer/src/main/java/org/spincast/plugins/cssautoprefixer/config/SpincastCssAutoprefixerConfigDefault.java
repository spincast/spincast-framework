package org.spincast.plugins.cssautoprefixer.config;

import org.spincast.shaded.org.apache.commons.lang3.SystemUtils;

public class SpincastCssAutoprefixerConfigDefault implements SpincastCssAutoprefixerConfig {

    @Override
    public String getPostcssExecutableName() {

        String postcssExecutable = "postcss";

        if (SystemUtils.IS_OS_WINDOWS) {
            postcssExecutable += ".cmd";
        }

        return postcssExecutable;
    }

}
