package com.template.batch.dao.slave;

import com.template.batch.entity.slave.RestUserInfo;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface RestUserInfoDao {

  RestUserInfo findOneByUserId(@Param("userId") String userId);

  void add(RestUserInfo restUserInfo);

  void addAll(List<RestUserInfo> restUserInfos);

  int getAllCount();

  void deleteAll();
}
