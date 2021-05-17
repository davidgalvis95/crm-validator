package com.crm.demo.infrastructure.config;

import com.crm.demo.infrastructure.exception.ExternalPublicServiceProcessingException;
import feign.Response;
import feign.codec.ErrorDecoder;


public class ExternalProcessingErrorDecoder
      implements ErrorDecoder
{
    @Override
    public Exception decode( final String methodKey,
                             final Response response )
    {
        if (response.status() >= 400 && response.status() <= 599) {
            return new ExternalPublicServiceProcessingException( "Failed go process request due to external services failure" );
        }
        return null;
    }
}
