package com.xuecheng.content;

import com.xuecheng.base.model.PageParams;
import com.xuecheng.base.model.PageResult;
import com.xuecheng.content.mapper.TeachplanMapper;
import com.xuecheng.content.model.dto.QueryCourseParamsDto;
import com.xuecheng.content.model.dto.TeachplanDto;
import com.xuecheng.content.model.po.CourseBase;
import com.xuecheng.content.model.po.Teachplan;
import com.xuecheng.content.service.CourseBaseInfoService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

/**
 * @author Mr.M
 * @version 1.0
 * @description TODO
 * @date 2023/2/12 9:24
 */
@SpringBootTest
public class TeachplanServiceTests {

    @Autowired
    TeachplanMapper teachplanMapper;

    @Test
    public void select(){
        List<TeachplanDto> teachplanDtos = teachplanMapper.selectTreeNodes(117L);
        System.out.println(teachplanDtos);
    }

    @Test
    public void  test(){
        Teachplan teachplan = teachplanMapper.selectById(320 + 1);
        System.out.println(teachplan);
    }
}
