package com.fnf.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.util.UriComponentsBuilder;

import com.fnf.Response.ErrorObject;
import com.fnf.Response.ResponseObject;
import com.fnf.Response.StatusObject;
import com.fnf.model.User;
import com.fnf.service.IUserService;

@RestController
public class LoginController {
	   private static final Logger logger = LoggerFactory.getLogger(LoginController.class);

	@Autowired 
	IUserService userService;
	@PostMapping("/signUp")
	public ResponseEntity<ResponseObject> addUser(@RequestBody User user,UriComponentsBuilder ucBuilder) {
		User u = userService.getUserByUserName(user.getUserName());
		if(u==null) {
			userService.addUser(user);
			}else {
				ResponseObject response = new ResponseObject();
				ErrorObject error = new ErrorObject();
				error.setCode(409);
				error.setMessage("User already exists");
				response.setError(error);
				logger.error("User already Exists");;
				return new ResponseEntity<ResponseObject>(response,HttpStatus.CONFLICT);
			}
			
		ResponseObject response = new ResponseObject();
		StatusObject status = new StatusObject();
		status.setCode(201);
		status.setMessage("User successfully created");
		response.setStatus(status);
			logger.info("Successfully Completed...");
	        return new ResponseEntity<ResponseObject>(response, HttpStatus.CREATED);		
	}
	@GetMapping("/login/{userName}")
	public ResponseEntity<?> getUser(@PathVariable("userName") String userName){
		
		User user = userService.getUserByUserName(userName);
		if(user==null) {
			ResponseObject response = new ResponseObject();
			ErrorObject error = new ErrorObject();
			error.setCode(404);
			error.setMessage("Invalid User Name");
			response.setError(error);
			return new ResponseEntity<ResponseObject>(response,HttpStatus.NOT_FOUND);		
		}
		return new ResponseEntity<User>(user,HttpStatus.OK);
		
	}
}
