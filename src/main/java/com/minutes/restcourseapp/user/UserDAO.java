package com.minutes.restcourseapp.user;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.stereotype.Component;

@Component
public class UserDAO {
	
	private static Map<Integer, User> users = new HashMap<>();
	private static int counter = 0;
	
	static {
		users.put(++counter, new User(counter, "Ricardo", new Date()));
		users.put(++counter, new User(counter, "Viviane", new Date()));
		users.put(++counter, new User(counter, "Rafaela", new Date()));
	}
	
	public List<User> getAll(){
		return users.values().stream().collect(Collectors.toList());
	}
	
	public User get(Integer id) {
		return users.get(id);
	}
	
	public User save(User user) {
		if(user.getId() == null) {
			user.setId(++counter);
		}
		users.put(user.getId(), user);
		return user;
	}
	
	public User delete(Integer id) {
		User user = this.get(id);
		if( user != null ) {
			users.remove(id);
		}
		return user;
	}

}
