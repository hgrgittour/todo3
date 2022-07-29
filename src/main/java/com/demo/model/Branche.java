package com.demo.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.query.BasicQuery;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.util.StringUtils;

import java.util.ArrayList;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Document("branches")
public class Branche {

    @Id
    private ObjectId id;

    private ArrayList<String> alias;

    private String code;
    private String created_at;
    private Integer is_parent;

    private String name;
    private String parent_branch_id;
    private Integer status;

    private String type;//not life   life

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
