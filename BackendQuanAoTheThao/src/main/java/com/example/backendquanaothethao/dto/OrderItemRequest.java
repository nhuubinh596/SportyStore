package com.example.backendquanaothethao.dto;
import lombok.Data;

@Data
public class OrderItemRequest {
    private Long productId;
    private int quantity;
    private Double price;
}