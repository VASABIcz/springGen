package RouteGen.values;


import RouteGen.ValueType;

public class VoidValue implements Value {
    @Override
    public ValueType getType() {
        return ValueType.Void;
    }
}
