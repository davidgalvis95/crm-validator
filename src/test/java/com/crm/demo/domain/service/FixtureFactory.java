package com.crm.demo.domain.service;

import com.crm.demo.domain.JudicialRecordsDto;
import com.crm.demo.domain.Lead;
import com.crm.demo.domain.LeadDto;
import com.crm.demo.domain.LeadValidationResponseDto;
import com.crm.demo.domain.ValidationResultAgainstNationalRegistryDto;
import org.jeasy.random.EasyRandom;

import java.time.LocalDate;


public class FixtureFactory
{
    public static final Integer LEAD_ID = 123456789;

    private static final EasyRandom GENERATOR = new EasyRandom();


    public static Lead buildLead()
    {
        return Lead.builder().idNumber( LEAD_ID )
                   .firstName( GENERATOR.nextObject( String.class ) )
                   .lastName( GENERATOR.nextObject( String.class ) )
                   .birthDate( GENERATOR.nextObject( LocalDate.class ) )
                   .email( GENERATOR.nextObject( String.class ) + "@addi.com" )
                   .build();
    }


    public static ValidationResultAgainstNationalRegistryDto buildNationalValidation()
    {
        return ValidationResultAgainstNationalRegistryDto.builder()
                                                         .isValid( true )
                                                         .build();
    }


    public static LeadDto buildLeadDto( Lead lead )
    {
        return LeadDto.builder()
                      .idNumber( lead.getIdNumber() )
                      .firstName( lead.getFirstName() )
                      .lastName( lead.getLastName() )
                      .birthDate( lead.getBirthDate() )
                      .email( lead.getEmail() )
                      .build();
    }


    public static JudicialRecordsDto buildJudicialRecords()
    {
        return JudicialRecordsDto.builder()
                                 .id( LEAD_ID )
                                 .hasJudicialRecords( true )
                                 .build();
    }


    public static JudicialRecordsDto buildJudicialWhenHasRecords()
    {
        return JudicialRecordsDto.builder()
                                 .id( LEAD_ID )
                                 .hasJudicialRecords( false )
                                 .build();
    }


    public static LeadValidationResponseDto buildResponse( Lead lead )
    {
        return LeadValidationResponseDto.builder()
                                        .lead( Lead.builder()
                                                   .idNumber( lead.getIdNumber() )
                                                   .build() )
                                        .score( 100 )
                                        .isAProspect( true )
                                        .reasonMessage( "The lead complies with the requested criteria" )
                                        .build();
    }
}
