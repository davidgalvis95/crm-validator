package com.crm.demo.domain.service;

import com.crm.demo.domain.JudicialRecordsDto;
import com.crm.demo.domain.Lead;
import com.crm.demo.domain.LeadDto;
import com.crm.demo.domain.LeadValidationResponseDto;
import com.crm.demo.domain.ValidationResultAgainstNationalRegistryDto;
import com.crm.demo.infrastructure.client.JudicialRegistryClient;
import com.crm.demo.infrastructure.client.NationalRegistryFeignClient;
import com.crm.demo.infrastructure.exception.ExternalPublicServiceProcessingException;
import com.crm.demo.infrastructure.repository.LeadRepository;
import com.crm.demo.infrastructure.repository.ScoreRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.Objects;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

//TODO This tests will fail if mongo docker container is not running
@SpringBootTest
@ExtendWith( SpringExtension.class )
class LeadValidationServiceTest
{
    @Autowired
    private LeadRepository leadRepository;

    @Autowired
    private ScoreRepository scoreRepository;

    @Autowired
    private NationalRegistryService nationalRegistryService;

    @Autowired
    private JudicialService judicialService;

    @Qualifier( "customObjectMapper" )
    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private JudicialRegistryClient judicialRegistryClient;

    @MockBean
    private NationalRegistryFeignClient nationalRegistryFeignClient;

    private LeadValidationService leadValidationService;

    private Lead lead;

    private ValidationResultAgainstNationalRegistryDto validationResultAgainstNationalRegistryDto;

    private LeadDto leadDto;

    private JudicialRecordsDto judicialRecordsDto;


    @BeforeEach
    public void setUp()
    {
        leadValidationService = new LeadValidationService( leadRepository,
                                                           scoreRepository,
                                                           nationalRegistryService,
                                                           judicialService,
                                                           objectMapper );
        lead = FixtureFactory.buildLead();
        leadDto = FixtureFactory.buildLeadDto( lead );
        validationResultAgainstNationalRegistryDto = FixtureFactory.buildNationalValidation();
        judicialRecordsDto = FixtureFactory.buildJudicialRecords();
    }


    @Test
    void testServiceWhenIsASampleLead()
    {
        final LeadValidationResponseDto response = leadValidationService.validateLead( FixtureFactory.LEAD_ID, true );

        assertNotNull( response );
        assertEquals( FixtureFactory.LEAD_ID, response.getLead().getIdNumber() );
        assertNotNull( response.getLead().getBirthDate() );
        assertNotNull( response.getLead().getFirstName() );
        assertNotNull( response.getLead().getLastName() );

        if ( Objects.nonNull( response.getScore() ) && response.getScore() < 60 )
        {
            assertEquals( false, response.getIsAProspect() );
            assertEquals( "The score of the lead is below the accepted limit", response.getReasonMessage() );
        }
        else if ( Objects.nonNull( response.getScore() ) && response.getScore() >= 60 )
        {
            assertEquals( true, response.getIsAProspect() );
            assertEquals( "The lead complies with the requested criteria", response.getReasonMessage() );
        }
        else
        {
            assertNull( response.getScore() );
            assertEquals( false, response.getIsAProspect() );
            assertEquals( "Either data of the lead does not match national registry, or is reported in judicial registries", response.getReasonMessage() );
        }
    }


    @Test
    void testServiceWhenIsNotASampleLead()
    {

        when( judicialRegistryClient.getJudicialRecordsByLeadId( FixtureFactory.LEAD_ID ) ).thenReturn( judicialRecordsDto );
        when( nationalRegistryFeignClient.getLeadFromNationalRegistry( any( Lead.class ) ) ).thenReturn( leadDto );

        final LeadValidationResponseDto response = leadValidationService.validateLead( FixtureFactory.LEAD_ID, false );

        verify( judicialRegistryClient ).getJudicialRecordsByLeadId( FixtureFactory.LEAD_ID );
        verify( nationalRegistryFeignClient ).getLeadFromNationalRegistry( any( Lead.class ) );

        assertNotNull( response );
        assertEquals( FixtureFactory.LEAD_ID, response.getLead().getIdNumber() );
        assertNotNull( response.getLead().getBirthDate() );
        assertNotNull( response.getLead().getFirstName() );
        assertNotNull( response.getLead().getLastName() );

        if ( Objects.nonNull( response.getScore() ) && response.getScore() < 60 )
        {
            assertEquals( false, response.getIsAProspect() );
            assertEquals( "The score of the lead is below the accepted limit", response.getReasonMessage() );
        }
        else if ( Objects.nonNull( response.getScore() ) && response.getScore() >= 60 )
        {
            assertEquals( true, response.getIsAProspect() );
            assertEquals( "The lead complies with the requested criteria", response.getReasonMessage() );
        }
        else
        {
            assertNull( response.getScore() );
            assertEquals( false, response.getIsAProspect() );
            assertEquals( "Either data of the lead does not match national registry, or is reported in judicial registries", response.getReasonMessage() );
        }
    }


    @Test
    void testServiceIfJudicialClientThrowsExceptionLeadIsNull()
    {
        when( judicialRegistryClient.getJudicialRecordsByLeadId( FixtureFactory.LEAD_ID ) )
              .thenThrow( ExternalPublicServiceProcessingException.class );

        when( nationalRegistryFeignClient.getLeadFromNationalRegistry( any( Lead.class ) ) ).thenReturn( leadDto );

        final LeadValidationResponseDto response = leadValidationService.validateLead( FixtureFactory.LEAD_ID, false );

        verify( judicialRegistryClient ).getJudicialRecordsByLeadId( any( Integer.class ) );
        verify( nationalRegistryFeignClient ).getLeadFromNationalRegistry( any( Lead.class ) );

        assertNotNull( response.getLead() );
        assertEquals( FixtureFactory.LEAD_ID, response.getLead().getIdNumber() );
        assertNull( response.getScore() );
        assertFalse( response.getIsAProspect() );
        assertEquals( "Failed to process the validation", response.getReasonMessage() );
    }


    @Test
    void testServiceIfNationalThrowsExceptionLeadIsNull()
    {
        when( nationalRegistryFeignClient.getLeadFromNationalRegistry( any( Lead.class ) ) )
              .thenThrow( ExternalPublicServiceProcessingException.class );

        when( nationalRegistryFeignClient.getLeadFromNationalRegistry( any( Lead.class ) ) ).thenReturn( leadDto );

        final LeadValidationResponseDto response = leadValidationService.validateLead( FixtureFactory.LEAD_ID, false );

        verify( judicialRegistryClient ).getJudicialRecordsByLeadId( any( Integer.class ) );
        verify( nationalRegistryFeignClient ).getLeadFromNationalRegistry( any( Lead.class ) );

        assertNotNull( response.getLead() );
        assertEquals( FixtureFactory.LEAD_ID, response.getLead().getIdNumber() );
        assertNull( response.getScore() );
        assertFalse( response.getIsAProspect() );
        assertEquals( "Either data of the lead does not match national registry, or is reported in judicial registries", response.getReasonMessage() );
    }
}