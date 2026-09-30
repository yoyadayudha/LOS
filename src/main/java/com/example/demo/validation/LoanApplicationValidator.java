package com.example.demo.validation;

import com.example.demo.dto.ApplicationFormRequestDTO;
import com.example.demo.model.CollateralType;
import com.example.demo.model.ProductType;

import java.time.LocalDate;
import java.time.Period;
import java.util.Arrays;
import java.util.List;

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

        if(dto.getBirthDate() != null){

            int age = Period.between(dto.getBirthDate(), LocalDate.now() ).getYears();

            if( age < 21 || age > 65){
                context.buildConstraintViolationWithTemplate("Usia debitur harus 21-65 tahun")
                .addPropertyNode("birthDate")
                .addConstraintViolation();

                isValid = false;
            }

        }

        

        if(dto.getTenorMonths() != null && dto.getProductType() != null){
            List<Integer> allowedTenors;

            if(dto.getProductType() == ProductType.KPR){
                allowedTenors = Arrays.asList(6, 12, 18, 24, 36, 60, 120, 180);
            }else{
                allowedTenors = Arrays.asList(6, 12, 18, 24, 36, 60);
            }


            if(!allowedTenors.contains(dto.getTenorMonths())){
                context.buildConstraintViolationWithTemplate("Tenor tidak sesuai dengan pilihan yang tersedia untuk produk ini")
                .addPropertyNode("tenorMonths")
                .addConstraintViolation();
                isValid = false;

            }


        }


        return isValid;
    }
}
