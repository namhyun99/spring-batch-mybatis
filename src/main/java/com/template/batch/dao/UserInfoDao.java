package com.template.batch.dao;

import com.template.batch.entity.UserInfo;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface UserInfoDao {

  UserInfo findOneByUserId(@Param("userId") String userId);

  List<UserInfo> findByCreateDateBetween(@Param("startDate") String startDate, @Param("endDate") String endDate);

  void add(UserInfo userInfo);

  void addAll(List<UserInfo> userInfos);

  void updateFlag(@Param("userId") String userId);

  int getAllCount();
}
