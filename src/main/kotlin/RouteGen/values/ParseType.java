package cz.vasabi.boikend.RouteGen.values;

import cz.vasabi.boikend.RouteGen.GenericType;
import cz.vasabi.boikend.RouteGen.values.BoolValue;

import java.lang.reflect.ParameterizedType;

public interface ParseType {
    Value parse(GenericType type);
}
