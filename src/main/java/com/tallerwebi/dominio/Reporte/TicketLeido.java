package com.tallerwebi.dominio.Reporte;

import java.util.List;

// Lo que se lee de la foto de un ticket: los productos y, si se pudo deducir, el tipo de comercio.
public record TicketLeido(List<ItemTicket> items, String tipoComercio) {}
