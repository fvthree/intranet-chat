package com.intranet.chat.auth;

import com.intranet.chat.security.JwtProperties;
import com.intranet.chat.user.User;
import java.time.Instant;
import java.util.List;
import org.springframework.security.oauth2.jose.jws.MacAlgorithm;
import org.springframework.security.oauth2.jwt.JwsHeader;
import org.springframework.security.oauth2.jwt.JwtClaimsSet;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.JwtEncoderParameters;
import org.springframework.stereotype.Service;

@Service
public class TokenService {

  private final JwtEncoder jwtEncoder;
  private final JwtProperties jwtProperties;

  public TokenService(JwtEncoder jwtEncoder, JwtProperties jwtProperties) {
    this.jwtEncoder = jwtEncoder;
    this.jwtProperties = jwtProperties;
  }

  public String createAccessToken(User user) {
    Instant now = Instant.now();
    JwtClaimsSet claims =
        JwtClaimsSet.builder()
            .issuer(jwtProperties.issuer())
            .issuedAt(now)
            .expiresAt(now.plusSeconds(jwtProperties.accessTokenExpirationSeconds()))
            .subject(user.id().toString())
            .claim("username", user.username())
            .claim("roles", List.of(user.role()))
            .build();
    // Required for NimbusJwtEncoder to select the HS256 OctetSequenceKey (from(claims) omits alg header).
    JwsHeader jwsHeader = JwsHeader.with(MacAlgorithm.HS256).build();
    return jwtEncoder.encode(JwtEncoderParameters.from(jwsHeader, claims)).getTokenValue();
  }
}
