package com.teletrabajo.security;

import com.teletrabajo.controller.acreditacion.AcreditacionController;
import com.teletrabajo.controller.calificaciones.CalificacionesController;
import com.teletrabajo.controller.capacitacion.*;
import com.teletrabajo.entity.UserEntity;
import com.teletrabajo.entity.acreditacion.*;
import com.teletrabajo.entity.calificaciones.QualCycle;
import com.teletrabajo.entity.capacitacion.*;
import com.teletrabajo.repository.UserRepository;
import com.teletrabajo.repository.acreditacion.*;
import com.teletrabajo.repository.calificaciones.QualCycleRepository;
import com.teletrabajo.repository.capacitacion.*;
import com.teletrabajo.service.capacitacion.*;
import jakarta.servlet.Filter;
import jakarta.servlet.DispatcherType;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.http.ResponseEntity;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit.jupiter.web.SpringJUnitWebConfig;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;

import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/** MVC + cadena JWT reales, repositorios y servicios simulados; no toca la BD. */
@SpringJUnitWebConfig(ModuleAuthorizationTest.Config.class)
@TestPropertySource(properties = {
        "app.jwt.secret=clave-solo-para-pruebas-unitarias-de-autorizacion-2026",
        "app.jwt.expiration-ms=3600000"
})
class ModuleAuthorizationTest {
    @Autowired WebApplicationContext context;
    @Autowired @Qualifier("springSecurityFilterChain") Filter securityFilter;
    @Autowired JwtService jwt;
    @Autowired UserDetailsService users;
    @Autowired AcrProcessRepository processes;
    @Autowired AcrRequirementRepository requirements;
    @Autowired AcrApplicationRepository applications;
    @Autowired QualCycleRepository cycles;
    @Autowired CapActivityRepository activities;
    @Autowired CapParticipantRepository participants;
    @Autowired CapSignatoryRepository signatories;
    @Autowired CapacitacionService certificates;
    @Autowired CapacitacionImportService imports;
    @Autowired UserRepository userRepository;
    MockMvc mvc;

    @BeforeEach void setup() throws Exception {
        mvc = MockMvcBuilders.webAppContextSetup(context).addFilters(securityFilter).build();
        when(userRepository.findByEmailIgnoreCase(anyString()))
                .thenReturn(Optional.of(UserEntity.builder().id(10).build()));
        when(processes.save(any(AcrProcess.class))).thenAnswer(i -> {
            AcrProcess p = i.getArgument(0); p.id = 1L; return p;
        });
        AcrProcess p = new AcrProcess(); p.id = 1L; p.status = "PROGRAMADA";
        when(processes.findById(1L)).thenReturn(Optional.of(p));
        when(requirements.save(any(AcrRequirement.class))).thenAnswer(i -> i.getArgument(0));
        when(cycles.save(any(QualCycle.class))).thenAnswer(i -> i.getArgument(0));
        CapActivity activity = new CapActivity(); activity.id = 1L; activity.title = "Prueba";
        when(activities.findById(1L)).thenReturn(Optional.of(activity));
        when(activities.save(any(CapActivity.class))).thenAnswer(i -> i.getArgument(0));
        when(participants.save(any(CapParticipant.class))).thenAnswer(i -> i.getArgument(0));
        when(signatories.save(any(CapSignatory.class))).thenAnswer(i -> i.getArgument(0));
        when(certificates.issue(1L)).thenReturn(new CapCertificate());
        when(certificates.revoke(1L)).thenReturn(new CapCertificate());
        when(imports.importParticipants(eq(1L), any(), anyString())).thenReturn(Map.of("successfulRows", 1));
        when(certificates.verify("test")).thenReturn(Map.of("valid", true));
        AcrApplication foreign = new AcrApplication(); foreign.applicantUserId = 20;
        when(applications.findById(1L)).thenReturn(Optional.of(foreign));
    }

    private String bearer(String role) {
        return "Bearer " + jwt.generateToken(users.loadUserByUsername(role + "@example.test"));
    }

    @Test void todosLosRolesPuedenUsarOperacionesAntesRestringidas() throws Exception {
        for (String role : List.of("ADMIN", "ADMINISTRATIVO", "SUPERVISOR", "JEFATURA", "FUNCIONARIO", "ACR_REFERENTE", "SIN_ROL")) {
            String token = bearer(role);
            mvc.perform(post("/api/v1/acreditacion/convocatorias").header("Authorization", token)
                    .contentType(MediaType.APPLICATION_JSON).content("""
                    {"year":2027,"title":"Prueba","opensAt":"2027-03-01T09:00:00","closesAt":"2027-03-31T17:00:00"}
                    """)).andExpect(status().isOk()).andExpect(jsonPath("$.status").value("PROGRAMADA"));
            mvc.perform(post("/api/v1/acreditacion/convocatorias/1/requisitos").header("Authorization", token)
                    .contentType(MediaType.APPLICATION_JSON).content("{\"title\":\"Prueba\"}"))
                    .andExpect(status().isOk());
            mvc.perform(get("/api/v1/calificaciones/ciclos").header("Authorization", token)).andExpect(status().isOk());
            mvc.perform(post("/api/v1/calificaciones/ciclos").header("Authorization", token)
                    .contentType(MediaType.APPLICATION_JSON).content("""
                    {"year":2027,"startAt":"2027-01-01T00:00:00","endAt":"2027-12-31T23:59:00","rulesVersion":"QA"}
                    """)).andExpect(status().isOk()).andExpect(jsonPath("$.status").value("BORRADOR"));
            mvc.perform(post("/api/v1/capacitacion/actividades").header("Authorization", token)
                    .contentType(MediaType.APPLICATION_JSON).content("{\"title\":\"Prueba\",\"code\":\"QA\",\"modality\":\"VIRTUAL\",\"executionNumber\":1}"))
                    .andExpect(status().isOk()).andExpect(jsonPath("$.modality").value("VIRTUAL"));
            mvc.perform(post("/api/v1/capacitacion/participantes").header("Authorization", token)
                    .contentType(MediaType.APPLICATION_JSON).content("""
                    {"rut":"RUT_DE_PRUEBA","fullName":"Participante de prueba","activity":{"id":1,"title":"Prueba"},"attendancePercent":90.5,"grade":6.5,"status":"COMPLETADO"}
                    """)).andExpect(status().isOk());
            mvc.perform(post("/api/v1/capacitacion/firmantes").header("Authorization", token)
                    .contentType(MediaType.APPLICATION_JSON).content("{\"fullName\":\"Firmante de prueba\"}"))
                    .andExpect(status().isOk());
            mvc.perform(post("/api/v1/capacitacion/certificados/emitir/1").header("Authorization", token)).andExpect(status().isOk());
            mvc.perform(post("/api/v1/capacitacion/certificados/1/revocar").header("Authorization", token)).andExpect(status().isOk());
            mvc.perform(multipart("/api/v1/capacitacion/actividades/1/importar-participantes")
                    .file(new MockMultipartFile("file", "qa.csv", "text/csv", new byte[]{1}))
                    .header("Authorization", token)).andExpect(status().isOk());
        }
    }

    @Test void sinTokenOConTokenInvalidoNoHayAcceso() throws Exception {
        for (String path : List.of("/api/v1/acreditacion/convocatorias", "/api/v1/calificaciones/ciclos", "/api/v1/capacitacion/participantes")) {
            mvc.perform(get(path)).andExpect(status().isUnauthorized());
            mvc.perform(post(path).contentType(MediaType.APPLICATION_JSON).content("{}")).andExpect(status().isUnauthorized());
            mvc.perform(post(path).header("Authorization", "Bearer invalido").contentType(MediaType.APPLICATION_JSON).content("{}"))
                    .andExpect(status().isUnauthorized());
        }
    }

    @Test void propiedadDeExpedienteYVerificacionPublicaSeConservan() throws Exception {
        mvc.perform(get("/api/v1/acreditacion/postulaciones/1").header("Authorization", bearer("ADMIN")))
                .andExpect(status().isForbidden());
        mvc.perform(get("/api/v1/public/capacitacion/verificar/test")).andExpect(status().isOk());
    }

    @Test void despachoInternoDeErroresNoSeEnmascaraComo401() throws Exception {
        mvc.perform(get("/error").with(request -> {
            request.setDispatcherType(DispatcherType.ERROR);
            return request;
        })).andExpect(status().isBadRequest());
        mvc.perform(get("/error")).andExpect(status().isUnauthorized());
    }

    @RestController
    static class TestErrorController {
        @RequestMapping("/error") ResponseEntity<Void> error() {
            return ResponseEntity.badRequest().build();
        }
    }

    @TestConfiguration
    @EnableWebMvc
    @EnableWebSecurity
    @Import({SecurityConfig.class, CorsConfig.class, JwtAuthenticationFilter.class, JwtService.class,
            AcreditacionController.class, CalificacionesController.class, CapacitacionController.class,
            CapacitacionExtraController.class, CapacitacionVerificationController.class, TestErrorController.class})
    static class Config {
        @Bean UserDetailsService userDetailsService() {
            return email -> {
                String role = email.substring(0, email.indexOf('@'));
                var builder = User.withUsername(email).password("no-usada");
                return ("SIN_ROL".equals(role) ? builder.authorities(List.of()) : builder.roles(role)).build();
            };
        }
        @Bean UserRepository userRepository() { return mock(UserRepository.class); }
        @Bean AcrProcessRepository processes() { return mock(AcrProcessRepository.class); }
        @Bean AcrApplicationRepository applications() { return mock(AcrApplicationRepository.class); }
        @Bean AcrRequirementRepository requirements() { return mock(AcrRequirementRepository.class); }
        @Bean AcrAuditEventRepository audits() { return mock(AcrAuditEventRepository.class); }
        @Bean QualCycleRepository cycles() { return mock(QualCycleRepository.class); }
        @Bean CapActivityRepository activities() { return mock(CapActivityRepository.class); }
        @Bean CapParticipantRepository participants() { return mock(CapParticipantRepository.class); }
        @Bean CapSignatoryRepository signatories() { return mock(CapSignatoryRepository.class); }
        @Bean CapCertificateRepository certificateRepository() { return mock(CapCertificateRepository.class); }
        @Bean CapacitacionService certificates() { return mock(CapacitacionService.class); }
        @Bean CapacitacionImportService imports() { return mock(CapacitacionImportService.class); }
        @Bean CapacitacionDocumentsService documents() { return mock(CapacitacionDocumentsService.class); }
    }
}
