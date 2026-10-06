package com.example.demo.dto;

import com.example.demo.model.Gender;
import com.example.demo.model.EmploymentType;
import com.example.demo.model.ProductType;
import com.example.demo.validation.OnDraft;
import com.example.demo.validation.OnSubmit;
import com.example.demo.validation.ValidLoanApplication;

import lombok.Data;
import lombok.NoArgsConstructor;

import com.example.demo.model.InterestScheme;
import com.example.demo.model.CollateralType;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Size;


import java.math.BigDecimal;
import java.time.LocalDate;
@Data 
@NoArgsConstructor 
@ValidLoanApplication (groups = {OnSubmit.class})
public class ApplicationFormRequestDTO {
    private Integer loanId;

    @NotBlank(message = "NIK wajib 16 digit angka", groups = {OnSubmit.class})
    @Pattern(regexp = "\\d{16}", message = "NIK wajib 16 digit angka", groups = {OnDraft.class, OnSubmit.class})
    private String nik;

    @NotBlank (message = "Nama lengkap wajib diisi", groups = {OnSubmit.class})
    @Size (max = 100, message = "Nama lengkap maksimal 100 karakter", groups = {OnSubmit.class, OnDraft.class})
    @Pattern (regexp = "^[a-zA-Z ]+$", message = "Nama lengkap hanya boleh huruf dan spasi", groups = {OnSubmit.class, OnDraft.class})
    private String fullName;

    @NotBlank (message = "Email wajib diisi", groups = {OnSubmit.class})
    @Email (message = "Format email tidak valid", groups = {OnSubmit.class, OnDraft.class})
    @Size (max = 100, message = "Email maksimal 100 karakter", groups = {OnSubmit.class, OnDraft.class})
    private String email;

    @NotBlank (message = "Nomor telepon wajib diisi", groups = {OnSubmit.class})
    @Pattern (regexp = "\\d{10,13}", message = "Nomor HP wajib 10-13 digit angka", groups = {OnSubmit.class, OnDraft.class})
    private String phone;

    @NotNull (message = "Tanggal lahir wajib diisi", groups = {OnSubmit.class})
    private LocalDate birthDate;

    @NotNull (message = "Jenis kelamin wajib dipilih", groups = {OnSubmit.class})
    private Gender gender;

    @NotBlank (message = "Alamat domisili wajib diisi", groups = {OnSubmit.class})
    @Size (max = 250, message = "Alamat maksimal 250 karakter", groups = {OnDraft.class, OnSubmit.class})
    private String address;

    @NotBlank (message = "Nama perusahaan wajib diisi", groups = {OnSubmit.class})
    @Size (max = 100, message = "Nama perusahaan maksimal 100 karakter", groups = {OnDraft.class, OnSubmit.class})
    private String companyName;

    @NotNull (message = "Status kepegawaian wajib dipilih", groups = {OnSubmit.class})
    private EmploymentType employmentType;

    @NotNull (message = "Lama bekerja wajib diisi", groups = {OnSubmit.class})
    @Min (value = 3, message = "Lama bekerja minimal 3 bulan", groups = {OnSubmit.class, OnDraft.class})
    private Integer workDurationMonths;

    @NotNull (message = "Pendapatan bersih wajib diisi", groups = {OnSubmit.class})
    @DecimalMin (value = "3000000", message = "Pendapatan bersih minimal Rp 3.000.000", groups = {OnSubmit.class, OnDraft.class})
    private BigDecimal monthlyIncome;

    @NotNull (message = "Cicilan tempat lain wajib diisi", groups = {OnSubmit.class})
    @DecimalMin (value = "0", message = "Cicilan tempat lain minimal Rp 0", groups = {OnSubmit.class, OnDraft.class})
    private BigDecimal existingInstallments;

    @NotNull (message = "Jenis produk pinjaman wajib dipilih (KPR/KKB)", groups = {OnSubmit.class})
    private ProductType productType;

    @NotNull (message = "Nominal pilihan wajib diisi", groups = {OnSubmit.class})
    @DecimalMin (value = "5000000", message = "Plafon pinjaman anatara Rp 5 Juta s.d Rp 500 Juta", groups = {OnSubmit.class, OnDraft.class})
    @DecimalMax (value = "500000000", message = "Plafon pinjaman anatara Rp 5 Juta s.d Rp 500 Juta", groups = {OnSubmit.class, OnDraft.class})
    private BigDecimal requestedAmount;

    @NotNull (message = "Tenor wajib dipilih", groups = {OnSubmit.class})
    private Integer tenorMonths;

    @NotNull(message = "Skema bunga wajib dipilih", groups = {OnSubmit.class})
    private InterestScheme interestScheme;

    @NotBlank (message = "Tujuan penggunaan wajib diisi", groups = {OnSubmit.class})
    @Size (max = 150, message = "Tujuan penggunaan maksimal 150 karakter", groups = {OnSubmit.class, OnDraft.class})
    private String loanPurpose;

    @NotNull (message = "Status agunan wajib dipilih", groups = {OnSubmit.class})
    private Boolean hasCollateral;

    
    private CollateralType collateralType;
    private BigDecimal collateralValue;

}
