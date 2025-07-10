package com.baokim.notification_service.features.email.configs;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import java.util.Map;

@Configuration
@ConfigurationProperties(prefix = "email")
public class EmailConfig {
    private String provider;
    private Map<String, ProviderConfig> providers;

    public static class ProviderConfig {
        private String apiKey;
        private String domain;
        private String fromEmail;

        public String getApiKey() { return apiKey; }
        public void setApiKey(String apiKey) { this.apiKey = apiKey; }

        public String getDomain() { return domain; }
        public void setDomain(String domain) { this.domain = domain; }

        public String getFromEmail() { return fromEmail; }
        public void setFromEmail(String fromEmail) { this.fromEmail = fromEmail; }

    }

    public String getProvider() { return provider; }
    public void setProvider(String provider) { this.provider = provider; }

    public Map<String, ProviderConfig> getProviders() { return providers; }
    public void setProviders(Map<String, ProviderConfig> providers) { this.providers = providers; }

    public ProviderConfig getProviderConfig(String providerName) {
        if (providers == null || !providers.containsKey(providerName)) {
            throw new RuntimeException("Provider '" + providerName + "' is not configured");
        }
        return providers.get(providerName);
    }
}
