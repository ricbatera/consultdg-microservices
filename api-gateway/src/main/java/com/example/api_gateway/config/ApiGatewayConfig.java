//package com.example.api_gateway.config;
//
//import org.springframework.cloud.gateway.route.RouteLocator;
//import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;
//import org.springframework.context.annotation.Configuration;
//
//@Configuration
//public class ApiGatewayConfig {
////	essa classe foi substituida por parametros no application.properties, mas pode ser mantida para referencia futura
//	
//	public RouteLocator routeLocator(RouteLocatorBuilder builder) {
//		return builder.routes()
////				.route(r -> r.path("/boletos-protocolos/**").uri("lb://protocolo-service"))			
//				.route("aws-service",r -> r.path("/s3/**").uri("lb://aws-service"))			
////				.route("api_boleto_service", r -> r.path("/api/boletos/**").uri("lb://api-boleto-service"))				
//				.build();
//	}
//
//}
