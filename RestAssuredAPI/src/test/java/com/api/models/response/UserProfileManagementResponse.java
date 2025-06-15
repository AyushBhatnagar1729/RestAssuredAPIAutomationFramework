package com.api.models.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserProfileManagementResponse {

	private int id;
	private String username;
	private String email;
	private String firstName;
	private String lastName;
	private String mobileNumber;
}
