package org.spincast.plugins.openapi.bottomup.utils;

import java.util.List;

import io.swagger.v3.oas.annotations.ExternalDocumentation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.Parameters;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Schema;

public interface SwaggerAnnotationsCreator {

    public Parameters createParametersAnnotation(List<Parameter> parameterAnnotationsList);

    public Parameter createPathParameterAnnotation(String paramName, String description, String pattern);

    public Schema createSchemaAnnotation(String description, String pattern);

    public ArraySchema createArraySchemaAnnotation();

    public ExternalDocumentation createExternalDocumentationAnnotation();
}
