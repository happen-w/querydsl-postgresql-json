package com.github.happen.dialect;

import org.hibernate.metamodel.mapping.AttributeMappingsList;
import org.hibernate.metamodel.mapping.EntityMappingType;
import org.hibernate.metamodel.mapping.ModelPartContainer;
import org.hibernate.metamodel.mapping.internal.BasicAttributeMapping;
import org.hibernate.query.sqm.function.AbstractSqmSelfRenderingFunctionDescriptor;
import org.hibernate.query.sqm.function.FunctionKind;
import org.hibernate.query.sqm.produce.function.StandardFunctionReturnTypeResolvers;
import org.hibernate.query.sqm.sql.internal.BasicValuedPathInterpretation;
import org.hibernate.query.sqm.sql.internal.EntityValuedPathInterpretation;
import org.hibernate.sql.ast.SqlAstTranslator;
import org.hibernate.sql.ast.spi.SqlAppender;
import org.hibernate.sql.ast.tree.SqlAstNode;
import org.hibernate.sql.ast.tree.expression.QueryLiteral;
import org.hibernate.type.BasicType;
import org.hibernate.type.StandardBasicTypes;
import org.hibernate.type.spi.TypeConfiguration;

import java.util.List;

/**
 * Author: Happen
 * Date: 2023/8/11 9:45
 **/
public class JsonBuildFunction extends AbstractSqmSelfRenderingFunctionDescriptor {


    private BasicType<String> stringType;

    public JsonBuildFunction(TypeConfiguration typeConfiguration) {
        super("jsonBuild",
                FunctionKind.AGGREGATE,
                null,
                StandardFunctionReturnTypeResolvers.invariant(
                        typeConfiguration.getBasicTypeRegistry().resolve(StandardBasicTypes.STRING)),
                null);
        this.stringType = typeConfiguration.getBasicTypeRegistry().resolve(StandardBasicTypes.STRING);
    }


    @Override
    public void render(SqlAppender sqlAppender, List<? extends SqlAstNode> sqlAstArguments, SqlAstTranslator<?> walker) {
        sqlAppender.append("json_build_object(");
        SqlAstNode sqlAstNode = sqlAstArguments.get(0);
        if (sqlAstNode instanceof EntityValuedPathInterpretation<?> entityValue) {
            String qualifier = entityValue.getSqlExpression().getColumnReference().getQualifier();
            ModelPartContainer modelPart = entityValue.getTableGroup().getModelPart();
            if (modelPart instanceof EntityMappingType mapping) {
                AttributeMappingsList attributeMappings = mapping.getAttributeMappings();
                for(int i = 0; i < attributeMappings.size(); i++){
                    if (attributeMappings.get(i) instanceof BasicAttributeMapping basicAttribute) {
                        new QueryLiteral<>(basicAttribute.getAttributeName(), stringType).accept(walker);
                        sqlAppender.append(",");
                        String value = qualifier + "." + basicAttribute.getSelectableName();
                        sqlAppender.append(value);
                        if(i != attributeMappings.size() - 1){
                            sqlAppender.append(",");
                        }
                    }
                }
            }
        } else {
            boolean hasAlias = false;  // 记录是否有别名
            for(int i = 0; i < sqlAstArguments.size(); i++){
                SqlAstNode sqlAstArgument = sqlAstArguments.get(i);
                if (sqlAstArgument instanceof QueryLiteral) {
                    sqlAstArgument.accept(walker);
                    hasAlias = true;
                }
                if (sqlAstArgument instanceof BasicValuedPathInterpretation<?> basicValue) {
                    if (hasAlias) {
                        hasAlias = false;  // 重置为没有别名
                    }else {                 // 没有别名的话，使用实体字段名
                        new QueryLiteral<>(basicValue.getExpressionType().getPartName(), stringType).accept(walker);
                        sqlAppender.append(",");
                    }
                    basicValue.accept(walker);
                }
                if(i != sqlAstArguments.size() - 1){
                    sqlAppender.append(",");
                }
            }
        }
        sqlAppender.append(")");
    }
}
