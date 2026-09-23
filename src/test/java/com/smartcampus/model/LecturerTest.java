package com.smartcampus.model;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class LecturerTest {

    @Test
    void shouldAllowBeanPropertyBindingForLecturerFields() {
        Lecturer lecturer = new Lecturer();
        lecturer.setId(1L);
        lecturer.setLecturerNumber("L-101");
        lecturer.setFullName("Alice Johnson");
        lecturer.setEmail("alice@example.com");
        lecturer.setPhoneNumber("0712345678");
        lecturer.setDepartment("Computer Science");

        assertEquals(1L, lecturer.getId());
        assertEquals("L-101", lecturer.getLecturerNumber());
        assertEquals("Alice Johnson", lecturer.getFullName());
        assertEquals("alice@example.com", lecturer.getEmail());
        assertEquals("0712345678", lecturer.getPhoneNumber());
        assertEquals("Computer Science", lecturer.getDepartment());
    }
}
