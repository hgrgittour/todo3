package com.demo.dao;

import com.demo.model.Branche;
import com.demo.model.Country;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface BrancheDao extends MongoRepository<Branche, String> {

    public List<Branche> findByName(String name) ;

}
