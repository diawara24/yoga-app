package com.openclassrooms.starterjwt.services;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.test.util.ReflectionTestUtils;

import javax.crypto.SecretKey;
import java.util.Base64;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

class JwtServiceTest {

    private JwtService jwtService;

    @BeforeEach
    void setUp() {
        jwtService = new JwtService();
        SecretKey secretKey = Jwts.SIG.HS512.key().build();
        String base64Secret = Base64.getEncoder().encodeToString(secretKey.getEncoded());
        ReflectionTestUtils.setField(jwtService, "jwtSecret", base64Secret);
        ReflectionTestUtils.setField(jwtService, "jwtExpirationMs", 86400000L);
    }

    @Test
    void generateToken_thenValidate_returnsTrue() {
        UserDetails userDetails = mock(UserDetails.class);
        when(userDetails.getUsername()).thenReturn("user@example.com");

        String token = jwtService.generateToken(userDetails);

        assertThat(jwtService.validateToken(token)).isTrue();
        assertThat(jwtService.extractUsername(token)).isEqualTo("user@example.com");
    }

    @Test
    void validateToken_malformedToken_returnsFalse() {
        assertThat(jwtService.validateToken("not-a-valid-jwt")).isFalse();
    }

    @Test
    void validateToken_expiredToken_returnsFalse() {
        ReflectionTestUtils.setField(jwtService, "jwtExpirationMs", -1000L); // expiré immédiatement
        UserDetails userDetails = mock(UserDetails.class);
        when(userDetails.getUsername()).thenReturn("user@example.com");

        String expiredToken = jwtService.generateToken(userDetails);

        assertThat(jwtService.validateToken(expiredToken)).isFalse();
    }

    @Test
    void validateTokenWithUserDetails_matchingUsername_returnsTrue() {
        UserDetails userDetails = mock(UserDetails.class);
        when(userDetails.getUsername()).thenReturn("user@example.com");
        String token = jwtService.generateToken(userDetails);

        assertThat(jwtService.validateToken(token, userDetails)).isTrue();
    }

    @Test
    void validateTokenWithUserDetails_mismatchedUsername_returnsFalse() {
        UserDetails tokenOwner = mock(UserDetails.class);
        when(tokenOwner.getUsername()).thenReturn("user@example.com");
        String token = jwtService.generateToken(tokenOwner);

        UserDetails other = mock(UserDetails.class);
        when(other.getUsername()).thenReturn("other@example.com");

        assertThat(jwtService.validateToken(token, other)).isFalse();
    }

    @Test
    void validateTokenWithUserDetails_malformedToken_returnsFalse() {
        UserDetails userDetails = mock(UserDetails.class);

        assertThat(jwtService.validateToken("not-a-valid-jwt", userDetails)).isFalse();
    }

    @Test
    void getUserNameFromJwtToken_delegatesToExtractUsername() {
        UserDetails userDetails = mock(UserDetails.class);
        when(userDetails.getUsername()).thenReturn("user@example.com");
        String token = jwtService.generateToken(userDetails);

        assertThat(jwtService.getUserNameFromJwtToken(token)).isEqualTo("user@example.com");
    }

    @Test
    void generateToken_withNonBase64Secret_fallsBackToRawBytes() {
        // Contient des caractères hors de l'alphabet Base64 ('-') : le décodage échoue
        // et le service doit utiliser les octets bruts de la chaîne comme clé.
        String rawSecret = "raw-secret-not-base64-encoded-1234567890-abcdefgh";
        ReflectionTestUtils.setField(jwtService, "jwtSecret", rawSecret);

        UserDetails userDetails = mock(UserDetails.class);
        when(userDetails.getUsername()).thenReturn("user@example.com");

        String token = jwtService.generateToken(userDetails);

        assertThat(jwtService.validateToken(token)).isTrue();
        assertThat(jwtService.extractUsername(token)).isEqualTo("user@example.com");
    }
}