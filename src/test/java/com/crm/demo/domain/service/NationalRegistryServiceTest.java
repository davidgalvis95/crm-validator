package com.crm.demo.domain.service;

import com.crm.demo.domain.Lead;
import com.crm.demo.domain.LeadDto;
import com.crm.demo.domain.ValidationResultAgainstNationalRegistryDto;
import com.crm.demo.infrastructure.client.NationalRegistryClientMock;
import com.crm.demo.infrastructure.client.NationalRegistryFeignClient;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.http.client.HttpClient;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;


@ExtendWith( MockitoExtension.class )
class NationalRegistryServiceTest
{
    @Mock
    private NationalRegistryFeignClient nationalRegistryFeignClient;

    @Mock
    private NationalRegistryClientMock nationalRegistryClientMock;

    @InjectMocks
    private NationalRegistryService nationalRegistryService;

    @Mock
    ObjectMapper objectMapper;

    @Mock
    HttpClient httpClient;

    private Lead lead;

    private LeadDto leadDto;


    @BeforeEach
    void setUp()
    {
        lead = FixtureFactory.buildLead();
        leadDto = FixtureFactory.buildLeadDto( lead );
    }


    @Test
    void testValidateLeadAgainstNationalRegistry()
    {
        when( nationalRegistryFeignClient.getLeadFromNationalRegistry( lead ) ).thenReturn( leadDto );

        ValidationResultAgainstNationalRegistryDto response = nationalRegistryService.validateLeadAgainstNationalRegistry( lead );

        assertTrue( response.getIsValid() );
        assertEquals( FixtureFactory.LEAD_ID, response.getId() );
    }


    @Test
    void testValidateSampleLeadAgainstNationalRegistry()
    {
        when( nationalRegistryClientMock.getMockedResponseFromNationalService( FixtureFactory.LEAD_ID, objectMapper, httpClient ) ).thenReturn( leadDto );

        ValidationResultAgainstNationalRegistryDto response = nationalRegistryService.validateSampleLeadAgainstNationalRegistry( lead, objectMapper, httpClient );

        assertTrue( response.getIsValid() );
        assertEquals( FixtureFactory.LEAD_ID, response.getId() );
    }

    @Test
    void testIsNotValidateLeadAgainstNationalRegistry()
    {
        lead.setEmail( "someotheremail@email.com" );
        when( nationalRegistryFeignClient.getLeadFromNationalRegistry( lead ) ).thenReturn( leadDto );

        ValidationResultAgainstNationalRegistryDto response = nationalRegistryService.validateLeadAgainstNationalRegistry( lead );

        assertFalse( response.getIsValid() );
        assertEquals( FixtureFactory.LEAD_ID, response.getId() );
        assertEquals( "There is no data, the data is not updated or there is a mismatch against the National Systems", response.getReason() );
    }


    @Test
    void testIsNotValidateSampleLeadAgainstNationalRegistry()
    {
        lead.setEmail( "someotheremail@email.com" );
        when( nationalRegistryClientMock.getMockedResponseFromNationalService( FixtureFactory.LEAD_ID, objectMapper, httpClient ) ).thenReturn( leadDto );

        ValidationResultAgainstNationalRegistryDto response = nationalRegistryService.validateSampleLeadAgainstNationalRegistry( lead, objectMapper, httpClient );

        assertFalse( response.getIsValid() );
        assertEquals( FixtureFactory.LEAD_ID, response.getId() );
        assertEquals( "There is no data, the data is not updated or there is a mismatch against the National Systems", response.getReason() );
    }

    @Test
    void testIsNotValidateLeadAgainstNationalRegistryWhenNull()
    {
        when( nationalRegistryFeignClient.getLeadFromNationalRegistry( lead ) ).thenReturn( null );

        ValidationResultAgainstNationalRegistryDto response = nationalRegistryService.validateLeadAgainstNationalRegistry( lead );

        assertFalse( response.getIsValid() );
        assertEquals( FixtureFactory.LEAD_ID, response.getId() );
        assertEquals( "There is no data, the data is not updated or there is a mismatch against the National Systems", response.getReason() );
    }


    @Test
    void testIsNotValidateSampleLeadAgainstNationalRegistryWhenNull()
    {
        when( nationalRegistryClientMock.getMockedResponseFromNationalService( FixtureFactory.LEAD_ID, objectMapper, httpClient ) ).thenReturn( null );

        ValidationResultAgainstNationalRegistryDto response = nationalRegistryService.validateSampleLeadAgainstNationalRegistry( lead, objectMapper, httpClient );

        assertFalse( response.getIsValid() );
        assertEquals( FixtureFactory.LEAD_ID, response.getId() );
        assertEquals( "There is no data, the data is not updated or there is a mismatch against the National Systems", response.getReason() );
    }
}