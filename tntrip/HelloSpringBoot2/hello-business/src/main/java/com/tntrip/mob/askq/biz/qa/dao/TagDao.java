package com.tntrip.mob.askq.biz.qa.dao;


import com.tntrip.mob.askq.biz.qa.vo.TagVO;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface TagDao {

    int insert(@Param("tagVO") TagVO tagVO);

    int update(@Param("tagId") int tagId,
               @Param("name") String name,
               @Param("tagType") Integer tagType,
               @Param("tagValue") String tagValue,
               @Param("customerId") Integer customerId,
               @Param("customerName") String customerName,
               @Param("enabled") int enabled);

    int delete(@Param("tagId") int tagId);


    int getTagsCount(@Param("name") String name, @Param("tagType") int tagType, @Param("enabled") int enabled);

    List<TagVO> getTagByExactName(@Param("name") String name);
    List<TagVO> getTagByExactTagValue(@Param("tagValue") String tagValue);

    List<TagVO> getTagsByNames(@Param("listNames") List<String> listNames);
    List<TagVO> getTagsByExactNames(@Param("listNames") List<String> listNames);

    List<TagVO> getEnabledTagsByIds(@Param("idList") List<Integer> idList);

    List<TagVO> getAllHotTags();

    List<TagVO> getAllGeneralEnabledTags();
    TagVO getCityNameByPoiValue(@Param("poiValue") String poiValue);

}
