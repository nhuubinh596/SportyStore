package com.example.backendquanaothethao.dto;
import lombok.Data;
import java.util.List;

@Data
public class OrderRequest {
    private String fullName;
    private String phone;
    private String address;
    private String username;

    private List<OrderItemRequest> items;
}