package com.fnf.service;

import com.fnf.model.User;

public interface IUserService {
	public void addUser(User user);
	public User getUserByUserName(String userName);

}
