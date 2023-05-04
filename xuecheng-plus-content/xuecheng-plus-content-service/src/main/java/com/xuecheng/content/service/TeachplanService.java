package com.xuecheng.content.service;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.xuecheng.content.model.dto.SaveTeachplanDto;
import com.xuecheng.content.model.dto.TeachplanDto;

import java.util.List;

public interface TeachplanService {
    /**
     * 查询课程计划
     * @param courseId 课程id
     * @return
     */
    public List<TeachplanDto> findTeachplanTree(Long courseId);

    /**
     * 新增/修改/保存课程计划
     * @param saveTeachplanDto
     */
    public void saveTeachplan(SaveTeachplanDto saveTeachplanDto);

    /**
     * 删除课程计划
     * @param teachplanId
     * @return
     */
    public void deleteTeachplan(Long teachplanId);

    /**
     * 上下移动teachplan
     * @param teachplanId 课程计划id
     * @param moveType 移动类型
     */
    public void moveTeachplan(Long teachplanId,String moveType);
}
