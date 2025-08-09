package com.iss.hdbPilot.service.impl;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;
import java.util.ArrayList;

import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.iss.hdbPilot.model.dto.*;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.iss.hdbPilot.mapper.PropertyImageMapper;
import com.iss.hdbPilot.mapper.PropertyMapper;
import com.iss.hdbPilot.model.entity.Property;
import com.iss.hdbPilot.model.entity.PropertyImage;
import com.iss.hdbPilot.model.vo.PropertyVO;
import com.iss.hdbPilot.service.PropertyService;

import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import com.iss.hdbPilot.model.vo.PropertyImageVO;

@Service
public class PropertyServiceImpl extends ServiceImpl<PropertyMapper, Property> implements PropertyService {
    
    @Autowired
    private S3Client s3Client;
    @Autowired
    private PropertyMapper propertyMapper;
    @Autowired
    private PropertyImageMapper propertyImageMapper;

    @Override
    public Page<PropertyVO> list(PageRequest pageRequest) {
        int pageNum = pageRequest.getPageNum();
        int pageSize = pageRequest.getPageSize();

        // 构造分页对象 - 先查询Property实体
        Page<Property> propertyPage = new Page<>(pageNum, pageSize);

        // 构造查询条件
        LambdaQueryWrapper<Property> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Property::getStatus, "available");

        // 执行分页查询
        Page<Property> result = propertyMapper.selectPage(propertyPage, wrapper);

        // 为每个房源查询并设置图片列表
        result.getRecords().forEach(property -> {
            List<PropertyImage> images = getPropertyImageEntities(property.getId());
            property.setImageList(images);
        });

        // 转换为PropertyVO
        Page<PropertyVO> propertyVOPage = new Page<>(pageNum, pageSize, result.getTotal());
        propertyVOPage.setRecords(result.getRecords().stream()
                .map(Property::toVO)
                .collect(java.util.stream.Collectors.toList()));

        return propertyVOPage;
    }
    
    @Override
    public List<PropertyVO> listAll() {
        QueryWrapper<Property> wrapper = new QueryWrapper<>();
        wrapper.eq("status", "available");
        List<Property> properties = propertyMapper.selectList(wrapper);
        
        // 为每个房源加载图片信息
        properties.forEach(property -> {
            List<PropertyImage> images = getPropertyImageEntities(property.getId());
            property.setImageList(images);
        });
        
        return properties.stream().map(Property::toVO).collect(Collectors.toList());
    }
    
    @Override
    public Page<PropertyVO> search(PropertyQueryRequest queryRequest) {
        int pageNum = queryRequest.getPageNum();
        int pageSize = queryRequest.getPageSize();

        // 构造分页对象
        Page<Property> propertyPage = new Page<>(pageNum, pageSize);

        // 构造查询条件
        LambdaQueryWrapper<Property> wrapper = new LambdaQueryWrapper<>();
        
        wrapper.eq(Property::getStatus, "available");
        // 房源标题模糊查询
        if (StringUtils.isNotBlank(queryRequest.getListingTitle())) {
            wrapper.like(Property::getListingTitle, queryRequest.getListingTitle());
        }
        
        // 邮政编码精确查询
        if (StringUtils.isNotBlank(queryRequest.getPostalCode())) {
            wrapper.eq(Property::getPostalCode, queryRequest.getPostalCode());
        }
        
        // 卧室数量范围查询
        if (queryRequest.getBedroomNumberMin() != null) {
            wrapper.ge(Property::getBedroomNumber, queryRequest.getBedroomNumberMin());
        }
        if (queryRequest.getBedroomNumberMax() != null) {
            wrapper.le(Property::getBedroomNumber, queryRequest.getBedroomNumberMax());
        }
        
        // 浴室数量范围查询
        if (queryRequest.getBathroomNumberMin() != null) {
            wrapper.ge(Property::getBathroomNumber, queryRequest.getBathroomNumberMin());
        }
        if (queryRequest.getBathroomNumberMax() != null) {
            wrapper.le(Property::getBathroomNumber, queryRequest.getBathroomNumberMax());
        }
        
        // 楼层范围查询（字符串比较）
        if (StringUtils.isNotBlank(queryRequest.getStoreyMin())) {
            wrapper.ge(Property::getStorey, queryRequest.getStoreyMin());
        }
        if (StringUtils.isNotBlank(queryRequest.getStoreyMax())) {
            wrapper.le(Property::getStorey, queryRequest.getStoreyMax());
        }
        
        // 建筑面积范围查询
        if (queryRequest.getFloorAreaSqmMin() != null) {
            wrapper.ge(Property::getFloorAreaSqm, queryRequest.getFloorAreaSqmMin());
        }
        if (queryRequest.getFloorAreaSqmMax() != null) {
            wrapper.le(Property::getFloorAreaSqm, queryRequest.getFloorAreaSqmMax());
        }
        
        // 顶层年份范围查询
        if (queryRequest.getTopYearMin() != null) {
            wrapper.ge(Property::getTopYear, queryRequest.getTopYearMin());
        }
        if (queryRequest.getTopYearMax() != null) {
            wrapper.le(Property::getTopYear, queryRequest.getTopYearMax());
        }
        
        // 转售价格范围查询
        if (queryRequest.getResalePriceMin() != null) {
            wrapper.ge(Property::getResalePrice, queryRequest.getResalePriceMin());
        }
        if (queryRequest.getResalePriceMax() != null) {
            wrapper.le(Property::getResalePrice, queryRequest.getResalePriceMax());
        }
        
        // 城镇查询
        if (StringUtils.isNotBlank(queryRequest.getTown())) {
            wrapper.eq(Property::getTown, queryRequest.getTown());
        }
        
        // 按创建时间倒序排列
        wrapper.orderByDesc(Property::getCreatedAt);

        // 执行分页查询
        Page<Property> result = propertyMapper.selectPage(propertyPage, wrapper);

        // 为每个房源查询并设置图片列表
        result.getRecords().forEach(property -> {
            List<PropertyImage> images = getPropertyImageEntities(property.getId());
            property.setImageList(images);
        });

        // 转换为PropertyVO
        Page<PropertyVO> propertyVOPage = new Page<>(pageNum, pageSize, result.getTotal());
        propertyVOPage.setRecords(result.getRecords().stream()
                .map(Property::toVO)
                .collect(Collectors.toList()));

        return propertyVOPage;
    }
    
    @Override
    public PropertyVO getById(Long id) {
        Property property = propertyMapper.selectById(id);
        if (property != null) {
            // 加载图片信息
            List<PropertyImage> images = getPropertyImageEntities(property.getId());
            property.setImageList(images);
            return property.toVO();
        }
        return null;
    }
    
    @Override
    public List<PropertyVO> getByIds(List<Long> ids) {
        if (ids == null || ids.isEmpty()) {
            return new ArrayList<>();
        }
        
        List<Property> properties = propertyMapper.selectBatchIds(ids);
        
        // 为每个房源加载图片信息
        properties.forEach(property -> {
            List<PropertyImage> images = getPropertyImageEntities(property.getId());
            property.setImageList(images);
        });
        
        return properties.stream().map(Property::toVO).collect(Collectors.toList());
    }
    
    @Override
    public List<PropertyVO> getUserProperties(Long sellerId) {
        QueryWrapper<Property> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("seller_id", sellerId);
        List<Property> properties = propertyMapper.selectList(queryWrapper);
        
        // 为每个房源加载图片信息
        properties.forEach(property -> {
            List<PropertyImage> images = getPropertyImageEntities(property.getId());
            property.setImageList(images);
        });
        
        return properties.stream().map(Property::toVO).collect(Collectors.toList());
    }
    
    
    @Override
    public PropertyVO create(PropertyAddForm form) {
        List<MultipartFile> imageFiles = form.getImageFiles();
        
        // 创建房源
        Property property = new Property();
        BeanUtils.copyProperties(form, property);
        property.setCreatedAt(LocalDateTime.now());
        property.setUpdatedAt(LocalDateTime.now());
        propertyMapper.insert(property);
        
        // 上传多张图片到S3并保存到数据库
        if (imageFiles != null && !imageFiles.isEmpty()) {
            for (MultipartFile imageFile : imageFiles) {
                if (imageFile != null && !imageFile.isEmpty()) {
                    // 构建图片名并上传到s3
                    String imageName = UUID.randomUUID() + "_" + imageFile.getOriginalFilename();
                    try {
                        s3Client.putObject(
                            PutObjectRequest.builder()
                                .bucket("hdb-pilot")
                                .key(imageName)
                                .contentType(imageFile.getContentType())
                                .build(),
                            RequestBody.fromInputStream(imageFile.getInputStream(), imageFile.getSize())
                        );
                    } catch (Exception e) {
                        throw new RuntimeException("upload image failed: " + e.getMessage());
                    }

                    // 构建图片url并插入数据库
                    String imageUrl = "https://hdb-pilot.s3.ap-southeast-1.amazonaws.com/" + URLEncoder.encode(imageName, StandardCharsets.UTF_8);
                    
                    // 创建PropertyImage对象
                    PropertyImage propertyImage = new PropertyImage();
                    propertyImage.setPropertyId(property.getId());
                    propertyImage.setImageUrl(imageUrl);
                    propertyImage.setCreatedAt(LocalDateTime.now());
                    propertyImage.setUpdatedAt(LocalDateTime.now());
                    propertyImageMapper.insert(propertyImage);
                }
            }
        }
        
        return property.toVO();
    }
    
    @Override
    public PropertyVO update(Long id, PropertyAddRequest request) {
        Property existingProperty = propertyMapper.selectById(id);
        if (existingProperty == null) {
            throw new RuntimeException("房源不存在");
        }
        
        BeanUtils.copyProperties(request, existingProperty);
        existingProperty.setUpdatedAt(LocalDateTime.now());
        
        propertyMapper.updateById(existingProperty);
        return existingProperty.toVO();
    }
    
    @Override
    public boolean delete(Long id) {
        Property property = propertyMapper.selectById(id);
        if (property == null) {
            return false;
        }
        
        return propertyMapper.deleteById(id) > 0;
    }
    
    @Override
    public List<PropertyImageVO> getPropertyImages(Long propertyId) {
        QueryWrapper<PropertyImage> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("property_id", propertyId);
        queryWrapper.orderByAsc("created_at");
        
        List<PropertyImage> images = propertyImageMapper.selectList(queryWrapper);
        
        return images.stream()
                .map(this::convertToVO)
                .collect(Collectors.toList());
    }
    
    private PropertyImageVO convertToVO(PropertyImage propertyImage) {
        PropertyImageVO vo = new PropertyImageVO();
        vo.setId(propertyImage.getId());
        vo.setPropertyId(propertyImage.getPropertyId());
        vo.setImageUrl(propertyImage.getImageUrl());
        vo.setCreatedAt(propertyImage.getCreatedAt());
        vo.setUpdatedAt(propertyImage.getUpdatedAt());
        return vo;
    }
    
    /**
     * 获取房源的图片实体列表（内部使用）
     */
    private List<PropertyImage> getPropertyImageEntities(Long propertyId) {
        QueryWrapper<PropertyImage> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("property_id", propertyId);
        queryWrapper.orderByAsc("created_at");
        
        return propertyImageMapper.selectList(queryWrapper);
    }
    
    @Override
    public PropertyImageVO addPropertyImage(Long propertyId, MultipartFile imageFile) {
        // 检查房源是否存在
        Property property = propertyMapper.selectById(propertyId);
        if (property == null) {
            throw new RuntimeException("房源不存在");
        }
        
        // 构建图片名并上传到s3
        String imageName = UUID.randomUUID() + "_" + imageFile.getOriginalFilename();
        try {
            System.out.println("开始上传图片到S3: " + imageName);
            s3Client.putObject(
                PutObjectRequest.builder()
                    .bucket("hdb-pilot")
                    .key(imageName)
                    .contentType(imageFile.getContentType())
                    .build(),
                RequestBody.fromInputStream(imageFile.getInputStream(), imageFile.getSize())
            );
            System.out.println("图片上传到S3成功: " + imageName);
        } catch (Exception e) {
            System.err.println("图片上传到S3失败: " + e.getMessage());
            e.printStackTrace();
            throw new RuntimeException("upload image failed: " + e.getMessage());
        }

        // 构建图片url并插入数据库
        String imageUrl = "https://hdb-pilot.s3.ap-southeast-1.amazonaws.com/" + URLEncoder.encode(imageName, StandardCharsets.UTF_8);
        
        // 创建PropertyImage对象
        PropertyImage propertyImage = new PropertyImage();
        propertyImage.setPropertyId(propertyId);
        propertyImage.setImageUrl(imageUrl);
        propertyImage.setCreatedAt(LocalDateTime.now());
        propertyImage.setUpdatedAt(LocalDateTime.now());
        
        try {
            propertyImageMapper.insert(propertyImage);
            System.out.println("图片信息保存到数据库成功: " + imageUrl);
        } catch (Exception e) {
            System.err.println("图片信息保存到数据库失败: " + e.getMessage());
            e.printStackTrace();
            throw new RuntimeException("save image info failed: " + e.getMessage());
        }
        
        return convertToVO(propertyImage);
    }
    
    @Override
    public boolean deletePropertyImage(Long imageId) {
        PropertyImage propertyImage = propertyImageMapper.selectById(imageId);
        if (propertyImage == null) {
            return false;
        }
        
        // 从S3删除图片（可选，取决于业务需求）
        // 这里暂时只从数据库删除，S3中的文件可以保留
        
        return propertyImageMapper.deleteById(imageId) > 0;
    }

    @Override
    public Page<PropertyVO> listPendingPropertiesByPage(long current, long size, PropertyFilterRequest request) {
        Page<Property> propertyPage = new Page<>(current, size);

        LambdaQueryWrapper<Property> wrapper = new LambdaQueryWrapper<>();

        // 添加待审核状态过滤
        wrapper.eq(Property::getStatus, "PENDING");

        // 过滤条件
        if (StringUtils.isNotBlank(request.getSellerId())) {
            wrapper.eq(Property::getSellerId, request.getSellerId());
        }

        if (StringUtils.isNotBlank(request.getAddress())) {
            wrapper.like(Property::getStreetName, request.getAddress())
                    .or().like(Property::getBlock, request.getAddress())
                    .or().like(Property::getPostalCode, request.getAddress());
        }

        if (StringUtils.isNotBlank(request.getTown())) {
            wrapper.eq(Property::getTown, request.getTown());
        }

        if (request.getBedroomNumber() != null) {
            wrapper.eq(Property::getBedroomNumber, request.getBedroomNumber());
        }

        if (request.getBathroomNumber() != null) {
            wrapper.eq(Property::getBathroomNumber, request.getBathroomNumber());
        }

        wrapper.orderByDesc(Property::getUpdatedAt);

        Page<Property> result = propertyMapper.selectPage(propertyPage, wrapper);

        // 加载图片列表
        result.getRecords().forEach(property -> {
            List<PropertyImage> images = getPropertyImageEntities(property.getId());
            property.setImageList(images);
        });

        // 转换为 VO
        Page<PropertyVO> voPage = new Page<>(current, size, result.getTotal());
        voPage.setRecords(result.getRecords().stream()
                .map(Property::toVO)
                .collect(Collectors.toList()));

        return voPage;
    }



    @Override
    public Boolean reviewProperty(Long id, Boolean approved) {
        Property property = new Property();
        property.setStatus(approved ? "available" : "rejected");

        UpdateWrapper<Property> updateWrapper = new UpdateWrapper<>();
        updateWrapper.eq("id", id);

        return this.update(property, updateWrapper);
    }

    @Override
    public int countAll() {
        return propertyMapper.selectCount(null).intValue();
    }

    @Override
    public int countByStatus(String status) {
        QueryWrapper<Property> wrapper = new QueryWrapper<>();
        wrapper.eq("status", status);
        return propertyMapper.selectCount(wrapper).intValue();
    }

    @Override
    public Double calculateListingGrowth() {
        long thisMonth = propertyMapper.countThisMonth();
        long lastMonth = propertyMapper.countLastMonth();
        System.out.println("thisMonth: " + thisMonth);
        System.out.println("lastMonth: " + lastMonth);

        if (lastMonth == 0) {
            if (thisMonth == 0) {
                return 0.0;
            } else {
                return 100.0;
            }
        }
        return ((double)(thisMonth - lastMonth) / lastMonth) * 100;
    }

    public List<MonthlyListingCount> getMonthlyListingCounts(Integer year) {
        return propertyMapper.getMonthlyListingCounts(year);
    }

    @Override
    public List<ListingStatusCount> getStatusDistribution() {
        return propertyMapper.getStatusDistribution();
    }

}
