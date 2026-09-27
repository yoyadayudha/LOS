package com.example.demo.validation;

import com.example.demo.dto.ApplicationFormRequestDTO;
import com.example.demo.model.CollateralType;
import com.example.demo.model.ProductType;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class LoanApplicationValidator implements ConstraintValidator<ValidLoanApplication, ApplicationFormRequestDTO> {
    
    @Override 
    public boolean isValid(ApplicationFormRequestDTO dto, ConstraintValidatorContext context ){

        boolean isValid = true;

        context.disableDefaultConstraintViolation();

        if (Boolean.TRUE.equals(dto.getHasCollateral())){

            if (dto.getCollateralType() == null){
                context.buildConstraintViolationWithTemplate("Jenis agunan wajib dipilih jika menggunakan agunan")
                .addPropertyNode("collateralType")
                .addConstraintViolation();

                isValid = false;
            }


            if(dto.getCollateralValue() == null || dto.getCollateralValue().signum() <= 0){
                context.buildConstraintViolationWithTemplate("Nilai agunan harus lebih dari 0")
                .addPropertyNode("collateralValue")
                .addConstraintViolation();

                isValid = false;
            }
        }

        if (dto.getProductType() == ProductType.KPR) {

            if(!Boolean.TRUE.equals(dto.getHasCollateral())){
                context.buildConstraintViolationWithTemplate("Produk KPR wajib ada agunan")
                .addPropertyNode("hasCollateral")
                .addConstraintViolation();

                isValid = false;
            }

            if(dto.getCollateralType() != null && dto.getCollateralType() != CollateralType.PROPERTY){
                context.buildConstraintViolationWithTemplate("Produk KPR wajib agunan PROPERTY")
                .addPropertyNode("collateralType")
                .addConstraintViolation();

                isValid = false;
            }
        }

        if (dto.getProductType() == ProductType.KKB) {

            if(!Boolean.TRUE.equals(dto.getHasCollateral())){
                context.buildConstraintViolationWithTemplate("Produk KKB wajib ada agunan")
                .addPropertyNode("hasCollateral")
                .addConstraintViolation();

                isValid = false;
            }

            if(dto.getCollateralType() != null && dto.getCollateralType() != CollateralType.VEHICLE){
                context.buildConstraintViolationWithTemplate("Produk KKB wajib agunan VEHICLE")
                .addPropertyNode("collateralType")
                .addConstraintViolation();

                isValid = false;
            }
        }


        return isValid;
    }
}
