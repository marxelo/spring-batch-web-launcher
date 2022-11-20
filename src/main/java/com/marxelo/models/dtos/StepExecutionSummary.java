package com.marxelo.models.dtos;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
/*
 * Copyright 2006-2022 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except
 * in compliance with the License. You may obtain a copy of the License at
 *
 * https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License
 * is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express
 * or implied. See the License for the specific language governing permissions and limitations under
 * the License.
 */

import org.springframework.batch.core.BatchStatus;
import org.springframework.batch.core.ExitStatus;
import org.springframework.batch.core.StepExecution;

@AllArgsConstructor
@Getter
@Setter
@ToString
@NoArgsConstructor
public class StepExecutionSummary {

  private String stepName;
  private BatchStatus status = BatchStatus.STARTING;
  private int readCount = 0;
  private int writeCount = 0;
  private int commitCount = 0;
  private int rollbackCount = 0;
  private int readSkipCount = 0;
  private int processSkipCount = 0;
  private int writeSkipCount = 0;
  private int filterCount = 0;
  private ExitStatus exitStatus = ExitStatus.EXECUTING;

  public StepExecutionSummary(StepExecution se) {
    this.setStepName(se.getStepName());
    this.setStatus(se.getStatus());
    this.setReadCount(se.getReadCount());
    this.setWriteCount(se.getWriteCount());
    this.setCommitCount(se.getCommitCount());
    this.setRollbackCount(se.getRollbackCount());
    this.setReadSkipCount(se.getReadSkipCount());
    this.setProcessSkipCount(se.getProcessSkipCount());
    this.setWriteSkipCount(se.getWriteSkipCount());
    this.setExitStatus(se.getExitStatus());
    this.setFilterCount(se.getFilterCount());
  }
}
