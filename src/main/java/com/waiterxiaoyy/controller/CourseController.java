package com.waiterxiaoyy.controller;

import cn.hutool.core.lang.UUID;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.waiterxiaoyy.common.lang.Result;
import com.waiterxiaoyy.entity.*;
import com.waiterxiaoyy.mapper.SysTermCourseMapper;
import com.waiterxiaoyy.service.SysDeptService;
import com.waiterxiaoyy.service.SysDistStudentService;
import com.waiterxiaoyy.service.SysTermCourseService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 功能描述：
 *
 * @Author WaiterXiaoYY
 * @Date 2022/3/5 22:05
 * @Version 1.0
 */
@RestController
@RequestMapping("/course")
public class CourseController {

    @Autowired
    SysTermCourseService sysTermCourseService;

    @Autowired
    SysDeptService sysDeptService;

    @Autowired
    SysDistStudentService sysDistStudentService;

    @Autowired
    SysTermCourseMapper sysTermCourseMapper;

    @GetMapping("/getTermById/{termId}")
    public Result getTermById(@PathVariable("termId") Long termId) {
        SysTermCourse sysTermCourse = sysTermCourseService.getById(termId);
        return Result.succ(sysTermCourse);
    }

    @GetMapping("/getCourseById/{courseId}")
    public Result getTermByCourseId(@PathVariable("courseId") Long courseId) {
        SysTermCourse sysTermCourse = sysTermCourseService.getById(courseId);
        return Result.succ(sysTermCourse);
    }

    @GetMapping("/getCourseCollege/{collegeId}")
    public Result getCourseCollege(@PathVariable("collegeId") Long collegeId) {
        SysDept sysDept = sysDeptService.getById(collegeId);
        return Result.succ(sysDept);
    }

    @PostMapping("/addTerm")
    public Result addTerm(@RequestBody SysTermCourse sysTermCourse) {
        sysTermCourse.setCreated(LocalDateTime.now());
        sysTermCourse.setParentId(0L);
        sysTermCourse.setType(0);
        if(sysTermCourseService.save(sysTermCourse)) {
            return Result.succ("创建学期成功");
        }
        return Result.fail("创建新学期失败");
    }

    @PostMapping("/updateTerm")
    public Result updateTerm(@RequestBody SysTermCourse sysTermCourse) {
        sysTermCourse.setUpdated(LocalDateTime.now());
        sysTermCourseService.updateById(sysTermCourse);
        return Result.succ("更新学期成功");
    }

    @GetMapping("/getTermList")
    public Result getTermList() {
        List<SysTermCourse> sysTermList = sysTermCourseService.list(new QueryWrapper<SysTermCourse>().eq("parent_id", 0).orderByAsc("orderNum"));
        return Result.succ(sysTermList);
    }

    @PostMapping("/addCourse")
    public Result addCourse(@RequestBody SysTermCourse sysTermCourse) {

        sysTermCourse.setType(1);
        sysTermCourse.setCourseId(UUID.randomUUID().toString().substring(0, 10));
        sysTermCourse.setCreated(LocalDateTime.now());

        if(sysTermCourseService.save(sysTermCourse)) {
            return Result.succ("创建课程成功");
        }
        return Result.fail("创建课程失败");

    }


    @GetMapping("/getTermCourse")
    public Result getTermCourse() {
        return sysTermCourseService.getTermCourseClass();
    }

    @GetMapping("/getStuTermCourseList")
    public Result getStuTermCourseList() {
        String username = SecurityContextHolder.getContext().getAuthentication().getPrincipal().toString();
        return sysTermCourseService.getStuTermCourseList(username);
    }

    @GetMapping("/getTeacTermCourseList")
    public Result getTeacTermCourseList() {
        String username = SecurityContextHolder.getContext().getAuthentication().getPrincipal().toString();
        return sysTermCourseService.getTeacTermCourseList(username);
    }

    @PostMapping("/updateCourse")
    public Result updateCourse(@RequestBody SysTermCourse sysTermCourse) {
        sysTermCourse.setUpdated(LocalDateTime.now());
        sysTermCourseService.updateById(sysTermCourse);
        return Result.succ("更新课程成功");
    }

    @PostMapping("/addCourseClass")
    public Result addCourseClass(@RequestBody SysTermCourse sysTermCourse) {
        sysTermCourse.setType(2);
        sysTermCourse.setClassId(sysTermCourse.getCourseId() + UUID.randomUUID().toString().substring(0, 3));
        sysTermCourse.setCreated(LocalDateTime.now());
        sysTermCourseService.save(sysTermCourse);
        return Result.succ(sysTermCourse);
    }

    @PostMapping("/updateCourseClass")
    public Result updateCourseClass(@RequestBody SysTermCourse sysTermCourse) {
        sysTermCourse.setUpdated(LocalDateTime.now());
        sysTermCourseService.updateById(sysTermCourse);
        return Result.succ("更新班级信息成功");
    }

    @PostMapping("/delete/{id}")
    public Result delete(@PathVariable("id") Long id) {
        int count = sysTermCourseService.count(new QueryWrapper<SysTermCourse>().eq("parent_id", id));
        if (count > 0) {
            return Result.fail("请先删除下级内容");
        }


        sysTermCourseService.removeById(id);

        // 同步删除中间关联表(记得在此处处理）


        return Result.succ("");
    }

    @GetMapping("/getCourseClassList")
    public Result getCourseClassList(Long courseId, String query) {
        List<SysTermCourse> sysTermCourseList = sysTermCourseService.list(
                new QueryWrapper<SysTermCourse>().eq("parent_id", courseId)
                        .orderByAsc("orderNum")
                        .like(StrUtil.isNotBlank(query), "name", query));
        return Result.succ(sysTermCourseList);

    }

    @GetMapping("/dist/{id}")
    public Result info(@PathVariable("id") Long id) {
        SysTermCourse sysTermCourse = sysTermCourseService.getById(id);
        List<SysDistStudent> sysDistStudents = sysDistStudentService.list(new QueryWrapper<SysDistStudent>().eq("class_id", id));
        List<String> studentIds = sysDistStudents.stream().map(p -> p.getStudentId()).collect(Collectors.toList());

        sysTermCourse.setStudentIdList(studentIds);
        return Result.succ(sysTermCourse);
    }

    @Transactional
    @PostMapping("/dist/submit/{classId}")
    public Result distStudent(@PathVariable("classId") Long classId, @RequestBody String[] studentIds) {

        List<SysDistStudent> sysDistStudents = new ArrayList<>();

        Arrays.stream(studentIds).forEach(studentId -> {
            SysDistStudent sysDistStudent = new SysDistStudent();
            sysDistStudent.setClassId(classId);
            sysDistStudent.setStudentId(studentId);

            sysDistStudents.add(sysDistStudent);
        });

        // 先删除原来的记录，再保存新的
        sysDistStudentService.remove(new QueryWrapper<SysDistStudent>().eq("class_id", classId));
        sysDistStudentService.saveBatch(sysDistStudents);
        return Result.succ(studentIds);
    }


    /**
     * 课程详情操作
     */

    @GetMapping("/getClassStudent/{classId}")
    public Result getClassStudent(@PathVariable("classId") Long classId) {
        List<SysStudent> sysStudents = null;
        try {
            sysStudents = sysTermCourseMapper.getClassStudent(classId);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return Result.succ(sysStudents);
    }

}
