package com.berenberg.Library.service;

import com.berenberg.Library.dao.LibraryItemsDao;
import com.berenberg.Library.model.LibraryItem;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.List;

@Service
public class OverdueItemSchedulerService {
    private static Logger logger = LoggerFactory.getLogger(OverdueItemSchedulerService.class);
    @Autowired
    private LibraryItemsDao libraryItemsDao;

    @Scheduled(cron = "0 0 0 * * *")
    public void checkOverdueStatus() {
        LocalDate currentDate = Instant.now().atZone(ZoneId.systemDefault()).toLocalDate();
        logger.info("Starting scheduler for Overdue items for date: " + currentDate);
        List<LibraryItem> overdueList = libraryItemsDao.findAndSetOverDueItems();
    }
}
