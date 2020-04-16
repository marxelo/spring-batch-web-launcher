package com.marxelo.web;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@AllArgsConstructor
@ToString
public class User {
   private Long id;
   private String userName;
   private String fullName;
//    private String userRole;
   private String status;
 
}
