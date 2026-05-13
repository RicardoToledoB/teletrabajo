package com.teletrabajo.dto.bienestar;

import com.teletrabajo.entity.bienestar.BienestarEnums;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

public class WellbeingDTOs {

    @Data @Builder @NoArgsConstructor @AllArgsConstructor
    public static class StartRequest {
        private Integer userId;
        private Integer periodYear;
    }

    @Data @Builder @NoArgsConstructor @AllArgsConstructor
    public static class AffiliateRequest {
        private String rut;
        private String names;
        private String lastNames;
        private String phone;
        private String email;
        private String address;
        private String birthDate;
        private String sex;
        private String affiliateType;
        private Integer stablishmentId;
        private String affiliateDate;
    }

    @Data @Builder @NoArgsConstructor @AllArgsConstructor
    public static class BeneficiaryRequest {
        private BienestarEnums.BeneficiaryType beneficiaryType;
        private Long familyMemberId;
    }

    @Data @Builder @NoArgsConstructor @AllArgsConstructor
    public static class FamilyMemberRequest {
        private String rut;
        private String names;
        private String lastNames;
        private Integer previtionId;
        private Integer incomeTypeId;
        private Integer parentTypeId;
        private Integer civilStateId;
        private Integer activityId;
        private Integer workPlaceId;
        private Integer studyLevelId;
        private String studyPlace;
        private Boolean student;
        private BigDecimal monthlyIncome;
    }

    @Data @Builder @NoArgsConstructor @AllArgsConstructor
    public static class AcademicInfoRequest {
        private String institution;
        private String career;
        private Integer studyLevelId;
        private String currentSemester;
        private Integer careerDurationSemesters;
        private Boolean studiesInRegion;
        private Boolean hadPreviousBenefit;
    }

    @Data @Builder @NoArgsConstructor @AllArgsConstructor
    public static class AcademicVerificationRequest {
        private String academicSituation;
        private BigDecimal gradeAverage;
        private BigDecimal approvalPercentage;
    }

    @Data @Builder @NoArgsConstructor @AllArgsConstructor
    public static class IncomeRequest {
        private Long familyMemberId;
        private Integer incomeTypeId;
        private BigDecimal amount;
    }

    @Data @Builder @NoArgsConstructor @AllArgsConstructor
    public static class ExpenseRequest {
        private BienestarEnums.ExpenseCategory category;
        private String code;
        private String name;
        private String description;
        private BigDecimal amount;
    }

    @Data @Builder @NoArgsConstructor @AllArgsConstructor
    public static class FixedExpensesRequest {
        private BigDecimal rentOrDividend;
        private BigDecimal electricity;
        private BigDecimal water;
        private BigDecimal gas;
        private BigDecimal phone;
        private BigDecimal credits;
        private BigDecimal tuition;
        private BigDecimal monthlyFee;
        private BigDecimal lodging;
    }

    @Data @Builder @NoArgsConstructor @AllArgsConstructor
    public static class HealthRecordRequest {
        private String personName;
        private Long familyMemberId;
        private String pathology;
        private BigDecimal monthlyExpense;
    }

    @Data @Builder @NoArgsConstructor @AllArgsConstructor
    public static class HousingRequest {
        private Integer typeHousingId;
        private Integer typePropertyId;
        private String housingBackground;
        private String otherBackground;
    }

    @Data @Builder @NoArgsConstructor @AllArgsConstructor
    public static class DocumentRequest {
        private Long documentTypeId;
        private String originalFilename;
        private String storagePath;
        private String contentType;
        private Long sizeBytes;
        private String checksum;
        private Integer uploadedByUserId;
    }

    @Data @Builder @NoArgsConstructor @AllArgsConstructor
    public static class StatusRequest {
        private BienestarEnums.PostulationStatus status;
        private String observation;
        private Integer changedByUserId;
    }

    @Data @Builder @NoArgsConstructor @AllArgsConstructor
    public static class SimpleResponse {
        private Long id;
        private String name;
        private String code;
        private BigDecimal amount;
    }

    @Data @Builder @NoArgsConstructor @AllArgsConstructor
    public static class PostulationResponse {
        private Long id;
        private String code;
        private Integer periodYear;
        private Integer userId;
        private String userRut;
        private String userFullName;
        private Integer stablishmentId;
        private String stablishmentName;
        private BienestarEnums.PostulationStatus status;
        private BienestarEnums.BeneficiaryType beneficiaryType;
        private Long beneficiaryFamilyMemberId;
        private AffiliateRequest affiliate;
        private BigDecimal totalFamilyIncome;
        private BigDecimal totalBasicExpenses;
        private BigDecimal totalEducationExpenses;
        private BigDecimal totalOtherExpenses;
        private BigDecimal totalHealthExpenses;
        private BigDecimal totalFamilyExpenses;
        private LocalDateTime submittedAt;
        private LocalDateTime createdAt;
        private LocalDateTime updatedAt;
    }

    @Data @Builder @NoArgsConstructor @AllArgsConstructor
    public static class SummaryResponse {
        private PostulationResponse postulation;
        private List<FamilyMemberResponse> familyMembers;
        private AcademicInfoResponse academicInfo;
        private AcademicVerificationResponse academicVerification;
        private List<SimpleResponse> incomes;
        private List<SimpleResponse> expenses;
        private List<HealthRecordResponse> healthRecords;
        private HousingResponse housing;
        private List<DocumentResponse> documents;
        private Long requiredDocumentsTotal;
        private Long requiredDocumentsUploaded;
        private Boolean canSubmit;
        private List<String> pendingRequiredDocuments;
    }

    @Data @Builder @NoArgsConstructor @AllArgsConstructor
    public static class FamilyMemberResponse {
        private Long id;
        private String rut;
        private String names;
        private String lastNames;
        private Integer parentTypeId;
        private String parentTypeName;
        private Integer previtionId;
        private Integer incomeTypeId;
        private BigDecimal monthlyIncome;
    }

    @Data @Builder @NoArgsConstructor @AllArgsConstructor
    public static class AcademicInfoResponse {
        private Long id;
        private String institution;
        private String career;
        private Integer studyLevelId;
        private String studyLevelName;
        private String currentSemester;
        private Integer careerDurationSemesters;
        private Boolean studiesInRegion;
        private Boolean hadPreviousBenefit;
    }

    @Data @Builder @NoArgsConstructor @AllArgsConstructor
    public static class AcademicVerificationResponse {
        private Long id;
        private String academicSituation;
        private BigDecimal gradeAverage;
        private BigDecimal approvalPercentage;
    }

    @Data @Builder @NoArgsConstructor @AllArgsConstructor
    public static class HealthRecordResponse {
        private Long id;
        private String personName;
        private Long familyMemberId;
        private String pathology;
        private BigDecimal monthlyExpense;
    }

    @Data @Builder @NoArgsConstructor @AllArgsConstructor
    public static class HousingResponse {
        private Long id;
        private Integer typeHousingId;
        private String typeHousingName;
        private Integer typePropertyId;
        private String typePropertyName;
        private String housingBackground;
        private String otherBackground;
    }

    @Data @Builder @NoArgsConstructor @AllArgsConstructor
    public static class DocumentTypeResponse {
        private Long id;
        private String code;
        private String name;
        private BienestarEnums.DocumentGroup documentGroup;
        private Boolean required;
        private String helpText;
        private String allowedExtensions;
        private Integer maxSizeMb;
    }

    @Data @Builder @NoArgsConstructor @AllArgsConstructor
    public static class DocumentResponse {
        private Long id;
        private Long documentTypeId;
        private String documentTypeName;
        private String documentTypeCode;
        private String originalFilename;
        private String storagePath;
        private String contentType;
        private Long sizeBytes;
        private LocalDateTime uploadedAt;
    }
}
