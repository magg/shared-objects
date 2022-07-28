
package com.magg.common.api;

import java.util.List;

/**
 * Class Description goes here.
 */
public class FilterComparison
{
    /**
     * Class Description goes here.
     * Created by jkida on 4/30/20
     */
    public enum Operator {
        EQ,
        GT,
    }

    private final String field;
    private final Operator operator;
    private final List<String> value;
    private boolean otherSide;
    private boolean aBoolean;

    public FilterComparison(String field, Operator operator, List<String> value, boolean aBoolean) {
        this.field = field;
        this.operator = operator;
        this.value = value;
        this.aBoolean = aBoolean;
    }

    public FilterComparison(String field, List<String> value) {
        this(field, Operator.EQ, value, false);
    }

    public FilterComparison(String field, String value) {
        this(field, Operator.EQ, List.of(value), false);
    }

    public FilterComparison(String field, String value, boolean isBoolean) {
        this(field, Operator.EQ, List.of(value), isBoolean);
    }


    public String getField() {
        return field;
    }

    public Operator getOperator() {
        return operator;
    }

    public List<String> getValue() {
        return value;
    }


    public boolean isOtherSide()
    {
        return otherSide;
    }


    public void setOtherSide(boolean otherSide)
    {
        this.otherSide = otherSide;
    }


    public boolean isaBoolean()
    {
        return aBoolean;
    }


    public void setaBoolean(boolean aBoolean)
    {
        this.aBoolean = aBoolean;
    }
}
