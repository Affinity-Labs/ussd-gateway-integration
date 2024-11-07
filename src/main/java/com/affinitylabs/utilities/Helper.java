package com.affinitylabs.utilities;

import com.affinitylabs.models.middleware.accounts.ClientAccountData;
import com.affinitylabs.models.middleware.accounts.CustomerAccounts;
import jakarta.enterprise.context.ApplicationScoped;


import java.util.*;
import java.util.regex.Pattern;

@ApplicationScoped
public class Helper {

    public Map<String, String> accountMapper(ClientAccountData customer) {
        Map<String, String> map = new HashMap<>();
        for(CustomerAccounts accounts: customer.depositAccounts()){
            if(!Objects.equals(accounts.accountSubType(), "FUTURE")){
                map.put(accounts.accountNumber(), accounts.accountSubType());
            }
        }
        // Step 1: Convert map entries into a list
        List<Map.Entry<String, String>> entryList = new ArrayList<>(map.entrySet());
        // Step 2: Sort the list by values
        entryList.sort(Map.Entry.comparingByValue());
        // Step 3: Optionally, put sorted entries into a LinkedHashMap to maintain the order
        Map<String, String> sortedMap = new LinkedHashMap<>();
        for (Map.Entry<String, String> entry : entryList) {
            sortedMap.put(entry.getKey(), entry.getValue());
        }
        return sortedMap;
    }

    public Map<String, String> dailyAccountMapper(ClientAccountData customer) {
        Map<String, String> map = new HashMap<>();
        for(CustomerAccounts accounts: customer.depositAccounts()){
            if(Objects.equals(accounts.accountSubType(), "DAILY")){
                map.put(accounts.accountNumber(), accounts.accountSubType());
            }
        }
        // Step 1: Convert map entries into a list
        List<Map.Entry<String, String>> entryList = new ArrayList<>(map.entrySet());
        // Step 2: Sort the list by values
        entryList.sort(Map.Entry.comparingByValue());
        // Step 3: Optionally, put sorted entries into a LinkedHashMap to maintain the order
        Map<String, String> sortedMap = new LinkedHashMap<>();
        for (Map.Entry<String, String> entry : entryList) {
            sortedMap.put(entry.getKey(), entry.getValue());
        }
        return sortedMap;
    }
    public Map<String, String> futureAccountMapper(ClientAccountData customer) {
        Map<String, String> map = new HashMap<>();
        for(CustomerAccounts accounts: customer.depositAccounts()){
            if(Objects.equals(accounts.accountSubType(), "FUTURE")){
                map.put(accounts.accountNumber(), accounts.accountSubType());
            }
        }
        // Step 1: Convert map entries into a list
        List<Map.Entry<String, String>> entryList = new ArrayList<>(map.entrySet());
        // Step 2: Sort the list by values
        entryList.sort(Map.Entry.comparingByValue());
        // Step 3: Optionally, put sorted entries into a LinkedHashMap to maintain the order
        Map<String, String> sortedMap = new LinkedHashMap<>();
        for (Map.Entry<String, String> entry : entryList) {
            sortedMap.put(entry.getKey(), entry.getValue());
        }
        return sortedMap;
    }

    public Map<String, String> accountLeftMapper(Map<String, String> accounts, Map<String, String> paginatedAccounts) {
        Set<String> targetAccountKeys = new HashSet<>(paginatedAccounts.keySet());
        accounts.keySet().removeAll(targetAccountKeys);
        return accounts;
    }


    public Map<String, String> paginatedAccount(Map<String, String> accounts){
        Set<String>targetAccountKeys = new HashSet<>();
        int maxAccount = 4;
        for(String key : accounts.keySet()){
            if(maxAccount <=0){
                break;
            }
            maxAccount--;
            targetAccountKeys.add(key);
        }
        Map<String, String> paginatedAccount = new HashMap<>(accounts);
        paginatedAccount.keySet().retainAll(targetAccountKeys);
        return paginatedAccount;
    }

    public boolean isValidAmount(String amount) {
        Pattern pattern = Pattern.compile("^(?=.*[1-9]|10)\\d*\\.?\\d{0,2}$");

        try {
            if (Double.parseDouble(amount) < 1) {
                return false;
            }
        } catch (NumberFormatException e) {
            return false;
        }

        return pattern.matcher(amount).matches();
    }
}
