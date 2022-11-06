package com.dronfies.trafficinformationservice.daos;

import com.dronfies.trafficinformationservice.daos.db.InMemoryDB;
import com.dronfies.trafficinformationservice.model.USpace;
import org.springframework.stereotype.Repository;

@Repository
public class USpaceDAO {

    public USpace getUSpace(String id){
        return InMemoryDB.getUSpace(id);
    }
}
