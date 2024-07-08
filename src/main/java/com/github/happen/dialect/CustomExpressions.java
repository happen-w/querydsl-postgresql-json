package com.github.happen.dialect;

import com.querydsl.core.types.Expression;
import com.querydsl.core.types.dsl.Expressions;
import com.querydsl.core.types.dsl.StringOperation;
import com.querydsl.core.types.dsl.StringTemplate;

import java.util.ArrayList;
import java.util.List;

/**
 * Author: Happen
 * Date: 2023/8/14 10:31
 **/
public class CustomExpressions {

    private static final String JSON_BUILD_FUNCTION = "jsonBuild(%s)";
    private static final String JSON_AGG_FUNCTION = "jsonb_agg(%s)";

    public static StringTemplate jsonBuildTemplate(Expression<?>... args) {
        List<String> part = new ArrayList<>();
        List<Object> param = new ArrayList<>();
        for (Expression<?> arg : args) {
            if (arg instanceof StringOperation stringOperation) {   //使用了别名
                part.add(String.format("'%s', {%d} ", stringOperation.getArg(1), param.size()));
                param.add(stringOperation.getArg(0));
            } else {      // 不使用别名
                part.add(String.format("{%d}", param.size()));
                param.add(arg);
            }
        }
        return Expressions.stringTemplate(String.format(JSON_BUILD_FUNCTION, String.join(",", part)), param);
    }


    public static StringTemplate jsonbAggTemplate(Expression<?> expression) {
        return Expressions.stringTemplate(String.format(JSON_AGG_FUNCTION, expression));
    }
}
