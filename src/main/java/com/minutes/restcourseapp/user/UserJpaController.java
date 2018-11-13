package com.minutes.restcourseapp.user;

import static org.mockito.Mockito.RETURNS_DEEP_STUBS;
import static org.springframework.hateoas.mvc.ControllerLinkBuilder.linkTo;
import static org.springframework.hateoas.mvc.ControllerLinkBuilder.methodOn;

import java.net.URI;
import java.util.List;
import java.util.Optional;
import java.util.function.Predicate;
import java.util.stream.Collectors;

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
public class UserJpaController {
	
	@Autowired
	private UserRepository userRepository;
	
	@Autowired
	private PostRepository postRepository;
	
	@Autowired
	ResourceBundleMessageSource resourceBundleMessageSource;
	
	@GetMapping("/jpa/users")
	@ApiOperation(value="Retrieve all users recorded in database.")
	public List<User> getAll(){
		List<User> allUsers = this.userRepository.findAll();
		if( allUsers == null || allUsers.isEmpty() ) {
			throw new UserNotFoundException(this.resourceBundleMessageSource.getMessage("exception.notfound", null, LocaleContextHolder.getLocale()));
		}
		return allUsers;
	}
	
	@GetMapping("/jpa/users/{id}")
	@ApiOperation(value="Retrieve a specific user")
	public Resource<User> get(@ApiParam(value="User's id to be retrieved.") @PathVariable int id) {
		Optional<User> optUser = this.userRepository.findById(id);
		if( !optUser.isPresent() ) {
			throw new UserNotFoundException(this.resourceBundleMessageSource.getMessage("exception.notfound", null, LocaleContextHolder.getLocale()) + " id: " + id);
		}
		
		User user = optUser.get();
		//HATEOAS - Hypermedia as the engine of application state
		Resource<User> resource = new Resource<>(user);
		ControllerLinkBuilder linkTo = linkTo(methodOn(this.getClass()).getAll());
		resource.add(linkTo.withRel("all-users"));
		return resource;
	}
	
	@PostMapping("/jpa/users")
	@ApiOperation(value="Creates or Updates a user")
	public ResponseEntity<User> save(@ApiParam(value="User definition that is going to be created.") @Valid @RequestBody User user) {
		if( user.getId() != null ) {
			Optional<User> optUser = this.userRepository.findById(user.getId());
			if( !optUser.isPresent() ) {
				throw new UserNotFoundException("id: " + user.getId());
			}
		}
		
		User save = this.userRepository.save(user);
		
		URI uri = ServletUriComponentsBuilder
			.fromCurrentRequest()
			.path("/{id}")
			.buildAndExpand(save.getId()).toUri();
		
		return ResponseEntity.created(uri).build();
	}
	
	@DeleteMapping("/jpa/users/{id}")
	@ApiOperation(value="Deletes a user by id.")
	public void delete(@ApiParam(value="Users id that is going to be deleted.") @PathVariable Integer id) {
		this.userRepository.deleteById(id);
	}
	
	@GetMapping("/jpa/users/{id}/posts")
	@ApiOperation("Get all posts from a specific user.")
	public List<Post> getPosts(@PathVariable int id){
		Optional<User> optUser = this.userRepository.findById(id);
		if( !optUser.isPresent() ) {
			throw new UserNotFoundException(this.resourceBundleMessageSource.getMessage("exception.notfound", null, LocaleContextHolder.getLocale()) + " id: " + id);			
		}
		
		User user = optUser.get();
		return user.getPosts();
	}
	
	@GetMapping("/jpa/users/{userId}/posts/{postId}")
	@ApiOperation("Finds a post by user and post id.")
	public Post getPost(@PathVariable int userId, @PathVariable int postId) {
		Optional<User> optUser = this.userRepository.findById(userId);
		if( !optUser.isPresent() ) {
			throw new UserNotFoundException(this.resourceBundleMessageSource.getMessage("exception.notfound", null, LocaleContextHolder.getLocale()) + " id: " + userId);			
		}
		
		Post post = null;
		final User user = optUser.get();
		final List<Post> posts = user.getPosts();
		if( posts != null && !posts.isEmpty() ) {
			List<Post> foundPostList = posts.stream().filter(onePost -> postId == onePost.getId().intValue()).collect(Collectors.toList());
			post = foundPostList.get(0);
		}
		
		return post;
	}
	
	@PostMapping("/jpa/users/{id}/posts")
	@ApiOperation("Saves a post to a specific user.")
	public ResponseEntity<Post> savePost(@PathVariable int id, @Valid @RequestBody Post post) {
		Optional<User> optUser = this.userRepository.findById(id);
		if( !optUser.isPresent() ) {
			throw new UserNotFoundException(this.resourceBundleMessageSource.getMessage("exception.notfound", null, LocaleContextHolder.getLocale()) + " id: " + id);			
		}
		
		User user = optUser.get();
		post.setUser(user);
		Post savedPost = this.postRepository.save(post);
		
		URI uri = ServletUriComponentsBuilder
				.fromCurrentRequest()
				.path("/{id}")
				.buildAndExpand(savedPost.getId()).toUri();
			
		return ResponseEntity.created(uri).build();
	}
}
