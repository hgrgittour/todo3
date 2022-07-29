package com.demo.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.util.StringUtils;

@Data
@AllArgsConstructor
@Document("regoin")
public class Region {

    @Id
    private ObjectId id;

    private String code;

    private String name;

    private String updated_at;

    public Query createQuery(){
        Query query = new Query();
        if (!StringUtils.isEmpty(id)){
            query.addCriteria(Criteria.where("id").is(id));
        }
        if (!StringUtils.isEmpty(code)){
            query.addCriteria(Criteria.where("code").is(code));
        }
        if (!StringUtils.isEmpty(name)){
            query.addCriteria(Criteria.where("name").is(name));
        }

        return query;
    }
}
