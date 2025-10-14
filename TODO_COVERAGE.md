# Unit Test Coverage TODO List

## Target: ≥95% Line Coverage, ≥85% Branch Coverage

### Current Baseline: 2% Line Coverage

---

## High Priority - Service Layer (Critical Business Logic)

### ✅ BankingServiceHelper.java (86/87 lines uncovered)
- [ ] Test `convertToCustomerDomain()` - entity to DTO conversion
- [ ] Test `convertToCustomerEntity()` - DTO to entity conversion
- [ ] Test `convertToAccountDomain()` - account entity to DTO
- [ ] Test `convertToAccountEntity()` - account DTO to entity
- [ ] Test `convertToAddressDomain()` - address entity to DTO
- [ ] Test `convertToAddressEntity()` - address DTO to entity
- [ ] Test `convertToContactDomain()` - contact entity to DTO
- [ ] Test `convertToContactEntity()` - contact DTO to entity
- [ ] Test `convertToBankInfoDomain()` - bank info entity to DTO
- [ ] Test `convertToBankInfoEntity()` - bank info DTO to entity
- [ ] Test `convertToTransactionDomain()` - transaction entity to DTO
- [ ] Test `convertToTransactionEntity()` - transaction DTO to entity
- [ ] Test `createTransaction()` - transaction creation from transfer details

### ✅ BankingServiceImpl.java (101/104 lines uncovered)
- [ ] Test `findAll()` - retrieve all customers with conversion
- [ ] Test `addCustomer()` - create new customer, verify save and response
- [ ] Test `findByCustomerNumber()` - find customer by number (exists and not exists)
- [ ] Test `updateCustomer()` - update existing customer details
  - [ ] Test with existing contact details
  - [ ] Test with null contact, creating new
  - [ ] Test with existing address
  - [ ] Test with null address, creating new
  - [ ] Test with non-existent customer (404 response)
- [ ] Test `deleteCustomer()` - delete existing customer (success and not found)
- [ ] Test `findByAccountNumber()` - find account (found and not found)
- [ ] Test `addNewAccount()` - create account for existing customer
- [ ] Test `transferDetails()` - fund transfer between accounts
  - [ ] Test successful transfer with sufficient funds
  - [ ] Test insufficient funds (400 response)
  - [ ] Test non-existent from account (404 response)
  - [ ] Test non-existent to account (404 response)
  - [ ] Test non-existent customer (404 response)
  - [ ] Test transaction creation for debit and credit
- [ ] Test `findTransactionsByAccountNumber()` - retrieve transactions
  - [ ] Test with existing account and transactions
  - [ ] Test with non-existent account
  - [ ] Test with account but no transactions

---

## Medium Priority - Controller Layer (Delegation & Integration)

### ✅ AccountController.java (9/11 lines uncovered)
- [ ] Test `getByAccountNumber()` - delegates to service, verify response
- [ ] Test `addNewAccount()` - delegates to service with path variable and body
- [ ] Test `transferDetails()` - delegates to service for fund transfer
- [ ] Test `getTransactionByAccountNumber()` - delegates to service for transactions

### ✅ CustomerController.java (Similar coverage gap)
- [ ] Test `getAllCustomers()` - delegates to service, returns list
- [ ] Test `addCustomer()` - delegates to service with request body
- [ ] Test `getCustomer()` - delegates to service with path variable
- [ ] Test `updateCustomer()` - delegates to service with body and path variable
- [ ] Test `deleteCustomer()` - delegates to service with path variable

---

## Testing Strategy

### BankingServiceHelper Tests (Pure Unit Tests)
- **Approach**: Simple object mapping tests
- **Dependencies**: None (pure functions)
- **Mocking**: Not required
- **Patterns**: Test null handling, verify all fields mapped correctly

### BankingServiceImpl Tests (Mock-Heavy Unit Tests)
- **Approach**: Mock all repository dependencies
- **Dependencies**: Mock CustomerRepository, AccountRepository, TransactionRepository, CustomerAccountXRefRepository, BankingServiceHelper
- **Mocking**: Use Mockito to mock repositories and verify interactions
- **Patterns**: 
  - Use `@ExtendWith(MockitoExtension.class)`
  - Mock repositories with `@Mock`
  - Inject mocks with `@InjectMocks`
  - Verify repository calls with `verify()`
  - Test edge cases: null, empty, not found scenarios
  - Test business logic: insufficient funds, valid updates, etc.

### Controller Tests (Integration-Style Unit Tests)
- **Approach**: Use MockMvc or mock service layer
- **Dependencies**: Mock BankingServiceImpl
- **Mocking**: Mock the service, verify delegation
- **Patterns**:
  - Use `@WebMvcTest` for focused controller tests
  - Mock BankingService
  - Verify HTTP status codes and response bodies
  - Test path variables and request bodies parsing

---

## Estimated Coverage Impact

- **BankingServiceHelper**: ~87 lines → +25% coverage
- **BankingServiceImpl**: ~101 lines → +29% coverage  
- **AccountController**: ~9 lines → +3% coverage
- **CustomerController**: ~11 lines → +3% coverage

**Total Expected**: ~60% → Well above 95% target with proper test coverage

---

## Next Steps

1. Create test file structure mirroring main package structure
2. Start with BankingServiceHelper (easiest, pure functions)
3. Move to BankingServiceImpl (core business logic)
4. Complete with Controller tests (delegation layer)
5. Run `mvn test` after each test class to verify coverage increases
6. Final verification with `mvn verify` to ensure 95% threshold met
