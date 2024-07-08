package com.github.happen.dialect;

import org.hibernate.boot.model.FunctionContributions;
import org.hibernate.dialect.PostgreSQLDialect;

/**
 * Author: Happen
 * Date: 2023/8/11 9:44
 **/
public class CustomPostgreSQLDialect extends PostgreSQLDialect {

    @Override
    public void initializeFunctionRegistry(FunctionContributions functionContributions) {
        super.initializeFunctionRegistry(functionContributions);


        functionContributions.getFunctionRegistry().register(
                "jsonb_agg",
                new JsonbAggFunction(functionContributions.getTypeConfiguration())
        );

        functionContributions.getFunctionRegistry().register(
                "jsonBuild",
                new JsonBuildFunction(functionContributions.getTypeConfiguration())
        );
    }
}
