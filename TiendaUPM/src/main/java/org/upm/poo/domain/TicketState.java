package org.upm.poo.domain;

public enum TicketState {
    EMPTY,   // Recién creado, sin productos
    ACTIVE,  // Con productos, en proceso de compra
    CLOSED   // Pagado y facturado (inmutable)
}