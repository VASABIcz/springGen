package RouteGen.values;

import RouteGen.ValueType;

public class StringValue implements Value {
    @Override
    public ValueType getType() {
        return ValueType.String;
    }
}
