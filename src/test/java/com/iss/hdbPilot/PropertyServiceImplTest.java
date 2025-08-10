package com.iss.hdbPilot;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.iss.hdbPilot.mapper.PropertyImageMapper;
import com.iss.hdbPilot.mapper.PropertyMapper;
import com.iss.hdbPilot.model.dto.ListingStatusCount;
import com.iss.hdbPilot.model.dto.MonthlyListingCount;
import com.iss.hdbPilot.model.dto.PropertyFilterRequest;
import com.iss.hdbPilot.model.entity.Property;
import com.iss.hdbPilot.model.vo.PropertyVO;
import com.iss.hdbPilot.service.impl.PropertyServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import java.util.*;

class PropertyServiceImplTest {

    @InjectMocks
    private PropertyServiceImpl propertyService;

    @Mock
    private PropertyMapper propertyMapper;

    @Mock
    private PropertyImageMapper propertyImageMapper;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }


    @Test
    void testListPendingPropertiesByPage_callRealPrivateMethod() {
        long current = 1;
        long size = 10;

        PropertyFilterRequest request = new PropertyFilterRequest();
        request.setSellerId("seller123");
        request.setAddress("Main St");
        request.setTown("TownA");
        request.setBedroomNumber("3");  // 按DTO类型调整
        request.setBathroomNumber("2");

        Page<Property> mockPage = new Page<>(current, size);
        Property property = new Property();
        property.setId(100L);
        mockPage.setRecords(Collections.singletonList(property));
        mockPage.setTotal(1);

        when(propertyMapper.selectPage(any(Page.class), any())).thenReturn(mockPage);

        // 直接调用真实 service，getPropertyImageEntities 是 private，真实调用
        Page<PropertyVO> result = propertyService.listPendingPropertiesByPage(current, size, request);

        assertNotNull(result);
        assertEquals(1, result.getTotal());
        assertEquals(1, result.getRecords().size());
    }


    @Test
    void testReviewProperty_Approved() {
        Long id = 10L;
        Boolean approved = true;

        PropertyServiceImpl spyService = Mockito.spy(propertyService);

        doReturn(true).when(spyService).update(any(Property.class), any(UpdateWrapper.class));

        Boolean result = spyService.reviewProperty(id, approved);

        assertTrue(result);

        ArgumentCaptor<Property> captor = ArgumentCaptor.forClass(Property.class);
        verify(spyService).update(captor.capture(), any(UpdateWrapper.class));
        assertEquals("available", captor.getValue().getStatus());
    }



    @Test
    void testReviewProperty_Rejected() {
        Long id = 10L;
        Boolean approved = false;

        PropertyServiceImpl spyService = Mockito.spy(propertyService);

        doReturn(true).when(spyService).update(any(Property.class), any(UpdateWrapper.class));

        Boolean result = spyService.reviewProperty(id, approved);

        assertTrue(result);

        ArgumentCaptor<Property> captor = ArgumentCaptor.forClass(Property.class);
        verify(spyService).update(captor.capture(), any(UpdateWrapper.class));
        assertEquals("rejected", captor.getValue().getStatus());
    }


    @Test
    void testCountAll() {
        when(propertyMapper.selectCount(null)).thenReturn(15L);

        int count = propertyService.countAll();

        assertEquals(15, count);
    }

    @Test
    void testCountByStatus() {
        when(propertyMapper.selectCount(any(QueryWrapper.class))).thenReturn(5L);

        int count = propertyService.countByStatus("available");

        assertEquals(5, count);
    }

    @Test
    void testCalculateListingGrowth() {
        when(propertyMapper.countThisMonth()).thenReturn(10L);
        when(propertyMapper.countLastMonth()).thenReturn(5L);

        Double growth = propertyService.calculateListingGrowth();

        assertEquals(100.0, growth);

        // 测试 lastMonth == 0 且 thisMonth == 0
        when(propertyMapper.countThisMonth()).thenReturn(0L);
        when(propertyMapper.countLastMonth()).thenReturn(0L);

        growth = propertyService.calculateListingGrowth();
        assertEquals(0.0, growth);

        // 测试 lastMonth == 0 且 thisMonth != 0
        when(propertyMapper.countThisMonth()).thenReturn(7L);
        when(propertyMapper.countLastMonth()).thenReturn(0L);

        growth = propertyService.calculateListingGrowth();
        assertEquals(100.0, growth);
    }

    @Test
    void testGetMonthlyListingCounts() {
        List<MonthlyListingCount> counts = new ArrayList<>();
        when(propertyMapper.getMonthlyListingCounts(2023)).thenReturn(counts);

        List<MonthlyListingCount> result = propertyService.getMonthlyListingCounts(2023);

        assertSame(counts, result);
    }

    @Test
    void testGetStatusDistribution() {
        List<ListingStatusCount> distribution = new ArrayList<>();
        when(propertyMapper.getStatusDistribution()).thenReturn(distribution);

        List<ListingStatusCount> result = propertyService.getStatusDistribution();

        assertSame(distribution, result);
    }
}
