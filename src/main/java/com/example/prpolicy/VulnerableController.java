package com.example.prpolicy;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.io.BufferedReader;
import java.io.File;
import java.io.InputStreamReader;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api")
public class VulnerableController {

    // SAST Issue 1: Hardcoded credentials
    private static final String API_KEY = "sk-1234567890abcdef";
    private static final String DB_PASSWORD = "admin123";
    private static final String SECRET_TOKEN = "secret-token-12345";

    private final JdbcTemplate jdbcTemplate;

    public VulnerableController(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    // SAST Issue 2: Command Injection vulnerability
    @GetMapping("/ping")
    public Map<String, String> ping(@RequestParam String host) {
        Map<String, String> response = new HashMap<>();
        try {
            // Vulnerable: User input directly passed to runtime exec
            Process process = Runtime.getRuntime().exec("ping -c 4 " + host);
            BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
            StringBuilder output = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                output.append(line).append("\n");
            }
            response.put("result", output.toString());
        } catch (Exception e) {
            response.put("error", e.getMessage());
        }
        return response;
    }

    // SAST Issue 3: Path Traversal vulnerability
    @GetMapping("/read-file")
    public Map<String, String> readFile(@RequestParam String filename) {
        Map<String, String> response = new HashMap<>();
        try {
            // Vulnerable: No path validation or sanitization
            String content = new String(Files.readAllBytes(Paths.get(filename)));
            response.put("content", content);
        } catch (Exception e) {
            response.put("error", "File not found");
        }
        return response;
    }

    // SAST Issue 4: SQL Injection vulnerability
    @PostMapping("/login")
    public Map<String, Object> login(@RequestBody Map<String, String> credentials) {
        String username = credentials.get("username");
        String password = credentials.get("password");

        // Vulnerable: String concatenation for SQL query
        String query = "SELECT * FROM users WHERE username = '" + username +
                      "' AND password = '" + password + "'";

        Map<String, Object> response = new HashMap<>();
        try {
            // This will execute the vulnerable query
            jdbcTemplate.queryForList(query);
            response.put("message", "Login processed");
            response.put("query", query);
        } catch (Exception e) {
            response.put("error", e.getMessage());
        }
        return response;
    }

    // SAST Issue 5: Exposing sensitive information
    @GetMapping("/config")
    public Map<String, String> getConfig() {
        Map<String, String> config = new HashMap<>();
        config.put("apiKey", API_KEY);
        config.put("dbPassword", DB_PASSWORD);
        config.put("secretToken", SECRET_TOKEN);
        return config;
    }

    // Additional SAST Issue: XSS vulnerability (reflected)
    @GetMapping("/search")
    public String search(@RequestParam String query) {
        // Vulnerable: User input directly returned in HTML without sanitization
        return "<html><body><h1>Search Results for: " + query + "</h1></body></html>";
    }

    // Additional SAST Issue: Insecure Random
    @GetMapping("/generate-token")
    public Map<String, String> generateToken() {
        Map<String, String> response = new HashMap<>();
        // Vulnerable: Using Math.random() for security-sensitive operations
        long token = (long) (Math.random() * 1000000000);
        response.put("token", String.valueOf(token));
        return response;
    }
}
