/***************************************************************************
 * Copyright 2013 DFG SPP 1593 (http://dfg-spp1593.de)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 ***************************************************************************/

package org.cocome.tradingsystem.cashdeskline.cashdesk;

import java.io.Serializable;
import java.rmi.RemoteException;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Set;

import javax.jms.Connection;
import javax.jms.JMSException;
import javax.jms.MessageListener;
import javax.jms.Session;

import org.apache.log4j.Logger;
import org.cocome.tradingsystem.cashdeskline.coordinator.ExpressModePolicy;
import org.cocome.tradingsystem.cashdeskline.datatypes.PaymentMode;
import org.cocome.tradingsystem.cashdeskline.events.AccountSaleEvent;
import org.cocome.tradingsystem.cashdeskline.events.ChangeAmountCalculatedEvent;
import org.cocome.tradingsystem.cashdeskline.events.ExpressModeDisabledEvent;
import org.cocome.tradingsystem.cashdeskline.events.ExpressModeEnabledEvent;
import org.cocome.tradingsystem.cashdeskline.events.InvalidCreditCardEvent;
import org.cocome.tradingsystem.cashdeskline.events.InvalidProductBarcodeEvent;
import org.cocome.tradingsystem.cashdeskline.events.PaymentModeRejectedEvent;
import org.cocome.tradingsystem.cashdeskline.events.PaymentModeSelectedEvent;
import org.cocome.tradingsystem.cashdeskline.events.RunningTotalChangedEvent;
import org.cocome.tradingsystem.cashdeskline.events.SaleFinishedEvent;
import org.cocome.tradingsystem.cashdeskline.events.SaleRegisteredEvent;
import org.cocome.tradingsystem.cashdeskline.events.SaleStartedEvent;
import org.cocome.tradingsystem.cashdeskline.events.SaleSuccessEvent;
import org.cocome.tradingsystem.external.DebitResult;
import org.cocome.tradingsystem.external.IBank;
import org.cocome.tradingsystem.external.TransactionID;
import org.cocome.tradingsystem.inventory.application.store.IStoreInventory;
import org.cocome.tradingsystem.inventory.application.store.NoSuchProductException;
import org.cocome.tradingsystem.inventory.application.store.ProductWithStockItemTO;
import org.cocome.tradingsystem.inventory.application.store.SaleTO;
import org.cocome.tradingsystem.util.ApplicationHelper;
import org.cocome.tradingsystem.util.ComponentNotAvailableException;
import org.cocome.tradingsystem.util.JmsHelper;
import org.cocome.tradingsystem.util.JmsHelper.SessionBoundProducer;
import org.cocome.tradingsystem.util.Names;
import org.cocome.tradingsystem.util.RemoteComponent;
import org.cocome.tradingsystem.util.event.ObjectMessageListener;
import org.cocome.tradingsystem.util.java.Lists;
import org.cocome.tradingsystem.util.java.Sets;
import org.cocome.tradingsystem.util.mvc.AbstractModel;

/**
 * Implements the cash desk model. The model provides methods to process a sale,
 * which will be typically triggered by events from other cash desk components.
 * The methods that can be called depend on the state of the cash desk.
 * 
 * @author Yannick Welsch
 * @author Lubomir Bulej
 */
public final class CashDeskModel
		extends AbstractModel<CashDeskModel> {

	private static final Logger LOG =
			Logger.getLogger(CashDeskModel.class);

	private static final String COMPONENT_NAME = "Cash Desk";

	private static final String INVALID_CARD_INFO = "XXXX XXXX XXXX XXXX";

	/**
	 * New sale can be started (and thus current sale aborted) in almost
	 * all cash desk states except when already paid by cash.
	 */
	private static final Set<CashDeskState> START_SALE_STATES = CashDeskModel.setOfStates(
			CashDeskState.EXPECTING_SALE,
			CashDeskState.EXPECTING_ITEMS,
			CashDeskState.EXPECTING_PAYMENT,
			CashDeskState.PAYING_BY_CASH,
			CashDeskState.EXPECTING_CARD_INFO,
			CashDeskState.PAYING_BY_CREDIT_CARD);

	/**
	 * Items can be only added to the sale after it has been started and
	 * has not been indicated by the cashier as finished.
	 */
	private static final Set<CashDeskState> ADD_ITEM_TO_SALE_STATES =
			CashDeskModel.setOfStates(CashDeskState.EXPECTING_ITEMS);

	/**
	 * Sale can be only finished when scanning items.
	 */
	private static final Set<CashDeskState> FINISH_SALES_STATES =
			CashDeskModel.setOfStates(CashDeskState.EXPECTING_ITEMS);

	/**
	 * Payment mode can be selected either when a sale has been finished or
	 * when switching from credit card payment to cash.
	 */
	private static final Set<CashDeskState> SELECT_PAYMENT_MODE_STATES =
			CashDeskModel.setOfStates(CashDeskState.EXPECTING_PAYMENT, CashDeskState.EXPECTING_CARD_INFO, CashDeskState.PAYING_BY_CREDIT_CARD);

	/**
	 * Cash payment can only proceed when the cashier selected the cash
	 * payment mode.
	 */
	private static final Set<CashDeskState> START_CASH_PAYMENT_STATES =
			CashDeskModel.setOfStates(CashDeskState.PAYING_BY_CASH);

	/**
	 * In cash payment mode, sale is finished when it has been paid for
	 * and the cash box has been closed.
	 */
	private static final Set<CashDeskState> FINISH_CASH_PAYMENT_STATES =
			CashDeskModel.setOfStates(CashDeskState.PAID_BY_CASH);

	/**
	 * Credit card payment can be made only when the cashier selected the credit
	 * card payment mode or when rescanning the card.
	 */
	private static final Set<CashDeskState> START_CREADIT_CARD_PAYMENT_STATES =
			CashDeskModel.setOfStates(CashDeskState.EXPECTING_CARD_INFO, CashDeskState.PAYING_BY_CREDIT_CARD);

	/**
	 * In credit card payment mode, sale is finished only when we have the
	 * credit card info.
	 */
	private static final Set<CashDeskState> FINISH_CREDIT_CARD_PAYMENT_STATES =
			CashDeskModel.setOfStates(CashDeskState.PAYING_BY_CREDIT_CARD);

	private CashDeskState state = CashDeskState.EXPECTING_SALE;

	private final ExpressModePolicy expressModePolicy = ExpressModePolicy.getInstance();

	private final String name;

	private final RemoteComponent<IBank> remoteBank;

	private final IStoreInventory inventory;

	private final SessionBoundProducer cashDeskProducer;

	private final SessionBoundProducer storeProducer;

	//
	// Cash desk state
	//

	private boolean expressModeEnabled = false;

	//
	// Sale state
	//

	private List<ProductWithStockItemTO> saleProducts;

	private double runningTotal;

	private String cardInfo;

	/**
	 * Creates a new instance of cash desk controller. The application naming
	 * must be initialized prior to creating a new instance.
	 * 
	 * @param cashDeskName
	 *            name of the cash desk
	 * @param remoteBank
	 *            name of the bank
	 * @param inventory
	 *            inventory handler
	 * @param cashDeskProducer
	 *            cash desk producer handler?
	 * @param storeProducer
	 *            store producer?
	 */
	public CashDeskModel(
			final String cashDeskName,
			final RemoteComponent<IBank> remoteBank,
			final IStoreInventory inventory,
			final SessionBoundProducer cashDeskProducer,
			final SessionBoundProducer storeProducer) {
		super(COMPONENT_NAME);

		this.name = cashDeskName;
		this.remoteBank = remoteBank;
		this.inventory = inventory;
		this.cashDeskProducer = cashDeskProducer;
		this.storeProducer = storeProducer;
	}

	public boolean isExpressModeEnabled() {
		return this.expressModeEnabled;
	}

	public void setExpressModeEnabled(final boolean expressModeEnabled) {
		this.expressModeEnabled = expressModeEnabled;
	}

	String getName() {
		return this.name;
	}

	CashDeskState getState() {
		return this.state;
	}

	boolean isInExpressMode() {
		return this.expressModeEnabled;
	}

	//
	// State mutator methods
	//

	/**
	 * TODO Someone tries to model an interface? Should always use public or private
	 * 
	 * @throws JMSException
	 */
	void startSale() throws JMSException {
		this.ensureStateIsLegal(START_SALE_STATES);
		this.sendSaleStartedEvent();
		this.state = CashDeskState.EXPECTING_ITEMS;
		this.resetSale();
	}

	/**
	 * 
	 */
	private void resetSale() {
		this.runningTotal = 0.0;
		this.saleProducts = Lists.newArrayList();
		this.cardInfo = INVALID_CARD_INFO;
	}

	private void sendSaleStartedEvent() throws JMSException {
		this.sendCashDeskEvent(new SaleStartedEvent());
	}

	/**
	 * Adds an item with the specified barcode into the running sale and updates
	 * the running total. Does nothing if there is no product with the specified
	 * barcode, or the number of items in an express sale exceeds the allowed
	 * limit.
	 * 
	 * @param barcode
	 */
	void addItemToSale(final long barcode) throws JMSException {
		this.ensureStateIsLegal(ADD_ITEM_TO_SALE_STATES);

		//
		// If we can accept more items into the sale, add the item to the sale,
		// otherwise complain of exceeding the express mode item count limit.
		//
		if (this.canAcceptNextItem()) {

			//
			// Using the barcode, query the store inventory product stock item
			// and add it to the sale. If there is no product with the given
			// barcode, notify the other cash desk components and ignore the
			// event. Any other failures will cause session rolled back.
			//
			try {
				final ProductWithStockItemTO productStockItem =
						this.inventory.getProductWithStockItem(barcode);
				this.addItemToSale(productStockItem);

			} catch (final NoSuchProductException nspe) {
				LOG.info("No product/stock item for barcode " + barcode);
				this.sendInvalidProductBarcodeEvent(barcode);

			} catch (final RemoteException re) {
				// TODO Consider notifying other components. --LB
				LOG.error("Failed to communicate with store inventory: " + re.getMessage());
			}

		} else {
			// TODO Consider notifying other components. --LB
			LOG.info(String.format(
					"Cannot process more than %d items in express mode!",
					this.expressModePolicy.getExpressItemLimit()
					));
		}
	}

	private boolean canAcceptNextItem() {
		final boolean expressModeDisabled = !this.expressModeEnabled;
		final boolean itemCountUnderLimit = this.saleProducts.size() < this.expressModePolicy.getExpressItemLimit();

		return expressModeDisabled || itemCountUnderLimit;
	}

	void sendInvalidProductBarcodeEvent(final long barcode) throws JMSException {
		this.sendCashDeskEvent(new InvalidProductBarcodeEvent(barcode));
	}

	private void addItemToSale(final ProductWithStockItemTO product)
			throws JMSException {
		//
		// Add the product item to the sale and update the running total.
		//
		this.saleProducts.add(product);

		final double price = product.getStockItemTO().getSalesPrice();
		this.runningTotal = this.computeNewRunningTotal(price);

		this.sendRunningTotalChangedEvent(product.getName(), price);
	}

	private double computeNewRunningTotal(final double price) {
		final double result = this.runningTotal + price;
		return Math.rint(100 * result) / 100;
	}

	private void sendRunningTotalChangedEvent(
			final String productName, final double salePrice
			) throws JMSException {
		this.sendCashDeskEvent(new RunningTotalChangedEvent(
				productName, salePrice, this.runningTotal));
	}

	/**
	 * 
	 * @throws JMSException
	 */
	void finishSale() throws JMSException {
		this.ensureStateIsLegal(FINISH_SALES_STATES);

		//
		// If there actually are any items in the sale, the sale can be finished
		// and the cashier will proceed to payment mode selection.
		//
		if (this.saleProducts.size() > 0) {
			this.sendSaleFinishedEvent();
			this.state = CashDeskState.EXPECTING_PAYMENT;
		}
	}

	/**
	 * 
	 * @throws JMSException
	 */
	private void sendSaleFinishedEvent() throws JMSException {
		this.sendCashDeskEvent(new SaleFinishedEvent());
	}

	void selectPaymentMode(final PaymentMode mode) throws JMSException {
		this.ensureStateIsLegal(SELECT_PAYMENT_MODE_STATES);
		//
		// Cash payment can be selected whenever payment mode selection is
		// legal (i.e. even in the middle of credit card payment, in case
		// it fails).
		//
		if (mode == PaymentMode.CASH) {
			this.sendPaymentModeSelectedEvent(mode);
			this.state = CashDeskState.PAYING_BY_CASH;

		} else if (mode == PaymentMode.CREDIT_CARD) {
			//
			// Credit card payment can be only selected when the
			// cash desk is in normal (not express) mode.
			//
			if (!this.expressModeEnabled) {
				this.sendPaymentModeSelectedEvent(mode);
				this.state = CashDeskState.EXPECTING_CARD_INFO;

			} else {
				final String message = "Credit cards not accepted in express mode";
				this.sendPaymentModeRejectedEvent(mode, message);
				LOG.info(message);
			}

		} else {
			final String message = "Unknown payment mode: " + mode;
			this.sendPaymentModeRejectedEvent(mode, message);
			LOG.error(message);
		}
	}

	/**
	 * 
	 * @param mode
	 * @throws JMSException
	 */
	private void sendPaymentModeSelectedEvent(final PaymentMode mode) throws JMSException {
		this.sendCashDeskEvent(new PaymentModeSelectedEvent(mode));
	}

	/**
	 * 
	 * @param mode
	 * @param reason
	 * @throws JMSException
	 */
	private void sendPaymentModeRejectedEvent(
			final PaymentMode mode, final String reason
			) throws JMSException {
		this.sendCashDeskEvent(new PaymentModeRejectedEvent(mode, reason));
	}

	/**
	 * 
	 * @param amount
	 * @throws JMSException
	 */
	void startCashPayment(final double amount) throws JMSException {
		this.ensureStateIsLegal(START_CASH_PAYMENT_STATES);
		//
		// Calculate the change to be given back. Negative change amount means
		// that the paid amount was insufficient.
		//
		final double change = this.computeChangeAmount(amount);
		if (Math.signum(change) >= 0) {
			this.sendChangeAmountCalculatedEvent(change);
			this.state = CashDeskState.PAID_BY_CASH;

		} else {
			LOG.error(String.format(
					"Insufficient cash (%f) for the sale total (%f)!",
					amount, this.runningTotal
					));
		}
	}

	/**
	 * 
	 * @param amount
	 * @return
	 */
	private double computeChangeAmount(final double amount) {
		// Calculate and round change to return.
		final double changeAmount = amount - this.runningTotal;
		return Math.rint(100 * changeAmount) / 100;
	}

	/**
	 * 
	 * @param amount
	 * @throws JMSException
	 */
	private void sendChangeAmountCalculatedEvent(final double amount) throws JMSException {
		this.sendCashDeskEvent(new ChangeAmountCalculatedEvent(amount));
	}

	void finishCashPayment() throws JMSException {
		this.ensureStateIsLegal(FINISH_CASH_PAYMENT_STATES);

		//

		this.makeSale(PaymentMode.CASH);
		this.state = CashDeskState.EXPECTING_SALE;
	}

	/**
	 * 
	 * @param cardInfo
	 */
	void startCreditCardPayment(final String cardInfo) { // NOCS
		this.ensureStateIsLegal(START_CREADIT_CARD_PAYMENT_STATES);

		this.cardInfo = cardInfo;
		this.state = CashDeskState.PAYING_BY_CREDIT_CARD;
	}

	/**
	 * 
	 * @param pin
	 * @throws JMSException
	 */
	void finishCreditCardPayment(final int pin)
			throws JMSException {
		this.ensureStateIsLegal(FINISH_CREDIT_CARD_PAYMENT_STATES);

		//

		try {
			this.finishCreditCardPayment(pin, this.remoteBank.get());

		} catch (final ComponentNotAvailableException cnae) {
			LOG.error("Could not connect to the bank: " + cnae.getMessage());
		} catch (final RemoteException re) {
			LOG.error("Failed to communicate with the bank: " + re.getMessage());
		}
	}

	/**
	 * 
	 * @param pin
	 * @param bank
	 * @throws JMSException
	 * @throws RemoteException
	 */
	void finishCreditCardPayment(final int pin, final IBank bank)
			throws JMSException, RemoteException {
		//
		// Validate the card. If the card is invalid, notify other components
		// about it and ignore the request.
		//
		final TransactionID creditCardTid = bank.validateCard(this.cardInfo, pin);
		if (creditCardTid == null) {
			this.sendInvalidCreditCardEvent(this.cardInfo);
			return;
		}

		//
		// Charge the card.
		//
		final DebitResult debitResult = bank.debitCard(creditCardTid);
		if (debitResult == DebitResult.OK) {
			//
			// If the card has been charged, account the finished sale and
			// prepare for the next sale.
			//
			this.makeSale(PaymentMode.CREDIT_CARD);
			this.state = CashDeskState.EXPECTING_SALE;

		} else if (debitResult == DebitResult.INVALID_TRANSACTION_ID) {
			//
			// If the transaction ID is invalid, notify other components and
			// restart the card payment process.
			//
			// TODO Use different event for invalid transaction id.
			//
			LOG.info("Invalid transaction ID, rescan card.");
			this.sendInvalidCreditCardEvent(this.cardInfo);
			this.state = CashDeskState.EXPECTING_CARD_INFO;

		} else if (debitResult == DebitResult.INSUFFICIENT_BALANCE) {
			//
			// If there are not enough money, notify other components and
			// restart the card payment process.
			//
			// TODO Use different event for insufficient money..
			// TODO Display should show "not enough money"
			//
			LOG.info("Not enough money on the account");
			this.sendInvalidCreditCardEvent(this.cardInfo);
			this.state = CashDeskState.EXPECTING_CARD_INFO;

		} else {
			LOG.error("Unexpected debit result " + debitResult);
		}
	}

	/**
	 * 
	 * @param cardInfo
	 * @throws JMSException
	 */
	void sendInvalidCreditCardEvent(final String cardInfo) throws JMSException { // NOCS
		this.sendCashDeskEvent(new InvalidCreditCardEvent(cardInfo));
	}

	//

	private void makeSale(final PaymentMode mode) throws JMSException {
		final SaleTO saleTO = this.createSaleTO();

		//
		// Request the store inventory system to account for the sale.
		// This uses JMS so that the notification can be asynchronous
		// and the message persisted.
		//
		this.sendAccountSaleEvent(saleTO);

		//
		// Notify cash desk components that sale has been successful.
		//
		this.sendSaleSuccessEvent();

		//
		// Notify the coordinator about the sale, providing basic statistics.
		//
		this.sendSaleRegisteredEvent(saleTO.getProductTOs().size(), mode);
	}

	private SaleTO createSaleTO() {
		final SaleTO saleTO = new SaleTO();
		saleTO.setDate(new Date());
		saleTO.setProductTOs(this.saleProducts);
		return saleTO;
	}

	private void sendAccountSaleEvent(final SaleTO saleTO) throws JMSException {
		this.sendStoreEvent(new AccountSaleEvent(saleTO));
	}

	private void sendSaleSuccessEvent() throws JMSException {
		this.sendCashDeskEvent(new SaleSuccessEvent());
	}

	private void sendSaleRegisteredEvent(
			final int itemCount, final PaymentMode mode
			) throws JMSException {
		this.sendStoreEvent(new SaleRegisteredEvent(this.name, itemCount, mode));
	}

	//
	// Cash desk can be switched to express mode in all states.
	//
	void enableExpressMode() throws JMSException {
		if (!this.expressModeEnabled) {
			this.expressModeEnabled = true;
			this.sendExpressModeEnabledEvent(this.name);
		}
	}

	private void sendExpressModeEnabledEvent(final String cashDeskName) throws JMSException {
		this.sendCashDeskEvent(new ExpressModeEnabledEvent(cashDeskName));
	}

	//
	// The express mode can be disabled in all states.
	//
	void disableExpressMode() throws JMSException {
		if (this.expressModeEnabled) {
			this.expressModeEnabled = false;
			this.sendExpressModeDisabledEvent();
		}
	}

	private void sendExpressModeDisabledEvent() throws JMSException {
		this.sendCashDeskEvent(new ExpressModeDisabledEvent());
	}

	//

	private void sendCashDeskEvent(final Serializable eventObject) throws JMSException {
		this.cashDeskProducer.send(eventObject);
	}

	private void sendStoreEvent(final Serializable eventObject) throws JMSException {
		this.storeProducer.send(eventObject);
	}

	//

	private void ensureStateIsLegal(final Set<CashDeskState> legalStates) {
		final CashDeskState currentState = this.state;
		if (!legalStates.contains(currentState)) {
			throw new IllegalCashDeskStateException(this.state, legalStates);
		}
	}

	//

	/**
	 * Factory method to create a node cash desk model.
	 * 
	 * @param cashDeskName
	 *            name of the cash desk
	 * @param storeName
	 *            store name
	 * @param bankName
	 *            bank name
	 * @param connection
	 *            connection model
	 * @return returns a new cash desk model
	 */
	public static CashDeskModel newInstance(
			final String cashDeskName, final String storeName,
			final String bankName, final Connection connection
			) {
		try {
			//
			// Connect to the store inventory system (via RMI).
			//
			final IStoreInventory inventory = ApplicationHelper.getComponent(
					Names.getStoreRemoteName(storeName), IStoreInventory.class
					);

			//
			// Create transacted session and event publishers.
			//
			final Session session = JmsHelper.createTransactedSession(connection);

			final String cashDeskTopicName =
					Names.getCashDeskTopicName(cashDeskName, storeName);
			final SessionBoundProducer cashDeskProducer =
					JmsHelper.createSessionBoundProducer(session, cashDeskTopicName);

			final String storeTopicName =
					Names.getStoreTopicName(storeName);
			final SessionBoundProducer storeProducer =
					JmsHelper.createSessionBoundProducer(session, storeTopicName);

			//
			// Create the cash desk controller and a message listener and register
			// it as receiver for the cash desk and store topics.
			//
			final RemoteComponent<IBank> remoteBank = ApplicationHelper.getRemoteComponent(
					Names.getBankRemoteName(bankName), IBank.class
					);

			final CashDeskModel cashDesk = new CashDeskModel(
					cashDeskName, remoteBank, inventory,
					cashDeskProducer, storeProducer
					);

			final MessageListener listener = new ObjectMessageListener(
					new CashDeskEventHandler(cashDesk, session)
					);

			JmsHelper.registerConsumer(session, cashDeskTopicName, listener);
			JmsHelper.registerConsumer(session, storeTopicName, listener);

			return cashDesk;

			// TODO Catch all exceptions are a style violation
		} catch (final Exception e) { // NOCS
			final String message = String.format(
					"Failed to initialize %s (%s, %s)",
					COMPONENT_NAME, cashDeskName, storeName
					);

			LOG.fatal(message, e);
			throw new RuntimeException(message, e);
		}
	}

	/**
	 * State collection factory.
	 * 
	 * @param elements
	 *            number of states
	 * @return return an enum value set
	 */
	private static <E extends Enum<E>> Set<E> setOfStates(final E... elements) {
		return Collections.unmodifiableSet(Sets.newEnumSet(elements));
	}

}
