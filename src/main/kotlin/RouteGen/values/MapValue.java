package RouteGen.values;

import RouteGen.ValueType;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NonNull;

@Data
@AllArgsConstructor
public class MapValue implements Value {
    @NonNull
    public Value key;
    @NonNull
    public Value value;

    @Override
    public ValueType getType() {
        return ValueType.String;
    }
}
