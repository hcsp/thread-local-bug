package com.github.hcsp;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.kevinsawicki.http.HttpRequest;
import org.flywaydb.core.Flyway;
import org.flywaydb.core.api.configuration.ClassicConfiguration;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.env.Environment;
import org.springframework.http.MediaType;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import static java.net.HttpURLConnection.HTTP_OK;

@ExtendWith(SpringExtension.class)
@SpringBootTest(classes = Application.class, webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@TestPropertySource(locations = "classpath:test.properties")
public class UserLoginIntegrationTest {
    private final ObjectMapper objectMapper = new ObjectMapper();
    @Autowired
    Environment environment;

    @BeforeEach
    public void setUp() throws JsonProcessingException {
        // 初始化内存数据库，以备测试
        ClassicConfiguration conf = new ClassicConfiguration();
        conf.setDataSource(
                "jdbc:h2:mem:test",
                "test",
                "test");
        Flyway flyway = new Flyway(conf);
        flyway.clean();
        flyway.migrate();

        // 注册一个测试用户，以备测试
        registerUser("test", "test");
    }

    private String getUrl(String apiName) {
        // 获取集成测试的端口号
        return "http://localhost:" + environment.getProperty("local.server.port") + apiName;
    }

    private void registerUser(String username, String password) throws JsonProcessingException {
        int code = HttpRequest.post(getUrl("/auth/register"))
                .contentType(MediaType.APPLICATION_JSON_UTF8_VALUE)
                .accept(MediaType.APPLICATION_JSON_UTF8_VALUE)
                .send(getUserNameAndPasswordJsonString(username, password))
                .code();
        Assertions.assertEquals(HTTP_OK, code);
    }

    private String getUserNameAndPasswordJsonString(String username, String password) throws JsonProcessingException {
        Map<String, Object> usernameAndPassword = new HashMap<>();
        usernameAndPassword.put("username", username);
        usernameAndPassword.put("password", password);
        return objectMapper.writeValueAsString(usernameAndPassword);
    }

    @Test
    public void usersCanLoginAndLogout() throws IOException {
        // 进行自动化的用户登录、注销操作
        for (int i = 0; i < 10; ++i) {
            // 执行一个登录操作，拿到Cookie
            HttpRequest loginRequest = HttpRequest.post(getUrl("/auth/login"))
                    .contentType(MediaType.APPLICATION_JSON_UTF8_VALUE)
                    .accept(MediaType.APPLICATION_JSON_UTF8_VALUE)
                    .send(getUserNameAndPasswordJsonString("test", "test"));
            String cookie = loginRequest.headers().get("Set-Cookie").get(0);

            // 执行一个注销操作
            HttpRequest logoutRequest = HttpRequest.get(getUrl("/auth/logout"))
                    .header("Cookie", cookie)
                    .contentType(MediaType.APPLICATION_JSON_UTF8_VALUE)
                    .accept(MediaType.APPLICATION_JSON_UTF8_VALUE);
            // 断言注销操作成功
            Assertions.assertEquals(HTTP_OK, logoutRequest.code());

            // 注销后，检查登录状态
            String logStatus = HttpRequest.get(getUrl("/auth"))
                    .accept(MediaType.APPLICATION_JSON_UTF8_VALUE)
                    .body();
            Map logStatusResponse = objectMapper.readValue(logStatus, Map.class);
            // 现在，登录状态应该是false
            Assertions.assertFalse((Boolean) logStatusResponse.get("isLogin"));
        }
    }
}
