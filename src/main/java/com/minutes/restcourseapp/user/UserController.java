package com.minutes.restcourseapp.user;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class UserController {
	
	@Autowired
	private UserDAO userDAO;
	
	@GetMapping("/users")
	public List<User> users(){
		return this.userDAO.getAll();
	}
	
	@GetMapping("/users/{id}")
	public User user(@PathVariable int id) {
		User user = this.userDAO.get(id);
		if( user == null ) {
			throw new UserNotFoundException("id: " + id);
		}
		return user;
	}
	
	@PostMapping("/users")
	public ResponseEntity<User> save(@RequestBody User user) {
		User save = this.userDAO.save(user);
		return new ResponseEntity<User>(save, HttpStatus.CREATED);
	}

}
