package org.spincast.core.filters;

/**
 * Filter to validate if a request contains valid cors
 * headers and, if so, add the required headers as a
 * response.
 */
public interface CorsFilter {

    /**
     * Apply the cors filter and return the result.
     */
    public CorsFilterResponse apply(CorsFilterClient corsFilterClient);

}
