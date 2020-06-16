package technou.com.controller;

import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import technou.com.dao.UsersRepository;
import technou.com.model.Address;
import technou.com.model.Role;
import technou.com.model.User;
import technou.com.service.AppUserService;

@Controller
@RequestMapping
public class HomeController {	
	

	@Autowired
	private AppUserService appUserService;
	
	@Autowired
	private UsersRepository userRepository;
	
	@GetMapping("/")
	public String getHomeView() {
		return "index";
	}
	
	@GetMapping("index")
	public String getIndexHomeView() {
		return "index";
	}
	
	@RequestMapping(path= "/login") 
	public String getLoginView() {
		 return "login";
	}
	
	@GetMapping("/register")
	public String getRegisterView() {
		return "register";
	}

	
	@RequestMapping(value="/register/checkUsername/{username}", method=RequestMethod.POST)
	@ResponseBody
	public String checkUsername(@Valid @PathVariable String username, Model model) {

		Optional<User> existingUsername = userRepository.findByUsername(username);

		if(existingUsername.isPresent()){
		    
		    return "true";

		} else {
		    
		    return "false";
		}
    }
	
	@PostMapping("/createUser")
	public String registerUser(@Valid User user, @Valid Address address, Model model) {
		
		user.setAddress(address);
		
		Role role = new Role();
		role.setRole("USER");
		user.setActive(1);
		
		Set<Role> roles = new HashSet<Role>();
		roles.add(role);
		
		user.setRoles(roles);
		
		appUserService.save(user);
    
        return "registersuccess";
    }
}
