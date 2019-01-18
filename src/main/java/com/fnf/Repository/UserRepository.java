package com.fnf.Repository;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import com.fnf.model.User;

@Repository
public interface UserRepository extends CrudRepository<User, Integer> {

	public User findByUserName(String userName);

}
