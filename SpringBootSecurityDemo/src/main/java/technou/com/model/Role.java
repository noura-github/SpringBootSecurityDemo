package technou.com.model;

import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;

import javax.persistence.*;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;



@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name="role")
public class Role implements Serializable{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy=GenerationType.AUTO)
	@Column(name = "roleId", unique = true, nullable = false)
	private int roleId;
		
	@Column(name = "userId")
	private int userId;

    @Column(name = "role"/*, unique=false*/)
	private String role;

    @ManyToMany(mappedBy="roles", fetch = FetchType.EAGER)
    private Set<User> users = new HashSet<User>();
}
