package technou.com.service;

import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import technou.com.dao.RoleRepository;
import technou.com.dao.UsersRepository;
import technou.com.model.Role;
import technou.com.model.User;

@Service
public class AppUserService implements UserService{

	@Autowired
	private UsersRepository usersRepository;
	
	@Autowired
	private RoleRepository roleRepository;
	
	@Autowired
	private PasswordEncoder passwordEncoder;
	
	
	@Override
	public void save(User user) {
		
		//Encrypt the password
		user.setPassword(passwordEncoder.encode(user.getPassword()));

	    user.getRoles().forEach(role->{
	    	roleRepository.save(role);
	    });
	    
	    usersRepository.save(user);
	}


	@Override
	public boolean delete(String username) {

		Optional<User> user = usersRepository.findByUsername(username);
		
		if (user.isPresent()) {
			
			usersRepository.delete(user.get());
			
			return true;
		}
		
		return false;
	}
	
	@Override
	public boolean update(User user, boolean crypt) {

		Optional<User> userdbOpt = usersRepository.findByUsername(user.getUsername());
		
		if (userdbOpt.isPresent()) {
			
			User userdb = userdbOpt.get();
			
			userdb.setAddress(user.getAddress());
			userdb.setDate_of_birth(user.getDate_of_birth());

			userdb.setFirstname(user.getFirstname());
			userdb.setLastname(user.getLastname());
			userdb.setUsername(user.getUsername());
			
			userdb.setEmail(user.getEmail());
			userdb.setPhone(user.getPhone());
			
			if (crypt) {
				userdb.setPassword(passwordEncoder.encode(user.getPassword()));
				userdb.setPassword_confirmation(passwordEncoder.encode(user.getPassword_confirmation()));
			} else {
				userdb.setPassword(user.getPassword());
				userdb.setPassword_confirmation(user.getPassword_confirmation());
			}
		
			userdb.setAcceptTerms(true);

			userdb = usersRepository.save(userdb);
			
			return (userdb!=null);
		}
		
		return false;
	}

	
	@Override
	public List<User> usersWithoutCurrentUser(String currentusername) {

		List<User> refinedUsers = usersRepository.findAll().stream()
								.filter(user->!user.getUsername().equals(currentusername))
								.collect(Collectors.toList());
		
		return refinedUsers;
	}


	@Override
	public boolean updateUserRole(String username, Set<Role> roles) {
		Optional<User> userdbOpt = usersRepository.findByUsername(username);
		
		if (userdbOpt.isPresent()) {
			
			User userdb = userdbOpt.get();
			
			userdb.setRoles(roles);

			userdb.setAcceptTerms(true);

			userdb = usersRepository.save(userdb);
			
			return (userdb!=null);
		}
		
		return false;
	}
}
