package org.spincast.plugins.openapi.bottomup.utils;

import java.lang.annotation.Annotation;
import java.util.List;

import io.swagger.v3.oas.annotations.ExternalDocumentation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.Parameters;
import io.swagger.v3.oas.annotations.enums.Explode;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.enums.ParameterStyle;
import io.swagger.v3.oas.annotations.extensions.Extension;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.DiscriminatorMapping;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;

public class SwaggerAnnotationsCreatorDefault implements SwaggerAnnotationsCreator {

    @Override
    public Parameters createParametersAnnotation(List<Parameter> parameterAnnotationsList) {

        return new Parameters() {

            @Override
            public Class<? extends Annotation> annotationType() {
                return Parameters.class;
            }

            @Override
            public Parameter[] value() {
                Parameter[] params = parameterAnnotationsList.toArray(new Parameter[parameterAnnotationsList.size()]);
                return params;
            }
        };
    }

    @Override
    public Parameter createPathParameterAnnotation(String paramName, String description, String pattern) {

        Parameter param = new Parameter() {

            @Override
            public Class<? extends Annotation> annotationType() {
                return Parameter.class;
            }

            @Override
            public String name() {
                return paramName;
            }

            @Override
            public ParameterStyle style() {
                return ParameterStyle.DEFAULT;
            }

            @Override
            public Schema schema() {
                return createSchemaAnnotation(description, pattern);
            }

            @Override
            public boolean required() {
                return true;
            }

            @Override
            public String ref() {
                return "";
            }

            @Override
            public ParameterIn in() {
                return ParameterIn.PATH;
            }

            @Override
            public boolean hidden() {
                return false;
            }

            @Override
            public Extension[] extensions() {
                return new Extension[0];
            }

            @Override
            public Explode explode() {
                return Explode.DEFAULT;
            }

            @Override
            public ExampleObject[] examples() {
                return new ExampleObject[0];
            }

            @Override
            public String example() {
                return "";
            }

            @Override
            public String description() {
                return "";
            }

            @Override
            public boolean deprecated() {
                return false;
            }

            @Override
            public Content[] content() {
                return new Content[0];
            }

            @Override
            public ArraySchema array() {
                return createArraySchemaAnnotation();
            }

            @Override
            public boolean allowReserved() {
                return false;
            }

            @Override
            public boolean allowEmptyValue() {
                return false;
            }
        };

        return param;
    }

    @Override
    public ArraySchema createArraySchemaAnnotation() {
        return new ArraySchema() {

            @Override
            public Class<? extends Annotation> annotationType() {
                return ArraySchema.class;
            }

            @Override
            public boolean uniqueItems() {
                return false;
            }

            @Override
            public Schema schema() {
                return createSchemaAnnotation("", "");
            }

            @Override
            public int minItems() {
                return Integer.MAX_VALUE;
            }

            @Override
            public int maxItems() {
                return Integer.MIN_VALUE;
            }

            @Override
            public Extension[] extensions() {
                return new Extension[0];
            }

            @Override
            public Schema arraySchema() {
                return createSchemaAnnotation("", "");
            }
        };
    }

    @Override
    public Schema createSchemaAnnotation(String description, String pattern) {

        final String descriptionFinal = description != null ? description : "";
        final String patternFinal = pattern != null ? pattern : "";

        return new Schema() {

            @Override
            public Class<? extends Annotation> annotationType() {
                return Schema.class;
            }

            @Override
            public Class<?> implementation() {
                return Void.class;
            }

            @Override
            public Class<?> not() {
                return Void.class;
            }

            @Override
            public Class<?>[] oneOf() {
                return new Class<?>[0];
            }

            @Override
            public Class<?>[] anyOf() {
                return new Class<?>[0];
            }

            @Override
            public Class<?>[] allOf() {
                return new Class<?>[0];
            }

            @Override
            public String name() {
                return "";
            }

            @Override
            public String title() {
                return "";
            }

            @Override
            public double multipleOf() {
                return 0;
            }

            @Override
            public String maximum() {
                return "";
            }

            @Override
            public boolean exclusiveMaximum() {
                return false;
            }

            @Override
            public String minimum() {
                return "";
            }

            @Override
            public boolean exclusiveMinimum() {
                return false;
            }

            @Override
            public int maxLength() {
                return Integer.MAX_VALUE;
            }

            @Override
            public int minLength() {
                return 0;
            }

            @Override
            public String pattern() {
                return "";
            }

            @Override
            public int maxProperties() {
                return 0;
            }

            @Override
            public int minProperties() {
                return 0;
            }

            @Override
            public String[] requiredProperties() {
                return new String[0];
            }

            @Override
            public boolean required() {
                return false;
            }

            @Override
            public String description() {
                return descriptionFinal;
            }

            @Override
            public String format() {
                return patternFinal;
            }

            @Override
            public String ref() {
                return "";
            }

            @Override
            public boolean nullable() {
                return false;
            }

            @Override
            public boolean readOnly() {
                return false;
            }

            @Override
            public boolean writeOnly() {
                return false;
            }

            @Override
            public AccessMode accessMode() {
                return AccessMode.AUTO;
            }

            @Override
            public String example() {
                return "";
            }

            @Override
            public ExternalDocumentation externalDocs() {
                return createExternalDocumentationAnnotation();
            }

            @Override
            public boolean deprecated() {
                return false;
            }

            @Override
            public String type() {
                return "";
            }

            @Override
            public String[] allowableValues() {
                return new String[0];
            }

            @Override
            public String defaultValue() {
                return "";
            }

            @Override
            public String discriminatorProperty() {
                return "";
            }

            @Override
            public DiscriminatorMapping[] discriminatorMapping() {
                return new DiscriminatorMapping[0];
            }

            @Override
            public boolean hidden() {
                return false;
            }

            @Override
            public Class<?>[] subTypes() {
                return new Class<?>[0];
            }

            @Override
            public Extension[] extensions() {
                return new Extension[0];
            }
        };
    }

    @Override
    public ExternalDocumentation createExternalDocumentationAnnotation() {
        return new ExternalDocumentation() {

            @Override
            public Class<? extends Annotation> annotationType() {
                return ExternalDocumentation.class;
            }

            @Override
            public String url() {
                return "";
            }

            @Override
            public Extension[] extensions() {
                return new Extension[0];
            }

            @Override
            public String description() {
                return "";
            }
        };
    }

}
