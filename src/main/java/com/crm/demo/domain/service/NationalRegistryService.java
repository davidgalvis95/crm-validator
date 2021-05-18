package com.crm.demo.domain.service;

import com.crm.demo.domain.Lead;
import com.crm.demo.domain.LeadDto;
import com.crm.demo.domain.ValidationResultAgainstNationalRegistryDto;
import com.crm.demo.infrastructure.client.NationalRegistryClientMock;
import com.crm.demo.infrastructure.client.NationalRegistryFeignClient;
import com.crm.demo.infrastructure.exception.ExternalPublicServiceProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.http.client.HttpClient;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.Objects;


@Slf4j
@Service
@AllArgsConstructor
public class NationalRegistryService
{
    private final NationalRegistryFeignClient nationalRegistryFeignClient;

    private final NationalRegistryClientMock nationalRegistryClientMock;


    public ValidationResultAgainstNationalRegistryDto validateLeadAgainstNationalRegistry( final Lead lead )
    {
        LeadDto leadFromNationalRegistry;
        try
        {
            leadFromNationalRegistry = nationalRegistryFeignClient.getLeadFromNationalRegistry( lead );
        }
        catch ( Exception e )
        {
            log.error( Arrays.toString( e.getStackTrace() ) + "====>" + e.getMessage() );
            throw new ExternalPublicServiceProcessingException( "ERROR: Failed to get record from national registry for leadId" + lead.getIdNumber() );
        }

        return validateResponse( lead, leadFromNationalRegistry );
    }


    public ValidationResultAgainstNationalRegistryDto validateSampleLeadAgainstNationalRegistry( final Lead lead,
                                                                                                 final ObjectMapper objectMapper,
                                                                                                 final HttpClient client )
    {
        LeadDto leadFromNationalRegistry;
        try
        {
            leadFromNationalRegistry = nationalRegistryClientMock.getMockedResponseFromNationalService( lead.getIdNumber(), objectMapper, client );
        }
        catch ( ExternalPublicServiceProcessingException e )
        {
            log.error( Arrays.toString( e.getStackTrace() ) + "====>" + e.getMessage() );
            throw new RuntimeException( "ERROR: Failed to get record from national sample registry for leadId" + lead.getIdNumber() );
        }

        return validateResponse( lead, leadFromNationalRegistry );
    }


    private ValidationResultAgainstNationalRegistryDto validateResponse( final Lead lead,
                                                                         final LeadDto leadFromNationalRegistry )
    {
        if ( Objects.isNull( leadFromNationalRegistry ) || !leadsFromNationalServiceAndInternalServiceAreEqual( lead, leadFromNationalRegistry ) )
        {
            log.warn( "The leadId {} is not a valid prospect because of data inconsistency against national services", lead.getIdNumber() );
            return ValidationResultAgainstNationalRegistryDto.builder()
                                                             .id( lead.getIdNumber() )
                                                             .isValid( false )
                                                             .reason( "There is no data, the data is not updated or there is a mismatch against the National Systems" )
                                                             .build();
        }

        return ValidationResultAgainstNationalRegistryDto.builder()
                                                         .id( leadFromNationalRegistry.getIdNumber() )
                                                         .isValid( true )
                                                         .build();
    }


    private boolean leadsFromNationalServiceAndInternalServiceAreEqual( final Lead lead,
                                                                        final LeadDto leadDto )
    {
        return lead.getIdNumber().equals( leadDto.getIdNumber() ) &&
               lead.getFirstName().equals( leadDto.getFirstName() ) &&
               lead.getLastName().equals( leadDto.getLastName() ) &&
               lead.getBirthDate().isEqual( leadDto.getBirthDate() ) &&
               lead.getEmail().equals( leadDto.getEmail() );
    }
}
