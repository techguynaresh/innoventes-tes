package com.innoventes.test.app.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.validation.constraints.*;

@NoArgsConstructor
@AllArgsConstructor
@Setter
@Getter
public class CompanyDTO {

	private Long id;

	@NotBlank(message = "Company name is mandatory")
	@Size(min = 5, message = "Company name should be at least 5 characters")
	private String companyName;

	@NotBlank(message = "Email is mandatory")
	@Email(message = "Should be a valid email address")
	private String email;

	@PositiveOrZero(message = "Strength should be a positive number or zero")
	private Integer strength;
	
	private String webSiteURL;

	@Size(min = 5, max = 5, message = "Company code should be 5 character long")
	@Pattern(regexp = "^[a-zA-Z]{2}[0-9]{2}[ENen]$", message = "Invalid company code format")
	private String companyCode;
}
