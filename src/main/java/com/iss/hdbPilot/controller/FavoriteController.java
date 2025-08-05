package com.iss.hdbPilot.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.iss.hdbPilot.common.BaseResponse;
import com.iss.hdbPilot.common.ResultUtils;
import com.iss.hdbPilot.model.dto.FavoriteRequest;
import com.iss.hdbPilot.model.dto.PageRequest;
import com.iss.hdbPilot.model.vo.FavoriteVO;
import com.iss.hdbPilot.service.FavoriteService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/api/favorite")
public class FavoriteController {
    
    @Autowired
    private FavoriteService favoriteService;
    
    /**
     * 添加收藏
     */
    @PostMapping
    public BaseResponse<FavoriteVO> addFavorite(@RequestParam Long userId, @RequestBody @Valid FavoriteRequest request) {
        return ResultUtils.success(favoriteService.addFavorite(userId, request));
    }
    
    /**
     * 取消收藏
     */
    @DeleteMapping("/{favoriteId}")
    public BaseResponse<Boolean> removeFavorite(@RequestParam Long userId, @PathVariable Long favoriteId) {
        boolean success = favoriteService.removeFavoriteById(userId, favoriteId);
        if (success) {
            return ResultUtils.success(true);
        } else {
            return new BaseResponse<>(-1, false, "收藏不存在或删除失败");
        }
    }
    
    /**
     * 获取用户的收藏列表
     */
    @GetMapping("/user/{userId}")
    public BaseResponse<Page<FavoriteVO>> getUserFavorites(@PathVariable Long userId, PageRequest pageRequest) {
        return ResultUtils.success(favoriteService.getUserFavorites(userId, pageRequest));
    }
    
    /**
     * 检查用户是否已收藏某个房源
     */
    @GetMapping("/check")
    public BaseResponse<Boolean> isFavorite(@RequestParam Long userId, @RequestParam Long propertyId) {
        return ResultUtils.success(favoriteService.isFavorite(userId, propertyId));
    }
    
    /**
     * 获取用户收藏的房源ID列表
     */
    @GetMapping("/user/{userId}/property-ids")
    public BaseResponse<List<Long>> getUserFavoritePropertyIds(@PathVariable Long userId) {
        return ResultUtils.success(favoriteService.getUserFavoritePropertyIds(userId));
    }
} 