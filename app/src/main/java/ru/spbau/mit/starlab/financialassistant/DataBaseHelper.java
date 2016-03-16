package ru.spbau.mit.starlab.financialassistant;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.firebase.client.Firebase;

import java.util.HashMap;
import java.util.Map;

//Class for creating queries to DB
public class DataBaseHelper {

    //Class for Expenses deserialization
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class Expense {
        private String addTimeExp;
        private String dateExp;
        private String commentExp;
        private double sumExp;
        private String nameExp;
        private String categoryExp;

        public Expense() {
        }

        public String getAddTimeExp() {
            return addTimeExp;
        }

        public String getDateExp() {
            return dateExp;
        }

        public String getCommentExp() {
            return commentExp;
        }

        public double getSumExp() {
            return sumExp;
        }

        public String getNameExp() {
            return nameExp;
        }

        public String getCategoryExp() {
            return categoryExp;
        }
    }

    //Class for LastActions deserialization
    public static class LastActions {
        private double sumLA;
        private String nameLA;
        private String categoryLA;

        public LastActions() {
        }

        public double getSumLA() {
            return sumLA;
        }

        public String getNameLA() {
            return nameLA;
        }

        public String getCategoryLA() {
            return categoryLA;
        }
    }

    public static void addDataToExpenses(Firebase ref, String category, String expenseName,
                                         String expenseSum, String expenseComment,
                                         String expenseDate, String expenseAddTime) {

        Firebase expensesReference = ref.child("Expenses");
        Firebase reference = expensesReference.push();

        Map<String, String> expense = new HashMap<>();
        expense.put("categoryExp", category);
        expense.put("nameExp", expenseName);
        expense.put("sumExp", expenseSum);
        expense.put("commentExp", expenseComment);
        expense.put("dateExp", expenseDate);
        expense.put("addTimeExp", expenseAddTime);
        reference.setValue(expense);

    }

    public static void addDataToIncomes(Firebase ref, String incomeName, String incomeSum,
                                        String incomeComment, String incomeDate,
                                        String incomeAddTime) {

        Firebase incomesReference = ref.child("Incomes");
        Firebase reference = incomesReference.push();

        Map<String, String> income = new HashMap<>();
        income.put("nameInc", incomeName);
        income.put("sumInc", incomeSum);
        income.put("commentInc", incomeComment);
        income.put("dateInc", incomeDate);
        income.put("addTimeInc", incomeAddTime);
        reference.setValue(income);
    }

    public static void addDataToRegularExpenses(Firebase ref, String startPeriod, String endPeriod,
                                                String name, String category, String sum,
                                                String comment, String addTime) {
        Firebase regularExpensesReference = ref.child("RegularExpenses");
        Firebase reference = regularExpensesReference.push();

        Map<String, String> regularExpense = new HashMap<>();
        regularExpense.put("startPeriodRegExp", startPeriod);
        regularExpense.put("endPeriodRegExp", endPeriod);
        regularExpense.put("categoryRegExp", category);
        regularExpense.put("nameRegExp", name);
        regularExpense.put("sumRegExp", sum);
        regularExpense.put("commentRegExp", comment);
        regularExpense.put("addTimeRegExp", addTime);
        reference.setValue(regularExpense);
    }

    public static void addDataToRegularIncome(Firebase ref, String startPeriod, String endPeriod,
                                              String name, String sum,
                                              String comment, String addTime) {
        Firebase regularIncomesReference = ref.child("RegularIncomes");
        Firebase reference = regularIncomesReference.push();

        Map<String, String> regularIncome = new HashMap<>();
        regularIncome.put("startPeriodRegInc", startPeriod);
        regularIncome.put("endPeriodRegInc", endPeriod);
        regularIncome.put("nameRegInc", name);
        regularIncome.put("sumRegInc", sum);
        regularIncome.put("commentRegInc", comment);
        regularIncome.put("addTimeRegInc", addTime);
        reference.setValue(regularIncome);
    }

    public static void addDataToCredits(Firebase ref, String startPeriod, String endPeriod,
                                        String name, String percent, String deposit,
                                        String sum, String addTime) {
        Firebase creditReference = ref.child("Credits");
        Firebase reference = creditReference.push();

        Map<String, String> credit = new HashMap<>();
        credit.put("startPeriodCredit", startPeriod);
        credit.put("endPeriodCredit", endPeriod);
        credit.put("nameCredit", name);
        credit.put("percentCredit", percent);
        credit.put("depositCredit", deposit);
        credit.put("sumCredit", sum);
        credit.put("addTimeCredit", addTime);
        reference.setValue(credit);
    }

    public static void addDataToLastActions(Firebase ref, String category, String name,
                                            String sum) {
        Firebase lastActionsReference = ref.child("LastActions");
        Firebase reference = lastActionsReference.push();

        Map<String, String> action = new HashMap<>();
        action.put("categoryLA", category);
        action.put("nameLA", name);
        action.put("sumLA", sum);
        reference.setValue(action);
    }
}
