package com.lagou.edu.ioc.beans;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;


@Component
public class Person {

	private int id;
	private String name;
	private int age;
	private Address address;

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public int getAge() {
		return age;
	}

	public void setAge(int age) {
		this.age = age;
	}

	public Address getAddress() {
		return address;
	}
//
//	@Autowired
	public void setAddress(Address address) {
		this.address = address;
	}

	public Person(Address address){
		this.address = address;
	}
}
