package org.spincast.core.guice;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

import org.aopalliance.intercept.MethodInterceptor;
import org.aopalliance.intercept.MethodInvocation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.spincast.core.utils.SpincastStatics;

import com.google.inject.Binding;
import com.google.inject.Inject;
import com.google.inject.Injector;
import com.google.inject.Key;
import com.google.inject.Module;
import com.google.inject.Provider;
import com.google.inject.matcher.AbstractMatcher;
import com.google.inject.matcher.Matcher;
import com.google.inject.matcher.Matchers;
import com.google.inject.spi.DefaultBindingTargetVisitor;
import com.google.inject.spi.DefaultElementVisitor;
import com.google.inject.spi.Element;
import com.google.inject.spi.Elements;
import com.google.inject.spi.InstanceBinding;
import com.google.inject.spi.LinkedKeyBinding;
import com.google.inject.spi.UntargettedBinding;
import com.google.inject.util.Modules;

/**
 * Utilities to inspect/manipulate
 * Guice modules.
 */
public class GuiceModuleUtils {

    protected final static Logger logger = LoggerFactory.getLogger(GuiceModuleUtils.class);

    private Module module;
    private List<Element> elements;

    public GuiceModuleUtils(Module module) {
        Objects.requireNonNull(module, "The module can't be NULL");
        this.module = module;
    }

    public GuiceModuleUtils(Set<Module> modules) {
        this(Modules.combine(modules));
    }

    public GuiceModuleUtils(Module... modules) {
        this(Modules.combine(modules));
    }

    protected Module getModule() {
        return this.module;
    }

    protected List<Element> getElements() {
        if (this.elements == null) {
            this.elements = Elements.getElements(this.module);
        }
        return this.elements;
    }

    /**
     * Checks if a class is bound in the Module
     */
    public boolean isKeyBound(Class<?> clazz) {
        return isKeyBound(Key.get(clazz));
    }

    /**
     * Checks if a key is bound in the Module
     */
    public boolean isKeyBound(final Key<?> keyToCheck) {

        final boolean[] keyFound = new boolean[]{false};
        for (Element element : getElements()) {

            if (keyFound[0]) {
                break;
            }

            element.acceptVisitor(new DefaultElementVisitor<Void>() {

                @Override
                public <T> Void visit(Binding<T> binding) {

                    Key<T> key = binding.getKey();
                    if (key.equals(keyToCheck)) {
                        keyFound[0] = true;
                    }

                    return null;
                }
            });
        }

        return keyFound[0];
    }

    /**
     * Returns the target of the binding with the specified key.
     * This won't work if a {@link Provider} is used!
     * 
     * @return the class or <code>null</code> if not found.
     */
    public <T> Class<? extends T> getBindingTarget(final Key<T> specificKey) {
        Objects.requireNonNull(specificKey, "The key can't be NULL");
        Set<Class<? extends T>> boundClasses = getBoundClasses(null, specificKey);
        if (boundClasses == null || boundClasses.size() == 0) {
            return null;
        }
        return boundClasses.iterator().next();
    }

    /**
     * Returns the target of the binding with the specified class/interface.
     * This won't work if a {@link Provider} is used!
     * 
     * @return the class or <code>null</code> if not found.
     */
    public <T> Class<? extends T> getBindingTarget(final Class<T> specificClass) {
        return getBindingTarget(Key.get(specificClass));
    }

    /**
     * Returns the bound classes that extend or implement the specified 
     * <code>parentType</code>. 
    * <p>
     * IMPORTANT : This doesn't mean that the <code>parentType</code>
     * is actually used <em>as a key</em> in a binding, but that there is a binding for an
     * class that extends/implements <code>parentType</code>.
     * <p>
     * This won't work if a {@link Provider} is used!
     */
    public <T> Set<Class<? extends T>> getBoundClassesExtending(final Class<? extends T> parentType) {
        Objects.requireNonNull(parentType, "The parent type can't be NULL");
        return getBoundClasses(parentType, null);
    }

    protected <T> Set<Class<? extends T>> getBoundClasses(final Class<? extends T> parentType,
                                                          final Key<?> specificKey) {

        final Set<Class<? extends T>> bindings = new HashSet<Class<? extends T>>();

        for (Element element : getElements()) {

            element.acceptVisitor(new DefaultElementVisitor<Void>() {

                @Override
                public <B> Void visit(Binding<B> binding) {

                    binding.acceptTargetVisitor(new DefaultBindingTargetVisitor<B, Void>() {

                        @Override
                        public Void visit(InstanceBinding<? extends B> instanceBinding) {

                            Key<? extends B> key = instanceBinding.getKey();

                            B instance = instanceBinding.getInstance();
                            if (instance instanceof Class) {
                                Class<?> bindingClass = (Class<?>)instance;

                                if (specificKey != null) {
                                    if (specificKey.equals(key)) {
                                        @SuppressWarnings("unchecked")
                                        Class<T> temp = (Class<T>)bindingClass;
                                        bindings.add(temp);
                                    }
                                } else if (parentType.isAssignableFrom(bindingClass)) {
                                    @SuppressWarnings("unchecked")
                                    Class<T> temp = (Class<T>)bindingClass;
                                    bindings.add(temp);
                                }
                            }

                            return null;
                        }

                        @Override
                        public Void visit(UntargettedBinding<? extends B> untargettedBinding) {

                            Key<? extends B> key = untargettedBinding.getKey();
                            Class<?> keyClass = key.getTypeLiteral().getRawType();

                            if (specificKey != null) {
                                if (specificKey.equals(key)) {
                                    @SuppressWarnings("unchecked")
                                    Class<T> temp = (Class<T>)keyClass;
                                    bindings.add(temp);
                                }
                            } else if (parentType.isAssignableFrom(keyClass)) {
                                @SuppressWarnings("unchecked")
                                Class<T> temp = (Class<T>)keyClass;
                                bindings.add(temp);
                            }

                            return null;
                        }

                        @Override
                        public Void visit(LinkedKeyBinding<? extends B> linkedKeyBinding) {

                            Key<?> bindingKey = linkedKeyBinding.getKey();
                            Class<?> bindingKeyClass = bindingKey.getTypeLiteral().getRawType();

                            Key<?> linkedKey = linkedKeyBinding.getLinkedKey();
                            Class<?> linkedKeyClass = linkedKey.getTypeLiteral().getRawType();

                            if (specificKey != null) {
                                if (specificKey.equals(bindingKey)) {
                                    @SuppressWarnings("unchecked")
                                    Class<T> temp = (Class<T>)linkedKeyClass;
                                    bindings.add(temp);
                                }
                            } else if (parentType.isAssignableFrom(bindingKeyClass)) {
                                @SuppressWarnings("unchecked")
                                Class<T> temp = (Class<T>)linkedKeyClass;
                                bindings.add(temp);
                            }

                            return null;
                        }
                    });
                    return null;
                }
            });
        }

        return bindings;
    }

    /**
     * Creates a module that is going to intercept the calls to all methods defined in
     * an object implementing <code>toIntercept</code> and will call those of 
     * <code>implementationClass</code> instead, if available.
     * <p>
     * This allows you, for example, to use a specific implementation for a
     * <code>toIntercept</code> binding, even if an existing binding is done for a
     * class extending <code>toIntercept</code>. This existing binding will still
     * continue to use its original implementation for the methods that are not
     * defined on <code>toIntercept</code> but will use the implementation speficied
     * here for those that are.
     * <p>
     * Note that the <code>implementationClass</code> binding must have been done in other
     * module.
     */
    public static <T> SpincastGuiceModuleBase createInterceptorModule(final Class<T> toIntercept,
                                                                      final Class<? extends T> implementationClass) {

        Objects.requireNonNull(toIntercept, "The Class to intercept can't be NULL");
        Objects.requireNonNull(implementationClass, "The implementation class can't be NULL");

        final Map<String, Method> toInterceptMethodsMap = new HashMap<String, Method>();
        Set<Method> toInterceptMethods = SpincastStatics.getAllMethods(implementationClass);
        for (Method toInterceptMethod : toInterceptMethods) {
            toInterceptMethodsMap.put(toInterceptMethod.getName(), toInterceptMethod);
        }

        return new SpincastGuiceModuleBase() {

            @Override
            protected void configure() {

                final MethodInterceptor interceptor = new MethodInterceptor() {

                    @Inject
                    protected Provider<Injector> injector;

                    private T implementation;

                    public T getImplementation() {
                        if (this.implementation == null) {
                            this.implementation = this.injector.get().getInstance(implementationClass);
                        }
                        return this.implementation;
                    }

                    @Override
                    public Object invoke(MethodInvocation invocation) throws Throwable {

                        Method method = invocation.getMethod();

                        if (!toInterceptMethodsMap.containsKey(method.getName())) {
                            return invocation.proceed();
                        }

                        Method toInterceptMethod = toInterceptMethodsMap.get(method.getName());

                        //==========================================
                        // Required to be able to call non public methods
                        //==========================================
                        toInterceptMethod.setAccessible(true);

                        try {
                            return toInterceptMethod.invoke(getImplementation(), invocation.getArguments());
                        } catch (Exception ex) {
                            logger.error("invocation error", ex);
                            throw SpincastStatics.runtimize(ex);
                        }
                    }
                };
                requestInjection(interceptor);

                @SuppressWarnings("rawtypes")
                Matcher<Class> matcher = new AbstractMatcher<Class>() {

                    Matcher<Class> matcherToIntercept = Matchers.subclassesOf(toIntercept);
                    Matcher<Class> matcherImpl = Matchers.subclassesOf(implementationClass);

                    @Override
                    public boolean matches(Class t) {
                        return this.matcherToIntercept.matches(t) &&
                               !this.matcherImpl.matches(t);
                    }
                };

                bindInterceptor(matcher,
                                Matchers.any(),
                                interceptor);
            }
        };
    }

    /**
     * Remove bindings from a Module.
     */
    public static Module removeBindings(Module module, final Set<Key<?>> keysToRemove) {

        if (keysToRemove == null || keysToRemove.size() == 0) {
            return module;
        }

        // Default List is immutable.
        List<Element> elements = new ArrayList<>(Elements.getElements(module));

        // From http://stackoverflow.com/a/2854662/843699
        for (Iterator<Element> i = elements.iterator(); i.hasNext();) {
            Element element = i.next();

            boolean remove = element.acceptVisitor(new DefaultElementVisitor<Boolean>() {

                @Override
                public <T> Boolean visit(Binding<T> binding) {
                    return keysToRemove.contains(binding.getKey());
                }

                @Override
                public Boolean visitOther(Element other) {
                    return false;
                }
            });

            if (remove) {
                i.remove();
            }
        }

        Module newModule = Elements.getModule(elements);

        return newModule;
    }

}
