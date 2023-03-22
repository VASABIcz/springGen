package cz.vasabi.boikend.RouteGen

import cz.vasabi.boikend.RouteGen.values.ArrayValue
import cz.vasabi.boikend.RouteGen.values.MapValue
import cz.vasabi.boikend.RouteGen.values.ObjectValue
import cz.vasabi.boikend.RouteGen.values.Value
import cz.vasabi.boikend.RouteGen.values.VoidValue
import cz.vasabi.boikend.entities.UserToken
import org.springframework.http.HttpMethod

val generatedClasses = mutableSetOf<String>()
interface RequestFactory {
    fun newRequest(): RequestBuilder
}
interface RequestBuilder {
    fun <T> setBody(body: T): RequestBuilder
    fun <T> setQuery(name: String, value: T): RequestBuilder
    fun <T> setPathVariable(name: String, value: T): RequestBuilder
    fun setMethod(method: HttpMethod): RequestBuilder
    fun setHost(host: String): RequestBuilder
    fun setSecure(secure: Boolean): RequestBuilder
    fun setRoute(path: String): RequestBuilder
    fun send(): RequestResponse
}

interface RequestResponse {
    fun code(): Int
    fun <T> getResponse(cls: Class<T>): T
}

data class AuthRequest(val password: String, val email: String)


class AuthAPI(private val _client: RequestFactory, private val _host: String, private val _secure: Boolean, private val _path: String = "/auth") {
    fun signUpPost(reqBody: AuthRequest): UserToken? {
        return try {
            _client
                .newRequest()
                .setHost(_host)
                .setRoute("$_path/signup")
                .setSecure(_secure)
                .setMethod(HttpMethod.POST)
                .setBody(reqBody)
                .send()
                .getResponse(UserToken::class.java)
        }
        catch (t: Throwable) {
            null
        }
    }
}

fun buildClass(clazz: ObjectValue): String? {
    if (generatedClasses.contains(clazz.name)) return null
    generatedClasses.add(clazz.name)
    val fields = hashMapOf<String, String>();
    clazz.fields.forEach { k, v ->
        fields.put(k, valueTypeToString(v)!!)
    }
    val args = fields.map { k ->
        "val ${k.key}: ${k.value}"
    }.joinToString(", ")

    return "data class ${clazz.name}($args)"
}

fun valueTypeToString(type: Value): String? {
    return when (type.type) {
        ValueType.String -> "String"
        ValueType.Array -> {
            val av = type as ArrayValue
            "List<${valueTypeToString(av.inner)}>"
        }
        ValueType.Map -> {
            val av = type as MapValue
            "Map<${valueTypeToString(av.key)}, ${valueTypeToString(av.value)}>"
        }
        ValueType.Double -> {
            "Double"
        }
        ValueType.Long -> {
            "Long"
        }
        ValueType.Bool -> {
            "Boolean"
        }
        ValueType.Void -> {
            null
        }
        ValueType.Object -> {
            val av = type as ObjectValue
            av.name
        }
    }
}

fun buildReturnEndpoint(endpoint: Endpoint): String {
    val args = mutableListOf<String>()

    val query = endpoint.queryParameters.map {
        "${it.key}QUERY: ${valueTypeToString(it.value.value)}"
    }
    args.addAll(query)
    val path = endpoint.pathParameters.map {
        "${it.key}PATH: ${valueTypeToString(it.value.value)}"
    }
    args.addAll(path)
    if (endpoint.requestBody != null) {
        args.add("REQUESTBODY: ${valueTypeToString(endpoint.requestBody.value)}")
    }
    val retV = valueTypeToString(endpoint.returnType.value)
    val head = "fun ${endpoint.name}${endpoint.method.first()}(${args.joinToString(", ")}): $retV?"

    // FIXME
    var body = "_client.newRequest().setHost(_host).setSecure(_secure).setMethod(HttpMethod.${endpoint.method.first()}).setRoute(\"\$_path${endpoint.path}\")"
    /*
    fun <T> setBody(body: T): RequestBuilder
    fun <T> setQuery(name: String, value: T): RequestBuilder
    fun <T> setPathVariable(name: String, value: T): RequestBuilder
     */

    endpoint.pathParameters.forEach {
        body += ".setBody(\"${valueTypeToString(it.value.value)}\", ${it.key}PATH)"
    }
    endpoint.queryParameters.forEach {
        body += ".setQuery(\"${valueTypeToString(it.value.value)}\", ${it.key}QUERY)"
    }
    if (endpoint.requestBody != null) {
        body += ".setBody(REQUESTBODY)"
    }
    body += ".send()"
    body += ".getResponse(${retV}::class.java)"
    return "$head{return try {$body} catch(_t: Throwable) {_t.printStackTrace(); null}}"
}

fun buildNonReturnEndpoint(endpoint: Endpoint): String {
    val args = mutableListOf<String>()

    val query = endpoint.queryParameters.map {
        "${it.key}QUERY: ${valueTypeToString(it.value.value)}"
    }
    args.addAll(query)
    val path = endpoint.pathParameters.map {
        "${it.key}PATH: ${valueTypeToString(it.value.value)}"
    }
    args.addAll(path)
    if (endpoint.requestBody != null) {
        args.add("REQUESTBODY: ${valueTypeToString(endpoint.requestBody.value)}")
    }
    val head = "fun ${endpoint.name}${endpoint.method.first()}(${args.joinToString(", ")})"

    // FIXME
    var body = "_client.newRequest().setHost(_host).setSecure(_secure).setMethod(HttpMethod.${endpoint.method.first()}).setRoute(\"\$_path${endpoint.path}\")"
    /*
    fun <T> setBody(body: T): RequestBuilder
    fun <T> setQuery(name: String, value: T): RequestBuilder
    fun <T> setPathVariable(name: String, value: T): RequestBuilder
     */

    endpoint.pathParameters.forEach {
        body += ".setBody(\"${valueTypeToString(it.value.value)}\", ${it.key}PATH)"
    }
    endpoint.queryParameters.forEach {
        body += ".setQuery(\"${valueTypeToString(it.value.value)}\", ${it.key}QUERY)"
    }
    if (endpoint.requestBody != null) {
        body += ".setBody(REQUESTBODY)"
    }
    body += ".send()"
    return "$head{try {$body} catch(_t: Throwable) {_t.printStackTrace()}}"
}
fun buildGroup(group: RequestGroup): String {
    val head = "class ${group.name.split(".").last()}API(private val _client: RequestFactory, private val _host: String, private val _secure: Boolean, private val _path: String = \"${group.path}\")"
    var body = ""
    group.endpoints.values.forEach {
        if (it.returnType.value.type == ValueType.Void) {
            body += buildNonReturnEndpoint(it)
        }
        else {
            body += buildReturnEndpoint(it)
        }
    }

    return "$head{$body}"
}
fun generate(group: RequestGroup): String {
    println("=================")
    group.endpoints.values.forEach {
        if (it.returnType.value is ObjectValue) {
            val cls = buildClass(it.returnType.value as ObjectValue) ?: return@forEach
            println(cls)
        }
        it.pathParameters.forEach {
            val cls = buildClass(it.value as ObjectValue) ?: return@forEach
            println(cls)
        }
        if (it.requestBody != null) {
            val cls = buildClass(it.requestBody.value as ObjectValue)
            println(cls)
        }
        it.queryParameters.forEach {
            val cls = buildClass(it.value as ObjectValue) ?: return@forEach
            println(cls)
        }
    }
    val r = buildGroup(group)
    println(r)
    return ""
}

data class UserToken(val user: String,val token: String)
data class UserAccount(val id: Long,val email: String)