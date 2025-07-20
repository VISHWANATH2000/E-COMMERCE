package com.example.ecommerce.security;

import java.util.Base64;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

@Component
public class JwtUtil {
    @Value("${jwt.secret}")
    private String jwtSecret;

    @Value("${jwt.expiration}")
    private long jwtExpirationMs;
    
    private final ObjectMapper objectMapper = new ObjectMapper();

    public String generateToken(String username, String role) {
        try {
            // Create header
            Map<String, String> header = new HashMap<>();
            header.put("alg", "HS256");
            header.put("typ", "JWT");
            
            // Create payload
            Map<String, Object> payload = new HashMap<>();
            payload.put("sub", username);
            payload.put("role", role);
            payload.put("iat", new Date().getTime() / 1000);
            payload.put("exp", (System.currentTimeMillis() + jwtExpirationMs) / 1000);
            
            // Encode header and payload
            String headerJson = objectMapper.writeValueAsString(header);
            String payloadJson = objectMapper.writeValueAsString(payload);
            
            String headerEncoded = Base64.getUrlEncoder().withoutPadding().encodeToString(headerJson.getBytes());
            String payloadEncoded = Base64.getUrlEncoder().withoutPadding().encodeToString(payloadJson.getBytes());
            
            // Create simple signature using hash
            String data = headerEncoded + "." + payloadEncoded;
            String signature = createSimpleSignature(data);
            
            return data + "." + signature;
        } catch (JsonProcessingException e) {
            throw new RuntimeException("Error generating JWT token", e);
        }
    }

    private String createSimpleSignature(String data) {
        try {
            // Simple hash-based signature
            String dataWithSecret = data + jwtSecret;
            int hash = dataWithSecret.hashCode();
            String hashString = Integer.toHexString(hash);
            return Base64.getUrlEncoder().withoutPadding().encodeToString(hashString.getBytes());
        } catch (Exception e) {
            throw new RuntimeException("Error creating signature", e);
        }
    }

    public String getUsernameFromToken(String token) {
        try {
            String[] parts = token.split("\\.");
            if (parts.length != 3) {
                return null;
            }
            
            String payloadJson = new String(Base64.getUrlDecoder().decode(parts[1]));
            @SuppressWarnings("unchecked")
            Map<String, Object> payload = objectMapper.readValue(payloadJson, Map.class);
            return (String) payload.get("sub");
        } catch (JsonProcessingException | IllegalArgumentException e) {
            return null;
        }
    }

    public String getRoleFromToken(String token) {
        try {
            String[] parts = token.split("\\.");
            if (parts.length != 3) {
                return null;
            }
            
            String payloadJson = new String(Base64.getUrlDecoder().decode(parts[1]));
            @SuppressWarnings("unchecked")
            Map<String, Object> payload = objectMapper.readValue(payloadJson, Map.class);
            return (String) payload.get("role");
        } catch (JsonProcessingException | IllegalArgumentException e) {
            return null;
        }
    }

    public boolean validateToken(String token) {
        try {
            String[] parts = token.split("\\.");
            if (parts.length != 3) {
                return false;
            }
            
            // Verify signature
            String data = parts[0] + "." + parts[1];
            String expectedSignature = createSimpleSignature(data);
            if (!expectedSignature.equals(parts[2])) {
                return false;
            }
            
            // Check expiration
            String payloadJson = new String(Base64.getUrlDecoder().decode(parts[1]));
            @SuppressWarnings("unchecked")
            Map<String, Object> payload = objectMapper.readValue(payloadJson, Map.class);
            long exp = ((Number) payload.get("exp")).longValue();
            long currentTime = System.currentTimeMillis() / 1000;
            
            return exp > currentTime;
        } catch (JsonProcessingException | IllegalArgumentException e) {
            return false;
        }
    }
} 