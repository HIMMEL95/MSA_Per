package com.util.msa_common.main;

import javax.ws.rs.Path;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
@Path("/MainServiceService")
public class MainServiceService {

    private static final Logger log = LoggerFactory.getLogger(MainServiceService.class);
}
