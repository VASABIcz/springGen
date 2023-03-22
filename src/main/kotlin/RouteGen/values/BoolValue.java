package RouteGen.values;


import RouteGen.ValueType;

public class BoolValue implements Value {
    @Override
    public ValueType getType() {
        return ValueType.Bool;
    }
}
