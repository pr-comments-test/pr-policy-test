# pr-policy-test

Testing for pr-policy evaluation flow

## Overview

This is a test repository containing intentional security vulnerabilities for testing SAST and SCA scanning tools. It's a Spring Boot Java application with multiple security issues.

## Prerequisites

- Java 11 or higher
- Maven 3.6+

## Build

```bash
mvn clean package
```

## Running the Application

```bash
mvn spring-boot:run
```

Or run the compiled JAR:

```bash
java -jar target/pr-policy-test-1.0.0.jar
```

The server will start on port 8080.

## API Endpoints

- `GET /api/ping?host=example.com` - Ping a host (Command Injection)
- `GET /api/read-file?filename=test.txt` - Read a file (Path Traversal)
- `POST /api/login` - User login (SQL Injection)
- `GET /api/config` - Get configuration (Exposed Secrets)
- `GET /api/search?query=test` - Search (XSS)
- `GET /api/generate-token` - Generate token (Weak Random)

## Security Issues (Intentional)

This repository contains the following intentional vulnerabilities for testing purposes:

### SAST Issues:
1. **Command Injection** - `/api/ping` endpoint (`VulnerableController.java:30`)
2. **Path Traversal** - `/api/read-file` endpoint (`VulnerableController.java:48`)
3. **SQL Injection** - `/api/login` endpoint (`VulnerableController.java:60`)
4. **Hardcoded Credentials** - API keys and passwords in source code (`VulnerableController.java:17-19`)
5. **XSS (Cross-Site Scripting)** - `/api/search` endpoint (`VulnerableController.java:87`)
6. **Weak Random Number Generator** - Using Math.random() for security tokens (`VulnerableController.java:95`)
7. **Information Exposure** - `/api/config` endpoint exposes secrets (`VulnerableController.java:78`)

### SCA Issues:
- **Spring Boot 2.3.0.RELEASE** - Multiple known CVEs
- **Log4j 2.14.1** - Log4Shell vulnerability (CVE-2021-44228)
- **Jackson 2.9.8** - Known deserialization vulnerabilities

**WARNING: This code is intentionally vulnerable and should NEVER be deployed to production.**

PR-POLICY-TEST
PR-POLICY-TEST
PR-POLICY-TEST
PR-POLICY-TEST
