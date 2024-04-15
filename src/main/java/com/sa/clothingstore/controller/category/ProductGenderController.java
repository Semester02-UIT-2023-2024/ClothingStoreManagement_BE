package com.sa.clothingstore.controller.category;

import com.sa.clothingstore.constant.APIConstant;
import com.sa.clothingstore.dto.request.category.BranchRequest;
import com.sa.clothingstore.dto.request.category.ProductGenderRequest;
import com.sa.clothingstore.model.category.Branch;
import com.sa.clothingstore.model.category.ProductGender;
import com.sa.clothingstore.service.category.branch.BranchService;
import com.sa.clothingstore.service.category.productGender.ProductGenderService;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.List;
import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping(APIConstant.PRODUCTGENDERS)
public class ProductGenderController {
    private final ProductGenderService productGenderService;
    @GetMapping
    public List<ProductGender> getAll() {
        return productGenderService.getAllProductGender();
    }
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public String createProductGender(@RequestBody @Valid ProductGenderRequest productGenderRequest) {
        productGenderService.createProductGender(productGenderRequest);
        return "Product Gender was created successfully";
    }
    @PutMapping(APIConstant.PRODUCTGENDER_ID)
    @ResponseStatus(HttpStatus.OK)
    public String modifyProductGender(@PathVariable UUID id, @RequestBody @Valid ProductGenderRequest productGenderRequest){
        productGenderService.modifyProductGender(id, productGenderRequest);
        return "Product Gender was modified successfully";
    }
    @DeleteMapping(APIConstant.PRODUCTGENDER_ID)
    @ResponseStatus(HttpStatus.OK)
    public String deleteProductGender(@PathVariable UUID id){
        productGenderService.deleteProductGender(id);
        return "Product Gender was delete successfully";
    }
}
