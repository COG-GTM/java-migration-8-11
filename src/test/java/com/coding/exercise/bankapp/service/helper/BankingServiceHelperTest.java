package com.coding.exercise.bankapp.service.helper;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import java.util.Date;

import org.junit.jupiter.api.Test;

import com.coding.exercise.bankapp.domain.AccountInformation;
import com.coding.exercise.bankapp.domain.AddressDetails;
import com.coding.exercise.bankapp.domain.BankInformation;
import com.coding.exercise.bankapp.domain.ContactDetails;
import com.coding.exercise.bankapp.domain.CustomerDetails;
import com.coding.exercise.bankapp.domain.TransactionDetails;
import com.coding.exercise.bankapp.domain.TransferDetails;
import com.coding.exercise.bankapp.model.Account;
import com.coding.exercise.bankapp.model.Address;
import com.coding.exercise.bankapp.model.BankInfo;
import com.coding.exercise.bankapp.model.Contact;
import com.coding.exercise.bankapp.model.Customer;
import com.coding.exercise.bankapp.model.Transaction;

public class BankingServiceHelperTest {

	private final BankingServiceHelper helper = new BankingServiceHelper();

	private Address addressEntity() {
		return Address.builder().address1("1 Main St").address2("Apt 2").city("Austin").state("TX").zip("78701")
				.country("US").build();
	}

	private AddressDetails addressDetails() {
		return AddressDetails.builder().address1("1 Main St").address2("Apt 2").city("Austin").state("TX").zip("78701")
				.country("US").build();
	}

	@Test
	public void testConvertToCustomerDomain() {
		Customer customer = Customer.builder().customerNumber(1001L).firstName("Jane").middleName("Q").lastName("Doe")
				.status("ACTIVE")
				.contactDetails(Contact.builder().emailId("jane@example.com").homePhone("111").workPhone("222").build())
				.customerAddress(addressEntity()).build();

		CustomerDetails details = helper.convertToCustomerDomain(customer);

		assertEquals(Long.valueOf(1001L), details.getCustomerNumber());
		assertEquals("Jane", details.getFirstName());
		assertEquals("jane@example.com", details.getContactDetails().getEmailId());
		assertEquals("Austin", details.getCustomerAddress().getCity());
	}

	@Test
	public void testConvertToCustomerEntity() {
		CustomerDetails details = CustomerDetails.builder().customerNumber(1001L).firstName("Jane").middleName("Q")
				.lastName("Doe").status("ACTIVE")
				.contactDetails(ContactDetails.builder().emailId("jane@example.com").homePhone("111").workPhone("222").build())
				.customerAddress(addressDetails()).build();

		Customer customer = helper.convertToCustomerEntity(details);

		assertEquals(Long.valueOf(1001L), customer.getCustomerNumber());
		assertEquals("Doe", customer.getLastName());
		assertEquals("222", customer.getContactDetails().getWorkPhone());
		assertEquals("US", customer.getCustomerAddress().getCountry());
	}

	@Test
	public void testConvertToAccountDomain() {
		Account account = Account.builder().accountNumber(5001L).accountBalance(250.0).accountType("SAVINGS")
				.accountStatus("ACTIVE")
				.bankInformation(BankInfo.builder().branchCode(12).branchName("Main").routingNumber(123456789)
						.branchAddress(addressEntity()).build())
				.build();

		AccountInformation info = helper.convertToAccountDomain(account);

		assertEquals(Long.valueOf(5001L), info.getAccountNumber());
		assertEquals(Double.valueOf(250.0), info.getAccountBalance());
		assertEquals("Main", info.getBankInformation().getBranchName());
		assertEquals("Austin", info.getBankInformation().getBranchAddress().getCity());
	}

	@Test
	public void testConvertToAccountEntity() {
		AccountInformation info = AccountInformation.builder().accountNumber(5001L).accountBalance(250.0)
				.accountType("SAVINGS").accountStatus("ACTIVE")
				.bankInformation(BankInformation.builder().branchCode(12).branchName("Main").routingNumber(123456789)
						.branchAddress(addressDetails()).build())
				.build();

		Account account = helper.convertToAccountEntity(info);

		assertEquals(Long.valueOf(5001L), account.getAccountNumber());
		assertEquals("SAVINGS", account.getAccountType());
		assertEquals(Integer.valueOf(123456789), account.getBankInformation().getRoutingNumber());
	}

	@Test
	public void testConvertToAddressDomainAndEntity() {
		AddressDetails details = helper.convertToAddressDomain(addressEntity());
		assertEquals("1 Main St", details.getAddress1());
		assertEquals("78701", details.getZip());

		Address address = helper.convertToAddressEntity(details);
		assertEquals("Apt 2", address.getAddress2());
		assertEquals("TX", address.getState());
	}

	@Test
	public void testConvertToContactDomainAndEntity() {
		ContactDetails details = helper
				.convertToContactDomain(Contact.builder().emailId("a@b.c").homePhone("1").workPhone("2").build());
		assertEquals("a@b.c", details.getEmailId());

		Contact contact = helper.convertToContactEntity(details);
		assertEquals("1", contact.getHomePhone());
		assertEquals("2", contact.getWorkPhone());
	}

	@Test
	public void testConvertToTransactionDomainAndEntity() {
		Date now = new Date();
		Transaction transaction = Transaction.builder().accountNumber(5001L).txAmount(75.0).txType("CREDIT")
				.txDateTime(now).build();

		TransactionDetails details = helper.convertToTransactionDomain(transaction);
		assertEquals(Long.valueOf(5001L), details.getAccountNumber());
		assertEquals(Double.valueOf(75.0), details.getTxAmount());
		assertEquals("CREDIT", details.getTxType());
		assertEquals(now, details.getTxDateTime());

		Transaction roundTrip = helper.convertToTransactionEntity(details);
		assertEquals(Long.valueOf(5001L), roundTrip.getAccountNumber());
		assertEquals("CREDIT", roundTrip.getTxType());
	}

	@Test
	public void testCreateTransaction() {
		TransferDetails transferDetails = new TransferDetails(5001L, 5002L, 60.0);

		Transaction transaction = helper.createTransaction(transferDetails, 5001L, "DEBIT");

		assertEquals(Long.valueOf(5001L), transaction.getAccountNumber());
		assertEquals(Double.valueOf(60.0), transaction.getTxAmount());
		assertEquals("DEBIT", transaction.getTxType());
		assertNotNull(transaction.getTxDateTime());
	}
}
