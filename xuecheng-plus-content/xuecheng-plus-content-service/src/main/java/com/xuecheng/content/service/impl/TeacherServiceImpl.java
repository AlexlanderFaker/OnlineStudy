package com.xuecheng.content.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.xuecheng.base.exception.XueChengPlusException;
import com.xuecheng.content.mapper.CourseTeacherMapper;
import com.xuecheng.content.model.po.CourseTeacher;
import com.xuecheng.content.service.TeacherService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

/**
 * @author Mr.Tang
 * @version 1.0
 * @description
 * @date ${LocalDateTime.now()}
 */
@Service
public class TeacherServiceImpl implements TeacherService {
    @Autowired
    private CourseTeacherMapper teacherMapper;

    @Override
    public List<CourseTeacher> getTeacher(Long courseId) {
        LambdaQueryWrapper<CourseTeacher> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(CourseTeacher::getCourseId, courseId);
        teacherMapper.selectList(queryWrapper);
        return teacherMapper.selectList(queryWrapper);
    }

    @Override
    public CourseTeacher insertTeacher(CourseTeacher courseTeacher) {
        Long id = courseTeacher.getId();
        CourseTeacher teacher = teacherMapper.selectById(id);
        if (teacher == null) {
            courseTeacher.setCreateDate(LocalDateTime.now());
            teacherMapper.insert(courseTeacher);
        }
        teacherMapper.updateById(courseTeacher);

        return courseTeacher;
    }

    @Override
    public void deleteTeacher(Long courseId, Long teacherId) {
        teacherMapper.deleteById(teacherId);
    }
}
