package com.github.tden27.plans.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import sbp.sbt.sdk.DataspaceCorePacketClient;
import sbp.sbt.sdk.client.config.DataspaceSdkApiClientConfiguration;
import sbp.sbt.sdk.client.config.apigateway.aksk.AKSKApiGatewayConfiguration;
import sbp.sbt.sdk.history.DataspaceCoreHistoryClient;
import sbp.sbt.sdk.search.DataspaceCoreSearchClient;

@Configuration
public class DataSpaceConfig {

    @Value("${plans.url}")
    private String dataSpaceUrl;
    @Value("${plans.appKey}")
    private String appKey;
    @Value("${plans.appSecret}")
    private String appSecret;

    @Bean
    public DataspaceCoreSearchClient searchClient() {
        return new DataspaceCoreSearchClient(dataSpaceUrl,
                DataspaceSdkApiClientConfiguration.of(builder ->
                        builder
                                .setApiGatewayConfiguration(AKSKApiGatewayConfiguration.of(appKey, appSecret))
                )
        );
    }

    @Bean
    public DataspaceCorePacketClient packetClient() {
        return new DataspaceCorePacketClient(dataSpaceUrl,
                DataspaceSdkApiClientConfiguration.of(builder ->
                        builder
                                .setApiGatewayConfiguration(AKSKApiGatewayConfiguration.of(appKey, appSecret))
                )
        );
    }

    @Bean
    public DataspaceCoreHistoryClient historyClient() {
        return new DataspaceCoreHistoryClient(dataSpaceUrl,
                DataspaceSdkApiClientConfiguration.of(builder ->
                        builder
                                .setApiGatewayConfiguration(AKSKApiGatewayConfiguration.of(appKey, appSecret))
                )
        );
    }
}
