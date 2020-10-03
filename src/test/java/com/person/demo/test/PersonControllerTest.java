package com.person.demo.test;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.core.Is.is;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.person.demo.Address;
import com.person.demo.Person;
import com.person.demo.PersonController;
import com.person.demo.PersonDAO;


public class PersonControllerTest {

    private MockMvc mockMvc;

    @Mock
    private PersonDAO personDAO;

    @InjectMocks
    private PersonController personController;

    @Before
    public void init(){
        MockitoAnnotations.initMocks(this);
        mockMvc = MockMvcBuilders.standaloneSetup(personController).build();
    }


    @Test
    public void test_get_all_success() throws Exception {
        List<Person> persons = Arrays.asList(
                new Person(1, "Subbu", "Palisetty"),
                new Person(2, "Subbu2", "Palisetty2"));
               

        when(personDAO.findAll()).thenReturn(persons);

        mockMvc.perform(get("/api/listPersons"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].pid", is(1)))
                .andExpect(jsonPath("$[0].firstName", is("Subbu")))
                .andExpect(jsonPath("$[1].pid", is(2)))
                .andExpect(jsonPath("$[1].firstName", is("Subbu2")));

        verify(personDAO, times(1)).findAll();
        verifyNoMoreInteractions(personDAO);
    }


    @Test
    public void test_get_person_by_id_success() throws Exception {
        Optional<Person> person = Optional.of(new Person(1, "Subbu", "Palisetty", null));

        when(personDAO.findById(1)).thenReturn(person);

        mockMvc.perform(get("/api/getPerson/{id}", 1))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.pid", is(1)))
                .andExpect(jsonPath("$.firstName", is("Subbu")));

        verify(personDAO, times(1)).findById(1);
        verifyNoMoreInteractions(personDAO);
    }

    @Test
    public void test_get_person_by_wrong_id_404() throws Exception {
    	Optional<Person> person = Optional.of(new Person());
        when(personDAO.findById(1)).thenReturn(person);

        mockMvc.perform(get("/api/getPerson/{id}", 10))
                .andExpect(status().isNotFound());

        verify(personDAO, times(1)).findById(10);
        verifyNoMoreInteractions(personDAO);
    }


    @Test
    public void test_create_person_success() throws Exception {
    	
    	Set<Address> addressSet = new HashSet<Address>(Arrays.asList(new Address(1,"KPHB","HYDERABAD","TELANGANA","500030")));
		    
    	 Person person = new Person(1,"Subbu", "Palisetty",addressSet);

    	 when(personDAO.existsById(person.getPid())).thenReturn(false);
         when(personDAO.save(person)).thenReturn(person);

        mockMvc.perform(
                post("/api/addPerson")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(asJsonString(person)))
                .andExpect(status().isOk());       
       
    }

    @Test
    public void test_update_person_success() throws Exception {
    	Set<Address> addressSet = new HashSet<Address>(Arrays.asList(new Address(1,"KPHB","HYDERABAD","TELANGANA","500030")));
		 
    	Person person = new Person(1, "Subbu", "Palisetty",addressSet);
    	when(personDAO.save(person)).thenReturn(person);
        when(personDAO.findById(person.getPid())).thenReturn(Optional.of(person));

        mockMvc.perform(
                put("/api/updatePerson", person.getPid())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(asJsonString(person)))
                .andExpect(status().isOk());
    }

    @Test
    public void test_update_person_fail_404_not_found() throws Exception {
    	
    	Person person = new Person(1, "Subbu", "Palisetty");
    	Optional<Person> p = Optional.of(person);
    	
        when(personDAO.findById(person.getPid())).thenReturn(p);

        mockMvc.perform(
                get("/api/getPerson/{id}", 11)
                        .contentType(MediaType.APPLICATION_JSON))
        				.andExpect(status().isNotFound());

        verify(personDAO, times(1)).findById(11);
        verifyNoMoreInteractions(personDAO);
    }

    @Test
    public void test_delete_person_success() throws Exception {

    	Set<Address> addressSet = new HashSet<Address>(Arrays.asList(new Address(2,"KPHB","HYDERABAD","TELANGANA","500030")));
    	Person person = new Person(2, "Subbu", "Palisetty",addressSet);
    	
        doNothing().when(personDAO).deleteById(person.getPid());

        mockMvc.perform(
                delete("/api/deletePerson/{pid}", person.getPid()));        
    }
    

    @Test
    public void test_get_all_addresses_success() throws Exception {
       
    	Set<Address> addressSet = new HashSet<Address>
    	(Arrays.asList(new Address(1,"KPHB","HYDERABAD","TELANGANA","500030"),
    				   new Address(2,"APHB","BANGALORE","KARNATAKA","500040")));
    	
    	List<Person> persons = Arrays.asList(new Person(1, "Subbu", "Palisetty",addressSet));

        when(personDAO.findAll()).thenReturn(persons);
        
        mockMvc.perform(get("/api/listPersons"))
        .andExpect(status().isOk())
        .andExpect(content().contentType(MediaType.APPLICATION_JSON))
        .andExpect(jsonPath("$", hasSize(1)))
        
        .andExpect(jsonPath("$[0].addresses[0].city", is("HYDERABAD")))
        .andExpect(jsonPath("$[0].addresses[1].city", is("BANGALORE"))) ;      
        
    }

    
    @Test
    public void test_create_address_success() throws Exception {
    	Set<Address> addressSet = new HashSet<Address>(Arrays.asList(new Address(1,"KPHB","HYDERABAD","TELANGANA","500030")));
    	Person person = new Person(1,"Subbu", "Palisetty",addressSet);

         when(personDAO.save(person)).thenReturn(person);
         when(personDAO.findById(person.getPid())).thenReturn(Optional.of(person));
         
         
         
        mockMvc.perform(
                post("/api/addAddress/{id}", person.getPid())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(asJsonString(person)))
                		.andExpect(status().isOk());       
       
    }


    @Test
    public void test_update_address_success() throws Exception {
    	Set<Address> addressSet = new HashSet<Address>(Arrays.asList(new Address(1,"KPHB","HYDERABAD","TELANGANA","500030")));
		Address newAddress = new Address(1,"KPHB_UPDATED","HYDERABAD_UPDATED","TELANGANA","500030"); 
    	Person person = new Person(1, "Subbu", "Palisetty",addressSet);
    	when(personDAO.save(person)).thenReturn(person);
        when(personDAO.findById(person.getPid())).thenReturn(Optional.of(person));

        mockMvc.perform(
                put("/api/updateAddress/{id}", person.getPid())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(asJsonString(newAddress)))
                .andExpect(status().isOk());
    }
   
    public static String asJsonString(final Object obj) {
        try {
            return new ObjectMapper().writeValueAsString(obj);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}