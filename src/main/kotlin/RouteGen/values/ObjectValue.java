package cz.vasabi.boikend.RouteGen.values;

import cz.vasabi.boikend.RouteGen.ValueType;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.util.Map;

@Data
@Getter
@Setter
@AllArgsConstructor
public class ObjectValue implements Value {
    public Class<?> clazz;
    public String name;
    public Map<String, Value> fields;

    @Override
    public ValueType getType() {
        return ValueType.Object;
    }
}
