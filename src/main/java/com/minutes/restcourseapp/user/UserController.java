package com.minutes.restcourseapp.user;

import static org.springframework.hateoas.mvc.ControllerLinkBuilder.*;
import java.net.URI;
import java.util.List;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.hateoas.Resource;
import org.springframework.hateoas.mvc.ControllerLinkBuilder;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

@RestController
public class UserController {
	
	@Autowired
	private UserDAO userDAO;
	
	@GetMapping("/users")
	public List<User> getAll(){
		List<User> allUsers = this.userDAO.getAll();
		if( allUsers == null || allUsers.isEmpty() ) {
			throw new UserNotFoundException("no user found");
		}
		return allUsers;
	}
	
	@GetMapping("/users/{id}")
	public Resource<User> get(@PathVariable int id) {
		User user = this.userDAO.get(id);
		if( user == null ) {
			throw new UserNotFoundException("id: " + id);
		}
		
		//HATEOAS - Hypermedia as the engine of application state
		Resource<User> resource = new Resource<>(user);
		ControllerLinkBuilder linkTo = linkTo(methodOn(this.getClass()).getAll());
		resource.add(linkTo.withRel("all-users"));
		return resource;
	}
	
	@PostMapping("/users")
	public ResponseEntity<User> save(@Valid @RequestBody User user) {
		if( user.getId() != null ) {
			User savedUser = this.userDAO.get(user.getId());
			if( savedUser == null ) {
				throw new UserNotFoundException("id: " + user.getId());
			}
		}
		User save = this.userDAO.save(user);
		
		URI uri = ServletUriComponentsBuilder
			.fromCurrentRequest()
			.path("/{id}")
			.buildAndExpand(save.getId()).toUri();
		
		return ResponseEntity.created(uri).build();
	}
	
	@DeleteMapping("/users/{id}")
	public void delete(@PathVariable Integer id) {
		User deletedUser = this.userDAO.delete(id);
		if( deletedUser == null ) {
			throw new UserNotFoundException("id: " + id);
		}
	}

}
