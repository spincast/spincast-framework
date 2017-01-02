package org.spincast.core.guice;

/**
 * Interface for Guice modules that are interested in
 * having the application Modules (combined) passed
 * before the Guice context is actually created.
 * <p>
 * They can use this module to ajust the bindings they are
 * going to perform and can even tweak the app Module itself.
 */
public interface SpincastAppModuleInterested {

    // todo
}
