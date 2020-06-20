package com.mtcarpenter.mall.portal.service.impl;

import com.mtcarpenter.mall.common.UmsIntegrationConsumeSettingOutput;
import com.mtcarpenter.mall.mapper.UmsIntegrationConsumeSettingMapper;
import com.mtcarpenter.mall.model.UmsIntegrationConsumeSetting;
import com.mtcarpenter.mall.model.UmsMember;
import com.mtcarpenter.mall.portal.domain.MemberProductCollection;
import com.mtcarpenter.mall.portal.repository.MemberProductCollectionRepository;
import com.mtcarpenter.mall.portal.service.MemberCollectionService;
import com.mtcarpenter.mall.portal.service.UmsMemberService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

/**
 * 会员收藏Service实现类
 * Created by macro on 2018/8/2.
 */
@Service
public class MemberCollectionServiceImpl implements MemberCollectionService {
    @Autowired
    private MemberProductCollectionRepository productCollectionRepository;
    @Autowired
    private UmsMemberService memberService;

    @Autowired
    private UmsIntegrationConsumeSettingMapper integrationConsumeSettingMapper;

    @Override
    public int add(MemberProductCollection productCollection) {
        int count = 0;
        UmsMember member = memberService.getCurrentMember();
        productCollection.setMemberId(member.getId());
        productCollection.setMemberNickname(member.getNickname());
        productCollection.setMemberIcon(member.getIcon());
        MemberProductCollection findCollection = productCollectionRepository.findByMemberIdAndProductId(productCollection.getMemberId(), productCollection.getProductId());
        if (findCollection == null) {
            productCollectionRepository.save(productCollection);
            count = 1;
        }
        return count;
    }

    @Override
    public int delete(Long productId) {
        UmsMember member = memberService.getCurrentMember();
        return productCollectionRepository.deleteByMemberIdAndProductId(member.getId(), productId);
    }

    @Override
    public Page<MemberProductCollection> list(Integer pageNum, Integer pageSize) {
        UmsMember member = memberService.getCurrentMember();
        Pageable pageable = PageRequest.of(pageNum - 1, pageSize);
        return productCollectionRepository.findByMemberId(member.getId(), pageable);
    }

    /**
     * 获取积分规则
     *
     * @param id
     * @return
     */
    @Override
    public UmsIntegrationConsumeSettingOutput integrationConsumeSetting(Long id) {
        UmsIntegrationConsumeSetting umsIntegrationConsumeSetting = integrationConsumeSettingMapper.selectByPrimaryKey(id);
        UmsIntegrationConsumeSettingOutput consumeSettingOutput = new UmsIntegrationConsumeSettingOutput();
        BeanUtils.copyProperties(umsIntegrationConsumeSetting, consumeSettingOutput);
        return consumeSettingOutput;
    }
}
