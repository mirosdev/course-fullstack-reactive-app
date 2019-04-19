package com.course.spring.reactive.rest.security;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.util.Base64;

import static com.course.spring.reactive.rest.security.SecurityConstants.*;

@Component
public class PBKDF2Encoder implements PasswordEncoder {

    /**
     * More info (https://www.owasp.org/index.php/Hashing_Java)
     * @param charSequence password
     * @return encoded password
     */

    @Override
    public String encode(CharSequence charSequence) {
        try {
            byte[] result = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA512")
                    .generateSecret(new PBEKeySpec(charSequence.toString().toCharArray(), PASSWORD_SECRET.getBytes(), ITERATION, KEYLENGTH))
                    .getEncoded();
            return Base64.getEncoder().encodeToString(result);
        } catch (NoSuchAlgorithmException | InvalidKeySpecException ex) {
            throw new RuntimeException(ex);
        }
    }

    @Override
    public boolean matches(CharSequence charSequence, String string) {
        return encode(charSequence).equals(string);
    }
}
