package com.github.tden27.plans.service;

import com.sbt.pprb.ac.graph.collection.GraphCollection;
import org.springframework.stereotype.Service;
import ru.sbt.platformv.prod.d10179b25_22e0_4888_8948_f74ebecaeb88.graph.GraphCreator;
import ru.sbt.platformv.prod.d10179b25_22e0_4888_8948_f74ebecaeb88.graph.get.TaskHistoryGet;
import ru.sbt.platformv.prod.d10179b25_22e0_4888_8948_f74ebecaeb88.graph.with.TaskHistoryCollectionWith;
import ru.sbt.platformv.prod.d10179b25_22e0_4888_8948_f74ebecaeb88.grasp.TaskHistoryGrasp;
import sbp.sbt.sdk.exception.SdkJsonRpcClientException;
import sbp.sbt.sdk.search.DataspaceCoreSearchClient;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.List;

@Service
public class PlansService {

    private final DataspaceCoreSearchClient searchClient;

    public PlansService(DataspaceCoreSearchClient searchClient) {
        this.searchClient = searchClient;
    }

    public List<String> getPlansHistoryChanges(String planId) throws SdkJsonRpcClientException {
        List<String> result = new ArrayList<>();

        OffsetDateTime startOffset = OffsetDateTime.of(
                LocalDateTime.now().minusDays(LocalDate.now().getDayOfWeek().getValue() + 6), ZoneOffset.UTC);
        OffsetDateTime endOffset = OffsetDateTime.of(
                LocalDateTime.now().minusDays(LocalDate.now().getDayOfWeek().getValue()), ZoneOffset.UTC);

        TaskHistoryCollectionWith<TaskHistoryGrasp> taskHistoryGraspTaskHistoryCollectionWith = GraphCreator
                .selectTaskHistory()
                .setWhere(t -> t.aggregateRoot().nameEq(planId).and(t.sysHistoryTimeBetween(startOffset, endOffset)))
                .withSysState()
                .withSysHistoryOwner((tsk) -> {
                    try {
                        searchClient.getTask(GraphCreator.selectTask().withName());
                    } catch (SdkJsonRpcClientException e) {
                        e.printStackTrace();
                    }
                })
                .withName()
                .withSysNameUpdated()
                .withStartDate()
                .withSysStartDateUpdated()
                .withEndDate()
                .withSysEndDateUpdated()
                .withPercentDone()
                .withSysPercentDoneUpdated();

        GraphCollection<TaskHistoryGet> taskHistoryGets = searchClient
                .searchTaskHistory(taskHistoryGraspTaskHistoryCollectionWith);

        for (int i = 0; i < taskHistoryGets.size(); i++) {
            result.add(getChange(taskHistoryGets.get(i)));
        }
        return result;
    }

    private String getChange(TaskHistoryGet taskhistory) {
        String change;
        byte status = taskhistory.getSysState();
        switch (status) {
            case 0:
                change = "Создана запись " + taskhistory.getName() + " " + taskhistory.getStartDate() + " "
                        + taskhistory.getEndDate() + " " + taskhistory.getPercentDone();
                break;
            case 1:
            case 3:
                change = "Обновлена запись. Изменились поля: "
                        + (taskhistory.getSysNameUpdated() == null ? "" : (taskhistory.getName() + " "))
                        + (taskhistory.getSysStartDateUpdated() == null ? "" : (taskhistory.getStartDate() + " "))
                        + (taskhistory.getSysEndDateUpdated() == null ? "" : (taskhistory.getEndDate() + " "))
                        + (taskhistory.getSysPercentDoneUpdated() == null ? "" : taskhistory.getPercentDone());
                break;
            case 2:
                change = "Удалена запись c ID: " + taskhistory.getSysHistoryOwner().getObjectId();
                break;
            default:
                change = "Нет изменений";
        }
        return change;
    }
}
