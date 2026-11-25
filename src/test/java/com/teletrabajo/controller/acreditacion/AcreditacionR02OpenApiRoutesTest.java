package com.teletrabajo.controller.acreditacion;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerMapping;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.*;
import static org.junit.jupiter.api.Assertions.*;

/** Verifica el inventario 84 operaciones del ACR-OPENAPI.json, no su semantica institucional. */
@SpringBootTest
class AcreditacionR02OpenApiRoutesTest {
 @Autowired @Qualifier("requestMappingHandlerMapping") RequestMappingHandlerMapping routes;
 @Test void rutasDelContratoR01EstanRegistradas() throws Exception {
  Set<String> published=new HashSet<>();
  routes.getHandlerMethods().forEach((info,method)->{
   for(String path:info.getPatternValues())
    info.getMethodsCondition().getMethods().forEach(http->published.add(http.name()+" "+path));
  });
  try(var stream=getClass().getResourceAsStream("/acreditacion/r01-routes.txt")){
   assertNotNull(stream);
   try(var reader=new BufferedReader(new InputStreamReader(stream,StandardCharsets.UTF_8))){
    for(String line; (line=reader.readLine())!=null; ){
     if(!line.isBlank())assertTrue(published.contains(line),"Ruta faltante en Spring MVC: "+line);
    }
   }
  }
 }
}
