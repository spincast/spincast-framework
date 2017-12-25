package org.spincast.plugins.jdbc.statements;

import javax.annotation.Nullable;

import com.google.inject.Inject;
import com.google.inject.assistedinject.Assisted;

public class InsertResultWithGeneratedKeyDefault extends QueryResultDefault implements InsertResultWithGeneratedKey {

    private final Long generatedKey;

    @Inject
    public InsertResultWithGeneratedKeyDefault(@Assisted int queryResult,
                                               @Assisted @Nullable Long generatedKey) {
        super(queryResult);
        this.generatedKey = generatedKey;
    }

    @Override
    public Long getGeneratedKey() {
        return this.generatedKey;
    }

}
