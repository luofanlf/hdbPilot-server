package com.iss.hdbPilot.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.iss.hdbPilot.mapper.FavoriteMapper;
import com.iss.hdbPilot.mapper.PropertyMapper;
import com.iss.hdbPilot.model.entity.Favorite;
import com.iss.hdbPilot.model.entity.Property;
import com.iss.hdbPilot.model.dto.FavoriteRequest;
import com.iss.hdbPilot.model.dto.PageRequest;
import com.iss.hdbPilot.model.vo.FavoriteVO;
import com.iss.hdbPilot.model.vo.PropertyVO;
import com.iss.hdbPilot.service.FavoriteService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class FavoriteServiceImpl implements FavoriteService {
    
    @Autowired
    private FavoriteMapper favoriteMapper;
    
    @Autowired
    private PropertyMapper propertyMapper;
    
    @Override
    public FavoriteVO addFavorite(Long userId, FavoriteRequest request) {
        // 检查房源是否存在
        Property property = propertyMapper.selectById(request.getPropertyId());
        if (property == null) {
            throw new RuntimeException("房源不存在");
        }
        
        // 检查是否已经收藏
        LambdaQueryWrapper<Favorite> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Favorite::getUserId, userId)
                   .eq(Favorite::getPropertyId, request.getPropertyId());
        
        Favorite existingFavorite = favoriteMapper.selectOne(queryWrapper);
        if (existingFavorite != null) {
            throw new RuntimeException("已经收藏过该房源");
        }
        
        // 创建新的收藏记录
        Favorite favorite = new Favorite();
        favorite.setUserId(userId);
        favorite.setPropertyId(request.getPropertyId());
        favorite.setCreatedAt(LocalDateTime.now());
        favorite.setUpdatedAt(LocalDateTime.now());
        
        favoriteMapper.insert(favorite);
        
        return convertToVO(favorite, property);
    }
    
    @Override
    public boolean removeFavorite(Long userId, Long propertyId) {
        LambdaQueryWrapper<Favorite> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Favorite::getUserId, userId)
                   .eq(Favorite::getPropertyId, propertyId);
        
        return favoriteMapper.delete(queryWrapper) > 0;
    }
    
    @Override
    public boolean removeFavoriteById(Long userId, Long favoriteId) {
        LambdaQueryWrapper<Favorite> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Favorite::getUserId, userId)
                   .eq(Favorite::getId, favoriteId);
        
        return favoriteMapper.delete(queryWrapper) > 0;
    }
    
    @Override
    public Page<FavoriteVO> getUserFavorites(Long userId, PageRequest pageRequest) {
        int pageNum = pageRequest.getPageNum();
        int pageSize = pageRequest.getPageSize();
        
        // 构造分页对象
        Page<Favorite> favoritePage = new Page<>(pageNum, pageSize);
        
        // 构造查询条件
        LambdaQueryWrapper<Favorite> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Favorite::getUserId, userId)
                   .orderByDesc(Favorite::getCreatedAt);
        
        // 执行分页查询
        Page<Favorite> result = favoriteMapper.selectPage(favoritePage, queryWrapper);
        
        // 转换为FavoriteVO
        Page<FavoriteVO> favoriteVOPage = new Page<>(pageNum, pageSize, result.getTotal());
        favoriteVOPage.setRecords(result.getRecords().stream()
                .map(this::convertToVOWithProperty)
                .collect(Collectors.toList()));
        
        return favoriteVOPage;
    }
    
    @Override
    public boolean isFavorite(Long userId, Long propertyId) {
        LambdaQueryWrapper<Favorite> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Favorite::getUserId, userId)
                   .eq(Favorite::getPropertyId, propertyId);
        
        return favoriteMapper.selectCount(queryWrapper) > 0;
    }
    
    @Override
    public List<Long> getUserFavoritePropertyIds(Long userId) {
        LambdaQueryWrapper<Favorite> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Favorite::getUserId, userId)
                   .select(Favorite::getPropertyId);
        
        List<Favorite> favorites = favoriteMapper.selectList(queryWrapper);
        return favorites.stream()
                .map(Favorite::getPropertyId)
                .collect(Collectors.toList());
    }
    
    private FavoriteVO convertToVO(Favorite favorite, Property property) {
        FavoriteVO vo = new FavoriteVO();
        vo.setId(favorite.getId());
        vo.setUserId(favorite.getUserId());
        vo.setPropertyId(favorite.getPropertyId());
        vo.setCreatedAt(favorite.getCreatedAt());
        vo.setUpdatedAt(favorite.getUpdatedAt());
        
        if (property != null) {
            vo.setProperty(property.toVO());
        }
        
        return vo;
    }
    
    private FavoriteVO convertToVOWithProperty(Favorite favorite) {
        Property property = propertyMapper.selectById(favorite.getPropertyId());
        return convertToVO(favorite, property);
    }
} 