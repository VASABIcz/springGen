package RouteGen;

import RouteGen.values.*;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;


@Data
@AllArgsConstructor
public class ValueObject {
    GenericType clazz;
    Value value;
    public static List<ParseType> parsers = new ArrayList<>();

    static {
        parsers.add((a) -> {
            var generics = a.generics;
            var papa = a.papa;
            System.out.println(papa.getTypeName());
            if (papa.getTypeName() == "void") {
                return new VoidValue();
            }
            var typeName = papa.getTypeName();
            if (typeName.contains(".")) {
                typeName = typeName.split("\\.")[2];
            }

            switch (typeName) {
                case "List" -> {
                    return new ArrayValue(Objects.requireNonNull(parseType(new GenericType(generics[0]))));
                }
                case "String", "UUID", "string" -> {
                    return new StringValue();
                }
                case "Map", "HashMap" -> {
                    return new MapValue(Objects.requireNonNull(parseType(new GenericType(generics[0]))), Objects.requireNonNull(parseType(new GenericType(generics[1]))));
                }
                case "Integer", "Long" -> {
                    return new LongValue();
                }
                case "Float", "Double" -> {
                    return new DoubleValue();
                }
                case "Boolean" -> {
                    return new BoolValue();
                }
            }

            return null;
        });

        parsers.add((a) -> {
            var clazz = (Class<?>)a.papa;
            HashMap<String, Value> fields = new HashMap<>();
            var name = clazz.getSimpleName();
            for (var field : clazz.getDeclaredFields()) {
                System.out.println(field.getName());
                if (field.isAnnotationPresent(JsonIgnore.class))
                    continue;

                var t = parseType(new GenericType(field.getGenericType()));
                fields.put(field.getName(), t);
            }

            return new ObjectValue(clazz, name, fields);
        });
    }

    public static Value parseType(GenericType type) {
        for (var parser : parsers) {
            var res = parser.parse(type);
            if (res != null) {
                return res;
            }
        }
        System.err.println("failed to parse type "+type);

        return null;
    }
    public static ValueObject build(GenericType type) {
        var t = parseType(type);

        return new ValueObject(type, t);
    }
}
