package org.example.dto;

import lombok.Data;

@Data
public class CurrancyDto {
    private int id;
    private String code;
    private String name;
    private String symbol;
    private String exchangeRate;
}
