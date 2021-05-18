package com.crm.demo.domain.service;

import com.crm.demo.domain.JudicialRecordsDto;
import com.crm.demo.infrastructure.client.JudicialRegistryClient;
import com.crm.demo.infrastructure.client.JudicialRegistryClientMock;
import com.crm.demo.infrastructure.exception.ExternalPublicServiceProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.http.client.HttpClient;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.Optional;


@Slf4j
@Service
@AllArgsConstructor
public class JudicialService
{
    private final JudicialRegistryClient judicialRegistryClient;

    private final JudicialRegistryClientMock judicialRegistryClientMock;


    public Boolean validateIfLeadHasAnyJudicialRecord( final int leadId )
    {
        JudicialRecordsDto judicialRecords;
        try
        {
            judicialRecords = judicialRegistryClient.getJudicialRecordsByLeadId( leadId );
        }
        catch ( ExternalPublicServiceProcessingException e )
        {
            log.error( Arrays.toString( e.getStackTrace() ) + "====>" + e.getMessage() );
            throw new RuntimeException( "ERROR: Failed to get record from judicial registry for leadId" + leadId );
        }

        return Optional.ofNullable( judicialRecords )
                       .map( JudicialRecordsDto::getHasJudicialRecords )
                       .orElseGet( () -> {
                           log.warn( "No data is available for the id {} in judicial systems, the lead is not a valid prospect", leadId );
                           return true;
                       } );
    }


    public Boolean validateIfSampleLeadHasAnyJudicialRecord( final int leadId,
                                                             final ObjectMapper objectMapper,
                                                             final HttpClient client )
    {
        JudicialRecordsDto judicialRecords;
        try
        {
            judicialRecords = judicialRegistryClientMock.getMockedResponseFromJudicialService( leadId, objectMapper, client );
        }
        catch ( Exception e )
        {
            log.error( Arrays.toString( e.getStackTrace() ) + "====>" + e.getMessage() );
            throw new ExternalPublicServiceProcessingException( "ERROR: Failed to get record from judicial sample registry for leadId" + leadId );
        }

        return Optional.ofNullable( judicialRecords )
                       .map( JudicialRecordsDto::getHasJudicialRecords )
                       .orElseGet( () -> {
                           log.warn( "No data is available for the id {} in judicial systems, the lead is not a valid prospect", leadId );
                           return true;
                       } );
    }
}
