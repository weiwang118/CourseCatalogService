package com.kotlinspring.controller

import com.kotlinspring.dto.CourseDTO
import com.kotlinspring.entity.Course
import com.kotlinspring.repository.CourseRepository
import com.kotlinspring.util.courseEntityList
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.web.reactive.server.WebTestClient

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
@AutoConfigureWebTestClient
class CourseControllerIntgTest {

    @Autowired
    lateinit var webTestClient:WebTestClient

    @Autowired
    lateinit var courseRepository: CourseRepository

    @BeforeEach
    fun setUp(){
        courseRepository.deleteAll()
        courseRepository.saveAll(courseEntityList())
    }
    @Test
    fun addCourse(){
        val courseDTO = CourseDTO(null, "name1", "category1")

        val savedCourseDTO = webTestClient
            .post()
            .uri("/v1/courses")
            .bodyValue(courseDTO)
            .exchange()
            .expectStatus().isCreated
            .expectBody(CourseDTO::class.java)
            .returnResult()
            .responseBody

        Assertions.assertTrue{
            savedCourseDTO!!.id != null
        }
    }

    @Test
    fun retrieveAllCourses(){
        val  CourseDTOs = webTestClient
            .get()
            .uri("/v1/courses")
            .exchange()
            .expectStatus().isOk
            .expectBodyList(CourseDTO::class.java)
            .returnResult()
            .responseBody

        assertEquals(3, CourseDTOs!!.size)
    }

    @Test
    fun updateCourse(){
        val existedCourse = Course(null, "old name", "dev1")
        courseRepository.save(existedCourse)

        val newCourse = CourseDTO(null, "new name","dev2")

        val updatedCourseDTO = webTestClient
            .put()
            .uri("/v1/courses/{course_id}", existedCourse.id)
            .bodyValue(newCourse)
            .exchange()
            .expectStatus().isOk
            .expectBody(CourseDTO::class.java)
            .returnResult()
            .responseBody

        assertEquals("new name", updatedCourseDTO!!.name)
    }

    @Test
    fun deleteCourse(){
        val existedCourse = Course(null, "old name", "dev1")
        courseRepository.save(existedCourse)


        val deleteCourseDTO = webTestClient
            .delete()
            .uri("/v1/courses/{course_id}", existedCourse.id)
            .exchange()
            .expectStatus().isNoContent
    }
}