package org.ujar.boot.restful.web;

import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.ujar.boot.build.BuildInfoConfig;

@Configuration
@Import({BuildInfoConfig.class, DefaultRestfulErrorHandler.class})
public class WebConfig {
}
