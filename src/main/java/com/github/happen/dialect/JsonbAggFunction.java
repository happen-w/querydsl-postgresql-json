package com.github.happen.dialect;

import org.hibernate.query.sqm.function.AbstractSqmSelfRenderingFunctionDescriptor;
import org.hibernate.query.sqm.function.FunctionKind;
import org.hibernate.query.sqm.produce.function.ArgumentTypesValidator;
import org.hibernate.query.sqm.produce.function.FunctionParameterType;
import org.hibernate.query.sqm.produce.function.StandardArgumentsValidators;
import org.hibernate.query.sqm.produce.function.StandardFunctionReturnTypeResolvers;
import org.hibernate.sql.ast.SqlAstTranslator;
import org.hibernate.sql.ast.spi.SqlAppender;
import org.hibernate.sql.ast.tree.SqlAstNode;
import org.hibernate.type.StandardBasicTypes;
import org.hibernate.type.spi.TypeConfiguration;

import java.util.List;

/**
 * Author: Happen
 * Date: 2023/8/11 9:45
 **/
public class JsonbAggFunction extends AbstractSqmSelfRenderingFunctionDescriptor {



    public JsonbAggFunction(TypeConfiguration typeConfiguration) {
        super("jsonb_agg",
                FunctionKind.AGGREGATE,
                new ArgumentTypesValidator(StandardArgumentsValidators.exactly(1), FunctionParameterType.ANY),
                StandardFunctionReturnTypeResolvers.invariant(
                        typeConfiguration.getBasicTypeRegistry().resolve(StandardBasicTypes.STRING)),
                null);
    }


    @Override
    public void render(SqlAppender sqlAppender, List<? extends SqlAstNode> sqlAstArguments, SqlAstTranslator<?> walker) {
        sqlAppender.append("jsonb_agg(");
        for (SqlAstNode sqlAstArgument : sqlAstArguments) {
            sqlAstArgument.accept(walker);
        }
        sqlAppender.append(")");
    }
}
