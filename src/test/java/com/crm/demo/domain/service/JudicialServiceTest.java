package com.crm.demo.domain.service;

import com.crm.demo.domain.JudicialRecordsDto;
import com.crm.demo.infrastructure.client.JudicialRegistryClient;
import com.crm.demo.infrastructure.client.JudicialRegistryClientMock;
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
class JudicialServiceTest
{
    @Mock
    private JudicialRegistryClient judicialRegistryClient;

    @Mock
    private JudicialRegistryClientMock judicialRegistryClientMock;

    @InjectMocks
    private JudicialService judicialService;

    @Mock
    ObjectMapper objectMapper;

    @Mock
    HttpClient httpClient;

    private JudicialRecordsDto judicialRecordsDto;


    @BeforeEach
    public void setUp()
    {
        judicialRecordsDto = FixtureFactory.buildJudicialRecords();
    }


    @Test
    void testValidateIfLeadHasAnyJudicialRecord()
    {
        when( judicialRegistryClient.getJudicialRecordsByLeadId( FixtureFactory.LEAD_ID ) ).thenReturn( judicialRecordsDto );

        final Boolean response = judicialService.validateIfLeadHasAnyJudicialRecord( FixtureFactory.LEAD_ID );

        assertTrue( response );
    }


    @Test
    void testValidateIfSampleLeadHasAnyJudicialRecord()
    {
        when( judicialRegistryClientMock.getMockedResponseFromJudicialService( FixtureFactory.LEAD_ID, objectMapper, httpClient ) ).thenReturn( judicialRecordsDto );

        final Boolean response = judicialService.validateIfSampleLeadHasAnyJudicialRecord( FixtureFactory.LEAD_ID, objectMapper, httpClient );

        assertTrue( response );
    }


    @Test
    void testValidateWhenLeadHasAnyJudicialRecord()
    {
        judicialRecordsDto = FixtureFactory.buildJudicialWhenHasRecords();
        when( judicialRegistryClient.getJudicialRecordsByLeadId( FixtureFactory.LEAD_ID ) ).thenReturn( judicialRecordsDto );

        final Boolean response = judicialService.validateIfLeadHasAnyJudicialRecord( FixtureFactory.LEAD_ID );

        assertFalse( response );
    }


    @Test
    void testValidateWhenSampleLeadHasAnyJudicialRecord()
    {
        judicialRecordsDto = FixtureFactory.buildJudicialWhenHasRecords();
        when( judicialRegistryClientMock.getMockedResponseFromJudicialService( FixtureFactory.LEAD_ID, objectMapper, httpClient ) ).thenReturn( judicialRecordsDto );

        final Boolean response = judicialService.validateIfSampleLeadHasAnyJudicialRecord( FixtureFactory.LEAD_ID, objectMapper, httpClient );

        assertFalse( response );
    }
}