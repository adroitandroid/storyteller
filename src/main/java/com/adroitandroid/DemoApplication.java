package com.adroitandroid;

import com.adroitandroid.model.Student;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.util.HashMap;

@SpringBootApplication
public class DemoApplication {

	public static HashMap<Long, Student> hmStudent;

	public static void main(String[] args) {
		SpringApplication.run(DemoApplication.class, args);

		hmStudent= new HashMap<>();
		Student one=new Student(1, "John","math");
		hmStudent.put(one.getId(),one);
		Student two=new Student(2, "Jane","history");
		hmStudent.put(two.getId(),two);
	}
}
