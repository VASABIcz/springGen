package RouteGen.values;


import RouteGen.ValueType;

public class LongValue implements Value {
    @Override
    public ValueType getType() {
        return ValueType.Long;
    }
}
