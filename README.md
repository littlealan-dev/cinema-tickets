# Cinema Tickets Code Test

This repository contains Java implementations of a cinema ticket purchasing system, a coding exercise for the job application of Java Software Engineer at DWP. The implementation demonstrates core logic for handling ticket purchases, payment processing, and seat reservations.

## Table of Contents
- [Project Structure](#project-structure)
- [Java Implementation](#java-implementation)
- [Testing](#testing)
- [License](#license)

## Project Structure
```
cinema-tickets-java/        # Java implementation (Maven project)

```

---

## Java Implementation

- **Location:** `cinema-tickets-java/`
- **Build Tool:** Maven

### Summary of Changes
The Java implementation provides a robust ticket purchasing service with:
- Input validation for account ID and ticket requests
- Enforcement of business rules (e.g., at least one adult per purchase, infants cannot exceed adults, max 25 tickets per purchase)
- Price calculation for different ticket types (adult, child, infant)
- Integration with payment and seat reservation services (mocked for testing)
- Clean separation of validation and calculation logic in utilities class for reusability.
- Separated error messages to message.properties file for easy internationalization
- Externalize the maximum ticket purchase limit to application.properties for changing the value without modifying code.
- Comprehensive unit and integration tests covering:
	- Account validation
	- Adult ticket requirements
	- Price calculation
	- Seat reservation logic
	- Ticket count and request validation


### Build
```bash
cd cinema-tickets-java
mvn clean install
```

### Run Tests
```bash
mvn test
```

Test results are available in `cinema-tickets-java/target/surefire-reports/`.


---

## License

---

## Author
Alan Chan (Application ID: 15375365)

---

## Notes
- For more details, see the source code in each respective folder.
