/**
 * 
 */
package com.excelsiorsoft.globalpayments.feeds.downstream.dailycashreport.acquire.provider;

/**
 * @author x266345
 *
 */

import org.springframework.batch.item.file.mapping.FieldSetMapper;
import org.springframework.batch.item.file.transform.FieldSet;

import com.excelsiorsoft.globalpayments.feeds.downstream.dailycashreport.domain.Trade;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class TradeFieldSetMapper implements FieldSetMapper<Trade> {

	public static final int ISIN_COLUMN = 0;

	public static final int QUANTITY_COLUMN = 1;

	public static final int PRICE_COLUMN = 2;

	public static final int CUSTOMER_COLUMN = 3;

	@Override
	public Trade mapFieldSet(FieldSet fieldSet) {

		log.info("\n\nMapping a fieldSet {}", fieldSet);
		Trade trade = new Trade();
		trade.setIsin(fieldSet.readString(ISIN_COLUMN));
		trade.setQuantity(fieldSet.readLong(QUANTITY_COLUMN));
		trade.setPrice(fieldSet.readBigDecimal(PRICE_COLUMN));
		trade.setCustomer(fieldSet.readString(CUSTOMER_COLUMN));

		return trade;
	}

}
