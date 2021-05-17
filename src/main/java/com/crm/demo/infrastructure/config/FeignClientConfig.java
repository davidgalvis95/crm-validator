package com.crm.demo.infrastructure.config;

import com.crm.demo.infrastructure.client.JudicialRegistryClient;
import com.crm.demo.infrastructure.client.NationalRegistryFeignClient;
import com.fasterxml.jackson.databind.ObjectMapper;
import feign.Feign;
import feign.gson.GsonDecoder;
import feign.gson.GsonEncoder;
import feign.jackson.JacksonDecoder;
import feign.jackson.JacksonEncoder;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.tuple.Pair;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;


@Slf4j
@Configuration
public class FeignClientConfig
{
    private final String nationalRegistryUrl;

    private final String judicialServiceUrl;

    private final String judicialServiceMockedUrl;

    private final String nationalServiceMockedUrl;


    public FeignClientConfig( @Value( "${national.registry.url}" ) final String nationalRegistryUrl,
                              @Value( "${judicial.registry.url}" ) final String judicialServiceUrl,
                              @Value( "${judicial.registry.mocked.url}" ) final String judicialServiceMockedUrl,
                              @Value( "${national.registry.mocked.url}" ) final String nationalServiceMockedUrl )
    {
        this.nationalRegistryUrl = nationalRegistryUrl;
        this.judicialServiceUrl = judicialServiceUrl;
        this.judicialServiceMockedUrl = judicialServiceMockedUrl;
        this.nationalServiceMockedUrl = nationalServiceMockedUrl;
    }


    @Bean
    public NationalRegistryFeignClient nationalRegistryFeignClient( @Qualifier( "customObjectMapper" ) ObjectMapper objectMapper )
    {
        log.info( "Creating NationalRegistryFeignClient" );
        return Feign.builder()
                    .encoder( new JacksonEncoder( objectMapper ) )
                    .decoder( new JacksonDecoder( objectMapper ) )
                    .errorDecoder( new ExternalProcessingErrorDecoder() )
                    .target( NationalRegistryFeignClient.class, nationalRegistryUrl );
    }


    @Bean
    public JudicialRegistryClient judicialRegistryClient()
    {
        log.info( "Creating JudicialRegistryClient" );
        return Feign.builder()
                    .encoder( new GsonEncoder() )
                    .decoder( new GsonDecoder() )
                    .errorDecoder( new ExternalProcessingErrorDecoder() )
                    .target( JudicialRegistryClient.class, judicialServiceUrl );
    }


    @Bean
    public Pair<String, String> externalServiceMockedUrls()
    {
        return Pair.of( nationalServiceMockedUrl, judicialServiceMockedUrl );
    }
}
