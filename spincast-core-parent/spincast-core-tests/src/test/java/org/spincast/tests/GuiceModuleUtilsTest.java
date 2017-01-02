package org.spincast.tests;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.util.Set;

import org.junit.Test;
import org.spincast.core.config.SpincastConfig;
import org.spincast.core.config.SpincastDictionary;
import org.spincast.core.guice.GuiceModuleUtils;
import org.spincast.core.guice.SpincastGuiceModuleBase;
import org.spincast.core.utils.SpincastUtils;
import org.spincast.core.utils.SpincastUtilsDefault;
import org.spincast.plugins.dictionary.SpincastDictionaryDefault;

import com.google.common.collect.Sets;
import com.google.inject.Inject;
import com.google.inject.Key;
import com.google.inject.Module;
import com.google.inject.Scopes;

public class GuiceModuleUtilsTest {

    @Test
    public void getBoundClassesExtending() throws Exception {

        Module module = new SpincastGuiceModuleBase() {

            @Override
            protected void configure() {
                bind(SpincastUtils.class).to(SpincastUtilsDefault.class).in(Scopes.SINGLETON);
            }
        };

        GuiceModuleUtils guiceModuleUtils = new GuiceModuleUtils(module);

        Set<Class<? extends SpincastUtils>> classes = guiceModuleUtils.getBoundClassesExtending(SpincastUtils.class);
        assertNotNull(classes);
        assertEquals(1, classes.size());
        assertEquals(SpincastUtilsDefault.class, classes.iterator().next());

        Set<Class<? extends SpincastDictionary>> classes2 =
                guiceModuleUtils.getBoundClassesExtending(SpincastDictionary.class);
        assertNotNull(classes2);
        assertEquals(0, classes2.size());
    }

    protected static interface SpincastUtilsTesting extends SpincastUtils {

        public String toto();
    }

    protected static class SpincastUtilsTestingDefault extends SpincastUtilsDefault implements SpincastUtilsTesting {

        @Inject
        public SpincastUtilsTestingDefault(SpincastConfig spincastConfig) {
            super(spincastConfig);
        }

        @Override
        public String toto() {
            return "titi";
        }
    }

    @Test
    public void getBoundClassesExtendingIndirect() throws Exception {

        Module module = new SpincastGuiceModuleBase() {

            @Override
            protected void configure() {
                //==========================================
                // Binds SpincastUtilsTesting not SpincastUtils
                // driectly.
                //==========================================
                bind(SpincastUtilsTesting.class).to(SpincastUtilsTestingDefault.class).in(Scopes.SINGLETON);
            }
        };

        GuiceModuleUtils guiceModuleUtils = new GuiceModuleUtils(module);

        Set<Class<? extends SpincastUtils>> classes = guiceModuleUtils.getBoundClassesExtending(SpincastUtils.class);
        assertNotNull(classes);
        assertEquals(1, classes.size());
        assertEquals(SpincastUtilsTestingDefault.class, classes.iterator().next());
    }

    @Test
    public void getBoundClassesExtendingNoInterfaceBound() throws Exception {

        Module module = new SpincastGuiceModuleBase() {

            @Override
            protected void configure() {
                bind(SpincastUtilsDefault.class).in(Scopes.SINGLETON);
            }
        };

        GuiceModuleUtils guiceModuleUtils = new GuiceModuleUtils(module);

        Set<Class<? extends SpincastUtils>> classes = guiceModuleUtils.getBoundClassesExtending(SpincastUtils.class);
        assertNotNull(classes);
        assertEquals(1, classes.size());
        assertEquals(SpincastUtilsDefault.class, classes.iterator().next());
    }

    @Test
    public void multipleModulesSet() throws Exception {

        Module module1 = new SpincastGuiceModuleBase() {

            @Override
            protected void configure() {
                bind(SpincastUtils.class).to(SpincastUtilsDefault.class).in(Scopes.SINGLETON);
            }
        };

        Module module2 = new SpincastGuiceModuleBase() {

            @Override
            protected void configure() {
                bind(SpincastDictionary.class).to(SpincastDictionaryDefault.class).in(Scopes.SINGLETON);
            }
        };

        GuiceModuleUtils guiceModuleUtils = new GuiceModuleUtils(Sets.newHashSet(module1, module2));

        Set<Class<? extends SpincastUtils>> classes = guiceModuleUtils.getBoundClassesExtending(SpincastUtils.class);
        assertNotNull(classes);
        assertEquals(1, classes.size());
        assertEquals(SpincastUtilsDefault.class, classes.iterator().next());

        Set<Class<? extends SpincastDictionary>> classes2 =
                guiceModuleUtils.getBoundClassesExtending(SpincastDictionary.class);
        assertNotNull(classes2);
        assertEquals(1, classes2.size());
        assertEquals(SpincastDictionaryDefault.class, classes2.iterator().next());
    }

    @Test
    public void multipleModulesVarArgs() throws Exception {

        Module module1 = new SpincastGuiceModuleBase() {

            @Override
            protected void configure() {
                bind(SpincastUtils.class).to(SpincastUtilsDefault.class).in(Scopes.SINGLETON);
            }
        };

        Module module2 = new SpincastGuiceModuleBase() {

            @Override
            protected void configure() {
                bind(SpincastDictionary.class).to(SpincastDictionaryDefault.class).in(Scopes.SINGLETON);
            }
        };

        GuiceModuleUtils guiceModuleUtils = new GuiceModuleUtils(module1, module2);

        Set<Class<? extends SpincastUtils>> classes = guiceModuleUtils.getBoundClassesExtending(SpincastUtils.class);
        assertNotNull(classes);
        assertEquals(1, classes.size());
        assertEquals(SpincastUtilsDefault.class, classes.iterator().next());

        Set<Class<? extends SpincastDictionary>> classes2 =
                guiceModuleUtils.getBoundClassesExtending(SpincastDictionary.class);
        assertNotNull(classes2);
        assertEquals(1, classes2.size());
        assertEquals(SpincastDictionaryDefault.class, classes2.iterator().next());
    }

    @Test
    public void getBindingTargetClass() throws Exception {

        Module module = new SpincastGuiceModuleBase() {

            @Override
            protected void configure() {
                bind(SpincastUtils.class).to(SpincastUtilsDefault.class).in(Scopes.SINGLETON);
            }
        };

        GuiceModuleUtils guiceModuleUtils = new GuiceModuleUtils(module);

        Class<? extends SpincastUtils> clazz = guiceModuleUtils.getBindingTarget(SpincastUtils.class);
        assertNotNull(clazz);
        assertEquals(SpincastUtilsDefault.class, clazz);
    }

    @Test
    public void getBindingTargetKey() throws Exception {

        Module module = new SpincastGuiceModuleBase() {

            @Override
            protected void configure() {
                bind(SpincastUtils.class).to(SpincastUtilsDefault.class).in(Scopes.SINGLETON);
            }
        };

        GuiceModuleUtils guiceModuleUtils = new GuiceModuleUtils(module);

        Class<? extends SpincastUtils> clazz = guiceModuleUtils.getBindingTarget(Key.get(SpincastUtils.class));
        assertNotNull(clazz);
        assertEquals(SpincastUtilsDefault.class, clazz);
    }

    @Test
    public void getBindingTargetIndirectIsInvalid() throws Exception {

        Module module = new SpincastGuiceModuleBase() {

            @Override
            protected void configure() {
                bind(SpincastUtilsTesting.class).to(SpincastUtilsTestingDefault.class).in(Scopes.SINGLETON);
            }
        };

        GuiceModuleUtils guiceModuleUtils = new GuiceModuleUtils(module);

        Class<? extends SpincastUtils> clazz = guiceModuleUtils.getBindingTarget(SpincastUtils.class);
        assertNull(clazz);
    }

    public void isKeyBound() throws Exception {

        Module module = new SpincastGuiceModuleBase() {

            @Override
            protected void configure() {
                bind(SpincastUtils.class).to(SpincastUtilsDefault.class).in(Scopes.SINGLETON);
            }
        };

        GuiceModuleUtils guiceModuleUtils = new GuiceModuleUtils(module);

        boolean keyBound = guiceModuleUtils.isKeyBound(SpincastUtils.class);
        assertTrue(keyBound);

        keyBound = guiceModuleUtils.isKeyBound(SpincastDictionary.class);
        assertFalse(keyBound);
    }

    public void isKeyBoundIndirectInvalid() throws Exception {

        Module module = new SpincastGuiceModuleBase() {

            @Override
            protected void configure() {
                bind(SpincastUtilsTesting.class).to(SpincastUtilsTestingDefault.class).in(Scopes.SINGLETON);
            }
        };

        GuiceModuleUtils guiceModuleUtils = new GuiceModuleUtils(module);

        boolean keyBound = guiceModuleUtils.isKeyBound(SpincastUtils.class);
        assertFalse(keyBound);
    }

}
