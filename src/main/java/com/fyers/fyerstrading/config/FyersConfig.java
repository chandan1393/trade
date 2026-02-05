package com.fyers.fyerstrading.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.web.client.RestTemplate;

import com.tts.in.model.FyersClass;
import com.tts.in.websocket.FyersSocket;

@Configuration
public class FyersConfig {
	
	 @Bean
	    public FyersClass fyersClass() {
	        return  FyersClass.getInstance();
	    }
	 
	 
	 @Bean
	    public FyersSocket fyersSocket() {
	        return new FyersSocket(3);
	    }
	 
	 @Bean
	 public RestTemplate restTemplate() {
	     HttpComponentsClientHttpRequestFactory factory = new HttpComponentsClientHttpRequestFactory();
	     factory.setConnectTimeout(5000); // 5 seconds
	     factory.setReadTimeout(5000);
	     return new RestTemplate(factory);
	 }
	

}
