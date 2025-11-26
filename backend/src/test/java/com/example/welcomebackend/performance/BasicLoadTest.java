package com.example.welcomebackend.performance;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("dev") // usa H2 en memoria para las pruebas
public class BasicLoadTest {

    @LocalServerPort
    private int port;

    private final RestTemplate restTemplate = new RestTemplate();

    /**
     * Prueba de carga sencilla que lanza múltiples peticiones concurrentes
     * contra el endpoint público /api/welcome (ajusta el endpoint si lo necesitas).
     *
     * No es una prueba de rendimiento "industrial" como JMeter o Gatling,
     * pero sirve como smoke test de carga básica dentro del proyecto.
     */
    @Test
    void basicConcurrentRequests() throws Exception {
        String url = "http://localhost:" + port + "/api/welcome";

        int threads = 10;
        int requestsPerThread = 20;

        ExecutorService executor = Executors.newFixedThreadPool(threads);
        List<Callable<Void>> tasks = new ArrayList<>();

        for (int i = 0; i < threads; i++) {
            tasks.add(() -> {
                for (int j = 0; j < requestsPerThread; j++) {
                    ResponseEntity<String> response =
                            restTemplate.getForEntity(url, String.class);
                    assertEquals(200, response.getStatusCode().value());
                }
                return null;
            });
        }

        long start = System.currentTimeMillis();
        List<Future<Void>> futures = executor.invokeAll(tasks);
        executor.shutdown();
        boolean finished = executor.awaitTermination(2, TimeUnit.MINUTES);
        long elapsed = System.currentTimeMillis() - start;

        // Aseguramos que todas las tareas terminaron correctamente
        for (Future<Void> f : futures) {
            f.get();
        }

        System.out.println("Basic concurrent load test finished in " + elapsed + " ms");
        assertEquals(true, finished, "El test de carga no terminó en el tiempo esperado");
    }
}


