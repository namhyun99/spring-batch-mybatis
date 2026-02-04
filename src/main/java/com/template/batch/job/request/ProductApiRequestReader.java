package com.template.batch.job.request;

import jdk.nashorn.internal.runtime.arrays.IteratorAction;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.ExitStatus;
import org.springframework.batch.core.StepExecution;
import org.springframework.batch.core.StepExecutionListener;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.item.ItemReader;
import org.springframework.batch.item.NonTransientResourceException;
import org.springframework.batch.item.ParseException;
import org.springframework.batch.item.UnexpectedInputException;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

@Slf4j
@StepScope
@Component
public class ProductApiRequestReader implements ItemReader<ApiRequest>, StepExecutionListener {

  private Iterator<ProductPolicy> productPolicyIterator;
  private Iterator<ApiRequest> currentApiRequestIterator;


  @Override
  public void beforeStep(StepExecution stepExecution) {
    List<ProductPolicy> targets =
        (List<ProductPolicy>) stepExecution
            .getJobExecution()
            .getExecutionContext()
            .get("targetProducts");

    if(targets == null || targets.isEmpty()) {
      log.info("targets is null or empty");
      return;
    }
    this.productPolicyIterator = targets.iterator();
  }

  @Override
  public ApiRequest read() throws Exception, UnexpectedInputException, ParseException, NonTransientResourceException {
    // 1. 현재 product에서 아직 반환할 요청 단위가 남아 있으면
    if( currentApiRequestIterator != null && currentApiRequestIterator.hasNext()) {
      log.info("1. currentApiRequestIterator = {}", currentApiRequestIterator.next());
      return currentApiRequestIterator.next();
    }


    if(productPolicyIterator == null || !productPolicyIterator.hasNext()) {
      return null;
    }

    while (productPolicyIterator.hasNext()) {
      ProductPolicy productPolicy = productPolicyIterator.next();

      List<ApiRequest> apiRequest =
          Arrays.asList(new ApiRequest(productPolicy.getCode(), productPolicy.getMaxQty()));

      this.currentApiRequestIterator = apiRequest.iterator();

      if (!currentApiRequestIterator.hasNext()) {
        log.info("currentApiRequestIterator is not next");
        continue;
      }
      // 첫 요청 단위 반환
      ApiRequest next = currentApiRequestIterator.next();
      log.info("currentApiRequestIterator is next. {}", next);
      return next;
    }

    log.info("3. productPolicyIterator = null is stop step");
    return null;
  }



  @Override
  public ExitStatus afterStep(StepExecution stepExecution) {
    return stepExecution.getExitStatus();
  }
}
