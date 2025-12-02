package com.example.backendquanaothethao.dto;
import lombok.Data;
import java.util.List;

@Data
public class OrderRequest {
    // Thông tin người nhận
    private String fullName;
    private String phone;
    private String address;
    private String username; // Để biết ai mua (nếu đã login)

    private List<OrderItemRequest> items;
}