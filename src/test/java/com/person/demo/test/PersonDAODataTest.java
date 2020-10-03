package com.person.demo.test;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.context.junit4.SpringRunner;

import com.person.demo.Address;
import com.person.demo.Person;
import com.person.demo.PersonDAO;


@RunWith(SpringRunner.class)
@DataJpaTest
public class PersonDAODataTest {

	  @Autowired
	  private TestEntityManager entityManager;
	
	  @Autowired
	  PersonDAO repository;

	
	  @Test 
	  public void should_find_no_persons_if_repository_is_empty() {
		  
		  Iterable<Person> persons = repository.findAll();	  
		  assertThat(persons).isEmpty(); 
	  
	  }
	  
	  @Test 
	  public void should_store_a_person() { 
		  Person Person = repository.save(new Person("Subbu", "Palisetty"));
	  
		  assertThat(Person).hasFieldOrPropertyWithValue("firstName", "Subbu");
		  assertThat(Person).hasFieldOrPropertyWithValue("lastName", "Palisetty");
	  
	  }
	  
	  @Test 
	  public void should_find_all_persons() {
		  
		  Person p1 = new Person("Subbu1", "Palisetty1"); 
		  p1 = entityManager.persist(p1);
	  
		  Person p2 = new Person("Subbu2", "Palisetty2"); 
		  p2 = entityManager.persist(p2);
		  
		  Person p3 = new Person("Subbu3", "Palisetty"); 
		  p3 = entityManager.persist(p3);
		  
		  Iterable<Person> persons = repository.findAll();
		  
		  assertThat(persons).hasSize(3).contains(p1, p2, p3); 
	  }
	  
	  @Test 
	  public void should_find_Person_by_id() { 
		  
		  Person p1 = new Person("Subbu1", "Palisetty1"); 
		  p1 = entityManager.persist(p1);	  
		  Person p2 = new Person("Subbu2", "Palisetty2"); 
		  p2 = entityManager.persist(p2);
		  
		  Person foundPerson = repository.findById(p2.getPid()).get();
		  
		  assertThat(foundPerson).isEqualTo(p2); 
	  
	  }
	  
	  @Test 
	  public void should_find_persons_by_firstName_containing_string() {
	  
		  Person p1 = new Person("Subbu1", "Palisetty1"); 
		  p1 = entityManager.persist(p1);	  
		  Person p2 = new Person("Subbu2", "Palisetty2"); 
		  p2 = entityManager.persist(p2);		  
		  Person p3 = new Person("Subbu3", "Palisetty"); 
		  p3 = entityManager.persist(p3);		  
		  
		  Iterable<Person> persons = repository.findByFirstName("Subbu2");
		  assertThat(persons).hasSize(1).contains(p2);  
	  
	  }
	  
	  @Test 
	  public void should_update_Person_by_id() { 
		  
		  Person p1 = new Person("Subbu1", "Palisetty1"); 
		  entityManager.persist(p1);
	  
		  Person p2 = new Person("Subbu2", "Palisetty2"); 
		  p2 = entityManager.persist(p2);
		  
		  Person updatedP2 = new Person("Updated-Subbu2", "Updated-Palisetty2");
		  
		  Person p = repository.findById(p2.getPid()).get();
		  p.setFirstName(updatedP2.getFirstName());
		  p.setLastName(updatedP2.getLastName());
		  
		  repository.save(p);
		  
		  Person person = repository.findById(p2.getPid()).get();
		  
		  assertThat(person.getPid()).isEqualTo(p2.getPid());
		  assertThat(person.getFirstName()).isEqualTo(updatedP2.getFirstName());
		  assertThat(person.getLastName()).isEqualTo(updatedP2.getLastName());
	  
	  }
	  
	  @Test 
	  public void should_delete_Person_by_id() { 
		  
		  Person p1 = new Person("Subbu1", "Palisetty1"); 
		  entityManager.persist(p1);	  
		  Person p2 = new Person("Subbu2", "Palisetty2"); 
		  entityManager.persist(p2);		  
		  Person p3 = new Person("Subbu3", "Palisetty"); 
		  entityManager.persist(p3);
		  
		  repository.deleteById(p2.getPid());
		  
		  Iterable<Person> persons = repository.findAll();
		  
		  assertThat(persons).hasSize(2).contains(p1, p3); 
	  }
	  
	  @Test 
	  public void should_delete_all_persons() { 
		 
		  entityManager.persist(new Person("Subbu1", "Palisetty1")); 
		  entityManager.persist(new Person("Subbu2", "Palisetty2"));
	  
		  repository.deleteAll();
		  
		  assertThat(repository.findAll()).isEmpty(); 
	  
	}  
  
	  @Test
	  public void should_store_address_to_person() {
		  
		    Person person = repository.save(new Person("Subbu", "Palisetty"));    
		    Set<Address> addressSet = new HashSet<Address>(Arrays.asList(new Address("KPHB","HYDERABAD","TELANGANA","500030")));
		    
		    person.setAddresses(addressSet);
		    
		    assertThat(person.getAddresses().iterator().next().getStreet().toString()).isEqualTo("KPHB");
		    assertThat(person.getAddresses().iterator().next().getState().toString()).isEqualTo("TELANGANA");
	    
	  }
	
	  @Test
	  public void should_find_all_addresses_of_person_by_id() {
		  
		    Person person = new Person("Subbu", "Palisetty");
		    Set<Address> addressSet = 
		    		new HashSet<Address>(Arrays.asList(new Address("KPHB","HYDERABAD","TELANGANA","500030"),
		    										   new Address("APHB","VIJAYAWADA","ANDHARAPRADESH","521020")));
		    person.setAddresses(addressSet);
		    person = entityManager.persist(person);    
		
		    Iterable<Person> persons = repository.findAll();
		
		    assertThat(persons.iterator().next().getAddresses()).hasSize(2);
	  }
	
	  @Test
	  public void should_find_address_for_Person_by_id() {
		  Person person = new Person("Subbu", "Palisetty");
		  Set<Address> addressSet = 
		    		new HashSet<Address>(Arrays.asList(new Address("KPHB","HYDERABAD","TELANGANA","500030")));
		  person.setAddresses(addressSet);
		  person = entityManager.persist(person);    
	
		  Person foundPerson = repository.findById(person.getPid()).get();
	
		  assertThat(foundPerson.getAddresses().iterator().next().getStreet()).isEqualTo("KPHB");
	  }
	
	  @Test
	  public void should_update_address_of_person_by_id() {
		  
		  Person person = new Person("Subbu", "Palisetty");
		  Set<Address> addressSet = 
		    		new HashSet<Address>(Arrays.asList(new Address("KPHB","HYDERABAD","TELANGANA","500030")));
		  person.setAddresses(addressSet);
		  person = entityManager.persist(person);    
	
		  Address newAddress = new Address("KPHB_UPDATED","HYDERABAD_UPDATED","TELANGANA_UPDATED","500030");
	
		  Person p = repository.findById(person.getPid()).get();
	    
		  Address foundAdddress = p.getAddresses().iterator().next();
		  foundAdddress.setStreet(newAddress.getStreet());
		  foundAdddress.setCity(newAddress.getCity());
		  foundAdddress.setState(newAddress.getState());    
		  p = entityManager.persist(p);
	
		  Person newPerson = repository.findById(p.getPid()).get();
	    
		  assertThat(newPerson.getPid()).isEqualTo(p.getPid());
		  assertThat(newPerson.getFirstName()).isEqualTo(p.getFirstName());
		  assertThat(newPerson.getAddresses().iterator().next().getStreet()).isEqualTo(newAddress.getStreet());
		  assertThat(newPerson.getAddresses().iterator().next().getCity()).isEqualTo(newAddress.getCity());
	   
	  }
	
	  @Test
	  public void should_delete_address_of_person_by_id() {
		  
		  Person person = new Person("Subbu", "Palisetty");
		  Set<Address> addressSet = new HashSet<Address>();
		  Address address1 = new Address("KPHB","HYDERABAD","TELANGANA","500030");
		  Address address2 = new Address("APHB","VIJAYAWADA","ANDHARAPRADESH","521020");
		  addressSet.add(address1);
		  addressSet.add(address2);
		  person.setAddresses(addressSet);
		  person = entityManager.persist(person);    
	
		  Person p = repository.findById(person.getPid()).get();    
		  p.getAddresses().remove(address1);	    
		  p = entityManager.persist(p);
		    
		  assertThat(p.getAddresses()).hasSize(1); 
		  assertThat(address2.getStreet()).isEqualTo(p.getAddresses().iterator().next().getStreet());
		  
	  } 
   
}
