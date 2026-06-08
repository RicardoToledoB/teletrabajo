package com.teletrabajo.controller.bienestar;

import com.teletrabajo.dto.bienestar.WellbeingDTOs.*;
import com.teletrabajo.entity.bienestar.BienestarEnums;
import com.teletrabajo.service.impl.bienestar.WellbeingPostulationService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.nio.charset.StandardCharsets;

@RestController
@RequestMapping("/api/v1/wellbeing/postulations")
@RequiredArgsConstructor
@PreAuthorize("hasAnyRole('ADMIN','ADMINISTRATIVO','SUPERVISOR','JEFATURA')")
public class WellbeingPostulationController {

    private final WellbeingPostulationService service;

    @PostMapping("/start")
    public ResponseEntity<PostulationResponse> start(@RequestBody StartRequest request) {
        return ResponseEntity.ok(service.start(request));
    }

    @GetMapping
    public ResponseEntity<Page<PostulationResponse>> search(
            @RequestParam(required = false) Integer userId,
            @RequestParam(required = false) Integer periodYear,
            @RequestParam(required = false) BienestarEnums.PostulationStatus status,
            @PageableDefault(size = 20, sort = "id", direction = Sort.Direction.DESC) Pageable pageable) {
        return ResponseEntity.ok(service.search(userId, periodYear, status, pageable));
    }

    @GetMapping("/my-drafts")
    public ResponseEntity<List<PostulationSummaryResponse>> getMyDrafts() {
        return ResponseEntity.ok(service.getMyDrafts());
    }

    @GetMapping("/my-active")
    public ResponseEntity<List<PostulationSummaryResponse>> getMyActive() {
        return ResponseEntity.ok(service.getMyActive());
    }

    @GetMapping("/my/{id}")
    public ResponseEntity<PostulationResponse> getMyById(@PathVariable Long id) {
        return ResponseEntity.ok(service.getMyById(id));
    }

    @GetMapping("/my/{id}/summary")
    public ResponseEntity<SummaryResponse> getMySummary(@PathVariable Long id) {
        return ResponseEntity.ok(service.getMySummary(id));
    }

    @DeleteMapping("/my/{id}")
    public ResponseEntity<Void> deleteMyPostulation(@PathVariable Long id) {
        service.deleteMyPostulation(id);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/my/{id}/restore")
    public ResponseEntity<Void> restoreMyPostulation(@PathVariable Long id) {
        service.restoreMyPostulation(id);
        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/my/{id}/step")
    public ResponseEntity<Void> updateMyCurrentStep(
            @PathVariable Long id,
            @RequestBody UpdateCurrentStepRequest request
    ) {
        service.updateMyCurrentStep(id, request.getCurrentStep());
        return ResponseEntity.noContent().build();
    }


    @PatchMapping("/my/{id}/family-group")
    public ResponseEntity<PostulationResponse> updateMyFamilyGroup(
            @PathVariable Long id,
            @RequestBody FamilyGroupRequest request
    ) {
        return ResponseEntity.ok(service.updateMyFamilyGroup(id, request));
    }

    @PostMapping("/{id}/restore")
    public ResponseEntity<Void> restorePostulation(@PathVariable Long id) {
        service.restorePostulation(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{id}")
    public ResponseEntity<PostulationResponse> getById(@PathVariable Long id) {
        return ResponseEntity.ok(service.getById(id));
    }

    @GetMapping("/{id}/summary")
    public ResponseEntity<SummaryResponse> summary(@PathVariable Long id) {
        return ResponseEntity.ok(service.getSummary(id));
    }

    @PutMapping("/{id}/affiliate")
    public ResponseEntity<PostulationResponse> updateAffiliate(@PathVariable Long id, @RequestBody AffiliateRequest request) {
        return ResponseEntity.ok(service.updateAffiliate(id, request));
    }

    @PutMapping("/my/{id}/affiliate")
    public ResponseEntity<PostulationResponse> updateMyAffiliate(@PathVariable Long id, @RequestBody AffiliateRequest request) {
        return ResponseEntity.ok(service.updateMyAffiliate(id, request));
    }

    @PostMapping("/{id}/family-members")
    public ResponseEntity<FamilyMemberResponse> createFamilyMember(@PathVariable Long id, @RequestBody FamilyMemberRequest request) {
        return ResponseEntity.ok(service.createFamilyMember(id, request));
    }

    @PutMapping("/family-members/{familyMemberId}")
    public ResponseEntity<FamilyMemberResponse> updateFamilyMember(@PathVariable Long familyMemberId, @RequestBody FamilyMemberRequest request) {
        return ResponseEntity.ok(service.updateFamilyMember(familyMemberId, request));
    }

    @DeleteMapping("/family-members/{familyMemberId}")
    public ResponseEntity<Void> deleteFamilyMember(@PathVariable Long familyMemberId) {
        service.deleteFamilyMember(familyMemberId);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/my/{id}/family-members")
    public ResponseEntity<FamilyMemberResponse> createMyFamilyMember(@PathVariable Long id, @RequestBody FamilyMemberRequest request) {
        return ResponseEntity.ok(service.createMyFamilyMember(id, request));
    }

    @PutMapping("/my/family-members/{familyMemberId}")
    public ResponseEntity<FamilyMemberResponse> updateMyFamilyMember(@PathVariable Long familyMemberId, @RequestBody FamilyMemberRequest request) {
        return ResponseEntity.ok(service.updateMyFamilyMember(familyMemberId, request));
    }

    @DeleteMapping("/my/family-members/{familyMemberId}")
    public ResponseEntity<Void> deleteMyFamilyMember(@PathVariable Long familyMemberId) {
        service.deleteMyFamilyMember(familyMemberId);
        return ResponseEntity.noContent().build();
    }

    @PutMapping("/{id}/beneficiary")
    public ResponseEntity<PostulationResponse> updateBeneficiary(@PathVariable Long id, @RequestBody BeneficiaryRequest request) {
        return ResponseEntity.ok(service.updateBeneficiary(id, request));
    }

    @PutMapping("/my/{id}/beneficiary")
    public ResponseEntity<PostulationResponse> updateMyBeneficiary(@PathVariable Long id, @RequestBody BeneficiaryRequest request) {
        return ResponseEntity.ok(service.updateMyBeneficiary(id, request));
    }

    @PutMapping("/{id}/academic-info")
    public ResponseEntity<AcademicInfoResponse> upsertAcademicInfo(@PathVariable Long id, @RequestBody AcademicInfoRequest request) {
        return ResponseEntity.ok(service.upsertAcademicInfo(id, request));
    }

    @PutMapping("/my/{id}/academic-info")
    public ResponseEntity<AcademicInfoResponse> upsertMyAcademicInfo(@PathVariable Long id, @RequestBody AcademicInfoRequest request) {
        return ResponseEntity.ok(service.upsertMyAcademicInfo(id, request));
    }

    @PutMapping("/{id}/academic-verification")
    public ResponseEntity<AcademicVerificationResponse> upsertAcademicVerification(@PathVariable Long id, @RequestBody AcademicVerificationRequest request) {
        return ResponseEntity.ok(service.upsertAcademicVerification(id, request));
    }

    @PutMapping("/my/{id}/academic-verification")
    public ResponseEntity<AcademicVerificationResponse> upsertMyAcademicVerification(@PathVariable Long id, @RequestBody AcademicVerificationRequest request) {
        return ResponseEntity.ok(service.upsertMyAcademicVerification(id, request));
    }

    @PostMapping("/{id}/incomes")
    public ResponseEntity<SimpleResponse> createIncome(@PathVariable Long id, @RequestBody IncomeRequest request) {
        return ResponseEntity.ok(service.createIncome(id, request));
    }

    @PostMapping("/my/{id}/incomes")
    public ResponseEntity<SimpleResponse> createMyIncome(@PathVariable Long id, @RequestBody IncomeRequest request) {
        return ResponseEntity.ok(service.createMyIncome(id, request));
    }

    @DeleteMapping("/incomes/{incomeId}")
    public ResponseEntity<Void> deleteIncome(@PathVariable Long incomeId) {
        service.deleteIncome(incomeId);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/my/incomes/{incomeId}")
    public ResponseEntity<Void> deleteMyIncome(@PathVariable Long incomeId) {
        service.deleteMyIncome(incomeId);
        return ResponseEntity.noContent().build();
    }

    @PutMapping("/{id}/fixed-expenses")
    public ResponseEntity<List<SimpleResponse>> replaceFixedExpenses(@PathVariable Long id, @RequestBody FixedExpensesRequest request) {
        return ResponseEntity.ok(service.replaceFixedExpenses(id, request));
    }

    @PutMapping("/my/{id}/fixed-expenses")
    public ResponseEntity<List<SimpleResponse>> replaceMyFixedExpenses(@PathVariable Long id, @RequestBody FixedExpensesRequest request) {
        return ResponseEntity.ok(service.replaceMyFixedExpenses(id, request));
    }

    @PostMapping("/{id}/other-expenses")
    public ResponseEntity<SimpleResponse> createOtherExpense(@PathVariable Long id, @RequestBody ExpenseRequest request) {
        return ResponseEntity.ok(service.createOtherExpense(id, request));
    }

    @PostMapping("/my/{id}/other-expenses")
    public ResponseEntity<SimpleResponse> createMyOtherExpense(@PathVariable Long id, @RequestBody ExpenseRequest request) {
        return ResponseEntity.ok(service.createMyOtherExpense(id, request));
    }

    @DeleteMapping("/expenses/{expenseId}")
    public ResponseEntity<Void> deleteExpense(@PathVariable Long expenseId) {
        service.deleteExpense(expenseId);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/my/expenses/{expenseId}")
    public ResponseEntity<Void> deleteMyExpense(@PathVariable Long expenseId) {
        service.deleteMyExpense(expenseId);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/{id}/health-records")
    public ResponseEntity<HealthRecordResponse> createHealthRecord(@PathVariable Long id, @RequestBody HealthRecordRequest request) {
        return ResponseEntity.ok(service.createHealthRecord(id, request));
    }

    @PostMapping("/my/{id}/health-records")
    public ResponseEntity<HealthRecordResponse> createMyHealthRecord(@PathVariable Long id, @RequestBody HealthRecordRequest request) {
        return ResponseEntity.ok(service.createMyHealthRecord(id, request));
    }

    @DeleteMapping("/health-records/{recordId}")
    public ResponseEntity<Void> deleteHealthRecord(@PathVariable Long recordId) {
        service.deleteHealthRecord(recordId);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/my/health-records/{recordId}")
    public ResponseEntity<Void> deleteMyHealthRecord(@PathVariable Long recordId) {
        service.deleteMyHealthRecord(recordId);
        return ResponseEntity.noContent().build();
    }

    @PutMapping("/{id}/housing")
    public ResponseEntity<HousingRequest> upsertHousing(@PathVariable Long id, @RequestBody HousingRequest request) {
        return ResponseEntity.ok(service.upsertHousing(id, request));
    }

    @PutMapping("/my/{id}/housing")
    public ResponseEntity<HousingRequest> upsertMyHousing(@PathVariable Long id, @RequestBody HousingRequest request) {
        return ResponseEntity.ok(service.upsertMyHousing(id, request));
    }

    @GetMapping("/document-types")
    public ResponseEntity<List<DocumentTypeResponse>> listDocumentTypes() {
        return ResponseEntity.ok(service.listDocumentTypes());
    }

    @PostMapping("/{id}/documents")
    public ResponseEntity<DocumentResponse> addDocument(@PathVariable Long id, @RequestBody DocumentRequest request) {
        return ResponseEntity.ok(service.addDocument(id, request));
    }

    @PostMapping("/my/{id}/documents")
    public ResponseEntity<DocumentResponse> addMyDocument(@PathVariable Long id, @RequestBody DocumentRequest request) {
        return ResponseEntity.ok(service.addMyDocument(id, request));
    }

    /**
     * Sube un documento real al servidor y registra automaticamente su metadata.
     * Uso Postman: Body -> form-data -> documentTypeId(text) + file(file).
     */
    @PostMapping(value = "/{id}/documents/upload", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<DocumentResponse> uploadDocument(
            @PathVariable Long id,
            @RequestParam Long documentTypeId,
            @RequestParam("file") MultipartFile file) {
        return ResponseEntity.ok(service.uploadDocument(id, documentTypeId, file));
    }

    /**
     * Variante segura para postulaciones propias del usuario autenticado.
     */
    @PostMapping(value = "/my/{id}/documents/upload", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<DocumentResponse> uploadMyDocument(
            @PathVariable Long id,
            @RequestParam Long documentTypeId,
            @RequestParam("file") MultipartFile file) {
        return ResponseEntity.ok(service.uploadMyDocument(id, documentTypeId, file));
    }


    /**
     * Descarga un documento existente de cualquier postulacion.
     * Uso Postman: GET con Authorization Bearer.
     */
    @GetMapping("/documents/{documentId}/download")
    public ResponseEntity<Resource> downloadDocument(@PathVariable Long documentId) {
        WellbeingPostulationService.DownloadableDocument download = service.downloadDocument(documentId);
        return buildDownloadResponse(download);
    }

    /**
     * Descarga un documento propio del usuario autenticado.
     */
    @GetMapping("/my/documents/{documentId}/download")
    public ResponseEntity<Resource> downloadMyDocument(@PathVariable Long documentId) {
        WellbeingPostulationService.DownloadableDocument download = service.downloadMyDocument(documentId);
        return buildDownloadResponse(download);
    }

    private ResponseEntity<Resource> buildDownloadResponse(WellbeingPostulationService.DownloadableDocument download) {
        MediaType mediaType;
        try {
            mediaType = MediaType.parseMediaType(download.getContentType());
        } catch (Exception ex) {
            mediaType = MediaType.APPLICATION_OCTET_STREAM;
        }

        String contentDisposition = org.springframework.http.ContentDisposition
                .attachment()
                .filename(download.getFilename(), StandardCharsets.UTF_8)
                .build()
                .toString();

        return ResponseEntity.ok()
                .contentType(mediaType)
                .contentLength(download.getSizeBytes())
                .header(HttpHeaders.CONTENT_DISPOSITION, contentDisposition)
                .body(download.getResource());
    }

    @DeleteMapping("/documents/{documentId}")
    public ResponseEntity<Void> deleteDocument(@PathVariable Long documentId) {
        service.deleteDocument(documentId);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/my/documents/{documentId}")
    public ResponseEntity<Void> deleteMyDocument(@PathVariable Long documentId) {
        service.deleteMyDocument(documentId);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/{id}/submit")
    public ResponseEntity<PostulationResponse> submit(@PathVariable Long id, @RequestBody(required = false) StatusRequest request) {
        return ResponseEntity.ok(service.submit(id, request));
    }

    @PostMapping("/my/{id}/submit")
    public ResponseEntity<PostulationResponse> submitMyPostulation(@PathVariable Long id, @RequestBody(required = false) StatusRequest request) {
        return ResponseEntity.ok(service.submitMyPostulation(id, request));
    }

    @PatchMapping("/{id}/status")
    public ResponseEntity<PostulationResponse> changeStatus(@PathVariable Long id, @RequestBody StatusRequest request) {
        return ResponseEntity.ok(service.changeStatus(id, request));
    }
}
