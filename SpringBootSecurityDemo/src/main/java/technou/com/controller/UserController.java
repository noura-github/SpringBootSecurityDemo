package technou.com.controller;

import java.text.SimpleDateFormat;
import java.util.Optional;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.SessionAttributes;

import technou.com.authority.AppUserDetails;
import technou.com.configuration.AppUserRole;
import technou.com.dao.UsersRepository;
import technou.com.model.Address;
import technou.com.model.User;
import technou.com.service.AppUserService;

@Controller
@RequestMapping("/user")
@SessionAttributes({"username", "userrole"})
public class UserController {

	@Autowired
	private UsersRepository userRepository;

	@Autowired
	private AppUserService appUserService;
	
	@GetMapping
	@PreAuthorize("hasAnyRole('ROLE_USER')")
	public String getUserView(Model model, HttpServletRequest request) {
		
		AppUserDetails userDetails = (AppUserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
		String userrole = AppUserRole.getRoleFromGrantedAuthorities(userDetails.getAuthorities()).toUpperCase();
		
		String username = userDetails.getUsername();
		
		Optional<User> user = userRepository.findByUsername(username);
		
		if (!user.isPresent()) {			
			user.orElseThrow(()->new UsernameNotFoundException(String.format("User % not found", username)));			
		}
		
		User currentuser = user.get();
		

		model.addAttribute("username", username);
		model.addAttribute("userrole", userrole);
		model.addAttribute("showmaincontent", "show");
		
		model.addAttribute("currentuser", currentuser);
		
		request.getSession().setAttribute("currentuser", currentuser);
					
		return "userhome";
	}

	@GetMapping(path = "/getUser")
	@PreAuthorize("hasAnyRole('ROLE_USER')")
	public String getUser(Model model, HttpServletRequest request) {

		model.addAttribute("currentuser", request.getSession().getAttribute("currentuser"));

		return "userhome";
	}
	
	
	@GetMapping("/allowUpdateUser")
	@PreAuthorize("hasAnyRole('ROLE_USER')")
	public String allowUpdateUser(Model model, User user, Address address, HttpServletRequest request) {

		User currentuser = (User)request.getSession().getAttribute("currentuser");
	
		SimpleDateFormat formatter = new SimpleDateFormat("yyyy-mm-dd");
		String date_of_birth = formatter.format(currentuser.getDate_of_birth().getTime());
		
		model.addAttribute("allowUpdateprofile", "ok");

		model.addAttribute("updateuser", currentuser);
		model.addAttribute("date_of_birth", date_of_birth);
   
        return "userhome";
    }
	
	
	@PostMapping("/updateUser")
	@PreAuthorize("hasAnyRole('ROLE_USER')")
	public String updateUser(Model model, User user, Address address, HttpServletRequest request) {
		
		user.setAddress(address);
		
		boolean crypt = user.getPassword().length()<=10?true:false;
		
		boolean updatedone = appUserService.update(user, crypt);
		
		if (updatedone) {
			model.addAttribute("updateprofile", "done");
		} else {
			model.addAttribute("updateprofile", "undone");
		}
		
		request.getSession().setAttribute("currentuser", user);

        return "userhome";
    }
}
