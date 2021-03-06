package org.egov.lams.repository;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.egov.lams.config.PropertiesManager;
import org.egov.lams.model.Agreement;
import org.egov.lams.model.AgreementCriteria;
import org.egov.lams.model.Allottee;
import org.egov.lams.model.Asset;
import org.egov.lams.model.Cancellation;
import org.egov.lams.model.Document;
import org.egov.lams.model.Eviction;
import org.egov.lams.model.Renewal;
import org.egov.lams.model.enums.Action;
import org.egov.lams.repository.builder.AgreementQueryBuilder;
import org.egov.lams.repository.helper.AgreementHelper;
import org.egov.lams.repository.helper.AllotteeHelper;
import org.egov.lams.repository.helper.AssetHelper;
import org.egov.lams.repository.rowmapper.AgreementRowMapper;
import org.egov.lams.web.contract.AgreementRequest;
import org.egov.lams.web.contract.AllotteeResponse;
import org.egov.lams.web.contract.AssetResponse;
import org.egov.lams.web.contract.RequestInfo;
import org.egov.lams.web.contract.RequestInfoWrapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
public class AgreementRepository {
	public static final Logger logger = LoggerFactory.getLogger(AgreementRepository.class);

	@Autowired
	private AssetHelper assetHelper;

	@Autowired
	private JdbcTemplate jdbcTemplate;

	@Autowired
	private AssetRepository assetRepository;

	@Autowired
	private AllotteeRepository allotteeRepository;

	@Autowired
	private AllotteeHelper allotteeHelper;

	@Autowired
	private AgreementHelper agreementHelper;

	@Autowired
	private PropertiesManager propertiesManager;

	public boolean isAgreementExist(String code) {

		Long  agreementId = null;
		String sql = AgreementQueryBuilder.AGREEMENT_QUERY;
		Object[] preparedStatementValues = new Object[] { code ,code };

		try {
			agreementId = jdbcTemplate.queryForList(sql, preparedStatementValues, Long.class).get(0);
		} catch (DataAccessException e) {
			logger.info("exception in getagreementbyid :: " + e);
			throw new RuntimeException(e.getMessage());
		}
		
		return (agreementId != null && agreementId != 0);
	}
	
	public List<Agreement> getAgreementForCriteria(AgreementCriteria agreementsModel) {

		List<Agreement> agreements = null;
		List<Object> preparedStatementValues = new ArrayList<>();
		String sql = AgreementQueryBuilder.getAgreementSearchQuery(agreementsModel, preparedStatementValues);
		try {
			agreements = jdbcTemplate.query(sql, preparedStatementValues.toArray(),new AgreementRowMapper());
		} catch (DataAccessException e) {
			logger.info("exception in getagreement :: " + e);
			throw new RuntimeException(e.getMessage());
		}
		return agreements;
	}

	public List<Agreement> findByAllotee(AgreementCriteria agreementCriteria,RequestInfo requestInfo) {
		List<Object> preparedStatementValues = new ArrayList<>();
		List<Agreement> agreements = null;

		List<Allottee> allottees = getAllottees(agreementCriteria,requestInfo);
		agreementCriteria.setAllottee(allotteeHelper.getAllotteeIdList(allottees));
		String queryStr = AgreementQueryBuilder.getAgreementSearchQuery(agreementCriteria, preparedStatementValues);
		try {
			agreements = jdbcTemplate.query(queryStr, preparedStatementValues.toArray(), new AgreementRowMapper());
		} catch (DataAccessException e) {
			logger.info("exception in agreementrepo jdbc temp :"+ e);
			throw new RuntimeException(e.getMessage());
		}
		if (agreements.isEmpty())
			return agreements; // empty agreement list is returned
		// throw new RuntimeException("The criteria provided did not match any
		// agreements");
		agreementCriteria.setAsset(assetHelper.getAssetIdListByAgreements(agreements));

		List<Asset> assets = getAssets(agreementCriteria,requestInfo);
		agreements = agreementHelper.filterAndEnrichAgreements(agreements, allottees, assets);

		return agreements;
	}

	public List<Agreement> findByAsset(AgreementCriteria agreementCriteria,RequestInfo requestInfo) {
		logger.info("AgreementController SearchAgreementService AgreementRepository : inside findByAsset");
		List<Object> preparedStatementValues = new ArrayList<Object>();
		List<Agreement> agreements = null;
		System.out.println("before calling get asset method");
		List<Asset> assets = getAssets(agreementCriteria,requestInfo);
		System.out.println("after calling get asset method : lengeth of result is" + assets.size());
		if (assets.size() > 1000) // FIXME
			throw new RuntimeException("Asset criteria is too big");
		agreementCriteria.setAsset(assetHelper.getAssetIdList(assets));
		String queryStr = AgreementQueryBuilder.getAgreementSearchQuery(agreementCriteria, preparedStatementValues);
		try {
			agreements = jdbcTemplate.query(queryStr, preparedStatementValues.toArray(), new AgreementRowMapper());
		} catch (DataAccessException e) {
			logger.info(e.getMessage(), e.getCause());
			throw new RuntimeException(e.getMessage());
		}
		if (agreements.isEmpty())
			return agreements;
		agreementCriteria.setAllottee(allotteeHelper.getAllotteeIdListByAgreements(agreements));
		List<Allottee> allottees = getAllottees(agreementCriteria,requestInfo);
		agreements = agreementHelper.filterAndEnrichAgreements(agreements, allottees, assets);

		return agreements;
	}

	public List<Agreement> findByAgreement(AgreementCriteria agreementCriteria,RequestInfo requestInfo) {
		logger.info("AgreementController SearchAgreementService AgreementRepository : inside findByAgreement");
		List<Object> preparedStatementValues = new ArrayList<>();
		List<Agreement> agreements = null;

		String queryStr = AgreementQueryBuilder.getAgreementSearchQuery(agreementCriteria, preparedStatementValues);
		try {
			agreements = jdbcTemplate.query(queryStr, preparedStatementValues.toArray(), new AgreementRowMapper());
		} catch (DataAccessException e) {
			throw new RuntimeException(e.getMessage());
		}
		if (agreements.isEmpty())
			return agreements;
		agreementCriteria.setAsset(assetHelper.getAssetIdListByAgreements(agreements));
		agreementCriteria.setAllottee(allotteeHelper.getAllotteeIdListByAgreements(agreements));
		List<Asset> assets = getAssets(agreementCriteria,requestInfo);
		List<Allottee> allottees = getAllottees(agreementCriteria,requestInfo);
		agreements = agreementHelper.filterAndEnrichAgreements(agreements, allottees, assets);

		return agreements;
	}

	public List<Agreement> findByAgreementAndAllotee(AgreementCriteria agreementCriteria,RequestInfo requestInfo) {
		logger.info(
				"AgreementController SearchAgreementService AgreementRepository : inside findByAgreementAndAllotee");
		List<Object> preparedStatementValues = new ArrayList<Object>();
		List<Agreement> agreements = null;

		String queryStr = AgreementQueryBuilder.getAgreementSearchQuery(agreementCriteria, preparedStatementValues);
		try {
			agreements = jdbcTemplate.query(queryStr, preparedStatementValues.toArray(), new AgreementRowMapper());
		} catch (DataAccessException e) {
			throw new RuntimeException(e.getMessage());
		}
		if (agreements.isEmpty())
			return agreements;
		agreementCriteria.setAllottee(allotteeHelper.getAllotteeIdListByAgreements(agreements));
		List<Allottee> allottees = getAllottees(agreementCriteria,requestInfo);
		agreementCriteria.setAsset(assetHelper.getAssetIdListByAgreements(agreements));
		List<Asset> assets = getAssets(agreementCriteria,requestInfo);
		agreements = agreementHelper.filterAndEnrichAgreements(agreements, allottees, assets);

		return agreements;
	}

	public List<Agreement> findByAgreementAndAsset(AgreementCriteria fetchAgreementsModel,RequestInfo requestInfo) {
		logger.info("AgreementController SearchAgreementService AgreementRepository : inside findByAgreementAndAsset");
		List<Object> preparedStatementValues = new ArrayList<>();
		List<Agreement> agreements = null;

		String queryStr = AgreementQueryBuilder.getAgreementSearchQuery(fetchAgreementsModel, preparedStatementValues);
		try {
			agreements = jdbcTemplate.query(queryStr, preparedStatementValues.toArray(), new AgreementRowMapper());
		} catch (DataAccessException e) {
			throw new RuntimeException(e.getMessage());
		}
		if (agreements.isEmpty())
			return agreements;
		fetchAgreementsModel.setAsset(assetHelper.getAssetIdListByAgreements(agreements));
		List<Asset> assets = getAssets(fetchAgreementsModel,requestInfo);
		fetchAgreementsModel.setAllottee(allotteeHelper.getAllotteeIdListByAgreements(agreements));
		List<Allottee> allottees = getAllottees(fetchAgreementsModel,requestInfo);
		agreements = agreementHelper.filterAndEnrichAgreements(agreements, allottees, assets);

		return agreements;
	}

	/*
	 * method to return a list of Allottee objects by making an API call to
	 * Allottee API
	 */
	public List<Allottee> getAllottees(AgreementCriteria agreementCriteria,RequestInfo requestInfo) {
		// FIXME TODO urgent allottee helper has to be changed for post
		// String queryString =
		// allotteeHelper.getAllotteeParams(agreementCriteria);
		logger.info("AgreementController SearchAgreementService AgreementRepository : inside Allottee API caller");
		AllotteeResponse allotteeResponse = allotteeRepository.getAllottees(agreementCriteria, new RequestInfo());
		if (allotteeResponse.getAllottee() == null || allotteeResponse.getAllottee().size() <= 0)
			throw new RuntimeException("No allottee found for given criteria");
		System.err.println("the result allottee response from allottee api call : " + allotteeResponse.getAllottee());
		return allotteeResponse.getAllottee();
	}

	/*
	 * method to return a list of Asset objects by calling AssetService API
	 */
	public List<Asset> getAssets(AgreementCriteria agreementCriteria,RequestInfo requestInfo) {
		System.out.println("inside get asset method");
		String queryString = assetHelper.getAssetParams(agreementCriteria);
		AssetResponse assetResponse = assetRepository.getAssets(queryString, new RequestInfoWrapper());
		if (assetResponse.getAssets() == null || assetResponse.getAssets().size() <= 0)
			throw new RuntimeException("No assets found for given criteria");
		// FIXME empty response exception
		System.err.println("the result asset response from asset api call : " + assetResponse.getAssets());
		return assetResponse.getAssets();
	}
	
	@Transactional
	public void saveAgreement(AgreementRequest agreementRequest) {
		
		Map<String, Object> processMap = getProcessMap(agreementRequest);
		Agreement agreement = agreementRequest.getAgreement();
		logger.info("AgreementDao agreement::" + agreement);
		
		String agreementinsert = AgreementQueryBuilder.INSERT_AGREEMENT_QUERY;
		
		Long rentIncrement = null;
		if(agreement.getRentIncrementMethod() !=null)
			rentIncrement = agreement.getRentIncrementMethod().getId();
		
		Object[] obj = new Object[] { agreement.getId(), agreement.getAgreementDate(), agreement.getAgreementNumber(),
				agreement.getBankGuaranteeAmount(), agreement.getBankGuaranteeDate(), agreement.getCaseNo(),
				agreement.getCommencementDate(), agreement.getCouncilDate(), agreement.getCouncilNumber(),
				agreement.getExpiryDate(), agreement.getNatureOfAllotment().toString(), processMap.get("orderDate"),
				agreement.getOrderDetails(), processMap.get("orderNumber"), agreement.getPaymentCycle().toString(),
				agreement.getRegistrationFee(), agreement.getRemarks(), agreement.getRent(), agreement.getRrReadingNo(),
				agreement.getSecurityDeposit(), agreement.getSecurityDepositDate(),
				agreement.getSolvencyCertificateDate(), agreement.getSolvencyCertificateNo(),
				agreement.getStatus().toString(), agreement.getTinNumber(), agreement.getTenderDate(),
				agreement.getTenderNumber(), agreement.getTradelicenseNumber(), agreement.getCreatedBy(),
				agreement.getLastmodifiedBy(), agreement.getCreatedDate(), agreement.getLastmodifiedDate(), agreement.getAllottee().getId(),
				agreement.getAsset().getId(), rentIncrement, agreement.getAcknowledgementNumber(),
				agreement.getStateId(), agreement.getTenantId(), agreement.getGoodWillAmount(),
				agreement.getTimePeriod(), agreement.getCollectedSecurityDeposit(),
				agreement.getCollectedGoodWillAmount(), agreement.getSource().toString(),processMap.get("reason"),
				processMap.get("terminationDate"),processMap.get("courtReferenceNumber"),agreement.getAction().toString() };

		try {
			jdbcTemplate.update(agreementinsert, obj);
		} catch (DataAccessException ex) {
			ex.printStackTrace();
			throw new RuntimeException(ex.getMessage());
		}
		
		List<String> demands = agreement.getDemands();
		if (demands != null) {
			String sql = "INSERT INTO eglams_demand values ( nextval('seq_eglams_demand'),?,?,?)";
			List<Object[]> demandBatchArgs = new ArrayList<>();
			int demandsCount = demands.size();

			for (int i = 0; i < demandsCount; i++) {
				Object[] demandRecord = { agreement.getTenantId(), agreement.getId(), demands.get(i) };
				demandBatchArgs.add(demandRecord);
			}

			try {
				jdbcTemplate.batchUpdate(sql, demandBatchArgs);
			} catch (DataAccessException ex) {
				ex.printStackTrace();
				throw new RuntimeException(ex.getMessage());
			}
		}
		
		List<Document> documents = agreement.getDocuments();
		if(documents != null){
			String sql = "INSERT INTO eglams_document (id,documenttype,agreement,filestore,tenantid) values "
					+ "(nextval('seq_eglams_document'),(select id from eglams_documenttype where "
					+ "name='Agreement Docs' and application='CREATE' and tenantid='"
					+ agreement.getTenantId()+"'),?,?,?);";
			logger.info("the insert query for agreement docs : "+sql);
			List<Object[]> documentBatchArgs = new ArrayList<>();
			
			for (Document document : documents) {
				Object[] documentRecord = { agreement.getId(),document.getFileStore(), agreement.getTenantId() };
				documentBatchArgs.add(documentRecord);
			}
		
			try {
				jdbcTemplate.batchUpdate(sql, documentBatchArgs);
			} catch (DataAccessException ex) {
				ex.printStackTrace();
				throw new RuntimeException(ex.getMessage());
			}
		}
		
	}

	public void updateAgreement(AgreementRequest agreementRequest) {

		Map<String, Object> processMap = getProcessMap(agreementRequest);
		Agreement agreement = agreementRequest.getAgreement();
		logger.info("AgreementDao agreement::" + agreement);

		String agreementUpdate = AgreementQueryBuilder.updateAgreementQuery();

		Object[] obj = new Object[] { agreement.getId(),agreement.getAgreementDate(), agreement.getAgreementNumber(),
				agreement.getBankGuaranteeAmount(), agreement.getBankGuaranteeDate(), agreement.getCaseNo(),
				agreement.getCommencementDate(), agreement.getCouncilDate(), agreement.getCouncilNumber(),
				agreement.getExpiryDate(), agreement.getNatureOfAllotment().toString(), processMap.get("orderDate"),
				agreement.getOrderDetails(),processMap.get("orderNumber"), agreement.getPaymentCycle().toString(),
				agreement.getRegistrationFee(), agreement.getRemarks(), agreement.getRent(), agreement.getRrReadingNo(),
				agreement.getSecurityDeposit(), agreement.getSecurityDepositDate(),
				agreement.getSolvencyCertificateDate(), agreement.getSolvencyCertificateNo(),
				agreement.getStatus().toString(), agreement.getTinNumber(), agreement.getTenderDate(),
				agreement.getTenderNumber(), agreement.getTradelicenseNumber(), agreement.getLastmodifiedBy(), agreement.getLastmodifiedDate(),
				agreement.getAllottee().getId(), agreement.getAsset().getId(),
				agreement.getRentIncrementMethod().getId(), agreement.getAcknowledgementNumber(),
				agreement.getStateId(), agreement.getTenantId(),agreement.getGoodWillAmount(),
				agreement.getTimePeriod(),agreement.getCollectedSecurityDeposit(),
				agreement.getCollectedGoodWillAmount(),agreement.getSource().toString(),
				processMap.get("reason"),processMap.get("terminationDate"),
				processMap.get("courtReferenceNumber"),agreement.getAction().toString(),
				
				agreement.getAcknowledgementNumber(),agreement.getTenantId()};

		try {
			jdbcTemplate.update(agreementUpdate, obj);
		} catch (DataAccessException ex) {
			logger.error("the exception from update demand in update agreement "+ex);
			throw new RuntimeException(ex.getMessage());
		}

		String demandQuery = "select demandid from eglams_demand where agreementid=" + agreement.getId();
		List<String> demandIdList = jdbcTemplate.queryForList(demandQuery, String.class);

		if (demandIdList.isEmpty() && agreement.getDemands() != null) {

			List<String> demands = agreement.getDemands();

			String sql = "INSERT INTO eglams_demand values ( nextval('seq_eglams_demand'),?,?,?)";
			List<Object[]> demandBatchArgs = new ArrayList<>();
			int demandsCount = demands.size();

			for (int i = 0; i < demandsCount; i++) {
				Object[] demandRecord = { agreement.getTenantId(), agreement.getId(), demands.get(i) };
				demandBatchArgs.add(demandRecord);
			}

			try {
				jdbcTemplate.batchUpdate(sql, demandBatchArgs);
			} catch (DataAccessException ex) {
				logger.error("the exception from add demand in update agreement "+ex);
				throw new RuntimeException(ex.getMessage());
			}
		}
	}
	
	private Map<String, Object> getProcessMap(AgreementRequest agreementRequest) {

		Agreement agreement = agreementRequest.getAgreement();
		String orderNumber = null;
		String reason = null;
		String courtReferenceNumber = null;
		Date orderDate = null;
		Date terminationDate = null;
		Action action = agreement.getAction();

		if(action!=null){
		switch (action) {

		case CANCELLATION:
					Cancellation cancellation = agreement.getCancellation();
					orderNumber = cancellation.getOrderNumber();
					orderDate = cancellation.getOrderDate();
					reason = cancellation.getReasonForCancellation().toString();
					terminationDate = cancellation.getTerminationDate();
					break;
		case RENEWAL:
					Renewal renewal = agreement.getRenewal();
					orderNumber = renewal.getRenewalOrderNumber();
					orderDate = renewal.getRenewalOrderDate();
					reason = renewal.getReasonForRenewal();
					break;
		case EVICTION:
			        Eviction eviction = agreement.getEviction();
			        orderNumber = eviction.getEvictionProceedingNumber();
			        orderDate = eviction.getEvictionProceedingDate();
			        reason = eviction.getReasonForEviction();
			        courtReferenceNumber = eviction.getCourtReferenceNumber();
			        break;
		case CREATE:
			        orderNumber = agreement.getOrderNumber();
			        orderDate = agreement.getOrderDate();
			        break;
		case OBJECTION:
			break;
		}
		}
		Map<String, Object> processMap = new HashMap<>();
		processMap.put("orderNumber", orderNumber);
		processMap.put("orderDate", orderDate);
		processMap.put("reason", reason);
		processMap.put("courtReferenceNumber", courtReferenceNumber);
		processMap.put("terminationDate", terminationDate);

		return processMap;
	}
	
	public Long getAgreementID() {
		String agreementIdQuery = "select nextval('seq_eglams_agreement')";
		try {
			return jdbcTemplate.queryForObject(agreementIdQuery, Long.class);
		} catch (DataAccessException ex) {
			ex.printStackTrace();
			throw new RuntimeException(ex.getMessage());
		}
	}
	
	public void updateAgreementAdvance(String acknowledgementNumber) {
		String sql = "UPDATE eglams_agreement set is_advancepaid = true where acknowledgementnumber = '"
				+ acknowledgementNumber + "'";
		logger.info("advance paid update query :" , sql);
		try {

			jdbcTemplate.update(sql);
		} catch (DataAccessException ex) {
			logger.info("exception while updating is_advancepaid flag" + ex);
		}
	}
}
