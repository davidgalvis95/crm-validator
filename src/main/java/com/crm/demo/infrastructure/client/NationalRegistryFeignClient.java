package com.crm.demo.infrastructure.client;

import com.crm.demo.domain.Lead;
import com.crm.demo.domain.LeadDto;
import feign.Headers;
import feign.RequestLine;


@Headers( { "Accept: application/json",
            "Content-Type: application/json" } )
public interface NationalRegistryFeignClient
{
    @RequestLine( "POST" )
    LeadDto getLeadFromNationalRegistry( Lead leadId );
}
