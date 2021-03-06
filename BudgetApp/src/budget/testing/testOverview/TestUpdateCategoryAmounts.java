package budget.testing.testOverview;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.Before;
import org.junit.jupiter.api.Test;

import budget.model.Budget;
import budget.model.OverView;

class TestUpdateCategoryAmounts {

	private OverView overview;
	private Budget budget;

	@Before
	public void setup() {
		overview = new OverView("rachetl");
		budget = new Budget("groceries", 200, 100);
		overview.addBudget(budget);
		overview.setCurrentBudget(0);

		overview.addNewCategory("Category1", 50, 0);


	}


	@Test
	void testUpdatingTheCategoryAndSeeingIfItUpdatesCorrectly() {
		setup();

		overview.updateCategoryAmounts("Category1", 40.0);
		// assertEquals(2,overview.getCategories());
		assertEquals(60.0, overview.getUnallocatedBalanceProperty().doubleValue());
	}

	@Test
	void testUpdatingTheCategoryAndShouldBePossibleToBeNegative() {
		setup();
		overview.updateCategoryAmounts("Category1", 105.0);
		assertEquals(-5.0, overview.getUnallocatedBalanceProperty().doubleValue());
	}
	

}
