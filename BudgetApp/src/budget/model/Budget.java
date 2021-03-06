package budget.model;

import java.util.ArrayList;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleDoubleProperty;

public class Budget {
	private ArrayList<Category> categories;
	private ArrayList<Transaction> transactions;
	
	private String name;
	
	private DoubleProperty overallAmount;
	private DoubleProperty unallocatedAmount;
	
	
	public Budget(String name) {
		this(name, 0, 0);
	}
	
	public Budget(String name, double overallAmount, double unallocatedAmount) {
		if(name == null || name.contains(",") || name.isEmpty()) {
			throw new IllegalArgumentException("Invalid budget name given");
		}
		
		this.name = name;
		this.overallAmount = new SimpleDoubleProperty(overallAmount);
		this.unallocatedAmount = new SimpleDoubleProperty(unallocatedAmount);
		
		this.categories = new ArrayList<Category>();
		this.transactions = new ArrayList<Transaction>();
	}
	
	public Budget(String name, double overallAmount, double unallocatedAmount, ArrayList<Category> categories, ArrayList<Transaction> transactions) {
		this(name, overallAmount, unallocatedAmount);
		
		this.categories = categories;
		this.transactions = transactions;
	}
	
	/**
	 * Gets the name of the budget
	 * @return The name of the budget
	 */
	public String getName() {
		return this.name;
	}
	
	/**
	 * Updates the name
	 * @precondition name cannot be null, empty, or contain ','
	 * @postcondition Budget's name is updated
	 * @param name The new name for the budget
	 */
	public void setName(String name) {
		if(name == null || name.isEmpty() || name.contains(",")) {
			throw new IllegalArgumentException("A budget's name cannot be empty or have a ',' in it.");
		}
		
		this.name = name;
	}
	
	/**
	 * Adds a category to the budget
	 * @precondition category cannot be null
	 * @postcondition Budget::getCategories.size() == @prev Budget::getCategories.size() + 1 
	 * @param category 
	 */
	public void addCategory(Category category) {
		if(category == null) {
			throw new IllegalArgumentException("Cannot add a null category");
		}
		
		this.categories.add(category);
		this.unallocatedAmount.set(this.unallocatedAmount.doubleValue() - category.getAllocatedAmount().doubleValue());
	}
	
	/**
	 * Removes the category from the budget and 
	 * updates the unallocated amount for the budget
	 * @precondition None
	 * @postcondition Budget::getCategories.size() == @prev Budget::getCategories.size() - 1
	 * @param category The category to remove from the budget
	 */
	public void deleteCategory(Category category) {
		if(this.categories.remove(category)) {
			this.unallocatedAmount.set(this.unallocatedAmount.doubleValue() + category.getAllocatedAmount().doubleValue() - category.getSpentAmount().doubleValue());
		}
		
	}
	
	public Category getCategoryByName(String categoryName) {
		for(Category current : this.categories) {
			if(current.getName().get().equals(categoryName)) {
				return current;
			}
		}
		return null;
	}
	
	public Transaction getTransactionByName(String transactionName) {
		for(Transaction current : this.transactions) {
			if(current.getTitle().get().equals(transactionName)) {
				return current;
			}
			
			
		}
		return null;
	}
	
	/**
	 * Gets the categories for the budget
	 * @return
	 */
	public ArrayList<Category> getCategories() {
		return this.categories;
	}
	
	/**
	 * Updates the allocated amount for the category
	 * @precondition None
	 * @postcondition The category's allocated amount now
	 * 				equals newAmount
	 * @param category The category's name
	 * @param newAmount The new allocated amount
	 */
	public void updateCategoryAllocatedAmount(String category, double newAmount) {
		this.getCategoryByName(category).setAllocatedAmount(newAmount);

		this.unallocatedAmount = new SimpleDoubleProperty (this.unallocatedAmount.subtract(newAmount).doubleValue()); 

	}
	
	public ArrayList<Transaction> getTransactions() {
		return this.transactions;
	}
	
	public void addInflow(Transaction inflow) {
		if(inflow == null) {
			throw new IllegalArgumentException("Cannot add null transaction.");
		}
		double inflowValue = inflow.getAmount().doubleValue();
		this.overallAmount.set(this.overallAmount.doubleValue() + inflowValue);
		this.transactions.add(inflow);
		this.unallocatedAmount.set(this.unallocatedAmount.doubleValue() + inflowValue);
	}
	
	public void addOutflow(Transaction outflow, String categoryName) {
		if(outflow == null) {
			throw new IllegalArgumentException("Cannot add null transaction.");
		}
		double outflowValue = outflow.getAmount().doubleValue();
		this.overallAmount.set(this.overallAmount.doubleValue() - outflowValue);
		this.transactions.add(outflow);
		this.getCategoryByName(categoryName).addToSpentAmount(outflow.getAmount().get()); 
		
	}
	
	public void removeTransaction(Transaction toDelete) {
		if(toDelete instanceof Outflow) {
			this.overallAmount.set(this.overallAmount.doubleValue() + toDelete.getAmount().doubleValue()) ;
			
			if(this.getCategoryByName(((Outflow) toDelete).getCategoryName().get()) != null) {
				this.getCategoryByName(((Outflow) toDelete).getCategoryName().get()).addToSpentAmount(-(toDelete.getAmount().get()));
			}
		} else {
			this.unallocatedAmount.set(this.unallocatedAmount.doubleValue() - toDelete.getAmount().doubleValue()) ;
			this.overallAmount.set(this.overallAmount.doubleValue() - toDelete.getAmount().doubleValue());
		}
			
		this.transactions.remove(toDelete);
	}
	
	public DoubleProperty getUnallocatedAmount() {
		return this.unallocatedAmount;
	}
	
	public DoubleProperty getOverallAmount() {
		return this.overallAmount;
	}
	
	@Override
	public String toString() {
		return this.name;
	}

	public void setUnallocatedAmount(DoubleProperty unallocatedAmount) {
		this.unallocatedAmount = unallocatedAmount;
	}
	
	
	
	/*
	 * Methods needed:
	 * Add category
	 * Delete category
	 * Change allocated amount for category
	 * 
	 * Add inflow
	 * Add outflow
	 * Delete transaction
	 * 
	 * update name
	 * get name
	 */
}
