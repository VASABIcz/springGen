
example api
```java
@Data
class AuthRequest {
    String email;
    String password;
}

@Data
public class UserToken {
    UserAccount user;
    UUID token;
}

@Data
public class UserAccount {
    Long id;
    String email;
}

@RestController
@RequestMapping("/auth")
public class Auth {
    @Autowired
    AuthService authService;

    @PostMapping("/signup")
    public UserToken SignUp(@RequestBody AuthRequest req);

    @PostMapping("/signin")
    public UserToken signIn(@RequestBody AuthRequest req);

    @GetMapping("/me")
    @Authenticate
    public UserAccount me(@RequestAttribute UserAccount user);

    @PostMapping("/test")
    public void test(@RequestBody UserAccount user);
}
```

generated kotlin code
```kotlin
data class UserToken(val user: UserAccount, val token: String)

data class AuthRequest(val password: String, val email: String)

data class UserAccount(val id: Long, val email: String)

class AuthAPI(private val _client: RequestFactory, private val _host: String, private val _secure: Boolean, private val _path: String = "/auth"){
    fun SignUpPOST(REQUESTBODY: AuthRequest): UserToken?{
        return try {
            _client
                .newRequest()
                .setHost(_host)
                .setSecure(_secure)
                .setMethod(HttpMethod.Post)
                .setRoute("$_path/signup")
                .setBody(REQUESTBODY)
                .send()
                .getResponse(UserToken::class.java)
        } catch(_t: Throwable) {
            _t.printStackTrace();null
        }
    }
    fun testPOST(REQUESTBODY: UserAccount){
        try {
            _client
                .newRequest()
                .setHost(_host)
                .setSecure(_secure)
                .setMethod(HttpMethod.Post)
                .setRoute("$_path/test")
                .setBody(REQUESTBODY)
                .send()
        } catch(_t: Throwable) { 
            _t.printStackTrace()
        }
    }
    fun signInPOST(REQUESTBODY: AuthRequest): UserToken?{
        return try {
            _client
                .newRequest()
                .setHost(_host)
                .setSecure(_secure)
                .setMethod(HttpMethod.Post)
                .setRoute("$_path/signin")
                .setBody(REQUESTBODY)
                .send()
                .getResponse(UserToken::class.java)
        } catch(_t: Throwable) {
            _t.printStackTrace(); null
        }
    }fun meGET(): UserAccount?{
        return try {
            _client
                .newRequest()
                .setHost(_host)
                .setSecure(_secure)
                .setMethod(HttpMethod.Get)
                .setRoute("$_path/me")
                .send()
                .getResponse(UserAccount::class.java)
        } catch(_t: Throwable) {
            _t.printStackTrace(); null
        }
    }
}
```