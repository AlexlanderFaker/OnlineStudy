package com.xuecheng.content.service;

import com.xuecheng.content.model.po.CourseTeacher;

import java.util.List;

/**
 * @author Mr.Tang
 * @version 1.0
 * @description
 * @date ${LocalDateTime.now()}
 */
public interface TeacherService {

    /**
     * 根据课程id查找所对应的老师列表
     * @param courseId 课程id
     * @return 老师列表
     */
    public List<CourseTeacher> getTeacher(Long courseId);

    /**
     * 新增和更改老师信息
     * @param courseTeacher 老师信息
     * @return 老师信息
     */
    public CourseTeacher insertTeacher(CourseTeacher courseTeacher);

    /**
     * 删除老师信息（根据课程id和老师id）
     * @param courseId 课程id
     * @param teacherId 老师id
     */
    public void deleteTeacher(Long courseId, Long teacherId);
}
