package com.demo.service.impl;

import com.demo.dao.BrancheDao;
import com.demo.dto.InputDto;
import com.demo.dto.ResultDto;
import com.demo.model.*;
import com.demo.service.QueryService;
import com.mongodb.BasicDBObject;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Example;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.*;
import org.springframework.data.mongodb.core.query.BasicQuery;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;


@Component
public class QueryServiceImpl implements QueryService {

    @Autowired
    private MongoTemplate template;

    @Autowired
    private BrancheDao brancheDao;

    @Override
    public List<ResultDto> listAllByParam(InputDto inputDto) {

        List<ObjectId> cedantsIds = generateCedantsIds(inputDto.getCountry(), inputDto.getRegion(), inputDto.getGroupsCedant());
        List<ObjectId> branchIds = generateBranchIds(inputDto.getBranche());
        List<ObjectId> splipsIds = generateSplipsIds(inputDto.getSlipsPremium());


        Criteria cedantsIdFilter = Criteria.where("cedants_id").in(cedantsIds);
        Criteria slipsIdFilter = Criteria.where("branches_id").in(branchIds);
        Criteria brancheIdFilter = Criteria.where("slipes_prime_id").in(splipsIds);
        Aggregation aggregation = Aggregation.newAggregation(
                Aggregation.match(cedantsIdFilter),
                Aggregation.match(slipsIdFilter),
                Aggregation.match(brancheIdFilter),
                Aggregation.group("slipes_prime_id").sum("premium_ht").as("totalht")
                        .last("branches_id").as("branches_id")
                        .last("branch").as("branch")
                        .last("cedants_id").as("cedants_id")
                        .last("slipes_prime_id").as("slipes_prime_id")
        );
        List<CaseNotLifePremium> caseNotLifePremiumss = template.aggregate(aggregation, "case_not_life_premium",
                CaseNotLifePremium.class).getMappedResults();

        List<ResultDto> list = generateDtoAndFillData(caseNotLifePremiumss);

        return list;
    }
    private List<ResultDto> generateDtoAndFillData(List<CaseNotLifePremium> caseNotLifePremiumss) {
        List<ResultDto> list = new ArrayList<>();
        for (int i = 0; i < caseNotLifePremiumss.size(); i++) {
            ResultDto dto = new ResultDto();
            CaseNotLifePremium caseNotLifePremium = caseNotLifePremiumss.get(i);

            Query slipsPremiumQuery = new Query();
            slipsPremiumQuery.addCriteria(Criteria.where("_id").is(caseNotLifePremium.getSlipes_prime_id()));
            SlipsPremium slipsPremium = template.findOne(slipsPremiumQuery, SlipsPremium.class);
            dto.setReference(slipsPremium.getReference());
            dto.setValidationStatus(slipsPremium.getValidation_status());
            dto.setConfirmationStatus(slipsPremium.getConfirmation_status());
            dto.setPublicationDate(slipsPremium.getPublished_date());

            Query cedantQuery = new Query();
            cedantQuery.addCriteria(Criteria.where("_id").is(caseNotLifePremium.getCedants_id()));
            Cedant cedant = template.findOne(cedantQuery, Cedant.class);
            dto.setCedantName(cedant.getName());
            dto.setBranchName(caseNotLifePremium.getBranch());

            Query countryQuery = new Query();
            countryQuery.addCriteria(Criteria.where("_id").is(cedant.getCountries_id()));
            Country country = template.findOne(countryQuery, Country.class);
            dto.setCountryName(country.getName());
            dto.setCalculatedREC(caseNotLifePremium.getTotalht() * 0.36);
            list.add(dto);
        }
        return list;
    }

    private List<ObjectId> generateCedantsIds(Country country, Region region, GroupsCedant groupsCedant) {
        List<Country> countries = null;
        if (country != null) {
            countries = template.find(country.createQuery(), Country.class);
        }
        List<Region> regions = null;
        if (region != null) {
            regions = template.find(region.createQuery(), Region.class);
        }
        List<GroupsCedant> groupsCedants = null;
        if (groupsCedant != null) {
            groupsCedants = template.find(groupsCedant.createQuery(), GroupsCedant.class);
        }

        Query cedantQuery = new Query();
        if (countries != null) {
            List<ObjectId> collect = countries.stream().map(Country::getId).collect(Collectors.toList());
            cedantQuery.addCriteria(Criteria.where("countries_id").in(collect));
        }
        if (regions != null) {
            List<ObjectId> collect = regions.stream().map(Region::getId).collect(Collectors.toList());
            cedantQuery.addCriteria(Criteria.where("region_id").in(collect));
        }
        if (groupsCedants != null) {
            List<ObjectId> collect = groupsCedants.stream().map(GroupsCedant::getId).collect(Collectors.toList());
            cedantQuery.addCriteria(Criteria.where("groups_cedants_id").in(collect));
        }
        List<Cedant> cedants = template.find(cedantQuery, Cedant.class);

        List<ObjectId> cedantsIds = new ArrayList<>();
        if (cedants.size() > 0) {
            cedantsIds = cedants.stream().map(Cedant::getId).collect(Collectors.toList());
        }
        return cedantsIds;
    }

    private List<ObjectId> generateBranchIds(Branche branche) {
        if (branche != null) {
            List<Branche> branches = template.find(branche.createQuery(), Branche.class,"branches");
            List<ObjectId> collect = branches.stream().map(Branche::getId).collect(Collectors.toList());
            return collect;
        } else {
            List<Branche> branches = template.findAll(Branche.class);
            List<ObjectId> collect = branches.stream().map(Branche::getId).collect(Collectors.toList());
            return collect;
        }
    }

    private List<ObjectId> generateSplipsIds(SlipsPremium slipsPremium) {
        if (slipsPremium != null) {
            List<SlipsPremium> slipsPremiums = template.find(slipsPremium.createQuery(), SlipsPremium.class);
            List<ObjectId> collect = slipsPremiums.stream().map(SlipsPremium::getId).collect(Collectors.toList());
            return collect;
        } else{
            List<SlipsPremium> slipsPremiums = template.findAll(SlipsPremium.class);
            List<ObjectId> collect = slipsPremiums.stream().map(SlipsPremium::getId).collect(Collectors.toList());
            return collect;
        }
    }
}
