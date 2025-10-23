package com.template.batch.dao;

import com.template.batch.entity.RestUserInfo;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface RestUserInfoDao {

  RestUserInfo findOneByUserId(@Param("userId") String userId);

  void add(RestUserInfo restUserInfo);

  void addAll(List<RestUserInfo> restUserInfos);

  int getAllCount();
}
