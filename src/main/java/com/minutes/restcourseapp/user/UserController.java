package com.minutes.restcourseapp.user;

import static org.springframework.hateoas.mvc.ControllerLinkBuilder.*;
import java.net.URI;
import java.util.List;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.context.support.ResourceBundleMessageSource;
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

import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;

@RestController
public class UserController {
	
	@Autowired
	private UserDAO userDAO;
	
	@Autowired
	ResourceBundleMessageSource resourceBundleMessageSource;
	
	@GetMapping("/users")
	@ApiOperation(value="Retrieve all users recorded in database.")
	public List<User> getAll(){
		List<User> allUsers = this.userDAO.getAll();
		if( allUsers == null || allUsers.isEmpty() ) {
			throw new UserNotFoundException(this.resourceBundleMessageSource.getMessage("exception.notfound", null, LocaleContextHolder.getLocale()));
		}
		return allUsers;
	}
	
	@GetMapping("/users/{id}")
	@ApiOperation(value="Retrieve a specific user")
	public Resource<User> get(@ApiParam(value="User's id to be retrieved.") @PathVariable int id) {
		User user = this.userDAO.get(id);
		if( user == null ) {
			throw new UserNotFoundException(this.resourceBundleMessageSource.getMessage("exception.notfound", null, LocaleContextHolder.getLocale()) + " id: " + id);
		}
		
		//HATEOAS - Hypermedia as the engine of application state
		Resource<User> resource = new Resource<>(user);
		ControllerLinkBuilder linkTo = linkTo(methodOn(this.getClass()).getAll());
		resource.add(linkTo.withRel("all-users"));
		return resource;
	}
	
	@PostMapping("/users")
	@ApiOperation(value="Creates or Updates a user")
	public ResponseEntity<User> save(@ApiParam(value="User definition that is going to be created.") @Valid @RequestBody User user) {
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
	@ApiOperation(value="Deletes a user by id.")
	public void delete(@ApiParam(value="Users id that is going to be deleted.") @PathVariable Integer id) {
		User deletedUser = this.userDAO.delete(id);
		if( deletedUser == null ) {
			throw new UserNotFoundException("id: " + id);
		}
	}

}
