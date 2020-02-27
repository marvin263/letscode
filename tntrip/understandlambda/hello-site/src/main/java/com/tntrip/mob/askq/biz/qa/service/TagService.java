/**
 * Copyright (C) 2006-2015 tntrip All rights reserved
 */
package com.tntrip.mob.askq.biz.qa.service;

import com.tntrip.mob.askq.biz.qa.dao.TagDao;
import com.tntrip.mob.askq.biz.qa.vo.TagVO;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import java.util.Collections;
import java.util.List;

@Component
public class TagService {
    //@Autowired
    private TagDao tagDao;

    public int insert(TagVO tagVO) {
        int affected = tagDao.insert(tagVO);
        return affected;
    }

    public int update(int tagId, String name, Integer tagType, String tagValue, Integer customerId, String customerName, int enabled) {
        int affected = tagDao.update(tagId, name, tagType, tagValue, customerId, customerName, enabled);
        return affected;
    }

    public int delete(int tagId) {
        int affected = tagDao.delete(tagId);
        return affected;
    }

    public List<TagVO> getTagByExactName(String name) {
        List<TagVO> list = tagDao.getTagByExactName(name);
        return CollectionUtils.isEmpty(list) ? Collections.emptyList() : list;
    }

    public List<TagVO> getTagByExactTagValue(String tagValue) {
        List<TagVO> list = tagDao.getTagByExactTagValue(tagValue);
        return CollectionUtils.isEmpty(list) ? Collections.emptyList() : list;
    }

    public List<TagVO> getTagsByNames(List<String> listNames) {
        if (CollectionUtils.isEmpty(listNames)) {
            return Collections.emptyList();
        }
        List<TagVO> list = tagDao.getTagsByNames(listNames);
        return CollectionUtils.isEmpty(list) ? Collections.emptyList() : list;
    }

    public List<TagVO> getTagsByExactNames(List<String> listNames) {
        if (CollectionUtils.isEmpty(listNames)) {
            return Collections.emptyList();
        }
        List<TagVO> list = tagDao.getTagsByExactNames(listNames);
        return CollectionUtils.isEmpty(list) ? Collections.emptyList() : list;
    }

    public int getTagsCount(@Param("name") String name, @Param("tagType") int tagType, @Param("enabled") int enabled) {
        return tagDao.getTagsCount(name, tagType, enabled);
    }

    public List<TagVO> getEnabledTagsByIds(List<Integer> ids) {
        List<TagVO> list = tagDao.getEnabledTagsByIds(ids);
        return CollectionUtils.isEmpty(list) ? Collections.emptyList() : list;
    }

    public List<TagVO> getAllHotTags() {
        return tagDao.getAllHotTags();
    }

    public List<TagVO> getAllGeneralEnabledTags() {
        List<TagVO> list = tagDao.getAllGeneralEnabledTags();
        return CollectionUtils.isEmpty(list) ? Collections.emptyList() : list;
    }

    public TagVO getCityNameByPoiValue(String poiValue) {
        return tagDao.getCityNameByPoiValue(poiValue);
    }
}
