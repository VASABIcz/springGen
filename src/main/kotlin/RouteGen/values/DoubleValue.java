package RouteGen.values;

import RouteGen.ValueType;

public class DoubleValue implements Value {
    @Override
    public ValueType getType() {
        return ValueType.Double;
    }
}
