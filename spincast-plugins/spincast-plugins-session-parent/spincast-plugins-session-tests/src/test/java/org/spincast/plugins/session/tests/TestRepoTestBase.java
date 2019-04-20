package org.spincast.plugins.session.tests;

import org.spincast.plugins.session.SpincastSessionRepository;

public abstract class TestRepoTestBase extends SessionTestBase {

    @Override
    protected Class<? extends SpincastSessionRepository> getSpincastSessionRepositoryImplClass() {
        return TestSessionRepository.class;
    }
}
