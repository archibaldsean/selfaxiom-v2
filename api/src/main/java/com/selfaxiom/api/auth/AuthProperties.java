package com.selfaxiom.api.auth;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "auth")
public class AuthProperties {

  private final Jwt jwt = new Jwt();
  private String refreshCookieName;
  private boolean refreshCookieSecure;
  private String refreshCookiePath;

  public Jwt getJwt() {
    return jwt;
  }

  public String getRefreshCookieName() {
    return refreshCookieName;
  }

  public void setRefreshCookieName(String refreshCookieName) {
    this.refreshCookieName = refreshCookieName;
  }

  public boolean isRefreshCookieSecure() {
    return refreshCookieSecure;
  }

  public void setRefreshCookieSecure(boolean refreshCookieSecure) {
    this.refreshCookieSecure = refreshCookieSecure;
  }

  public String getRefreshCookiePath() {
    return refreshCookiePath;
  }

  public void setRefreshCookiePath(String refreshCookiePath) {
    this.refreshCookiePath = refreshCookiePath;
  }

  public static class Jwt {
    private String accessSecret;
    private String refreshSecret;
    private long accessTtlSeconds;
    private long refreshTtlSeconds;

    public String getAccessSecret() {
      return accessSecret;
    }

    public void setAccessSecret(String accessSecret) {
      this.accessSecret = accessSecret;
    }

    public String getRefreshSecret() {
      return refreshSecret;
    }

    public void setRefreshSecret(String refreshSecret) {
      this.refreshSecret = refreshSecret;
    }

    public long getAccessTtlSeconds() {
      return accessTtlSeconds;
    }

    public void setAccessTtlSeconds(long accessTtlSeconds) {
      this.accessTtlSeconds = accessTtlSeconds;
    }

    public long getRefreshTtlSeconds() {
      return refreshTtlSeconds;
    }

    public void setRefreshTtlSeconds(long refreshTtlSeconds) {
      this.refreshTtlSeconds = refreshTtlSeconds;
    }
  }
}
