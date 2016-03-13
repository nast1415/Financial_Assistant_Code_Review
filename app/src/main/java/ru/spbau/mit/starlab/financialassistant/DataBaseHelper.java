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

        ;

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

        public LastActions() {};

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
                                  String expenseSum, String expenseComment, String expenseDate,
                                  String expenseAddTime) {

        Firebase expRef = ref.child("Expenses");
        Firebase newExp = expRef.push();

        Map<String, String> expense = new HashMap<String, String>();
        expense.put("categoryExp", category);
        expense.put("nameExp", expenseName);
        expense.put("sumExp", expenseSum);
        expense.put("commentExp", expenseComment);
        expense.put("dateExp", expenseDate);
        expense.put("addTimeExp", expenseAddTime);
        newExp.setValue(expense);

    }

    public static void addDataToIncomes(Firebase ref, String incomeName, String incomeSum, String incomeComment,
                                 String incomeDate, String incomeAddTime) {

        Firebase incRef = ref.child("Incomes");
        Firebase newInc = incRef.push();

        Map<String, String> income = new HashMap<String, String>();
        income.put("nameInc", incomeName);
        income.put("sumInc", incomeSum);
        income.put("commentInc", incomeComment);
        income.put("dateInc", incomeDate);
        income.put("addTimeInc", incomeAddTime);
        newInc.setValue(income);
    }

    public static void addDataToLastActions(Firebase ref, String category, String name, String sum) {
        Firebase lastActionsRef = ref.child("LastActions");
        Firebase newAction = lastActionsRef.push();

        Map<String, String> action = new HashMap<String, String>();
        action.put("categoryLA", category);
        action.put("nameLA", name);
        action.put("sumLA", sum);
        newAction.setValue(action);
    }
}
