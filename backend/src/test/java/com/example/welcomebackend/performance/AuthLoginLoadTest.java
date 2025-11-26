package com.example.welcomebackend.performance;

import com.example.welcomebackend.model.AppUser;
import com.example.welcomebackend.repository.AppUserRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Prueba de carga sencilla sobre el endpoint de login autenticado.
 *
 * Crea (si no existe) un usuario de pruebas en la BD H2 y
 * lanza múltiples peticiones concurrentes a /api/auth/login.
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("dev")
public class AuthLoginLoadTest {

    @LocalServerPort
    private int port;

    @Autowired
    private AppUserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    private final RestTemplate restTemplate = new RestTemplate();

    @Test
    void concurrentLoginRequests() throws Exception {
        final String username = "loaduser";
        final String rawPassword = "testpass123";

        // Aseguramos que exista un usuario de pruebas
        AppUser user = userRepository.findByUsername(username).orElse(null);
        if (user == null) {
            user = new AppUser();
            user.setUsername(username);
            user.setEmail(username + "@example.com");
            user.setFullName("Load Test User");
            user.setPassword(passwordEncoder.encode(rawPassword));
            userRepository.save(user);
        }

        String loginUrl = "http://localhost:" + port + "/api/auth/login";

        int threads = 10;
        int requestsPerThread = 10;

        ExecutorService executor = Executors.newFixedThreadPool(threads);
        List<Callable<Void>> tasks = new ArrayList<>();

        for (int i = 0; i < threads; i++) {
            tasks.add(() -> {
                for (int j = 0; j < requestsPerThread; j++) {
                    Map<String, String> payload = new HashMap<>();
                    payload.put("username", username);
                    payload.put("password", rawPassword);

                    ResponseEntity<Map> response =
                            restTemplate.postForEntity(loginUrl, payload, Map.class);

                    assertEquals(200, response.getStatusCode().value());
                    Map body = response.getBody();
                    assertNotNull(body, "El cuerpo de la respuesta no debe ser nulo");
                    assertTrue(body.get("token") != null, "La respuesta debe contener un token JWT");
                }
                return null;
            });
        }

        long start = System.currentTimeMillis();
        List<Future<Void>> futures = executor.invokeAll(tasks);
        executor.shutdown();
        boolean finished = executor.awaitTermination(2, TimeUnit.MINUTES);
        long elapsed = System.currentTimeMillis() - start;

        for (Future<Void> f : futures) {
            f.get();
        }

        System.out.println("Auth login concurrent load test finished in " + elapsed + " ms");
        assertTrue(finished, "El test de carga de login no terminó en el tiempo esperado");
    }
}


