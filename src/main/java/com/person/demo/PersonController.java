package com.person.demo;

import java.util.List;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api")	
public class PersonController {

	@Autowired
	PersonDAO personDAO;

	@GetMapping("/getPerson/{id}")
	public Person showPerson(@PathVariable int id) {
		Person person = personDAO.findById(id)
				.orElseThrow(() -> new RecordNotFoundException("Person ID '" + id + "' Doesn't exist"));
		return person;
	}
	
	@GetMapping("/getAddress/{id}")
	public Set<Address> showAddress(@PathVariable int id) {
		Person person = personDAO.findById(id)
				.orElseThrow(() -> new RecordNotFoundException("Person ID '" + id + "' Doesn't exist"));
		return person.getAddresses();
	}

	@PostMapping(value = "/addPerson")
	public Person addPerson(@RequestBody Person person) {

		try {
			personDAO.save(person);
		} catch (Exception exception) {
			throw new CustomException("Error in Saving the record :" + exception.getLocalizedMessage());
		}

		return person;
	}

	@DeleteMapping(value = "/deletePerson/{pid}")
	public Person deletePerson(@PathVariable int id) {

		Person person = personDAO.findById(id)
				.orElseThrow(() -> new RecordNotFoundException("Person ID '" + id + "' Doesn't exist"));
		if (person != null)
			try {
				personDAO.deleteById(id);
			} catch (Exception e) {
				throw new CustomException("Error in Saving the record :" + e.getLocalizedMessage());
			}
		return person;
	}

	@PutMapping(value = "/updatePerson")
	public Person updatePerson(@RequestBody Person p) {

		Person person = personDAO.findById(p.getPid())
				.orElseThrow(() -> new RecordNotFoundException("Person ID '" + p.getPid() + "' Doesn't exist"));
		if (person != null) {
			person.setFirstName(p.getFirstName());
			person.setLastName(p.getLastName());
		}
		Set<Address> originalSet = person.getAddresses();
		Set<Address> updatedSet = p.getAddresses();

		originalSet.forEach((Address OriginalAddress) -> {
			updatedSet.forEach((Address updatedAddress) -> {
				if (OriginalAddress.getAid() == updatedAddress.getAid()) {
					OriginalAddress.setCity(updatedAddress.getCity());
					OriginalAddress.setState(updatedAddress.getState());
					OriginalAddress.setStreet(updatedAddress.getStreet());
					OriginalAddress.setPostalCode(updatedAddress.getPostalCode());
				}
			});
		});

		person.setAddresses(originalSet);
		try {
			personDAO.save(person);
		} catch (Exception e) {			
			throw new CustomException("Error in Saving the record :" + e.getLocalizedMessage());
		}
		return person;
	}

	@PostMapping(value = "/addAddress/{id}")
	public Person addAddress(@PathVariable int id, @RequestBody Address address) {
		Person person = personDAO.findById(id)
				.orElseThrow(() -> new RecordNotFoundException("Person ID '" + id + "' Doesn't exist"));

			Set<Address> addressSet = person.getAddresses();
			addressSet.add(address);
			person.setAddresses(addressSet);
			
		try {
			personDAO.save(person);
		} catch (Exception e) {
			throw new CustomException("Error in Saving the record :" + e.getLocalizedMessage());
		}
		return person;
	}

	@PutMapping(value = "/updateAddress/{id}")
	public Person updateAddress(@PathVariable int id, @RequestBody Address updatedAddress) {
		System.out.println("id = " + id);
		Person person = personDAO.findById(id).orElseThrow(() -> new RecordNotFoundException("Person ID '" + id + "' Doesn't exist"));
		
			Set<Address> originalSet = person.getAddresses();
			originalSet.forEach((Address OriginalAddress) -> {
				if (OriginalAddress.getAid() == updatedAddress.getAid()) {
					OriginalAddress.setCity(updatedAddress.getCity());
					OriginalAddress.setState(updatedAddress.getState());
					OriginalAddress.setStreet(updatedAddress.getStreet());
					OriginalAddress.setPostalCode(updatedAddress.getPostalCode());
				}
			});
			person.setAddresses(originalSet);
			try {
				personDAO.save(person);
			} catch (Exception e) {
				throw new CustomException("Error in Saving the record :" + e.getLocalizedMessage());
			}
			return person;		
	}

	@DeleteMapping(value = "/deleteAddress/{id}")
	public Person deleteAddress(@PathVariable int id, @RequestBody Address address) {
		
		Person person = personDAO.findById(id).orElseThrow(() -> new RecordNotFoundException("Person ID '" + id + "' Doesn't exist"));
		
			Set<Address> addressSet = person.getAddresses();
			addressSet.forEach((Address dbAddress) -> {
				if (dbAddress.getAid() == address.getAid()) {
					addressSet.remove(dbAddress);
				}
			});
			person.setAddresses(addressSet);
			try {
				personDAO.save(person);
			} catch (Exception e) {
				throw new CustomException("Error in Saving the record :" + e.getLocalizedMessage());
			}
			return person;		
	}

	@GetMapping(value = "/personCount")
	public String showCount() {

		try {
			List<Person> list = (List<Person>) personDAO.findAll();
			return "Total Number of persons : " + list.size();
		} catch (Exception e) {
			throw new CustomException("Error in Saving the record :" + e.getLocalizedMessage());
		}				
	}

	@GetMapping(value = "/listPersons")
	public List<Person> listAllPersons() {	
			
			List<Person> list = (List<Person>) personDAO.findAll();
			if(list.size()==0) {
				throw new CustomException("No Records Found" );
			}
			return list;			
	}

}
