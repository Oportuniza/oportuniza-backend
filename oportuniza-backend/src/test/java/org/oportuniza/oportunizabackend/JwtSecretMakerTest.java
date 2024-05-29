package org.oportuniza.oportunizabackend;

import io.jsonwebtoken.Jwts;
import jakarta.xml.bind.DatatypeConverter;
import org.junit.jupiter.api.Test;

import javax.crypto.SecretKey;

public class JwtSecretMakerTest {

    @Test
    public void generateSecretKey() {
        SecretKey key = Jwts.SIG.HS512.key().build();
        String keyEncoded = DatatypeConverter.printHexBinary(key.getEncoded());
        System.out.printf("\nKey: [%s]\n", keyEncoded);
    }
}