package com.xuecheng.content.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.xuecheng.base.exception.XueChengPlusException;
import com.xuecheng.content.mapper.TeachplanMapper;
import com.xuecheng.content.model.dto.SaveTeachplanDto;
import com.xuecheng.content.model.dto.TeachplanDto;
import com.xuecheng.content.model.po.Teachplan;
import com.xuecheng.content.service.TeachplanService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * @author Mr.Tang
 * @version 1.0
 * @description
 * @date ${LocalDateTime.now()}
 */
@Service
public class TeachplanServiceImpl implements TeachplanService {
    @Autowired
    private TeachplanMapper teachplanMapper;

    @Override
    public List<TeachplanDto> findTeachplanTree(Long courseId) {
        List<TeachplanDto> teachplanDtos = teachplanMapper.selectTreeNodes(courseId);
        return teachplanDtos;
    }

    @Override
    public void saveTeachplan(SaveTeachplanDto saveTeachplanDto) {
        Long id = saveTeachplanDto.getId();
        if (id == null) {
            Teachplan teachplan = new Teachplan();
            BeanUtils.copyProperties(saveTeachplanDto, teachplan);
            Long parentId = saveTeachplanDto.getParentid();
            Long courseId = saveTeachplanDto.getCourseId();
            LambdaQueryWrapper<Teachplan> queryWrapper = new LambdaQueryWrapper<>();
            queryWrapper.eq(Teachplan::getCourseId, courseId).eq(Teachplan::getParentid, parentId);
            Integer integer = teachplanMapper.selectCount(queryWrapper);
            teachplan.setOrderby(integer + 1);
            teachplanMapper.insert(teachplan);
        } else {
            Teachplan teachplan = teachplanMapper.selectById(saveTeachplanDto.getId());
            BeanUtils.copyProperties(saveTeachplanDto, teachplan);
            teachplanMapper.updateById(teachplan);
        }
    }

    @Override
    public void deleteTeachplan(Long teachplanId) {
        LambdaQueryWrapper<Teachplan> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Teachplan::getParentid, teachplanId);
        Integer integer = teachplanMapper.selectCount(queryWrapper);
        if (integer > 0) {
            XueChengPlusException.cast("课程计划信息还有子级信息，无法操作");
        } else {
            teachplanMapper.deleteById(teachplanId);
        }
    }

    @Override
    public void moveTeachplan(Long teachplanId, String moveType) {
        Teachplan tar = teachplanMapper.selectById(teachplanId);
        LambdaQueryWrapper<Teachplan> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper
                .eq(Teachplan::getParentid,tar.getParentid())
                .eq(Teachplan::getCourseId, tar.getCourseId())
                .gt(moveType.equals("movedown"), Teachplan::getOrderby, tar.getOrderby())
                .lt(moveType.equals("moveup"),Teachplan::getOrderby,tar.getOrderby())
                .orderByDesc(moveType.equals("moveup"), Teachplan::getOrderby)
                .last("limit 1");
        Teachplan teachplan = teachplanMapper.selectOne(queryWrapper);//查询出被移动的teachplan
        if (teachplan==null){
            XueChengPlusException.cast("已经到边界了，不能再移动啦");
        }
        int o1=tar.getOrderby();//需要移动的teachplan
        int o2 = teachplan.getOrderby();//被移动的teachplan
        teachplan.setOrderby(o1);
        tar.setOrderby(o2);
        teachplanMapper.updateById(tar);
        teachplanMapper.updateById(teachplan);
    }
}
