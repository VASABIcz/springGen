package cz.vasabi.boikend.RouteGen.values;

import cz.vasabi.boikend.RouteGen.ValueType;

public class StringValue implements Value {
    @Override
    public ValueType getType() {
        return ValueType.String;
    }
}
