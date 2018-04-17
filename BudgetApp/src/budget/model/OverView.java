/**
 * 
 */
package budget.model;

import java.time.LocalDateTime;
import java.util.ArrayList;

import budget.io.ImportServerData;
import javafx.beans.binding.DoubleBinding;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

/**
 * Defines Overview Class
 * 
 * @author Software Engineering Group 1
 * @version 1
 * 
 */
public class OverView {

	private ObservableList<Transaction> currentTransactions;
	private ObservableList<Category> currentCategories;
	private DoubleProperty overallBalanceLabel;
	private DoubleProperty unallocatedBalanceLabel;
	private StringProperty name;

	private ObservableList<Budget> budgets;

	private Budget currentBudget;

	private String currentUser;

	/**
	 * Constructor
	 * 
	 * @precondition none
	 * @postcondition none
	 * 
	 * @param overallBalanace
	 */
	public OverView(String username) {
		this.currentCategories = FXCollections.observableArrayList();
		this.currentTransactions = FXCollections.observableArrayList();
		this.budgets = FXCollections.observableArrayList();
		this.unallocatedBalanceLabel = new SimpleDoubleProperty();
		this.overallBalanceLabel = new SimpleDoubleProperty();
		this.name = new SimpleStringProperty();

		this.currentUser = username;

		this.loadUser();

		
		if (this.budgets.size() > 0) {
			this.setCurrentBudget(0);
		}
	}

	private void loadUser() {
		ImportServerData serverImporter = new ImportServerData();
		this.budgets.addAll(serverImporter.pullFromServer(this.currentUser));
	}

	/**
	 * Sets the currently viewed budget to the budget at index in the budgets list
	 * 
	 * @precondition index >= 0 and index < budgets.size()
	 * @postcondition the current budget is the budget at index and the bound
	 *                properties are updated
	 * 
	 * @param index
	 *            The index of the budget
	 */
	public void setCurrentBudget(int index) {
		if (index < 0 || index >= this.budgets.size()) {
			throw new IllegalArgumentException("index given is out of bounds to update the current budget");
		}

		this.currentBudget = this.budgets.get(index);
		this.name.set(this.currentBudget.getName());

		this.currentCategories.clear();
		this.currentCategories.addAll(this.currentBudget.getCategories());

		this.currentTransactions.clear();
		this.currentTransactions.addAll(this.currentBudget.getTransactions());

		this.unallocatedBalanceLabel.bind(this.currentBudget.getUnallocatedAmount());
		this.overallBalanceLabel.bind(this.currentBudget.getOverallAmount());
	}

	/**
	 * Gets ArrayList of of categories
	 * 
	 * @precondition none
	 * @postcondition none
	 * 
	 * @return list of categories
	 */
	public ObservableList<Category> getCategories() {
		return this.currentCategories;
	}

	/**
	 * Get the list of transactions
	 * 
	 * @precondition none
	 * @postcondition none
	 * 
	 * @return the list of transactions
	 */
	public ObservableList<Transaction> getTransactions() {
		return this.currentTransactions;
	}

	/**
	 * Gets the budgets for the overview
	 * 
	 * @precondition None
	 * @return The list of budgets for the overview
	 */
	public ObservableList<Budget> getBudgets() {
		return this.budgets;
	}

	/**
	 * Gets the overallBalance property
	 * 
	 * @precondition none
	 * @postcondition none
	 * 
	 * @return overall balance
	 */
	public DoubleProperty getOverallBalanceProperty() {
		return this.overallBalanceLabel;
	}

	/**
	 * Gets the unallocated balance property
	 * 
	 * @precondition none
	 * @postcondition none
	 * 
	 * @return unallocated balance
	 */
	public DoubleProperty getUnallocatedBalanceProperty() {
		return this.unallocatedBalanceLabel;
	}

	/**
	 * Gets the category specified by name and returns it, otherwise returns null
	 * 
	 * @precondition None
	 * @postcondition none
	 * 
	 * @param name
	 *            name of the category
	 * @return the category if
	 */
	public Category getSpecificCategory(String name) {
		return this.currentBudget.getCategoryByName(name);
	}

	public void updateCategoryAmounts(String selectedCategoryName, double newAmount) {
//		System.out.println(unallocatedBalanceLabel.doubleValue());
		this.currentBudget.updateCategoryAllocatedAmount(selectedCategoryName, newAmount);
		
		
//		this.unallocatedBalanceLabel(this.unallocatedBalanceLabel.subtract(newAmount).doubleValue());
//		System.out.println(unallocatedBalanceLabel.doubleValue());
		
	}
	




	/**
	 * Adds a new category by specified name
	 * 
	 * @precondition name != null AND name != ""
	 * @postcondition none
	 * 
	 * @param name
	 *            name of the new category
	 */
	public void addNewCategory(String name, double AllocatedAmount, double SpentAmount) {
		Category newCat = new Category(name, AllocatedAmount, SpentAmount);
		this.currentBudget.addCategory(newCat);
		this.currentCategories.add(newCat);
	}

	public void addNewInflow(double amount, LocalDateTime date, String title) {
		Inflow inflow = new Inflow(amount, date, title);
		this.currentBudget.addInflow(inflow);
	}

	public void addNewOutflow(double amount, LocalDateTime date, String title, Category category) {
		this.currentBudget.addOutflow(new Outflow(amount, date, title, category), category.getName().get());
	}

	public void RemoveTransaction(Transaction toDelete) {
		this.currentTransactions.remove(toDelete);
		this.currentBudget.removeTransaction(toDelete);
	}

	public void RemoveCategory(Category toDelete) {
		this.currentCategories.remove(toDelete);
		this.currentBudget.deleteCategory(toDelete);
	}

	public void addBudget(Budget budget) {
		this.budgets.add(budget);
	}

	/**
	 * Updates the budget's name
	 * 
	 * @precondition The name cannot be null, empty, or contain commas
	 * @postcondition The budget's name is changed
	 * @param name
	 *            The new name for the budget
	 */
	public void setName(String name) {
		this.currentBudget.setName(name);
	}
	
	public Budget getCurrentBudget() {
		return this.currentBudget;
	}

}
