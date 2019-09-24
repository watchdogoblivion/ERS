package com.revature.ers.security;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.security.Key;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.SecureRandom;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Base64;
import java.util.Date;
import java.util.Map;
import java.util.StringTokenizer;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import javax.servlet.http.HttpServletRequest;

import org.apache.log4j.Logger;
import org.mindrot.jbcrypt.BCrypt;

import com.revature.ers.services.UserAccountService;
import com.revature.ers.servicesimpl.UserAccountServiceImpl;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.JwtBuilder;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;

public class SecurityHandler {
	private static Logger LOGGER = Logger.getLogger(SecurityHandler.class);
	private UserAccountService userAccountService = new UserAccountServiceImpl();
	private static SignatureAlgorithm signatureAlgorithm;
	private static Key signingKey;

	static {
		try {
			KeyGenerator keyGenerator = KeyGenerator.getInstance("HmacSHA256");
			keyGenerator.init(256, new SecureRandom());
			SecretKey secretKey = keyGenerator.generateKey();
			byte[] apiKeySecretBytes = secretKey.getEncoded();
			signatureAlgorithm = SignatureAlgorithm.HS256;
			signingKey = new SecretKeySpec(apiKeySecretBytes, signatureAlgorithm.getJcaName());
		} catch (NoSuchAlgorithmException e) {
			LOGGER.error(e);
		}
	}

	public boolean isAuthenticated(String credentials) {
		if (null == credentials)
			return false;
		final String encodedCredentials = credentials.replaceFirst("Basic ", "");
		String authInfo = null;
		try {
			byte[] decodedBytes = Base64.getDecoder().decode(encodedCredentials);
			authInfo = new String(decodedBytes, "UTF-8");
		} catch (IOException e) {
			e.printStackTrace();
		}
		final StringTokenizer tokenizer = new StringTokenizer(authInfo, ":");
		final String email = tokenizer.nextToken();
		final String password = tokenizer.nextToken();

		return userAccountService.areValidCredentials(email, password);
	}

	public boolean isAuthorized(String credentials, String expectedAuthority) {
		if (null == credentials)
			return false;
		final String encodedCredentials = credentials.replaceFirst("Basic ", "");
		String authInfo = null;
		try {
			byte[] decodedBytes = Base64.getDecoder().decode(encodedCredentials);
			authInfo = new String(decodedBytes, "UTF-8");
		} catch (IOException e) {
			e.printStackTrace();
		}
		final StringTokenizer tokenizer = new StringTokenizer(authInfo, ":");
		tokenizer.nextToken();
		tokenizer.nextToken();
		final String authority = tokenizer.nextToken();

		return authority.equalsIgnoreCase(expectedAuthority);
	}

	public Jws<Claims> getJwsClaims(String jwt) {
		return Jwts.parser().setSigningKey(signingKey).parseClaimsJws(jwt);
	}

	public boolean isAuthenticatedJWT(String jwt, HttpServletRequest request) {
		try {
			Jws<Claims> claims = Jwts.parser().setSigningKey(signingKey).parseClaimsJws(jwt);
			String IP = (String) claims.getBody().get("IP");
			String email = (String) claims.getBody().get("email");
			return email != null && request.getRemoteAddr().equals(IP);
		} catch (JwtException e) {
			LOGGER.info(e);
		}
		return false;
	}

	public boolean isAuthorizedJWT(String jwt, String expectedAuthority) {
		Jws<Claims> claims = Jwts.parser().setSigningKey(signingKey).parseClaimsJws(jwt);
		String authority = (String) claims.getBody().get("authority");
		return expectedAuthority.equalsIgnoreCase(authority);
	}

	public String encodeData(String data) {
		return Base64.getEncoder().encodeToString(data.getBytes());
	}

	public String createJWT(String subject, Map<String, String> claims, Long amountToAdd, ChronoUnit unit) {

		Instant instant = Instant.now();
		Date now = Date.from(instant);
		Date expireAt = Date.from(instant.plus(amountToAdd, unit));	
		JwtBuilder builder = Jwts.builder().setIssuedAt(now).setSubject(subject);
		for (String key : claims.keySet()) {
			builder.claim(key, claims.get(key));
		}
		builder.claim("IAT", now.getTime());
		builder.claim("EXP", expireAt.getTime());
		builder.setExpiration(expireAt).signWith(signatureAlgorithm, signingKey);
		LOGGER.info(expireAt);
		return builder.compact();
	}

	public void encryptionSchema() {
		try {
			KeyPairGenerator keyPairGenerator = KeyPairGenerator.getInstance("RSA");
			KeyPair keyPair = keyPairGenerator.generateKeyPair();
			PublicKey publicKey = keyPair.getPublic();
			PrivateKey privateKey = keyPair.getPrivate();

			Cipher rsa = Cipher.getInstance("RSA/ECB/PKCS1Padding");
			rsa.init(Cipher.ENCRYPT_MODE, publicKey);
			byte[] ciphertext = rsa.doFinal("my cleartext".getBytes());

			System.out.println(ciphertext);
			rsa.init(Cipher.DECRYPT_MODE, privateKey);
			String cleartext = new String(rsa.doFinal(ciphertext));
			System.out.println(cleartext);
		} catch (GeneralSecurityException e) {
			e.printStackTrace();
		}
	}

	public String hash(CharSequence rawPassword) {
		String hashed = BCrypt.hashpw(rawPassword.toString(), BCrypt.gensalt());
		return hashed;
	}

	public boolean hashMatches(CharSequence rawPassword, String hashedPassword) {
		return BCrypt.checkpw(rawPassword.toString(), hashedPassword);
	}
}
