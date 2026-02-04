package com.template.batch.dao.master;

import com.template.batch.entity.master.BatchHistory;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface BatchHistoryDao {

  BatchHistory findOne(@Param("jobId") String jobId);

  int addBatchHistory(BatchHistory batchHistory);

  int upsertBatchHistory(BatchHistory batchHistory);

  int deleteBatchHistory(@Param("jobId") String jobId);
}
