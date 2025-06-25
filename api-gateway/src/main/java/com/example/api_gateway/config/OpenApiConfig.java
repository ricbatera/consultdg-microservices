package com.example.api_gateway.config;

import java.util.ArrayList;
import java.util.List;

import org.springdoc.core.models.GroupedOpenApi;
import org.springdoc.core.properties.SwaggerUiConfigParameters;
import org.springframework.cloud.gateway.route.RouteDefinitionLocator;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Lazy;

@Configuration
public class OpenApiConfig {
	
	@Bean
	@Lazy(false)
	public List<GroupedOpenApi> apis(SwaggerUiConfigParameters config, RouteDefinitionLocator locator){
		var definitions = locator.getRouteDefinitions().collectList().block();
		definitions.stream()
				.filter(routerDef -> routerDef.getId()
						.matches(".*-service"))
							.forEach(routerDef -> {
								String name = routerDef.getId();
								config.addGroup(name);
								GroupedOpenApi.builder()
									.pathsToMatch("/"+ name + "/**")
									.group(name).build();
							});
		
		return new ArrayList<>();
	}

}
