package budget.io;

import java.io.File;
import java.io.FileNotFoundException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Scanner;

import budget.model.Budget;
import budget.model.Category;
import budget.model.Inflow;
import budget.model.Outflow;
import budget.model.Transaction;

public class Parser {

	public ArrayList<Budget> parseFiles(ArrayList<File> files) {
		ArrayList<Budget> budgets = new ArrayList<Budget>();
		
		for(File file : files) {
			try {
				budgets.add(this.read(new Scanner(file)));
			} catch (FileNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
		return budgets;
	}
	
	public ArrayList<Budget> parseFromString(ArrayList<String> data) {
		ArrayList<Budget> budgets = new ArrayList<Budget>();
		
		for(String budget : data) {
			budgets.add(this.read(new Scanner(budget)));
		}
		
		return budgets;
	}
	
	private Budget read(Scanner scan) {
		StringBuilder fullString = new StringBuilder();
		while(scan.hasNextLine()) {
			fullString.append(scan.nextLine());
		}
		
		scan.close();
		
		String[] lines = fullString.toString().split("~");
		String[] topLine = lines[0].split(",");
		//username,budgetname,overallBal,unallocatedBal
		String budgetName = topLine[1];
		Double overallBal = Double.parseDouble(topLine[2]);
		Double unallocatedBal = Double.parseDouble(topLine[3]);
		
		ArrayList<String> seplines = new ArrayList<String>();
		int lastIndex = 0;
		for(int i = 2; i < lines.length; i++) {
			if(lines[i].equals("**Transactions**")) {
				lastIndex = i;
				break;
			}
			
			seplines.add(lines[i]);
		}
		
		ArrayList<Category> categories = this.parseCategories(seplines);
		
		seplines.clear();
		for(int i = lastIndex+1; i < lines.length; i++) {
			seplines.add(lines[i]);
		}
		
		ArrayList<Transaction> transactions = this.parseTransactions(seplines);
		
		Budget newBudget = new Budget(budgetName, overallBal, unallocatedBal, categories, transactions);
		return newBudget;
	}
	
	private ArrayList<Category> parseCategories(ArrayList<String> lines) {
		ArrayList<Category> categories = new ArrayList<Category>();
		for(String line : lines) {
			String[] catData = line.split(",");
			categories.add(new Category(catData[0], Double.parseDouble(catData[1]), Double.parseDouble(catData[2])));
		}
		return categories;
	}
	
	private ArrayList<Transaction> parseTransactions(ArrayList<String> lines) {
		//transactionTitle,date,amount,in/out(as bool),category
		ArrayList<Transaction> transactions = new ArrayList<Transaction>();
		for (String line : lines) {
			String[] transactionData = line.split(",");
			
			if(Boolean.parseBoolean(transactionData[3])) {
				transactions.add(new Inflow(
						Double.parseDouble(transactionData[2]),
						LocalDateTime.parse(transactionData[1]),
						transactionData[0]
						));
			} else {
				transactions.add(new Outflow(Double.parseDouble(transactionData[2]),
						LocalDateTime.parse(transactionData[1]), 
						transactionData[0], new Category(transactionData[4], 0, 0)));
			}
		}
		
		return transactions;
	}
}
