package com.xuecheng.content.api;

import com.xuecheng.base.exception.XueChengPlusException;
import com.xuecheng.content.model.dto.SaveTeachplanDto;
import com.xuecheng.content.model.dto.TeachplanDto;
import com.xuecheng.content.service.TeachplanService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.websocket.server.PathParam;
import java.util.List;

/**
 * @author Mr.Tang
 * @version 1.0
 * @description 课程计划管理相关接口
 * @date ${LocalDateTime.now()}
 */
@Api(value = "课程计划编辑相关接口",tags = "课程计划编辑相关接口")
@RestController
public class TeachplanController {
    @Autowired
    private TeachplanService teachplanService;
    //查询课程计划
    @ApiOperation("查询课程计划树形结构")
    @GetMapping("/teachplan/{courseId}/tree-nodes")
    public List<TeachplanDto> getTreeNodes(@PathVariable Long courseId){
        List<TeachplanDto> teachplanDtos = teachplanService.findTeachplanTree(courseId);
        return teachplanDtos;
    }
    @ApiOperation("课程计划创建或修改")
    @PostMapping("/teachplan")
    public void saveTeachplan(@RequestBody SaveTeachplanDto saveTeachplanDto){
        teachplanService.saveTeachplan(saveTeachplanDto);
    }
    @ApiOperation("删除课程计划")
    @DeleteMapping("/teachplan/{teachplanId}")
    public void deleteTeachplan(@PathVariable Long teachplanId){
        teachplanService.deleteTeachplan(teachplanId);
    }

    @ApiOperation("课程下移接口")
    @PostMapping("/teachplan/{moveType}/{id}")
    public void moveTeachplan(@PathVariable String moveType, @PathVariable Long id){
        teachplanService.moveTeachplan(id,moveType);
    }

}
