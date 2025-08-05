package com.iss.hdbPilot.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.iss.hdbPilot.model.dto.FavoriteRequest;
import com.iss.hdbPilot.model.dto.PageRequest;
import com.iss.hdbPilot.model.vo.FavoriteVO;

import java.util.List;

public interface FavoriteService {
    
    /**
     * 添加收藏
     */
    FavoriteVO addFavorite(Long userId, FavoriteRequest request);
    
    /**
     * 取消收藏（通过propertyId）
     */
    boolean removeFavorite(Long userId, Long propertyId);
    
    /**
     * 取消收藏（通过favoriteId）
     */
    boolean removeFavoriteById(Long userId, Long favoriteId);
    
    /**
     * 获取用户的收藏列表
     */
    Page<FavoriteVO> getUserFavorites(Long userId, PageRequest pageRequest);
    
    /**
     * 检查用户是否已收藏某个房源
     */
    boolean isFavorite(Long userId, Long propertyId);
    
    /**
     * 获取用户收藏的房源ID列表
     */
    List<Long> getUserFavoritePropertyIds(Long userId);
} 