package org.example.controller;

import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletResponse;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.example.common.Result;
import org.example.service.StatisticsService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.io.OutputStream;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/statistics")
public class StatisticsController {
    
    @Resource
    private StatisticsService statisticsService;
    
    /**
     * 获取热门活动TOP5
     */
    @GetMapping("/topActivities")
    public Result getTopActivities() {
        List<Map<String, Object>> topActivities = statisticsService.getTopActivities(5);
        return Result.success(topActivities);
    }
    
    /**
     * 获取志愿者参与活动次数排行榜
     */
    @GetMapping("/volunteerRanking")
    public Result getVolunteerRanking() {
        List<Map<String, Object>> volunteerRanking = statisticsService.getVolunteerRanking();
        return Result.success(volunteerRanking);
    }
    
    /**
     * 导出热门活动数据到Excel
     */
    @GetMapping("/exportTopActivities")
    public void exportTopActivities(HttpServletResponse response) throws IOException {
        List<Map<String, Object>> topActivities = statisticsService.getTopActivities(10); // 导出Top10
        
        // 创建工作簿和工作表
        Workbook workbook = new XSSFWorkbook();
        Sheet sheet = workbook.createSheet("热门活动排行榜");
        
        // 创建标题行样式
        CellStyle headerStyle = workbook.createCellStyle();
        Font headerFont = workbook.createFont();
        headerFont.setBold(true);
        headerStyle.setFont(headerFont);
        
        // 创建标题行
        Row headerRow = sheet.createRow(0);
        Cell headerCell1 = headerRow.createCell(0);
        headerCell1.setCellValue("排名");
        headerCell1.setCellStyle(headerStyle);
        
        Cell headerCell2 = headerRow.createCell(1);
        headerCell2.setCellValue("活动ID");
        headerCell2.setCellStyle(headerStyle);
        
        Cell headerCell3 = headerRow.createCell(2);
        headerCell3.setCellValue("活动名称");
        headerCell3.setCellStyle(headerStyle);
        
        Cell headerCell4 = headerRow.createCell(3);
        headerCell4.setCellValue("报名人数");
        headerCell4.setCellStyle(headerStyle);
        
        // 添加数据行
        int rowNum = 1;
        for (Map<String, Object> activity : topActivities) {
            Row row = sheet.createRow(rowNum++);
            row.createCell(0).setCellValue(rowNum - 1); // 排名
            row.createCell(1).setCellValue(String.valueOf(activity.get("activityId")));
            row.createCell(2).setCellValue(String.valueOf(activity.get("activityName")));
            row.createCell(3).setCellValue(Integer.parseInt(String.valueOf(activity.get("signupCount"))));
        }
        
        // 自动调整列宽
        for (int i = 0; i < 4; i++) {
            sheet.autoSizeColumn(i);
        }
        
        // 设置响应头
        String fileName = URLEncoder.encode("热门活动排行榜", StandardCharsets.UTF_8);
        response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
        response.setHeader("Content-Disposition", "attachment; filename=" + fileName + ".xlsx");
        
        // 写入响应输出流
        try (OutputStream outputStream = response.getOutputStream()) {
            workbook.write(outputStream);
        } finally {
            workbook.close();
        }
    }
    
    /**
     * 导出志愿者参与排行榜数据到Excel
     */
    @GetMapping("/exportVolunteerRanking")
    public void exportVolunteerRanking(HttpServletResponse response) throws IOException {
        List<Map<String, Object>> volunteerRanking = statisticsService.getVolunteerRanking();
        
        // 创建工作簿和工作表
        Workbook workbook = new XSSFWorkbook();
        Sheet sheet = workbook.createSheet("志愿者参与排行榜");
        
        // 创建标题行样式
        CellStyle headerStyle = workbook.createCellStyle();
        Font headerFont = workbook.createFont();
        headerFont.setBold(true);
        headerStyle.setFont(headerFont);
        
        // 创建标题行
        Row headerRow = sheet.createRow(0);
        Cell headerCell1 = headerRow.createCell(0);
        headerCell1.setCellValue("排名");
        headerCell1.setCellStyle(headerStyle);
        
        Cell headerCell2 = headerRow.createCell(1);
        headerCell2.setCellValue("志愿者ID");
        headerCell2.setCellStyle(headerStyle);
        
        Cell headerCell3 = headerRow.createCell(2);
        headerCell3.setCellValue("志愿者姓名");
        headerCell3.setCellStyle(headerStyle);
        
        Cell headerCell4 = headerRow.createCell(3);
        headerCell4.setCellValue("参与活动次数");
        headerCell4.setCellStyle(headerStyle);
        
        // 添加数据行
        int rowNum = 1;
        for (Map<String, Object> volunteer : volunteerRanking) {
            Row row = sheet.createRow(rowNum++);
            row.createCell(0).setCellValue(rowNum - 1); // 排名
            row.createCell(1).setCellValue(String.valueOf(volunteer.get("volunteerId")));
            row.createCell(2).setCellValue(String.valueOf(volunteer.get("volunteerName")));
            row.createCell(3).setCellValue(Integer.parseInt(String.valueOf(volunteer.get("activityCount"))));
        }
        
        // 自动调整列宽
        for (int i = 0; i < 4; i++) {
            sheet.autoSizeColumn(i);
        }
        
        // 设置响应头
        String fileName = URLEncoder.encode("志愿者参与排行榜", StandardCharsets.UTF_8);
        response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
        response.setHeader("Content-Disposition", "attachment; filename=" + fileName + ".xlsx");
        
        // 写入响应输出流
        try (OutputStream outputStream = response.getOutputStream()) {
            workbook.write(outputStream);
        } finally {
            workbook.close();
        }
    }
} 