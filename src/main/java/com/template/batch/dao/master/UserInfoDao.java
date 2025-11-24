package com.template.batch.dao.master;

import com.template.batch.entity.master.UserInfo;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface UserInfoDao {

  UserInfo findOneByUserId(@Param("userId") String userId);

  List<UserInfo> findByCreateDateBetween(@Param("startDate") String startDate, @Param("endDate") String endDate, @Param("skipRows") int skipRows, @Param("pageSize") int pageSize);

  void add(UserInfo userInfo);

  void addAll(List<UserInfo> userInfos);

  void updateFlag(@Param("userId") String userId);

  int getAllCount();

  void deleteAll();
}
