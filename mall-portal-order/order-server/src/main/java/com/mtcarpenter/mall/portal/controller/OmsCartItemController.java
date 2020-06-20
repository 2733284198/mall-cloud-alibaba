package com.mtcarpenter.mall.portal.controller;

import com.mtcarpenter.mall.common.CartProductOutput;
import com.mtcarpenter.mall.common.CartPromotionItemOutput;
import com.mtcarpenter.mall.common.UmsMemberOutput;
import com.mtcarpenter.mall.common.api.CommonResult;
import com.mtcarpenter.mall.model.OmsCartItem;
import com.mtcarpenter.mall.portal.domain.CartPromotionItem;
import com.mtcarpenter.mall.portal.service.OmsCartItemService;
import com.mtcarpenter.mall.portal.util.MemberUtil;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 购物车管理Controller
 * Created by macro on 2018/8/2.
 */
@Controller
@Api(tags = "OmsCartItemController", description = "购物车管理")
@RequestMapping("/cart")
public class OmsCartItemController {
    @Autowired
    private OmsCartItemService cartItemService;

    @Autowired
    private HttpServletRequest request;

    @Autowired
    private MemberUtil memberUtil;

    @ApiOperation("添加商品到购物车")
    @RequestMapping(value = "/add", method = RequestMethod.POST)
    @ResponseBody
    public CommonResult add(@RequestBody OmsCartItem cartItem) {
        int count = cartItemService.add(cartItem);
        if (count > 0) {
            return CommonResult.success(count);
        }
        return CommonResult.failed();
    }

    @ApiOperation("获取某个会员的购物车列表")
    @RequestMapping(value = "/list", method = RequestMethod.GET)
    @ResponseBody
    public CommonResult<List<OmsCartItem>> list() {
        UmsMemberOutput umsMember = memberUtil.getRedisUmsMember(request);
        List<OmsCartItem> cartItemList = cartItemService.list(umsMember.getId());
        return CommonResult.success(cartItemList);
    }

    @ApiOperation("获取某个会员的购物车列表,包括促销信息")
    @RequestMapping(value = "/list/promotion", method = RequestMethod.GET)
    @ResponseBody
    public CommonResult<List<CartPromotionItemOutput>> listPromotion(@RequestParam(required = false) List<Long> cartIds) {
        UmsMemberOutput umsMember = memberUtil.getRedisUmsMember(request);
        List<CartPromotionItem> cartPromotionItemList = cartItemService.listPromotion(umsMember.getId(), cartIds);
        List<CartPromotionItemOutput> cartPromotionItemOutputs = cartPromotionItemList.stream().map(c -> {
            CartPromotionItemOutput cartPromotionItemOutput = new CartPromotionItemOutput();
            BeanUtils.copyProperties(c, cartPromotionItemOutput);
            return cartPromotionItemOutput;
        }).collect(Collectors.toList());
        return CommonResult.success(cartPromotionItemOutputs);
    }

    @ApiOperation("修改购物车中某个商品的数量")
    @RequestMapping(value = "/update/quantity", method = RequestMethod.GET)
    @ResponseBody
    public CommonResult updateQuantity(@RequestParam Long id,
                                       @RequestParam Integer quantity) {
        UmsMemberOutput umsMember = memberUtil.getRedisUmsMember(request);
        int count = cartItemService.updateQuantity(id, umsMember.getId(), quantity);
        if (count > 0) {
            return CommonResult.success(count);
        }
        return CommonResult.failed();
    }

    @ApiOperation("获取购物车中某个商品的规格,用于重选规格")
    @RequestMapping(value = "/getProduct/{productId}", method = RequestMethod.GET)
    @ResponseBody
    public CommonResult<CartProductOutput> getCartProduct(@PathVariable Long productId) {
        CartProductOutput cartProduct = cartItemService.getCartProduct(productId);
        return CommonResult.success(cartProduct);
    }

    @ApiOperation("修改购物车中商品的规格")
    @RequestMapping(value = "/update/attr", method = RequestMethod.POST)
    @ResponseBody
    public CommonResult updateAttr(@RequestBody OmsCartItem cartItem) {
        int count = cartItemService.updateAttr(cartItem);
        if (count > 0) {
            return CommonResult.success(count);
        }
        return CommonResult.failed();
    }

    @ApiOperation("删除购物车中的某个商品")
    @RequestMapping(value = "/delete", method = RequestMethod.POST)
    @ResponseBody
    public CommonResult delete(@RequestParam("ids") List<Long> ids) {
        UmsMemberOutput umsMember = memberUtil.getRedisUmsMember(request);
        int count = cartItemService.delete(umsMember.getId(), ids);
        if (count > 0) {
            return CommonResult.success(count);
        }
        return CommonResult.failed();
    }

    @ApiOperation("清空购物车")
    @RequestMapping(value = "/clear", method = RequestMethod.POST)
    @ResponseBody
    public CommonResult clear() {
        UmsMemberOutput umsMember = memberUtil.getRedisUmsMember(request);
        int count = cartItemService.clear(umsMember.getId());
        if (count > 0) {
            return CommonResult.success(count);
        }
        return CommonResult.failed();
    }
}
