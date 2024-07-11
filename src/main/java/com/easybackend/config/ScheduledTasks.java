package com.easybackend.config;

import com.easybackend.model.DynamicData;
import com.easybackend.repository.DynamicDataRepository;
import com.easybackend.service.DynamicDataService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.List;

@Component
public class ScheduledTasks {

    @Autowired
    private DynamicDataRepository repository;
    @Autowired
    private DynamicDataService dynamicDataService;

    @Scheduled(cron = "0 0 0 * * ?") // Run daily at midnight
    public void deleteOldData() {
        System.err.println("Inside Cron");
        LocalDate oneMonthAgo = LocalDate.now().minusMonths(1);
        System.err.println("Inside Cron "+oneMonthAgo.toString());
        List<DynamicData> oldData = repository.findAllByCreatedAtBefore(oneMonthAgo);

        for (DynamicData data : oldData) {
            String uniqueKey = data.getUniqueKey();
            dynamicDataService.deleteAllData(uniqueKey);
            evict(uniqueKey);
        }
    }

    @CacheEvict(value = {"dynamicDataDtoById", "dynamicDataDto"}, key = "#uniqueKey")
    private void evict(String uniqueKey) {
        // This method will be used to clear cache
    }

}
