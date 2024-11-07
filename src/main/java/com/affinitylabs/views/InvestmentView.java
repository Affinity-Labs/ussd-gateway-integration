package com.affinitylabs.views;

import com.affinitylabs.models.Actions;
import com.affinitylabs.models.BaseResponse;
import jakarta.enterprise.context.ApplicationScoped;

import java.util.ArrayList;
import java.util.Map;

@ApplicationScoped
public class InvestmentView {

    public BaseResponse affinityAccount(Map<String, String> accounts, int[] count, String lastOption) {
        ArrayList<String> accountList = new ArrayList<>();
        accountList.add("0. Go back");
        accountList.add( "1. Add a new Affinity Future account");
        accounts.forEach((k,v) -> {
            accountList.add(count[0] + ". " + v + " (****" + k.substring(9, 13) + ")");
            count[0]++;
        });
        if (lastOption != null) {
            accountList.add(lastOption);
        }
        BaseResponse baseResponse = new BaseResponse("Affinity Future accounts", Actions.SHOWMENU);
        baseResponse.setOptions(accountList);
        return baseResponse;
    }

    public BaseResponse createFuturePage1() {
        BaseResponse baseResponse = new BaseResponse("Affinity Future accounts are fixed term agreements. A principal deposit earns interest after an agreed time.", Actions.SHOWMENU);
        ArrayList<String> options = new ArrayList<>();
        options.add("# for Next");
        baseResponse.setOptions(options);
        return baseResponse;
    }

    public BaseResponse requestCertificate(String AccName, String accNumber){
        BaseResponse baseResponse = new BaseResponse("You requested for Investment Certificate of Affinity " + AccName +  " (****" + accNumber.substring(9, 13) + ") " + "account.", Actions.SHOWMENU);
        ArrayList<String> options = new ArrayList<>();
        options.add("1. Confirm");
        options.add("0. Cancel");
        baseResponse.setOptions(options);
        return baseResponse;
    }

    public BaseResponse certificateRequestFailed(){
        return new BaseResponse("Certificate not ready, please try again later", Actions.PROMPT);
    }
}
