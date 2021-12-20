package com.printtex.printtex_pos.service;

import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.text.DecimalFormat;

@Service
public class NumberToWordServiceImpl implements NumberToWordService {
    @Override
    public String convertNumberToWord(double doubleNumber) {
        String stringNumber = new BigDecimal(doubleNumber).toPlainString();
        String[] numbers = stringNumber.split("\\.");
        String number = numbers[0];
        String twodigitword = "";
        String word = "";
        String[] HTLC = {"", "Hundred", "Thousand", "Lac", "Crore"};
        int split[] = {0, 2, 3, 5, 7, 9};
        String[] temp = new String[split.length];
        boolean addzero = true;
        int len1 = number.length();
        if (len1 > split[split.length - 1]) {
            System.out.println("Error. Maximum Allowed digits " + split[split.length - 1]);
            System.exit(0);
        }
        for (int l = 1; l < split.length; l++)
            if (number.length() == split[l]) addzero = false;
        if (addzero == true) number = "0" + number;
        int len = number.length();
        int j = 0;
        while (split[j] < len) {
            int beg = len - split[j + 1];
            int end = beg + split[j + 1] - split[j];
            temp[j] = number.substring(beg, end);
            j = j + 1;
        }

        for (int k = 0; k < j; k++) {
            twodigitword = ConvertOnesTwos(temp[k]);
            if (k >= 1) {
                if (twodigitword.trim().length() != 0) word = twodigitword + " " + HTLC[k] + " " + word;
            } else {
                word = twodigitword;
            }
        }
        return word;
    }

    private String ConvertOnesTwos(String t) {
        final String[] ones = {"", "One", "Two", "Three", "Four", "Five", "Six", "Seven", "Eight", "Nine", "Ten", "Eleven", "Twelve", "Thirteen", "Fourteen", "Fifteen", "Sixteen", "Seventeen", "Eighteen", "Nineteen"};
        final String[] tens = {"", "Ten", "Twenty", "Thirty", "Forty", "Fifty", "Sixty", "Seventy", "Eighty", "Ninety"};

        String word = "";
        int num = Integer.parseInt(t);
        if (num % 10 == 0) {
            word = tens[num / 10] + " " + word;
        } else if (num < 20) {
            word = ones[num] + " " + word;
        } else {
            word = tens[(num - (num % 10)) / 10] + word;
            word = word + " " + ones[num % 10];
        }
        return word;
    }

    @Override
    public String addCommasToNumber(double amount) {
        DecimalFormat formatter = new DecimalFormat("#,###.00");
        return formatter.format(amount);
    }

    @Override
    public String castDoubleToString(double amount) {
        return new BigDecimal(amount).toPlainString();
    }
}
