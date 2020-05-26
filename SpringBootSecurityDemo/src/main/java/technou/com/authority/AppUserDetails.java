package technou.com.authority;

import java.util.Collection;
import java.util.Collections;
import java.util.Set;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import technou.com.configuration.AppUserRole;
import technou.com.model.Role;
import technou.com.model.User;



public class AppUserDetails implements UserDetails{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	
	private String username;
	private String password;
	private Set<? extends GrantedAuthority> grantedAuthorities;
	
	private boolean isAccountNonExpired;
	private boolean isAccountNonLocked;
	
	private boolean isCredentialsNonExpired;
	private boolean isEnabled;
	
	
	private final User user;
	
	
	Set<SimpleGrantedAuthority> simpleAuth = Collections.singleton(new SimpleGrantedAuthority("USER"));


	public AppUserDetails(User user) {
		
		this.user = user;
		
		this.username = this.user.getUsername();
		this.password = this.user.getPassword();
	
		Set<Role> roles = this.user.getRoles();
			
		roles.forEach(x->{
			
			if (x.getRole().equals("ADMIN")) {
								
				this.grantedAuthorities = AppUserRole.ADMIN.getGrantedAuthorities();				
				
			} else if (x.getRole().equals("ADMINTRAINEE")) {
				
				this.grantedAuthorities = AppUserRole.ADMINTRAINEE.getGrantedAuthorities();
				
			} else if (x.getRole().equals("USER")) {
				
				this.grantedAuthorities = AppUserRole.USER.getGrantedAuthorities();
				
			} else {
			
				this.grantedAuthorities = this.simpleAuth;
			}
		});
		
		
		this.isAccountNonExpired=true;
		this.isAccountNonLocked=true;
		
		this.isCredentialsNonExpired=true;
		this.isEnabled=true;
		
	}

	@Override
	public Collection<? extends GrantedAuthority> getAuthorities() {
		return this.grantedAuthorities;
	}

	@Override
	public String getPassword() {
		return this.password;
	}

	@Override
	public String getUsername() {
		return this.username;
	}

	@Override
	public boolean isAccountNonExpired() {
		return this.isAccountNonExpired;
	}

	@Override
	public boolean isAccountNonLocked() {

		return this.isAccountNonLocked;
	}

	@Override
	public boolean isCredentialsNonExpired() {
		return this.isCredentialsNonExpired;
	}

	@Override
	public boolean isEnabled() {
		return this.isEnabled;
	}

}
