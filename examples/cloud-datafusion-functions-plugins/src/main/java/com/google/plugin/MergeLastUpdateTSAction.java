/*
 * Copyright 2021 Google LLC
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.google.plugin;

import com.google.functions.MergeLastUpdateTSFunction;
import io.cdap.cdap.api.TxRunnable;
import io.cdap.cdap.api.annotation.Description;
import io.cdap.cdap.api.annotation.Macro;
import io.cdap.cdap.api.annotation.Name;
import io.cdap.cdap.api.annotation.Plugin;
import io.cdap.cdap.api.data.DatasetContext;
import io.cdap.cdap.api.plugin.PluginConfig;
import io.cdap.cdap.etl.api.PipelineConfigurer;
import io.cdap.cdap.etl.api.action.Action;
import io.cdap.cdap.etl.api.action.ActionContext;
import javax.annotation.Nullable;

/** An Action Plugin to merge based on last update timestamp field. */
@Plugin(type = Action.PLUGIN_TYPE)
@Name(MergeLastUpdateTSAction.NAME)
@Description("MergeLastUpdateTSAction")
public class MergeLastUpdateTSAction extends Action {
  public static final String NAME = "MergeLastUpdateTSAction";
  private final Conf config;

  public MergeLastUpdateTSAction(Conf config) {
    this.config = config;
  }

  @Override
  public void configurePipeline(PipelineConfigurer pipelineConfigurer) {}

  @Override
  public void run(ActionContext context) throws Exception {
    context.execute(
        new TxRunnable() {
          @Override
          public void run(DatasetContext context) throws Exception {
            new MergeLastUpdateTSFunction(
                    config.keyPath,
                    config.projectId,
                    config.dataset,
                    config.tableName,
                    config.primaryKeyList,
                    config.updateColumnsList,
                    config.partitionColumn)
                .executeMerge();
          }
        });
  }

  public static class Conf extends PluginConfig {
    @Name("keyPath")
    @Description("Path to credential key")
    @Macro
    private final String keyPath;

    @Name("projectId")
    @Description("Project ID")
    @Macro
    private final String projectId;

    @Name("dataset")
    @Description("Dataset name")
    @Macro
    private final String dataset;

    @Name("tableName")
    @Description("Table name")
    @Macro
    private final String tableName;

    @Name("primaryKeyList")
    @Description("Comma separated list of PK's")
    @Macro
    private final String primaryKeyList;

    @Name("updateColumnsList")
    @Description("Comma separated list of update columns")
    @Macro
    private final String updateColumnsList;

    @Name("partitionColumn")
    @Description("Partition coulmn name in destination table")
    @Macro
    @Nullable
    private final String partitionColumn;

    public Conf(
        String keyPath,
        String projectId,
        String dataset,
        String tableName,
        String primaryKeyList,
        String updateColumnsList,
        String partitionColumn) {
      this.keyPath = keyPath;
      this.projectId = projectId;
      this.dataset = dataset;
      this.tableName = tableName;
      this.primaryKeyList = primaryKeyList;
      this.updateColumnsList = updateColumnsList;
      this.partitionColumn = partitionColumn;
    }
  }
}
