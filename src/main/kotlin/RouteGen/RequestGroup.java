package cz.vasabi.boikend.RouteGen;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NonNull;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;


@Data
@AllArgsConstructor
public class RequestGroup {
    String name;
    String path;
    Class<?> clazz;
    Map<String, Endpoint> endpoints;

    public static Set<String> ignoredMethodNames = Set.of("notify", "notifyAll", "hashCode", "getClass", "toString", "equals", "wait");

    public static RequestGroup build(@NonNull Class<?> clazz) {
        var name = clazz.getName();
        String path = null;
        var pathA = clazz.getAnnotation(RequestMapping.class);
        if (pathA != null && pathA.value().length != 0) {
            path = pathA.value()[0];
        }
        var endpoints = new HashMap<String, Endpoint>();

        for (var m : clazz.getMethods()) {
            if (m.getAnnotation(IgnoreRoute.class) != null)
                continue;
            if (ignoredMethodNames.contains(m.getName()))
                continue;

            System.out.println("gona build endpoint "+m);
            var e = Endpoint.build(m);
            if (e == null) {
                System.out.println("failed to parse method " + m + "in class " + clazz);
                continue;
            }
            endpoints.put(m.getName(), e);
        }

        return new RequestGroup(name, path, clazz, endpoints);
    }
}
