	package com.person.demo;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;

@Configuration
@ComponentScan("com.person.demo")
@EnableWebMvc
public class DemoConfig{
	   
	@Autowired   
	PersonDAO personDAO; 
	  
}