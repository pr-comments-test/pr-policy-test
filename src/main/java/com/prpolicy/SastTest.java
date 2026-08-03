package com.prpolicy;

import java.io.*;
import java.security.MessageDigest;
import java.sql.*;
import java.util.Base64;
import javax.xml.parsers.*;
import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;


public class SastTest {

    // Issue 1: Hardcoded credentials
    private static final String DB_URL = "jdbc:mysql://localhost:3306/mydb";
    private static final String DB_USER = "admin";
    private static final String DB_PASSWORD = "superSecret123";

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

    // Issue 4: Weak cryptography - MD5 used for hashing passwords
    public static String hashPassword(String password) throws Exception {
        MessageDigest md = MessageDigest.getInstance("MD5");
        byte[] hash = md.digest(password.getBytes());
        return Base64.getEncoder().encodeToString(hash);
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

    // Issue 6: XXE (XML External Entity) injection - external entities enabled
    public static void parseXml(InputStream xmlInput) throws Exception {
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        DocumentBuilder builder = factory.newDocumentBuilder();
        builder.parse(xmlInput);
    }
}
