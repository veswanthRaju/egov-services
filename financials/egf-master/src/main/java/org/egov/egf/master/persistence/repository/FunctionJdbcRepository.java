package org.egov.egf.master.persistence.repository;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.egov.common.domain.model.Pagination;
import org.egov.common.persistence.repository.JdbcRepository;
import org.egov.egf.master.domain.model.Function;
import org.egov.egf.master.domain.model.FunctionSearch;
import org.egov.egf.master.persistence.entity.FunctionEntity;
import org.egov.egf.master.persistence.entity.FunctionSearchEntity;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.stereotype.Service;

@Service
public class FunctionJdbcRepository extends JdbcRepository {
	private static final Logger LOG = LoggerFactory.getLogger(FunctionJdbcRepository.class);

	static {
		LOG.debug("init function");
		init(FunctionEntity.class);
		LOG.debug("end init function");
	}

	public FunctionEntity create(FunctionEntity entity) {

		entity.setId(UUID.randomUUID().toString().replace("-", ""));
		super.create(entity);
		return entity;
	}

	public FunctionEntity update(FunctionEntity entity) {
		super.update(entity);
		return entity;

	}

	public Pagination<Function> search(FunctionSearch domain) {
		FunctionSearchEntity functionSearchEntity = new FunctionSearchEntity();
		functionSearchEntity.toEntity(domain);

		String searchQuery = "select :selectfields from :tablename :condition  :orderby   ";

		Map<String, Object> paramValues = new HashMap<>();
		StringBuffer params = new StringBuffer();
		searchQuery = searchQuery.replace(":tablename", FunctionEntity.TABLE_NAME);

		searchQuery = searchQuery.replace(":selectfields", " * ");
		
		if (functionSearchEntity.getSortBy() != null && !functionSearchEntity.getSortBy().isEmpty()) {
                    validateSortByOrder(functionSearchEntity.getSortBy());
                    validateEntityFieldName(functionSearchEntity.getSortBy(), FunctionEntity.class);
                }
                
                String orderBy = "order by name asc";
                if (functionSearchEntity.getSortBy() != null && !functionSearchEntity.getSortBy().isEmpty())
                        orderBy = "order by " + functionSearchEntity.getSortBy();


		// implement jdbc specfic search
		if (functionSearchEntity.getId() != null) {
			if (params.length() > 0)
				params.append(" and ");
			params.append("id =:id");
			paramValues.put("id", functionSearchEntity.getId());
		}
		if (functionSearchEntity.getName() != null) {
			if (params.length() > 0)
				params.append(" and ");
			params.append("name =:name");
			paramValues.put("name", functionSearchEntity.getName());
		}
		if (functionSearchEntity.getCode() != null) {
			if (params.length() > 0)
				params.append(" and ");
			params.append("code =:code");
			paramValues.put("code", functionSearchEntity.getCode());
		}
		if (functionSearchEntity.getLevel() != null) {
			if (params.length() > 0)
				params.append(" and ");
			params.append("level =:level");
			paramValues.put("level", functionSearchEntity.getLevel());
		}
		if (functionSearchEntity.getActive() != null) {
			if (params.length() > 0)
				params.append(" and ");
			params.append("active =:active");
			paramValues.put("active", functionSearchEntity.getActive());
		}
		if (functionSearchEntity.getIsParent() != null) {
			if (params.length() > 0)
				params.append(" and ");
			params.append("isParent =:isParent");
			paramValues.put("isParent", functionSearchEntity.getIsParent());
		}
		if (functionSearchEntity.getParentId() != null) {
			if (params.length() > 0)
				params.append(" and ");
			params.append("parentId =:parentId");
			paramValues.put("parentId", functionSearchEntity.getParentId());
		}

		Pagination<Function> page = new Pagination<>();
		if (functionSearchEntity.getOffset() != null)
			page.setOffset(functionSearchEntity.getOffset());
		if (functionSearchEntity.getPageSize() != null)
			page.setPageSize(functionSearchEntity.getPageSize());

		if (params.length() > 0) {

			searchQuery = searchQuery.replace(":condition", " where " + params.toString());

		} else {
			searchQuery = searchQuery.replace(":condition", "");
		}
		
	        searchQuery = searchQuery.replace(":orderby", orderBy);

		page = (Pagination<Function>) getPagination(searchQuery, page, paramValues);
		searchQuery = searchQuery + " :pagination";

		searchQuery = searchQuery.replace(":pagination",
				"limit " + page.getPageSize() + " offset " + page.getOffset() * page.getPageSize());

		BeanPropertyRowMapper row = new BeanPropertyRowMapper(FunctionEntity.class);

		List<FunctionEntity> functionEntities = namedParameterJdbcTemplate.query(searchQuery.toString(), paramValues,
				row);

		page.setTotalResults(functionEntities.size());

		List<Function> functions = new ArrayList<Function>();
		for (FunctionEntity functionEntity : functionEntities) {

			functions.add(functionEntity.toDomain());
		}
		page.setPagedData(functions);

		return page;
	}

	public FunctionEntity findById(FunctionEntity entity) {
		List<String> list = allUniqueFields.get(entity.getClass().getSimpleName());

		Map<String, Object> paramValues = new HashMap<>();

		for (String s : list) {
			paramValues.put(s, getValue(getField(entity, s), entity));
		}

		List<FunctionEntity> functions = namedParameterJdbcTemplate.query(
				getByIdQuery.get(entity.getClass().getSimpleName()).toString(), paramValues,
				new BeanPropertyRowMapper(FunctionEntity.class));
		if (functions.isEmpty()) {
			return null;
		} else {
			return functions.get(0);
		}

	}

}