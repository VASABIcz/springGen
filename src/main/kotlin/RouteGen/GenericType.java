package RouteGen;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NonNull;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;

@Data
@AllArgsConstructor
public class GenericType {
    Type[] generics;
    Type papa;

    GenericType(@NonNull ParameterizedType type) {
        generics = type.getActualTypeArguments();
        papa = type.getRawType();
    }
    GenericType(@NonNull Type type) {
        try {
            var t = (ParameterizedType)type;

            generics = t.getActualTypeArguments();
            papa = t.getRawType();
            return;
        }
        catch (Exception e) {}

        generics = new Type[0];
        papa = type;
    }
}
