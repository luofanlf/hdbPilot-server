package com.iss.hdbPilot;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.iss.hdbPilot.model.dto.PropertyQueryRequest;
import com.iss.hdbPilot.model.entity.Property;
import com.iss.hdbPilot.model.entity.PropertyImage;
import com.iss.hdbPilot.model.vo.PropertyVO;
import com.iss.hdbPilot.service.PropertyService;
import com.iss.hdbPilot.service.impl.PropertyServiceImpl;
import com.iss.hdbPilot.mapper.PropertyMapper;
import com.iss.hdbPilot.mapper.PropertyImageMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class PropertySearchTest {

    @Mock
    private PropertyMapper propertyMapper;

    @Mock
    private PropertyImageMapper propertyImageMapper;

    @InjectMocks
    private PropertyServiceImpl propertyService;

    private List<Property> mockProperties;
    private List<PropertyImage> mockImages;

    @BeforeEach
    public void setUp() {
        // 初始化模拟数据
        mockProperties = Arrays.asList(
            createMockProperty(1L, "Ang Mo Kio 4-Room HDB Flat", "Ang Mo Kio", "560210", 4, 2, 92.0f, 1986, 550000f),
            createMockProperty(2L, "Bedok 5-Room HDB Flat", "Bedok", "460506", 5, 2, 121.0f, 1980, 680000f),
            createMockProperty(3L, "Clementi 3-Room HDB Flat", "Clementi", "120372", 3, 1, 67.0f, 1982, 420000f),
            createMockProperty(4L, "Tampines 4-Room HDB Flat", "Tampines", "520833", 4, 2, 104.0f, 1992, 610000f),
            createMockProperty(5L, "Yishun 5-Room HDB Flat", "Yishun", "760759", 5, 2, 122.0f, 1988, 595000f),
            createMockProperty(6L, "Sengkang 4-Room HDB Flat", "Sengkang", "540291", 4, 2, 93.0f, 2012, 580000f),
            createMockProperty(7L, "Punggol 5-Room HDB Flat", "Punggol", "820673", 5, 2, 112.0f, 2015, 750000f),
            createMockProperty(8L, "Bishan 4-Room HDB Flat", "Bishan", "570191", 4, 2, 84.0f, 1987, 730000f),
            createMockProperty(9L, "Toa Payoh 3-Room HDB Flat", "Toa Payoh", "310125", 3, 1, 68.0f, 1972, 1.0f),
            createMockProperty(10L, "Jurong West Executive HDB", "Jurong West", "640987", 5, 3, 142.0f, 1995, 810000f)
        );

        mockImages = Arrays.asList(
            createMockImage(1L, 1L, "https://example.com/image1.jpg"),
            createMockImage(2L, 1L, "https://example.com/image2.jpg"),
            createMockImage(3L, 2L, "https://example.com/image3.jpg")
        );
    }

    private Property createMockProperty(Long id, String title, String town, String postalCode, 
                                      Integer bedrooms, Integer bathrooms, Float area, 
                                      Integer year, Float price) {
        Property property = new Property();
        property.setId(id);
        property.setListingTitle(title);
        property.setTown(town);
        property.setPostalCode(postalCode);
        property.setBedroomNumber(bedrooms);
        property.setBathroomNumber(bathrooms);
        property.setFloorAreaSqm(area);
        property.setTopYear(year);
        property.setResalePrice(price);
        property.setStatus("available");
        property.setCreatedAt(LocalDateTime.now());
        property.setUpdatedAt(LocalDateTime.now());
        return property;
    }

    private PropertyImage createMockImage(Long id, Long propertyId, String imageUrl) {
        PropertyImage image = new PropertyImage();
        image.setId(id);
        image.setPropertyId(propertyId);
        image.setImageUrl(imageUrl);
        image.setCreatedAt(LocalDateTime.now());
        image.setUpdatedAt(LocalDateTime.now());
        return image;
    }

    @Test
    public void testSearchByTitle() {
        // 准备测试数据
        PropertyQueryRequest request = new PropertyQueryRequest();
        request.setPageNum(1);
        request.setPageSize(10);
        request.setListingTitle("HDB");

        // 过滤包含"HDB"的房源
        List<Property> filteredProperties = mockProperties.stream()
            .filter(p -> p.getListingTitle().toLowerCase().contains("hdb"))
            .toList();

        // Mock Mapper 行为 - 只Mock实际使用的方法
        when(propertyMapper.selectPage(any(), any())).thenReturn(createMockPage(filteredProperties, 1, 10));

        // 执行测试
        Page<PropertyVO> result = propertyService.search(request);
        
        // 验证结果
        assertNotNull(result);
        assertNotNull(result.getRecords());
        assertEquals(filteredProperties.size(), result.getRecords().size());
        
        // 验证所有结果都包含"HDB"
        result.getRecords().forEach(property -> {
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

        // 过滤指定邮政编码的房源
        List<Property> filteredProperties = mockProperties.stream()
            .filter(p -> "560210".equals(p.getPostalCode()))
            .toList();

        when(propertyMapper.selectPage(any(), any())).thenReturn(createMockPage(filteredProperties, 1, 10));

        Page<PropertyVO> result = propertyService.search(request);
        
        assertNotNull(result);
        assertNotNull(result.getRecords());
        assertEquals(filteredProperties.size(), result.getRecords().size());
        
        result.getRecords().forEach(property -> {
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

        // 过滤卧室数量在范围内的房源
        List<Property> filteredProperties = mockProperties.stream()
            .filter(p -> p.getBedroomNumber() >= 3 && p.getBedroomNumber() <= 5)
            .toList();

        when(propertyMapper.selectPage(any(), any())).thenReturn(createMockPage(filteredProperties, 1, 10));

        Page<PropertyVO> result = propertyService.search(request);
        
        assertNotNull(result);
        assertNotNull(result.getRecords());
        assertEquals(filteredProperties.size(), result.getRecords().size());
        
        result.getRecords().forEach(property -> {
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

        // 过滤浴室数量在范围内的房源
        List<Property> filteredProperties = mockProperties.stream()
            .filter(p -> p.getBathroomNumber() >= 1 && p.getBathroomNumber() <= 2)
            .toList();

        when(propertyMapper.selectPage(any(), any())).thenReturn(createMockPage(filteredProperties, 1, 10));

        Page<PropertyVO> result = propertyService.search(request);
        
        assertNotNull(result);
        assertNotNull(result.getRecords());
        assertEquals(filteredProperties.size(), result.getRecords().size());
        
        result.getRecords().forEach(property -> {
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

        // 过滤价格在范围内的房源
        List<Property> filteredProperties = mockProperties.stream()
            .filter(p -> p.getResalePrice() >= 400000f && p.getResalePrice() <= 600000f)
            .toList();

        when(propertyMapper.selectPage(any(), any())).thenReturn(createMockPage(filteredProperties, 1, 10));

        Page<PropertyVO> result = propertyService.search(request);
        
        assertNotNull(result);
        assertNotNull(result.getRecords());
        assertEquals(filteredProperties.size(), result.getRecords().size());
        
        result.getRecords().forEach(property -> {
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

        // 过滤建筑面积在范围内的房源
        List<Property> filteredProperties = mockProperties.stream()
            .filter(p -> p.getFloorAreaSqm() >= 80f && p.getFloorAreaSqm() <= 120f)
            .toList();

        when(propertyMapper.selectPage(any(), any())).thenReturn(createMockPage(filteredProperties, 1, 10));

        Page<PropertyVO> result = propertyService.search(request);
        
        assertNotNull(result);
        assertNotNull(result.getRecords());
        assertEquals(filteredProperties.size(), result.getRecords().size());
        
        result.getRecords().forEach(property -> {
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

        // 过滤顶层年份在范围内的房源
        List<Property> filteredProperties = mockProperties.stream()
            .filter(p -> p.getTopYear() >= 1980 && p.getTopYear() <= 1990)
            .toList();

        when(propertyMapper.selectPage(any(), any())).thenReturn(createMockPage(filteredProperties, 1, 10));

        Page<PropertyVO> result = propertyService.search(request);
        
        assertNotNull(result);
        assertNotNull(result.getRecords());
        assertEquals(filteredProperties.size(), result.getRecords().size());
        
        result.getRecords().forEach(property -> {
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

        // 过滤指定城镇的房源
        List<Property> filteredProperties = mockProperties.stream()
            .filter(p -> "Ang Mo Kio".equals(p.getTown()))
            .toList();

        when(propertyMapper.selectPage(any(), any())).thenReturn(createMockPage(filteredProperties, 1, 10));

        Page<PropertyVO> result = propertyService.search(request);
        
        assertNotNull(result);
        assertNotNull(result.getRecords());
        assertEquals(filteredProperties.size(), result.getRecords().size());
        
        result.getRecords().forEach(property -> {
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

        // 过滤满足多个条件的房源
        List<Property> filteredProperties = mockProperties.stream()
            .filter(p -> p.getListingTitle().toLowerCase().contains("hdb") &&
                        p.getBedroomNumber() >= 3 &&
                        p.getResalePrice() <= 700000f)
            .toList();

        when(propertyMapper.selectPage(any(), any())).thenReturn(createMockPage(filteredProperties, 1, 10));

        Page<PropertyVO> result = propertyService.search(request);
        
        assertNotNull(result);
        assertNotNull(result.getRecords());
        assertEquals(filteredProperties.size(), result.getRecords().size());
        
        result.getRecords().forEach(property -> {
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

        when(propertyMapper.selectPage(any(), any())).thenReturn(createMockPage(mockProperties, 1, 10));

        Page<PropertyVO> result = propertyService.search(request);
        
        assertNotNull(result);
        assertNotNull(result.getRecords());
        assertEquals(mockProperties.size(), result.getTotal());
        assertTrue(result.getRecords().size() > 0, "应该有搜索结果");
    }

    @Test
    public void testSearchPagination() {
        PropertyQueryRequest request = new PropertyQueryRequest();
        request.setPageNum(1);
        request.setPageSize(5);

        // 只返回前5条记录来模拟分页
        List<Property> pagedProperties = mockProperties.subList(0, Math.min(5, mockProperties.size()));
        when(propertyMapper.selectPage(any(), any())).thenReturn(createMockPage(pagedProperties, 1, 5));

        Page<PropertyVO> result = propertyService.search(request);
        
        assertNotNull(result);
        assertNotNull(result.getRecords());
        assertEquals(1, result.getCurrent(), "当前页应该是1");
        assertEquals(5, result.getSize(), "页大小应该是5");
        assertTrue(result.getRecords().size() <= 5, "当前页记录数应该不超过5");
    }

    private Page<Property> createMockPage(List<Property> properties, int current, int size) {
        Page<Property> page = new Page<>(current, size);
        page.setRecords(properties);
        page.setTotal(properties.size());
        return page;
    }
} 