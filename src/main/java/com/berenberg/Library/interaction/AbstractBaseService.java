package com.berenberg.Library.interaction;

import com.berenberg.Library.requestprocessors.AbstractRequestProcessor;
import org.slf4j.LoggerFactory;
import org.slf4j.Logger;
import org.springframework.http.ResponseEntity;


public class AbstractBaseService {
    private static Logger logger = LoggerFactory.getLogger(AbstractBaseService.class);

    public ResponseEntity<Object> generateResponse(AbstractRequestProcessor abstractRequestProcessor, Object request) {
        logger.info("Handling Request");
        Object obj = abstractRequestProcessor.handleRequest(request);
        return ResponseEntity.ok(obj);
    }


}
