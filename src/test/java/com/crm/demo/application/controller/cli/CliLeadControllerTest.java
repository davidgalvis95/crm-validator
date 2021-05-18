package com.crm.demo.application.controller.cli;

import com.crm.demo.domain.Lead;
import com.crm.demo.domain.LeadValidationResponseDto;
import com.crm.demo.domain.service.FixtureFactory;
import com.crm.demo.domain.service.LeadValidationService;
import com.crm.demo.domain.service.ValidationService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;


@SpringBootTest
@ExtendWith( SpringExtension.class )
class CliLeadControllerTest
{
    @MockBean
    private LeadValidationService leadValidationService;


    @Test
    void testValidateLead()
    {
        ValidationService leadValidationServiceI = leadValidationService;
        CliLeadController cliLeadController = new CliLeadController( leadValidationServiceI );
        final Lead lead = FixtureFactory.buildLead();
        when( leadValidationService.validateLead( FixtureFactory.LEAD_ID, true ) ).thenReturn( FixtureFactory.buildResponse( lead ) );
        final LeadValidationResponseDto response = cliLeadController.validateLead( FixtureFactory.LEAD_ID, true );

        assertNotNull( response );
        assertEquals( FixtureFactory.LEAD_ID, response.getLead().getIdNumber() );
        assertEquals( true, response.getIsAProspect() );
        assertEquals( "The lead complies with the requested criteria", response.getReasonMessage() );
    }
}