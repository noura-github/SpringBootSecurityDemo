package technou.com.dao;

import org.springframework.context.annotation.ComponentScan;
import org.springframework.data.jpa.repository.JpaRepository;

import technou.com.model.Role;

@ComponentScan(basePackages = "technou.com")
public interface RoleRepository extends JpaRepository<Role, Long>{

}
