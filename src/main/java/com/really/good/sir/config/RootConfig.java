package com.really.good.sir.config;

import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

@Configuration
@ComponentScan(basePackages = {
        "com.really.good.sir"
})
public class RootConfig {
    // no other code needed for now
}