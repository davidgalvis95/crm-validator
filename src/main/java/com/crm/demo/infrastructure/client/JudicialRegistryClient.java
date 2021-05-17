package com.crm.demo.infrastructure.client;

import com.crm.demo.domain.JudicialRecordsDto;
import feign.Headers;
import feign.Param;
import feign.RequestLine;


@Headers( { "Accept: application/json",
            "Content-Type: application/json" } )
public interface JudicialRegistryClient
{
    @RequestLine( "GET /{leadId}" )
    JudicialRecordsDto getJudicialRecordsByLeadId( @Param( "leadId" ) int leadId );
}
