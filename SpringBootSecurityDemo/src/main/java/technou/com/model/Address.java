package technou.com.model;

import javax.persistence.Embeddable;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;


@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString
@Embeddable
public class Address {
	
    @NotBlank(message = "Street is mandatory")
	private String street;
    
    @NotNull(message = "Number is mandatory")
	private int number;
    
    @NotBlank(message = "City is mandatory")
	private String city;
    
    @NotBlank(message = "State is mandatory")
	private String state;
    
    @NotBlank(message = "Pincode is mandatory")
	private String pincode;

}
