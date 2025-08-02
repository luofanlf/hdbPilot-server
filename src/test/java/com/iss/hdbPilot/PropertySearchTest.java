package com.iss.hdbPilot;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.iss.hdbPilot.model.dto.PropertyQueryRequest;
import com.iss.hdbPilot.model.vo.PropertyVO;
import com.iss.hdbPilot.service.PropertyService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@ActiveProfiles("dev")
public class PropertySearchTest {

    @Autowired
    private PropertyService propertyService;

    @Test
    public void testSearchByTitle() {
        PropertyQueryRequest request = new PropertyQueryRequest();
        request.setPageNum(1);
        request.setPageSize(10);
        request.setListingTitle("HDB");

        Page<PropertyVO> result = propertyService.search(request);
        
        assertNotNull(result);
        assertNotNull(result.getRecords());
        System.out.println("搜索结果数量: " + result.getRecords().size());
        
        // 验证结果中是否包含包含"HDB"的标题
        result.getRecords().forEach(property -> {
            System.out.println("房源标题: " + property.getListingTitle());
            assertTrue(property.getListingTitle().toLowerCase().contains("hdb"), 
                "房源标题应该包含'HDB': " + property.getListingTitle());
        });
    }

    @Test
    public void testSearchByPostalCode() {
        PropertyQueryRequest request = new PropertyQueryRequest();
        request.setPageNum(1);
        request.setPageSize(10);
        request.setPostalCode("560210");

        Page<PropertyVO> result = propertyService.search(request);
        
        assertNotNull(result);
        assertNotNull(result.getRecords());
        System.out.println("邮政编码搜索结果数量: " + result.getRecords().size());
        
        // 验证结果中的邮政编码是否匹配
        result.getRecords().forEach(property -> {
            System.out.println("房源邮政编码: " + property.getPostalCode());
            assertEquals("560210", property.getPostalCode(), 
                "邮政编码应该匹配: " + property.getPostalCode());
        });
    }

    @Test
    public void testSearchByBedroomRange() {
        PropertyQueryRequest request = new PropertyQueryRequest();
        request.setPageNum(1);
        request.setPageSize(10);
        request.setBedroomNumberMin(3);
        request.setBedroomNumberMax(5);

        Page<PropertyVO> result = propertyService.search(request);
        
        assertNotNull(result);
        assertNotNull(result.getRecords());
        System.out.println("卧室范围搜索结果数量: " + result.getRecords().size());
        
        // 验证结果中的卧室数量是否在范围内
        result.getRecords().forEach(property -> {
            System.out.println("房源卧室数量: " + property.getBedroomNumber());
            assertTrue(property.getBedroomNumber() >= 3, 
                "卧室数量应该大于等于3: " + property.getBedroomNumber());
            assertTrue(property.getBedroomNumber() <= 5, 
                "卧室数量应该小于等于5: " + property.getBedroomNumber());
        });
    }

    @Test
    public void testSearchByBathroomRange() {
        PropertyQueryRequest request = new PropertyQueryRequest();
        request.setPageNum(1);
        request.setPageSize(10);
        request.setBathroomNumberMin(1);
        request.setBathroomNumberMax(2);

        Page<PropertyVO> result = propertyService.search(request);
        
        assertNotNull(result);
        assertNotNull(result.getRecords());
        System.out.println("浴室范围搜索结果数量: " + result.getRecords().size());
        
        // 验证结果中的浴室数量是否在范围内
        result.getRecords().forEach(property -> {
            System.out.println("房源浴室数量: " + property.getBathroomNumber());
            assertTrue(property.getBathroomNumber() >= 1, 
                "浴室数量应该大于等于1: " + property.getBathroomNumber());
            assertTrue(property.getBathroomNumber() <= 2, 
                "浴室数量应该小于等于2: " + property.getBathroomNumber());
        });
    }

    @Test
    public void testSearchByPriceRange() {
        PropertyQueryRequest request = new PropertyQueryRequest();
        request.setPageNum(1);
        request.setPageSize(10);
        request.setResalePriceMin(400000f);
        request.setResalePriceMax(600000f);

        Page<PropertyVO> result = propertyService.search(request);
        
        assertNotNull(result);
        assertNotNull(result.getRecords());
        System.out.println("价格范围搜索结果数量: " + result.getRecords().size());
        
        // 验证结果中的价格是否在范围内
        result.getRecords().forEach(property -> {
            System.out.println("房源价格: " + property.getResalePrice());
            assertTrue(property.getResalePrice() >= 400000f, 
                "价格应该大于等于400000: " + property.getResalePrice());
            assertTrue(property.getResalePrice() <= 600000f, 
                "价格应该小于等于600000: " + property.getResalePrice());
        });
    }

    @Test
    public void testSearchByFloorAreaRange() {
        PropertyQueryRequest request = new PropertyQueryRequest();
        request.setPageNum(1);
        request.setPageSize(10);
        request.setFloorAreaSqmMin(80f);
        request.setFloorAreaSqmMax(120f);

        Page<PropertyVO> result = propertyService.search(request);
        
        assertNotNull(result);
        assertNotNull(result.getRecords());
        System.out.println("建筑面积范围搜索结果数量: " + result.getRecords().size());
        
        // 验证结果中的建筑面积是否在范围内
        result.getRecords().forEach(property -> {
            System.out.println("房源建筑面积: " + property.getFloorAreaSqm());
            assertTrue(property.getFloorAreaSqm() >= 80f, 
                "建筑面积应该大于等于80: " + property.getFloorAreaSqm());
            assertTrue(property.getFloorAreaSqm() <= 120f, 
                "建筑面积应该小于等于120: " + property.getFloorAreaSqm());
        });
    }

    @Test
    public void testSearchByTopYearRange() {
        PropertyQueryRequest request = new PropertyQueryRequest();
        request.setPageNum(1);
        request.setPageSize(10);
        request.setTopYearMin(1980);
        request.setTopYearMax(1990);

        Page<PropertyVO> result = propertyService.search(request);
        
        assertNotNull(result);
        assertNotNull(result.getRecords());
        System.out.println("顶层年份范围搜索结果数量: " + result.getRecords().size());
        
        // 验证结果中的顶层年份是否在范围内
        result.getRecords().forEach(property -> {
            System.out.println("房源顶层年份: " + property.getTopYear());
            assertTrue(property.getTopYear() >= 1980, 
                "顶层年份应该大于等于1980: " + property.getTopYear());
            assertTrue(property.getTopYear() <= 1990, 
                "顶层年份应该小于等于1990: " + property.getTopYear());
        });
    }

    @Test
    public void testSearchByTown() {
        PropertyQueryRequest request = new PropertyQueryRequest();
        request.setPageNum(1);
        request.setPageSize(10);
        request.setTown("Ang Mo Kio");

        Page<PropertyVO> result = propertyService.search(request);
        
        assertNotNull(result);
        assertNotNull(result.getRecords());
        System.out.println("城镇搜索结果数量: " + result.getRecords().size());
        
        // 验证结果中的城镇是否匹配
        result.getRecords().forEach(property -> {
            System.out.println("房源城镇: " + property.getTown());
            assertEquals("Ang Mo Kio", property.getTown(), 
                "城镇应该匹配: " + property.getTown());
        });
    }

    @Test
    public void testSearchByMultipleConditions() {
        PropertyQueryRequest request = new PropertyQueryRequest();
        request.setPageNum(1);
        request.setPageSize(10);
        request.setListingTitle("HDB");
        request.setBedroomNumberMin(3);
        request.setResalePriceMax(700000f);

        Page<PropertyVO> result = propertyService.search(request);
        
        assertNotNull(result);
        assertNotNull(result.getRecords());
        System.out.println("多条件搜索结果数量: " + result.getRecords().size());
        
        // 验证结果是否满足所有条件
        result.getRecords().forEach(property -> {
            System.out.println("房源信息: " + property.getListingTitle() + 
                ", 卧室: " + property.getBedroomNumber() + 
                ", 价格: " + property.getResalePrice());
            
            assertTrue(property.getListingTitle().toLowerCase().contains("hdb"), 
                "房源标题应该包含'HDB': " + property.getListingTitle());
            assertTrue(property.getBedroomNumber() >= 3, 
                "卧室数量应该大于等于3: " + property.getBedroomNumber());
            assertTrue(property.getResalePrice() <= 700000f, 
                "价格应该小于等于700000: " + property.getResalePrice());
        });
    }

    @Test
    public void testSearchWithNoConditions() {
        PropertyQueryRequest request = new PropertyQueryRequest();
        request.setPageNum(1);
        request.setPageSize(10);

        Page<PropertyVO> result = propertyService.search(request);
        
        assertNotNull(result);
        assertNotNull(result.getRecords());
        System.out.println("无条件搜索结果数量: " + result.getRecords().size());
        System.out.println("总记录数: " + result.getTotal());
        
        // 应该返回所有房源
        assertTrue(result.getTotal() > 0, "应该有房源数据");
        assertTrue(result.getRecords().size() > 0, "应该有搜索结果");
    }

    @Test
    public void testSearchPagination() {
        PropertyQueryRequest request = new PropertyQueryRequest();
        request.setPageNum(1);
        request.setPageSize(5);

        Page<PropertyVO> result = propertyService.search(request);
        
        assertNotNull(result);
        assertNotNull(result.getRecords());
        System.out.println("分页搜索结果数量: " + result.getRecords().size());
        System.out.println("当前页: " + result.getCurrent());
        System.out.println("页大小: " + result.getSize());
        System.out.println("总记录数: " + result.getTotal());
        
        // 验证分页信息
        assertEquals(1, result.getCurrent(), "当前页应该是1");
        assertEquals(5, result.getSize(), "页大小应该是5");
        assertTrue(result.getRecords().size() <= 5, "当前页记录数应该不超过5");
    }
} 