package technou.com.dao;

import java.util.List;
import java.util.Optional;

import org.springframework.context.annotation.ComponentScan;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import technou.com.model.User;

@ComponentScan(basePackages = "technou.com")
public interface UsersRepository extends JpaRepository<User, Integer>{
		
	Optional<User> findByUsername(String username);
	
	//Search 
    @Query("SELECT ur FROM User ur WHERE UPPER(ur.firstname) LIKE CONCAT('%',UPPER(:searchusername),'%')"
    		+ "OR UPPER(ur.lastname) LIKE CONCAT('%',UPPER(:searchusername),'%')"
    		+ "OR UPPER(ur.username) LIKE CONCAT('%',UPPER(:searchusername),'%')"
    		
    		)
    List<User> findBySearchusername(String searchusername);

}
