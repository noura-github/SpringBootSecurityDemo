package technou.com.service;

import java.util.List;
import java.util.Set;

import technou.com.model.Role;
import technou.com.model.User;

public interface UserService {

	 void save(User user);
	
	 boolean delete(String username);
	 
	 boolean update(User user, boolean crypt);

	 List<User> usersWithoutCurrentUser(String currentusername);

	 boolean updateUserRole(String username, Set<Role> roles);
}
