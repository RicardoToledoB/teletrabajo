package com.teletrabajo.dto;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.teletrabajo.entity.UserEntity;
import jakarta.persistence.Column;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Field;
import java.math.BigDecimal;
import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

class UserProfileR03ContractTest {
    private final ObjectMapper mapper = new ObjectMapper().findAndRegisterModules();

    @Test
    void aceptaNombreDeColumnaYConservaValoresEnDTO() throws Exception {
        String json = """
            {"establishment_id": 14, "professionId": 21, "contract_hours": 44.50,
             "institutional_entry_date": "2020-05-12", "personal_email": "persona@example.org"}
            """;
        UserDTO dto = mapper.readValue(json, UserDTO.class);
        assertEquals(14L, dto.getEstablishmentId());
        assertEquals(21L, dto.getProfessionId());
        assertEquals(new BigDecimal("44.50"), dto.getContractHours());
        assertEquals(LocalDate.of(2020, 5, 12), dto.getInstitutionalEntryDate());
        assertEquals("persona@example.org", dto.getPersonalEmail());
        String output = mapper.writeValueAsString(dto);
        assertTrue(output.contains("\"establishmentId\":14"));
        assertFalse(output.contains("\"establishment_id\""));
    }

    @Test
    void columnasSonAnotadasSinAlterarIdExistente() throws Exception {
        assertEquals(Integer.class, UserEntity.class.getDeclaredField("id").getType());
        for (String name : new String[] {"establishmentId", "unitId", "professionId", "position",
            "staffCategory", "grade", "contractHours", "applicableLaw", "institutionalEntryDate",
            "employmentStatus", "nationalityId", "professionalTitleId", "phone", "personalEmail"}) {
            Field field=UserEntity.class.getDeclaredField(name);
            assertNotNull(field.getAnnotation(Column.class), name);
            assertTrue(field.getAnnotation(Column.class).nullable(), name);
        }
    }
}
