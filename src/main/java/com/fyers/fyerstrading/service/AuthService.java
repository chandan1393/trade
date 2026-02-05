package com.fyers.fyerstrading.service;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.fyers.fyerstrading.entity.FyersAuthDetails;
import com.fyers.fyerstrading.entity.UserEntity;
import com.fyers.fyerstrading.repo.FyersAuthRepository;
import com.fyers.fyerstrading.repo.UserRepository;


@Service
public class AuthService {

	@Autowired
	UserRepository userRepository;
	
	@Autowired
	FyersAuthRepository fyersAuthRepository;
	
	
	public UserEntity fetchUserDetail(String email) {
		List<UserEntity> userEntityList = userRepository.findAll();
		UserEntity	userEntity=userEntityList.stream().filter(a->a.getEmail().equals(email)).collect(Collectors.toList()).get(0);
		return userEntity;
	}
	
	public FyersAuthDetails fetchFyersAuthDetail() {
		List<FyersAuthDetails> fyersAuthDetails = fyersAuthRepository.findAll();
		return fyersAuthDetails.get(0);
	}
	
}
