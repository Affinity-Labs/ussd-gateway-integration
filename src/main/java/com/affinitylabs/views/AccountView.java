package com.affinitylabs.views;

import com.affinitylabs.models.Actions;
import com.affinitylabs.models.BaseResponse;
import jakarta.enterprise.context.ApplicationScoped;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@ApplicationScoped
public class AccountView {

    public BaseResponse affinityAccount(Map<String, String> accounts, int[] count, String lastOption) {
        ArrayList<String> accountList = new ArrayList<>();

        accounts.forEach((k,v) -> {
            accountList.add(count[0] + ". " + v + " (****" + k.substring(9, 13) + ")");
            count[0]++;
        });
        accountList.add(lastOption);
        BaseResponse baseResponse = new BaseResponse("Affinity Accounts", Actions.SHOWMENU);
        baseResponse.setOptions(accountList);
        return baseResponse;
    }

    public BaseResponse accountRequest() {
        ArrayList<String> options = new ArrayList<>();
        options.add("1. Check account balance");
        options.add("2. Request a mini statement");
        BaseResponse baseResponse = new BaseResponse("Select your Request", Actions.SHOWMENU);
        baseResponse.setOptions(options);
        return baseResponse;
    }

    public BaseResponse accountBalance(String accountType, String accountNumber, Float accountBalance) {
        String formattedBalance = String.format("%,.2f", accountBalance);
        String formattedAccountType = accountType.substring(0, 1).toUpperCase() + accountType.substring(1).toLowerCase();
        String title = "Your Affinity " + formattedAccountType + " (****" + accountNumber.substring(9, 13)+ ")" + " account balance is GHS" +formattedBalance+ ". Thank you for using Affinity";
        return new BaseResponse(title, Actions.PROMPT);
    }

    public BaseResponse accountStatement(boolean emailVerified, String accountType, String accountNumber) {
        String formattedAccountType = accountType.substring(0, 1).toUpperCase() + accountType.substring(1).toLowerCase();
        BaseResponse baseResponse = new BaseResponse("You requested for a statement of Affinity " + formattedAccountType + " (****" + accountNumber.substring(9, 13)+ ")" +  " account. How would like to receive your statement", Actions.SHOWMENU);
        ArrayList<String> options = new ArrayList<>();
        if (emailVerified) {
            options.add("1. SMS");
            options.add("2. Email");
            baseResponse.setOptions(options);
            return baseResponse;
        }
        options.add("1. SMS");
        baseResponse.setOptions(options);
        return baseResponse;
    }

    public BaseResponse addNewGrowth(Map<String, String> accounts, int[] count, String lastOption) {
        ArrayList<String> accountList = new ArrayList<>();
        accounts.forEach((k,v) -> {
            accountList.add(count[0] + ". " + v + " (****" + k.substring(9, 13) + ")");
            count[0]++;
        });
        accountList.add(lastOption);
        BaseResponse baseResponse = new BaseResponse("Fund your new Affinity Growth account from?", Actions.SHOWMENU);
        baseResponse.setOptions(accountList);
        return baseResponse;
    }

    public BaseResponse selectMobileNetwork(String phoneNumber){
       BaseResponse baseResponse = new BaseResponse("Please select the network you are using for "+ phoneNumber, Actions.INPUT);
       ArrayList<String> options = new ArrayList<>();
       options.add("0. Go back");
       options.add("1. MTN");
       options.add("2. Telecel");
       options.add("3. AirtelTigo");
       baseResponse.setOptions(options);
       return baseResponse;
    }

    public BaseResponse amtToTransferNewAccount(String accName, String accNumber){
        return new BaseResponse("How much will you like to transfer from your " + accName + " (****" + accNumber.substring(9, 13) + ")" + "account to your new Affinity Growth account?", Actions.INPUT);
    }

    public BaseResponse nameNewAccount(){
        return new BaseResponse("Please give your new Affinity Growth account a name", Actions.INPUT);
    }

    public BaseResponse amtToTransferMomo(String mobileNetwork){
        return new BaseResponse("How much will you like to transfer from" + mobileNetwork+ "account to your new Affinity Growth account (Min. amount is GHS 20)", Actions.INPUT);
    }
    public  BaseResponse confirmNewAccount(float amount, String accNumber){
        BaseResponse baseResponse = new BaseResponse("Fund your new Affinity Growth account with GHS "+  amount +  "from your Affinity Daily" + " (****" + accNumber.substring(9, 13) + ")", Actions.SHOWMENU);
        ArrayList<String> options = new ArrayList<>();
        options.add("0. Go back");
        options.add("1. Confirm");
        baseResponse.setOptions(options);
        return baseResponse;
    }

    public BaseResponse confirmNewAccountMomo(float amount, String phoneNumber, String mobileNetwork){
        BaseResponse baseResponse = new BaseResponse( "Fund your new Affinity Growth account with GHS "+ amount +  " from your " + mobileNetwork +" mobile wallet (" +  phoneNumber+ ")", Actions.SHOWMENU);
        ArrayList<String> options = new ArrayList<>();
        options.add("0. Go back");
        options.add("1. Confirm");
        baseResponse.setOptions(options);
        return baseResponse;
    }
}
