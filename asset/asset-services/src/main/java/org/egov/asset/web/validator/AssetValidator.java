package org.egov.asset.web.validator;

import java.math.BigDecimal;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang3.StringUtils;
import org.egov.asset.contract.AssetRequest;
import org.egov.asset.contract.DisposalRequest;
import org.egov.asset.contract.RevaluationRequest;
import org.egov.asset.model.Asset;
import org.egov.asset.model.AssetCategory;
import org.egov.asset.model.AssetStatus;
import org.egov.asset.model.DisposalCriteria;
import org.egov.asset.model.RevaluationCriteria;
import org.egov.asset.model.enums.TransactionType;
import org.egov.asset.model.enums.TypeOfChangeEnum;
import org.egov.asset.service.AssetCurrentAmountService;
import org.egov.asset.service.AssetMasterService;
import org.egov.asset.service.AssetService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class AssetValidator {

	private static final Logger logger = LoggerFactory.getLogger(AssetValidator.class);

	@Autowired
	private AssetCategoryValidator assetCategoryValidator;

	@Autowired
	private AssetService assetService;

	@Autowired
	private AssetCurrentAmountService assetCurrentAmountService;

	@Autowired
	private AssetMasterService assetMasterService;

	public void validateAsset(final AssetRequest assetRequest) {
		findAssetCategory(assetRequest);
		// findAsset(assetRequest); FIXME not need as per elzan remove the full
		// code later
	}

	public void findAssetCategory(final AssetRequest assetRequest) {

		final List<AssetCategory> assetCategories = assetCategoryValidator.findByIdAndCode(
				assetRequest.getAsset().getAssetCategory().getId(),
				assetRequest.getAsset().getAssetCategory().getCode(), assetRequest.getAsset().getTenantId());

		if (assetCategories.isEmpty())
			throw new RuntimeException("Invalid asset category");

	}

	public void findAsset(final AssetRequest assetRequest) {

		final Asset asset = assetRequest.getAsset();
		final String existingName = assetService.getAssetName(asset.getTenantId(), asset.getName());

		if (existingName != null) {
			if (existingName.equalsIgnoreCase(assetRequest.getAsset().getName())) {
				logger.info("duplicate asset with same name found");
				throw new RuntimeException("Duplicate asset name asset already exists");
			}
		} else
			logger.info("no duplicate asset with same name found");
	}

	public void validateRevaluationCriteria(final RevaluationCriteria revaluationCriteria) {
		if (revaluationCriteria.getFromDate() == null && revaluationCriteria.getToDate() != null)
			throw new RuntimeException("Invalid Search! from date required");
		if (revaluationCriteria.getFromDate() != null && revaluationCriteria.getToDate() == null)
			throw new RuntimeException("Invalid Search! to date required");
		if (revaluationCriteria.getFromDate() != null && revaluationCriteria.getToDate() != null
				&& revaluationCriteria.getToDate().compareTo(revaluationCriteria.getFromDate()) == -1)
			throw new RuntimeException("Invalid Search! to date should not be less than from date");

		if (revaluationCriteria.getFromDate() != null && revaluationCriteria.getToDate() != null
				&& revaluationCriteria.getToDate().compareTo(revaluationCriteria.getFromDate()) == 0)
			throw new RuntimeException("Invalid Search! to date should not be equal to from date");
	}

	public void validateDisposalCriteria(final DisposalCriteria disposalCriteria) {
		if (disposalCriteria.getFromDate() == null && disposalCriteria.getToDate() != null)
			throw new RuntimeException("Invalid Search! from date required");
		if (disposalCriteria.getFromDate() != null && disposalCriteria.getToDate() == null)
			throw new RuntimeException("Invalid Search! to date required");

		if (disposalCriteria.getFromDate() != null && disposalCriteria.getToDate() != null
				&& disposalCriteria.getToDate().compareTo(disposalCriteria.getFromDate()) == -1)
			throw new RuntimeException("Invalid Search! to date should not be less than from date");

		if (disposalCriteria.getFromDate() != null && disposalCriteria.getToDate() != null
				&& disposalCriteria.getToDate().compareTo(disposalCriteria.getFromDate()) == 0)
			throw new RuntimeException("Invalid Search! to date should not be equal to from date");
	}

	public void validateDisposal(final DisposalRequest disposalRequest) {
		final Asset asset = assetCurrentAmountService.getAsset(disposalRequest.getDisposal().getAssetId(),
				disposalRequest.getDisposal().getTenantId(), disposalRequest.getRequestInfo());
		validateAssetForDisposedStatus(asset);
		validateAssetForCapitalizedStatus(asset);

		if (StringUtils.isEmpty(disposalRequest.getDisposal().getBuyerName()))
			throw new RuntimeException("Buyer Name should be present for disposing asset : " + asset.getName());

		if (StringUtils.isEmpty(disposalRequest.getDisposal().getBuyerAddress()))
			throw new RuntimeException("Buyer Address should be present for disposing asset : " + asset.getName());

		if (StringUtils.isEmpty(disposalRequest.getDisposal().getDisposalReason()))
			throw new RuntimeException("Disposal Reason should be present for disposing asset : " + asset.getName());

		if (disposalRequest.getDisposal().getSaleValue() == null)
			throw new RuntimeException("Sale Value should be present for disposing asset : " + asset.getName());

		verifyPanCardAndAdhaarCardForAssetSale(disposalRequest);
		validateAssetCategoryForVoucherGeneration(asset);

//		if (asset.getAssetCategory() != null && asset.getAssetCategory().getAssetAccount() == null)
//			throw new RuntimeException("Asset account should be present for disposing asset : " + asset.getName());
//
//		if (disposalRequest.getDisposal().getAssetSaleAccount() == null)
//			throw new RuntimeException("Asset sale account should be present for asset disposal voucher generation");
	}

	private void verifyPanCardAndAdhaarCardForAssetSale(final DisposalRequest disposalRequest) {
		final String adhaarcardNumber = disposalRequest.getDisposal().getAadharCardNumber();
		final String pancardNumber = disposalRequest.getDisposal().getPanCardNumber();
		final Pattern pattern = Pattern.compile("[A-Z]{5}[0-9]{4}[A-Z]{1}");
		final Matcher matcher = pattern.matcher(pancardNumber);

		if (TransactionType.SALE.compareTo(disposalRequest.getDisposal().getTransactionType()) == 0
				&& StringUtils.isEmpty(adhaarcardNumber))
			throw new RuntimeException("Aadhar Card Number is necessary for asset sale");

		if (adhaarcardNumber.length() < 12 && !adhaarcardNumber.matches("\\d+"))
			throw new RuntimeException("Aadhar Card Number should be numeric and should have length 12");

		if (TransactionType.SALE.compareTo(disposalRequest.getDisposal().getTransactionType()) == 0
				&& StringUtils.isEmpty(pancardNumber))
			throw new RuntimeException("PAN Card Number is necessary for asset sale");

		if (!matcher.matches())
			throw new RuntimeException("PAN Card Number Should be in Format : ABCDE1234F");
	}

	private void validateAssetForCapitalizedStatus(final Asset asset) {
		final List<AssetStatus> assetStatus = assetMasterService.getStatuses("Asset Master", "CAPITALIZED",
				asset.getTenantId());
		if (!assetStatus.get(0).getStatusValues().get(0).getCode().equalsIgnoreCase(asset.getStatus()))
			throw new RuntimeException("Status of Asset " + asset.getName()
					+ " Should be Captalized for Reevaluation, Depreciation and Disposal/sale");
	}

	private void validateAssetForDisposedStatus(final Asset asset) {
		final List<AssetStatus> assetStatus = assetMasterService.getStatuses("Asset Master", "DISPOSED",
				asset.getTenantId());
		if (assetStatus.get(0).getStatusValues().get(0).getCode().equalsIgnoreCase(asset.getStatus()))
			throw new RuntimeException("Asset " + asset.getName() + " is already Disposed");
	}

	public void validateRevaluation(final RevaluationRequest revaluationRequest) {
		final Asset asset = assetCurrentAmountService.getAsset(revaluationRequest.getRevaluation().getAssetId(),
				revaluationRequest.getRevaluation().getTenantId(), revaluationRequest.getRequestInfo());

		validateAssetForCapitalizedStatus(asset);

		validateAssetCategoryForVoucherGeneration(asset);

		final TypeOfChangeEnum typeOfChange = validateRevaluationForTypeOfChange(revaluationRequest, asset);

		if (revaluationRequest.getRevaluation().getRevaluationAmount() == null)
			throw new RuntimeException(
					"The amount by which the value is increased/decreased is necessary for revaluation");

		if (revaluationRequest.getRevaluation().getFunction() == null)
			throw new RuntimeException("Function from financials is necessary for asset revaluation");

		if (revaluationRequest.getRevaluation().getFund() == null)
			throw new RuntimeException("Fund from financials is necessary for asset revaluation");

		final BigDecimal assetCurrentAmount = assetCurrentAmountService.getCurrentAmount(asset.getId(),
				revaluationRequest.getRevaluation().getTenantId(), revaluationRequest.getRequestInfo())
				.getAssetCurrentValue().getCurrentAmmount();

		if (typeOfChange != null && TypeOfChangeEnum.DECREASED.compareTo(typeOfChange) == 0
				&& (revaluationRequest.getRevaluation().getValueAfterRevaluation().compareTo(assetCurrentAmount) == 0
						|| revaluationRequest.getRevaluation().getValueAfterRevaluation()
								.compareTo(assetCurrentAmount) == -1))
			throw new RuntimeException(
					"Decrease in amount should not be equal or greater than the gross value of the asset. current gross value of asset is "
							+ assetCurrentAmount + " and value after revaluation is "
							+ revaluationRequest.getRevaluation().getValueAfterRevaluation());

	}

	private TypeOfChangeEnum validateRevaluationForTypeOfChange(final RevaluationRequest revaluationRequest,
			final Asset asset) {
		final TypeOfChangeEnum typeOfChange = revaluationRequest.getRevaluation().getTypeOfChange();
		if (typeOfChange == null)
			throw new RuntimeException("Type Of Change is necessary for asset revaluation");

//		if (typeOfChange != null && TypeOfChangeEnum.DECREASED.compareTo(typeOfChange) == 0
//				&& revaluationRequest.getRevaluation().getFixedAssetsWrittenOffAccount() == null)
//			throw new RuntimeException("Fixed Asset Written Off Account is necessary for asset " + asset.getName()
//					+ " voucher generation for revaluation");
//
//		if (typeOfChange != null
//				&& (TypeOfChangeEnum.DECREASED.compareTo(typeOfChange) == 0
//						|| TypeOfChangeEnum.INCREASED.compareTo(typeOfChange) == 0)
//				&& asset.getAssetCategory() != null && asset.getAssetCategory().getAssetAccount() == null)
//			throw new RuntimeException(
//					"Asset Account is necessary for asset " + asset.getName() + " voucher generation for revaluation");
//
//		if (typeOfChange != null && TypeOfChangeEnum.INCREASED.compareTo(typeOfChange) == 0
//				&& asset.getAssetCategory() != null && asset.getAssetCategory().getRevaluationReserveAccount() == null)
//			throw new RuntimeException("Revaluation Reserve Account is necessary for asset " + asset.getName()
//					+ " voucher generation for revaluation");
		return typeOfChange;
	}

	private void validateAssetCategoryForVoucherGeneration(final Asset asset) {
		if (asset.getAssetCategory() == null)
			throw new RuntimeException(
					"Asset Category should be present for asset " + asset.getName() + " for voucher generation");
	}

}
