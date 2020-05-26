package technou.com.controller;

import java.text.SimpleDateFormat;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.http.MediaType;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.view.InternalResourceViewResolver;

import technou.com.authority.AppUserDetails;
import technou.com.configuration.AppUserRole;
import technou.com.dao.UsersRepository;
import technou.com.model.Address;
import technou.com.model.User;
import technou.com.service.AppUserDetailsService;
import technou.com.service.AppUserService;

@Controller
@RequestMapping("/user")
public class UserController {

	@Autowired
	private UsersRepository userRepository;

	@Autowired
	private AppUserService appUserService;
	
	@GetMapping
	@PreAuthorize("hasAnyRole('ROLE_USER')")
	public String getUserView(Model model) {
		
		AppUserDetails userDetails = (AppUserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
		String userrole = AppUserRole.getRoleFromGrantedAuthorities(userDetails.getAuthorities());

		model.addAttribute("username", userDetails.getUsername());
		model.addAttribute("userrole", userrole.toUpperCase());
		model.addAttribute("showmaincontent", "show");
					
		return "userhome";
	}

	@GetMapping(path = "/getUser")
	@PreAuthorize("hasAnyRole('ROLE_USER')")
	public String getUser(Model model) {

		AppUserDetails userDetails = (AppUserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
		String userrole = AppUserRole.getRoleFromGrantedAuthorities(userDetails.getAuthorities());
		
		String username = userDetails.getUsername();
		
		Optional<User> user = userRepository.findByUsername(userDetails.getUsername());
		
		if (!user.isPresent()) {			
			user.orElseThrow(()->new UsernameNotFoundException(String.format("User % not found", username)));			
		}
		
		User currentuser = user.get();

		model.addAttribute("username", userDetails.getUsername());
		model.addAttribute("userrole", userrole.toUpperCase());

		model.addAttribute("currentuser", currentuser);

		return "userhome";
	}
	
	
	@GetMapping("/allowUpdateUser")
	@PreAuthorize("hasAnyRole('ROLE_USER')")
	public String allowUpdateUser(Model model, User user, Address address) {

		
		
		
		
		AppUserDetails userDetails = (AppUserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
		String userrole = AppUserRole.getRoleFromGrantedAuthorities(userDetails.getAuthorities());
		String username = userDetails.getUsername();
		

		
		Optional<User> currentuser = userRepository.findByUsername(username);
		
		if (!currentuser.isPresent()) {			
			currentuser.orElseThrow(()->new UsernameNotFoundException(String.format("User % not found", username)));			
		}
		
	
		SimpleDateFormat formatter = new SimpleDateFormat("yyyy-mm-dd");
		String date_of_birth = formatter.format(currentuser.get().getDate_of_birth().getTime());
		
		model.addAttribute("allowUpdateprofile", "ok");
		model.addAttribute("username", username);
		model.addAttribute("userrole", userrole.toUpperCase());
		model.addAttribute("updateuser", currentuser.get());
		model.addAttribute("date_of_birth", date_of_birth);
   
        return "userhome";
    }
	
	
	@PostMapping("/updateUser")
	@PreAuthorize("hasAnyRole('ROLE_USER')")
	public String updateUser(Model model, User user, Address address) {
		
		user.setAddress(address);

		AppUserDetails userDetails = (AppUserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
		String userrole = AppUserRole.getRoleFromGrantedAuthorities(userDetails.getAuthorities());
		String username = userDetails.getUsername();
		
		boolean crypt = user.getPassword().length()<=10?true:false;
		
		boolean updatedone = appUserService.update(user, crypt);
		
		if (updatedone) {
			model.addAttribute("updateprofile", "done");
		} else {
			model.addAttribute("updateprofile", "undone");
		}
		
		model.addAttribute("username", username);
		model.addAttribute("userrole", userrole.toUpperCase());
    
        return "userhome";
    }
}
