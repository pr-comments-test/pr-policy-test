package com.prpolicy;

import java.io.*;
import java.security.SecureRandom;
import java.sql.*;
import java.util.Base64;
import javax.xml.parsers.*;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;


public class SastTest {

    private static final String DB_URL = System.getenv("DB_URL");
    private static final String DB_USER = System.getenv("DB_USER");
    private static final String DB_PASSWORD = System.getenv("DB_PASSWORD");

    // Issue 2: SQL injection - user input concatenated directly into query
    public static void getUser(String username) throws SQLException {
        Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
        Statement stmt = conn.createStatement();
        String query = "SELECT * FROM users WHERE username = '" + username + "'";
        ResultSet rs = stmt.executeQuery(query);
    }

    // Issue 3: Command injection - unsanitized input passed to Runtime.exec
    public static void runCommand(String userInput) throws IOException {
        Runtime runtime = Runtime.getRuntime();
        Process process = runtime.exec("ls " + userInput);
    }

    public static String hashPassword(String password) throws Exception {
        SecureRandom random = new SecureRandom();
        byte[] salt = new byte[16];
        random.nextBytes(salt);
        PBEKeySpec spec = new PBEKeySpec(password.toCharArray(), salt, 310_000, 256);
        SecretKeyFactory skf = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA256");
        byte[] hash = skf.generateSecret(spec).getEncoded();
        return Base64.getEncoder().encodeToString(salt) + ":" + Base64.getEncoder().encodeToString(hash);
    }

    // Issue 5: Path traversal - no validation on user-controlled file path
    public static String readFile(String filename) throws IOException {
        String basePath = "/var/app/files/";
        BufferedReader reader = new BufferedReader(new FileReader(basePath + filename));
        StringBuilder content = new StringBuilder();
        String line;
        while ((line = reader.readLine()) != null) {
            content.append(line);
        }
        return content.toString();
    }

    public static void parseXml(InputStream xmlInput) throws Exception {
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        factory.setFeature("http://apache.org/xml/features/disallow-doctype-decl", true);
        factory.setFeature("http://xml.org/sax/features/external-general-entities", false);
        factory.setFeature("http://xml.org/sax/features/external-parameter-entities", false);
        factory.setXIncludeAware(false);
        factory.setExpandEntityReferences(false);
        DocumentBuilder builder = factory.newDocumentBuilder();
        builder.parse(xmlInput);
    }
}
