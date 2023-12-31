/**
 * 
 */
package com.excelsiorsoft.globalpayments.feeds.downstream.dailycashreport.acquire.provider;

import lombok.extern.slf4j.Slf4j;

/**
 * A wrapper type for an item that is used by {@link AggregateItemReader} to identify the
 * start and end of an aggregate record.
 *
 * @see AggregateItemReader
 * @author Dave Syer
 * @author Mahmoud Ben Hassine
 *
 */
@Slf4j
public class AggregateItem<T> {

	@SuppressWarnings("rawtypes")
	private static final AggregateItem FOOTER = new AggregateItem(false, true) {
		@Override
		public Object getItem() {
			throw new IllegalStateException("Footer record has no item.");
		}
	};

	/**
	 * @param <T> the type of item nominally wrapped
	 * @return a static {@link AggregateItem} that is a footer.
	 */
	@SuppressWarnings("unchecked")
	public static <T> AggregateItem<T> getFooter() {
		return FOOTER;
	}

	@SuppressWarnings("rawtypes")
	private static final AggregateItem HEADER = new AggregateItem(true, false) {
		@Override
		public Object getItem() {
			throw new IllegalStateException("Header record has no item.");
		}
	};

	/**
	 * @param <T> the type of item nominally wrapped
	 * @return a static {@link AggregateItem} that is a header.
	 */
	@SuppressWarnings("unchecked")
	public static <T> AggregateItem<T> getHeader() {
		return HEADER;
	}

	private final T item;

	private boolean footer = false;

	private boolean header = false;

	/**
	 * @param item the item to wrap
	 */
	public AggregateItem(T item) {
		super();
		this.item = item;
		log.info("Item = {}", item);
	}

	public AggregateItem(boolean header, boolean footer) {
		this(null);
		this.header = header;
		this.footer = footer;
	}

	/**
	 * Accessor for the wrapped item.
	 * @return the wrapped item
	 * @throws IllegalStateException if called on a record for which either
	 * {@link #isHeader()} or {@link #isFooter()} answers true.
	 */
	public T getItem() {
		return item;
	}

	/**
	 * Responds true if this record is a footer in an aggregate.
	 * @return true if this is the end of an aggregate record.
	 */
	public boolean isFooter() {
		return footer;
	}

	/**
	 * Responds true if this record is a header in an aggregate.
	 * @return true if this is the beginning of an aggregate record.
	 */
	public boolean isHeader() {
		return header;
	}

}
