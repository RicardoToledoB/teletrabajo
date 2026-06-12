package com.teletrabajo.service.impl.bienestar;

import com.teletrabajo.dto.bienestar.WellbeingDTOs.*;
import com.teletrabajo.entity.*;
import com.teletrabajo.entity.bienestar.*;
import com.teletrabajo.repository.*;
import com.teletrabajo.repository.bienestar.*;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.multipart.MultipartFile;

import java.math.BigDecimal;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.security.DigestInputStream;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.Optional;
import java.util.Set;

@Service
@RequiredArgsConstructor
@Transactional
public class WellbeingPostulationService {

    private final WellbeingPostulationRepository postulationRepository;
    private final WellbeingFamilyMemberRepository familyMemberRepository;
    private final WellbeingAcademicInfoRepository academicInfoRepository;
    private final WellbeingAcademicVerificationRepository academicVerificationRepository;
    private final WellbeingFamilyIncomeRepository incomeRepository;
    private final WellbeingFamilyExpenseRepository expenseRepository;
    private final WellbeingHealthRecordRepository healthRecordRepository;
    private final WellbeingHousingRepository housingRepository;
    private final WellbeingDocumentTypeRepository documentTypeRepository;
    private final WellbeingPostulationDocumentRepository documentRepository;
    private final WellbeingStatusHistoryRepository statusHistoryRepository;

    private final UserRepository userRepository;
    private final StablishmentRepository stablishmentRepository;
    private final PrevitionRepository previtionRepository;
    private final ContractTypeRepository contractTypeRepository;
    private final ParentTypeRepository parentTypeRepository;
    private final CivilStateRepository civilStateRepository;
    private final ActivityRepository activityRepository;
    private final WorkPlaceRepository workPlaceRepository;
    private final StudyRepository studyRepository;
    private final TypeHousingRepository typeHousingRepository;
    private final TypePropertyRepository typePropertyRepository;

    @Value("${app.upload.base-dir:uploads}")
    private String uploadBaseDir;

    private static final long MAX_DOCUMENT_SIZE_BYTES = 10L * 1024L * 1024L;
    private static final Set<String> ALLOWED_DOCUMENT_CONTENT_TYPES = Set.of(
            "application/pdf",
            "image/jpeg",
            "image/png"
    );

    @lombok.Getter
    @lombok.AllArgsConstructor
    public static class DownloadableDocument {
        private final Resource resource;
        private final String filename;
        private final String contentType;
        private final long sizeBytes;
    }

    /**
     * Inicia una postulación de bienestar de forma idempotente.
     *
     * Regla productiva:
     * - Si el usuario ya tiene un borrador activo para el año solicitado, se retorna ese mismo borrador.
     * - Si no existe borrador activo, se crea uno nuevo.
     *
     * Esto evita múltiples DRAFT activos para el mismo usuario/año cuando el frontend llama /start más de una vez.
     */
    public PostulationResponse start(StartRequest request) {
        UserEntity user = userRepository.findById(request.getUserId())
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        Integer year = request.getPeriodYear() != null
                ? request.getPeriodYear()
                : LocalDateTime.now().getYear();

        Optional<WellbeingPostulationEntity> existingDraft = postulationRepository
                .findFirstByUserIdAndPeriodYearAndStatusAndDeletedAtIsNullOrderByCurrentStepDescUpdatedAtDescCreatedAtDesc(
                        user.getId(),
                        year,
                        BienestarEnums.PostulationStatus.DRAFT
                );

        if (existingDraft.isPresent()) {
            return mapPostulation(existingDraft.get());
        }

        String code = "POST-" + UUID.randomUUID().toString().replace("-", "").substring(0, 10).toUpperCase();

        WellbeingPostulationEntity postulation = WellbeingPostulationEntity.builder()
                .code(code)
                .periodYear(year)
                .user(user)
                .status(BienestarEnums.PostulationStatus.DRAFT)
                .currentStep(1)
                .affiliateRut(user.getRut())
                .affiliateNames(join(user.getFirstName(), user.getSecondName()))
                .affiliateLastNames(join(user.getFirstLastName(), user.getSecondLastName()))
                .affiliateEmail(user.getEmail())
                .build();

        return mapPostulation(postulationRepository.save(postulation));
    }

    @Transactional(readOnly = true)
    public PostulationResponse getById(Long id) { return mapPostulation(getPostulation(id)); }

    @Transactional(readOnly = true)
    public SummaryResponse getSummary(Long id) { return buildSummary(getPostulation(id)); }

    @Transactional(readOnly = true)
    public Page<PostulationResponse> search(Integer userId, Integer periodYear, BienestarEnums.PostulationStatus status, Pageable pageable) {
        return postulationRepository.search(userId, periodYear, status, pageable).map(this::mapPostulation);
    }

    @Transactional(readOnly = true)
    public List<PostulationSummaryResponse> getMyDrafts() {
        UserEntity currentUser = getAuthenticatedUser();

        return postulationRepository
                .findByUserIdAndStatusAndDeletedAtIsNullOrderByCurrentStepDescUpdatedAtDescCreatedAtDesc(
                        currentUser.getId(),
                        BienestarEnums.PostulationStatus.DRAFT
                )
                .stream()
                .map(this::mapPostulationSummary)
                .toList();
    }

    @Transactional(readOnly = true)
    public List<PostulationSummaryResponse> getMyActive() {
        UserEntity currentUser = getAuthenticatedUser();

        return postulationRepository
                .findByUserIdAndStatusInAndDeletedAtIsNullOrderByUpdatedAtDescCreatedAtDesc(
                        currentUser.getId(),
                        List.of(
                                BienestarEnums.PostulationStatus.DRAFT,
                                BienestarEnums.PostulationStatus.SUBMITTED,
                                BienestarEnums.PostulationStatus.UNDER_REVIEW,
                                BienestarEnums.PostulationStatus.OBSERVED
                        )
                )
                .stream()
                .map(this::mapPostulationSummary)
                .toList();
    }

    @Transactional(readOnly = true)
    public PostulationResponse getMyById(Long id) {
        return mapPostulation(getMyPostulation(id));
    }

    @Transactional(readOnly = true)
    public SummaryResponse getMySummary(Long id) {
        return buildSummary(getMyPostulation(id));
    }

    public void deleteMyPostulation(Long id) {
        WellbeingPostulationEntity p = getMyPostulation(id);
        ensureDraft(p);
        postulationRepository.delete(p);
    }

    public void updateMyCurrentStep(Long id, Integer currentStep) {
        if (currentStep == null || currentStep < 1 || currentStep > 11) {
            throw new RuntimeException("El paso actual no es válido");
        }

        WellbeingPostulationEntity p = getMyPostulation(id);

        // El paso 11 corresponde a la pantalla final. Puede guardarse incluso
        // cuando la postulación ya fue enviada, porque submit() cambia el estado a SUBMITTED.
        if (currentStep == 11 && p.getStatus() == BienestarEnums.PostulationStatus.SUBMITTED) {
            p.setCurrentStep(11);
            postulationRepository.save(p);
            return;
        }

        ensureDraft(p);
        p.setCurrentStep(currentStep);
        postulationRepository.save(p);
    }

    public PostulationResponse updateMyFamilyGroup(Long id, FamilyGroupRequest request) {
        WellbeingPostulationEntity p = getMyEditablePostulation(id);

        p.setIsSingleParentHome(Boolean.TRUE.equals(request.getIsSingleParentHome()));

        return mapPostulation(postulationRepository.save(p));
    }

    @Transactional(readOnly = true)
    public List<PostulationResponse> getDeletedPostulations(Integer userId, Integer periodYear, BienestarEnums.PostulationStatus status) {
        return postulationRepository.findAllDeletedIncludingSoftDeleted()
                .stream()
                .filter(p -> userId == null || (p.getUser() != null && userId.equals(p.getUser().getId())))
                .filter(p -> periodYear == null || periodYear.equals(p.getPeriodYear()))
                .filter(p -> status == null || status == p.getStatus())
                .map(this::mapPostulation)
                .toList();
    }

    @Transactional
    public void deletePostulation(Long id) {
        WellbeingPostulationEntity p = postulationRepository.findAnyByIdIncludingDeleted(id)
                .orElseThrow(() -> new RuntimeException("Postulación no encontrada"));

        if (p.getDeletedAt() == null) {
            postulationRepository.delete(p);
        }
    }

    public void restorePostulation(Long id) {
        WellbeingPostulationEntity p = postulationRepository.findAnyByIdIncludingDeleted(id)
                .orElseThrow(() -> new RuntimeException("Postulación no encontrada"));

        p.setDeletedAt(null);
        postulationRepository.save(p);
    }

    public void restoreMyPostulation(Long id) {
        UserEntity currentUser = getAuthenticatedUser();

        WellbeingPostulationEntity p = postulationRepository.findAnyByIdIncludingDeleted(id)
                .orElseThrow(() -> new RuntimeException("Postulación no encontrada"));

        if (p.getUser() == null || !p.getUser().getId().equals(currentUser.getId())) {
            throw new RuntimeException("La postulación no pertenece al usuario autenticado");
        }

        p.setDeletedAt(null);
        postulationRepository.save(p);
    }

    public PostulationResponse updateAffiliate(Long id, AffiliateRequest request) {
        WellbeingPostulationEntity p = getDraftPostulation(id);
        p.setAffiliateRut(request.getRut());
        p.setAffiliateNames(request.getNames());
        p.setAffiliateLastNames(request.getLastNames());
        p.setAffiliatePhone(request.getPhone());
        p.setAffiliateEmail(request.getEmail());
        p.setAffiliateAddress(request.getAddress());
        p.setAffiliateBirthDate(request.getBirthDate());
        p.setAffiliateSex(request.getSex());
        p.setAffiliateType(request.getAffiliateType());
        p.setAffiliateDate(request.getAffiliateDate());
        if (request.getStablishmentId() != null) {
            p.setStablishment(stablishmentRepository.findById(request.getStablishmentId()).orElseThrow(() -> new RuntimeException("Establecimiento no encontrado")));
        }
        return mapPostulation(postulationRepository.save(p));
    }

    public PostulationResponse updateMyAffiliate(Long id, AffiliateRequest request) {
        getMyEditablePostulation(id);
        return updateAffiliate(id, request);
    }

    public FamilyMemberResponse createFamilyMember(Long postulationId, FamilyMemberRequest request) {
        WellbeingPostulationEntity p = getDraftPostulation(postulationId);
        WellbeingFamilyMemberEntity e = mapFamilyMemberRequest(new WellbeingFamilyMemberEntity(), request);
        e.setPostulation(p);
        return mapFamilyMember(familyMemberRepository.save(e));
    }

    public FamilyMemberResponse updateFamilyMember(Long id, FamilyMemberRequest request) {
        WellbeingFamilyMemberEntity e = familyMemberRepository.findById(id).orElseThrow(() -> new RuntimeException("Integrante familiar no encontrado"));
        ensureDraft(e.getPostulation());
        e = mapFamilyMemberRequest(e, request);
        return mapFamilyMember(familyMemberRepository.save(e));
    }

    public void deleteFamilyMember(Long id) {
        WellbeingFamilyMemberEntity e = familyMemberRepository.findById(id).orElseThrow(() -> new RuntimeException("Integrante familiar no encontrado"));
        ensureDraft(e.getPostulation());
        familyMemberRepository.delete(e);
        recalcTotals(e.getPostulation().getId());
    }

    public FamilyMemberResponse createMyFamilyMember(Long postulationId, FamilyMemberRequest request) {
        getMyEditablePostulation(postulationId);
        return createFamilyMember(postulationId, request);
    }

    public FamilyMemberResponse updateMyFamilyMember(Long id, FamilyMemberRequest request) {
        WellbeingFamilyMemberEntity e = getMyFamilyMember(id);
        ensureDraft(e.getPostulation());
        e = mapFamilyMemberRequest(e, request);
        return mapFamilyMember(familyMemberRepository.save(e));
    }

    public void deleteMyFamilyMember(Long id) {
        WellbeingFamilyMemberEntity e = getMyFamilyMember(id);
        ensureDraft(e.getPostulation());
        Long postulationId = e.getPostulation().getId();
        familyMemberRepository.delete(e);
        recalcTotals(postulationId);
    }

    public PostulationResponse updateBeneficiary(Long id, BeneficiaryRequest request) {
        WellbeingPostulationEntity p = getDraftPostulation(id);
        p.setBeneficiaryType(request.getBeneficiaryType());
        if (request.getBeneficiaryType() == BienestarEnums.BeneficiaryType.FAMILY_MEMBER) {
            if (request.getFamilyMemberId() == null) throw new RuntimeException("Debe indicar familyMemberId para beneficiario integrante familiar");
            WellbeingFamilyMemberEntity member = familyMemberRepository.findById(request.getFamilyMemberId())
                    .orElseThrow(() -> new RuntimeException("Integrante familiar beneficiario no encontrado"));
            if (!member.getPostulation().getId().equals(id)) throw new RuntimeException("El integrante no pertenece a la postulación");
            p.setBeneficiaryFamilyMember(member);
        } else {
            p.setBeneficiaryFamilyMember(null);
        }
        return mapPostulation(postulationRepository.save(p));
    }

    public PostulationResponse updateMyBeneficiary(Long id, BeneficiaryRequest request) {
        getMyEditablePostulation(id);
        if (request.getFamilyMemberId() != null) {
            getMyFamilyMember(request.getFamilyMemberId());
        }
        return updateBeneficiary(id, request);
    }

    public AcademicInfoResponse upsertAcademicInfo(Long postulationId, AcademicInfoRequest request) {
        WellbeingPostulationEntity p = getDraftPostulation(postulationId);
        WellbeingAcademicInfoEntity e = academicInfoRepository.findByPostulationId(postulationId).orElseGet(WellbeingAcademicInfoEntity::new);
        e.setPostulation(p);
        e.setInstitution(request.getInstitution());
        e.setCareer(request.getCareer());
        e.setStudyLevel(request.getStudyLevelId() == null ? null : studyRepository.findById(request.getStudyLevelId()).orElseThrow(() -> new RuntimeException("Nivel de estudio no encontrado")));
        e.setCurrentSemester(request.getCurrentSemester());
        e.setCareerDurationSemesters(request.getCareerDurationSemesters());
        e.setStudiesInRegion(request.getStudiesInRegion());
        e.setHadPreviousBenefit(request.getHadPreviousBenefit());
        return mapAcademicInfo(academicInfoRepository.save(e));
    }

    public AcademicInfoResponse upsertMyAcademicInfo(Long postulationId, AcademicInfoRequest request) {
        getMyEditablePostulation(postulationId);
        return upsertAcademicInfo(postulationId, request);
    }

    public AcademicVerificationResponse upsertAcademicVerification(Long postulationId, AcademicVerificationRequest request) {
        WellbeingPostulationEntity p = getDraftPostulation(postulationId);
        WellbeingAcademicVerificationEntity e = academicVerificationRepository.findByPostulationId(postulationId).orElseGet(WellbeingAcademicVerificationEntity::new);
        e.setPostulation(p);
        e.setAcademicSituation(request.getAcademicSituation());
        e.setGradeAverage(request.getGradeAverage());
        e.setApprovalPercentage(request.getApprovalPercentage());
        return mapAcademicVerification(academicVerificationRepository.save(e));
    }

    public AcademicVerificationResponse upsertMyAcademicVerification(Long postulationId, AcademicVerificationRequest request) {
        getMyEditablePostulation(postulationId);
        return upsertAcademicVerification(postulationId, request);
    }

    public SimpleResponse createIncome(Long postulationId, IncomeRequest request) {
        WellbeingPostulationEntity p = getDraftPostulation(postulationId);
        WellbeingFamilyIncomeEntity e = WellbeingFamilyIncomeEntity.builder()
                .postulation(p)
                .familyMember(request.getFamilyMemberId() == null ? null : familyMemberRepository.findById(request.getFamilyMemberId()).orElseThrow(() -> new RuntimeException("Integrante familiar no encontrado")))
                .incomeType(request.getIncomeTypeId() == null ? null : contractTypeRepository.findById(request.getIncomeTypeId()).orElseThrow(() -> new RuntimeException("Tipo de ingreso no encontrado")))
                .amount(nvl(request.getAmount()))
                .build();
        e = incomeRepository.save(e);
        recalcTotals(postulationId);
        return mapIncome(e);
    }

    public SimpleResponse createMyIncome(Long postulationId, IncomeRequest request) {
        getMyEditablePostulation(postulationId);
        if (request.getFamilyMemberId() != null) {
            getMyFamilyMember(request.getFamilyMemberId());
        }
        return createIncome(postulationId, request);
    }

    public void deleteIncome(Long id) {
        WellbeingFamilyIncomeEntity e = incomeRepository.findById(id).orElseThrow(() -> new RuntimeException("Ingreso no encontrado"));
        ensureDraft(e.getPostulation());
        Long postulationId = e.getPostulation().getId();
        incomeRepository.delete(e);
        recalcTotals(postulationId);
    }

    public void deleteMyIncome(Long id) {
        WellbeingFamilyIncomeEntity e = incomeRepository.findById(id).orElseThrow(() -> new RuntimeException("Ingreso no encontrado"));
        ensureOwnsPostulation(e.getPostulation());
        deleteIncome(id);
    }

    public List<SimpleResponse> replaceFixedExpenses(Long postulationId, FixedExpensesRequest r) {
        WellbeingPostulationEntity p = getDraftPostulation(postulationId);
        expenseRepository.findByPostulationIdAndCategory(postulationId, BienestarEnums.ExpenseCategory.BASIC).forEach(expenseRepository::delete);
        expenseRepository.findByPostulationIdAndCategory(postulationId, BienestarEnums.ExpenseCategory.EDUCATION).forEach(expenseRepository::delete);
        addExpense(p, BienestarEnums.ExpenseCategory.BASIC, "RENT_OR_DIVIDEND", "Arriendo / Dividendo", r.getRentOrDividend());
        addExpense(p, BienestarEnums.ExpenseCategory.BASIC, "ELECTRICITY", "Luz", r.getElectricity());
        addExpense(p, BienestarEnums.ExpenseCategory.BASIC, "WATER", "Agua", r.getWater());
        addExpense(p, BienestarEnums.ExpenseCategory.BASIC, "GAS", "Gas", r.getGas());
        addExpense(p, BienestarEnums.ExpenseCategory.BASIC, "PHONE", "Teléfono", r.getPhone());
        addExpense(p, BienestarEnums.ExpenseCategory.BASIC, "CREDITS", "Créditos", r.getCredits());
        addExpense(p, BienestarEnums.ExpenseCategory.EDUCATION, "TUITION", "Matrícula", r.getTuition());
        addExpense(p, BienestarEnums.ExpenseCategory.EDUCATION, "MONTHLY_FEE", "Mensualidad", r.getMonthlyFee());
        addExpense(p, BienestarEnums.ExpenseCategory.EDUCATION, "LODGING", "Alojamiento", r.getLodging());
        recalcTotals(postulationId);
        return expenseRepository.findByPostulationIdOrderByIdAsc(postulationId).stream().map(this::mapExpense).toList();
    }

    public List<SimpleResponse> replaceMyFixedExpenses(Long postulationId, FixedExpensesRequest request) {
        getMyEditablePostulation(postulationId);
        return replaceFixedExpenses(postulationId, request);
    }

    public SimpleResponse createOtherExpense(Long postulationId, ExpenseRequest request) {
        WellbeingPostulationEntity p = getDraftPostulation(postulationId);
        WellbeingFamilyExpenseEntity e = WellbeingFamilyExpenseEntity.builder()
                .postulation(p).category(BienestarEnums.ExpenseCategory.OTHER)
                .code(request.getCode()).name(request.getName()).description(request.getDescription()).amount(nvl(request.getAmount())).build();
        e = expenseRepository.save(e);
        recalcTotals(postulationId);
        return mapExpense(e);
    }

    public SimpleResponse createMyOtherExpense(Long postulationId, ExpenseRequest request) {
        getMyEditablePostulation(postulationId);
        return createOtherExpense(postulationId, request);
    }

    public void deleteExpense(Long id) {
        WellbeingFamilyExpenseEntity e = expenseRepository.findById(id).orElseThrow(() -> new RuntimeException("Gasto no encontrado"));
        ensureDraft(e.getPostulation());
        Long postulationId = e.getPostulation().getId();
        expenseRepository.delete(e);
        recalcTotals(postulationId);
    }

    public void deleteMyExpense(Long id) {
        WellbeingFamilyExpenseEntity e = expenseRepository.findById(id).orElseThrow(() -> new RuntimeException("Gasto no encontrado"));
        ensureOwnsPostulation(e.getPostulation());
        deleteExpense(id);
    }

    public HealthRecordResponse createHealthRecord(Long postulationId, HealthRecordRequest request) {
        WellbeingPostulationEntity p = getDraftPostulation(postulationId);
        WellbeingHealthRecordEntity e = WellbeingHealthRecordEntity.builder()
                .postulation(p)
                .familyMember(request.getFamilyMemberId() == null ? null : familyMemberRepository.findById(request.getFamilyMemberId()).orElseThrow(() -> new RuntimeException("Integrante familiar no encontrado")))
                .personName(request.getPersonName()).pathology(request.getPathology()).monthlyExpense(nvl(request.getMonthlyExpense())).build();
        e = healthRecordRepository.save(e);
        recalcTotals(postulationId);
        return mapHealth(e);
    }

    public HealthRecordResponse createMyHealthRecord(Long postulationId, HealthRecordRequest request) {
        getMyEditablePostulation(postulationId);
        if (request.getFamilyMemberId() != null) {
            getMyFamilyMember(request.getFamilyMemberId());
        }
        return createHealthRecord(postulationId, request);
    }

    public void deleteHealthRecord(Long id) {
        WellbeingHealthRecordEntity e = healthRecordRepository.findById(id).orElseThrow(() -> new RuntimeException("Registro de salud no encontrado"));
        ensureDraft(e.getPostulation());
        Long postulationId = e.getPostulation().getId();
        healthRecordRepository.delete(e);
        recalcTotals(postulationId);
    }

    public void deleteMyHealthRecord(Long id) {
        WellbeingHealthRecordEntity e = healthRecordRepository.findById(id).orElseThrow(() -> new RuntimeException("Registro de salud no encontrado"));
        ensureOwnsPostulation(e.getPostulation());
        deleteHealthRecord(id);
    }

    public HousingRequest upsertHousing(Long postulationId, HousingRequest request) {
        WellbeingPostulationEntity p = getDraftPostulation(postulationId);
        WellbeingHousingEntity e = housingRepository.findByPostulationId(postulationId).orElseGet(WellbeingHousingEntity::new);
        e.setPostulation(p);
        e.setTypeHousing(request.getTypeHousingId() == null ? null : typeHousingRepository.findById(request.getTypeHousingId()).orElseThrow(() -> new RuntimeException("Tipo de inmueble no encontrado")));
        e.setTypeProperty(request.getTypePropertyId() == null ? null : typePropertyRepository.findById(request.getTypePropertyId()).orElseThrow(() -> new RuntimeException("Tipo de tenencia no encontrado")));
        e.setHousingBackground(request.getHousingBackground());
        e.setOtherBackground(request.getOtherBackground());
        housingRepository.save(e);
        return request;
    }

    public HousingRequest upsertMyHousing(Long postulationId, HousingRequest request) {
        getMyEditablePostulation(postulationId);
        return upsertHousing(postulationId, request);
    }

    public DocumentResponse addDocument(Long postulationId, DocumentRequest request) {
        WellbeingPostulationEntity p = getDraftPostulation(postulationId);
        WellbeingDocumentTypeEntity dt = documentTypeRepository.findById(request.getDocumentTypeId()).orElseThrow(() -> new RuntimeException("Tipo de documento no encontrado"));
        WellbeingPostulationDocumentEntity e = WellbeingPostulationDocumentEntity.builder()
                .postulation(p).documentType(dt).originalFilename(request.getOriginalFilename()).storagePath(request.getStoragePath())
                .contentType(request.getContentType()).sizeBytes(request.getSizeBytes()).checksum(request.getChecksum())
                .uploadedBy(request.getUploadedByUserId() == null ? p.getUser() : userRepository.findById(request.getUploadedByUserId()).orElseThrow(() -> new RuntimeException("Usuario que sube no encontrado"))).build();
        return mapDocument(documentRepository.save(e));
    }

    public DocumentResponse addMyDocument(Long postulationId, DocumentRequest request) {
        getMyEditablePostulation(postulationId);
        return addDocument(postulationId, request);
    }

    /**
     * Lista todos los documentos asociados a una postulacion.
     * No modifica datos; solo consulta wellbeing_postulation_documents.
     */
    @Transactional(readOnly = true)
    public List<DocumentResponse> getDocumentsByPostulation(Long postulationId) {
        WellbeingPostulationEntity p = getPostulation(postulationId);
        return documentRepository.findByPostulationIdOrderByIdAsc(p.getId())
                .stream()
                .map(this::mapDocument)
                .toList();
    }

    /**
     * Lista documentos de una postulacion propia del usuario autenticado.
     */
    @Transactional(readOnly = true)
    public List<DocumentResponse> getMyDocumentsByPostulation(Long postulationId) {
        WellbeingPostulationEntity p = getMyPostulation(postulationId);
        return documentRepository.findByPostulationIdOrderByIdAsc(p.getId())
                .stream()
                .map(this::mapDocument)
                .toList();
    }

    /**
     * Sube fisicamente un documento al servidor y registra la metadata real.
     */
    public DocumentResponse uploadDocument(Long postulationId, Long documentTypeId, MultipartFile file) {
        WellbeingPostulationEntity p = getDraftPostulation(postulationId);
        return uploadDocumentInternal(p, documentTypeId, file);
    }

    /**
     * Sube fisicamente un documento de una postulacion propia del usuario autenticado.
     */
    public DocumentResponse uploadMyDocument(Long postulationId, Long documentTypeId, MultipartFile file) {
        WellbeingPostulationEntity p = getMyEditablePostulation(postulationId);
        return uploadDocumentInternal(p, documentTypeId, file);
    }

    private DocumentResponse uploadDocumentInternal(WellbeingPostulationEntity p, Long documentTypeId, MultipartFile file) {
        if (documentTypeId == null) {
            throw new RuntimeException("Debe indicar el tipo de documento");
        }
        if (file == null || file.isEmpty()) {
            throw new RuntimeException("Debe adjuntar un archivo");
        }
        if (file.getSize() > MAX_DOCUMENT_SIZE_BYTES) {
            throw new RuntimeException("El archivo supera el maximo permitido de 10 MB");
        }

        String contentType = file.getContentType() == null ? "application/octet-stream" : file.getContentType();
        if (!ALLOWED_DOCUMENT_CONTENT_TYPES.contains(contentType)) {
            throw new RuntimeException("Tipo de archivo no permitido. Solo se permiten PDF, JPG o PNG");
        }

        WellbeingDocumentTypeEntity dt = documentTypeRepository.findById(documentTypeId)
                .orElseThrow(() -> new RuntimeException("Tipo de documento no encontrado"));

        String originalFilename = sanitizeFilename(file.getOriginalFilename());
        String extension = getExtension(originalFilename);
        String storedFilename = UUID.randomUUID() + (extension.isBlank() ? "" : "." + extension);
        String documentFolder = sanitizePathSegment(dt.getCode() == null ? "documento" : dt.getCode().toLowerCase());

        Path targetDir = Paths.get(uploadBaseDir, "bienestar", String.valueOf(p.getId()), documentFolder)
                .toAbsolutePath()
                .normalize();
        Path targetFile = targetDir.resolve(storedFilename).normalize();

        if (!targetFile.startsWith(targetDir)) {
            throw new RuntimeException("Nombre de archivo invalido");
        }

        try {
            Files.createDirectories(targetDir);
            Files.copy(file.getInputStream(), targetFile, StandardCopyOption.REPLACE_EXISTING);
            String checksum = sha256(targetFile);
            String storagePath = "/uploads/bienestar/" + p.getId() + "/" + documentFolder + "/" + storedFilename;

            WellbeingPostulationDocumentEntity e = WellbeingPostulationDocumentEntity.builder()
                    .postulation(p)
                    .documentType(dt)
                    .originalFilename(originalFilename)
                    .storagePath(storagePath)
                    .contentType(contentType)
                    .sizeBytes(file.getSize())
                    .checksum(checksum)
                    .uploadedBy(p.getUser())
                    .build();

            return mapDocument(documentRepository.save(e));
        } catch (IOException ex) {
            throw new RuntimeException("No fue posible guardar el archivo en el servidor", ex);
        }
    }

    private String sanitizeFilename(String filename) {
        if (filename == null || filename.isBlank()) {
            return "documento";
        }
        String cleaned = filename.replace("\\", "/");
        cleaned = cleaned.substring(cleaned.lastIndexOf('/') + 1);
        cleaned = cleaned.replaceAll("[^a-zA-Z0-9._-]", "_");
        return cleaned.isBlank() ? "documento" : cleaned;
    }

    private String sanitizePathSegment(String value) {
        return value.replaceAll("[^a-zA-Z0-9_-]", "_");
    }

    private String getExtension(String filename) {
        int idx = filename.lastIndexOf('.');
        if (idx < 0 || idx == filename.length() - 1) {
            return "";
        }
        return filename.substring(idx + 1).toLowerCase();
    }

    private String sha256(Path file) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            try (InputStream input = Files.newInputStream(file); DigestInputStream dis = new DigestInputStream(input, digest)) {
                byte[] buffer = new byte[8192];
                while (dis.read(buffer) != -1) {
                    // lectura completa para calcular hash
                }
            }
            StringBuilder sb = new StringBuilder();
            for (byte b : digest.digest()) {
                sb.append(String.format("%02x", b));
            }
            return sb.toString();
        } catch (NoSuchAlgorithmException | IOException ex) {
            throw new RuntimeException("No fue posible calcular checksum del archivo", ex);
        }
    }


    @Transactional(readOnly = true)
    public DownloadableDocument downloadDocument(Long documentId) {
        WellbeingPostulationDocumentEntity document = documentRepository.findById(documentId)
                .orElseThrow(() -> new RuntimeException("Documento no encontrado"));
        return buildDownloadableDocument(document);
    }

    @Transactional(readOnly = true)
    public DownloadableDocument downloadMyDocument(Long documentId) {
        WellbeingPostulationDocumentEntity document = documentRepository.findById(documentId)
                .orElseThrow(() -> new RuntimeException("Documento no encontrado"));
        ensureOwnsPostulation(document.getPostulation());
        return buildDownloadableDocument(document);
    }

    private DownloadableDocument buildDownloadableDocument(WellbeingPostulationDocumentEntity document) {
        Path filePath = resolveStoragePath(document.getStoragePath());

        if (!Files.exists(filePath) || !Files.isRegularFile(filePath) || !Files.isReadable(filePath)) {
            throw new RuntimeException("El archivo fisico no existe o no puede ser leido en el servidor");
        }

        try {
            Resource resource = new UrlResource(filePath.toUri());
            String filename = sanitizeFilename(document.getOriginalFilename());
            String contentType = document.getContentType() == null || document.getContentType().isBlank()
                    ? "application/octet-stream"
                    : document.getContentType();
            long sizeBytes = Files.size(filePath);
            return new DownloadableDocument(resource, filename, contentType, sizeBytes);
        } catch (IOException ex) {
            throw new RuntimeException("No fue posible preparar la descarga del documento", ex);
        }
    }

    private Path resolveStoragePath(String storagePath) {
        if (storagePath == null || storagePath.isBlank()) {
            throw new RuntimeException("El documento no tiene ruta de almacenamiento registrada");
        }

        Path baseDir = Paths.get(uploadBaseDir).toAbsolutePath().normalize();
        String cleanPath = storagePath.trim().replace("\\", "/");

        Path candidate;
        if (cleanPath.startsWith("/uploads/")) {
            candidate = baseDir.resolve(cleanPath.substring("/uploads/".length())).normalize();
        } else if (cleanPath.startsWith("uploads/")) {
            candidate = baseDir.resolve(cleanPath.substring("uploads/".length())).normalize();
        } else {
            candidate = baseDir.resolve(cleanPath.replaceFirst("^/+", "")).normalize();
        }

        if (!candidate.startsWith(baseDir)) {
            throw new RuntimeException("Ruta de documento invalida");
        }

        return candidate;
    }

    public void deleteDocument(Long id) {
        WellbeingPostulationDocumentEntity e = documentRepository.findById(id).orElseThrow(() -> new RuntimeException("Documento no encontrado"));
        ensureDraft(e.getPostulation());
        documentRepository.delete(e);
    }

    public void deleteMyDocument(Long id) {
        WellbeingPostulationDocumentEntity e = documentRepository.findById(id).orElseThrow(() -> new RuntimeException("Documento no encontrado"));
        ensureOwnsPostulation(e.getPostulation());
        deleteDocument(id);
    }

    @Transactional(readOnly = true)
    public List<DocumentTypeResponse> listDocumentTypes() {
        return documentTypeRepository.findByActiveTrueOrderByIdAsc().stream().map(this::mapDocumentType).toList();
    }

    public PostulationResponse submit(Long id, StatusRequest request) {
        WellbeingPostulationEntity p = getDraftPostulation(id);
        SummaryResponse summary = buildSummary(p);
        if (!Boolean.TRUE.equals(summary.getCanSubmit())) {
            throw new RuntimeException("No se puede enviar la postulación. Existen documentos obligatorios pendientes o datos mínimos incompletos.");
        }
        BienestarEnums.PostulationStatus old = p.getStatus();
        p.setStatus(BienestarEnums.PostulationStatus.SUBMITTED);
        p.setSubmittedAt(LocalDateTime.now());
        p.setCurrentStep(11);
        p = postulationRepository.save(p);
        saveHistory(p, old, p.getStatus(), request != null ? request.getObservation() : "Postulación enviada", request != null ? request.getChangedByUserId() : null);
        return mapPostulation(p);
    }

    public PostulationResponse submitMyPostulation(Long id, StatusRequest request) {
        getMyEditablePostulation(id);
        return submit(id, request);
    }

    public PostulationResponse changeStatus(Long id, StatusRequest request) {
        WellbeingPostulationEntity p = getPostulation(id);
        BienestarEnums.PostulationStatus old = p.getStatus();
        p.setStatus(request.getStatus());
        p = postulationRepository.save(p);
        saveHistory(p, old, p.getStatus(), request.getObservation(), request.getChangedByUserId());
        return mapPostulation(p);
    }

    private void saveHistory(WellbeingPostulationEntity p, BienestarEnums.PostulationStatus oldStatus, BienestarEnums.PostulationStatus newStatus, String observation, Integer userId) {
        statusHistoryRepository.save(WellbeingStatusHistoryEntity.builder()
                .postulation(p).oldStatus(oldStatus).newStatus(newStatus).observation(observation)
                .changedBy(userId == null ? null : userRepository.findById(userId).orElse(null)).build());
    }

    private void recalcTotals(Long postulationId) {
        WellbeingPostulationEntity p = getPostulation(postulationId);
        BigDecimal income = incomeRepository.sumByPostulationId(postulationId);
        BigDecimal basic = expenseRepository.sumByPostulationIdAndCategory(postulationId, BienestarEnums.ExpenseCategory.BASIC);
        BigDecimal education = expenseRepository.sumByPostulationIdAndCategory(postulationId, BienestarEnums.ExpenseCategory.EDUCATION);
        BigDecimal other = expenseRepository.sumByPostulationIdAndCategory(postulationId, BienestarEnums.ExpenseCategory.OTHER);
        BigDecimal health = healthRecordRepository.sumByPostulationId(postulationId);
        p.setTotalFamilyIncome(nvl(income)); p.setTotalBasicExpenses(nvl(basic)); p.setTotalEducationExpenses(nvl(education)); p.setTotalOtherExpenses(nvl(other)); p.setTotalHealthExpenses(nvl(health));
        p.setTotalFamilyExpenses(nvl(basic).add(nvl(education)).add(nvl(other)).add(nvl(health)));
        postulationRepository.save(p);
    }

    private void addExpense(WellbeingPostulationEntity p, BienestarEnums.ExpenseCategory category, String code, String name, BigDecimal amount) {
        expenseRepository.save(WellbeingFamilyExpenseEntity.builder().postulation(p).category(category).code(code).name(name).amount(nvl(amount)).build());
    }

    private SummaryResponse buildSummary(WellbeingPostulationEntity p) {
        List<FamilyMemberResponse> members = familyMemberRepository.findByPostulationIdOrderByIdAsc(p.getId()).stream().map(this::mapFamilyMember).toList();
        AcademicInfoResponse academic = academicInfoRepository.findByPostulationId(p.getId()).map(this::mapAcademicInfo).orElse(null);
        AcademicVerificationResponse verification = academicVerificationRepository.findByPostulationId(p.getId()).map(this::mapAcademicVerification).orElse(null);
        List<SimpleResponse> incomes = incomeRepository.findByPostulationIdOrderByIdAsc(p.getId()).stream().map(this::mapIncome).toList();
        List<SimpleResponse> expenses = expenseRepository.findByPostulationIdOrderByIdAsc(p.getId()).stream().map(this::mapExpense).toList();
        List<HealthRecordResponse> health = healthRecordRepository.findByPostulationIdOrderByIdAsc(p.getId()).stream().map(this::mapHealth).toList();
        HousingResponse housing = housingRepository.findByPostulationId(p.getId()).map(this::mapHousing).orElse(null);
        List<DocumentResponse> docs = documentRepository.findByPostulationIdOrderByIdAsc(p.getId()).stream().map(this::mapDocument).toList();
        List<WellbeingDocumentTypeEntity> required = documentTypeRepository.findByActiveTrueOrderByIdAsc().stream().filter(d -> Boolean.TRUE.equals(d.getRequired())).toList();
        List<String> pending = new ArrayList<>();
        for (WellbeingDocumentTypeEntity dt : required) {
            if (!documentRepository.existsByPostulationIdAndDocumentTypeId(p.getId(), dt.getId())) pending.add(dt.getName());
        }
        boolean minData = p.getBeneficiaryType() != null && academic != null;
        return SummaryResponse.builder().postulation(mapPostulation(p)).familyMembers(members).academicInfo(academic).academicVerification(verification).incomes(incomes).expenses(expenses).healthRecords(health).housing(housing).documents(docs).requiredDocumentsTotal((long) required.size()).requiredDocumentsUploaded(required.size() - (long) pending.size()).canSubmit(pending.isEmpty() && minData).pendingRequiredDocuments(pending).build();
    }

    private UserEntity getAuthenticatedUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null || !authentication.isAuthenticated()) {
            throw new RuntimeException("Usuario no autenticado");
        }

        String email = authentication.getName();

        return userRepository.findByEmailIgnoreCase(email)
                .orElseThrow(() -> new RuntimeException("Usuario autenticado no encontrado: " + email));
    }

    private WellbeingPostulationEntity getMyPostulation(Long id) {
        UserEntity currentUser = getAuthenticatedUser();

        return postulationRepository.findByIdAndUserIdAndDeletedAtIsNull(id, currentUser.getId())
                .orElseThrow(() -> new RuntimeException("Postulación no encontrada o no pertenece al usuario autenticado"));
    }

    private PostulationSummaryResponse mapPostulationSummary(WellbeingPostulationEntity p) {
        return PostulationSummaryResponse.builder()
                .id(p.getId())
                .code(p.getCode())
                .status(p.getStatus())
                .currentStep(p.getCurrentStep())
                .createdAt(p.getCreatedAt())
                .updatedAt(p.getUpdatedAt())
                .affiliate(AffiliateSummaryResponse.builder()
                        .rut(p.getAffiliateRut())
                        .names(p.getAffiliateNames())
                        .lastNames(p.getAffiliateLastNames())
                        .build())
                .build();
    }

    /**
     * Obtiene una postulación propia del usuario autenticado, incluyendo una validación
     * explícita de soft delete. Esto evita que registros con deleted_at informado fallen
     * como si simplemente no existieran.
     */
    private WellbeingPostulationEntity getMyEditablePostulation(Long id) {
        UserEntity currentUser = getAuthenticatedUser();

        WellbeingPostulationEntity p = postulationRepository.findAnyByIdIncludingDeleted(id)
                .orElseThrow(() -> new RuntimeException("Postulación no encontrada"));

        if (p.getDeletedAt() != null) {
            throw new RuntimeException("La postulación se encuentra eliminada y no puede ser modificada. Debe restaurarse antes de continuar.");
        }

        if (p.getUser() == null || !p.getUser().getId().equals(currentUser.getId())) {
            throw new RuntimeException("La postulación no pertenece al usuario autenticado");
        }

        ensureDraft(p);
        return p;
    }

    /**
     * Valida que una postulación pertenezca al usuario autenticado.
     */
    private void ensureOwnsPostulation(WellbeingPostulationEntity p) {
        UserEntity currentUser = getAuthenticatedUser();

        if (p == null || p.getUser() == null || !p.getUser().getId().equals(currentUser.getId())) {
            throw new RuntimeException("La postulación no pertenece al usuario autenticado");
        }

        if (p.getDeletedAt() != null) {
            throw new RuntimeException("La postulación se encuentra eliminada y no puede ser modificada. Debe restaurarse antes de continuar.");
        }
    }

    /**
     * Obtiene un integrante familiar validando que pertenezca a una postulación del usuario autenticado
     * y que dicha postulación esté editable.
     */
    private WellbeingFamilyMemberEntity getMyFamilyMember(Long familyMemberId) {
        UserEntity currentUser = getAuthenticatedUser();

        WellbeingFamilyMemberEntity member = familyMemberRepository.findById(familyMemberId)
                .orElseThrow(() -> new RuntimeException("Integrante familiar no encontrado"));

        WellbeingPostulationEntity postulation = member.getPostulation();

        if (postulation == null) {
            throw new RuntimeException("El integrante familiar no tiene una postulación asociada");
        }

        if (postulation.getDeletedAt() != null) {
            throw new RuntimeException("La postulación asociada se encuentra eliminada y no puede ser modificada");
        }

        if (postulation.getUser() == null || !postulation.getUser().getId().equals(currentUser.getId())) {
            throw new RuntimeException("El integrante familiar no pertenece al usuario autenticado");
        }

        ensureDraft(postulation);
        return member;
    }


    private WellbeingPostulationEntity getPostulation(Long id) { return postulationRepository.findById(id).orElseThrow(() -> new RuntimeException("Postulación no encontrada")); }
    private WellbeingPostulationEntity getDraftPostulation(Long id) { WellbeingPostulationEntity p = getPostulation(id); ensureDraft(p); return p; }
    private void ensureDraft(WellbeingPostulationEntity p) { if (p.getStatus() != BienestarEnums.PostulationStatus.DRAFT && p.getStatus() != BienestarEnums.PostulationStatus.OBSERVED) throw new RuntimeException("La postulación no se puede modificar en estado " + p.getStatus()); }
    private BigDecimal nvl(BigDecimal v){ return v == null ? BigDecimal.ZERO : v; }
    private String join(String a, String b){ return ((a==null?"":a) + " " + (b==null?"":b)).trim(); }

    private WellbeingFamilyMemberEntity mapFamilyMemberRequest(WellbeingFamilyMemberEntity e, FamilyMemberRequest r) {
        e.setRut(r.getRut()); e.setNames(r.getNames()); e.setLastNames(r.getLastNames()); e.setBirthDate(r.getBirthDate());
        e.setPrevition(r.getPrevitionId()==null?null:previtionRepository.findById(r.getPrevitionId()).orElseThrow(() -> new RuntimeException("Previsión no encontrada")));
        e.setIncomeType(r.getIncomeTypeId()==null?null:contractTypeRepository.findById(r.getIncomeTypeId()).orElseThrow(() -> new RuntimeException("Tipo de ingreso no encontrado")));
        e.setParentType(r.getParentTypeId()==null?null:parentTypeRepository.findById(r.getParentTypeId()).orElseThrow(() -> new RuntimeException("Parentesco no encontrado")));
        e.setCivilState(r.getCivilStateId()==null?null:civilStateRepository.findById(r.getCivilStateId()).orElseThrow(() -> new RuntimeException("Estado civil no encontrado")));
        e.setActivity(r.getActivityId()==null?null:activityRepository.findById(r.getActivityId()).orElseThrow(() -> new RuntimeException("Actividad no encontrada")));
        e.setWorkPlace(r.getWorkPlaceId()==null?null:workPlaceRepository.findById(r.getWorkPlaceId()).orElseThrow(() -> new RuntimeException("Lugar de trabajo no encontrado")));
        e.setStudyLevel(r.getStudyLevelId()==null?null:studyRepository.findById(r.getStudyLevelId()).orElseThrow(() -> new RuntimeException("Nivel de estudio no encontrado")));
        e.setStudyPlace(r.getStudyPlace());
        e.setOthersWorkplaces(r.getOthersWorkplaces());
        e.setOthersActivities(r.getOthersActivities());
        e.setStudent(r.getStudent()); e.setMonthlyIncome(nvl(r.getMonthlyIncome()));
        return e;
    }

    private PostulationResponse mapPostulation(WellbeingPostulationEntity p) {
        UserEntity u = p.getUser();
        return PostulationResponse.builder()
                .id(p.getId())
                .code(p.getCode())
                .periodYear(p.getPeriodYear())
                .userId(u != null ? u.getId() : null)
                .userRut(u != null ? u.getRut() : null)
                .userFullName(u != null ? join(join(u.getFirstName(), u.getSecondName()), join(u.getFirstLastName(), u.getSecondLastName())) : null)
                .stablishmentId(p.getStablishment()!=null?p.getStablishment().getId():null)
                .stablishmentName(p.getStablishment()!=null?p.getStablishment().getName():null)
                .status(p.getStatus())
                .currentStep(p.getCurrentStep())
                .isSingleParentHome(p.getIsSingleParentHome())
                .beneficiaryType(p.getBeneficiaryType())
                .beneficiaryFamilyMemberId(p.getBeneficiaryFamilyMember()!=null?p.getBeneficiaryFamilyMember().getId():null).affiliate(AffiliateRequest.builder().rut(p.getAffiliateRut()).names(p.getAffiliateNames()).lastNames(p.getAffiliateLastNames()).phone(p.getAffiliatePhone()).email(p.getAffiliateEmail()).address(p.getAffiliateAddress()).birthDate(p.getAffiliateBirthDate()).sex(p.getAffiliateSex()).affiliateType(p.getAffiliateType()).stablishmentId(p.getStablishment()!=null?p.getStablishment().getId():null).affiliateDate(p.getAffiliateDate()).build()).totalFamilyIncome(p.getTotalFamilyIncome()).totalBasicExpenses(p.getTotalBasicExpenses()).totalEducationExpenses(p.getTotalEducationExpenses()).totalOtherExpenses(p.getTotalOtherExpenses()).totalHealthExpenses(p.getTotalHealthExpenses()).totalFamilyExpenses(p.getTotalFamilyExpenses()).submittedAt(p.getSubmittedAt()).createdAt(p.getCreatedAt()).updatedAt(p.getUpdatedAt()).deletedAt(p.getDeletedAt()).build();
    }
    private FamilyMemberResponse mapFamilyMember(WellbeingFamilyMemberEntity e){
        return FamilyMemberResponse.builder()
                .id(e.getId())
                .rut(e.getRut())
                .names(e.getNames())
                .lastNames(e.getLastNames())
                .birthDate(e.getBirthDate())
                .parentTypeId(e.getParentType()!=null?e.getParentType().getId():null)
                .parentTypeName(e.getParentType()!=null?e.getParentType().getName():null)
                .previtionId(e.getPrevition()!=null?e.getPrevition().getId():null)
                .previtionName(e.getPrevition()!=null?e.getPrevition().getName():null)
                .incomeTypeId(e.getIncomeType()!=null?e.getIncomeType().getId():null)
                .incomeTypeName(e.getIncomeType()!=null?e.getIncomeType().getName():null)
                .civilStateId(e.getCivilState()!=null?e.getCivilState().getId():null)
                .civilStateName(e.getCivilState()!=null?e.getCivilState().getName():null)
                .activityId(e.getActivity()!=null?e.getActivity().getId():null)
                .activityName(e.getActivity()!=null?e.getActivity().getName():null)
                .workPlaceId(e.getWorkPlace()!=null?e.getWorkPlace().getId():null)
                .workPlaceName(e.getWorkPlace()!=null?e.getWorkPlace().getName():null)
                .studyLevelId(e.getStudyLevel()!=null?e.getStudyLevel().getId():null)
                .studyLevelName(e.getStudyLevel()!=null?e.getStudyLevel().getName():null)
                .studyPlace(e.getStudyPlace())
                .othersWorkplaces(e.getOthersWorkplaces())
                .othersActivities(e.getOthersActivities())
                .student(e.getStudent())
                .monthlyIncome(e.getMonthlyIncome())
                .build();
    }
    private AcademicInfoResponse mapAcademicInfo(WellbeingAcademicInfoEntity e){ return AcademicInfoResponse.builder().id(e.getId()).institution(e.getInstitution()).career(e.getCareer()).studyLevelId(e.getStudyLevel()!=null?e.getStudyLevel().getId():null).studyLevelName(e.getStudyLevel()!=null?e.getStudyLevel().getName():null).currentSemester(e.getCurrentSemester()).careerDurationSemesters(e.getCareerDurationSemesters()).studiesInRegion(e.getStudiesInRegion()).hadPreviousBenefit(e.getHadPreviousBenefit()).build(); }
    private AcademicVerificationResponse mapAcademicVerification(WellbeingAcademicVerificationEntity e){ return AcademicVerificationResponse.builder().id(e.getId()).academicSituation(e.getAcademicSituation()).gradeAverage(e.getGradeAverage()).approvalPercentage(e.getApprovalPercentage()).build(); }
    private SimpleResponse mapIncome(WellbeingFamilyIncomeEntity e){
        return SimpleResponse.builder()
                .id(e.getId())
                .name(e.getIncomeType()!=null?e.getIncomeType().getName():"Ingreso")
                .incomeTypeId(e.getIncomeType()!=null?e.getIncomeType().getId():null)
                .familyMemberId(e.getFamilyMember()!=null?e.getFamilyMember().getId():null)
                .amount(e.getAmount())
                .build();
    }

    private SimpleResponse mapExpense(WellbeingFamilyExpenseEntity e){
        return SimpleResponse.builder()
                .id(e.getId())
                .code(e.getCode())
                .name(e.getName())
                .description(e.getDescription())
                .category(e.getCategory()!=null?e.getCategory().name():null)
                .amount(e.getAmount())
                .build();
    }
    private HealthRecordResponse mapHealth(WellbeingHealthRecordEntity e){ return HealthRecordResponse.builder().id(e.getId()).personName(e.getPersonName()).familyMemberId(e.getFamilyMember()!=null?e.getFamilyMember().getId():null).pathology(e.getPathology()).monthlyExpense(e.getMonthlyExpense()).build(); }
    private HousingResponse mapHousing(WellbeingHousingEntity e){ return HousingResponse.builder().id(e.getId()).typeHousingId(e.getTypeHousing()!=null?e.getTypeHousing().getId():null).typeHousingName(e.getTypeHousing()!=null?e.getTypeHousing().getName():null).typePropertyId(e.getTypeProperty()!=null?e.getTypeProperty().getId():null).typePropertyName(e.getTypeProperty()!=null?e.getTypeProperty().getName():null).housingBackground(e.getHousingBackground()).otherBackground(e.getOtherBackground()).build(); }
    private DocumentTypeResponse mapDocumentType(WellbeingDocumentTypeEntity e){ return DocumentTypeResponse.builder().id(e.getId()).code(e.getCode()).name(e.getName()).documentGroup(e.getDocumentGroup()).required(e.getRequired()).helpText(e.getHelpText()).allowedExtensions(e.getAllowedExtensions()).maxSizeMb(e.getMaxSizeMb()).build(); }
    private DocumentResponse mapDocument(WellbeingPostulationDocumentEntity e) {
        LocalDateTime createdAt = e.getCreatedAt() != null ? e.getCreatedAt() : e.getUploadedAt();
        return DocumentResponse.builder()
                .id(e.getId())
                .originalFilename(e.getOriginalFilename())
                .sizeBytes(e.getSizeBytes())
                .mimeType(e.getContentType())
                .documentTypeId(e.getDocumentType().getId())
                .documentTypeCode(e.getDocumentType().getCode())
                .documentTypeName(e.getDocumentType().getName())
                .createdAt(createdAt)
                .storagePath(e.getStoragePath())
                .contentType(e.getContentType())
                .uploadedAt(e.getUploadedAt())
                .build();
    }
}
