package com.affinitylabs.models.session;

import io.quarkus.mongodb.panache.PanacheMongoEntity;
import io.quarkus.mongodb.panache.common.MongoEntity;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;


@MongoEntity(collection = "ussd_blocked_users")
public class BlockedUsers extends PanacheMongoEntity {
    public String phoneNumber;
    public Status status;
    public LocalDateTime createdDate;
    public LocalDateTime modifiedDate;

    public static BlockedUsers findbyPhoneNumber(String phoneNumber) {
        return BlockedUsers.find("phoneNumber = ?1 and status = ?2", phoneNumber, Status.BLOCKED).firstResult();
    }

    public List<BlockedUsers> findbyStatus(Status status) {
        return BlockedUsers.find("status", status).list();
    }

    public void findandUpdate(String phoneNumber) {
        BlockedUsers.update("status", Status.UNBLOCKED).where("phoneNumber", phoneNumber);
    }
}
