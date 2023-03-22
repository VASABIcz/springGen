package cz.vasabi.boikend.RouteGen.values;

import cz.vasabi.boikend.RouteGen.ValueType;

public class DoubleValue implements Value {
    @Override
    public ValueType getType() {
        return ValueType.Double;
    }
}
