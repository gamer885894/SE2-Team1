/**
 * 
 */
package budget.model;

import java.time.LocalDateTime;
import java.util.ArrayList;

import budget.io.Exporter;
import budget.io.ImportServerData;
import budget.server.ServerAccess;
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
	}

	public OverView(String username, ArrayList<Budget> budgets) {
		this(username);
		this.budgets.addAll(budgets);
		if (this.budgets.size() > 1) {
			this.setCurrentBudget(0);
		}
	}

	public void loadUser() {
		ImportServerData serverImporter = new ImportServerData();
		this.budgets.clear();
		this.budgets.addAll(serverImporter.pullFromServer(this.currentUser));

		if (this.budgets.size() > 0) {
			this.setCurrentBudget(0);
		}
	}
	
	public boolean uploadBudget() {
		Exporter export = new Exporter();
		return export.ExportBudgetToServer(this.currentUser, this.currentBudget);
	}
	
	public boolean removeCurrentBudget() {
		boolean out = ServerAccess.deleteBudget(this.currentUser, this.currentBudget.getName());
		
		if(out) {
			this.loadUser();
		}
		
		return out;
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
	 * @precondition name != null & name != empty
	 * @postcondition none
	 * 
	 * @param name
	 *            name of the category
	 * @return the category if
	 */
	public Category getSpecificCategory(String name) {
		if (name == null) {
			throw new IllegalArgumentException("name cannot be null");
		}
		if (name.isEmpty()) {
			throw new IllegalArgumentException("Must Provide a Name");
		}
		return this.currentBudget.getCategoryByName(name);
	}

	public void updateCategoryAmounts(String selectedCategoryName, double newAmount) {

		this.currentBudget.updateCategoryAllocatedAmount(selectedCategoryName, newAmount);

		this.unallocatedBalanceLabel = new SimpleDoubleProperty(
				this.unallocatedBalanceLabel.subtract(newAmount).doubleValue());

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
		this.currentTransactions.add(inflow);
		this.overallBalanceLabel = this.currentBudget.getOverallAmount();
	}

	public void addNewOutflow(double amount, LocalDateTime date, String title, String categoryName) {
		Category category = this.getSpecificCategory(categoryName);
		Outflow outflow = new Outflow(amount, date, title, category);
		this.currentBudget.addOutflow(outflow, category.getName().get());
		this.currentTransactions.add(outflow);
		this.overallBalanceLabel = new SimpleDoubleProperty(this.overallBalanceLabel.subtract(amount).doubleValue());
	}

	public void RemoveTransaction(String title) {
		this.currentTransactions.remove(this.currentBudget.getTransactionByName(title));
		this.currentBudget.removeTransaction(this.currentBudget.getTransactionByName(title));
	}

	public void RemoveCategory(String toDelete) {
		this.currentCategories.remove(this.currentBudget.getCategoryByName(toDelete));
		this.currentBudget.deleteCategory(this.getCurrentBudget().getCategoryByName(toDelete));
		this.unallocatedBalanceLabel = this.currentBudget.getUnallocatedAmount();
	}
	

		

		
	

	public void addBudget(Budget budget) {
		Exporter exporter = new Exporter();
		
		if(exporter.ExportBudgetToServer(this.currentUser, budget)) {
			this.budgets.add(budget);
		}
		
		
		
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
