package com.project.course_project_1.repository;


import com.project.course_project_1.entity.DateTime;
import org.springframework.data.repository.CrudRepository;

public interface DateRepository extends CrudRepository<DateTime, Long> {
}
