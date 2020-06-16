package technou.com.controller;

import java.text.SimpleDateFormat;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.SessionAttributes;

import technou.com.authority.AppUserDetails;
import technou.com.configuration.AppUserRole;
import technou.com.dao.UsersRepository;
import technou.com.model.Address;
import technou.com.model.Role;
import technou.com.model.User;
import technou.com.service.AppUserService;

@Controller
@RequestMapping("admin")
@SessionAttributes({"username", "userrole"})
@Scope(value="session")
public class AdminController {

	
	@Autowired
	private UsersRepository userRepository;
	
	@Autowired
	private AppUserService appUserService;

	@GetMapping("/") 
	@PreAuthorize("hasAnyRole('ROLE_ADMIN', 'ROLE_ADMINTRAINEE')")
	public String getAdminView(Model model, HttpServletRequest request) { 
		
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
		
		request.getSession().setAttribute("currentuser", currentuser);
		

		return "adminhome";
	}
	
	
	@GetMapping(path = "/getAdminUser")
	@PreAuthorize("hasAnyRole('ROLE_ADMIN', 'ROLE_ADMINTRAINEE')")
	public String getUser(Model model, HttpServletRequest request) {
		
		model.addAttribute("currentuser", request.getSession().getAttribute("currentuser"));
			
		return "adminhome";
	}
	
	@GetMapping("searchUser")
	@PreAuthorize("hasAnyRole('ROLE_ADMIN', 'ROLE_ADMINTRAINEE')")
	public String geLookedupUser(Model model, @RequestParam String searchusername) {

		
		List<User> lookedupusers = userRepository.findBySearchusername(searchusername);
		
		model.addAttribute("lookedupusers", lookedupusers);
		model.addAttribute("matchfound", String.valueOf(lookedupusers.size()));
		
		boolean nomatchfound = lookedupusers.size()==0?true:false;
		model.addAttribute("nomatchfound", String.valueOf(nomatchfound));
		
		return "adminhome";
	}
	
	
	@GetMapping("getAllUsers")
	@PreAuthorize("hasAnyRole('ROLE_ADMIN', 'ROLE_ADMINTRAINEE')")
	public String showAllUsers(Model model) {

		AppUserDetails userDetails = (AppUserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
		
		String username = userDetails.getUsername();	
		
		List<User> refinedUsers = appUserService.usersWithoutCurrentUser(username);

		model.addAttribute("allusers", refinedUsers);
		
		return "adminhome";
	}
	

	@GetMapping("/allowUpdateAdminUser")
	@PreAuthorize("hasAnyRole('ROLE_ADMIN', 'ROLE_ADMINTRAINEE')")
	public String allowUpdateAdminUser(Model model, HttpServletRequest request, User user, Address address) {
		

		User currentuser = (User)request.getSession().getAttribute("currentuser");
		model.addAttribute("currentuser", currentuser);
				
		SimpleDateFormat formatter = new SimpleDateFormat("yyyy-mm-dd");
		String date_of_birth = formatter.format(currentuser.getDate_of_birth().getTime());
		
		model.addAttribute("allowUpdateprofile", "ok");
		model.addAttribute("updateuser", currentuser);
		
		model.addAttribute("date_of_birth", date_of_birth);
   
        return "adminhome";
    }
	
	
	@PostMapping("/updateAdminUser")
	@PreAuthorize("hasAnyRole('ROLE_ADMIN', 'ROLE_ADMINTRAINEE')")
	public String updateAdminUser(Model model, HttpServletRequest request, User user, Address address) {
		
		user.setAddress(address);
		
		boolean crypt = user.getPassword().length()<=10?true:false;
		
		boolean updatedone = appUserService.update(user, crypt);
		
		if (updatedone) {
			model.addAttribute("updateprofile", "done");
		} else {
			model.addAttribute("updateprofile", "undone");
		}
		
		request.getSession().setAttribute("currentuser", user);
    
        return "adminhome";
    }
	
	
	@PostMapping("/updateUserRole")
	@PreAuthorize("hasAuthority('user:write')")
	public String updateUserRole(Model model, String updateroleusername, String updaterole) {
		
		AppUserDetails userDetails = (AppUserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
		String username = userDetails.getUsername();
			
		Role role = new Role();
		role.setRole(updaterole);
		HashSet<Role> roles = new HashSet<Role>();
		roles.add(role);
		
		appUserService.updateUserRole(updateroleusername, roles);
		
		List<User> refinedUsers = appUserService.usersWithoutCurrentUser(username);		
		model.addAttribute("allusers", refinedUsers);
    
        return "adminhome";
    }

	
	@PostMapping("deleteUserByName")
	@PreAuthorize("hasAuthority('user:write')")
	public String deleteUser(Model model, String deleteusername) {
		
		AppUserDetails userDetails = (AppUserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

		String username = userDetails.getUsername();
		
		boolean isDeleted = appUserService.delete(deleteusername);

		model.addAttribute("userdeleted", isDeleted);
			
		List<User> refinedUsers = appUserService.usersWithoutCurrentUser(username);
		model.addAttribute("allusers", refinedUsers);
    
        return "adminhome";
    }
}
