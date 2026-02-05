package com.fyers.fyerstrading.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

@Component
public class TelegramService {

    @Value("${telegram.bot.token}")
    private String botToken;

    @Value("${telegram.chat.id}")
    private String chatId;

    @Autowired
    private  RestTemplate restTemplate ;

    private String telegramBaseUrl = "https://api.telegram.org/bot";
    private String apiUrl = telegramBaseUrl+botToken;
    
    public void sendMessage(String message) throws Exception {
        try {
            UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(apiUrl+"/sendMessage")
                    .queryParam("chat_id", chatId)
                    .queryParam("text", message);
            ResponseEntity exchange = restTemplate.exchange(builder.toUriString().replaceAll("%20", " "), HttpMethod.GET, null, String.class);
        } catch (HttpClientErrorException | HttpServerErrorException e) {
          System.out.println("Error response : State code: {}, response: {} "+ e.getStatusCode()+ e.getResponseBodyAsString());
            throw e;
        } catch (Exception err) {
        	 System.out.println("Error: {} "+err.getMessage());
            throw new Exception("This service is not available at the moment!");
        }
    }

}
