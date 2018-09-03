package org.spincast.plugins.attemptslimiter;

/**
 * A criteria for which a maximum of attempts at
 * an action may have been reached.
 * <p>
 * For example: "<code>ip=123.1.2.3</code>" or
 * "<code>userId=Stromgol123</code>"
 */
public class AttemptCriteria {

    private final String name;
    private final String value;

    public AttemptCriteria(String criteriaName, String value) {
        this.name = criteriaName;
        this.value = value;
    }

    public static AttemptCriteria of(String criteriaName, String value) {
        return new AttemptCriteria(criteriaName, value);
    }

    public String getName() {
        return this.name;
    }

    public String getValue() {
        return this.value;
    }

    @Override
    public String toString() {
        return getName() + " - " + getValue();
    }

}
