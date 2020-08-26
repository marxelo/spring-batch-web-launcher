package com.marxelo.web.model;

import java.io.Serializable;

public class JwtResponse implements Serializable {

	/**
     *
     */
    private static final long serialVersionUID = -1097499471851102786L;
    
    private final String jwttoken;

	public JwtResponse(String jwttoken) {
		this.jwttoken = jwttoken;
	}

	public String getToken() {
		return this.jwttoken;
	}
}