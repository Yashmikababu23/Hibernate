package com.fnf.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.fnf.Repository.UserRepository;
import com.fnf.model.User;

@Service
public class UserService implements IUserService {
	@Autowired 
	private UserRepository userRepository;
	
	public User getUserByUserName(String userName) {
		
		User user = userRepository.findByUserName(userName);
		return user;
	}
	
	public void addUser(User user) {
		userRepository.save(user);
	}

}
