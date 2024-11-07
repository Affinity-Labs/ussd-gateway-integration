package com.affinitylabs.views;

import com.affinitylabs.models.Actions;
import com.affinitylabs.models.BaseResponse;
import jakarta.enterprise.context.ApplicationScoped;

import java.util.ArrayList;
import java.util.Map;

@ApplicationScoped
public class SendMoneyView {

    public BaseResponse sendMoneyChannel(){
        BaseResponse baseResponse = new BaseResponse("Send money to:", Actions.SHOWMENU);
        ArrayList<String> optionsList = new ArrayList<>();
        optionsList.add("0. Go back");
        optionsList.add("1. An Affinity account");
        optionsList.add("2. A mobile wallet");
        optionsList.add("3. Other bank");
        baseResponse.setOptions(optionsList);
        return baseResponse;
    }

    public BaseResponse sendMoneyAnyAccount(){
        return new BaseResponse("Enter the Affinity account number you want to send money to.", Actions.INPUT);
    }

    public BaseResponse sendMoneyToMomo(){
        BaseResponse baseResponse = new BaseResponse("What mobile network do you want to send money to?", Actions.SHOWMENU);
        ArrayList<String> optionsList = new ArrayList<>();
        optionsList.add("0. Go back");
        optionsList.add( "1. MTN");
        optionsList.add( "2. Telecel");
        optionsList.add( "3. AT");
        baseResponse.setOptions(optionsList);
        return baseResponse;
    }

    public BaseResponse amtToSend(String accName, String accNumber){
        return new BaseResponse("How much do you want to send to " + accName +  " (****" + accNumber.substring(9, 13) + ")", Actions.INPUT);
    }

    public BaseResponse accSendMoney(Map<String, String> accounts, int[] count, String lastOption){
        ArrayList<String> accountList = new ArrayList<>();
        accounts.forEach((k,v) -> {
            accountList.add(count[0] + ". " + v + " (****" + k.substring(9, 13) + ")");
            count[0]++;
        });
        if(lastOption !=null){
            accountList.add(lastOption);
        }
        BaseResponse baseResponse = new BaseResponse("Select where you want to send money from", Actions.SHOWMENU);
        baseResponse.setOptions(accountList);
        return baseResponse;
    }

}
