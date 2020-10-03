package com.person.demo;

import java.util.List;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Repository;

@Repository
@Component(value = "personDAO")
public interface PersonDAO extends CrudRepository<Person, Integer>{	
	
	  List<Person> findByFirstName(String name); 
	  
	  List<Person> findByLastName(String name);
	 

}
