package technou.com.utilities;

import java.io.Serializable;
import java.util.GregorianCalendar;
import java.util.HashSet;
import java.util.Set;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

import technou.com.model.Address;
import technou.com.model.Role;
import technou.com.model.User;



public class UserManager {

	protected static SessionFactory sessionFactory;
	
	private static PasswordEncoder passwordEncoder = new BCryptPasswordEncoder(10);

	protected static void setup() {
		sessionFactory = HibernateAnnotationUtil.getSessionFactory();
	}

	protected static void exit() {
		sessionFactory.close();
	}

	protected static void createUser(String firstname, String lastname, String gender, String phone, 
			String email, GregorianCalendar date_of_birt, String username, String password, int active, Set<Role> roles,
			String city, String state, String street, int number, String pinCode) {

		User user = new User();

		user.setFirstname(firstname);
		user.setLastname(lastname);
		user.setGender(gender);
		user.setPhone(phone);
		user.setEmail(email);
		user.setDate_of_birth(date_of_birt);
		user.setUsername(username);
		user.setPassword(password);
		user.setActive(active);
		user.setRoles(roles);
				
		Address address = new Address();
				
		address.setCity(city);
		address.setState(state);
		address.setStreet(street);
		address.setNumber(number);
		address.setPincode(pinCode);
							
		user.setAddress(address);
		user.setAcceptTerms(true);
		user.setPassword_confirmation(password);
				
		Session session = sessionFactory.openSession();
		session.beginTransaction();

		Serializable userId = session.save(user);
		
		roles
			.forEach(role-> {
				role.setUserId((int)userId);
				session.save(role);
			});


		session.getTransaction().commit();
		session.close();	
	}
	
	
	
	/**
	 * To create some users with their authorities at the beginning 
	 */
	public static void createSampleUsers() {
		
			//Configure this code by adding the data you want to add
			Role roleAdmin = new Role();
			roleAdmin.setRole("ADMIN");
			HashSet<Role> roleAdminSet = new HashSet<Role>();
			roleAdminSet.add(roleAdmin);
			
			Role roleUser = new Role();
			roleUser.setRole("USER");
			HashSet<Role> roleUserSet = new HashSet<Role>();
			roleUserSet.add(roleUser);
			
			Role roleATrainee = new Role();
			roleATrainee.setRole("ADMINTRAINEE");
			HashSet<Role> roleATraineeSet = new HashSet<Role>();
			roleATraineeSet.add(roleATrainee);
			
			
			createUser(
					"Marry", "Doll", "female", "555222444", "mary@yahoo.com",
					new GregorianCalendar(2000,11,14), "mary", passwordEncoder.encode("mary123"), 1, roleUserSet,
					"Golling", "Salzburg", "Mark", 50, "5440"
					);
			
			createUser(
					"Noura", "Bensaber", "female", "555111999", "noura@company.com",
					new GregorianCalendar(1995,6,10), "noura", passwordEncoder.encode("noura123"), 1, roleAdminSet,
					"Hallein", "Salzburg", "Straße", 12, "5000"
					);
			
			createUser(
					"Joe", "Smith", "male", "555777555", "joe@gmail.com",
					new GregorianCalendar(1993,3,30), "joe", passwordEncoder.encode("joe123"), 1, roleATraineeSet,
					"zell am see", "Salzburg", "Markt", 18, "6000"
					);
			
			createUser(
					"Eric", "Ben", "male", "555444333", "eric@yahoo.com",
					new GregorianCalendar(1990,10,4), "eric", passwordEncoder.encode("eric123"), 1, roleUserSet,
					 "Elsbethen", "Salzburg", "flohMarkt", 220, "7000"
					);
			
			createUser(
					"Linda", "Spar", "female", "555999666", "linda@yahoo.com",
					new GregorianCalendar(1998,8,8), "linda", passwordEncoder.encode("linda123"), 1, roleUserSet,
					 "Elsbethen", "Salzburg", "Maria", 75, "7000"
					);

	}

	/**
	 * Activate this code only to create some data inside the database
	 */
	/*public static void main(String[] args) {

		setup();

		createSampleUsers();

		exit();
	}*/
	//----------------------------------------------------------------------------------
}
