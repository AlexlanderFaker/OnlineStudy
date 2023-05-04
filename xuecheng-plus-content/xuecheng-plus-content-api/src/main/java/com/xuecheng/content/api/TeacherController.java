package com.xuecheng.content.api;

import com.xuecheng.content.model.po.CourseBase;
import com.xuecheng.content.model.po.CourseTeacher;
import com.xuecheng.content.service.TeacherService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * @author Mr.Tang
 * @version 1.0
 * @description
 * @date ${LocalDateTime.now()}
 */
@Api(value = "老师信息相关接口",tags = "老师信息相关接口")
@RestController
public class TeacherController {

    @Autowired
    private TeacherService teacherService;

    @ApiOperation("查询老师")
    @GetMapping("/courseTeacher/list/{id}")
    public List<CourseTeacher> selectTeacher(@PathVariable Long id){
        List<CourseTeacher> teachers = teacherService.getTeacher(id);
        return teachers;
    }

    @ApiOperation("添加老师")
    @PostMapping("/courseTeacher")
    public CourseTeacher insertTeacher(@RequestBody CourseTeacher courseTeacher){
        teacherService.insertTeacher(courseTeacher);
        return courseTeacher;
    }

    @ApiOperation("删除老师")
    @DeleteMapping("/courseTeacher/course/{courseId}/{teacherId}")
    public void deleteTeacher(@PathVariable Long courseId,@PathVariable Long teacherId){
        teacherService.deleteTeacher(courseId,teacherId);
    }
}
