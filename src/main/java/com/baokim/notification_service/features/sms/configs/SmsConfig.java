package com.baokim.notification_service.features.sms.configs;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import java.util.Map;

@Configuration
@ConfigurationProperties(prefix = "sms")
public class SmsConfig {
    private String provider;
    private Map<String, ProviderConfig> providers;

    public static class ProviderConfig {
        private String url;
        private String apiKey;

        public String getUrl() { return url; }
        public void setUrl(String url) { this.url = url; }

        public String getApiKey() { return apiKey; }
        public void setApiKey(String apiKey) { this.apiKey = apiKey; }
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
