package cz.vasabi.boikend.RouteGen;

import lombok.AllArgsConstructor;
import lombok.Data;
import org.springframework.http.HttpMethod;
import org.springframework.web.bind.annotation.*;

import java.lang.reflect.Method;
import java.util.*;

@Data
@AllArgsConstructor
public class Endpoint {
    String name;
    String path;
    Method clazzMethod;
    Set<HttpMethod> method;
    ValueObject returnType;
    ValueObject requestBody;
    Map<String, ValueObject> queryParameters;
    Map<String, ValueObject> pathParameters;

    static Endpoint build(Method method) {
        var name = method.getName();
        var httpMethods = new HashSet<HttpMethod>();
        String path = null;
        HashMap<String, ValueObject> pathParameters = new HashMap<>();
        HashMap<String, ValueObject> queryParameters = new HashMap<>();

        var annotations = method.getAnnotations();
        for (var annotation : annotations) {
            if (annotation instanceof IgnoreRoute) {
                return null;
            } else if (annotation instanceof RequestMapping an) {
                if (an.value().length != 0) {
                    path = an.value()[0];
                }

                httpMethods.addAll(Arrays.stream(an.method()).map(RequestMethod::asHttpMethod).toList());
                break;
            } else if (annotation instanceof GetMapping an) {
                if (an.value().length != 0) {
                    path = an.value()[0];
                }

                httpMethods.add(HttpMethod.GET);
                break;
            } else if (annotation instanceof PostMapping an) {
                if (an.value().length != 0) {
                    path = an.value()[0];
                }

                httpMethods.add(HttpMethod.POST);
                break;
            } else if (annotation instanceof PutMapping an) {
                if (an.value().length != 0) {
                    path = an.value()[0];
                }

                httpMethods.add(HttpMethod.PUT);
                break;
            } else if (annotation instanceof PatchMapping an) {
                if (an.value().length != 0) {
                    path = an.value()[0];
                }

                httpMethods.add(HttpMethod.PATCH);
                break;
            } else if (annotation instanceof DeleteMapping an) {
                if (an.value().length != 0) {
                    path = an.value()[0];
                }

                httpMethods.add(HttpMethod.DELETE);
                break;
            }
        }
        ValueObject req = null;
        for (var parameter : method.getParameters()) {
            for (var annotation : parameter.getAnnotations()) {
                if (annotation instanceof PathVariable an) {
                    pathParameters.put(parameter.getName(), ValueObject.build(new GenericType(parameter.getParameterizedType())));
                    break;
                } else if (annotation instanceof RequestBody an) {
                    req = ValueObject.build(new GenericType(parameter.getParameterizedType()));
                    break;
                } else if (annotation instanceof RequestParam an) {
                    queryParameters.put(parameter.getName(), ValueObject.build(new GenericType(parameter.getParameterizedType())));
                    break;
                }
            }
        }
        var ret = ValueObject.build(new GenericType(method.getGenericReturnType()));

        return new Endpoint(name, path, method, httpMethods, ret, req, queryParameters, pathParameters);
    }
}
