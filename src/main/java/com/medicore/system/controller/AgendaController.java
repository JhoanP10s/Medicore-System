package com.medicore.system.controller;

import java.time.LocalDate;
import java.util.List;

import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.medicore.system.dto.response.ErrorResponse;
import com.medicore.system.dto.response.HorarioDisponibleResponse;
import com.medicore.system.service.AgendaService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;

@RestController
@RequestMapping("/agenda")
@Tag(name = "Agenda medica", description = "Consulta horarios disponibles a partir de disponibilidad, citas activas y bloqueos.")
public class AgendaController {

    private final AgendaService agendaService;

    public AgendaController(AgendaService agendaService) {
        this.agendaService = agendaService;
    }

    @Operation(summary = "Consultar horarios disponibles", description = "Retorna horarios disponibles para un medico en una fecha y duracion solicitada. Los horarios se generan en bloques consecutivos con paso igual a duracionMinutos; no se generan alternativas intermedias con granularidad menor.")
    @ApiResponse(responseCode = "200", description = "Horarios disponibles", content = @Content(schema = @Schema(implementation = HorarioDisponibleResponse.class), examples = @ExampleObject(value = """
            [{"inicio":"2026-05-15T08:00:00","fin":"2026-05-15T08:30:00"}]
            """)))
    @ApiResponse(responseCode = "400", description = "Parametros invalidos", content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    @GetMapping("/medico/{medicoId}/disponibles")
    public ResponseEntity<List<HorarioDisponibleResponse>> buscarHorariosDisponibles(
            @PathVariable String medicoId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fecha,
            @RequestParam Integer duracionMinutos) {
        return ResponseEntity.ok(agendaService.buscarHorariosDisponibles(medicoId, fecha, duracionMinutos));
    }
}
