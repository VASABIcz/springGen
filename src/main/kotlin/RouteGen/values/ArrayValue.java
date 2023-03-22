package cz.vasabi.boikend.RouteGen.values;

import cz.vasabi.boikend.RouteGen.ValueType;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NonNull;

@Data
@AllArgsConstructor
public class ArrayValue implements Value {
    @NonNull
    public Value inner;

    @Override
    public ValueType getType() {
        return ValueType.String;
    }
}
