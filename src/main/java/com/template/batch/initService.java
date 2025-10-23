package com.template.batch;

import com.template.batch.dao.RestUserInfoDao;
import com.template.batch.dao.UserInfoDao;
import com.template.batch.entity.UserInfo;
import com.template.batch.util.IdGenerator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StopWatch;

import javax.annotation.PostConstruct;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class initService {
  private final UserInfoDao userInfoDao;
  private final RestUserInfoDao restUserInfoDao;

  private static int TEST_DATA_COUNT = 1000000;


  @PostConstruct
  @Transactional
  public void init(){
    StopWatch stopWatch = new StopWatch();
    stopWatch.start();

    int userInfoCount = userInfoDao.getAllCount();
    log.info(">>>>> test data count=[{}]", userInfoCount);
    if(userInfoCount > 0) {
      log.warn(">>>>> test data count=[{}]", userInfoCount);
      return;
    }

    List<UserInfo> testUserInfos = new ArrayList<>();

    for(int i=0; i < TEST_DATA_COUNT; i++) {
      LocalDateTime createDate = LocalDateTime.now();
      String userId = IdGenerator.generateUserId();
//      try {
//        Thread.sleep(1);
//      } catch (InterruptedException e) {
//        throw new RuntimeException(e);
//      }

      testUserInfos.add(UserInfo.builder().userId(userId).restFlag("N").createDate(createDate).build());
      //userInfoDao.add(UserInfo.builder().userId(userId).restFlag("N").createDate(createDate).build());
    }

    userInfoDao.addAll(testUserInfos);
    stopWatch.stop();
    log.info(">>>> init test data complete. count=[{}], elapsedTime=[{}]ms", testUserInfos.size(), stopWatch.getTotalTimeMillis());
  }
}
