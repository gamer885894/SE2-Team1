package budget.testing.testInflow;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.LocalDateTime;

import org.junit.jupiter.api.Test;
																						
import budget.model.Inflow;
import budget.model.Transaction;
import javafx.beans.property.StringProperty;
import javafx.beans.property.SimpleStringProperty;

public class TestConstructor {

	@Test
	void testValidConstructor() {
		LocalDateTime currTime = LocalDateTime.now();
		Transaction Inflow = new Inflow(50, currTime, "bills");
		StringProperty result = new SimpleStringProperty("Inflow");
		

		assertEquals("bills", Inflow.getTitle().get().toString());
		assertTrue(currTime.equals(Inflow.getDate().getValue()));
		assertEquals(50, Inflow.getAmount().intValue());
		assertEquals(result.toString(), Inflow.getType().toString());

	}

	@Test
	void testInvalidAmount() {

		LocalDateTime currTime = LocalDateTime.now();

		IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
				() -> new Inflow(-55, currTime, "bills"));
		assertEquals("Amount must be greater than zero", exception.getMessage());

	}

	@Test
	void testInvalidDate() {

		IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
				() -> new Inflow(55, null, "bills"));
		assertEquals("Date cannot be null", exception.getMessage());

	}

	@Test
	void testNullTitle() {
		LocalDateTime currTime = LocalDateTime.now();

		IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
				() -> new Inflow(55, currTime, null));
		assertEquals("Invalid name", exception.getMessage());

	}

	@Test
	void testEmptyTitle() {
		LocalDateTime currTime = LocalDateTime.now();

		IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
				() -> new Inflow(55, currTime, ""));
		assertEquals("Invalid name", exception.getMessage());

	}

	@Test
	void testInvalidTitleLength() {
		LocalDateTime currTime = LocalDateTime.now();

		IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
				() -> new Inflow(-55, currTime, ""));
		assertEquals("Amount must be greater than zero", exception.getMessage());

	}

}
