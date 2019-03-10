package org.spincast.testing.defaults.tests.utils;

import org.spincast.core.exchange.RequestContextBase;
import org.spincast.core.exchange.RequestContextBaseDeps;

import com.google.inject.assistedinject.Assisted;
import com.google.inject.assistedinject.AssistedInject;

public class RequestContextTestingDefault extends RequestContextBase<RequestContextTesting>
                                                 implements RequestContextTesting {

    @AssistedInject
    public RequestContextTestingDefault(@Assisted Object exchange,
                                        RequestContextBaseDeps<RequestContextTesting> requestContextBaseDeps) {
        super(exchange, requestContextBaseDeps);
    }

    @Override
    public String test() {
        return "42";
    }
}