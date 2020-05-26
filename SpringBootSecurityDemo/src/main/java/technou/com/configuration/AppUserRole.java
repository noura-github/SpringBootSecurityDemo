package technou.com.configuration;

import java.util.Collection;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import com.google.common.collect.Sets;


public enum AppUserRole {
	
	USER(Sets.newHashSet()),
	ADMIN(Sets.newHashSet(AppUserPermission.USER_READ,
			AppUserPermission.USER_WRITE)),
	ADMINTRAINEE(Sets.newHashSet(AppUserPermission.USER_READ));
	
	
	private final Set<AppUserPermission> permissions;

	private AppUserRole(Set<AppUserPermission> permissions) {
		this.permissions = permissions;
	}

	public Set<AppUserPermission> getPermissions() {
		return permissions;
	}

	public Set<SimpleGrantedAuthority> getGrantedAuthorities(){
	
		Set<SimpleGrantedAuthority> allpermissions = getPermissions().stream()
		.map(permission -> new SimpleGrantedAuthority(permission.getPermission()))
		.collect(Collectors.toSet());
		
		allpermissions.add(new SimpleGrantedAuthority("ROLE_" + this.name()));
		
		return allpermissions;
	}

	public static String getRoleFromGrantedAuthorities(Collection<? extends GrantedAuthority> authorities){

		Optional<? extends GrantedAuthority> roleOp = authorities.stream()
			.filter(a->a.getAuthority().contains("ROLE_ADMINTRAINEE"))
			.findFirst();
		
		if (roleOp.isPresent()) return "ADMINTRAINEE";
		
		roleOp = authorities.stream()
				.filter(a->a.getAuthority().contains("ROLE_ADMIN"))
				.findFirst();
			
		if (roleOp.isPresent()) return "ADMIN";
		
		roleOp = authorities.stream()
				.filter(a->a.getAuthority().contains("ROLE_USER"))
				.findFirst();
			
		if (roleOp.isPresent()) return "USER";
		

		return "";
	}
}
