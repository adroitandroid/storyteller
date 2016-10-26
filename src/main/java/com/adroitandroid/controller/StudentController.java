package com.adroitandroid.controller;

import com.adroitandroid.DemoApplication;
import com.adroitandroid.model.Student;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;

@RestController
@RequestMapping(value = "/student")
public class StudentController {

    @RequestMapping(value = "/", method = RequestMethod.GET)
    public HashMap<Long, Student> getAllStudents() {
        return DemoApplication.hmStudent;
    }

    @RequestMapping(value = "/add", method = RequestMethod.POST)
    public HashMap<Long, Student> addStudent(@RequestParam(value = "name") String name
            , @RequestParam(value = "subject", defaultValue = "unknown") String subject) {

        Student student = new Student(name, subject);
        DemoApplication.hmStudent.put(student.getId(), student);
        return DemoApplication.hmStudent;
//        return student;
    }

    @RequestMapping(value = "/update", method = RequestMethod.PUT)
    public Student updateStudent(@RequestBody Student student) throws Exception {

        if (DemoApplication.hmStudent.containsKey(student.getId())) {
            DemoApplication.hmStudent.put(student.getId(), student);
        } else {
            throw new Exception("Student " + student.getId() + " does not exists");
        }

        return student;
    }

    @RequestMapping(value = "/{id}", method = RequestMethod.GET)
    public Student getStudent(@PathVariable long id) {
        return DemoApplication.hmStudent.get(id);
    }

    @RequestMapping(value = "/delete/{id}", method = RequestMethod.DELETE)
    public Student deleteStudent(@PathVariable long id) throws Exception {

        Student student;

        if (DemoApplication.hmStudent.containsKey(id)) {
            student = DemoApplication.hmStudent.get(id);
            DemoApplication.hmStudent.remove(id);
        } else {
            throw new Exception("Student " + id + " does not exists");
        }
        return student;
    }
}
