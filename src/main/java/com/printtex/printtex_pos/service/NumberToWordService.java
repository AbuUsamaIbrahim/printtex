package com.printtex.printtex_pos.service;

public interface NumberToWordService {
    String addCommasToNumber(double amount);

    String convertNumberToWord(double doubleNumber);

    String castDoubleToString(double amount);
}
