package com.template.batch.dao.slave;

import com.template.batch.entity.slave.RestUserInfo;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface RestUserInfoDao {

  RestUserInfo findOneByUserId(@Param("userId") String userId);

  int add(RestUserInfo restUserInfo);

  int addAll(List<RestUserInfo> restUserInfos);

  int getAllCount();

  int deleteAll();
}
