package com.template.batch.job;


import com.template.batch.dao.master.UserInfoDao;
import com.template.batch.dao.slave.RestUserInfoDao;
import com.template.batch.entity.master.UserInfo;
import com.template.batch.util.IdGenerator;
import lombok.extern.slf4j.Slf4j;
import org.junit.After;
import org.junit.Before;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Slf4j
public abstract class AbstractSetUserTestData {
  protected static int TEST_DATA_COUNT = 100;

  @Autowired
  protected UserInfoDao userInfoDao;
  @Autowired
  protected RestUserInfoDao restUserInfoDao;


  @Before
  public void setup(){
    int beforeCount = userInfoDao.getAllCount();
    log.info("------ before set data. count=[{}]", beforeCount);

    List<UserInfo> testUserInfos = new ArrayList<>();

    for(int i=0; i < TEST_DATA_COUNT; i++) {
      LocalDateTime createDate = LocalDateTime.now();
      String userId = IdGenerator.generateUserId();

      testUserInfos.add(UserInfo.builder().userId(userId).restFlag("N").createDate(createDate).build());
    }

    userInfoDao.addAll(testUserInfos);
//    restUserInfoDao.add(RestUserInfo.builder().userId(testUserInfos.get(0).getUserId()).createDate(testUserInfos.get(0).getCreateDate()).build());
    int userCount = userInfoDao.getAllCount();
    int restUserCount = restUserInfoDao.getAllCount();
    log.info("------ before set data. userCount=[{}], restUserCount=[{}]", userCount, restUserCount);
  }


  @After
  public void clear(){
    userInfoDao.deleteAll();
    restUserInfoDao.deleteAll();
    log.info("---- clear test data.");
  }
}
