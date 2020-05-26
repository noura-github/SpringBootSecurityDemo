package technou.com.configuration;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.context.annotation.ComponentScan;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.web.DefaultRedirectStrategy;
import org.springframework.security.web.RedirectStrategy;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

@Component
@ComponentScan({ "technou.com.configuration" })
public class AppSuccessHandler extends SimpleUrlAuthenticationSuccessHandler {

	private RedirectStrategy redirectStrategy = new DefaultRedirectStrategy();

	@Override
	protected void handle(HttpServletRequest request, HttpServletResponse response, Authentication authentication)
			throws IOException {
		String targetUrl = determineTargetUrl(authentication);

		if (response.isCommitted()) {
			System.out.println("Can't redirect");
			return;
		}

		redirectStrategy.sendRedirect(request, response, targetUrl);
	}

	/**
	 * Extracts the roles of the principal and returns the appropriate URL according
	 * to his role (admin, admintrainee, user)
	 * 
	 * @param authentication
	 * @return
	 */
	protected String determineTargetUrl(final Authentication authentication) {
		String url = "";

		Collection<? extends GrantedAuthority> authorities = authentication.getAuthorities();

		List<String> roles = new ArrayList<String>();

		for (GrantedAuthority a : authorities) {
			roles.add(a.getAuthority());
		}

		if (isAdmin(roles) || isAdminTrainne(roles)) {
			url = "admin/";
		} else if (isUser(roles)) {
			url = "user/";
		} else {
			url = "/login";
		}

		System.out.println(">>>> url=" + url);

		return url;
	}

	private boolean isUser(List<String> roles) {
		if (roles.contains("ROLE_USER") || roles.contains("USER")) {
			return true;
		}
		return false;
	}

	private boolean isAdmin(List<String> roles) {
		if (roles.contains("ROLE_ADMIN") || roles.contains("ADMIN")) {
			return true;
		}
		return false;
	}

	private boolean isAdminTrainne(List<String> roles) {
		if (roles.contains("ROLE_ADMINTRAINEE") || roles.contains("ROLE_ADMINTRAINEE")) {
			return true;
		}
		return false;
	}

	public void setRedirectStrategy(RedirectStrategy redirectStrategy) {
		this.redirectStrategy = redirectStrategy;
	}

	protected RedirectStrategy getRedirectStrategy() {
		return redirectStrategy;
	}
}
