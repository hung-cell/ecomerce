package org.example.ecomerce.common.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ErrorResponse {
    
    @Builder.Default
    private boolean success = false;
    
    private String errorCode;
    
    private String message;
    
    private String details;
    
    @Builder.Default
    private LocalDateTime timestamp = LocalDateTime.now();
    
    private String path;
    
    private Map<String, List<String>> validationErrors;
}

