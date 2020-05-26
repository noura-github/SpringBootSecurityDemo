package technou.com.configuration;

public enum AppUserPermission {
	
	USER_READ("user:read"),
	USER_WRITE("user:write");
	
	private final String permission;

	private AppUserPermission(String permission) {
		this.permission = permission;
	}

	public String getPermission() {
		return permission;
	}
}
